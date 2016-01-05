$(document).ready(function () {
    setNamesForNoteUsers();
});

function setNamesForNoteUsers() {
    $("[id^=note-user]").each(function() {
        $(this).text(getNameForNoteUser($(this).attr("user")));
    });
}

function getNameForNoteUser(user) {
    $.ajax({
        type: 'GET',
        url: contextPath + '/virkailija/hakemus/note/user/' + user + "/name",
        async: false,
        data: '',
        success: function (data, textStatus, jqXHR) {
            return data;
        },
        error: function (e, ts, et) {
            return e;
        }
    });
    
}
