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

package fi.vm.sade.oppija.lomake.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:25 AM}
 * @since 1.1
 */
public class FormModel implements Serializable {


    private static final long serialVersionUID = -530066716898062722L;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    private org.bson.types.ObjectId id;

    final Map<String, ApplicationPeriod> applicationPerioidMap;

    public FormModel() {
        this.applicationPerioidMap = new HashMap<String, ApplicationPeriod>();
    }

    public ApplicationPeriod getApplicationPeriodById(String id) {
        return applicationPerioidMap.get(id);
    }

    public void addApplicationPeriod(ApplicationPeriod applicationPeriod) {
        applicationPerioidMap.put(applicationPeriod.getId(), applicationPeriod);
    }

    public Map<String, ApplicationPeriod> getApplicationPerioidMap() {
        return applicationPerioidMap;
    }

    public org.bson.types.ObjectId getId() {
        return id;
    }

}
