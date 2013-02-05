/**
    Handles adding new language rows to the GradeGrid component.
*/

var gradegrid = (function() {
    var langRowUrl = gradegrid_settings.contextPath + '/lomake/' +
        gradegrid_settings.applicationSystemId + '/' + gradegrid_settings.formId + '/' +
        gradegrid_settings.elementId + '/' + 'additionalLanguageRow';

    function appendRow(rowHtml) {
        rowHtml.addClass('gradegrid-language-row').addClass('gradegrid-custom-language-row');
        var customLanguageCount = $(".gradegrid-custom-language-row").length;
        $(rowHtml.find('select').get(0)).attr('id', 'custom-scope_' + customLanguageCount).attr('name', 'custom-scope_' + customLanguageCount);
        $(rowHtml.find('select').get(1)).attr('id', 'custom-language_' + customLanguageCount).attr('name', 'custom-language_' + customLanguageCount);
        $(rowHtml.find('select').get(2)).attr('id', 'custom-commongrade_' + customLanguageCount).attr('name', 'custom-commongrade_' + customLanguageCount);
        $(rowHtml.find('select').get(3)).attr('id', 'custom-optionalgrade_' + customLanguageCount).attr('name', 'custom-optionalgrade_' + customLanguageCount);
        $("#gradegrid-table tr.gradegrid-language-row:last").after(rowHtml);
    }

    return {
        addLanguage: function(event) {
            $.get(langRowUrl, function(data){
                appendRow($(data));
            });
            
        }
    };
}());

$(document).ready(function() {
   $("#add_language_button").click(gradegrid.addLanguage);

    //add listner for dynamically added language rows
    $('#gradegrid-table').on('click', 'a.btn-remove', function() {
        $(this).parent().parent().remove();
        return false;
    });
})
