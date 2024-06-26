/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.oppija.hakemus.domain;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.ObjectIdDeserializer;
import fi.vm.sade.haku.oppija.lomake.domain.ObjectIdSerializer;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties({ "type", "personOidChecked", "studentOidChecked" })
public class Application implements Serializable {

    private static final String[] EXCLUDED_FIELDS = new String[]{"id"};

    @JsonIgnore
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    public static final Integer CURRENT_MODEL_VERSION = 7;
    public static final String META_FILING_LANGUAGE = "filingLanguage";
    public static final String REQUIRED_PAYMENT_STATE = "requiredPaymentState";
    public static final String PAYMENT_DUE_DATE = "paymentDueDate";
    public static final String APPLICATION_STATE = "state";
    private Date paymentDueDate;

    public Integer getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(Integer modelVersion) {
        this.modelVersion = modelVersion;
    }

    public void setPaymentDueDate(Date paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public Date getPaymentDueDate() {
        return paymentDueDate;
    }

    public enum State {
        ACTIVE, PASSIVE, INCOMPLETE, SUBMITTED, DRAFT
    }

    public enum PostProcessingState {
        NOMAIL, FULL, DONE, FAILED
    }

    public enum PaymentState {
        NOTIFIED, // Successfully notified payment system
        OK, // Payment done according to payment system
        NOT_OK // Payment system indicated that payment will not be made
    }

    private PaymentState requiredPaymentState;

    public boolean paymentIsOk() {
        return requiredPaymentState == null || requiredPaymentState == PaymentState.OK;
    }

    private static final long serialVersionUID = -7491168801255850954L;
    public static final String VAIHE_ID = "phaseId";

    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private org.bson.types.ObjectId id; //NOSONAR Json-sarjallistajan käyttämä.

    private String oid;
    private State state;
    private Boolean studentIdentificationDone;
    private String applicationSystemId;
    private User user;
    private String phaseId;
    private String personOid;
    private String studentOid;
    private Long lastAutomatedProcessingTime;
    private Integer automatedProcessingFailCount;
    private Long automatedProcessingFailRetryTime;
    private Date received;
    private Date updated;
    //TODO: Rename if/when refactoring
    private PostProcessingState redoPostProcess;

    private String fullName;
    private HashSet<String> searchNames = new HashSet<String>();

    private Map<String, Map<String, String>> answers = new HashMap<String, Map<String, String>>();
    private Map<String, String> meta = new HashMap<String, String>();
    private AuthorizationMeta authorizationMeta = null; // new AuthorizationMeta();
    private Map<String, String> overriddenAnswers = new HashMap<String, String>();
    private Map<String, String> additionalInfo = new HashMap<String, String>();
    private LinkedList<ApplicationNote> notes = new LinkedList<ApplicationNote>();
    private List<Change> history = new ArrayList<Change>();
    private Integer version;
    private Integer modelVersion;

    //Higher-education only ...
    private Date eligibilitiesAndAttachmentsUpdated;
    private List<PreferenceEligibility> preferenceEligibilities = new ArrayList<PreferenceEligibility>();
    private List<ApplicationAttachmentRequest> attachmentRequests = new ArrayList<ApplicationAttachmentRequest>();
    private List<PreferenceChecked> preferencesChecked = new ArrayList<PreferenceChecked>();

    @JsonCreator
    public Application(@JsonProperty(value = "applicationSystemId") final String applicationSystemId,
                       @JsonProperty(value = "user") final User user,
                       @JsonProperty(value = "answers") Map<String, Map<String, String>> answers,
                       @JsonProperty(value = "additionalInfo") Map<String, String> additionalInfo) {
        this(applicationSystemId, user);
        if (answers != null) {
            this.answers = answers;
            updateNameMetadata();
        }
        if (additionalInfo != null) {
            this.additionalInfo = additionalInfo;
        }
    }

    @JsonIgnore
    public Application(User user) {
        this.user = user;
    }

    @JsonIgnore
    public Application() {
    }

    @JsonIgnore
    public Application(final String oid) {
        this.oid = oid;
    }

    @JsonIgnore
    public Application(final String oid, final Integer version) {
        this.oid = oid;
        this.version = version;
    }


    @JsonIgnore
    public Application(@JsonProperty(value = "applicationSystemId") final String applicationSystemId,
                       @JsonProperty(value = "user") final User user) {
        this.applicationSystemId = applicationSystemId;
        this.user = user;
    }

    @JsonIgnore
    public Application(@JsonProperty(value = "applicationSystemId") final String applicationSystemId,
                       @JsonProperty(value = "user") final User user,
                       final String oid) {
        this.applicationSystemId = applicationSystemId;
        this.user = user;
        this.oid = oid;
    }

    public Application(final User user, final ApplicationPhase phase) {
        this(phase.getApplicationSystemId(), user);
        setVaiheenVastauksetAndSetPhaseId(phase.getPhaseId(), phase.getAnswers());
    }

    public Application(final String applicationSystemId, final String oid) {
        this.applicationSystemId = applicationSystemId;
        this.oid = oid;
    }

    @JsonIgnore
    public void activate() {
        state = State.ACTIVE;
    }

    @JsonIgnore
    public void incomplete() {
        state = State.INCOMPLETE;
    }

    @JsonIgnore
    public void submitted() {
        state = State.SUBMITTED;
    }

    @JsonIgnore
    public boolean isActive() {
        return state != null && state.equals(State.ACTIVE);
    }

    @JsonIgnore
    public boolean isDraft() {
        return state != null && state.equals(State.DRAFT);
    }

    @JsonIgnore
    public boolean isPassive() {
        return state != null && state.equals(State.PASSIVE);
    }

    @JsonIgnore
    public boolean isIncomplete() {
        return state != null && state.equals(State.INCOMPLETE);
    }

    @JsonIgnore
    public boolean isSubmitted() {
        return state != null && state.equals(State.SUBMITTED);
    }

    // final, koska kutsutaan rakentajasta
    public final Application setVaiheenVastauksetAndSetPhaseId(final String phaseId, Map<String, String> answers) {
        this.phaseId = answers.get(VAIHE_ID);
        Map<String, String> answersWithoutPhaseId = new HashMap<String, String>(
                Maps.filterKeys(answers, Predicates.not(Predicates.equalTo(VAIHE_ID))));
        this.answers.put(phaseId, answersWithoutPhaseId);
        updateNameMetadata();
        return this;
    }

    public User getUser() {
        return user;
    }

    public void resetUser() {
        this.user = null;
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    @JsonIgnore
    public Map<String, String> getVastauksetMerged() {
        Map<String, String> answers = new HashMap<String, String>(200);
        for (Map<String, String> phaseAnswers : this.answers.values()) {
            answers.putAll(phaseAnswers);
        }
        answers = addMetaToAnswers(answers);
        return Collections.unmodifiableMap(answers);
    }

    @JsonIgnore
    public String getEmail() {
        Map<String, String> vastauksetMerged = getVastauksetMerged();
        if (vastauksetMerged.containsKey(OppijaConstants.ELEMENT_ID_EMAIL)) {
            return vastauksetMerged.get(OppijaConstants.ELEMENT_ID_EMAIL);
        } else {
            return null;
        }
    }

    @JsonIgnore
    public String getHuoltajaEmail() {
        Map<String, String> vastauksetMerged = getVastauksetMerged();
        if (vastauksetMerged.containsKey(OppijaConstants.ELEMENT_ID_HUOLTAJANSAHKOPOSTI)) {
            return vastauksetMerged.get(OppijaConstants.ELEMENT_ID_HUOLTAJANSAHKOPOSTI);
        } else {
            return null;
        }
    }

    @JsonIgnore
    public Map<String, String> getVastauksetMergedIgnoringPhase(final String phaseId) {
        Map<String, String> answers = new HashMap<>(200);
        for (String phaseKey : this.answers.keySet()) {
            if (!phaseKey.equalsIgnoreCase(phaseId)) {
                answers.putAll(this.answers.get(phaseKey));
            }
        }
        answers = addMetaToAnswers(answers);
        return Collections.unmodifiableMap(answers);
    }

    private Map<String, String> addMetaToAnswers(Map<String, String> answers) {
        for (Map.Entry<String, String> entry : meta.entrySet()) {
            String key = "_meta_" + entry.getKey();
            String value = entry.getValue();
            answers.put(key, value);
        }
        return answers;
    }

    @JsonIgnore
    public void removeUser() {
        if (user != null) {
            HashMap<String, String> meta = new HashMap<String, String>();
            meta.put("sessionId", user.getUserName());
            this.setMeta(meta);
            this.user = null;
        }
    }

    public Map<String, String> getPhaseAnswers(final String phaseId) {
        Map<String, String> phaseAnswers = this.answers.get(phaseId);
        if (phaseAnswers != null && !phaseAnswers.isEmpty()) {
            return Collections.unmodifiableMap(phaseAnswers);
        }
        return new HashMap<String, String>();
    }

    public Map<String, String> getPhaseAnswersForModify(final String phaseId) {
        Map<String, String> phaseAnswers = this.answers.get(phaseId);
        if (phaseAnswers != null && !phaseAnswers.isEmpty()) {
            return phaseAnswers;
        }
        return new HashMap<String, String>();
    }

    @JsonIgnore
    public boolean isNew() {
        return this.phaseId == null;
    }

    @JsonIgnore
    public void enforceLowercaseEmail() {
        Map<String, String> henkilotiedot = this.answers.get("henkilotiedot");
        if (henkilotiedot != null) {
            String maybeEmptyEmail = henkilotiedot.get(OppijaConstants.ELEMENT_ID_EMAIL);
            if(maybeEmptyEmail != null) {
                henkilotiedot.put(OppijaConstants.ELEMENT_ID_EMAIL, maybeEmptyEmail.toLowerCase());
            }
            String maybeEmptyHuoltajanEmail = henkilotiedot.get(OppijaConstants.ELEMENT_ID_HUOLTAJANSAHKOPOSTI);
            if(maybeEmptyHuoltajanEmail != null) {
                henkilotiedot.put(OppijaConstants.ELEMENT_ID_HUOLTAJANSAHKOPOSTI, maybeEmptyHuoltajanEmail.toLowerCase());
            }
        }
    }

    @JsonIgnore
    public void updateNameMetadata() {
        Map<String, String> henkilotiedot = getPhaseAnswers("henkilotiedot");
        if (henkilotiedot != null) {
            String lastName = henkilotiedot.get(OppijaConstants.ELEMENT_ID_LAST_NAME);
            String firstNames = henkilotiedot.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES);
            String callingName = henkilotiedot.get(OppijaConstants.ELEMENT_ID_NICKNAME);
            updateFullName(lastName, firstNames);
            updateSearchNames(lastName, firstNames, callingName);
        }
    }

    private void updateFullName(String lastName, String firstNames) {
        if (lastName != null) {
            fullName = lastName.toLowerCase() + " " + firstNames.toLowerCase();
        } else {
            fullName = "";
        }
        fullName = fullName.replaceAll("-", "");
    }

    private void updateSearchNames(String... names) {
        if (names == null || names.length < 1)
            return;

        for (String name : names) {
            if (name == null || name.isEmpty())
                continue;
            for (String searchName : name.split(" ")) {
                searchName = searchName.toLowerCase();
                addSearchName(searchName);
                for (String namePart : searchName.split("-")) {
                    addSearchName(namePart);
                }
            }
        }
    }

    @JsonIgnore
    public void flagStudentIdentificationDone() {
        this.studentIdentificationDone = null;
        this.automatedProcessingFailCount = null;
        this.automatedProcessingFailRetryTime = null;
    }

    @JsonIgnore
    public void flagStudentIdentificationRequired() {
        this.studentIdentificationDone = Boolean.FALSE;
        this.automatedProcessingFailCount = null;
        this.automatedProcessingFailRetryTime = null;
    }

    @JsonIgnore
    public Application modifyPersonalData(Person person) {
        Map<String, String> henkilotiedot = new HashMap<String, String>(getPhaseAnswers("henkilotiedot"));
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getFirstNames(),
                OppijaConstants.ELEMENT_ID_FIRST_NAMES);
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getLastName(),
                OppijaConstants.ELEMENT_ID_LAST_NAME);
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getNickName(),
                OppijaConstants.ELEMENT_ID_NICKNAME);
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getSocialSecurityNumber(),
                OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER);
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getSex(),
                OppijaConstants.ELEMENT_ID_SEX);
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, String.valueOf(person.getDateOfBirth()),
                OppijaConstants.ELEMENT_ID_DATE_OF_BIRTH);
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, String.valueOf(person.getLanguage()),
                OppijaConstants.ELEMENT_ID_LANGUAGE);

        Boolean eiSuomalaistaHetua = person.isNoSocialSecurityNumber();
        if (eiSuomalaistaHetua != null) {
            henkilotiedot = updateHenkilotiedotField(henkilotiedot, String.valueOf(!eiSuomalaistaHetua),
                    OppijaConstants.ELEMENT_ID_HAS_SOCIAL_SECURITY_NUMBER);
        }

        Boolean securityOrder = person.isSecurityOrder();
        if (securityOrder != null) {
            henkilotiedot = updateHenkilotiedotField(henkilotiedot, String.valueOf(securityOrder),
                    OppijaConstants.ELEMENT_ID_SECURITY_ORDER);
        }

        String personOid = person.getPersonOid();
        if (isNotEmpty(personOid)) {
            setPersonOid(personOid);
        }
        String studentOid = person.getStudentOid();
        if (isNotEmpty(studentOid)) {
            setStudentOid(studentOid);
        }

        updateNameMetadata();
        setVaiheenVastauksetAndSetPhaseId("henkilotiedot", henkilotiedot);
        return this;
    }

    private Map<String, String> updateHenkilotiedotField(Map<String, String> answers, String value, String key) {
        String oldValue = null;
        if (isNotBlank(value)) {
            oldValue = answers.put(key, value);
        } else {
            answers.remove(key);
        }
        addOverriddenAnswer(key, oldValue);

        log.debug("Changing value key: {}, value: {} -> {}", key, oldValue, value);
        return answers;
    }

    public Map<String, Map<String, String>> getAnswers() {
        return answers;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public Application setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public String getOid() {
        return oid;
    }

    public Application setOid(String oid) {
        this.oid = oid; return this;
    }

    public Application setState(State state) {
        this.state = state; return this;
    }

    public State getState() {
        return this.state;
    }

    public Application setStudentIdentificationDone(Boolean studentIdentificationDone) {
        this.studentIdentificationDone = studentIdentificationDone;
        return this;
    }

    public Boolean getStudentIdentificationDone() {
        return this.studentIdentificationDone;
    }

    public String getPhaseId() {
        return phaseId;
    }

    public Application setPhaseId(final String phaseId) {
        this.phaseId = phaseId; return this;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public AuthorizationMeta getAuthorizationMeta() {
        return authorizationMeta;
    }

    public void setAuthorizationMeta(AuthorizationMeta authorizationMeta) {
        this.authorizationMeta = authorizationMeta;
    }

    public Map<String, String> getOverriddenAnswers() {
        return Collections.unmodifiableMap(overriddenAnswers);
    }

    public boolean addOverriddenAnswer(String key, String value) {
        if (overriddenAnswers.containsKey(key)) {
            return false;
        }

        if ("Henkilotunnus".equals(key) && value != null) {
            value = "(hidden)";
        }

        if (value != null) {
            overriddenAnswers.put(key, value);
        } else {
            overriddenAnswers.put(key, "(null)");
        }
        return true;
    }


    public Application setMeta(Map<String, String> meta) {
        this.meta = meta; return this;
    }

    public String addMeta(String key, String value) {
        return this.meta.put(key, value);
    }

    public String getMetaValue(String key) {
        return this.meta.get(key);
    }

    public Application setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId; return this;
    }

    public String getPersonOid() {
        return personOid;
    }

    public Application setPersonOid(String personOid) {
        this.personOid = personOid; return this;
    }

    public Application setReceived(Date received) {
        this.received = received;
        if(this.updated == null){
            this.updated = received;
        }
        return this;
    }

    public Date getReceived() {
        return received;
    }

    public Application setUpdated(Date updated) {
        this.updated = updated; return this;
    }

    public Date getUpdated() { return updated; }

    public String getStudentOid() {
        return studentOid;
    }

    public Application setStudentOid(String studentOid) {
        this.studentOid = studentOid; return this;
    }

    public Long getLastAutomatedProcessingTime() {
        return lastAutomatedProcessingTime;
    }

    public Application setLastAutomatedProcessingTime(Long lastAutomatedProcessingTime) {
        this.lastAutomatedProcessingTime = lastAutomatedProcessingTime;
        return this;
    }

    public Integer getAutomatedProcessingFailCount() {
        return this.automatedProcessingFailCount;
    }

    public Application setAutomatedProcessingFailCount(Integer automatedProcessingFailCount) {
        this.automatedProcessingFailCount = automatedProcessingFailCount;
        return this;
    }

    public Long getAutomatedProcessingFailRetryTime() {
        return automatedProcessingFailRetryTime;
    }

    public void setAutomatedProcessingFailRetryTime(Long automatedProcessingFailRetryTime) {
        this.automatedProcessingFailRetryTime = automatedProcessingFailRetryTime;
    }

    public Application setRedoPostProcess(PostProcessingState redoPostProcess) {
        this.redoPostProcess = redoPostProcess; return this;
    }

    public PostProcessingState getRedoPostProcess() {
        return redoPostProcess;
    }

    public Application setFullname(String fullName) {
        this.fullName = fullName; return this;
    }

    public String getFullName() {
        return fullName;
    }

    public List<ApplicationNote> getNotes() {
        return notes;
    }

    public Application addNote(ApplicationNote note) {
        notes.add(0, note);
        return this;
    }

    public List<Change> getHistory() {
        return history;
    }

    public Set<String> getSearchNames() {
        return this.searchNames;
    }

    public Application addSearchName(String searchName) {
        if (searchName != null && !searchName.isEmpty()) {
            this.searchNames.add(searchName);
        }
        return this;
    }

    public Application addHistory(final Change change) {
        if (this.history == null) {
            this.history = new LinkedList<Change>();
        }
        this.history.add(0, change);
        this.version = this.history.size();
        return this;
    }

    @JsonIgnore
    public void logPersonOidIfChanged(String changedBy, String oidBefore) {
        logUserOidIfChanged("personOid", "Henkilönumero", changedBy, oidBefore, getPersonOid());
    }

    @JsonIgnore
    public void logStudentOidIfChanged(String changedBy, String oidBefore) {
        logUserOidIfChanged("studentOid", "Oppijanumero", changedBy, oidBefore, getStudentOid());
    }

    @JsonIgnore
    private void logUserOidIfChanged(String oidType, String oidText, String changedBy, String oidBefore, String oidAfter) {
        if (!Objects.equals(oidBefore, oidAfter)) {
            if(!StringUtils.isEmpty(oidBefore)) {
                addNote(new ApplicationNote(String.format("%s muuttui %s -> %s", oidText, oidBefore, oidAfter),
                        new Date(), changedBy));
            }
            addHistory(new Change(
                    new Date(), changedBy, oidType + " modified",
                    Collections.<Map<String, String>>singletonList(
                            ImmutableMap.of(oidType, oidBefore + " -> " + oidAfter)
                    )
            ));
            log.info("Application {} {} changed from value {} -> {}", getOid(), oidType, oidBefore, oidAfter);
        }
    }

    public Integer getVersion() {
        return version;
    }

    public List<PreferenceEligibility> getPreferenceEligibilities() {
        return preferenceEligibilities;
    }

    public void setPreferenceEligibilities(List<PreferenceEligibility> preferenceEligibilities) {
        this.preferenceEligibilities = new ArrayList<PreferenceEligibility>(preferenceEligibilities);
    }

    public List<ApplicationAttachmentRequest> getAttachmentRequests() {
        return attachmentRequests;
    }

    public void setAttachmentRequests(List<ApplicationAttachmentRequest> attachmentRequests) {
        this.attachmentRequests = new ArrayList<ApplicationAttachmentRequest>(attachmentRequests);
    }

    public List<PreferenceChecked> getPreferencesChecked() {
        return preferencesChecked;
    }

    public void setPreferencesChecked(List<PreferenceChecked> preferencesChecked) {
        this.preferencesChecked = preferencesChecked;
    }

    public PaymentState getRequiredPaymentState() {
        return requiredPaymentState;
    }

    public void setRequiredPaymentState(PaymentState requiredPaymentState) {
        this.requiredPaymentState = requiredPaymentState;
    }

    public Date getEligibilitiesAndAttachmentsUpdated() {
        return this.eligibilitiesAndAttachmentsUpdated;
    }

    public Application setEligibilitiesAndAttachmentsUpdated(Date eligibilitiesAndAttachmentsUpdated) {
        this.eligibilitiesAndAttachmentsUpdated = eligibilitiesAndAttachmentsUpdated; return this;
    }

    @Override
    public Application clone() {
        final Application clone = new Application(this.oid);
        clone.applicationSystemId = this.applicationSystemId;
        clone.user = this.user;
        clone.answers = cloneAnswers();
        clone.additionalInfo = Maps.newHashMap(this.additionalInfo);
        clone.lastAutomatedProcessingTime = this.lastAutomatedProcessingTime;
        clone.meta = new HashMap<>(this.meta);
        clone.personOid = this.personOid;
        clone.phaseId = this.phaseId;
        clone.received = this.received;
        clone.redoPostProcess = this.redoPostProcess;
        clone.state = this.state;
        clone.studentIdentificationDone = this.studentIdentificationDone;
        clone.studentOid = this.studentOid;
        clone.updated = this.updated;
        //TODO =RS= check if too shallow
        clone.preferenceEligibilities = new ArrayList<>(this.preferenceEligibilities);
        //TODO =RS= check if too shallow
        clone.attachmentRequests = new ArrayList<>(this.attachmentRequests);
        //TODO =RS= check if too shallow
        clone.preferencesChecked = new ArrayList<>(this.preferencesChecked);
        clone.fullName = this.fullName;
        clone.modelVersion = this.modelVersion;
        clone.version = this.version;
        clone.overriddenAnswers = new HashMap<>(this.overriddenAnswers);
        clone.searchNames = new HashSet<>(this.searchNames);
        clone.history = new ArrayList<>(this.history);
        clone.notes = new LinkedList<>(this.notes);
        clone.authorizationMeta = null == this.authorizationMeta ? null: authorizationMeta.clone();
        clone.automatedProcessingFailCount = this.automatedProcessingFailCount;
        clone.automatedProcessingFailRetryTime = this.automatedProcessingFailRetryTime;
        clone.requiredPaymentState = this.requiredPaymentState;
        clone.paymentDueDate = this.paymentDueDate;
        clone.eligibilitiesAndAttachmentsUpdated = this.eligibilitiesAndAttachmentsUpdated;
        return clone;
    }

    private Map<String, Map<String, String>> cloneAnswers() {
        Map<String, Map<String, String>> newMap = Maps.newHashMapWithExpectedSize(this.answers.size());
        for (String key : this.answers.keySet()) {
            newMap.put(key, Maps.newHashMap(this.answers.get(key)));
        }
        return newMap;
    }

    public List<ApplicationAttachmentRequest> cloneAttachmentRequests() {
        List<ApplicationAttachmentRequest> list = new ArrayList<ApplicationAttachmentRequest>();
        for(ApplicationAttachmentRequest req : this.attachmentRequests) {
            list.add(req.clone());
        }
        return list;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, null, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        return EqualsBuilder.reflectionEquals(this, o, false, null, EXCLUDED_FIELDS);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(7, 23, this, false, null, EXCLUDED_FIELDS);
    }
}
