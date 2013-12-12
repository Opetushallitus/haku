/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

$(document).ready(function () {
// Organisation search
// Handle presentation of organisation search form and results

    var orgSearchDialog = {
        settings: {
            listenTimeout: 1000
        },
        build: function () {
            orgSearchDialog.set.listHeight();
            orgSearchDialog.set.tableCellWidth();
            orgSearchDialog.listen.dialogDimensions();
            orgSearchDialog.set.triggers();
        },
        listen: {
            dialogDimensions: function () {
                // Listen for changes in organisation search height,
                // and adjust search results list's height accordinly

                height = $('#orgsearch').height();
                width = $('#orgsearch').outerWidth(true);
                setTimeout(function () {
                    if (height != $('#orgsearch').height()) {
                        orgSearchDialog.set.listHeight();
                    }
                    if (width != $('#orgsearch').outerWidth(true)) {
                        orgSearchDialog.set.tableCellWidth();
                    }

                    orgSearchDialog.listen.dialogDimensions();

                }, orgSearchDialog.settings.listenTimeout);
            }
        },
        set: {
            listHeight: function () {
                // Set organisation search result list to fill remaining vertical space
                height = $('#orgsearch').height();
                form_height = $('#orgsearch .orgsearchform').outerHeight(true);
                list_height = height - form_height;
                $('#orgsearch .orgsearchlist').css({'height': list_height + 'px'});
            },
            tableCellWidth: function () {
                // Set organisation search dialog's parenting table cell width
                width = $('#orgsearch').outerWidth(true);
                $('#orgsearch').parent('td').css({'width': width + 'px'});
            },
            triggers: function () {
                $('body').on('click', '#orgsearch a.expander', function (event) {
                    event.preventDefault();
                    target = $(this).closest('#orgsearch');
                    if (target.hasClass('expand')) {
                        orgSearchDialog.collapse();
                    }
                    else {
                        orgSearchDialog.expand();
                    }
                });
            }
        },

        expand: function() {
            $('#orgsearch').addClass('expand');
            orgSearchDialog.set.tableCellWidth();
            orgSearchDialog.set.listHeight();
        },
        collapse: function() {
            $('#orgsearch').removeClass('expand');
            orgSearchDialog.set.tableCellWidth();
        }
    }

    orgSearchDialog.build();

    var cookieName = 'hakemukset_last_search';
    var cookiePath = '/haku-app/virkailija';

    var applicationSearch = (function () {
        $.cookie.path = cookiePath;
        $.cookie.json = true;
        var oid = $('#oid');
        var self = this,
            $tbody = $('#application-table tbody:first'),
            $resultcount = $('#resultcount'),
            $applicationTabLabel = $('#application-tab-label'),
            maxRows = 50;

        function createQueryParameters(start) {
            $.cookie.path = cookiePath;
            $.cookie.json = true;
            var lastSearch = $.cookie(cookieName);
            var obj = {};
            if (lastSearch && window.location.hash === '#useLast') {
                obj = lastSearch;
                $('#entry').val(obj.q);
                $('#oid').val(obj.oid);
                $('#application-state').val(obj.appState);
                $('#application-preference').val(obj.aoid);
                $('#lopoid').val(obj.lopoid);
                $('#lop-title').text(obj.lopTitle);
                $('#application-system').val(obj.asId);
                $('#hakukausiVuosi').val(obj.asYear);
                $('#hakukausi').val(obj.asSemester);
                $('#discretionary-only').prop('checked', obj.discretionaryOnly);
                if (obj.orgSearchExpanded) {
                    orgSearchDialog.expand();
                }
                $('#check-all-applications').prop('checked', obj.checkAllApplications);
                start = obj.start;
            } else {
                addParameter(obj, 'q', '#entry');
                addParameter(obj, 'oid', '#oid');
                addParameter(obj, 'appState', '#application-state');
                addParameter(obj, 'aoid', '#application-preference');
                addParameter(obj, 'lopoid', '#lopoid');
                addParameter(obj, 'asId', '#application-system');
                addParameter(obj, 'asYear', '#hakukausiVuosi');
                addParameter(obj, 'asSemester', '#hakukausi');
                addParameter(obj, 'discretionaryOnly', '#discretionary-only');
                var lopTitle = $('#lop-title').text();
                if (lopTitle) {
                    obj['lopTitle'] = lopTitle;
                }
                if ($('#orgsearch').hasClass('expand')) {
                    obj['orgSearchExpanded'] = true;
                }
                obj['discretionaryOnly'] = $('#discretionary-only').prop('checked');
                obj['checkAllApplications'] = $('#check-all-applications').prop('checked');
                obj['start'] = start;
                obj['rows'] = maxRows;
                $.removeCookie(cookieName);
                $.cookie(cookieName, obj);
            }
            return obj;
        }

        function addParameter(obj, queryParameterName, selector) {
            var val = $(selector).val();
            if (val) {
                obj[queryParameterName] = val;
            }
        }

        this.search = function (start, orderBy, orderDir) {
            $('#application-table thead tr td').removeAttr('class');
            var queryParameters = createQueryParameters(start);
            start = queryParameters.start;
            $('#search-spinner').show();
            $.getJSON(page_settings.contextPath + "/applications/list/"+orderBy+"/"+orderDir,
                queryParameters,
                function (data) {
                    $tbody.empty();
                    self.updateCounters(data.totalCount);
                    if (data.totalCount > 0) {
                        $(data.results).each(function (index, item) {
                            var cleanOid = item.oid.replace(/\./g, '_');
                            $tbody.append('<tr><td><input class="check-application" id="check-application-'+cleanOid+'" type="checkbox"/></td><td>' +
                                (item.lastName ? item.lastName : '') + ' ' + (item.firstNames ? item.firstNames : '') + '</td><td>' +
                                (item.ssn ? item.ssn : '') + '</td><td><a class="application-link" href="' +
                                page_settings.contextPath + '/virkailija/hakemus/' + item.oid + '/">' +
                                item.oid + '</a></td><td>' + (item.state ? page_settings[item.state] : '') + '</td></tr>');
                        });
                        var options = {
                            currentPage: Math.ceil(start / maxRows) + 1,
                            totalPages: Math.ceil(data.totalCount / maxRows),
                            onPageClicked: function (e, originalEvent, type, page) {
                                applicationSearch.search((page - 1) * maxRows, orderBy, orderDir);
                            }
                        }
                        $('#pagination').bootstrapPaginator(options);
                    } else {
                        $('#pagination').empty();
                    }
                    $('#search-spinner').hide();
                    window.location.hash = '';
                    $('input.check-application').each(function(index) {
                        $.cookie.path = cookiePath;
                        $.cookie.json = true;
                        var lastSearch = $.cookie(cookieName);
                        if (lastSearch && lastSearch.applicationList) {
                            var applicationList = lastSearch.applicationList;
                            var application = $(this).attr('id').replace(/^.*-/g, '').replace(/_/g, '.');
                            if (applicationList.indexOf(application) !== -1) {
                                $(this).attr('checked', 'checked');
                            }
                        }
                    });
                });
        },
        this.updateCounters = function (count) {
            $resultcount.empty().append(count);
            $applicationTabLabel.empty().append('Hakemukset (' + count + ')');
        },
        this.reset = function () {
            $.cookie.path = cookiePath;
            $.cookie.json = true;
            $.removeCookie(cookieName);
            $('#application-table thead tr td').removeAttr('class');
            self.updateCounters(0);
            $tbody.empty();
            $('#lopoid').val('');
            $('#lop-title').empty();
            $('#entry').val('');
            $('#application-state').val('ACTIVE');
            $('#application-preference').val('');
            $('#pagination').empty();
            $('#application-system').val('');
            $('#hakukausiVuosi').val(hakukausiDefaultYear);
            $('#hakukausi').val(hakukausiDefaultSemester);
            $('#discretionary-only').attr('checked', false);
        }
        return this;
    })();

    if ($.cookie(cookieName) && window.location.hash === '#useLast') {
        applicationSearch.search(0, 'fullName', 'asc');
    }

    $('#search-applications').click(function (event) {
        window.location.hash = '';
        $.cookie.path = cookiePath;
        $.cookie.json = true;
        $.removeCookie(cookieName);
        applicationSearch.search(0, 'fullName', 'asc');
        return false;
    });

    $('#reset-search').click(function (event) {
        applicationSearch.reset();
        return false;
    });

    function sortApplications(column, sortBy) {
        var clazz = column.attr('class');
        var sortOrder = 'asc';
        if (clazz === 'sorted-asc') {
            clazz = 'sorted-desc';
            sortOrder = 'desc';
        } else {
            clazz = 'sorted-asc';
        }
        applicationSearch.search(0, sortBy, sortOrder);
        column.attr('class', clazz);
    }

    $('#application-table-header-fullName').click(function (event) {
        sortApplications($(this), 'fullName');
    });

    $('#application-table-header-applicationOid').click(function (event) {
        sortApplications($(this), 'applicationOid');
    });

    $('#application-table-header-state').click(function (event) {
        sortApplications($(this), 'state');
    });

    $('#check-all-applications').change(function() {
        if($(this).is(":checked")) {
            $('.check-application').attr('checked', 'checked');
        } else {
            $('.check-application').removeAttr('checked');
        }
    });

    $('#open-application').click(function() {
        var applicationList = '';
        var selectedApplication = null;
        $('input.check-application').each(function(index) {
            if($(this).is(":checked")) {
                var application = $(this).attr('id').replace(/^.*-/g, '').replace(/_/g, '.');
                applicationList += application + ',';
                if (!selectedApplication) { selectedApplication = application; }
            }
        });
        if (selectedApplication) {
            $.cookie.path = cookiePath;
            $.cookie.json = true;
            var lastSearch = $.cookie(cookieName);
            if (lastSearch) {
                lastSearch.applicationList = applicationList;
                lastSearch.checkAllApplications = $('#check-all-applications').prop('checked');
                $.removeCookie(cookieName);
                $.cookie(cookieName, lastSearch);
            }
            $('#applicationList').val(applicationList);
            $('#selectedApplication').val(selectedApplication);
            $('#open-applications').submit();
        }
    });

    $('#previousApplication').click(function() {
        var selectedApplication = previousApplication;
        if (selectedApplication) {
            $('#selectedApplication').val(selectedApplication);
            $('#open-applications').submit();
        }
    });

    $('#nextApplication').click(function() {
        var selectedApplication = nextApplication;
        if (selectedApplication) {
            $('#selectedApplication').val(selectedApplication);
            $('#open-applications').submit();
        }
    });

    var additionalInfo = (function () {
        var self = this, $extraData = $('#extra-data');

        this.addNewRow = function () {
            var $row = $('<div class="form-row"></div>'), $inputKey = $('<input type="text" placeholder="Avain" class="extra-key-input"/>'),
                $inputValue = $('<input type="text" placeholder="Arvo" class="margin-horizontal-4"/>'),
                $removeButton = $('<button class="remove_key_value_button remove" type="button"><span>Poista</span></button>');

            $inputKey.bind('change', function () {
                $(this).next().prop("name", this.value);
            });
            $removeButton.bind('click', function () {
                var row = $(this).parent();
                additionalInfo.removeRow(row);
            });
            $row.append($inputKey);
            $row.append($inputValue);
            $row.append($removeButton);
            $extraData.append($row);
        }

        this.init = function () {
            $('.extra-key-input').each(function (index, domEle) {
                $(domEle).bind('change', function () {
                    $(this).next().prop("name", this.value);
                });
            });
        }

        this.removeRow = function (row) {
            $(row).remove();
        }
        self.init();
        return this;
    })();


    $('#add_key_value_button').click(function (event) {
        additionalInfo.addNewRow();
    });

    $('.remove_key_value_button').click(function (event) {
        var row = $(this).parent();
        additionalInfo.removeRow(row);
    });

    var orgSearch = (function () {
        $('#reset-organizations').click(function (event) {
            $('#lopoid').val('');
            $('#lop-title').empty();
            applicationSearch.search(0, 'fullName', 'asc');
        });
        $('#search-organizations').click(function (event) {
            var parameters = $('#orgsearchform').serialize();
            $('#search-organizations').attr('disabled', 'disabled');
            $.getJSON(page_settings.contextPath + "/organization/hakemus?" + $('#orgsearchform').serialize(),
                function (data) {
                    var toTree = function (data) {
                        var ul = $(document.createElement("ul")).addClass('branch');
                        for (var i = 0; i < data.length; i++) {
                            var children = data[i].children;
                            var li = createListItem(children.length < 1, data[i].organization);
                            var childItems = toTree(children);
                            childItems.appendTo(li);
                            li.appendTo(ul);
                            childItems.hide();
                        }
                        return ul;
                    };

                    $('#orgsearchlist').empty();
                    $('#orgsearchlist').append(toTree(data));
                    $('#orgsearchlist').find('ul').eq(0).addClass("treelist").removeClass('branch');
                }
            ).complete(function () {
                $('#search-organizations').removeAttr('disabled');
            });
            return false;
        });
        function createListItem(leaf, org) {
            var li = $(document.createElement("li"));
            var icon = leaf ? 'file' : 'folder';
            var label = $(document.createElement('span')).html(org.name.translations.fi).attr("id", org.oid);
            var link = $(document.createElement('a')).attr('href', '#').addClass('label');
            var span = $(document.createElement('span')).addClass('icon close').addClass(icon).html('&#8203;');
            span.appendTo(link);
            label.appendTo(link);
            link.appendTo(li);
            if (!leaf) {
                span.click(function (e) {
                    e.preventDefault();
                    $(this).parent().parent().children('ul').slideToggle();
                    toggleIcon($(this));
                });
            }
            label.click(function (e) {
                $('#lopoid').val($(this).attr('id'));
                $('#lop-title').text($(this).html());
                applicationSearch.search(0, 'fullName', 'asc');
                e.preventDefault();
            });

            return li;
        }

        function toggleIcon(children) {
            if (children.hasClass('close')) {
                children.removeClass('close');
                children.addClass('open');
            } else {
                children.removeClass('open');
                children.addClass('close');
            }
        }

    })();

});
