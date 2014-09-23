package fi.vm.sade.haku.oppija.hakemus.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class PreferenceEligability {
    public static enum Status {
        NOT_CHECKED,
        ELIGIBLE,
        UNELIGABLE,
        INADEQUATE
    }

    public static enum Source {
        UNKNOWN,
        REGISTER,
        COPY,
        AUTHENTICATED_COPY,
        OFFICIALLY_AUTHENTICATED_COPY,
        ORIGINAL_DIPLOMA,
        LEARNING_PROVIDER
    }

    private final String aoId;
    private final Status status;
    private final Source source;
    private final String rejectionBasis;

    public PreferenceEligability(@JsonProperty(value = "aoId") final String aoId,
      @JsonProperty(value = "status") final Status status,
      @JsonProperty(value = "source") final Source source,
      @JsonProperty(value = "rejectionBasis") final String rejectionBasis) {
        this.aoId = aoId;
        this.status = status;
        this.source = source;
        this.rejectionBasis = rejectionBasis;
    }

    public String getAoId() {
        return aoId;
    }

    public Status getStatus() {
        return status;
    }

    public Source getSource() {
        return source;
    }

    public String getRejectionBasis() {
        return rejectionBasis;
    }
}
