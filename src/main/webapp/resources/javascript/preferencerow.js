(function(){
	$('button.reset').click(function(event){
	    var id = $(this).data('id');
        $('[id|="' + id + '"]').val('');
	});

	$(".field-container-text input:text").each(function(index) {
        $(this).autocomplete({
            minLength : 1,
            source : function(request, response) {
                $.getJSON("/haku/fi/education/institute/search", {
                    term : request.term
                }, function(data) {
                    response($.map(data, function(result) {
                        return {
                            label : result.name,
                            value : result.name
                        }
                    }));
                });
            },
            change: function (ev, ui) {
                if (!ui.item) {
                    $(this).val("");
                }
            }
        });
    });
})();