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
        return "input#onkosinullakaksoiskansallisuus[value=" + onKaksoiskansalaisuus + "]";
    },
    koulusivistyskieli: "select#koulusivistyskieli",

    // Koulutustausta
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
    },
    pohjakoulutusMuu: 'input#pohjakoulutus_muu',
    pohjakoulutusMuuVuosi: 'input#pohjakoulutus_muu_vuosi',
    pohjakoulutusMuuKuvaus: 'textarea#pohjakoulutus_muu_kuvaus',
    suoritusoikeusTaiAiempiTutkinto: function(bool) {
        return "input[type=radio][id=suoritusoikeus_tai_aiempi_tutkinto][value=" + bool + "]";
    },
    koulutusError: function(n) {
        return "#preference" + n + "-Koulutus-error";
    },

    // Hakutoiveet
    opetuspiste: function(n) {
        return "input#preference" + n + "-Opetuspiste";
    },
    koulutus: function(n) {
        return "select#preference" + n + "-Koulutus";
    },
    fromKoulutustausta: "button[class=right][value=hakutoiveet][name=phaseId]:first",
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
    nuoliAlas: function(n) {
        return "button.down[data-id=preference" + n + "]";
    },
    nuoliYlos: function(n) {
        return "button.up[data-id=preference" + n + "]";
    },

    // Osaaminen
    fromHakutoiveet: "button[class=right][value=osaaminen][name=phaseId]:first",

    // Lisätiedot
    fromOsaaminen: "button[class=right][value=lisatiedot][name=phaseId]:first",
    asiointikieli: function(lang) {
        return "input#asiointikieli[value="+lang+"]";
    },

    // Esikatselu
    fromLisatieto: "button[class=right][value=esikatselu][name=phaseId]:first"
});
