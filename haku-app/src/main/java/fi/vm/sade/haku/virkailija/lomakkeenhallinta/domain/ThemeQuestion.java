package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.ComplexObjectIdDeserializer;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.SimpleObjectIdSerializer;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;


@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = ThemeTextQuestion.class, name = "TextQuestion")
})
public abstract class ThemeQuestion implements ConfiguredElement {

    // ThemeQuestion oid
    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = SimpleObjectIdSerializer.class)
    @JsonDeserialize(using = ComplexObjectIdDeserializer.class)
    private org.bson.types.ObjectId id;

    //Application System oid
    private String applicationSystemId;

    // Where the question is to be displayed
    private String theme;

    //user ident oid
    private String creatorPersonOid;

    // organization oid
    private String ownerOrganizationOid;

    // Type of question
    private String type;

    // Message text
    private I18nText messageText;

    // Help text
    private I18nText helpText;

    // Verbose help text
    private I18nText verboseHelpText;

    // Attached to learning opportunity Identifier
    private String learningOpportunityId;

    // Is requiredQuestion
    private Boolean requiredFieldValidator = Boolean.FALSE;

    // Is show on the completed page
    private Boolean onCompletedPage = Boolean.FALSE;

    // Validators for the question
    private Map<String, String> validators;


    protected ThemeQuestion() {
        this.validators = new HashMap<String, String>();
    }

    protected ThemeQuestion(String type){
        this.type = type;
    }

    @JsonCreator
    protected ThemeQuestion(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
                            @JsonProperty(value = "theme")String theme,
                            @JsonProperty(value = "type")String type,
                            @JsonProperty(value = "learningOpportunityId")String learningOpportunityId,
                            @JsonProperty(value = "validators")Map<String,String> validators ) {
        this.applicationSystemId =  applicationSystemId;
        this.theme = theme;
        this.type = type;
        this.learningOpportunityId = learningOpportunityId;
        this.validators = new HashMap<String,String>(validators);
    }

    protected ThemeQuestion(String applicationSystemId, String theme, String creatorPersonOid,
      String ownerOrganizationOid,
      String type,
      String learningOpportunityId,
      Map<String,String> validators ) {
        this.applicationSystemId =  applicationSystemId;
        this.theme = theme;
        this.creatorPersonOid = creatorPersonOid;
        this.ownerOrganizationOid = ownerOrganizationOid;
        this.type = type;
        this.learningOpportunityId = learningOpportunityId;
        this.validators = new HashMap<String,String>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getCreatorPersonOid() {
        return creatorPersonOid;
    }

    public void setCreatorPersonOid(String creatorPersonOid) {
        this.creatorPersonOid = creatorPersonOid;
    }

    public String getOwnerOrganizationOid() {
        return ownerOrganizationOid;
    }

    public void setOwnerOrganizationOid(String ownerOrganizationOid) {
        this.ownerOrganizationOid = ownerOrganizationOid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public I18nText getMessageText() {
        return messageText;
    }

    public void setMessageText(I18nText messageText) {
        this.messageText = messageText;
    }

    public I18nText getHelpText() {
        return helpText;
    }

    public void setHelpText(I18nText helpText) {
        this.helpText = helpText;
    }

    public I18nText getVerboseHelpText() {
        return verboseHelpText;
    }

    public void setVerboseHelpText(I18nText verboseHelpText) {
        this.verboseHelpText = verboseHelpText;
    }

    public String getLearningOpportunityId() {
        return learningOpportunityId;
    }

    public void setLearningOpportunityId(String learningOpportunityId) {
        this.learningOpportunityId = learningOpportunityId;
    }

    public Boolean getRequiredFieldValidator() {
        return requiredFieldValidator;
    }

    public void setRequiredFieldValidator(Boolean requiredFieldValidator) {
        this.requiredFieldValidator = requiredFieldValidator;
    }

    public Boolean getOnCompletedPage() {
        return onCompletedPage;
    }

    public void setOnCompletedPage(Boolean onCompletedPage) {
        this.onCompletedPage = onCompletedPage;
    }

    public Map<String, String> getValidators() {
        return validators;
    }

    public void setValidators(Map<String, String> validators) {
        this.validators = validators;
    }

    @Override
    public String toString() {
        return "ThemeQuestion{" +
          "id=" + id +
          ", applicationSystemId='" + applicationSystemId + '\'' +
          ", theme='" + theme + '\'' +
          ", creatorPersonOid='" + creatorPersonOid + '\'' +
          ", ownerOrganizationOid='" + ownerOrganizationOid + '\'' +
          ", type='" + type + '\'' +
          ", messageText=" + messageText +
          ", helpText=" + helpText +
          ", verboseHelpText=" + verboseHelpText +
          ", learningOpportunityId='" + learningOpportunityId + '\'' +
          ", requiredFieldValidator=" + requiredFieldValidator +
          ", onCompletedPage=" + onCompletedPage +
          ", validators=" + validators +
          '}';
    }
}
