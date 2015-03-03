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

import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.util.SpringInjector;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
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
    private SocialSecurityNumberFieldValidator validator;
    private TextQuestion henkilotunnus;

    @Before
    public void setUp() throws Exception {
        SpringInjector.setTestMode(true);
        values = new HashMap<String, String>();
        validator = new SocialSecurityNumberFieldValidator();
        henkilotunnus = new TextQuestion("henkilotunnus", ElementUtil.createI18NAsIs("Henkilotunnus"));
    }

    @Test
    public void testValidateValid() throws Exception {
        String[] hetus = new String[]{"120187-123Y", "101167-157N", "211011A8927", "200114+870C"};
        values.put("kansalaisuus", "fi");
        for (String hetu : hetus) {
            values.put("henkilotunnus", hetu);


            ValidationResult validationResult = validator.validate(new ValidationInput(henkilotunnus, values, null, null, ValidationInput.ValidationContext.officer_modify));
            assertFalse(validationResult.hasErrors());
        }
    }

    @Test
    public void testValidateInvalidCheck() throws Exception {
        values.put("henkilotunnus", "120187-123Z");
        values.put("kansalaisuus", "fi");
        ValidationResult validationResult = validator.validate(new ValidationInput(henkilotunnus, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidateInvalid() throws Exception {
        values.put("henkilotunnus", "10.02.1977");
        values.put("kansalaisuus", "fi");
        ValidationResult validationResult = validator.validate(new ValidationInput(henkilotunnus, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testInvalidDOB() {
        String[] hetus = new String[]{"000000-0000", "310277-1112"};
        values.put("kansalaisuus", "fi");
        for (String hetu : hetus) {
            values.put("henkilotunnus", hetu);
            ValidationResult validationResult = validator.validate(new ValidationInput(henkilotunnus, values, null, null, ValidationInput.ValidationContext.officer_modify));
            assertTrue(validationResult.hasErrors());
        }
    }

    @Test
    public void testNotYetBorn() {
        values.put("henkilotunnus", "311299A999E");
        values.put("kansalaisuus", "fi");
        ValidationResult validationResult = validator.validate(new ValidationInput(henkilotunnus, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertTrue(validationResult.hasErrors());
    }

}
