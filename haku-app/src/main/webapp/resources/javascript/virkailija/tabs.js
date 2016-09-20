
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
        console.log("lazy getting valintaTab");
        var url = $('#valintaContent').data("url");
        if(url) {
            console.log("getting " + url);
            $('#valintaContent').load(url);
        } else {
            console.log("already loading content!")
        }
        $('#valintaContent').data("url", null);
    });
});