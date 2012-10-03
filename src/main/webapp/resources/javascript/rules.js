var haku = {
    triggerRule: function(elementId) {
        var container = '.container_' + elementId.id;
        $(container).empty();
        var hiddenInput = '<input type="hidden" id="enabling-submit" name="enabling-submit" value="ok">';
        $(container).append(hiddenInput);
            var input = '#' + elementId.id;
            $(input).on('change', function submit(event) {
            var componentId = '#' + elementId.id;
                $(componentId).closest("form").submit();
            });
    }
};