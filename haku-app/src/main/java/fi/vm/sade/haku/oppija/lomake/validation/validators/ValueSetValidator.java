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

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ValueSetValidator extends FieldValidator {

    private static Logger log = LoggerFactory.getLogger(ValueSetValidator.class);

    private final List<String> validValues;

    public ValueSetValidator(final String fieldName,
                             final I18nText errorMessage,
                             final List<String> validValues) {
        super(fieldName, errorMessage);
        this.validValues = ImmutableList.copyOf(validValues);
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        ValidationResult validationResult = new ValidationResult();
        String value = validationInput.getValues().get(fieldName);
        if (value != null && !this.validValues.contains(value)) {
            validationResult = new ValidationResult(fieldName, getErrorMessage());
        }
        return validationResult;
    }

    public List<String> getValidValues() {
        return new ArrayList<String>(validValues);
    }
}
