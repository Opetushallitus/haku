package fi.vm.sade.haku.oppija.hakemus.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationPreferenceMeta {

    private final Integer ordinal;
    private final Map<String, String> preferenceData;

    @JsonCreator
    public ApplicationPreferenceMeta(@JsonProperty(value = "ordinal")final Integer ordinal,
                                     @JsonProperty(value = "preferenceData") final Map<String, String> preferenceData) {
        this.ordinal = ordinal;
        if (null != preferenceData)
            this.preferenceData = preferenceData;
        else
            this.preferenceData = new HashMap<>(2);
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public Map<String, String> getPreferenceData() {
        return preferenceData;
    }
}
