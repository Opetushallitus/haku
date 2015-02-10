function ToinenAsteLomakeOsaaminenPage() {

    var pageFunctions = {

        fromHakutoiveet: function() {
            return S("button[class=right][value=osaaminen][name=phaseId]").first();
        }
    };

    return pageFunctions;
}