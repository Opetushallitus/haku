$(document).ready(function () {
    setNamesForNoteUsers();
});

function setNamesForNoteUsers() {
    var personOids = getListOfPersonOids();
    var personNames = getPersonNames(personOids);
    setPersonNames(personNames);
}

function getListOfPersonOids() {
    var personOids = [];
    $(".note-user").each(function() {
        var oid = $(this).text();
        if(personOids.indexOf(oid) === -1) {
            personOids.push(oid);
        }
    });
    return personOids;
}

function getPersonNames(personOids) {
    var personNames = [];
    $.ajax({
        type: 'POST',
        url: contextPath + '/virkailija/hakemus/note/users/',
        async: false,
        data: JSON.stringify(personOids),
        contentType: "application/json",
        success: function (data, textStatus, jqXHR) {
            personNames = data;
        },
        error: function (e, ts, et) {
        }
    });
    return personNames;
}

function setPersonNames(personNames) {
    $(".note-user").each(function() {
        var oid = $(this).text();
        if(personNames[oid]) {
            $(this).text(personNames[oid])
        }
    });
}