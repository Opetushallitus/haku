package fi.vm.sade.haku.oppija.hakemus.domain;


public class PreferenceEligability {
    public static enum Status {
        NOT_CHECKED,
        INELIGIBLE,
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

    public PreferenceEligability(String aoId, Status status, Source source, String rejectionBasis) {
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
