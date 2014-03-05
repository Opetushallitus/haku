
$(document).ready(function() {

    $('div.tabs .tab').click(function() {
        var tabGroup = $(this).attr('data-tabs-group');
        var tabId = $(this).attr('data-tabs-id');
        $('div.tabs a[data-tabs-group="'+tabGroup+'"]').removeClass('current');
        $('div.tabsheets section[data-tabs-group="'+tabGroup+'"]').hide();
        $('div.tabs a[data-tabs-id="'+tabId+'"]').addClass('current');
        $('div.tabsheets section[data-tabs-id="'+tabId+'"]').show();

    });

});