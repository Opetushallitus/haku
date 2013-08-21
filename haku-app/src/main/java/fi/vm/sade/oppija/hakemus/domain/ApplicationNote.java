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

import fi.vm.sade.oppija.lomake.domain.ObjectIdDeserializer;
import fi.vm.sade.oppija.lomake.domain.ObjectIdSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class ApplicationNote implements Serializable {
    private String noteText;
    private Date added;
    private String user;

    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private org.bson.types.ObjectId id; //NOSONAR Json-sarjallistajan käyttämä.

    @JsonCreator
    public ApplicationNote(@JsonProperty(value = "noteText") final String noteText,
                       @JsonProperty(value = "added") final Date added,
                       @JsonProperty(value = "user") final String user) {
        this.noteText = noteText;
        this.added = added;
        this.user = user;
    }

    public String getNoteText() {
        return noteText;
    }

    public Date getAdded() {
        return added;
    }

    public String getUser() {
        return user;
    }
}
