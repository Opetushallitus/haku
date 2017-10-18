var virkailijaSide = true;

$(document).ready(function() {

    var cookieName = 'hakemukset_last_search';
    var cookiePath = '/haku-app/virkailija/';

/* ****************************************************************************
 * Application additional info
 */

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

/* ****************************************************************************
 * Application navigation
 */

    var applicationNavigation = {

        initLink : function(oid, elementId, prev) {
            var id = elementId;
            $.getJSON(window.url("haku-app.applications.single", oid),
                function (data) {
                    var etunimet = data.answers.henkilotiedot.Etunimet;
                    var sukunimi = data.answers.henkilotiedot.Sukunimi;
                    var linkText = prev ?
                        "&lt;&nbsp;Edellinen ("+sukunimi + " " + etunimet+")" :
                        "("+sukunimi + " " + etunimet+")&nbsp;Seuraava&nbsp;&gt;";
                    $('#'+elementId).text(linkText);
                    $('#'+elementId).attr('href', window.url("haku-app.application", oid));
                }
            )
        },

        init : function() {

            $.cookie.path = cookiePath;
            $.cookie.json = true;

            var lastSearch = $.cookie(cookieName);
            if (!lastSearch) {
                return;
            }
            var previousApplication = undefined;
            var nextApplication = undefined;
            var applicationList = lastSearch.applicationList;
            if (typeof applicationList !== "string") {
                return;
            }
            var applicationArray = applicationList.split(',');
            if (applicationArray.length == 0) {
                return;
            }
            var i = 0;
            for (i = 0; i < applicationArray.length; i++) {
                var curr = applicationArray[i];
                var oid = page_settings.applicationOid;
                if (curr !== page_settings.applicationOid) {
                    continue;
                }
                if (i > 0) {
                    previousApplication = applicationArray[i-1];
                }
                if (i < applicationArray.length - 1) {
                    nextApplication = applicationArray[i+1];
                }
                break;
            }
            i++

            $('#applicationCount').text("" + i + " / " + applicationArray.length);

            if (previousApplication) {
                $.getJSON(window.url("haku-app.applications.single", previousApplication),
                    function (data) {
                        var etunimet = data.answers.henkilotiedot.Etunimet;
                        var sukunimi = data.answers.henkilotiedot.Sukunimi;
                        var linkText = "< Edellinen ("+sukunimi + " " + etunimet+")";
                        $('#previousApplication').text(linkText);
                        var prevApplicationUrl = window.url("haku-app.application", previousApplication);
                        $(document).bind('keypress', 'j', function() { location.href = prevApplicationUrl });
                        $('#previousApplication').attr('href', prevApplicationUrl);
                    }
                )
            }
            if (nextApplication) {
                $(document).bind('keypress', 'l', function() {$('#nextApplication').click() });
                $.getJSON(window.url("haku-app.applications.single", nextApplication),
                    function (data) {
                        var etunimet = data.answers.henkilotiedot.Etunimet;
                        var sukunimi = data.answers.henkilotiedot.Sukunimi;
                        var linkText = "("+sukunimi + " " + etunimet+") Seuraava >";
                        $('#nextApplication').text(linkText);
                        var nextApplicationUrl = window.url("haku-app.application", nextApplication)
                        $(document).bind('keypress', 'k', function() { location.href = nextApplicationUrl });
                        $('#nextApplication').attr('href', nextApplicationUrl);
                    }
                )
            }

        }
    }

    /* ************************************************************************
     * Hotkey init
     */

    $(document).bind('keypress', '1', function() { $('#applicationTab').click() });
    $(document).bind('keypress', '2', function() { $('#valintaTab').click() });
    $(document).bind('keypress', '3', function() { $('#applicationValintaTab').click() });
    $(document).bind('keypress', '4', function() { $('#kelpoisuusliitteetTab').click() });
    $(document).bind('keypress', 'b', function() { location.href = $('#back').attr('href') });

    if (page_settings.preview === "true") {
        applicationNavigation.init();
    }

});
