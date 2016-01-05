package fi.vm.sade.haku.oppija.hakemus.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class PreferenceEligibility implements Serializable {
    public static enum Status {
        NOT_CHECKED,
        ELIGIBLE,
        INELIGIBLE,
        INADEQUATE,
        AUTOMATICALLY_CHECKED_ELIGIBLE
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

    public static String getStatusMessage(Status status) {
        switch (status) {
            case NOT_CHECKED:
                return "Kelpoisuus tarkistamatta";
            case AUTOMATICALLY_CHECKED_ELIGIBLE:
                return "Kelpoisuus hyväksytty automaattisesti";
            case ELIGIBLE:
                return "Kelpoisuus hyväksytty automaattisesti";
            case INELIGIBLE:
                return "Ei hakukelpoinen";
            case INADEQUATE:
                return "Puutteelinen";
            default:
                return "";
        }
    }

    public static String getSourceMessage(Source source) {
        switch (source) {
            case LEARNING_PROVIDER:
                return "Oppilaitoksen toimittama tieto";
            case ORIGINAL_DIPLOMA:
                return "Alkuperäinen todistus";
            case OFFICIALLY_AUTHENTICATED_COPY:
                return "Virallinen oikeaksi todistettu kopio";
            case AUTHENTICATED_COPY:
                return "Oikeaksi todistettu kopio";
            case COPY:
                return "Kopio";
            case REGISTER:
                return "Rekisteri";
            default:
                return "";
        }
    }

    private final String aoId;
    private Status status;
    private Source source;
    private String rejectionBasis;

    public PreferenceEligibility(@JsonProperty(value = "aoId") final String aoId,
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

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setSource(final Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public void setRejectionBasis(String rejectionBasis) {
        this.rejectionBasis = rejectionBasis;
    }

    public String getRejectionBasis() {
        return rejectionBasis;
    }
}
