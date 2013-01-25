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

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmailValidatorTest {


    public static final String FIELD_ID = "email";
    public static final EmailValidator EMAIL_VALIDATOR = new EmailValidator(FIELD_ID, "Invalid email address");

    @Test
    public void testNoValue() throws Exception {
        ValidationResult validate = EMAIL_VALIDATOR.validate(new HashMap<String, String>(0));
        assertFalse(validate.hasErrors());
    }

    @Test
    public void testValid() throws Exception {
        ValidationResult validate = EMAIL_VALIDATOR.validate(ImmutableMap.of(FIELD_ID, "test@oph.fi"));
        assertFalse(validate.hasErrors());
    }

    @Test
    public void testEmpty() throws Exception {
        ValidationResult validate = EMAIL_VALIDATOR.validate(ImmutableMap.of(FIELD_ID, ""));
        assertTrue(validate.hasErrors());
    }
}
