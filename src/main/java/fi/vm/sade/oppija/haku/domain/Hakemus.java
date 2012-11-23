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

package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:48 PM}
 * @since 1.1
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Hakemus implements Serializable {

    public static final String STATEKEY = "state";
    public static final String OID = "oid";

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    private org.bson.types.ObjectId id;

    public enum State {
        LUONNOS, VIREILLÄ
    }

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonProperty(value = OID)
    private String oid;

    private static final long serialVersionUID = -7491168801255850954L;

    private HakuLomakeId hakuLomakeId;
    private final User user;

    @JsonProperty(value = STATEKEY)
    private State state = State.LUONNOS;

    private final Map<String, String> meta = new HashMap<String, String>();
    private final Map<String, Map<String, String>> vastaukset = new HashMap<String, Map<String, String>>();

    @JsonCreator
    public Hakemus(@JsonProperty(value = "hakuLomakeId") final HakuLomakeId hakuLomakeId, @JsonProperty(value = "user") final User user, @JsonProperty(value = "vastaukset") Map<String, Map<String, String>> vastaukset) {
        this(hakuLomakeId, user);
        this.vastaukset.putAll(vastaukset);
    }

    @JsonIgnore
    public Hakemus(@JsonProperty(value = "hakuLomakeId") final HakuLomakeId hakuLomakeId, @JsonProperty(value = "user") final User user) {
        this.hakuLomakeId = hakuLomakeId;
        this.user = user;
    }

    public Hakemus(User user, VaiheenVastaukset vaihe) {
        this(vaihe.getHakuLomakeId(), user);
        addVaiheenVastaukset(vaihe.getVaiheId(), vaihe.getVastaukset());
    }

    public Hakemus addMeta(final Map<String, String> meta) {
        this.meta.putAll(meta);
        return this;
    }

    public Hakemus addMeta(final String name, final String value) {
        this.meta.put(name, value);
        return this;
    }


    public Hakemus addVaiheenVastaukset(final String vaiheId, Map<String, String> vastaukset) {
        this.vastaukset.put(vaiheId, vastaukset);
        return this;
    }

    public User getUser() {
        return user;
    }

    public HakuLomakeId getHakuLomakeId() {
        return hakuLomakeId;
    }

    public Map<String, String> getMeta() {
        return Collections.unmodifiableMap(meta);
    }

    @JsonIgnore
    public Map<String, String> getVastauksetMerged() {
        final Map<String, String> vastaukset = new HashMap<String, String>();
        for (Map<String, String> vaiheenVastaukset : this.vastaukset.values()) {
            vastaukset.putAll(vaiheenVastaukset);
        }
        return vastaukset;
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

    public void setStateVireilla() {
        this.state = State.VIREILLÄ;
    }

    public State getState() {
        return state;
    }
}
