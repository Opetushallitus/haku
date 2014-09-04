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

import fi.vm.sade.haku.oppija.lomake.domain.ObjectIdDeserializer;
import fi.vm.sade.haku.oppija.lomake.domain.ObjectIdSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class AuthorizationMeta implements Serializable {
    private Boolean opoAllowed;
    private Map<String, Set<String>> aoOrganizations;
    private Set<String> allAoOrganizations;
    private Set<String> sendingSchool;

    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private org.bson.types.ObjectId id; //NOSONAR Json-sarjallistajan käyttämä.

    @JsonCreator
    public AuthorizationMeta() {
    }

    public Boolean isOpoAllowed() {
        return opoAllowed;
    }

    public void setOpoAllowed(Boolean opoAllowed) {
        this.opoAllowed = opoAllowed;
    }

    public Map<String, Set<String>> getAoOrganizations() {
        return aoOrganizations;
    }

    public void setAoOrganizations(Map<String, Set<String>> aoOrganizations) {
        this.aoOrganizations = aoOrganizations;
    }

    public Set<String> getSendingSchool() {
        return sendingSchool;
    }

    public void setSendingSchool(Set<String> sendingSchool) {
        this.sendingSchool = sendingSchool;
    }

    public Set<String> getAllAoOrganizations() {
        return allAoOrganizations;
    }

    public void setAllAoOrganizations(Set<String> allAoOrganizations) {
        this.allAoOrganizations = allAoOrganizations;
    }
}
