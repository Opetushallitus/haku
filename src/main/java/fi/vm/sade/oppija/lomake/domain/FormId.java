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

import org.apache.commons.lang3.Validate;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.io.Serializable;


/**
 * @author jukka
 * @version 9/26/123:02 PM}
 * @since 1.1
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class FormId implements Serializable {

    private static final long serialVersionUID = 8484849312020479901L;
    private static final int HASH_CODE_MAGIC = 31;

    private final String applicationPeriodId;
    private final String formId;

    public FormId(@JsonProperty(value = "applicationPeriodId") String applicationPeriodId,
                  @JsonProperty(value = "formId") String formId) {
        Validate.notNull(applicationPeriodId, "ApplicationPeriodId can't be null");
        Validate.notNull(formId, "FormId can't be null");
        this.applicationPeriodId = applicationPeriodId;
        this.formId = formId;
    }

    public String getFormId() {
        return formId;
    }

    public String getApplicationPeriodId() {
        return applicationPeriodId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FormId formId = (FormId) o;

        if (applicationPeriodId != null ? !applicationPeriodId.equals(formId.applicationPeriodId) : formId.applicationPeriodId != null) {
            return false;
        }
        if (this.formId != null ? !this.formId.equals(formId.formId) : formId.formId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = applicationPeriodId != null ? applicationPeriodId.hashCode() : 0;
        return HASH_CODE_MAGIC * result + (formId != null ? formId.hashCode() : 0);
    }
}
