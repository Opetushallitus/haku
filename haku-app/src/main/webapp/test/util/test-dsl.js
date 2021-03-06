/* master at https://github.com/Opetushallitus/haku/tree/master/haku-app/src/main/webapp/test/util/test-dsl.js */

testDslDebug = false
function dslDebug() {
    if(testDslDebug) {
        var args = Array.prototype.slice.call(arguments);
        args.splice(0, 0, "test-dsl -")
        console.log.apply(console, args);
    }
}

function wrap(elementDefinition) {
    switch (typeof(elementDefinition)) {
        case 'string':
            return function() {
                return S(elementDefinition);
            };
        case 'function':
            return function() {
                var args = arguments;
                return function() {
                    return S(elementDefinition.apply(this, args));
                };
            };
        default:
            throw new Error("Element definitions need to be strings or functions that return strings")
    }
}

function initSelectors(elements) {
    return Object.keys(elements).reduce(function(agg, key) {
        agg[key] = wrap(elements[key]);
        return agg;
    }, {})
}

function assertText() {
    var args = Array.prototype.slice.call(arguments);
    var selector = args.shift()
    chai.assert(typeof selector().text() !== 'undefined', "element "  + selector().selector + " should be defined, is undefined")
    var trimmedTexts = jQuery.makeArray(selector().map(function(i,e){return jQuery(e).text().trim()}))
    expect(trimmedTexts).to.deep.equal(args, selector().selector)
}

function assertValue(selector, val) {
    chai.assert(typeof selector().val() !== 'undefined', "element "  + selector().selector + " should be defined, is undefined")
    expect(selector().val().trim()).to.equal(val, selector().selector)
}

function seq(/* ...promises */) {
    var promises = arguments;
    return function() {
        return Array.prototype.slice.call(promises).reduce(Q.when, Q());
    }
}

function seqDone(/* ...promises */) {
    var promiseArgs = arguments;
    return function(done) {
        return seq.apply(this, promiseArgs)().then(function() { return done(); }, done);
    }
}

function waitJqueryIs(fn, param, value) {
    if(typeof value == 'undefined') {
        value = true
    }
    if (typeof(fn) !== 'function') {
        throw new Error('visible() got a non-function: ' + fn);
    }
    return function() {
        return wait.until(function() {
            dslDebug(fn().selector, param, fn().is(param));
            return fn().is(param) === value;
        })().fail(function(error) {
            throw new Error("Wait for selector '" + fn().selector + "' status: "+param+" to be " +value+ " failed: " + error);
        })
    }
}

function visible(fn) {
    return waitJqueryIs(fn, ':visible')
}

function hidden(fn) {
    return waitJqueryIs(fn, ':hidden')
}

function input1(fn, value) {
    return seq(
        visible(fn),
        function() {
            dslDebug(fn().selector, "visible and ready for input1: '" + value + "'")
            return fn().val(value).change().blur();
        });
}

function input(/* fn, value, fn, value, ... */) {
    var argv = Array.prototype.slice.call(arguments);
    if (argv % 2 === 0) {
        throw new Error("inputs() got odd number of arguments. Give input function and value argument for each input.")
    }
    var sequence = [];
    for (var i = 0; i < argv.length; i += 2) {
        sequence.push(input1(argv[i], argv[i + 1]));
    }
    return seq.apply(this, sequence);
}

function click(/* ...promises */) {
    var fns =  Array.prototype.slice.call(arguments);
    return function() {
        dslDebug("click selector count:", fns.length)
        var clickSequence = fns.map(function(fn) {
            return seq(
                visible(fn),
                function() {
                    dslDebug(fn().selector, "visible and ready for click. matched elements: ", fn().length)
                    fn().click();
                });
        });
        return clickSequence.reduce(Q.when, Q());
    }
}

function sleep(ms) {
    return function() {
        return Q.delay(ms);
    }
}

function getRandomInt(min, max) {
    return Math.round(Math.random() * (max - min) + min);
}

wait = {
    waitIntervalMs: 10,
    until: function(condition, maxWaitMs) {
        return function (/*...promiseArgs*/) {
            var promiseArgs = arguments;
            if (maxWaitMs == undefined) maxWaitMs = testTimeoutDefault;
            var deferred = Q.defer();
            var count = maxWaitMs / wait.waitIntervalMs;

            (function waitLoop(remaining) {
                var cond = condition.apply(this, promiseArgs);
                if (cond) {
                    deferred.resolve()
                } else if (remaining < 1) {
                    deferred.reject("timeout of " + wait.maxWaitMs + " in wait.until")
                } else {
                    setTimeout(function () {
                        waitLoop(remaining - 1)
                    }, wait.waitIntervalMs)
                }
            })(count);
            return deferred.promise
        }
    },
    untilFalse: function (condition) {
        return wait.until(function () {
            return !condition()
        })
    },
    forAngular: function () {
        var deferred = Q.defer();
        try {
            var angular = testFrame().angular;
            var el = angular.element(S("body"));
            var timeout = angular.element(el).injector().get('$timeout');
            angular.element(el).injector().get('$browser').notifyWhenNoOutstandingRequests(function () {
                timeout(function () {
                    deferred.resolve()
                })
            })
        } catch (e) {
            deferred.reject(e)
        }
        return deferred.promise
    },
    forMilliseconds: function (ms) {
        return function () {
            var deferred = Q.defer();
            setTimeout(function () {
                deferred.resolve()
            }, ms);
            return deferred.promise
        }
    }
};

function select(fn, value) {
    return seq(
        visible(fn),
        wait.until(function() {
            var matches = fn().find('option[value="' + value + '"]').length;
            if (matches > 1) {
                throw new Error('Value "' + value + '" matches ' + matches + ' <option>s from <select> ' + fn().selector)
            }
            return matches === 1;
        }),
        input(fn, value));
}

