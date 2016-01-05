$(document).ready(function () {
    setNamesForNoteUsers();
});

function setNamesForNoteUsers() {
    $("[id^=note-user]").each(function() {
        $(this).text(getNameForNoteUser($(this).attr("user")));
    });
}

function getNameForNoteUser(user) {
    var name = user;
    $.ajax({
        type: 'GET',
        url: contextPath + '/virkailija/hakemus/note/user/' + user + "/name",
        async: false,
        data: '',
        success: function (data, textStatus, jqXHR) {
            name = data;
        },
        error: function (e, ts, et) {
        }
    });
    return name;
}
