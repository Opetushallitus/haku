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

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Transient;

import java.util.regex.Pattern;

public class RegexFieldValidator extends FieldValidator {

    private final String pattern;
    @Transient
    private final Pattern compiledPattern;

    public RegexFieldValidator(final String errorMessageKey, final String pattern) {
        super(errorMessageKey);
        Validate.notNull(pattern, "Pattern can't be null");
        this.pattern = pattern;
        this.compiledPattern = Pattern.compile(this.pattern);
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        String value = validationInput.getValue();
        if (value != null && !compiledPattern.matcher(value).matches()) {
            return getInvalidValidationResult(validationInput);
        }
        return validValidationResult;
    }

    public String getPattern() {
        return pattern;
    }
}
