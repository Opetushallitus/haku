function ToinenAsteLomakeHakutoiveetPage() {

    var pageFunctions = {

        pohjakoulutus : function(koulutus) {
            return S("input#POHJAKOULUTUS[value="+koulutus+"]").prop("checked", true);
        },
        pkPaattotodistusVuosi : function() {
            return S("input#PK_PAATTOTODISTUSVUOSI");
        },
        pkKieli : function() {
            return S("select#perusopetuksen_kieli");
        },
        fromKoulutustausta: function() {
            return S("button[class=right][value=hakutoiveet][name=phaseId]").first();
        }
    };

    return pageFunctions;
}