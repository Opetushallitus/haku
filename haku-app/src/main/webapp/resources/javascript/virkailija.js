﻿/*
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

    var spinner = new Spinner( {
        lines: 8, // The number of lines to draw
        length: 5, // The length of each line
        width: 4, // The line thickness
        radius: 4, // The radius of the inner circle
        corners: 1, // Corner roundness (0..1)
        rotate: 0, // The rotation offset
        direction: 1, // 1: clockwise, -1: counterclockwise
        color: '#000', // #rgb or #rrggbb or array of colors
        speed: 1, // Rounds per second
        trail: 60, // Afterglow percentage
        shadow: false, // Whether to render a shadow
        hwaccel: false, // Whether to use hardware acceleration
        className: 'spinner', // The CSS class to assign to the spinner
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        top: -1, // Top position relative to parent in px
        left: 7 // Left position relative to parent in px
    });

    var cookieName = 'hakemukset_last_search';
    var cookiePath = '/haku-app/virkailija/';


/* ****************************************************************************
 * Application system selection
 */

    var applicationSystemSelection = {
        init : function() {
            $.getJSON(page_settings.contextPath + "/virkailija/hakemus/applicationSystems",
                function (data) {
                    var selectedSemester = $('#hakukausi').val();
                    var selectedYear = $('#hakukausiVuosi').val();
                    var ass = [];
                    $('#application-system option').remove();
                    $('#application-system').append('<option value="">&nbsp;</option>');
                    for (var i in data) {
                        var as = data[i];
                        var year = as.hakukausiVuosi;
                        var semester = as.hakukausiUri;

                        if (selectedSemester && selectedSemester !== semester) {
                            continue;
                        }
                        if (selectedYear && selectedYear !== year) {
                            continue;
                        }

                        var id = as.id;
                        var name = as['name_'+page_settings.lang];

                        if (!name) {
                            if (as['name_fi']) {
                                name = as['name_fi'];
                            } else if (as['name_sv']) {
                                name = as['name_sv'];
                            } else if (as['name_en']) {
                                name = as['name_en'];
                            } else {
                                name = '???';
                            }
                        }

                    $('#application-system').append('<option value="'+id+'">'+name+'</option>');
                    }
                });
        }
    };

    if (typeof page_settings !== 'undefined') {
        applicationSystemSelection.init();
    }

    $('#hakukausi').change(function() {applicationSystemSelection.init()});
    $('#hakukausiVuosi').change(function() {applicationSystemSelection.init()});

    $('input#sendingSchool').autocomplete({
        minLength : 1,
        delay: 500,
        source: function(req, res) {
            $.get(page_settings.contextPath + "/virkailija/autocomplete/school?term="+encodeURI(req.term),
                function(data) {
                    res($.map(data, function (result) {
                        var name = result.name[page_settings.lang];
                        if ( !name ) {
                            var langs = ['fi', 'sv', 'en'];
                            for (var i = 0; i < langs.length; i++) {
                                name = result.name[langs[i]];
                                if (name) {
                                    break;
                                }
                            }
                            if (!name) {
                                name = "???";
                            }
                        }
                        return {
                            label: name,
                            value: name,
                            dataId: result.dataId
                        }
                    }));
                })
        },
        select: function(event, ui) {
            $('#sendingSchoolOid').val(ui.item.dataId);
        }
    });

    $('input#sendingSchool').change(function(event) {
        if (!$(this).val()) {
            $('#sendingSchoolOid').val("");
        }
    });

    $('input#application-preference').autocomplete({
        minLength : 1,
        delay : 500,
        source: function(req, res) {
            $.get(page_settings.contextPath + "/virkailija/autocomplete/preference?term="+encodeURI(req.term),
                function(data) {
                    res($.map(data, function (result) {
                        var name = result.name[page_settings.lang];
                        if ( !name ) {
                            var langs = ['fi', 'sv', 'en'];
                            for (var i = 0; i < langs.length; i++) {
                                name = result.name[langs[i]];
                                if (name) {
                                    break;
                                }
                            }
                            if (!name) {
                                name = "???";
                            }
                        }
                        return {
                            label: name,
                            value: name,
                            dataId: result.dataId
                        }
                    }));
                })
        },
        select: function(event, ui) {
            $('#application-preference-code').val(ui.item.dataId);
        }
    });

    $('input#application-preference').change(function(event) {
        if (!$(this).val()) {
            $('#application-preference-code').val("");
        }
    });

