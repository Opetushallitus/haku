package fi.vm.sade.haku.oppija.hakemus.service;

public enum Role {
    ROLE_OPO("APP_HAKEMUS_OPO"),
    ROLE_R("APP_HAKEMUS_READ"),
    ROLE_RU("APP_HAKEMUS_READ_UPDATE"),
    ROLE_CRUD("APP_HAKEMUS_CRUD"),
    ROLE_LISATIETORU("APP_HAKEMUS_LISATIETORU"),
    ROLE_LISATIETOCRUD("APP_HAKEMUS_LISATIETOCRUD"),
    ROLE_HETUTTOMIENKASITTELY("APP_HAKEMUS_HETUTTOMIENKASITTELY"),
    ROLE_KKVIRKAILIJA("APP_HAKEMUS_KKVIRKAILIJA");

    public final String casName;

    private Role(String casName) {
        this.casName = casName;
    }
}
