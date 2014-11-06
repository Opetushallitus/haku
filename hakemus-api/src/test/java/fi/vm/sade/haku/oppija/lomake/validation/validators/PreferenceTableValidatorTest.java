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

import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for preference table validator
 *
 * @author Mikko Majapuro
 */
public class PreferenceTableValidatorTest {

    PreferenceTableValidator validator;

    @Before
    public void setUp() {
        List<String> learningInstitutionInputIds = new ArrayList<String>();
        List<String> educationInputIds = new ArrayList<String>();
        learningInstitutionInputIds.add("li1");
        learningInstitutionInputIds.add("li2");
        learningInstitutionInputIds.add("li3");
        learningInstitutionInputIds.add("li4");
        learningInstitutionInputIds.add("li5");
        educationInputIds.add("e1");
        educationInputIds.add("e2");
        educationInputIds.add("e3");
        educationInputIds.add("e4");
        educationInputIds.add("e5");
        validator = new PreferenceTableValidator(learningInstitutionInputIds, educationInputIds);
    }


    @Test
    public void testValidateValid() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("li2", "li2");
        values.put("li3", "li3");
        values.put("li4", "li4");
        values.put("li5", "li5");
        values.put("e1", "e1");
        values.put("e2", "e2");
        values.put("e3", "e3");
        values.put("e4", "e4");
        values.put("e5", "e5");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testValidateNotUniquePreferences() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("li2", "li1");
        values.put("e1", "e1");
        values.put("e2", "e1");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateEmptyRows() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("li2", "li2");
        values.put("li4", "li4");
        values.put("e1", "e1");
        values.put("e2", "e2");
        values.put("e4", "e4");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateEducationValueMissing() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("li2", "li2");
        values.put("e1", "e1");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateLearningInstituteValueMissing() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("e1", "e1");
        values.put("e2", "e2");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertTrue(result.hasErrors());
    }
}
