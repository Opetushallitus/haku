$(document).ready(function () {
    $('form').submit(function () {
        $(this).find('input:text').each(function () {
            $(this).val($.trim($(this).val()));
        });
    });

    var popover = {
        handlers: {
            openPopovers: 0,
            autoGenCount: 0
        },
        build: function () {
            popover.set.triggers();
        },
        add: function (title, content) {
            // Popover auto-generated id
            id = 'poag' + popover.handlers.autoGenCount;
            popover.handlers.autoGenCount++;

            popover_close = '<span class="popup-dialog-close">&#8203;</span>';

            html = '<div class="popup-dialog-wrapper generated" id="' + id + '" style="z-index:' + (popover.handlers.autoGenCount + 1000) + ';">';
            html += popover_close;
            html += '<div class="popup-dialog">';
            html += popover_close;
            html += '<div class="popup-dialog-header">';
            html += '<h3>' + title + '</h3>';
            html += '</div>';
            html += '<div class="popup-dialog-content">';
            html += content;
            html += '</div>';
            html += '</div>';
            html += '</div>';

            $('#overlay').append(html);

            $('#' + id).show();
            popover.handlers.openPopovers++;
            popover.set.overlay();
            popover.set.size($('#' + id + ' .popup-dialog'));
            popover.set.position($('#' + id + ' .popup-dialog'));
        },
        hide: function (id) {
            if ($('#' + id).length != 0) {
                $('#' + id).hide();
                popover.handlers.openPopovers--;
                popover.set.overlay();
            }
        },
        remove: function (target) {
            if (target.length != 0 && $(target).length != 0) {
                $(target).closest('.popup-dialog-wrapper').remove(); // Alternatively .detach()
                popover.handlers.openPopovers--;
                popover.set.overlay();
            }
        },
        show: function (id) {
            if ($('#' + id).length != 0) {
                $('#' + id).show();
                popover.handlers.openPopovers++;
                popover.set.overlay();
                popover.set.size($('#' + id + ' .popup-dialog'));
                popover.set.position($('#' + id + ' .popup-dialog'));
                $('#' + id + " :focusable:first").focus();
            }
        },
        set: {
            overlay: function () {

                // Show overlay if 1 or more popovers are open/visible
                // Hide overlay if no popovers are open/visible
                if (popover.handlers.openPopovers > 0) {
                    $('#overlay').show();
                }
                else {
                    $('#overlay').hide();
                }
            },
            position: function (target) {

                // Target the actual popover-window
                if ($(target).hasClass('.popup-dialog-wrapper')) {
                    target = $(target).find('.popup-dialog');
                }

                // Get window height and position from top
                //window = $(window);
                window_top = $(window).scrollTop();
                window_height = $(window).height();

                // Get wrapper position from top
                wrapper_top = $('#viewport').scrollTop();
                popover_height = $(target).outerHeight(true);

                // Center popover if it fits in the window
                if (popover_height < window_height) {
                    offset = (window_height - popover_height) / 2;
                }
                else {
                    offset = 0;
                }
                // Determine popover position
                popover_position = window_top + offset - wrapper_top;
                // console.log(window_top+"+"+offset+"-"+wrapper_top+"="+popover_position);
                target.css({'top': popover_position + 'px'});

            },
            size: function (target) {

                // Target the actual popover-window
                if ($(target).hasClass('.popup-dialog-wrapper')) {
                    target = $(target).find('.popup-dialog');
                }

                content_width = $(target).find('.popup-dialog-content').width();
                content_outerwidth = $(target).find('.popup-dialog-content').outerWidth(true);
                content_padding = content_outerwidth - content_width;

                // Content area has minimum width
                if (content_outerwidth < 460) {
                    content_width = 460 - content_padding;
                }

                popover_width = content_width - content_padding;

                $(target).find('.popup-dialog-content').css({'width': content_width + 'px'});
                $(target).css({'width': popover_width + 'px'});

            },
            triggers: function () {

                // Remove or hide popover from closing links
                $('body').on('click', '.popup-dialog-wrapper .popup-dialog-close', function () {

                    // If window was generated dynamically remove, else just hide
                    if ($(this).closest('.popup-dialog-wrapper').hasClass('generated')) {
                        target = $(this).closest('.popup-dialog-wrapper').find('.popup-dialog');
                        popover.remove(target);
                    }
                    else {
                        id = $(this).closest('.popup-dialog-wrapper').attr('id');
                        popover.hide(id);
                    }
                });

                // Generate new popover
                $('body').on('click', '[data-po-add]', function (event) {
                    event.preventDefault();
                    popover.add();

                });

                // Show already existing popover with id
                $('body').on('click', '[data-po-show]', function (event) {
                    event.preventDefault();
                    id = $(this).attr('data-po-show');
                    popover.show(id);
                });

                // Hide already existing popover with id
                $('body').on('click', '[data-po-hide]', function (event) {
                    event.preventDefault();
                    id = $(this).attr('data-po-hide');
                    var toClear = $(this).attr('data-po-clear-on-hide');
                    if (toClear) {
                        $('#' + toClear).val('');
                    }
                    popover.hide(id);
                });
            }
        }
    };

    popover.build();

    $('#confirm-form #submit_confirm').on('click', function(e) {
        e.preventDefault();
        $('#submit_confirm').prop('disabled', true);
        $('#confirm-form').submit();
        if ($('#submit_confirm #areyousure_question_yes') && $('#submit_confirm #loading_button_text')) {
            $('#submit_confirm #areyousure_question_yes').hide();
            $('#submit_confirm #loading_button_text').show();
        };
    });
});

