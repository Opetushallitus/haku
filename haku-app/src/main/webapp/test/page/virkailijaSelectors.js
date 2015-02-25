virkailija = initSelectors({
    selectHaku: "#asSelect:first",
    editKoulutusTaustaButton: "a[href*='1.2.246.562.29.173465377510/koulutustausta']:first button",
    addAmmatillinenCheckbox: "input#pohjakoulutus_am",
    addAvoinCheckbox: "input#pohjakoulutus_avoin",
    submitConfirm: "#submit_confirm",
    addSecondAmmatillinenLink: "a#addAmmatillinenRule2-link",
    createApplicationButton: "#create-application",
    ammatilliset: "a[name=pohjakoulutus_am]:first",
    ammatillinenSuoritusVuosi: "input#pohjakoulutus_am_vuosi",
    ammatillinenTutkintonimike: "input#pohjakoulutus_am_nimike",
    ammatillinenTutkinnonLaajuus: "input#pohjakoulutus_am_laajuus",
    ammatillinenOppilaitos: "input#pohjakoulutus_am_oppilaitos",
    ammatillinenNayttotutkinto: "input[name=pohjakoulutus_am_nayttotutkintona]",
    ammatillinenSuoritusVuosi2: "input#pohjakoulutus_am_vuosi2",
    ammatillinenTutkintonimike2: "input#pohjakoulutus_am_nimike2",
    ammatillinenTutkinnonLaajuus2: "input#pohjakoulutus_am_laajuus2",
    ammatillinenOppilaitos2: "input#pohjakoulutus_am_oppilaitos2",
    ammatillinenOppilaitos2Text: "td:contains(SLK)",
    ammatillinenNayttotutkinto2: "input[name=pohjakoulutus_am_nayttotutkintona2]",
    kkTutkintoSuoritettu: function(bool) {
        return "input[name=suoritusoikeus_tai_aiempi_tutkinto][value=" + bool + "]";
    },
    avoinAla: function(index) {
        return "input#pohjakoulutus_avoin_ala" + (index ? index : '');
    },
    avoinKokonaisuus: function(index) {
        return "input#pohjakoulutus_avoin_kokonaisuus" + (index ? index : '');
    },
    avoinLaajuus: function(index) {
        return "input#pohjakoulutus_avoin_laajuus"+ (index ? index : '');
    },
    avoinKorkeakoulu: function(index) {
        return "input#pohjakoulutus_avoin_korkeakoulu" + (index ? index : '');
    },
    addSecondAvoinLink: "a#addAvoinTutkintoRule2-link",
    saveButton: "button.save[value=koulutustausta]:first"
});
