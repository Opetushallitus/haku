(function(){
    var sortabletable = {
        sortUrl : document.URL.split("?")[0] + "/" + sortabletable_settings.elementId,
        moveRow : function(id, targetId) {
            var fData = $("form").serialize(), tmpPrefix = "preferencex";
            fData = fData.split(id).join(tmpPrefix);
            fData = fData.split(targetId).join(id);
            fData = fData.split(tmpPrefix).join(targetId);

            $.post(sortabletable.sortUrl, fData).done(function(data) {
                $("#" + sortabletable_settings.elementId).replaceWith(data);
            });
        }
    };

	$('button.sort').click(function(event){
	    var id = $(this).data('id'),
	        targetId = $(this).data('target');
	    sortabletable.moveRow(id, targetId);
	});
})();
