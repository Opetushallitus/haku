
var loadKelpoisuusJaLiitteetContent = function() {
    var url = $('#kelpoisuusLiitteetContent').data("url");
    var isLoading = $('#kelpoisuusLiitteetContent').data("loading");
    if(isLoading) {
        console.log("already loading tab..");
    } else {
        var load = function reload () {
            $('#kelpoisuusLiitteetContent').html("");
            $('#kelpoisuusLiitteetContentLoaderIcon').show();
            console.log('--:--' + hakutoiveet);
            console.log('--:--' + hakutoiveetCache);
            hakutoiveet = [];
            hakutoiveetCache = [];
            $('#kelpoisuusLiitteetContent').load(url, function( response, status, xhr ) {
                $('#kelpoisuusLiitteetContentLoaderIcon').hide();
                $('#kelpoisuusLiitteetContent').data("loading", false);
                if ( status == "error" ) {
                    $('#kelpoisuusLiitteetContent')
                    var retry = $("<a href='#'>Välilehden lataus epäonnistui. Yritä välilehden hakua uudelleen.</a>").click(function(e) {
                        reload();
                    });
                    $('#kelpoisuusLiitteetContent').html("");
                    $('#kelpoisuusLiitteetContent').append(retry);
                } else {
                    kjal.populateForm();
                    /**
                     * kelpoisuus ja liitteet välilehden tallennuksen jälkeen
                     * asetataan kelpoisuus ja liitteet välilehti takaisin aktiiviseksi
                     */
                    if (window.location.href.split('#')[1] === 'liitteetkelpoisuusTab' ) {
                        var navY = window.location.href.split('#')[2];
                        window.location.href = window.location.href.split('#')[0]+'#';
                        $('#kelpoisuusliitteetTab').click();
                        window.setTimeout(function (){scrollTo(0,navY);},10);
                    }
                }
            });
        }
        load();
        $('#kelpoisuusLiitteetContent').data("loading", true);
    }
}

$(document).ready(function() {

    $('div.tabs .tab').click(function() {
        console.log("opening tab");
        var tabGroup = $(this).attr('data-tabs-group');
        var tabId = $(this).attr('data-tabs-id');
        $('div.tabs a[data-tabs-group="'+tabGroup+'"]').removeClass('current');
        $('div.tabsheets section[data-tabs-group="'+tabGroup+'"]').hide();
        $('div.tabs a[data-tabs-id="'+tabId+'"]').addClass('current');
        $('div.tabsheets section[data-tabs-id="'+tabId+'"]').show();
    });

    $('#valintaTab').click(function() {
        var url = $('#valintaContent').data("url");
        if(url) {
            var load = function reload () {
                $('#valintaContent').load(url, function( response, status, xhr ) {
                    $('#valintaContentLoaderIcon').hide();
                    if ( status == "error" ) {
                        $('#valintaContent')
                        var retry = $("<a href='#'>Välilehden lataus epäonnistui. Yritä välilehden hakua uudelleen.</a>").click(function(e) {
                            $('#valintaContent').html("");
                            $('#valintaContentLoaderIcon').show();
                            reload();
                        });
                        $('#valintaContent').html("");
                        $('#valintaContent').append(retry);
                    }
                });
            }
            load();
        }
        $('#valintaContent').data("url", null);
    });
    $('#kelpoisuusliitteetTab').click(function() {
        loadKelpoisuusJaLiitteetContent();
    });
});