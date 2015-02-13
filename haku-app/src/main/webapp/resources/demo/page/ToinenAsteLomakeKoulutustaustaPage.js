function ToinenAsteLomakeKoulutustaustaPage() {
    return initSelectors({
        pkPaattotodistusVuosi : "input#PK_PAATTOTODISTUSVUOSI",
        pkKieli : "select#perusopetuksen_kieli",
        fromHenkilotiedot: "button[class=right][value=koulutustausta][name=phaseId]:first",
        suorittanutTutkinnonRule: '#suorittanutTutkinnonRule',
        warning: '.notification.warning',
        lukioPaattotodistusVuosi: 'input#lukioPaattotodistusVuosi',
        lukionKieli: "select#lukion_kieli",
        ammatillinenKoulutuspaikka: function(bool) {
            return 'input[name=KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON][value=' + bool + ']';
        },
        ammatillinenSuoritettu: function(bool) {
            return 'input[name=ammatillinenTutkintoSuoritettu][value=' + bool + ']';
        },
        pohjakoulutus : function(koulutus) {
            return "input[name=POHJAKOULUTUS][value="+koulutus+"]";
        }
    });
}
