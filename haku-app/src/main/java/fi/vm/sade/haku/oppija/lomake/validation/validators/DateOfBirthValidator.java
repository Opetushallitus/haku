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

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateOfBirthValidator extends FieldValidator {

    public static final String DATE_OF_BIRTH_GENERIC_ERROR_MESSAGE = "henkilotiedot.syntymaaika.virhe";
    public static final String DATE_OF_BIRTH_IN_FUTURE_ERROR = "henkilotiedot.syntymaaika.tulevaisuudessa";
    public static final String DATE_OF_BIRTH_NOT_A_DATE_ERROR = "henkilotiedot.syntymaaika.eipaivamaara";
    public static final String DATE_OF_BIRTH_FORMAT = "dd.MM.yyyy";

    private String bundleName;

    public DateOfBirthValidator(final String fieldName, final I18nText errorMessage, String bundleName) {
        super(fieldName, errorMessage);
        this.bundleName = bundleName;
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        String dateOfBirthString = validationInput.getValues().get(fieldName);
        ValidationResult result = new ValidationResult();
        Date dateOfBirth = null;
        try {
            dateOfBirth = (new SimpleDateFormat(DATE_OF_BIRTH_FORMAT)).parse(dateOfBirthString);
        } catch (ParseException e) {
            result = new ValidationResult(fieldName, ElementUtil.createI18NText(DATE_OF_BIRTH_NOT_A_DATE_ERROR,
                    bundleName));
        } catch (NullPointerException e) {
            // Ei syntymäaikaa
            result = new ValidationResult(fieldName, ElementUtil.createI18NText(DATE_OF_BIRTH_NOT_A_DATE_ERROR,
                    bundleName));
        }
        if (dateOfBirth != null && dateOfBirth.after(new Date())) {
            result = new ValidationResult(fieldName, ElementUtil.createI18NText(DATE_OF_BIRTH_IN_FUTURE_ERROR,
                    bundleName));
        }
        return result;
    }
}
