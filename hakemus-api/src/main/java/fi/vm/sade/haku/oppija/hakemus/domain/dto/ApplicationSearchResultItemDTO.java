/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Mikko Majapuro
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationSearchResultItemDTO implements Serializable {

    private String oid;
    private Application.State state;
    private Date received;
    private String firstNames;
    private String lastName;
    private String ssn;
    private String personOid;

    @JsonCreator
    public ApplicationSearchResultItemDTO(@JsonProperty(value = "oid") final String oid,
                                          @JsonProperty(value = "state") final Application.State state,
                                          @JsonProperty(value = "received") final Date received,
                                          @JsonProperty(value = "firstNames") final String firstNames,
                                          @JsonProperty(value = "lastName") final String lastName,
                                          @JsonProperty(value = "ssn") final String ssn,
                                          @JsonProperty(value = "personOid") final String personOid) {
        this.oid = oid;
        this.state = state;
        this.received = received;
        this.firstNames = firstNames;
        this.lastName = lastName;
        this.ssn = ssn;
        this.personOid = personOid;
    }

    @JsonIgnore
    public ApplicationSearchResultItemDTO() {

    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Application.State getState() {
        return state;
    }

    public void setState(Application.State state) {
        this.state = state;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getPersonOid() {
        return personOid;
    }

    public void setPersonOid(String personOid) {
        this.personOid = personOid;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
