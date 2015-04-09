lomake = initSelectors({
    // General
    autocomplete: function(text) {
        return "a.ui-corner-all:contains(" + text + ")";
    },

    // Henkilötiedot
    sukunimi: "input#Sukunimi",
    etunimet: "input#Etunimet",
    kutsumanimi: "input#Kutsumanimi",
    hetu: "input#Henkilotunnus",
    sukupuoli: "input#sukupuoli",
    sukupuoliLabel: "span#sex",
    asuinmaa: "select#asuinmaa",
    lahiosoite: "input#lahiosoite",
    postinumero: "input#Postinumero",
    kotikunta: "select#kotikunta",
    kaksoiskansalaisuus: function(onKaksoiskansalaisuus) {
        return "input#onkosinullakaksoiskansallisuus_"+onKaksoiskansalaisuus;
    },
    koulusivistyskieli: "select#koulusivistyskieli",

    // Koulutustausta
    pkPaattotodistusVuosi : "input#PK_PAATTOTODISTUSVUOSI",
    pkKieli : "select#perusopetuksen_kieli",
    fromHenkilotiedot: "button[class=right][value=koulutustausta][name=phaseId]:first",
    suorittanutTutkinnonRule: '#suorittanutTutkinnonRule',
    warning: '.notification.warning',
    lukioPaattotodistusVuosi: 'input#lukioPaattotodistusVuosi',
    lukioPaattotodistusKeskiarvo: 'input#lukion-paattotodistuksen-keskiarvo',
    lukionKieli: "select#lukion_kieli",
    ammatillinenKoulutuspaikka: function(bool) {
        return 'input#KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON_'+ bool;
    },
    ammatillinenSuoritettu: function(bool) {
        return 'input#ammatillinenTutkintoSuoritettu_' + bool;
    },
    pohjakoulutus : function(koulutus) {
        return "input#POHJAKOULUTUS_"+koulutus;
    },
    lisaaUusiAmmatillinenPohjakoulutus: function(n) {
        return "a#addAmmatillinenRule" + n + "-link";
    },
    pohjakoulutusAm: 'input#pohjakoulutus_am',
    pohjakoulutusAmVuosi: function(n) {
        return 'input#pohjakoulutus_am_vuosi' + (n == 1 ? "" : n);
    },
    pohjakoulutusAmNimike: function(n) {
        return 'select#pohjakoulutus_am_nimike' + (n == 1 ? "" : n);
    },
    pohjakoulutusAmLaajuus: function(n) {
        return 'input#pohjakoulutus_am_laajuus' + (n == 1 ? "" : n);
    },
    pohjakoulutusAmOppilaitos: function(n) {
        return 'select#pohjakoulutus_am_oppilaitos' + (n == 1 ? "" : n);
    },
    pohjakoulutusAmNayttotutkintona: function(n, bool) {
        return 'input#pohjakoulutus_am_nayttotutkintona' + (n == 1 ? "" : n) + '_' + bool;
    },
    pohjakoulutusYo: 'input#pohjakoulutus_yo',
    pohjakoulutusYoVuosi: 'input#pohjakoulutus_yo_vuosi',
    pohjakoulutusYoAmmatillinen: 'input#pohjakoulutus_yo_ammatillinen',
    pohjakoulutusYoAmmatillinenVuosi: 'input#pohjakoulutus_yo_ammatillinen_vuosi',
    pohjakoulutusYoAmmatillinenNimike: 'select#pohjakoulutus_yo_ammatillinen_nimike',
    pohjakoulutusYoAmmatillinenLaajuus: 'input#pohjakoulutus_yo_ammatillinen_laajuus',
    pohjakoulutusYoAmmatillinenOppilaitos: 'select#pohjakoulutus_yo_ammatillinen_oppilaitos',
    pohjakoulutusMuu: 'input#pohjakoulutus_muu',
    pohjakoulutusMuuVuosi: 'input#pohjakoulutus_muu_vuosi',
    pohjakoulutusMuuKuvaus: 'textarea#pohjakoulutus_muu_kuvaus',
    suoritusoikeusTaiAiempiTutkinto: function(bool) {
        return "input#suoritusoikeus_tai_aiempi_tutkinto_" + bool;
    },
    koulutusError: function(n) {
        return "#preference" + n + "-Koulutus-error";
    },

    // Hakutoiveet
    preferencesVisibleInput: "input#preferencesVisible",
    opetuspiste: function(n) {
        return "input#preference" + n + "-Opetuspiste";
    },
    koulutus: function(n) {
        return "select#preference" + n + "-Koulutus";
    },
    fromKoulutustausta: "button[class=right][value=hakutoiveet][name=phaseId]:first",
    harkinnanvaraisuus1: function(harkinnanvaraisuus) {
        return "input#preference1-discretionary_" + harkinnanvaraisuus;
    },
    soraTerveys1: function(value) {
        return "input#preference1_sora_terveys_" + value;
    },
    soraOikeudenMenetys1: function(value) {
        return "input#preference1_sora_oikeudenMenetys_" + value;
    },
    urheilija1: function(value) {
        return "input#preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys_" + value;
    },
    nuoliAlas: function(n) {
        return "button.down[data-id=preference" + n + "]";
    },
    nuoliYlos: function(n) {
        return "button.up[data-id=preference" + n + "]";
    },
    tyhjenna: function(n) {
        return "button#preference" + n + "-reset";
    },

    // Osaaminen
    fromHakutoiveet: "button[class=right][value=osaaminen][name=phaseId]:first",
    keskiarvoTutkinto: function(suffix) {
        return "textarea[name=keskiarvo-tutkinto" + (suffix == 1 ? "" : suffix) + "]";
    },
    keskiarvo: function(suffix) {
        return '[name=keskiarvo' + (suffix == 1 ? "" : suffix) + ']';
    },

    // Lisätiedot
    fromOsaaminen: "button[class=right][value=lisatiedot][name=phaseId]:first",
    asiointikieli: function(lang) {
        return "input#asiointikieli_"+lang;
    },

    // Esikatselu
    fromLisatieto: "button[class=right][value=esikatselu][name=phaseId]:first"
});
