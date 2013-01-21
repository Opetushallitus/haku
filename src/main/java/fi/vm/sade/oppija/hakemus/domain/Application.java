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

import fi.vm.sade.oppija.lomake.domain.FormId;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:48 PM}
 * @since 1.1
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Application implements Serializable {

    private static final long serialVersionUID = -7491168801255850954L;
    public static final String VAIHE_ID = "vaiheId";


    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private org.bson.types.ObjectId id;

    private String oid;
    private FormId formId;
    private User user;
    private String vaiheId;

    private Map<String, Map<String, String>> vastaukset = new HashMap<String, Map<String, String>>();
    private Map<String, String> meta = new HashMap<String, String>();

    @JsonCreator
    public Application(@JsonProperty(value = "hakuLomakeId") final FormId formId,
                       @JsonProperty(value = "user") final User user,
                       @JsonProperty(value = "vastaukset") Map<String, Map<String, String>> vastaukset) {
        this(formId, user);
        if (vastaukset != null) {
            this.vastaukset = vastaukset;
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
    public Application(@JsonProperty(value = "hakuLomakeId") final FormId formId,
                       @JsonProperty(value = "user") final User user) {
        this.formId = formId;
        this.user = user;
    }

    @JsonIgnore
    public Application(@JsonProperty(value = "hakuLomakeId") final FormId formId,
                       @JsonProperty(value = "user") final User user,
                       final String oid) {
        this.formId = formId;
        this.user = user;
        this.oid = oid;
    }

    public Application(User user, ApplicationPhase vaihe) {
        this(vaihe.getFormId(), user);
        addVaiheenVastaukset(vaihe.getVaiheId(), vaihe.getVastaukset());
    }

    public Application(String oid, ApplicationPhase vaihe) {
        this(vaihe.getFormId(), oid);
        addVaiheenVastaukset(vaihe.getVaiheId(), vaihe.getVastaukset());
    }

    public Application(final FormId formId, final String oid) {
        this.formId = formId;
        this.oid = oid;
    }


    public Application addVaiheenVastaukset(final String vaiheId, Map<String, String> vastaukset) {
        this.vaiheId = vastaukset.remove(VAIHE_ID);
        this.vastaukset.put(vaiheId, vastaukset);
        return this;
    }

    public User getUser() {
        return user;
    }

    public FormId getFormId() {
        return formId;
    }

    @JsonIgnore
    public Map<String, String> getVastauksetMerged() {
        final Map<String, String> vastaukset = new HashMap<String, String>();
        for (Map<String, String> vaiheenVastaukset : this.vastaukset.values()) {
            vastaukset.putAll(vaiheenVastaukset);
        }
        return vastaukset;
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

    @JsonIgnore
    public boolean isNew() {
        return this.vaiheId == null;
    }

    public Map<String, Map<String, String>> getVastaukset() {
        return vastaukset;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getVaiheId() {
        return vaiheId;
    }

    public void setVaiheId(final String vaiheId) {
        this.vaiheId = vaiheId;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public void setFormId(FormId formId) {
        this.formId = formId;
    }
}
