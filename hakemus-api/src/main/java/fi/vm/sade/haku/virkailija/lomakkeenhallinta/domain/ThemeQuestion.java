package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationOptionAttachmentRequest;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationOptionAttachmentRequestBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ElementBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.ComplexObjectIdDeserializer;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.SimpleObjectIdSerializer;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
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
  @JsonSubTypes.Type(value = ThemeRichText.class, name = "RichText"),
  @JsonSubTypes.Type(value = ThemeRadioButtonQuestion.class, name = "RadioButton"),
  @JsonSubTypes.Type(value = ThemeCheckBoxQuestion.class, name = "CheckBox")
})
public abstract class ThemeQuestion implements ConfiguredElement {

    public static String FIELD_ORDINAL = "ordinal";

    public enum State {
        ACTIVE, LOCKED, DELETED
    }

    // ThemeQuestion id
    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = SimpleObjectIdSerializer.class)
    @JsonDeserialize(using = ComplexObjectIdDeserializer.class)
    private org.bson.types.ObjectId id;

    //parent question id
    @JsonProperty(value = "parentId")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = SimpleObjectIdSerializer.class)
    @JsonDeserialize(using = ComplexObjectIdDeserializer.class)
    private org.bson.types.ObjectId parentId;

    private String followupCondition;

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

    // Message text
    private I18nText messageText;

    // Help text
    private I18nText helpText;

    // Verbose help text
    private I18nText verboseHelpText;

    // Attached to learning opportunity Identifier
    private String learningOpportunityId;

    // the learning opportunity is not a learning opportunity but a group \o/
    private Boolean targetIsGroup;

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


    @JsonCreator
    protected ThemeQuestion(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
                            @JsonProperty(value = "theme") String theme,
                            @JsonProperty(value = "learningOpportunityId") String learningOpportunityId,
                            @JsonProperty(value = "targetIsGroup") Boolean targetIsGroup,
                            @JsonProperty(value = "ordinal") Integer ordinal,
                            @JsonProperty(value = "validators")Map<String,String> validators,
                            @JsonProperty(value = "attachmentRequests") List<AttachmentRequest>attachmentRequests) {
        this.applicationSystemId =  applicationSystemId;
        this.theme = theme;
        this.learningOpportunityId = learningOpportunityId;
        this.targetIsGroup = targetIsGroup;
        this.ordinal = ordinal;
        this.validators = new HashMap<String,String>();
        if (null != validators)
            this.validators.putAll(validators);
        this.attachmentRequests = new ArrayList<AttachmentRequest>();
        if (null != attachmentRequests)
            this.attachmentRequests.addAll(attachmentRequests);
        this.ownerOrganizationOids = new ArrayList<String>();
    }

    protected ThemeQuestion(String applicationSystemId, String theme, String creatorPersonOid,
      List<String> ownerOrganizationOid,
      String learningOpportunityId,
      Boolean targetIsGroup,
      Integer ordinal,
      Map<String,String> validators,
      List<AttachmentRequest>attachmentRequests) {
        this.applicationSystemId =  applicationSystemId;
        this.theme = theme;
        this.creatorPersonOid = creatorPersonOid;
        this.ownerOrganizationOids = new ArrayList<String>(ownerOrganizationOid);
        this.learningOpportunityId = learningOpportunityId;
        this.targetIsGroup = targetIsGroup;
        this.ordinal = ordinal;
        this.validators = new HashMap<String,String>(validators);
        this.ownerOrganizationOids = new ArrayList<String>();
        this.attachmentRequests = new ArrayList<AttachmentRequest>(attachmentRequests);
    }

    public List<ApplicationOptionAttachmentRequest> generateAttactmentRequests(FormParameters formParameters){
        List<ApplicationOptionAttachmentRequest> generatedRequests = new ArrayList<ApplicationOptionAttachmentRequest>(attachmentRequests.size());
        for (AttachmentRequest attachmentRequest : attachmentRequests){
            generatedRequests.add(
              ApplicationOptionAttachmentRequestBuilder.start()
              .setApplicationOptionId(learningOpportunityId)
              .setGroupOption(getTargetIsGroup())
              .setCondition(generateAttachmentCondition(formParameters, attachmentRequest))
              .setDeliveryAddress(attachmentRequest.getDeliveryAddress())
              .setDeliveryDue(attachmentRequest.getDeliveryDue())
              .setHeader(attachmentRequest.getHeader())
              .setDescription(attachmentRequest.getDescription())
              .build());
        }
        return generatedRequests;
    }

    protected abstract Expr generateAttachmentCondition(FormParameters formParameters, AttachmentRequest attachmentRequest);

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public List<String> getOwnerOrganizationOids() {
        return ownerOrganizationOids;
    }

    public void setOwnerOrganizationOids(List<String> ownerOrganizationOids) {
        this.ownerOrganizationOids = new ArrayList<String>(ownerOrganizationOids);
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

    public Boolean getTargetIsGroup() {
        return null != targetIsGroup ? targetIsGroup: Boolean.FALSE;
    }

    public void setTargetIsGroup(Boolean targetIsGroup) {
        this.targetIsGroup = targetIsGroup;
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

    void addAoidOrAoidGroup(ElementBuilder elementBuilder) {
        if (this.getTargetIsGroup()) {
            elementBuilder.applicationOptionGroupId(this.getLearningOpportunityId());
        } else {
            elementBuilder.applicationOptionId(this.getLearningOpportunityId());
        }
    }
    
    public ObjectId getParentId() {
        return parentId;
    }

    public void setParentId(ObjectId parentId) {
        this.parentId = parentId;
    }

    public String getFollowupCondition() {
        return followupCondition;
    }

    public void setFollowupCondition(String followupCondition) {
        this.followupCondition = followupCondition;
    }

    @Override
    public String toString() {
        return "ThemeQuestion{" +
          "id=" + id +
          ", class=" + this.getClass().getSimpleName() +
          ", parentId=" + parentId +
          ", followupCondition='" + followupCondition + '\'' +
          ", state=" + state +
          ", applicationSystemId='" + applicationSystemId + '\'' +
          ", theme='" + theme + '\'' +
          ", ordinal=" + ordinal +
          ", creatorPersonOid='" + creatorPersonOid + '\'' +
          ", ownerOrganizationOids=" + ownerOrganizationOids +
          ", messageText=" + messageText +
          ", helpText=" + helpText +
          ", verboseHelpText=" + verboseHelpText +
          ", learningOpportunityId='" + learningOpportunityId + '\'' +
          ", targetIsGroup=" + targetIsGroup +
          ", requiredFieldValidator=" + requiredFieldValidator +
          ", onCompletedPage=" + onCompletedPage +
          ", validators=" + validators +
          ", attachmentRequests=" + attachmentRequests +
          '}';
    }
}
