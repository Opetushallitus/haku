package fi.vm.sade.haku.oppija.hakemus.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class PreferenceChecked {
    private final String preferenceAoOid;
    private final String checkedByOfficerOid;

    @JsonCreator
    public PreferenceChecked(@JsonProperty(value = "preferenceAoOid") final String preferenceAoOid, @JsonProperty(value = "checkedByOfficerOid")final String checkedByOfficerOid) {
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
