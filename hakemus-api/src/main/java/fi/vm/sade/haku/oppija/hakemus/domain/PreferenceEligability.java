package fi.vm.sade.haku.oppija.hakemus.domain;


public class PreferenceEligability {
    public static enum Status {
        ELIGABLE
    }

    public static enum Source {
        REGISTER
    }

    private String aoId;
    private Status status;
    private Source source;
    private String rejectionBasis;
}
