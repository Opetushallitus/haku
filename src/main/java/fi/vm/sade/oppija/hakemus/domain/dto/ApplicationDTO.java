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

import fi.vm.sade.oppija.hakemus.domain.Application;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class ApplicationDTO {

    private String oid;
    private String asId;
    private Map<String, String> answers;
    private Map<String, String> additionalInfo;

    @JsonCreator
    public ApplicationDTO(@JsonProperty(value = "oid") final String oid,
                          @JsonProperty(value = "asId") final String asId,
                          @JsonProperty(value = "answers") Map<String, String> answers,
                          @JsonProperty(value = "additionalInfo") Map<String, String> additionalInfo) {
        this.oid = oid;
        this.asId = asId;
        this.answers = answers;
        this.additionalInfo = additionalInfo;
    }


    public ApplicationDTO(Application application) {
        this.oid = application.getOid();
        this.asId = application.getFormId().getApplicationPeriodId();
        this.answers = application.getVastauksetMerged();
        this.additionalInfo = null;
    }

    public String getAsId() {
        return asId;
    }

    public void setAsId(String asId) {
        this.asId = asId;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOid() {
        return oid;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
}
