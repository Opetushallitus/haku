
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
});