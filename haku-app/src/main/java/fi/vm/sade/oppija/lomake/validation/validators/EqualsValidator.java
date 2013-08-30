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
import org.apache.commons.lang3.Validate;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class EqualsValidator extends FieldValidator {

    private final String validValue;

    public EqualsValidator(@JsonProperty(value = "fieldName") final String fieldName,
                           @JsonProperty(value = "errorMessage") final I18nText errorMessage,
                           @JsonProperty(value = "validValue") final String validValue) {
        super(fieldName, errorMessage);
        Validate.notNull(validValue, "Valid value can't be null");
        this.validValue = validValue;
    }

    public String getValidValue() {
        return validValue;
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        Map<String, String> values = validationInput.getValues();
        String value = values.get(fieldName);
        if (value != null) {
            if (validValue.equals(value)) {
                return validValidationResult;
            }
        }
        return invalidValidationResult;
    }
}
