function site(jQuery, baseUrl) {

    var load = function (resource, selector) {
        jQuery.get(resource, function (data) {
            jQuery(selector).html(data);
        });
    };

    load(baseUrl + 'header.html', '#global_header');
    load(baseUrl + 'menu.html', '#global_menu');
    load(baseUrl + 'footer.html', '#global_footer');
}

