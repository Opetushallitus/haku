package fi.vm.sade.haku.oppija.hakemus.domain;

public class PreferenceCheckedBuilder {
    private String preferenceAoOid;
    private String checkedByOfficerOid;

    public static PreferenceCheckedBuilder start(){
        return new PreferenceCheckedBuilder();
    }

    public PreferenceCheckedBuilder setPreferenceAoOid(String preferenceAoOid) {
        this.preferenceAoOid = preferenceAoOid;
        return this;
    }

    public PreferenceCheckedBuilder setCheckedByOfficerOid(String checkedByOfficerOid) {
        this.checkedByOfficerOid = checkedByOfficerOid;
        return this;
    }

    public PreferenceChecked build() {
        return new PreferenceChecked(preferenceAoOid, checkedByOfficerOid);
    }
}