function appendCookieConfirm() {
    var texts = getCookieTexts();
    $('body').prepend('<div class="cookieHeader"><span class="cookieText">' + texts.info + '</span><a class="btn-cookies" href="#" onclick="javascript:setAcceptCookie();return false;">' + texts.close + '</a></div>');
}
function checkAcceptCookie() {
    // virkailijaSide is set in other js
    if(!isAcceptCookie() && typeof virkailijaSide == "undefined"){
        appendCookieConfirm();
    }
}
function getCookieTexts(){
    if(document.cookie.indexOf('i18next=sv') >= 0){
        return {
            info: "Vi använder oss av cookies för att underlätta användningen av webbplatsen.",
            close: "Stäng"
        };
    } else if(document.cookie.indexOf('i18next=en') >= 0){
        return {
            info: "We use cookies on this site to enhance your user experience.",
            close: "Close"
        };
    } else {
        return {
            info: "Jotta sivuston käyttö olisi sinulle sujuvaa, käytämme evästeitä.",
            close: "Sulje"
        };
    }
}

function isAcceptCookie() {
    if(document.cookie.indexOf('oph-cookies-accepted=true') >= 0){
        return true;
    } else {
        return false;
    }
}

function setAcceptCookie() {
    document.cookie = "oph-cookies-accepted=true; path=/;";
    $('div.cookieHeader').attr('style', 'display:none;');
    return false;
}

$(document).ready(function () {
    checkAcceptCookie();
});

// Sets CSRF header for ajax and hidden field for FORMs
function initCSRF() {
    function getCookie(name) {
        var value = "; " + document.cookie;
        var parts = value.split("; " + name + "=");
        if (parts.length == 2) return parts.pop().split(";").shift();
    }

    var ajaxHeaders = {
        headers: { 'clientSubSystemCode': 'haku.haku-app.frontend'}
    };

    var csrf = getCookie("CSRF");

    if(csrf) {
        ajaxHeaders.headers['CSRF'] = csrf;
    }

    $.ajaxSetup(ajaxHeaders);

    $(document).ready(function () {
        var forms = $("form[method='post']")
        if(csrf) {
            forms.append($("<input name='CSRF' type='hidden'>").attr("value", csrf))
        }
        forms.append($("<input name='clientSubSystemCode' type='hidden' value='haku.haku-app.frontend'>"))
    })
}

initCSRF();

