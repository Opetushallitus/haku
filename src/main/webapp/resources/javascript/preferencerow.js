(function(){
	$('button.reset').click(function(event){
	    var id = $(this).data('id');
        $('[id|="' + id + '"]').val('');
	});
})();