package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;


public class ApplicationOidsAndReason {
    public final List<String> applicationOids;
    public final String reason;

    public ApplicationOidsAndReason(
            @JsonProperty("applicationOids") List<String> applicationOids,
            @JsonProperty("reason") String reason) {
        this.applicationOids = applicationOids;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ApplicationOidsAndReason{applicationOids=" + applicationOids + ", reason='" + reason + "'}";
    }
}
