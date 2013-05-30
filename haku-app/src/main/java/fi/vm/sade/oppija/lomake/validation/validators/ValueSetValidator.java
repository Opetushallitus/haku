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

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;

import java.util.List;
import java.util.Map;

public class ValueSetValidator extends FieldValidator {
    private final List<String> validValues;

    public ValueSetValidator(final String fieldName, final String errorMessage, final List<String> validValues) {
        super(fieldName, errorMessage);
        this.validValues = ImmutableList.copyOf(validValues);
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {
        ValidationResult validationResult = new ValidationResult();
        String value = values.get(fieldName);
        if (value != null && !this.validValues.contains(value)) {
            validationResult = new ValidationResult(fieldName, ElementUtil.createI18NTextError(errorMessage));
        }
        return validationResult;
    }
}
