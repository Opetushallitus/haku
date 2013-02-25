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

package fi.vm.sade.oppija.lomake.validation;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class fieldValidatorTest {

    public static final String FIELD_NAME = "field_name";
    public static final String ERROR_MESSAGE = "error_message";

    @Test
    public void testFieldNameConstructor() throws Exception {
        FieldValidator validator = createValidator(FIELD_NAME, ERROR_MESSAGE);
        assertEquals(FIELD_NAME, validator.fieldName);
    }

    @Test
    public void testErrorMessageConstructor() throws Exception {
        FieldValidator validator = createValidator(FIELD_NAME, ERROR_MESSAGE);
        assertEquals(ERROR_MESSAGE, validator.errorMessage);
    }

    @Test
    public void testValidConstructor() throws Exception {
        createValidator(FIELD_NAME, ERROR_MESSAGE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullField() throws Exception {
        createValidator(null, "");
    }

    @Test(expected = NullPointerException.class)
    public void testNullErrorMessage() throws Exception {
        createValidator("", null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullNUll() throws Exception {
        createValidator(null, null);
    }

    private FieldValidator createValidator(String fieldName, String errorMessage) {
        return new FieldValidator(fieldName, errorMessage) {
            @Override
            public ValidationResult validate(Map<String, String> values) {
                throw new NotImplementedException();
            }
        };
    }
}
