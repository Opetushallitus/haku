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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.apache.commons.lang3.StringUtils;

public class YearValidator extends FieldValidator {

    private final Integer fromYear;
    private final Integer toYear;
    private final boolean allowEmpty;

    private static final I18nText EMPTY_NOT_ALLOWED = ElementUtil.createI18NText("yearValidator.emptyNotAllowed");
    private static final I18nText NOT_A_NUMBER = ElementUtil.createI18NText("yearValidator.notANumber");
    private static final I18nText TOO_EARLY = ElementUtil.createI18NText("yearValidator.tooEarly");
    private static final I18nText TOO_LATE = ElementUtil.createI18NText("yearValidator.tooLate");

    public YearValidator(final Integer fromYear, final Integer toYear,
                         final boolean allowEmpty) {
        super(ElementUtil.createI18NAsIs("Generic error message"));
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.allowEmpty = allowEmpty;
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        String value = validationInput.getValue();
        if (StringUtils.isBlank(value)) {
            if (allowEmpty) {
                return new ValidationResult();
            } else {
                return new ValidationResult(validationInput.getFieldName(), EMPTY_NOT_ALLOWED);
            }
        }
        int year;
        try {
            year = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return new ValidationResult(validationInput.getFieldName(), NOT_A_NUMBER);
        }
        if (fromYear != null && fromYear > year) {
            return new ValidationResult(validationInput.getFieldName(), TOO_EARLY);
        }
        if (toYear != null && toYear < year) {
            return new ValidationResult(validationInput.getFieldName(), TOO_LATE);
        }
        return new ValidationResult();
    }
}
