lomake = initSelectors({
    // General
    autocomplete: function(text) {
        return "a.ui-corner-all:contains(" + text + ")";
    },
    overlay: "div#overlay-fixed",

    // Henkilötiedot
    sukunimi: "input#Sukunimi",
    etunimet: "input#Etunimet",
    kutsumanimi: "input#Kutsumanimi",
    hetu: "input#Henkilotunnus",
    sukupuoli: "input#sukupuoli",
    sukupuoliLabel: "span#sex",
    Sähköposti: "input#Sähköposti",
    asuinmaa: "select#asuinmaa",
    lahiosoite: "input#lahiosoite",
    postinumero: "input#Postinumero",
    kotikunta: "select#kotikunta",
    kansalaisuus: "select#kansalaisuus",
    aidinkieli: "select#aidinkieli",
    kaksoiskansalaisuus: function(onKaksoiskansalaisuus) {
        return "input#onkosinullakaksoiskansallisuus_"+onKaksoiskansalaisuus;
    },
    suomalainenHetu: function(onKaksoiskansalaisuus) {
        return "input#onkoSinullaSuomalainenHetu_"+onKaksoiskansalaisuus;
    },
    koulusivistyskieli: "select#koulusivistyskieli",

    // Koulutustausta
    pkPaattotodistusVuosi : "input#PK_PAATTOTODISTUSVUOSI",
    pkPaattotodistusSaatuPuolenVuodenSisaan : function(bool) {
        return "input#peruskoulutodistus_saatu_puolivuotta_haun_lopusta_" + bool;
    },
    pkKieli : "select#perusopetuksen_kieli",
    fromHenkilotiedot: "button[class=right][value=koulutustausta][name=phaseId]:last",
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
    pohjakoulutusYoTutkinto: 'select#pohjakoulutus_yo_tutkinto',
    pohjakoulutusYoAmmatillinen: 'input#pohjakoulutus_yo_ammatillinen',
    pohjakoulutusYoAmmatillinenVuosi: 'input#pohjakoulutus_yo_ammatillinen_vuosi',
    pohjakoulutusYoAmmatillinenNimike: 'select#pohjakoulutus_yo_ammatillinen_nimike',
    pohjakoulutusYoAmmatillinenLaajuus: 'input#pohjakoulutus_yo_ammatillinen_laajuus',
    enOleSuorittanutYoAmmatillistaTutkintoa: "#toisen_asteen_suoritus_false_label",
    pohjakoulutusYoAmmatillinenOppilaitos: 'select#pohjakoulutus_yo_ammatillinen_oppilaitos',

    pohjakoulutusYoUlkomainen: 'input#pohjakoulutus_yo_ulkomainen',
    pohjakoulutusYoUlkomainenVuosi: 'input#pohjakoulutus_yo_ulkomainen_vuosi',
    pohjakoulutusYoUlkomainenTutkinto: 'select#pohjakoulutus_yo_ulkomainen_tutkinto',
    pohjakoulutusYoUlkomainenMaa: 'select#pohjakoulutus_yo_ulkomainen_maa',
    pohjakoulutusYoUlkomainenMaaMuu: 'input#pohjakoulutus_yo_ulkomainen_maa_muu',

    pohjakoulutusKKUlk: 'input#pohjakoulutus_kk_ulk',
    pohjakoulutusKKUlkTaso: function(n) {
        return 'select#pohjakoulutus_kk_ulk_taso' + (n === 1 ? '' : n);
    },
    pohjakoulutusKKUlkPvm: function(n) {
        return 'input#pohjakoulutus_kk_ulk_pvm' + (n === 1 ? '' : n);
    },
    pohjakoulutusKKUlkTutkinto: function(n) {
        return 'input#pohjakoulutus_kk_ulk_nimike' + (n === 1 ? '' : n);
    },
    pohjakoulutusKKUlkOppilaitos: function(n) {
        return 'input#pohjakoulutus_kk_ulk_oppilaitos' + (n === 1 ? '' : n);
    },
    pohjakoulutusKKUlkMaa: function(n) {
        return 'select#pohjakoulutus_kk_ulk_maa' + (n === 1 ? '' : n);
    },
    pohjakoulutusKKUlkMaaMuu: function(n) {
        return 'input#pohjakoulutus_kk_ulk_maa_muu' + (n === 1 ? '' : n);
    },

    pohjakoulutusUlk: 'input#pohjakoulutus_ulk',
    pohjakoulutusUlkVuosi: function(n) {
        return 'input#pohjakoulutus_ulk_vuosi' + (n === 1 ? '' : n);
    },
    pohjakoulutusUlkTutkinto: function(n) {
        return 'input#pohjakoulutus_ulk_nimike' + (n === 1 ? '' : n);
    },
    pohjakoulutusUlkOppilaitos: function(n) {
        return 'input#pohjakoulutus_ulk_oppilaitos' + (n === 1 ? '' : n);
    },
    pohjakoulutusUlkSuoritusmaa: function(n) {
        return 'select#pohjakoulutus_ulk_suoritusmaa' + (n === 1 ? '' : n);
    },
    pohjakoulutusUlkSuoritusmaaMuu: function(n) {
        return 'input#pohjakoulutus_ulk_suoritusmaa_muu' + (n === 1 ? '' : n);
    },

    lisakoulutusKymppi: 'input#LISAKOULUTUS_KYMPPI',
    lisakoulutusKymppiYear: 'input#KYMPPI_PAATTOTODISTUSVUOSI',
    lisakoulutusValma: 'input#LISAKOULUTUS_VALMA',
    kiinnostunutOppisopimuksesta: 'input#kiinnostunutoppisopimuksesta',

    pohjakoulutusMuu: 'input#pohjakoulutus_muu',
    pohjakoulutusMuuVuosi: 'input#pohjakoulutus_muu_vuosi',
    pohjakoulutusMuuKuvaus: 'textarea#pohjakoulutus_muu_kuvaus',
    muukoulutus: 'textarea#muukoulutus',
    suoritusoikeusTaiAiempiTutkinto: function(bool) {
        return "input#suoritusoikeus_tai_aiempi_tutkinto_" + bool;
    },
    koulutusError: function(n) {
        return "#preference" + n + "-Koulutus-error";
    },

    // Hakutoiveet
    noPreferencesText: "div#nogradegrid",
    preferencesVisibleInput: "input#preferencesVisible",
    sortDown: function(n) {
        return "button.down.sort[data-id=preference" + n + "]";
    },
    sortUp: function(n) {
        return "button.up.sort[data-id=preference" + n + "]";
    },
    opetuspiste: function(n) {
        return "input#preference" + n + "-Opetuspiste";
    },
    opetuspisteDropdown: function(n) {
        return "select#preference" + n + "-Opetuspiste";
    },
    koulutus: function(n) {
        return "select#preference" + n + "-Koulutus";
    },
    fromKoulutustausta: "button[class=right][value=hakutoiveet][name=phaseId]:last",
    harkinnanvaraisuus: function(n, harkinnanvaraisuus) {
        return "input#preference" + n + "-discretionary_" + harkinnanvaraisuus;
    },
    soraTerveys: function(n, value) {
        return "input#preference"+ n + "_sora_terveys_" + value;
    },
    soraOikeudenMenetys: function(n, value) {
        return "input#preference" + n + "_sora_oikeudenMenetys_" + value;
    },
    urheilija: function(n, value) {
        return "input#preference" + n + "_urheilijan_ammatillisen_koulutuksen_lisakysymys_" + value;
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
    fromHakutoiveet: "button[class=right][value=osaaminen][name=phaseId]:last",
    keskiarvoTutkinto: function(suffix) {
        return "textarea[name=keskiarvo-tutkinto" + (suffix == 1 ? "" : suffix) + "]";
    },
    keskiarvo: function(suffix) {
        return '[name=keskiarvo' + (suffix == 1 ? "" : suffix) + ']';
    },
    asteikko: function(suffix) {
        return 'select#arvosanaasteikko' + (suffix === 1 ? '' : suffix);
    },

    // Lisätiedot
    fromOsaaminen: "button[class=right][value=lisatiedot][name=phaseId]:last",
    lupatiedotSahkoinenViestinta: function(value) {
        return "input#lupatiedot-sahkoinen-viestinta_"+value;
    },
    asiointikieli: function(lang) {
        return "input#asiointikieli_"+lang;
    },
    hojks: function(value) {
        return "input#hojks_" + value;
    },
    koulutuskokeilu: function(value) {
        return "input#koulutuskokeilu_" + value;
    },
    miksi_ammatilliseen: 'textarea#miksi_ammatilliseen',

    // Esikatselu
    fromLisatieto: "button[class=right][value=esikatselu][name=phaseId]:last"
});
