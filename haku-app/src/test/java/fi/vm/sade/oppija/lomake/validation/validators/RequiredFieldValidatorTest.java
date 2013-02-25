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

/**
 * Created by IntelliJ IDEA.
 * User: ville
 * Date: 10/5/12
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequiredFieldValidatorTest {
    public static final String ERROR_MESSAGE = "kenttä on virheellinen";
    public static final String FIELD_NAME = "kenttä";
    private Map<String, String> values;

    @Before
    public void setUp() throws Exception {
        values = new HashMap<String, String>();
    }

    @Test
    public void testValidateValid() throws Exception {
        values.put(FIELD_NAME, "1");
        RequiredFieldFieldValidator requiredFieldValidator = new RequiredFieldFieldValidator(FIELD_NAME, ERROR_MESSAGE);
        ValidationResult validationResult = requiredFieldValidator.validate(values);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testValidateInvalid() throws Exception {
        RequiredFieldFieldValidator requiredFieldValidator = new RequiredFieldFieldValidator(FIELD_NAME, ERROR_MESSAGE);
        ValidationResult validationResult = requiredFieldValidator.validate(values);
        assertTrue(validationResult.hasErrors());
    }

}
