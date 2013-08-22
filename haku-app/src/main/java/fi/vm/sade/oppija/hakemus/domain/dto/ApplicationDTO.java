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

package fi.vm.sade.oppija.hakemus.domain.dto;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.hakemus.domain.Application;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collections;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class ApplicationDTO {

    private String oid;
    private String personOid;
    private String asId;
    private Map<String, String> answers;
    private Map<String, String> additionalInfo;

    @JsonCreator
    public ApplicationDTO(@JsonProperty(value = "oid") final String oid,
                          @JsonProperty(value = "personOid") final String personOid,
                          @JsonProperty(value = "asId") final String asId,
                          @JsonProperty(value = "answers") Map<String, String> answers,
                          @JsonProperty(value = "additionalInfo") Map<String, String> additionalInfo) {
        this.oid = oid;
        this.personOid = personOid;
        this.asId = asId;
        this.answers = getStringStringImmutableMap(answers);
        this.additionalInfo = getStringStringImmutableMap(additionalInfo);
    }

    private ImmutableMap<String, String> getStringStringImmutableMap(Map<String, String> map) {
        return ImmutableMap.copyOf(map == null ? Collections.<String, String>emptyMap() : map);
    }


    public ApplicationDTO(Application application) {
        this(application.getOid(),
                application.getPersonOid(),
                application.getApplicationSystemId(),
                application.getVastauksetMerged(),
                application.getAdditionalInfo());
    }

    public String getAsId() {
        return asId;
    }

    public String getOid() {
        return oid;
    }

    public String getPersonOid() {
        return personOid;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
}
