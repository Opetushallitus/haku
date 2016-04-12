package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ApplicationSearchDTO {
    public final List<String> aoOids;
    public final List<String> asIds;
    public final List<String> states;


    public ApplicationSearchDTO(
            @JsonProperty("aoOids") List<String> aoOids,
            @JsonProperty("asIds") List<String> asIds,
            @JsonProperty("states") List<String> states) {
        this.aoOids = aoOids;
        this.asIds = asIds;
        this.states = states;
    }

    @Override
    public String toString() {
        return "ApplicationSearchDTO{applicationOptionOids=" + aoOids + ", asIds='" + asIds + ", states='" + states + " keys='"+ keys +"'}";
    }
}
