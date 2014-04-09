package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao;

public final class ThemeQuestionQueryParameters {
    private String applicationSystemId;
    private String learningOpportunityProviderId;

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public String getLearningOpportunityProviderId() {
        return learningOpportunityProviderId;
    }

    public void setLearningOpportunityProviderId(String learningOpportunityProviderId) {
        this.learningOpportunityProviderId = learningOpportunityProviderId;
    }
}
