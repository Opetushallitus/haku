(function(){
    var sortabletable = {
        moveRow : function(id, targetId) {
            var value,
                html,
                target;
            $('[id|="' + id + '"]').each(function(index) {
               value = $(this).val();
               html = $(this).html();
               $target =  $('[id|="' + targetId + '"]').eq(index);
               $(this).val($target.val()).html($target.html());
               $target.val(value).html(html);
            });
        }
    };

	$('button.sort').click(function(event){
	    var id = $(this).data('id'),
	        targetId = $(this).data('target');
	    sortabletable.moveRow(id, targetId);
	});
})();