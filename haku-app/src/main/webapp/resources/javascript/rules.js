var haku = {
    triggerRule: function (element) {
        var elem = $('#' + element.id);
        elem.on('change', function submit(event) {
            elem.closest("form").submit();
        });
    }
};
