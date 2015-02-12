function ToinenAsteLomakeEsikatseluPage() {

    var pageFunctions = {

        fromLisatieto: function() {
            return S("button[class=right][value=esikatselu][name=phaseId]").first();
        }
    };

    return pageFunctions;
}