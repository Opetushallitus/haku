function ToinenAsteLomakeKoulutustaustaPage() {

    var pageFunctions = {

        pohjakoulutus : function(koulutus) {
            S("input#POHJAKOULUTUS[value="+koulutus+"]").prop("checked", true);
        },
        pkPaattotodistusVuosi : function() {
            return S("input#PK_PAATTOTODISTUSVUOSI");
        },
        pkKieli : function() {
            return S("select#perusopetuksen_kieli");
        },
        fromHenkilotiedot: function() {
            return S("button[class=right][value=koulutustausta][name=phaseId]").first();
        }
    };

    return pageFunctions;
}