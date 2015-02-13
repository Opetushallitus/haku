function ToinenAsteLomakeHakutoiveetPage() {
    return initSelectors({
        opetuspiste1: "input#preference1-Opetuspiste",
        koulutus1: "select#preference1-Koulutus",
        faktia: "a.ui-corner-all:contains(FAKTIA, Espoo op)",
        harkinnanvaraisuus1: function(harkinnanvaraisuus) {
            return "input#preference1-discretionary[value=" + harkinnanvaraisuus + "]";
        },
        soraTerveys1: function(value) {
            return "input#preference1_sora_terveys[value=" + value + "]";
        },
        soraOikeudenMenetys1: function(value) {
            return "input#preference1_sora_oikeudenMenetys[value=" + value + "]";
        },
        urheilija1: function(value) {
            return "input#preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys[value=" + value + "]";
        },
        fromKoulutustausta: "button[class=right][value=hakutoiveet][name=phaseId]:first"
    });
}
