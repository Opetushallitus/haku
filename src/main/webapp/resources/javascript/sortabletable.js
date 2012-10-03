(function(){
    var sortabletable = {
        moveRow : function(id, targetId) {
            var value,
                target;
            $('[id|="' + id + '"]').each(function(index) {
               value = $(this).val();
               target =  $('[id|="' + targetId + '"]').eq(index);
               $(this).val($(target).val());
               $(target).val(value);
            });
        }
    };

	$('button.sort').click(function(event){
	    var id = $(this).data('id'),
	        targetId = $(this).data('target');
	    sortabletable.moveRow(id, targetId);
	});
})();