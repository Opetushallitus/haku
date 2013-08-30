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

package fi.vm.sade.oppija.lomake.validation.validators;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class ContainedInOtherFieldValidator extends FieldValidator {

    private final String otherFieldName;

    public ContainedInOtherFieldValidator(@JsonProperty(value = "fieldName") final String fieldName,
                                          @JsonProperty(value = "otherFieldName") final String otherFieldName,
                                          @JsonProperty(value = "errorMessage") final I18nText errorMessage) {
        super(fieldName, errorMessage);
        this.otherFieldName = otherFieldName;
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        Map<String, String> values = validationInput.getValues();
        String otherValue = values.get(otherFieldName);
        String thisValue = values.get(fieldName);
        if (otherValue == null || thisValue == null || !otherValue.trim().toLowerCase().contains(thisValue.trim().toLowerCase())) {
            return invalidValidationResult;
        }
        return validValidationResult;
    }

    public String getOtherFieldName() {
        return otherFieldName;
    }
}
