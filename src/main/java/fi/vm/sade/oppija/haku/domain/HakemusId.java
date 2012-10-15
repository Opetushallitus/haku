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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;


/**
 * @author jukka
 * @version 9/26/123:02 PM}
 * @since 1.1
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class HakemusId implements Serializable {

    private static final long serialVersionUID = 8484849312020479901L;
    public static final int HASH_CODE_MAGIC = 31;

    private final String applicationPeriodId;
    private final String formId;
    private final String categoryId;

    public HakemusId(@JsonProperty(value = "applicationPeriodId") String applicationPeriodId, @JsonProperty(value = "formId") String formId, @JsonProperty(value = "categoryId") String categoryId) {
        this.applicationPeriodId = applicationPeriodId;
        this.formId = formId;
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }


    public String getFormId() {
        return formId;
    }

    public String getApplicationPeriodId() {
        return applicationPeriodId;
    }


    public String asKey() {
        return applicationPeriodId + '_' + formId + "_" + categoryId;
    }

    public static HakemusId fromKey(String key) {
        final String[] split = key.split("_");
        return new HakemusId(split[0], split[1], split[2]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HakemusId hakemusId = (HakemusId) o;

        if (applicationPeriodId != null ? !applicationPeriodId.equals(hakemusId.applicationPeriodId) : hakemusId.applicationPeriodId != null)
            return false;
        if (categoryId != null ? !categoryId.equals(hakemusId.categoryId) : hakemusId.categoryId != null) return false;
        if (formId != null ? !formId.equals(hakemusId.formId) : hakemusId.formId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = applicationPeriodId != null ? applicationPeriodId.hashCode() : 0;
        result = 31 * result + (formId != null ? formId.hashCode() : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        return result;
    }
}
