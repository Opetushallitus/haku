/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author majapuro
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationAdditionalDataDTO implements Serializable {
    private String oid;
    private String personOid;
    private String firstNames;
    private String lastName;
    private Map<String, String> additionalData = new HashMap<String, String>();

    public ApplicationAdditionalDataDTO() {}

    @JsonCreator
    public ApplicationAdditionalDataDTO(@JsonProperty(value = "oid") String oid,
                                        @JsonProperty(value = "personOid") String personOid,
                                        @JsonProperty(value = "firstNames") String firstNames,
                                        @JsonProperty(value = "lastName") String lastName,
                                        @JsonProperty(value = "additionalData") Map<String, String> additionalData) {
        this.oid = oid;
        this.personOid = personOid;
        this.firstNames = firstNames;
        this.lastName = lastName;
        this.additionalData = additionalData;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getPersonOid() {
        return personOid;
    }

    public void setPersonOid(String personOid) {
        this.personOid = personOid;
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

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, String> additionalData) {
        this.additionalData = additionalData;
    }
}
