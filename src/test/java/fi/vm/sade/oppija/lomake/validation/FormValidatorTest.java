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

import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class FormValidatorTest {

    RequiredFieldFieldValidator EtunimiRequiredFieldValidator;
    RequiredFieldFieldValidator sukunimiRequiredFieldValidator;
    private Map<String, String> values;
    private List<Validator> validators;

    @Before
    public void setUp() throws Exception {
        values = new HashMap<String, String>();
        validators = new ArrayList<Validator>();
        EtunimiRequiredFieldValidator = new RequiredFieldFieldValidator("etunimi", "Etunimi on pakollinen kenttä");
        sukunimiRequiredFieldValidator = new RequiredFieldFieldValidator("sukunimi", "Sukunimi on pakollinen tieto");
    }

    @Test
    public void testNoValidation() throws Exception {
        ValidationResult validate = FormValidator.validate(validators, values);
        assertFalse(validate.hasErrors());
    }

    @Test
    public void testRequiredValueMissing() throws Exception {
        validators.add(EtunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testRequiredValue() throws Exception {
        values.put(EtunimiRequiredFieldValidator.fieldName, "Urho");
        validators.add(EtunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testMultipleRequiredValue() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put(EtunimiRequiredFieldValidator.fieldName, "Urho");
        values.put(sukunimiRequiredFieldValidator.fieldName, "Kekkonen");
        validators.add(EtunimiRequiredFieldValidator);
        validators.add(sukunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testMultipleRequiredValueMissing() throws Exception {
        validators.add(EtunimiRequiredFieldValidator);
        validators.add(sukunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertTrue(validationResult.hasErrors());
        assertEquals(2, validationResult.getErrorMessages().size());
    }

    @Test
    public void testRequiredValueIsNull() throws Exception {
        values.put(EtunimiRequiredFieldValidator.fieldName, null);
        validators.add(EtunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertTrue(validationResult.hasErrors());

    }

}
