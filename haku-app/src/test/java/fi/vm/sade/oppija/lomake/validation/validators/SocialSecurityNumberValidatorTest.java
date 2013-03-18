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
 * @author Mikko Majapuro
 */
public class SocialSecurityNumberValidatorTest {

    private Map<String, String> values;

    @Before
    public void setUp() throws Exception {
        values = new HashMap<String, String>();
    }

    @Test
    public void testValidateValid() throws Exception {
        String[] hetus = new String[] {"120187-123Y", "101167-157N", "211011A8927", "200114+870C"};
        values.put("kansalaisuus", "fi");
        for (String hetu : hetus) {
            values.put("henkilotunnus", hetu);
            SocialSecurityNumberFieldValidator validator = new SocialSecurityNumberFieldValidator("henkilotunnus");
            ValidationResult validationResult = validator.validate(values);
            assertFalse(validationResult.hasErrors());
        }
    }

    @Test
    public void testValidateInvalidCheck() throws Exception {
        values.put("henkilotunnus", "120187-123Z");
        values.put("kansalaisuus", "fi");
        SocialSecurityNumberFieldValidator validator = new SocialSecurityNumberFieldValidator("henkilotunnus");
        ValidationResult validationResult = validator.validate(values);
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidateInvalid() throws Exception {
        values.put("henkilotunnus", "10.02.1977");
        values.put("kansalaisuus", "fi");
        SocialSecurityNumberFieldValidator validator = new SocialSecurityNumberFieldValidator("henkilotunnus");
        ValidationResult validationResult = validator.validate(values);
        assertTrue(validationResult.hasErrors());
    }
    
    @Test
    public void testInvalidDOB() {
        String[] hetus = new String[] {"000000-0000", "310277-1112"};
        values.put("kansalaisuus", "fi");
        for (String hetu : hetus) {
            values.put("henkilotunnus", hetu);
            SocialSecurityNumberFieldValidator validator = new SocialSecurityNumberFieldValidator("henkilotunnus");
            ValidationResult validationResult = validator.validate(values);
            assertTrue(validationResult.hasErrors());
        }
    }
    
    @Test
    public void testNotYetBorn() {
        values.put("henkilotunnus", "311299A999E");
        values.put("kansalaisuus", "fi");
        SocialSecurityNumberFieldValidator validator = new SocialSecurityNumberFieldValidator("henkilotunnus");
        ValidationResult validationResult = validator.validate(values);
        assertTrue(validationResult.hasErrors());
    }
    
}
