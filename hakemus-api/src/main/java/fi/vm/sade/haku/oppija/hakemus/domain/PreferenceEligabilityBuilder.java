package fi.vm.sade.haku.oppija.hakemus.domain;

public class PreferenceEligabilityBuilder {
    private String aoId;
    private PreferenceEligability.Status status = PreferenceEligability.Status.NOT_CHECKED;
    private PreferenceEligability.Source source = PreferenceEligability.Source.UNKNOWN;
    private String rejectionBasis;

    public static PreferenceEligabilityBuilder start(){
        return new PreferenceEligabilityBuilder();
    }

    public PreferenceEligabilityBuilder setAoId(String aoId) {
        this.aoId = aoId;
        return this;
    }

    public PreferenceEligabilityBuilder setStatus(PreferenceEligability.Status status) {
        this.status = status;
        return this;
    }

    public PreferenceEligabilityBuilder setSource(PreferenceEligability.Source source) {
        this.source = source;
        return this;
    }

    public PreferenceEligabilityBuilder setRejectionBasis(String rejectionBasis) {
        this.rejectionBasis = rejectionBasis;
        return this;
    }

    public PreferenceEligability build() {
        return new PreferenceEligability(aoId, status, source, rejectionBasis);
    }
}