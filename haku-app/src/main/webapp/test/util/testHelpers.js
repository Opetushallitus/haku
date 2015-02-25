var expect = chai.expect;
chai.should();
chai.config.truncateThreshold = 0; // disable truncating

function Button(el) {
    return {
        element: function () {
            return el()
        },
        isEnabled: function () {
            return !el().prop("disabled")
        },
        isVisible: function () {
            return el().is(":visible")
        },
        click: function () {
            el().click()
        },
        isRealButton: function () {
            return el().prop("tagName") == "BUTTON"
        },
        hasTabIndex: function () {
            return el().prop("tabIndex") > 0
        },
        isFocusableBefore: function (button) {
            return !this.hasTabIndex() && !button.hasTabIndex() && compareDOMIndex(this.element(), button.element()) < 0
        }
    }
}

function S(selector) {
    try {
        if (!testFrame() || !testFrame().jQuery) {
            return $([])
        }
        return testFrame().jQuery(selector)
    } catch (e) {
        console.log("Premature access to testFrame.jQuery, printing stack trace.");
        console.log(new Error().stack);
        throw e;
    }
}

wait = {
    maxWaitMs: testTimeout,
    waitIntervalMs: 10,
    until: function (condition, count) {
        return function (/*...promiseArgs*/) {
            var promiseArgs = arguments;
            var deferred = Q.defer();
            if (count == undefined) count = wait.maxWaitMs / wait.waitIntervalMs;

            (function waitLoop(remaining) {
                var cond = condition.apply(this, promiseArgs);
                if (cond) {
                    deferred.resolve()
                } else if (remaining === 0) {
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
            var el = angular.element(S("#appRoot"));
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

uiUtil = {
    inputValues: function (el) {
        function formatKey(key) {
            return key.replace(".data.", ".")
        }

        function getId(el) {
            return [el.attr("ng-model"), el.attr("ng-bind")].join("")
        }

        return _.chain(el.find("[ng-model]:visible, [ng-bind]:visible"))
            .map(function (el) {
                return [formatKey(getId($(el))), $(el).val() + $(el).text()]
            })
            .object().value()
    }
};

mockAjax = {
    init: function () {
        var deferred = Q.defer();
        if (testFrame().sinon)
            deferred.resolve();
        else
            testFrame().$.getScript('test/lib/sinon-server-1.10.3.js', function () {
                deferred.resolve()
            });
        return deferred.promise
    },
    respondOnce: function (method, url, responseCode, responseBody) {
        var fakeAjax = function () {
            var xhr = sinon.useFakeXMLHttpRequest()
            xhr.useFilters = true
            xhr.addFilter(function (method, url) {
                return url != _fakeAjaxParams.url || method != _fakeAjaxParams.method
            });

            xhr.onCreate = function (request) {
                window.setTimeout(function () {
                    if (window._fakeAjaxParams && request.method == _fakeAjaxParams.method && request.url == _fakeAjaxParams.url) {
                        request.respond(_fakeAjaxParams.responseCode, {"Content-Type": "application/json"}, _fakeAjaxParams.responseBody);
                        xhr.restore()
                        delete _fakeAjaxParams
                    }
                }, 0)
            }
        };

        testFrame()._fakeAjaxParams = {
            method: method,
            url: url,
            responseCode: responseCode,
            responseBody: responseBody
        };
        testFrame().eval("(" + fakeAjax.toString() + ")()")
    }
};

util = {
    flattenObject: function (obj) {
        function flatten(obj, prefix, result) {
            _.each(obj, function (val, id) {
                if (_.isObject(val)) {
                    flatten(val, id + ".", result)
                } else {
                    result[prefix + id] = val
                }
            });
            return result
        }

        return flatten(obj, "", {})
    }
};

function getJson(url) {
    return Q($.ajax({url: url, dataType: "json"}))
}

function frameJquery() {
    return wait.until(function() {
        return testFrame() && testFrame().jQuery;
    })().then(function() {
        return testFrame().jQuery;
    });
}

// Ensure that evaluation of given promise leads into a new page being loaded
function waitPageLoad(promise) {
    return function() {
        return Q.fcall(function() { testFrame().window.SPEC_PAGE_CHANGE_PENDING = true; })
            .then(promise)
            .then(wait.until(function() {
                return testFrame().window.SPEC_PAGE_CHANGE_PENDING === undefined;
            }))
            .then(wait.until(function() {
                return (testFrame().document.readyState === 'complete');
            }));
    }
}

function pageChange(promise) {
    return waitPageLoad(click(promise))
}

// Submit data as if it was a form instead of an AJAX request. Used for
// rendering the HTML response as a new page.
// Modified from: http://stackoverflow.com/a/133997
function postAsForm(path, params) {
    var form = document.createElement("form");
    form.setAttribute("method", 'POST');
    form.setAttribute("action", path);

    for (var key in params) {
        if (params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
        }
    }

    return waitPageLoad(function() {
        testFrame().document.body.appendChild(form);
        form.submit();
    });
}

function get(url) {
    return function() {
        return frameJquery().then(function($) {
            var deferred = Q.defer();
            $.get(url, function(data, status) {
                if (status === 'success') {
                    deferred.resolve(data)
                } else {
                    deferred.reject("get got status " + status)
                }
            });
            return deferred.promise
        })
    }
}

function post(url, data, contentType) {
    contentType = contentType || 'application/x-www-form-urlencoded';
    return function() {
        return frameJquery().then(function($) {
            var deferred = Q.defer();
            $.ajax({
                type: "POST",
                url: url,
                data: contentType === 'application/json' ? JSON.stringify(data) : data,
                contentType: contentType
            }).done(function(data) {
                deferred.resolve(data)
            }).fail(function(jqXHR, status) {
                deferred.reject("post got status " + status)
            });
            return deferred.promise
        })
    }
}

function testFrame() {
    return $("#testframe").get(0).contentWindow
}

function openPage(path, predicate) {
    if (!predicate) {
        console.log("nopredicate for: " + path);
        predicate = function () {
            return testFrame().jQuery
        }
    }
    return function () {
        var newTestFrame = $('<iframe/>').attr({src: path, width: 1024, height: 800, id: "testframe"});
        $("#testframe").replaceWith(newTestFrame);
        return wait.until(function () {
            return predicate()
        })().then(function () {
            window.uiError = null;
            testFrame().onerror = function (err) {
                window.uiError = err;
            }; // Hack: force mocha to fail on unhandled exceptions
        })
    }
}

function logout() {
    return openPage("/haku-app/user/logout", function() {
        return S("ul").is(":visible");
    })();
}

// Enforce login to get authorized JSESSIONID cookie
function login(username, password) {
    if (username === undefined || password === undefined) {
        throw new Error("Must give username and password for login");
    }
    return seq(
        logout,
        openPage("/haku-app/user/login", function() {
            return testFrame().document.getElementById('loginForm') !== null;
        }),
        function() {
            function elementByName(name) {
                return testFrame().document.getElementsByName(name)[0];
            }

            elementByName("j_username").value = username;
            elementByName("j_password").value = password;
            elementByName("login").click();
        },
        wait.until(function() {
            var pathname = testFrame().document.location.pathname;
            // Page redirection depends on credentials
            return (pathname === "/haku-app/virkailija/hakemus"
            || pathname === "/haku-app/user/login");
        }));
}

function setupGroupConfiguration(applicationSystemId, groupId, type, configurations) {
    var resource = "/haku-app/application-system-form-editor/configuration";
    return seq(
        get(resource + "/" + applicationSystemId),
        post(resource + "/" + applicationSystemId + "/groupConfiguration/" + groupId,
            {groupId: groupId, type: type, configurations: configurations},
            'application/json'))
}

function takeScreenshot() {
    if (window.callPhantom) {
        var date = new Date()
        var filename = "target/screenshots/" + date.getTime()
        console.log("Taking screenshot " + filename)
        callPhantom({'screenshot': filename})
    }
}

(function improveMocha() {
    var origBefore = before
    before = function () {
        Array.prototype.slice.call(arguments).forEach(function (arg) {
            if (typeof arg !== "function") {
                throw ("not a function: " + arg)
            }
            origBefore(arg)
        })
    }
})();

function sleep(ms) {
    return function() {
        return Q.delay(ms);
    }
}

function log(marker) {
    return function(arg) {
        console.log(marker, arg);
        return arg;
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

function input1(fn, value) {
    return function() {
        return visible(fn)().then(function() {
            return fn().val(value).change().blur();
        })
    }
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

function select(fn, value) {
    return function() {
        return visible(fn)()
            .then(wait.until(function() {
                var matches = fn().find('option[value="' + value + '"]').length;
                if (matches > 1) {
                    throw new Error('Value "' + value + '" matches ' + matches + ' <option>s from <select> ' + fn().selector)
                }
                return matches === 1;
            })).then(input(fn, value))
    }
}

function readTable($tableElement, allowWrongDimensions) {
    return $tableElement.find('tr').filter(function(i) {
        return allowWrongDimensions || testFrame().jQuery("td", this).length === 2
    }).toArray().reduce(function(agg, tr) {
        var tds = tr.getElementsByTagName('td');
        if (tds.length != 2) {
            throw new Error("Cannot read non-2-column table into map")
        }
        var key = tds[0].textContent.trim();
        var value = tds[1].textContent.trim();
        agg[key] = value;
        return agg;
    }, {});
}

function visible(fn) {
    if (typeof(fn) !== 'function') {
        throw new Error('visible() got a non-function');
    }
    return wait.until(function() {
        return fn().is(':visible');
    })
}

function exists(fn) {
    if (typeof(fn) !== 'function') {
        throw new Error('exists() got a non-function');
    }
    return wait.until(function() {
        return fn().length > 0;
    })
}

function notExists(fn) {
    if (typeof(fn) !== 'function') {
        throw new Error('notExists() got a non-function');
    }
    return wait.until(function() {
        return fn().length === 0;
    });
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
        return seq.apply(this, promiseArgs)().then(done, done);
    }
}

function click(/* ...promises */) {
    var fns = arguments;
    return function() {
        var clickSequence = Object.keys(fns).map(function(i) {
            return function() {
                var fn = fns[i];
                return visible(fn)().then(function() {
                    fn().click();
                })
            }
        });
        return clickSequence.reduce(Q.when, Q());
    }
}

function visibleText(fn, text) {
    return wait.until(function() {
        return fn().is(':visible') && fn().text().trim().indexOf(text) !== -1;
    })
}

function hasClass(fn, className) {
    return visible(fn)().then(function() {
        return fn().hasClass(className);
    });
}

function headingVisible(heading) {
    return visible(function() {
        return S("legend[class=h3]:contains(" + heading + ")");
    });
}

function eventIsBound(fn, event) {
    return Q.fcall(visible(fn))
        .then(frameJquery)
        .then(wait.until(function($) {
            var events = $._data(fn()[0], 'events');
            return events && events[event].length > 0;
        }));
}

function autocomplete(fn, partialText, suggestionChoiceText) {
    var pickFn = function() {
        return S("a.ui-corner-all:visible:contains(" + suggestionChoiceText + ")");
    };

    return function() {
        return eventIsBound(fn, 'keydown')
            .then(function() {
                fn().val(partialText).trigger("keydown");
            })
            .then(visible(pickFn))
            .then(function() { return pickFn().mouseover() })
            .then(hasClass(pickFn, 'ui-state-hover'))
            .then(click(pickFn))
    };
}
