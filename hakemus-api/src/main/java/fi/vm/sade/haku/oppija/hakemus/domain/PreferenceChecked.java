package fi.vm.sade.haku.oppija.hakemus.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class PreferenceChecked implements Serializable {
    private final String preferenceAoOid;
    private Boolean checked;
    private String checkedByOfficerOid;


    @JsonCreator
    public PreferenceChecked(@JsonProperty(value = "preferenceAoOid") final String preferenceAoOid,  @JsonProperty(value = "checked") final Boolean checked, @JsonProperty(value = "checkedByOfficerOid") final String checkedByOfficerOid) {
        this.preferenceAoOid = preferenceAoOid;
        this.checked = null == checked ? Boolean.FALSE: checked;
        this.checkedByOfficerOid = checkedByOfficerOid;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @JsonIgnore
    public Boolean isChecked(){
        return checked;
    }

    public Boolean getChecked(){
        return checked;
    }

    public String getPreferenceAoOid() {
        return preferenceAoOid;
    }

    public void setCheckedByOfficerOid(String checkedByOfficerOid) {
        this.checkedByOfficerOid = checkedByOfficerOid;
    }

    public String getCheckedByOfficerOid() {
        return checkedByOfficerOid;
    }

}
