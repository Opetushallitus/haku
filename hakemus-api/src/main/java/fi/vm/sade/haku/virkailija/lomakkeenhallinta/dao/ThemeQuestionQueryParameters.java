package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public final class ThemeQuestionQueryParameters implements Cloneable{
    // 3,2,1
    public static Integer SORT_DESCENDING = -1;
    // 1,2,3
    public static Integer SORT_ASCENDING = 1;

    private String applicationSystemId;
    private String learningOpportunityId;
    private Value<String> parentThemeQuestionId;
    private String organizationId;
    private String theme;
    private Boolean searchDeleted;
    private Boolean queryGroups;
    private Boolean onlyWithAttachmentRequests;

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

    public String getParentThemeQuestionId() {
        return null == parentThemeQuestionId ? null : parentThemeQuestionId.get();
    }

    public void setParentThemeQuestionId(String parentThemeQuestionId) {
        this.parentThemeQuestionId = new Value<String>(parentThemeQuestionId);
    }

    public void unsetParentThemeQuestionId() {
        this.parentThemeQuestionId = null;
    }

    public Boolean isSetParentThemeQuestionId() {
        return null != parentThemeQuestionId;
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

    public Boolean queryGroups() {
        return queryGroups;
    }

    public void setQueryGroups(final Boolean queryGroups) {
        this.queryGroups = queryGroups;
    }

    public Boolean onlyWithAttachmentRequests() {
        return onlyWithAttachmentRequests;
    }

    public void setOnlyWithAttachmentRequests(Boolean onlyWithAttachmentRequests) {
        this.onlyWithAttachmentRequests = onlyWithAttachmentRequests;
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
          ", parentThemeQuestionId='" + parentThemeQuestionId + '\'' +
          ", organizationId='" + organizationId + '\'' +
          ", theme='" + theme + '\'' +
          ", searchDeleted=" + searchDeleted +
          ", onlyWithAttachmentRequests=" + onlyWithAttachmentRequests +
          ", sortBy=" + sortBy +
          '}';
    }

    @Override
    public ThemeQuestionQueryParameters clone() {
        ThemeQuestionQueryParameters clone = new ThemeQuestionQueryParameters();
        clone.applicationSystemId = this.applicationSystemId;
        clone.learningOpportunityId = this.learningOpportunityId;
        clone.parentThemeQuestionId = this.parentThemeQuestionId;
        clone.organizationId = this.organizationId;
        clone.theme = this.theme;
        clone.searchDeleted = this.searchDeleted;
        clone.onlyWithAttachmentRequests = this.onlyWithAttachmentRequests;
        clone.sortBy = (ArrayList<Pair<String, Integer>>) this.sortBy.clone();
        return clone;
    }

    private final class Value<T>{
        private final T value;

        private Value(final T value) {
            this.value = value;
        }

        private T get(){
            return value;
        }

        @Override
        public String toString() {
            return "Value{"+ value +'}';
        }
    }
}
