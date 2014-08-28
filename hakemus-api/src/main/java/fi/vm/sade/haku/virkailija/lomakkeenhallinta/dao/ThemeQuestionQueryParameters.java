package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public final class ThemeQuestionQueryParameters {
    // 3,2,1
    public static Integer SORT_DESCENDING = -1;
    // 1,2,3
    public static Integer SORT_ASCENDING = 1;

    private String applicationSystemId;
    private String learningOpportunityId;
    private String organizationId;
    private String theme;
    private Boolean searchDeleted;

    private ArrayList<Pair<String, Integer>> sortBy = new ArrayList<Pair<String, Integer>>();

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

    public void addSortBy(final String fieldName, final Integer sortOrder){
        if (null != fieldName && (SORT_ASCENDING.equals(sortOrder) || SORT_DESCENDING.equals(sortOrder))){
            sortBy.add(Pair.of(fieldName, sortOrder));
        }
        else {
            throw new RuntimeException("Invalid parameters: fieldName : "+ fieldName +", sortOrder: "+ sortOrder);
        }
    }

    public List<Pair<String, Integer>> getSortBy(){
        return sortBy;
    }

    @Override
    public String toString() {
        return "ThemeQuestionQueryParameters{" +
          "applicationSystemId='" + applicationSystemId + '\'' +
          ", learningOpportunityId='" + learningOpportunityId + '\'' +
          ", organizationId='" + organizationId + '\'' +
          ", theme='" + theme + '\'' +
          ", searchDeleted=" + searchDeleted +
          ", sortBy=" + sortBy +
          '}';
    }
}
