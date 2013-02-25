/**
    Handles adding new language rows to the GradeGrid component.
*/

var gradegrid = (function() {
    var langRowUrl = gradegrid_settings.contextPath + '/lomake/' +
        gradegrid_settings.applicationSystemId + '/' + gradegrid_settings.formId + '/' +
        gradegrid_settings.elementId + '/' + 'additionalLanguageRow';

    function appendRow($rowHtml) {
        $rowHtml.data("gradegrid-row", {lang: true, customLang: true});

        $("tr").filter(
            function(index) {
                return $(this).data("gradegrid-row");
            }
        ).last().after($rowHtml);
    }

    function updateCustomLanguageIndices() {
        $("tr").filter(
            function() {
                return $(this).data("gradegrid-row") && $(this).data("gradegrid-row").customLang;
            }
        ).each(
            function(i, langRow) {
                $($(langRow).find('select').get(0)).attr('id', 'custom-scope_' + i).attr('name', 'custom-scope_' + i);
                $($(langRow).find('select').get(1)).attr('id', 'custom-language_' + i).attr('name', 'custom-language_' + i);
                $($(langRow).find('select').get(2)).attr('id', 'custom-commongrade_' + i).attr('name', 'custom-commongrade_' + i);
                $($(langRow).find('select').get(3)).attr('id', 'custom-optionalgrade_' + i).attr('name', 'custom-optionalgrade_' + i);
            }
        );
    }

    return {
        addLanguage: function(event) {
            $.get(langRowUrl, function(data){
                appendRow($(data));
                updateCustomLanguageIndices();
            });
        },
        removeLanguage: function(event) {
            $(this).parent().parent().remove();
            updateCustomLanguageIndices();
            return false;
        }
    };
}());

$(document).ready(function() {
   $("#add_language_button").click(gradegrid.addLanguage);

    //add listner for dynamically added language rows
    $('#gradegrid-table').on('click', 'a.btn-remove', gradegrid.removeLanguage);
})
