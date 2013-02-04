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

import fi.vm.sade.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;

import java.util.Map;

public class ISO88591NameValidator extends FieldValidator {

    public static final String DEFAULT_ERROR_MESSAGE = "Virheellinen syöte";

    public ISO88591NameValidator(final String fieldName) {
        super(fieldName, DEFAULT_ERROR_MESSAGE);
    }

    protected ISO88591NameValidator(final String fieldName, final String errorMessage) {
        super(fieldName, errorMessage);
    }


    @Override
    public ValidationResult validate(Map<String, String> values) {
        String value = values.get(fieldName);
        if (containsValidNameCharacters(value)) {
            return validValidationResult;
        } else {
            return invalidValidationResult;
        }
    }

    private boolean containsValidNameCharacters(CharSequence cs) {
        if (cs == null) {
            return true;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            boolean letter = Character.isLetter(cs.charAt(i));
            if ((letter == false &&
                    cs.charAt(i) != ' ' &&
                    cs.charAt(i) != '-' &&
                    cs.charAt(i) != ',') || (i == 0 && !letter)) {
                return false;
            }
        }
        return true;
    }
}
