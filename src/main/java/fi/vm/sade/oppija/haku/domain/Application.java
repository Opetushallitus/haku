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
import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:48 PM}
 * @since 1.1
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Application implements Serializable {

    public static final String OID = "oid";
    public static final String VAIHE_ID = "vaiheId";

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    private org.bson.types.ObjectId id;


    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonProperty(value = OID)
    private String oid;

    private static final long serialVersionUID = -7491168801255850954L;

    private HakuLomakeId hakuLomakeId;
    private final User user;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "vaiheId")
    private String vaiheId;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Map<String, Map<String, String>> vastaukset = new HashMap<String, Map<String, String>>();

    @JsonCreator
    public Application(@JsonProperty(value = "hakuLomakeId") final HakuLomakeId hakuLomakeId,
                       @JsonProperty(value = "user") final User user,
                       @JsonProperty(value = "vastaukset") Map<String, Map<String, String>> vastaukset) {
        this(hakuLomakeId, user);
        if (vastaukset != null) {
            this.vastaukset = vastaukset;
        }
    }

    @JsonIgnore
    public Application(@JsonProperty(value = "hakuLomakeId") final HakuLomakeId hakuLomakeId,
                       @JsonProperty(value = "user") final User user) {
        this.hakuLomakeId = hakuLomakeId;
        this.user = user;
    }

    public Application(User user, VaiheenVastaukset vaihe) {
        this(vaihe.getHakuLomakeId(), user);
        addVaiheenVastaukset(vaihe.getVaiheId(), vaihe.getVastaukset());
    }

    public Application addVaiheenVastaukset(final String vaiheId, Map<String, String> vastaukset) {
        this.vaiheId = vastaukset.remove(VAIHE_ID);
        this.vastaukset.put(vaiheId, vastaukset);
        return this;
    }

    public User getUser() {
        return user;
    }

    public HakuLomakeId getHakuLomakeId() {
        return hakuLomakeId;
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

    public String getVaiheId() {
        return vaiheId;
    }

    public void setVaiheId(final String vaiheId) {
        this.vaiheId = vaiheId;
    }

    @JsonIgnore
    public boolean isNew() {
        return this.vaiheId == null;
    }
}
