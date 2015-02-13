function ToinenAsteLomakeLisatietoPage() {
    var selectors = initSelectors({
        fromOsaaminen: "button[class=right][value=lisatiedot][name=phaseId]:first"
    });

    selectors.asiointikieli = function(lang) {
        return S("input#asiointikieli[value="+lang+"]").click();
    };

    return selectors;
}
