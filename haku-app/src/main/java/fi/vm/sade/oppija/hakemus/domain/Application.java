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

package fi.vm.sade.oppija.hakemus.domain;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fi.vm.sade.oppija.lomake.domain.ObjectIdDeserializer;
import fi.vm.sade.oppija.lomake.domain.ObjectIdSerializer;
import fi.vm.sade.oppija.lomake.domain.User;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.*;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Application implements Serializable {

    public enum State {
        ACTIVE, PASSIVE, INCOMPLETE
    }

    private static final long serialVersionUID = -7491168801255850954L;
    public static final String VAIHE_ID = "phaseId";

    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private org.bson.types.ObjectId id; //NOSONAR Json-sarjallistajan käyttämä.

    private String oid;
    private State state;
    private String applicationSystemId;
    private User user;
    private String phaseId;
    private String personOid;
    private Date received;

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

    // final, koska kutsutaan konstruktorista
    public final Application addVaiheenVastaukset(final String phaseId, Map<String, String> answers) {
        this.phaseId = answers.get(VAIHE_ID);
        Map<String, String> answersWithoutPhaseId = Maps.filterKeys(answers, Predicates.not(Predicates.equalTo(VAIHE_ID)));
        this.answers.put(phaseId, answersWithoutPhaseId);
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
        return ImmutableMap.copyOf(this.answers.get(phaseId));
    }

    @JsonIgnore
    public boolean isNew() {
        return this.phaseId == null;
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

    public List<ApplicationNote> getNotes() {
        return notes;
    }

    public void addNote(ApplicationNote note) {
        notes.add(0, note);
    }
}
