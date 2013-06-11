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
                        target.removeClass('expand');
                        orgSearchDialog.set.tableCellWidth();
                    }
                    else {
                        target.addClass('expand');
                        orgSearchDialog.set.tableCellWidth();
                        orgSearchDialog.set.listHeight();
                    }
                });
            }
        }
    }

    orgSearchDialog.build();


    var applicationSearch = (function () {
        var oid = $('#oid');
        var self = this, $q = $('#entry'), $appState = $('#application-state'),
            $appPreference = $('#application-preference'),
            $tbody = $('#application-table tbody:first'), $resultcount = $('#resultcount'),
            $applicationTabLabel = $('#application-tab-label');

        this.search = function () {

            $.getJSON(page_settings.contextPath + "/applications", {
                q: $q.val(),
                oid: oid.val(),
                appState: $appState.val(),
                appPreference: $appPreference.val(),
                lopOid: $('#lopoid').val()
            }, function (data) {
                $tbody.empty();
                self.updateCounters(data.length);
                $(data).each(function (index, item) {
                    var henkilotiedot = item.answers.henkilotiedot;
                    $tbody.append('<tr><td>' +
                        henkilotiedot.Sukunimi + '</td><td>' +
                        henkilotiedot.Etunimet + '</td><td>' +
                        henkilotiedot.Henkilotunnus + '</td><td><a class="application-link" href="' +
                        page_settings.contextPath + '/virkailija/hakemus/' + item.oid + '/">' +
                        item.oid + '</a></td><td>' + item.state + '</td></tr>');
                });
            });
        },
            this.updateCounters = function (count) {
                $resultcount.empty().append(count);
                $applicationTabLabel.empty().append('Hakemukset ' + count);
            },
            this.reset = function () {
                self.updateCounters(0);
                $tbody.empty();
                $q.val('');
                $appState.val('');
                $appPreference.val('');
            }
        return this;
    })();


    $('#search-applications').click(function (event) {
        applicationSearch.search();
        return false;
    });

    $('#reset-search').click(function (event) {
        applicationSearch.reset();
        return false;
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
