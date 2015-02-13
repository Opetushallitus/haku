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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ContainedInOtherFieldValidator extends FieldValidator {

    private final String otherFieldName;

    public ContainedInOtherFieldValidator(final String otherFieldName,
                                          final String errorMessageKey) {
        super(errorMessageKey);
        Validate.notNull(otherFieldName, "'otherFieldName' can't be null");
        this.otherFieldName = otherFieldName;
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        String otherValue = StringUtils.trim(validationInput.getValueByKey(otherFieldName));
        String thisValue = StringUtils.trim(validationInput.getValue());

        if (isBlank(otherValue) && isBlank(thisValue)) {
            return validValidationResult;
        }

        if (isNotBlank(otherValue) ^ isNotBlank(thisValue)) {
            return getInvalidValidationResult(validationInput);
        }

        for (String part : otherValue.split("[ ]")) {
            if (thisValue.equalsIgnoreCase(part)) {
                return validValidationResult;
            }
        }
        for (String part : otherValue.split("[ -]")) {
            if (thisValue.equalsIgnoreCase(part)) {
                return validValidationResult;
            }
        }

        return getInvalidValidationResult(validationInput);
    }
}
