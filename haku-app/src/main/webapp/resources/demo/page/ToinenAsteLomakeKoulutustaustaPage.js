function ToinenAsteLomakeKoulutustaustaPage() {

    var pageFunctions = {

        pohjakoulutus : function(koulutus) {
            return function() {
                return S("input[name=POHJAKOULUTUS][value="+koulutus+"]");
            }
        },
        pkPaattotodistusVuosi : function() {
            return S("input#PK_PAATTOTODISTUSVUOSI");
        },
        pkKieli : function() {
            return S("select#perusopetuksen_kieli");
        },
        fromHenkilotiedot: function() {
            return S("button[class=right][value=koulutustausta][name=phaseId]").first();
        },
        ammatillinenKoulutuspaikka: function(bool) {
            return function() {
                return S('input[name=KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON][value=' + bool + ']');
            }
        },
        ammatillinenSuoritettu: function(bool) {
            return function() {
                return S('input[name=ammatillinenTutkintoSuoritettu][value=' + bool + ']');
            }
        },
        suorittanutTutkinnonRule: function() {
            return S('#suorittanutTutkinnonRule');
        },
        warning: function() {
            return S('.notification.warning');
        },
        lukioPaattotodistusVuosi: function() {
            return S('input#lukioPaattotodistusVuosi');
        },
        lukionKieli: function() {
            return S("select#lukion_kieli");
        }
    };

    return pageFunctions;
}
