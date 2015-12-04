var expiringTimeout = setExpiringTimeout();
var expiredTimeout = setExpiredTimeout();

function setExpiringTimeout() {
    return setTimeout(function(){
        $('#overlay-fixed').show();
        $('#timeout-banner-expiring').show();
    }, 20 * 60 * 1000);
}

function setExpiredTimeout() {
    return setTimeout(function(){
        $('#timeout-banner-expiring').hide();
        $('#timeout-banner-expired').show();
    }, 25 * 60 * 1000);
}

function resetTimeouts() {
    clearTimeout(expiringTimeout);
    clearTimeout(expiredTimeout);
    expiringTimeout = setExpiringTimeout();
    expiredTimeout = setExpiredTimeout();
}

function refreshSession(contextPath) {
    $.ajax({
        type: 'POST',
        url: contextPath + '/lomake/session/refresh',
        async: false,
        data: '',
        success: function (data, textStatus, jqXHR) {
            resetTimeouts();
            $('#overlay-fixed').hide();
            $('#timeout-banner-expiring').hide();
        },
        error: function (e, ts, et) {
        }
    });
}
