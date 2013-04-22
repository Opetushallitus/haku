$(document).ready(function () {
    // hide and disable closest tr
    $('#gradegrid-table').on('click', 'a.btn-remove', function () {
        var row = $(this).closest('tr');
        $("#" + row.attr('group')).closest('tr').show();
        row.hide();
        row.find('*').attr("disabled", true);
    });
    $("button[class=link]").each(function(index, element) {
        var group = $(element).attr('id');
        if ($("tr[hidden][group=" + group + "]").length == 0) {
            console.log(group);
            $("#"+group).closest('tr').hide();
        }
    });
})