var complexRule = {
    url: function() {
        var split = document.URL.split("?");
        var postUrl = split[0] + '/rules';
        if (split.length > 1) {
            postUrl = postUrl + "?" + split[1];
        }
        return postUrl;
    },
    doPost: function(arrayOfRequestsToSend) {
        arrayOfRequestsToSend.map(function(request) {
            complexRule.doOnePost(request)
        })
    },
    doOnePost: function(mergedRequest) {
        $.ajax({
            type: 'POST',
            url: complexRule.url(),
            async: true,
            data: mergedRequest,

            success: complexRule.doPostResult,
            error: function (e, ts, et) {
                //console.log("refresh view error" + ts);
            }
        });
    },
    doPostResult: function(data, textStatus, jqXHR) {
        var json = $.parseJSON(data);
        json.map(function(rule) {
            $("#" + rule.id).replaceWith(rule.html);
        })
    },
    initBus: function() {
        complexRule.bus = new Bacon.Bus();
        complexRule.bus.bufferWithTime(100).onValue(function(arrayOfRuleIds) {
            var arrayOfRequestsToSend = [];
            var combinedRuleIds = [];
            var previousForm;

            if (arrayOfRuleIds) {
                arrayOfRuleIds.map(function (ruleId) {
                    var form = _.clone($("form.form").serializeArray());
                    if (previousForm) {
                        if (_.isEqual(previousForm, form)) {
                            combinedRuleIds.push(ruleId);
                        } else {
                            arrayOfRequestsToSend.push(addCombinedRuleIdsToFrom(previousForm, combinedRuleIds));
                            previousForm = form;
                            combinedRuleIds = [ruleId];
                        }
                    } else {
                        previousForm = form;
                        combinedRuleIds = [ruleId];
                    }
                })
                arrayOfRequestsToSend.push(addCombinedRuleIdsToFrom(previousForm, combinedRuleIds));
                complexRule.doPost(arrayOfRequestsToSend);
            }


            function addCombinedRuleIdsToFrom(formToSend, combinedRuleIds) {
                combinedRuleIds.map(function(ruleId) {
                    formToSend.push( {
                        name: 'ruleIds[]',
                        value: ruleId
                    })
                })
                return formToSend;
            }
        })
    },

    init: function (ruleData) {
        for (index in ruleData.variables) {
            var question = $("[name=\"" + ruleData.variables[index] + "\"]");
            if (question.length) {
                var events = $._data(question[0], "events");
                if (events) {
                    var bind = true;
                    for (eIndex in events.change) {
                        if (events.change[eIndex].data && events.change[eIndex].data.ruleId == ruleData.ruleId) {
                            bind = false;
                        }
                    }
                    if (bind == true) {
                        var refreshShouldBeDelayed = question[0].type === 'text';
                        var refresh = refreshShouldBeDelayed ? _.throttle(function(event) { complexRule.refreshView(event); }, 500) : complexRule.refreshView;
                        question.on('change paste keyup input', ruleData, refresh);
                    }
                } else {
                    question.on('change paste keyup input', ruleData, complexRule.refreshView);
                }
            }
        }
    },

    refreshView: function (event) {
        complexRule.bus.push(event.data.ruleId);
    }
};
complexRule.initBus()

window.elementAdder = {
    toggleAddRemoveButtons: function(el) {
        'use strict';

        var lastEl = rootEl().find('.repeatingElement:last');
        var secondToLastEl = lastEl.parents('.repeatingElement:first');

        hideAllButtons();

        if (!canAddMoreElementsToElement(lastEl)) {
            showRemoveForEl(lastEl);
        } else {
            showAddForEl(lastEl);
            showRemoveForEl(secondToLastEl);
        }

        function rootEl() {
            var rootEl = el.parents('.repeatingElement:last');
            if (rootEl.length === 0) {
                rootEl = el.parent();
            }
            return rootEl;
        }

        function hideAllButtons() {
            rootEl().find('.addRemoveLinks').removeClass('showRemove showAdd');
        }

        function canAddMoreElementsToElement(el) {
            return el.find('.elementChildren').children().length === 0;
        }

        function showRemoveForEl(el) {
            el.find('.addRemoveLinks:first').addClass('showRemove');
        }

        function showAddForEl(el) {
            el.find('.addRemoveLinks:first').addClass('showAdd');
        }
    }
};
