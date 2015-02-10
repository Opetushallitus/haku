function ToinenAsteLomakeLisatietoPage() {

    var pageFunctions = {

        asiointikieli: function(lang) {
            return S("input#asiointikieli[value="+lang+"]").click();
        },

        fromOsaaminen: function() {
            return S("button[class=right][value=lisatiedot][name=phaseId]").first();
        }
    };

    return pageFunctions;
}