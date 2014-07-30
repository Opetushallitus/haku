package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao;

public final class ThemeQuestionQueryParameters {
    private String applicationSystemId;
    private String learningOpportunityId;
    private String organizationId;
    private String theme;
    private Boolean searchDeleted;

    public ThemeQuestionQueryParameters(){
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(final String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public String getLearningOpportunityId() {
        return learningOpportunityId;
    }

    public void setLearningOpportunityId(final String learningOpportunityId) {
        this.learningOpportunityId = learningOpportunityId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(final String organizationId) {
        this.organizationId = organizationId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(final String theme) {
        this.theme = theme;
    }

    public Boolean searchDeleted() {
        return ((null != searchDeleted) && searchDeleted) ;
    }

    public void setSearchDeleted(final Boolean searchDeleted){
        this.searchDeleted = searchDeleted;
    }

    @Override
    public String toString() {
        return "ThemeQuestionQueryParameters{" +
          "applicationSystemId='" + applicationSystemId + '\'' +
          ", learningOpportunityId='" + learningOpportunityId + '\'' +
          ", organizationId='" + organizationId + '\'' +
          ", theme='" + theme + '\'' +
          ", searchDeleted=" + searchDeleted +
          '}';
    }
}
