

var gradegrid = {

    addLanguage: function() {

        var newLang = $('#gradegrid-table tr:last').clone().css('display', '');
        $("#gradegrid-table tr.gradegrid-language-row:last").after(newLang);
    }
}

$(document).ready(function() {
   $("#add_language_button").click(gradegrid.addLanguage);
})
