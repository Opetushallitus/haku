/**
 Handles adding new language rows to the GradeGrid component.
 */

var gradegrid = (function () {
    var langRowUrl = document.URL + '/' +
        gradegrid_settings.elementId + '/' + 'additionalLanguageRow';

    function appendRow(rowHtml) {
        $("#add-lang").before(rowHtml);
    }

    function updateCustomLanguageIndices() {
        $(".dynamic").each(function (i, row) {
            var selects = $(row).find('select');
            $(selects.get(0)).attr('id', 'custom-scope_' + i).attr('name', 'custom-scope_' + i);
            $(selects.get(1)).attr('id', 'custom-language_' + i).attr('name', 'custom-language_' + i);
            $(selects.get(2)).attr('id', 'custom-commongrade_' + i).attr('name', 'custom-commongrade_' + i);
            $(selects.get(3)).attr('id', 'custom-optionalgrade_' + i).attr('name', 'custom-optionalgrade_' + i);
            if (selects.length == 5) {
                $(selects.get(4)).attr('id', 'custom-secondoptionalgrade_' + i).attr('name', 'custom-secondoptionalgrade_' + i);
            }
        })
    }

    return {
        addLanguage: function (event) {
            $.get(langRowUrl, function (data) {
                appendRow(data);
                updateCustomLanguageIndices();
            });
        },
        removeLanguage: function (event) {
            $(this).closest(".dynamic").remove();
            updateCustomLanguageIndices();
            return false;
        }
    };
}() );

$(document).ready(function () {
    $("#add_language_button").click(gradegrid.addLanguage);
    //add listener for dynamically added language rows
    $('#gradegrid-table').on('click', 'a.btn-remove', gradegrid.removeLanguage);
})
