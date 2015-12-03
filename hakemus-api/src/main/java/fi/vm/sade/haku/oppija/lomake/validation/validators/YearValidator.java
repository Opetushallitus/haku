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
import org.apache.commons.lang3.StringUtils;

public class YearValidator extends FieldValidator {

    private final Integer fromYear;
    private final Integer toYear;

    private static final String NOT_A_NUMBER_ERROR_KEY = "yearValidator.notANumber";
    private static final String TOO_EARLY_ERROR_KEY = "yearValidator.tooEarly";
    private String tooLateErrorKey;

    public YearValidator(final Integer fromYear, final Integer toYear) {
        super("yleinen.virheellinenarvo");
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.tooLateErrorKey = "yearValidator.tooLate";
    }

    public void setTooLateErrorKey(String key) {
        this.tooLateErrorKey = key;
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        String value = validationInput.getValue();
        if (StringUtils.isBlank(value)) {
            return new ValidationResult();
        }
        int year;
        try {
            year = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return new ValidationResult(validationInput.getFieldName(),  getI18Text(NOT_A_NUMBER_ERROR_KEY, validationInput.getApplicationSystemId()));
        }
        if (fromYear != null && fromYear > year) {
            return new ValidationResult(validationInput.getFieldName(),  getI18Text(TOO_EARLY_ERROR_KEY, validationInput.getApplicationSystemId()));
        }
        if (toYear != null && toYear < year) {
            return new ValidationResult(validationInput.getFieldName(),  getI18Text(tooLateErrorKey, validationInput.getApplicationSystemId()));
        }
        return new ValidationResult();
    }
}
