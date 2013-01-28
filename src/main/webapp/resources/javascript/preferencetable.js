(function(){
    var sortabletable = {
        moveRow : function(id, targetId) {
            var $clone,
                $target,
                $targetClone;
            $('[id|="' + id + '"]').each(function(index) {
               $clone = $(this).clone();
               $target =  $('[id|="' + targetId + '"]').eq(index);
               $targetClone = $target.clone();
               $(this).val($targetClone.val()).empty().append($targetClone.children());
               $target.val($clone.val()).empty().append($clone.children());
            });
        }
    };

	$('button.sort').click(function(event){
	    var id = $(this).data('id'),
	        targetId = $(this).data('target');
	    sortabletable.moveRow(id, targetId);
	});
})();