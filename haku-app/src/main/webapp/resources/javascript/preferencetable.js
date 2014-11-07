(function(){
    var sortabletable = {
        sortUrl : document.URL.split("?")[0] + "/" + sortabletable_settings.elementId,

        moveRow : function(id, targetId) {
            var fData = $("form").serialize();
            var tmpPrefix = "preferencex";
            fData = fData.split(id).join(tmpPrefix);
            fData = fData.split(targetId).join(id);
            fData = fData.split(tmpPrefix).join(targetId);

            $.post(sortabletable.sortUrl, fData).done(function(data) {
                $("#" + sortabletable_settings.elementId).replaceWith(data);
            });
        },

        addRow : function() {
            var preferencesVisible = parseInt($('#preferencesVisible').val(), 10) + 1;
            $('#preferencesVisible').val(preferencesVisible);
            var fData = $("form") .serialize();

            $.post(sortabletable.sortUrl, fData).done(function(data) {
                $("#" + sortabletable_settings.elementId).replaceWith(data);
            });
        }
    };

    $('#add-preference').click(function(event) {
        sortabletable.addRow();
    });

	$('button.sort').click(function(event){
	    var id = $(this).data('id');
	    var targetId = $(this).data('target');
	    sortabletable.moveRow(id, targetId);
	});
})();
