/**
    Handles adding new language rows to the GradeGrid component.
*/

var gradegrid = (function() {

    return {

        addLanguage: function() {

            var customLanguageCount = $(".gradegrid-custom-language-row").length

            var newLang = $('#gradegrid-table tr:last').clone().css('display', '')
                .addClass('gradegrid-language-row').addClass('gradegrid-custom-language-row');

            $(newLang.find('select').get(0)).attr('id', 'custom-scope_' + customLanguageCount).attr('name', 'custom-scope_' + customLanguageCount);
            $(newLang.find('select').get(1)).attr('id', 'custom-language_' + customLanguageCount).attr('name', 'custom-language_' + customLanguageCount);
            $(newLang.find('select').get(2)).attr('id', 'custom-commongrade_' + customLanguageCount).attr('name', 'custom-commongrade_' + customLanguageCount);
            $(newLang.find('select').get(3)).attr('id', 'custom-optionalgrade_' + customLanguageCount).attr('name', 'custom-optionalgrade_' + customLanguageCount);

            $("#gradegrid-table tr.gradegrid-language-row:last").after(newLang);
        }
    };
}());

$(document).ready(function() {
   $("#add_language_button").click(gradegrid.addLanguage);
})
