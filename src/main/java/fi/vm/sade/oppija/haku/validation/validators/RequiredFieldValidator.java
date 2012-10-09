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

package fi.vm.sade.oppija.haku.validation.validators;

import fi.vm.sade.oppija.haku.validation.ValidationResult;
import fi.vm.sade.oppija.haku.validation.Validator;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class RequiredFieldValidator extends Validator {


    public RequiredFieldValidator(String fieldName) {
        super(fieldName, fieldName + " on pakollinen kenttä");
    }

    public RequiredFieldValidator(final String fieldName, final String errorMessage) {
        super(fieldName, errorMessage);
    }

    @Override
    public ValidationResult validate(final Map<String, String> values) {
        ValidationResult validationResult = new ValidationResult();
        if (values == null || StringUtils.isBlank(values.get(fieldName))) {
            validationResult = new ValidationResult(fieldName, errorMessage);
        }
        return validationResult;
    }
}
