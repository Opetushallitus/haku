$(document).ready(function () {

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
            }
        },
        set: {
            /*
             active:function(){
             $('#overlay .popup-dialog-wrapper').addClass('inactive').last().removeClass('inactive');
             },
             */
            overlay: function () {

                // Show overlay if 1 or more popovers are open/visible
                // Hide overlay if no popovers are open/visible
                if (popover.handlers.openPopovers > 0) {
                    $('#overlay').show();
                    popover.set.active();
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
                    //console.log($(this).attr('data-po-show'));
                    event.preventDefault();
                    id = $(this).attr('data-po-show');
                    popover.show(id);
                });

                // Hide already existing popover with id
                $('body').on('click', '[data-po-hide]', function (event) {
                    event.preventDefault();
                    id = $(this).attr('data-po-hide');
                    popover.hide(id);
                });
            }
        }
    }

    popover.build();

    var orgSearch = {
        settings: {
            listenTimeout: 500
        },
        build: function () {
            orgSearch.listen.listHeight();
        },
        listen: {
            listHeight: function () {
                // Listen for changes in

                height = $('#orgsearch').height();
                setTimeout(function () {
                    if (height != $('#orgsearch').height()) {
                        form_height = $('#orgsearch .orgsearchform').outerHeight(true);
                        list_height = height - form_height;
                        $('#orgsearch .orgsearchlist').css({'height': list_height + 'px'});
                    }
                    orgSearch.listen.listHeight();
                }, orgSearch.settings.listenTimeout);
            }
        }
    }

    orgSearch.build();

    var fieldInfo = {
        load: function () {
            $('input[title]').each(function () {
                label = $(this).attr('title');
                $(this).val(label);
                $(this).addClass('blurred');
            });

            $('input[data-field-preset]').each(function () {
                label = $(this).attr('data-field-preset');
                $(this).val(label);
                $(this).addClass('blurred');
            });
        },
        build: function () {
            fieldInfo.load();
            fieldInfo.setTriggers();
        },
        setTriggers: function () {
            $('input[title]').focus(function () {
                if ($(this).val() == $(this).attr('title')) {
                    $(this).val('');
                    $(this).removeClass('blurred');
                }
            });

            $('input[title]').blur(function () {
                if ($(this).val() == '') {
                    $(this).val($(this).attr('title'));
                    $(this).addClass('blurred');
                }
            });

            $('input[data-field-preset]').focus(function () {
                if ($(this).val() == $(this).attr('data-field-preset')) {
                    $(this).removeClass('blurred');
                }
            });

            $('input[data-field-preset]').blur(function () {
                if ($(this).val() == $(this).attr('data-field-preset')) {
                    $(this).addClass('blurred');
                }
            });
        }
    }


    var overlayPopup = {
        setPopup: function (id) {

            popup_height = $('#popup > .popup-content[data-popup-id="' + id + '"]').height();
            popup_width = $('#popup > .popup-content[data-popup-id="' + id + '"]').width();
            $('#popup').css({'width': popup_width + 'px', 'height': popup_height + 'px'});

            window_height = $(window).height();
            window_scrollTop = $(window).scrollTop();
            popup_height = $('#popup').height();


            window_offset = (window_height - popup_height) / 2 + window_scrollTop;
            $('#popup').offset({top: window_offset});


        },
        popupAction: function (param, url) {

            if (param.indexOf('close') != -1) {
                $('#overlay').hide();
            }
            else if (param.indexOf('open') != -1) {
                params = param.split(':');
                id = params[1];

                $('#popup > .popup-content').hide();
                $('#popup > .popup-content[data-popup-id="' + id + '"]').show();
                $('#popup > .popup-content[data-popup-id="' + id + '"]').find('[data-popup-url]').attr('href', url);

                $('#overlay').show();
                overlayPopup.setPopup(id);
            }
            else if (param.indexOf('goto') != -1) {


            }
        },
        build: function () {
            overlayPopup.setTriggers();
        },
        setTriggers: function () {
            $('#overlay .close').click(function (event) {
                event.preventDefault();
                $('#overlay').hide();
            });


            $('[data-popup-action]').click(function (event) {
                event.preventDefault();
                if (!$(this).hasClass('disabled')) {
                    params = $(this).attr('data-popup-action');
                    url = $(this).attr('href');

                    overlayPopup.popupAction(params, url);
                }
            });
        }
    }

    var fieldDate = {
        generateLinks: function () {
            index = 0;

            $('input.text.date').each(function () {
                id = index;
                if ($(this).hasClass('small')) {
                    html = '<span class="datepicker small" data-date-field-id="' + id + '">&#8203;</span>';
                }
                else {
                    html = '<span class="datepicker" data-date-field-id="' + id + '">&#8203;</span>';
                }
                $(this).attr('data-date-field-id', id);
                $(this).after(html);
                index = index + 1;
            });
        },
        build: function () {
            fieldDate.generateLinks();
            fieldDate.setTriggers();
        },
        setTriggers: function () {

            $('input.text.date').datepicker({dateFormat: 'dd/mm/yy'});

            $('input.text.date').focus(function () {
                if (typeof $(this).attr('data-date-field-id') != 'undefined') {
                    id = $(this).attr('data-date-field-id');

                    $('.datepicker[data-date-field-id="' + id + '"]').addClass('active');
                }
            });

            $('input.text.date').blur(function () {
                if (typeof $(this).attr('data-date-field-id') != 'undefined') {
                    id = $(this).attr('data-date-field-id');

                    $('.datepicker[data-date-field-id="' + id + '"]').removeClass('active');
                }
            });

            $('.datepicker').click(function (event) {
                event.preventDefault();

                if (typeof $(this).attr('data-date-field-id') != 'undefined') {
                    id = $(this).attr('data-date-field-id');

                    if ($(this).hasClass('active')) {
                        $('input[data-date-field-id="' + id + '"]').blur();
                    }
                    else {
                        $('input[data-date-field-id="' + id + '"]').focus();
                    }
                }
            });
        }
    }

    fieldInfo.build();
    overlayPopup.build();
    fieldDate.build();

});
var relatedRule = {

    getChildrenAndAppendToDom: function (childIds, domElement) {
        var url = document.URL.split("?")[0];
        for (var id in childIds) {
            $.get(url + '/' + childIds[id],
                function (data) {
                    domElement.append(data);
                }
            );
        }
    },

    changeState: function ($this, ruleChilds, childIds, expression) {
        if ($this.is(':checkbox') && $this.is(':checked')) {
            if ($.trim(ruleChilds.html()) === "") {
                this.getChildrenAndAppendToDom(childIds, ruleChilds);
            }
        }
        else if (!$this.is(':checkbox') && $this.val().search(expression) !== -1) {
            if ($.trim(ruleChilds.html()) === "") {
                this.getChildrenAndAppendToDom(childIds, ruleChilds);
            }
        } else {
            ruleChilds.html("");
        }
    }
}

var complexRule = {
    init: function (ruleData) {
        for (index in ruleData.variables) {

            var question = $("[name=\"" + ruleData.variables[index] + "\"]");
            if (question.length) {
                var events = $._data(question[0], "events");
                if (events) {
                    if (events.change.length < 1 && events.change[0].data.ruleId != ruleData.ruleId) {
                        question.on('change ', ruleData, complexRule.refreshView);
                    }
                } else {
                    question.on('change ', ruleData, complexRule.refreshView);
                }
            }
        }
    },

    refreshView: function (event) {
        console.log("refresh view " + event.type);
        var ruleData = event.data;
        var url = document.URL.split("?")[0] + '/' + ruleData.ruleId;
        $.ajax({
            type: 'POST',
            url: url,
            async: false,
            data: $("form.form").serialize(),

            success: function (data, textStatus, jqXHR) {
                console.log(textStatus);
                $("#" + ruleData.ruleId).replaceWith(data);
            },
            error: function (e, ts, et) {
                console.log("refresh view error" + ts);
            }
        });
    }
}
