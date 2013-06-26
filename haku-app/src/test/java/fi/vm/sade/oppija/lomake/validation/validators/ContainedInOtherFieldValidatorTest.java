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
import org.junit.Test;

import java.util.HashMap;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NTextError;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContainedInOtherFieldValidatorTest {

    String thisField = "thisField";
    String thatField = "thatField";
    FieldValidator validator = new ContainedInOtherFieldValidator(thisField, thatField, createI18NTextError("error"));

    @Test
    public void testExactMatch() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "FirstName");
        values.put(thisField, "FirstName");
        ValidationResult result = validator.validate(values);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testPartialMatch() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "First-Name");
        values.put(thisField, "First");
        ValidationResult result = validator.validate(values);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testIgnoreCaseMatch() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "First-Name");
        values.put(thisField, "first");
        ValidationResult result = validator.validate(values);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testNoMatch() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "First-Name");
        values.put(thisField, "Last");
        ValidationResult result = validator.validate(values);
        assertTrue(result.hasErrors());
    }

    @Test
    public void testNoMatchNull() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "FirstName");
        values.put(thisField, null);
        ValidationResult result = validator.validate(values);
        assertTrue(result.hasErrors());
    }
}
