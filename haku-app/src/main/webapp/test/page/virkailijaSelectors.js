virkailija = initSelectors({
    selectHaku: "#asSelect:first",
    hakemusOid: "#_infocell_oid",
    addAmmatillinenCheckbox: "input#pohjakoulutus_am",
    addUlkomainenKkKelpoisuus: "input#pohjakoulutus_ulk",
    addYksilollistettyCheckbox: "input#POHJAKOULUTUS_6",
    addAvoinCheckbox: "input#pohjakoulutus_avoin",
    submitConfirm: "#submit_confirm",
    addSecondAmmatillinenLink: "a#addAmmatillinenRule2-link",
    createApplicationButton: "#create-application",
    hakukausi: "#hakukausi",
    hakukausiVuosi: "#hakukausiVuosi",
    searchSelectHaku: "select#application-system",
    searchHarkinnanvaraiset: "#discretionary-only",
    searchMaksuntilaSelect: "#payment-state-select",
    searchMaksuntila: "#payment-state",
    searchMaksuntilaSelected: "#payment-state option:selected",
    searchReset: "#reset-search",
    ammatillinenSuoritusVuosi: "input#pohjakoulutus_am_vuosi",
    ammatillinenTutkintonimike: "select#pohjakoulutus_am_nimike",
    ammatillinenTutkinnonLaajuus: "input#pohjakoulutus_am_laajuus",
    ammatillinenOppilaitos: "select#pohjakoulutus_am_oppilaitos",
    ammatillinenNayttotutkinto: "input[name=pohjakoulutus_am_nayttotutkintona]",
    ammatillinenSuoritusVuosi2: "input#pohjakoulutus_am_vuosi2",
    ammatillinenTutkintonimike2: "selecgt#pohjakoulutus_am_nimike2",
    ammatillinenTutkinnonLaajuus2: "input#pohjakoulutus_am_laajuus2",
    ammatillinenOppilaitos2: "select#pohjakoulutus_am_oppilaitos2",
    ammatillinenOppilaitos2Text: "td:contains(SLK)",
    ammatillinenNayttotutkinto2: "input[name=pohjakoulutus_am_nayttotutkintona2]",
    ulkomainenKkKelpoisuusVuosi: "input#pohjakoulutus_ulk_vuosi",
    ulkomainenKkKelpoisuusNimike: "input#pohjakoulutus_ulk_nimike",
    ulkomainenKkKelpoisuusOppilaitos: "input#pohjakoulutus_ulk_oppilaitos",
    ulkomainenKkKelpoisuusMaa: "select#pohjakoulutus_ulk_suoritusmaa",
    ulkomainenKkKelpoisuusMuuMaa: "input#pohjakoulutus_ulk_suoritusmaa_muu",
    preference1KoulutusId: "input#preference1-Koulutus-id",
    koulutustaustaPaymentNotification: "#koulutustausta_payment_notification",
    pohjakoulutusYo: "#pohjakoulutus_yo",
    pohjakoulutusYoVuosi: "#pohjakoulutus_yo_vuosi",
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
    editVaiheButton: function(hakuOid, vaihe) {
        return "a[href*='" + hakuOid + "/" + vaihe + "']:first button";
    },
    saveVaiheButton: function(vaihe) {
        return "button.save[value=" + vaihe + "]:first";
    },
    previewLiitteet: "#applicationAttachments tr:gt(0)",
    notes: "#notes"
});
