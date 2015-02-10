function ToinenAsteLomakeEsikatseluPage() {

    var pageFunctions = {

        themeAnswersAsMap: function(themeId) {
            var rows;
            S("table#"+themeId+" tr").each(function(tr) {
                var key = $(tr).find("td:first").text();
                var value = $(tr).find("td:last").text();
                rows[key] = value;
            });
            return rows;
        },
        fromLisatieto: function() {
            return S("button[class=right][value=esikatselu][name=phaseId]").first();
        }
    };

    return pageFunctions;
}