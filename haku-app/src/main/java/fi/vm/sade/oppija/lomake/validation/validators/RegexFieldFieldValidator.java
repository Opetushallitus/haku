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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;

import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import fi.vm.sade.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;

public class RegexFieldFieldValidator extends FieldValidator {

    final Pattern pattern;

    public RegexFieldFieldValidator(final String fieldName, final String pattern) {
        this(fieldName, "Virheellinen syöte " + pattern, pattern);
    }

    public RegexFieldFieldValidator(final String fieldName, final String errorMessage, final String pattern) {
        super(fieldName, errorMessage);
        Validate.notNull(pattern, "Pattern can't be null");
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {
        String value = values.get(fieldName);
        ValidationResult validationResult = new ValidationResult();
        if (value != null) {
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                validationResult = new ValidationResult(fieldName, 
                		ElementUtil.createI18NTextError(errorMessage));
            }
        }
        return validationResult;
    }
}
