package fi.vm.sade.haku.oppija.hakemus.domain;

public class PreferenceEligibilityBuilder {
    private String aoId;
    private PreferenceEligibility.Maksuvelvollisuus maksuvelvollisuus = PreferenceEligibility.Maksuvelvollisuus.NOT_CHECKED;
    private PreferenceEligibility.Status status = PreferenceEligibility.Status.NOT_CHECKED;
    private PreferenceEligibility.Source source = PreferenceEligibility.Source.UNKNOWN;
    private String rejectionBasis;

    public static PreferenceEligibilityBuilder start(){
        return new PreferenceEligibilityBuilder();
    }

    public PreferenceEligibilityBuilder setAoId(String aoId) {
        this.aoId = aoId;
        return this;
    }

    public PreferenceEligibilityBuilder setStatus(PreferenceEligibility.Status status) {
        this.status = status;
        return this;
    }

    public PreferenceEligibilityBuilder setSource(PreferenceEligibility.Source source) {
        this.source = source;
        return this;
    }

    public PreferenceEligibilityBuilder setRejectionBasis(String rejectionBasis) {
        this.rejectionBasis = rejectionBasis;
        return this;
    }

    public PreferenceEligibility build() {
        return new PreferenceEligibility(aoId, status, source, rejectionBasis, maksuvelvollisuus);
    }
}