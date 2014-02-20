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
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Application implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public enum State {
        ACTIVE, PASSIVE, INCOMPLETE, SUBMITTED
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
    @Deprecated
    private Long personOidChecked;
    private String studentOid;
    private Long lastAutomatedProcessingTime;
    @Deprecated
    private Long studentOidChecked;
    private Date received;
    private String redoPostProcess;

    private String fullName;
    private HashSet<String> searchNames = new HashSet<String>();

    private Map<String, Map<String, String>> answers = new HashMap<String, Map<String, String>>();
    private Map<String, String> meta = new HashMap<String, String>();
    private Map<String, String> additionalInfo = new HashMap<String, String>();
    private LinkedList<ApplicationNote> notes = new LinkedList<ApplicationNote>();

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
        addVaiheenVastaukset(phase.getPhaseId(), phase.getAnswers());
    }

    public Application(final String oid, final ApplicationPhase phase) {
        this(phase.getApplicationSystemId(), oid);
        addVaiheenVastaukset(phase.getPhaseId(), phase.getAnswers());
    }

    public Application(final String applicationSystemId, final String oid) {
        this.applicationSystemId = applicationSystemId;
        this.oid = oid;
    }

    @JsonIgnore
    public void passivate() {
        state = State.PASSIVE;
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
    public final Application addVaiheenVastaukset(final String phaseId, Map<String, String> answers) {
        this.phaseId = answers.get(VAIHE_ID);
        Map<String, String> answersWithoutPhaseId = ImmutableMap.copyOf(Maps.filterKeys(answers, Predicates.not(Predicates.equalTo(VAIHE_ID))));
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
        final Map<String, String> answers = new HashMap<String, String>();
        for (Map<String, String> phaseAnswers : this.answers.values()) {
            answers.putAll(phaseAnswers);
        }
        return answers;
    }

    @JsonIgnore
    public Map<String, String> getVastauksetMergedIgnoringPhase(final String phaseId) {
        final Map<String, String> answers = new HashMap<String, String>();
        for (String phaseKey : this.answers.keySet()) {
            if (!phaseKey.equalsIgnoreCase(phaseId)) {
                answers.putAll(this.answers.get(phaseKey));
            }
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
            return ImmutableMap.copyOf(phaseAnswers);
        }
        return new HashMap<String, String>();
    }

    @JsonIgnore
    public boolean isNew() {
        return this.phaseId == null;
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
    }

    private void updateSearchNames(String... names){
        if (names == null || names.length < 1)
            return;

        for (String name : names){
            if(name == null || name.isEmpty())
                continue;
            for (String searchName : name.split(" ")){
                searchName = searchName.toLowerCase();
                addSearchName(searchName);
                for (String namePart : searchName.split("-")){
                    addSearchName(namePart);
                }
            }
        }
    }

    @JsonIgnore
    public void studentIdentificationDone() {
        this.studentIdentificationDone = Boolean.TRUE;
    }

    @JsonIgnore
    public void flagStudentIdentificationRequired() {
        this.studentIdentificationDone = Boolean.FALSE;
    }

    @JsonIgnore
    public boolean isStudentIdentificationDone() {
        return null != this.studentIdentificationDone ? this.studentIdentificationDone: true;
    }

    @JsonIgnore
    public Application modifyPersonalData(Person person) {
        Map<String, String> henkilotiedot = new HashMap<String, String>(getPhaseAnswers("henkilotiedot"));
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getFirstNames(),
                OppijaConstants.ELEMENT_ID_FIRST_NAMES,OppijaConstants.ELEMENT_ID_FIRST_NAMES_USER);
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getLastName(),
                OppijaConstants.ELEMENT_ID_LAST_NAME,OppijaConstants.ELEMENT_ID_LAST_NAME_USER);
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getNickName(),
                OppijaConstants.ELEMENT_ID_NICKNAME,OppijaConstants.ELEMENT_ID_NICKNAME_USER);
        updateNameMetadata();

        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getContactLanguage(),
                OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE, OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE_USER);
        henkilotiedot = updateHenkilotiedotField(henkilotiedot, person.getHomeCity(),
                OppijaConstants.ELEMENT_ID_HOME_CITY, OppijaConstants.ELEMENT_ID_HOME_CITY_USER);

        String personOid = person.getPersonOid();
        if (isNotEmpty(personOid)) {
            setPersonOid(personOid);
        }
        String studentOid = person.getStudentOid();
        if (isNotEmpty(studentOid)) {
            setStudentOid(studentOid);
        }

        addVaiheenVastaukset("henkilotiedot", henkilotiedot);
        return this;
    }

    private Map<String, String> updateHenkilotiedotField(Map<String, String> henkilotiedot, String newValue, String field, String fieldUser) {
        if (newValue == null) {
            return henkilotiedot;
        }

        String value = henkilotiedot.get(field);
        String valueByUser = henkilotiedot.get(fieldUser);

        if (valueByUser == null) {
            valueByUser = value;
            henkilotiedot.put(fieldUser, valueByUser);
        }
        value = newValue;

        henkilotiedot.put(field, value);

        return henkilotiedot;

    }

    public Map<String, Map<String, String>> getAnswers() {
        return answers;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

    public void setStudentIdentificationDone(Boolean studentIdentificationDone) {this.studentIdentificationDone = studentIdentificationDone;}

    public Boolean getStudentIdentificationDone() {return this.studentIdentificationDone;}

    public String getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(final String phaseId) {
        this.phaseId = phaseId;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public String getPersonOid() {
        return personOid;
    }

    public void setPersonOid(String personOid) {
        this.personOid = personOid;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public Date getReceived() {
        return received;
    }

    public String getStudentOid() {
        return studentOid;
    }

    public void setStudentOid(String studentOid) {
        this.studentOid = studentOid;
    }

    public Long getLastAutomatedProcessingTime() {
        return lastAutomatedProcessingTime;
    }

    public void setLastAutomatedProcessingTime(Long lastAutomatedProcessingTime) {
        this.lastAutomatedProcessingTime = lastAutomatedProcessingTime;
    }

    @Deprecated
    public Long getPersonOidChecked() {
        return personOidChecked;
    }

    @Deprecated
    public void setPersonOidChecked(Long personOidChecked) {
        this.personOidChecked = personOidChecked;
    }

    @Deprecated
    public Long getStudentOidChecked() {
        return studentOidChecked;
    }

    @Deprecated
    public void setStudentOidChecked(Long studentOidChecked) {
        this.studentOidChecked = studentOidChecked;
    }

    public void setRedoPostProcess(String redoPostProcess) {
        this.redoPostProcess = redoPostProcess;
    }

    public String getRedoPostProcess() {
        return redoPostProcess;
    }

    public void setFullname(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public List<ApplicationNote> getNotes() {
        return notes;
    }

    public void addNote(ApplicationNote note) {
        notes.add(0, note);
    }

    public Set<String> getSearchNames(){
        return this.searchNames;
    }

    public void addSearchName(String searchName){
        if (searchName != null && !searchName.isEmpty()){
            this.searchNames.add(searchName);
        }
    }

}
