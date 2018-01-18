package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ApplicationSearchDTO {
    public final String searchTerms;
    public final List<String> aoOids;
    public final List<String> asIds;
    public final List<String> states;
    public final List<String> keys;

    public ApplicationSearchDTO(
            @JsonProperty("searchTerms") String searchTerms,
            @JsonProperty("aoOids") List<String> aoOids,
            @JsonProperty("asIds") List<String> asIds,
            @JsonProperty("states") List<String> states,
            @JsonProperty("keys") List<String> keys) {
        this.searchTerms = searchTerms;
        this.aoOids = aoOids;
        this.asIds = asIds;
        this.states = states;
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "ApplicationSearchDTO{applicationOptionOids=" + aoOids + ", asIds='" + asIds + ", states='" + states + " keys='"+ keys +"'}";
    }
}
