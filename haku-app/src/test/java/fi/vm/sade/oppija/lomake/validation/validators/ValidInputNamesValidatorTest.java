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

import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;

public class ValidInputNamesValidatorTest {

    private HashSet<String> validParameterNames = new HashSet<String>();

    @Test
    public void testValidateWithoutParameters() throws Exception {
        validParameterNames.add("nimi");
        ValidInputNamesValidator validInputNamesValidator = new ValidInputNamesValidator(validParameterNames);
        ValidationResult validationResult = validInputNamesValidator.validate(new HashMap<String, String>());
        assertFalse(validationResult.hasErrors());
    }

    @Test(expected = RuntimeException.class)
    public void testValidateInvalid() throws Exception {
        validateWithParameter("key");
    }

    @Test
    public void testValidateValid() throws Exception {
        ValidationResult validate = validateWithParameter("nimi");
        assertFalse(validate.hasErrors());
    }

    private ValidationResult validateWithParameter(final String parameterName) {
        validParameterNames.add("nimi");
        ValidInputNamesValidator validInputNamesValidator = new ValidInputNamesValidator(validParameterNames);
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(parameterName, "value");
        return validInputNamesValidator.validate(values);
    }
}
