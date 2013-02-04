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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ISO88591NameValidatorTest {

    public static final String ERROR_MESSAGE = "error message";
    public static final String FIELD_NAME = "name";
    private final ISO88591NameValidator iso88591NameValidator = new ISO88591NameValidator(FIELD_NAME, ERROR_MESSAGE);

    private final List<String> listOfValidNames = ImmutableList.of(
            "",
            "etunimi sukunimi",
            "etunimi, sukunimi",
            "etunimi-sukunimi",
            "\u00C0",
            "\u00D6");

    private final List<String> listOfInvalidNames = ImmutableList.of(
            ",etunimi sukunimi",
            "-etunimi sukunimi",
            " etunimi sukunimi",
            "                          ",
            "\"",
            ".",
            "_",
            "*",
            "\u00D7",
            "\u00F7",
            "\u20ac");

    @Test
    public void testValidNames() throws Exception {
        for (String validName : listOfValidNames) {
            validateValidName(validName);
        }
    }

    @Test
    public void testInvalidNames() throws Exception {
        for (String invalidName : listOfInvalidNames) {
            validateInvalidName(invalidName);
        }
    }

    @Test
    public void testNoValue() throws Exception {
        Map<String, String> values = new HashMap<String, String>(0);
        ValidationResult validationResult = iso88591NameValidator.validate(values);
        assertFalse("Null is valid input", validationResult.hasErrors());

    }

    @Test
    public void testConstructor() throws Exception {
        ISO88591NameValidator validator = new ISO88591NameValidator(FIELD_NAME);
        assertEquals("Wrong default error message", ISO88591NameValidator.DEFAULT_ERROR_MESSAGE, validator.errorMessage);
    }

    private void validateValidName(String name) {
        assertFalse(name, validate(name).hasErrors());
    }

    private void validateInvalidName(final String name) {
        assertTrue(name, validate(name).hasErrors());
    }

    private ValidationResult validate(String name) {
        Map<String, String> values = ImmutableMap.of(FIELD_NAME, name);
        return iso88591NameValidator.validate(values);
    }
}
