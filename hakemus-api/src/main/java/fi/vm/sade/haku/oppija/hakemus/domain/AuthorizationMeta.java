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

import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.ObjectIdDeserializer;
import fi.vm.sade.haku.oppija.lomake.domain.ObjectIdSerializer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class AuthorizationMeta implements Serializable {

    private static final String[] EXCLUDED_FIELDS = new String[]{"id"};

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

    @Override
    public AuthorizationMeta clone() {
        final AuthorizationMeta clone = new AuthorizationMeta();
        clone.opoAllowed = this.opoAllowed;
        clone.allAoOrganizations = null == this.allAoOrganizations ? null : new HashSet<>(this.allAoOrganizations);
        clone.sendingSchool = null == sendingSchool ? null : new HashSet<>(this.sendingSchool);
        clone.aoOrganizations = cloneAoOrganizations();
        return clone;
    }

    private Map<String, Set<String>> cloneAoOrganizations() {
        if (null == this.aoOrganizations)
            return null;
        final Map<String, Set<String>> newMap = Maps.newHashMapWithExpectedSize(this.aoOrganizations.size());
        for (String key : this.aoOrganizations.keySet()) {
            newMap.put(key, new HashSet<>(this.aoOrganizations.get(key)));
        }
        return newMap;
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
