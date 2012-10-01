

var gradegrid = {

    addLanguage: function() {

        $("#gradegrid-table tr.gradegrid-language-row:last").after('<tr><td colspan="5">new lang</td></tr>')

    }
}

$(document).ready(function() {
   $("#add_language_button").click(gradegrid.addLanguage);
})
