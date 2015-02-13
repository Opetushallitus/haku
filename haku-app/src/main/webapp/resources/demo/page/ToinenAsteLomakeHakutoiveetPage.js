function ToinenAsteLomakeHakutoiveetPage() {

    var pageFunctions = {

        opetuspiste1: function() {
            return S("input#preference1-Opetuspiste");
        },
        koulutus1: function() {
            return S("select#preference1-Koulutus");
        },
        faktia: function() {
            return S("a.ui-corner-all:contains(FAKTIA, Espoo op)");
        },
        harkinnanvaraisuus1: function(harkinnanvaraisuus) {
            return function() {
                return S("input#preference1-discretionary[value="+harkinnanvaraisuus+"]");
            }
        },
        soraTerveys1: function(value) {
            return function() {
                return S("input#preference1_sora_terveys[value="+value+"]");
            }
        },
        soraOikeudenMenetys1: function(value) {
            return function() {
                return S("input#preference1_sora_oikeudenMenetys[value="+value+"]");
            }
        },
        urheilija1: function(value) {
            return function() {
                return S("input#preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys[value=" + value + "]");
            }
        },
        fromKoulutustausta: function() {
            return S("button[class=right][value=hakutoiveet][name=phaseId]").first();
        }
    };

    return pageFunctions;
}
