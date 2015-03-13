virkailija = initSelectors({
    selectHaku: "#asSelect:first",
    hakemusOid: "#_infocell_oid",
    editKoulutusTaustaButton: "a[href*='1.2.246.562.29.173465377510/koulutustausta']:first button",
    addAmmatillinenCheckbox: "input#pohjakoulutus_am",
    addAvoinCheckbox: "input#pohjakoulutus_avoin",
    submitConfirm: "#submit_confirm",
    addSecondAmmatillinenLink: "a#addAmmatillinenRule2-link",
    createApplicationButton: "#create-application",
    ammatillinenSuoritusVuosi: "input#pohjakoulutus_am_vuosi",
    ammatillinenTutkintonimike: "select#pohjakoulutus_am_koodi",
    ammatillinenTutkinnonLaajuus: "input#pohjakoulutus_am_laajuus",
    ammatillinenOppilaitos: "select#pohjakoulutus_am_oppilaitos_koodi",
    ammatillinenNayttotutkinto: "input[name=pohjakoulutus_am_nayttotutkintona]",
    ammatillinenSuoritusVuosi2: "input#pohjakoulutus_am_vuosi2",
    ammatillinenTutkintonimike2: "selecgt#pohjakoulutus_am_nimike2",
    ammatillinenTutkinnonLaajuus2: "input#pohjakoulutus_am_laajuus2",
    ammatillinenOppilaitos2: "select#pohjakoulutus_am_oppilaitos2",
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
    saveKoulutusTaustaButton: "button.save[value=koulutustausta]:first",
    editHakutoiveetButton: "a[href*='1.2.246.562.29.173465377510/hakutoiveet']:first button",
    saveHakutoiveetButton: "button.save[value=hakutoiveet]:first",
    previewLiitteet: "#applicationAttachments tr:gt(0)",
    notes: "#notes"
});
