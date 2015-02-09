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

package fi.vm.sade.haku.oppija.lomake.validation.validators;

import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.apache.commons.lang3.Validate;

public class LengthValidator extends FieldValidator {

    private final int length;

    public LengthValidator(final String errorMessageKey, int length) {
        super(errorMessageKey);
        Validate.isTrue(length >= 0, "Length must be greater than or equal to 0");
        this.length = length;
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        ValidationResult validationResult = new ValidationResult();
        String value = validationInput.getValue();
        if (value != null && value.length() > this.length) {
            validationResult = getInvalidValidationResult(validationInput);
        }
        return validationResult;
    }
}
