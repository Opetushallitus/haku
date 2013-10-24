$(document).ready(function () {
    // hide and disable closest tr
    $('#gradegrid-table').on('click', 'a.btn-remove', function () {
        var row = $(this).closest('tr');
        $("#" + row.attr('data-group')).closest('tr').show();
        row.hide();
        row.find('*').attr("disabled", true);
        $("option[value=" + row.attr('id') + "]").show();
        var select = $("#" + row.attr('data-group') + "-add-lang-select");
        select.children().remove();
        $("tr[data-group='" + row.attr('data-group') + "']:hidden").each(
            function (index, item) {
                var option = $('<option>&nbsp;</option>');
                option.html($(item).children('td:first').text());
                option.val($(item).attr('id'));
                select.append(option);
            }
        )
        return false;
    });
    $("button[class=link]").each(function (index, element) {
        var group = $(element).attr('id');
        if ($("tr[hidden][data-group=" + group + "]").length == 0) {
            $("#" + group).closest('tr').hide();
        }
    });
})
