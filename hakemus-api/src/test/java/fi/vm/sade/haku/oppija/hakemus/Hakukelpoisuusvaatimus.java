package fi.vm.sade.haku.oppija.hakemus;

public enum Hakukelpoisuusvaatimus {
    YLEINEN_AMMATTIKORKEAKOULUKELPOISUUS("Yleinen ammattikorkeakoulukelpoisuus", "100"),
    AMMATTIKORKEAKOULUTUTKINTO("Ammattikorkeakoulututkinto", "101"),
    ALEMPI_KORKEAKOULUTUTKINTO("Alempi korkeakoulututkinto", "102"),
    YLEMPI_KORKEAKOULUTUTKINTO("Ylempi korkeakoulututkinto", "103"),
    AMMATILLINEN_PERUSTUTKINTO_TAI_VASTAAVA_AIKAISEMPI_TUTKINTO("Ammatillinen perustutkinto tai vastaava aikaisempi tutkinto", "104"),
    AMMATTI_TAI_ERIKOISAMMATTITUTKINTO("Ammatti- tai erikoisammattitutkinto", "105"),
    HARKINNANVARAISUUS_TAI_ERIVAPAUS("Harkinnanvaraisuus tai erivapaus", "106"),
    YLIOPPILASTUTKINTO_JA_AMMATILLINEN_PERUSTUTKINTO_120_OV("Ylioppilastutkinto ja ammatillinen perustutkinto (120 ov)", "107"),
    OPISTO_TAI_AMMATILLISEN_KORKEA_ASTEEN_TUTKINTO("Opisto- tai ammatillisen korkea-asteen tutkinto", "108"),
    SUOMALAINEN_YLIOPPILASTUTKINTO("Suomalainen ylioppilastutkinto", "109"),
    EUROPEAN_BACCALAUREATE_TUTKINTO("European baccalaureate -tutkinto", "110"),
    REIFEPRUFUNG_TUTKINTO("Reifeprüfung-tutkinto", "111"),
    INTERNATIONAL_BACCALAUREATE_TUTKINTO("International Baccalaureate -tutkinto", "112"),
    ULKOMAINEN_TOISEN_ASTEEN_TUTKINTO("Ulkomainen toisen asteen tutkinto", "114"),
    AVOIMEN_AMMATTIKORKEAKOULUN_OPINNOT("Avoimen ammattikorkeakoulun opinnot", "115"),
    ULKOMAINEN_KORKEAKOULUTUTKINTO_BACHELOR("Ulkomainen korkeakoulututkinto (Bachelor)", "116"),
    ULKOMAINEN_KORKEAKOULUTUTKINTO_MASTER("Ulkomainen korkeakoulututkinto (Master)", "117"),
    AVOIMEN_YLIOPISTON_OPINNOT("Avoimen yliopiston opinnot", "118"),
    YLEMPI_AMMATTIKORKEAKOULUTUTKINTO("Ylempi ammattikorkeakoulututkinto", "119"),
    LISENSIAATIN_TUTKINTO("Lisensiaatin tutkinto", "120"),
    TOHTORIN_TUTKINTO("Tohtorin tutkinto", "121"),
    SUOMALAISEN_LUKION_OPPIMAARA_TAI_YLIOPPILASTUTKINTO("Suomalaisen lukion oppimäärä tai ylioppilastutkinto", "122"),
    YLEINEN_YLIOPISTOKELPOISUUS("Yleinen yliopistokelpoisuus", "123");

    private final String name;

    private final String arvo;

    Hakukelpoisuusvaatimus(final String name, final String arvo) {
        this.name = name;
        this.arvo = arvo;
    }

    public String getName() {
        return name;
    }

    public String getArvo() {
        return arvo;
    }

}
