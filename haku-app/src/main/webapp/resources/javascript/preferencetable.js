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

            //child lo names
            var $names = $("#childLONames-" + id),
                $targetNames = $("#childLONames-" + targetId),
                nameValue = $names.html(), targetValue = $targetNames.html();

            $targetNames.html(nameValue);
            $names.html(targetValue);
            console.log("|"+targetValue+"|");
            nameValue !== '' ? $("#container-childLONames-" + targetId).show() : $("#container-childLONames-" + targetId).hide();
            targetValue !== '' ? $("#container-childLONames-" + id).show() : $("#container-childLONames-" + id).hide();
        }
    };

	$('button.sort').click(function(event){
	    var id = $(this).data('id'),
	        targetId = $(this).data('target');
	    sortabletable.moveRow(id, targetId);
	});
})();