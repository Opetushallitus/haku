package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao;

public final class ThemeQuestionQueryParameters {
    private String applicationSystemId;
    private String learningOpportunityId;
    private String organizationId;
    private Boolean searchDeleted;

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

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean searchDeleted() {
        return ((null != searchDeleted) && searchDeleted) ;
    }

    public void setSearchDeleted(Boolean searchDeleted){
        this.searchDeleted = searchDeleted;
    }
}