/* ****************************************************************************
 * Organization search dialog
 */

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

        expand: function () {
            $('#orgsearch').addClass('expand');
            orgSearchDialog.set.tableCellWidth();
            orgSearchDialog.set.listHeight();
        },
        collapse: function () {
            $('#orgsearch').removeClass('expand');
            orgSearchDialog.set.tableCellWidth();
        }
    }

    orgSearchDialog.build();


/* ****************************************************************************
 * Organization search
 */

    var orgSearch = (function () {

        $('#reset-organizations').click(function (event) {
            // $('#lopoid').val('');
            // $('#lop-title').empty();
            // $('#pagination').empty();
            // $('#application-table tbody:first').empty();
            // $('#application-table thead tr td').removeAttr('class');
            // applicationSearch.updateCounters(0);
            $('#orgsearchlist').empty();
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

            function getTranslation(translations) {
                var text = translations[page_settings.lang];
                if (!text) {
                    for (var translation in translations) {
                        return translations[translation];
                    }
                }
                return text;
            }

            var label = $(document.createElement('span')).html(getTranslation(org.name.translations)).attr("id", org.oid);
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

/* ****************************************************************************
 * Application search
 */

    var applicationSearch = (function () {
        $.cookie.path = cookiePath;
        $.cookie.json = true;
        var oid = $('#oid');
        var self = this,
            $tbody = $('#application-table tbody:first'),
            $resultcount = $('#resultcount'),
            $applicationTabLabel = $('#application-tab-label'),
            maxRows = 50;

        function createQueryParameters(start, orderBy, orderDir) {
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
                $('#application-preference-code').val(obj.aoid);
                $('#lopoid').val(obj.lopoid);
                $('#application-system').val(obj.asId);
                $('#hakukausiVuosi').val(obj.asYear);
                $('#hakukausi').val(obj.asSemester);
                $('#sendingSchoolOid').val(obj.sendingSchoolOid);
                $('#sendingClass').val(obj.sendingClass);
                $('#discretionary-only').prop('checked', obj.discretionaryOnly);
                $('#lop-title').text(obj.lopTitle ? obj.lopTitle.replace('ThisIsStupidButNecessary', '&') : undefined);
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
                addParameter(obj, 'aoidCode', '#application-preference-code');
                addParameter(obj, 'lopoid', '#lopoid');
                addParameter(obj, 'asId', '#application-system');
                addParameter(obj, 'asYear', '#hakukausiVuosi');
                addParameter(obj, 'asSemester', '#hakukausi');
                addParameter(obj, 'sendingSchoolOid', '#sendingSchoolOid');
                addParameter(obj, 'sendingClass', '#sendingClass');
                addParameter(obj, 'discretionaryOnly', '#discretionary-only');
                var lopTitle = $('#lop-title').text();
                if (lopTitle) {
                    lopTitle = lopTitle.replace('&', 'ThisIsStupidButNecessary');
                    obj['lopTitle'] = lopTitle;
                }
                if ($('#orgsearch').hasClass('expand')) {
                    obj['orgSearchExpanded'] = true;
                }
                obj['discretionaryOnly'] = $('#discretionary-only').prop('checked');
                obj['checkAllApplications'] = $('#check-all-applications').prop('checked');
                obj['start'] = start;
                obj['rows'] = maxRows;
                obj['orderBy'] = orderBy;
                obj['orderDir'] = orderDir;
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
            var queryParameters = createQueryParameters(start, orderBy, orderDir);
            start = queryParameters.start;
            spinner.stop();
            spinner.spin(document.getElementById('search-spinner'));
            $.getJSON(page_settings.contextPath + "/applications/listshort",
                queryParameters,
                function (data) {
                    $tbody.empty();
                    self.updateCounters(data.totalCount);
                    if (data.totalCount > 0) {
                        $(data.results).each(function (index, item) {
                            var cleanOid = item.oid.replace(/\./g, '_');
                            $tbody.append('<tr><td><input class="check-application" id="check-application-' + cleanOid + '" type="checkbox"/></td><td>' +
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
                                $('#check-all-applications').prop('checked', false);
                            }
                        }
                        $('#pagination').bootstrapPaginator(options);
                        applicationSearch.setSortOrder(queryParameters.orderBy, queryParameters.orderDir);
                        if (queryParameters.asId && queryParameters.aoid) {
                            $('#excel-link').removeAttr('hidden');
                            var href = $('#excel-link').attr('href');
                            href = href + queryParameters.asId + '/' + queryParameters.aoid + '?'  +
                                Object.keys(queryParameters).reduce(function(a,k){
                                    if (k !== 'asId' || k !== 'aoid') {
                                        a.push(k+'='+encodeURIComponent(queryParameters[k]));
                                    }
                                    return a
                                },[]).join('&');
                            var href = $('#excel-link').attr('href',href);
                        } else {
                            $('#excel-link').attr('hidden','hidden');
                        }
                    } else {
                        $('#pagination').empty();
                        $('#excel-link').attr('hidden', 'hidden');
                    }
                    spinner.stop();
                    window.location.hash = '';

                    $('.application-link').click(function () {
                        var applicationList = '';
                        var selectedApplication = null;
                        $('input.check-application').each(function (index) {
                            if ($(this).is(":checked")) {
                                var application = $(this).attr('id').replace(/^.*-/g, '').replace(/_/g, '.');
                                applicationList += application + ',';
                                if (!selectedApplication) {
                                    selectedApplication = application;
                                }
                            }
                        });
                        applicationList = applicationList.substring(0, applicationList.length - 1);
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
                        }
                        return true;
                    });

                    $('input.check-application').each(function (index) {
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
            $('#pagination').empty();
            $('#lop-title').empty();
            $('#lopoid').val('');
            $('#entry').val('');
            $('#application-state').val('');
            $('#application-preference').val('');
            $('#application-preference-code').val('');
            $('#application-system').val('');
            $('#hakukausiVuosi').val(hakukausiDefaultYear);
            $('#hakukausi').val(hakukausiDefaultSemester);
            $('#sendingSchoolOid').val('');
            $('#sendingSchool').val('');
            $('#sendingClass').val('');
            $('#discretionary-only').attr('checked', false);
            $('#check-all-applications').attr('checked', false);
            $('#excel-link').attr('hidden', 'hidden');
        },
        this.getSortOrder = function(columnName) {
            var column = $('#application-table-header-'+columnName);
            var clazz = column.attr('class');
            var sortOrder = 'asc';
            if (clazz === 'sorted-asc') {
                clazz = 'sorted-desc';
                sortOrder = 'desc';
            } else {
                clazz = 'sorted-asc';
            }
            return sortOrder;
        },
        this.setSortOrder = function(columnName, sortOrder) {
            if (columnName && sortOrder) {
                var column = $('#application-table-header-'+columnName);
                column.attr('class', 'sorted-'+sortOrder)
            }
        },
        this.sort = function(sortBy) {
            var sortOrder = applicationSearch.getSortOrder(sortBy);
            applicationSearch.search(0, sortBy, sortOrder);
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

    $('#application-table-header-fullName').click(function (event) {
        applicationSearch.sort('fullName');
    });

    $('#application-table-header-oid').click(function (event) {
        applicationSearch.sort('oid');
    });

    $('#application-table-header-state').click(function (event) {
        applicationSearch.sort('state');
    });

    $('#check-all-applications').change(function () {
        if ($(this).is(":checked")) {
            $('.check-application').attr('checked', 'checked');
        } else {
            $('.check-application').removeAttr('checked');
        }
    });

    $('#open-selected').click(function () {
        var applicationList = '';
        var selectedApplication = null;
        $('input.check-application').each(function (index) {
            if ($(this).is(":checked")) {
                var application = $(this).attr('id').replace(/^.*-/g, '').replace(/_/g, '.');
                applicationList += application + ',';
                if (!selectedApplication) {
                    selectedApplication = application;
                }
            }
        });
        applicationList = applicationList.substring(0, applicationList.length - 1);
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
        }
        if (selectedApplication) {
            location.href = page_settings.contextPath + "/virkailija/hakemus/"+selectedApplication+"/"
        }
    });

    $('#notApplied').click(function() {
        var school = $('#sendingSchoolOid').val();
        var clazz = $('#sendingClass').val();
        var as = $('#application-system').val();
        if (school && as) {
            var url = location.protocol+"//"+location.host+"/suoritusrekisteri/#/eihakeneet?haku="+as
                +"&oppilaitos="+school
                + (clazz !== "" ? "&luokka="+clazz : "");
            window.location.href=url;
        } else {
            alert('Koulu ja haku ovat pakollisia tietoja')
        }
    });


    /* *****************************************************************************************
     *
     * Keyboard shortcuts
     *
     */

    $(document).bind('keypress', 'a', function() { $('#check-all-applications').click()});
    $(document).bind('keypress', 'o', function() { $('#open-selected').click()});

    $(document).bind('keypress', 'j', function() {
        var firstApplication = false;
        var checkedApplication = false;
        var checkThis = false;
        $('.check-application').each(function() {
            if (!firstApplication) {
                firstApplication = $(this).attr('id');
            }
            if ($(this).prop('checked')) {
                checkedApplication = $(this).attr('id');
            } else if (checkedApplication && !checkThis) {
                checkThis = $(this).attr('id');
            }
            $(this).prop('checked', false);
        });
        if (checkedApplication) {
            $('#'+checkedApplication).prop('checked', false);
        }
        if (checkThis) {
            $('#'+checkThis).prop('checked', true);
        } else {
            $('#'+firstApplication).prop('checked', true);
        }
    });

    $(document).bind('keypress', 'shift+j', function() {
        var firstApplication = false;
        var checkedApplications = false;
        var checkThis = false;
        $('.check-application').each(function(index) {
            if (!firstApplication) {
                firstApplication = $(this).attr('id');
            }
            if ($(this).prop('checked')) {
                checkedApplications = checkedApplications + ',' + $(this).attr('id');
            } else if (checkedApplications && !checkThis) {
                checkThis = $(this).attr('id');
            }
            $(this).prop('checked', false);
        });
        if (!checkedApplications && !checkThis) {
            $('#'+firstApplication).prop('checked', true);
        } else {
            $('#'+checkThis).prop('checked', true);
            var checkThese = checkedApplications.split(',');
            for (var i = 0; i < checkThese.length; i++) {
                $('#'+checkThese[i]).prop('checked', true);
            }
        }
    });

    $(document).bind('keypress', 'k', function() {
        var lastApplication = false;
        var checkedApplication = false;
        var checkThis = false;
        $('.check-application').each(function() {
            if ($(this).prop('checked') && !checkThis) {
                checkedApplication = $(this).attr('id');
                checkThis = lastApplication;
            }
            lastApplication = $(this).attr('id');
            $(this).prop('checked', false);
        });
        if (checkedApplication) {
            $('#'+checkedApplication).prop('checked', false);
        }
        if (checkThis) {
            $('#'+checkThis).prop('checked', true);
        } else {
            $('#'+lastApplication).prop('checked', true);
        }
    });

    $(document).bind('keypress', 'shift+k', function() {
        var lastApplication = false;
        var checkedApplications = false;
        var checkThis = false;
        $('.check-application').each(function() {
            if ($(this).prop('checked') && !checkThis) {
                checkThis = lastApplication;
            }
            if ($(this).prop('checked')) {
                checkedApplications = checkedApplications + ',' + $(this).attr('id');
            }
            lastApplication = $(this).attr('id');
            $(this).prop('checked', false);
        });
        if (!checkedApplications && !checkThis) {
            $('#'+lastApplication).prop('checked', true);
        } else {
            $('#'+checkThis).prop('checked', true);
            var checkThese = checkedApplications.split(',');
            for (var i = 0; i < checkThese.length; i++) {
                $('#'+checkThese[i]).prop('checked', true);
            }
        }
    });

});
