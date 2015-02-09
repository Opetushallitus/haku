/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class PastDateValidator extends FieldValidator {

    public static final String DATE_OF_BIRTH_FORMAT = "dd.MM.yyyy";

    public PastDateValidator(final String errorMessageKey) {
        super(errorMessageKey);
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        String dateOfBirthString = validationInput.getValue();
        ValidationResult result = null;
        try {
            Date dateOfBirth = (new SimpleDateFormat(DATE_OF_BIRTH_FORMAT)).parse(dateOfBirthString);
            if (dateOfBirth.before(new Date())) {
                result = this.validValidationResult;
            }
        } catch (Exception e) {
        }

        if (result == null) {
            result = getInvalidValidationResult(validationInput);
        }
        return result;
    }
}
