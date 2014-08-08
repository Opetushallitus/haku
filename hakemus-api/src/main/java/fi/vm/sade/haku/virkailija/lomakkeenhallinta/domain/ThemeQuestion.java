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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = ThemeTextQuestion.class, name = "TextQuestion"),
  @JsonSubTypes.Type(value = ThemeRadioButtonQuestion.class, name = "RadioButton"),
  @JsonSubTypes.Type(value = ThemeCheckBoxQuestion.class, name = "CheckBox")
})
public abstract class ThemeQuestion implements ConfiguredElement {

    public enum State {
        ACTIVE, LOCKED, DELETED
    }
    // ThemeQuestion oid
    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = SimpleObjectIdSerializer.class)
    @JsonDeserialize(using = ComplexObjectIdDeserializer.class)
    private org.bson.types.ObjectId id;

    // ThemeQuestion state
    private State state = State.ACTIVE;

    //Application System oid
    private String applicationSystemId;

    // Where the question is to be displayed
    private String theme;

    //Ordinal
    private Integer ordinal;

    //user ident oid
    private String creatorPersonOid;

    // organization oid
    private List<String> ownerOrganizationOids;

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

    // the learning opportunity is not a learning opportunity but a group \o/
    private Boolean isGroup;

    // Is requiredQuestion
    private Boolean requiredFieldValidator = Boolean.FALSE;

    // Is show on the completed page
    private Boolean onCompletedPage = Boolean.FALSE;

    // Validators for the question
    private Map<String, String> validators;

    // Attachment requests
    private List<AttachmentRequest> attachmentRequests;


    protected ThemeQuestion() {
        this.ownerOrganizationOids = new ArrayList<String>();
        this.validators = new HashMap<String, String>();
        this.attachmentRequests = new ArrayList<AttachmentRequest>();
    }

    protected ThemeQuestion(String type){
        this.type = type;
        this.ownerOrganizationOids = new ArrayList<String>();
        this.validators = new HashMap<String, String>();
        this.attachmentRequests = new ArrayList<AttachmentRequest>();
    }

    @JsonCreator
    protected ThemeQuestion(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
                            @JsonProperty(value = "theme")String theme,
                            @JsonProperty(value = "type")String type,
                            @JsonProperty(value = "learningOpportunityId")String learningOpportunityId,
                            @JsonProperty(value = "isGroup") Boolean isGroup,
                            @JsonProperty(value = "ordial") Integer ordinal,
                            @JsonProperty(value = "validators")Map<String,String> validators,
                            @JsonProperty(value = "attachmentRequests") List<AttachmentRequest>attachmentRequests) {
        this.applicationSystemId =  applicationSystemId;
        this.theme = theme;
        this.type = type;
        this.learningOpportunityId = learningOpportunityId;
        this.isGroup = isGroup;
        this.ordinal = ordinal;
        this.validators = new HashMap<String,String>(validators);
        this.ownerOrganizationOids = new ArrayList<String>();
        this.attachmentRequests = new ArrayList<AttachmentRequest>(attachmentRequests);
    }

    protected ThemeQuestion(String applicationSystemId, String theme, String creatorPersonOid,
      List<String> ownerOrganizationOid,
      String type,
      String learningOpportunityId,
      Boolean isGroup,
      Integer ordinal,
      Map<String,String> validators,
      List<AttachmentRequest>attachmentRequests) {
        this.applicationSystemId =  applicationSystemId;
        this.theme = theme;
        this.creatorPersonOid = creatorPersonOid;
        this.ownerOrganizationOids = new ArrayList<String>(ownerOrganizationOid);
        this.type = type;
        this.learningOpportunityId = learningOpportunityId;
        this.isGroup = isGroup;
        this.ordinal = ordinal;
        this.validators = new HashMap<String,String>(validators);
        this.ownerOrganizationOids = new ArrayList<String>();
        this.attachmentRequests = new ArrayList<AttachmentRequest>(attachmentRequests);
    }

    public ObjectId getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public String getTheme() {
        return theme;
    }

    public String getCreatorPersonOid() {
        return creatorPersonOid;
    }

    public void setCreatorPersonOid(String creatorPersonOid) {
        this.creatorPersonOid = creatorPersonOid;
    }

    public List<String> getOwnerOrganizationOids() {
        return ownerOrganizationOids;
    }

    public void setOwnerOrganizationOids(List<String> ownerOrganizationOids) {
        this.ownerOrganizationOids = new ArrayList<String>(ownerOrganizationOids);
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

    public Boolean isGroup() {
        return null != isGroup ? isGroup: Boolean.FALSE;
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
        this.validators = new HashMap<String, String>(validators);
    }

    public List<AttachmentRequest> getAttachmentRequests() {
        return attachmentRequests;
    }

    public void setAttachmentRequests(List<AttachmentRequest> attachmentRequests) {
        this.attachmentRequests = new ArrayList<AttachmentRequest>(attachmentRequests);
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public String toString() {
        return "ThemeQuestion{" +
          "id=" + id +
          ", state=" + state +
          ", applicationSystemId='" + applicationSystemId + '\'' +
          ", theme='" + theme + '\'' +
          ", ordinal=" + ordinal +
          ", creatorPersonOid='" + creatorPersonOid + '\'' +
          ", ownerOrganizationOids=" + ownerOrganizationOids +
          ", type='" + type + '\'' +
          ", messageText=" + messageText +
          ", helpText=" + helpText +
          ", verboseHelpText=" + verboseHelpText +
          ", learningOpportunityId='" + learningOpportunityId + '\'' +
          ", isGroup=" + isGroup +
          ", requiredFieldValidator=" + requiredFieldValidator +
          ", onCompletedPage=" + onCompletedPage +
          ", validators=" + validators +
          ", attachmentRequests=" + attachmentRequests +
          '}';
    }
}
