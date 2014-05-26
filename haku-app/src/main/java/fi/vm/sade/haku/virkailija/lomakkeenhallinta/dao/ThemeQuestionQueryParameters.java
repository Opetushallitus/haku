package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao;

public final class ThemeQuestionQueryParameters {
    private String applicationSystemId;
    private String learningOpportunityId;

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public String getLearningOpportunityId() {
        return learningOpportunityId;
    }

    public void setLearningOpportunityId(String learningOpportunityId) {
        this.learningOpportunityId = learningOpportunityId;
    }
}
