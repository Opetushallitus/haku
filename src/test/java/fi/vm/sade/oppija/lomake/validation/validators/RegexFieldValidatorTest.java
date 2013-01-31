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
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegexFieldValidatorTest {

    public static final String ERROR_MESSAGE = "kenttä on virheellinen";
    public static final String FIELD_NAME = "kenttä";
    public static final String PATTERN = "[A-Za-z]{3}";
    public static final String PATTERN_WORK_EXPERIENCE = "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$"; // 0-1000
    public static final String TEST_VALUE = "ABC";

    private Map<String, String> values;

    @Before
    public void setUp() throws Exception {
        values = new HashMap<String, String>();
    }

    @Test
    public void testValidateValid() throws Exception {
        values.put(FIELD_NAME, TEST_VALUE);
        RegexFieldFieldValidator validator = createValidator(TEST_VALUE);
        ValidationResult validationResult = validator.validate(values);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testValidateInvalid() throws Exception {
        values.put(FIELD_NAME, TEST_VALUE + "Ä");
        RegexFieldFieldValidator validator = createValidator(TEST_VALUE);
        ValidationResult validationResult = validator.validate(values);
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidatePattern() throws Exception {
        values.put(FIELD_NAME, TEST_VALUE);
        RegexFieldFieldValidator validator = createValidator(PATTERN);
        ValidationResult validationResult = validator.validate(values);
        assertFalse(validationResult.hasErrors());
    }

    @Test(expected = NullPointerException.class)
    public void testNullPattern() throws Exception {
        values.put(FIELD_NAME, TEST_VALUE);
        createValidator(null);
    }

    @Test
    public void testValidateWorkExperience_negative() throws Exception {
        ValidationResult validationResult = validateWorkExperience("-1");
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidateValidWorkExperiencies() throws Exception {
        ValidationResult validationResult;
        for (int i = 0; i <= 1000; i++) {
            validationResult = validateWorkExperience(String.valueOf(i));
            assertFalse(validationResult.hasErrors());
        }
    }

    @Test
    public void testValidateWorkExperienceUpperLimit() throws Exception {
        ValidationResult validationResult = validateWorkExperience("1001");
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidateWorkExperienceEmpty() throws Exception {
        ValidationResult validationResult = validateWorkExperience("");
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testValidateWorkExperienceSpace() throws Exception {
        ValidationResult validationResult = validateWorkExperience(" ");
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidateWorkExperienceSign() throws Exception {
        ValidationResult validationResult = validateWorkExperience("-");
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidateWorkExperienceWithoutValue() throws Exception {
        RegexFieldFieldValidator validator = createValidator(PATTERN_WORK_EXPERIENCE);
        ValidationResult validationResult = validator.validate(values);
        assertFalse(validationResult.hasErrors());
    }

    private ValidationResult validateWorkExperience(final String value) {
        values.put(FIELD_NAME, value);
        RegexFieldFieldValidator validator = createValidator(PATTERN_WORK_EXPERIENCE);
        return validator.validate(values);
    }


    private RegexFieldFieldValidator createValidator(final String pattern) {
        return new RegexFieldFieldValidator(FIELD_NAME, ERROR_MESSAGE, pattern);
    }


}
