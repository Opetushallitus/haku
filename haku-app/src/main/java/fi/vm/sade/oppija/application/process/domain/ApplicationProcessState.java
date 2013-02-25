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

package fi.vm.sade.oppija.application.process.domain;

import fi.vm.sade.oppija.lomake.domain.ObjectIdDeserializer;
import fi.vm.sade.oppija.lomake.domain.ObjectIdSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * @author Mikko Majapuro
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class ApplicationProcessState implements Serializable {

    @JsonProperty(value = "_id")
    @JsonSerialize(using = ObjectIdSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private org.bson.types.ObjectId id; //NOSONAR Json-sarjallistajan käyttämä.

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String oid;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String status;

    @JsonCreator
    public ApplicationProcessState(@JsonProperty("oid") final String oid, @JsonProperty("status") final String status) {
        this.oid = oid;
        this.status = status;
    }

    public String getOid() {
        return oid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
