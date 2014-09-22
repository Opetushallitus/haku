package fi.vm.sade.haku.oppija.hakemus.domain;

public class PreferenceChecked {
    private final String preferenceAoOid;
    private final String checkedByOfficerOid;

    public PreferenceChecked(final String preferenceAoOid, final String checkedByOfficerOid) {
        this.preferenceAoOid = preferenceAoOid;
        this.checkedByOfficerOid = checkedByOfficerOid;
    }

    public String getPreferenceAoOid() {
        return preferenceAoOid;
    }

    public String getCheckedByOfficerOid() {
        return checkedByOfficerOid;
    }
}
