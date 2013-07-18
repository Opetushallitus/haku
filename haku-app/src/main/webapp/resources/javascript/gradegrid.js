$(document).ready(function () {
    // hide and disable closest tr
    $('#gradegrid-table').on('click', 'a.btn-remove', function () {
        var row = $(this).closest('tr');
        $("#" + row.attr('group')).closest('tr').show();
        row.hide();
        row.find('*').attr("disabled", true);
        $("option[value=" + row.attr('id') + "]").show();
        var select = $("#" + row.attr('group') + "-add-lang-select");
        select.children().remove();
        $("tr[group='" + row.attr('group') + "']:hidden").each(
            function (index, item) {
                var option = $('<option></option>');
                option.html($(item).children('td:first').text());
                option.val($(item).attr('id'));
                select.append(option);
            }
        )

    });
    $("button[class=link]").each(function (index, element) {
        var group = $(element).attr('id');
        if ($("tr[hidden][group=" + group + "]").length == 0) {
            $("#" + group).closest('tr').hide();
        }
    });
})
