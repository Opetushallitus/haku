const WARNING_TIMEOUT_IN_MINUTES = 5;

var g_timeoutCounter = getTimeoutCounter();
var g_expiringTimeout = setExpiringTimeout(g_timeoutCounter);
var g_expiredTimeout = setExpiredTimeout(g_timeoutCounter);

$(document).ajaxComplete(function() { resetTimeouts() } );

function setExpiringTimeout() {
    return setTimeout(function(){
        showTimeoutExpiringBanner();
    }, g_timeoutCounter - (WARNING_TIMEOUT_IN_MINUTES * 60 * 1000));
}

function setExpiredTimeout() {
    return setTimeout(function(){
        showTimeoutExpiredBanner();
    }, g_timeoutCounter);
}

function showTimeoutExpiringBanner() {
    $('#overlay-fixed').show();
    $('#timeout-banner-expiring').show();
}

function showTimeoutExpiredBanner() {
    $('#timeout-banner-expiring').hide();
    $('#timeout-banner-expired').show();
}

function hideTimeoutExpiringBanner() {
    $('#overlay-fixed').hide();
    $('#timeout-banner-expiring').hide();
}

function resetTimeouts() {
    clearTimeout(g_expiringTimeout);
    clearTimeout(g_expiredTimeout);
    g_timeoutCounter = getTimeoutCounter();
    g_expiringTimeout = setExpiringTimeout();
    g_expiredTimeout = setExpiredTimeout();
}

function getTimeoutCounter() {
    return getCookie('sessionExpiresInMillis', 0);
}

function getCookie(cookieName, defaultValue) {
    var name = cookieName + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
    }
    return defaultValue;
}

function refreshSession() {
    $.ajax({
        type: 'POST',
        url: window.url("haku-app.session.refresh"),
        async: false,
        data: '',
        success: function (data, textStatus, jqXHR) {
            hideTimeoutExpiringBanner();
        },
        error: function (e, ts, et) {
        }
    });
}
