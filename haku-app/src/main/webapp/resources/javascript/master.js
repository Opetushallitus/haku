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
    }

    popover.build();

});

var complexRule = {
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
                        question.on('change paste keyup input', ruleData, complexRule.refreshView);
                    }
                } else {
                    question.on('change paste keyup input', ruleData, complexRule.refreshView);
                }
            }
        }
    },

    refreshView: function (event) {
        var ruleData = event.data;
        var split = document.URL.split("?");
        var url = split[0] + '/' + ruleData.ruleId;
        if (split.length > 1) {
            url = url + "?" + split[1];
        }
        $.ajax({
            type: 'POST',
            url: url,
            async: false,
            data: $("form.form").serialize(),

            success: function (data, textStatus, jqXHR) {
                $("#" + ruleData.ruleId).replaceWith(data);
            },
            error: function (e, ts, et) {
                //console.log("refresh view error" + ts);
            }
        });
    }
};
