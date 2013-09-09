(function(){
    var educationId = $("#" + singlePreference_settings.elementId + "-Koulutus-id").val();
    if (educationId && educationId !== '') {
        $.getJSON(singlePreference_settings.koulutusinformaatioBaseUrl + '/ao/' + educationId, function(data) {
            var $names =  $("#childLONames"), htmlData = '<ol class="list-style-none">', childRefs = data.childRefs;
            for (var index in childRefs) {
                htmlData = htmlData.concat("<li><small>", childRefs[index].name, "</small></li>");
            }
            htmlData = htmlData.concat("</ol>");
            $names.append(htmlData);
            $("#container-childLONames").show();
        });
    }
})();