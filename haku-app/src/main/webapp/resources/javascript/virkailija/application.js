

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

    $('#previousApplication').click(function () {
        var selectedApplication = previousApplication;
        if (selectedApplication) {
            $('#selectedApplication').val(selectedApplication);
            $('#open-applications').submit();
        }
    });

    $('#nextApplication').click(function () {
        var selectedApplication = nextApplication;
        if (selectedApplication) {
            $('#selectedApplication').val(selectedApplication);
            $('#open-applications').submit();
        }
    });

});