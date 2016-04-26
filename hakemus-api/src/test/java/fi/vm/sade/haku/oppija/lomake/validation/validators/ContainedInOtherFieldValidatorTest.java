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

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.util.SpringInjector;
import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContainedInOtherFieldValidatorTest {

    private static final String thisField = "thisField";
    private static final String thatField = "thatField";
    private static final Element element = new TextQuestion(thisField, ElementUtil.createI18NAsIs(thisField));
    private FieldValidator validator;

    @Before
    public void setUp() throws Exception {
        SpringInjector.setTestMode(true);
        validator = new ContainedInOtherFieldValidator(thatField, "error");
    }

    @Test
    public void testExactMatch() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "FirstName");
        values.put(thisField, "FirstName");
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertFalse(result.hasErrors());
    }

    @Test
    public void testPartialMatch() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "First-Name");
        values.put(thisField, "First");
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertFalse(result.hasErrors());
    }

    @Test
    public void testIgnoreCaseMatch() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "First-Name");
        values.put(thisField, "first");
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertFalse(result.hasErrors());
    }

    @Test
    public void testNoMatch() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "First-Name");
        values.put(thisField, "Last");
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertTrue(result.hasErrors());
    }

    @Test(expected=NullPointerException.class)
    public void testNoMatchNull() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "FirstName");
        values.put(thisField, null);
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertTrue(result.hasErrors());
    }

    @Test
    public void testVaino() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "Väinö");
        values.put(thisField, "Väinö");
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertFalse(result.hasErrors());
    }

    @Test
    public void testSame() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, thisField);
        values.put(thisField, thisField);
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertFalse(result.hasErrors());
    }

    @Test
    public void testSameTwoParts() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "Teemu Hanz");
        values.put(thisField, "Teemu Hanz");
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertTrue(result.hasErrors());
    }

    @Test
    public void testSameTwoPartsOneName() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "Teemu-Hanz");
        values.put(thisField, "Teemu-Hanz");
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertFalse(result.hasErrors());
    }

    @Test
    public void testSpaceAtTheEnd() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "Teemu-Hanz");
        values.put(thisField, "Teemu-Hanz");
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertFalse(result.hasErrors());
    }

    @Test
    public void testSpace() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "Teemu-Hanz");
        values.put(thisField, "    Teemu-Hanz");
        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertFalse(result.hasErrors());
    }

    @Test
    public void testPostProcessingNickName() {
        Element elementNickName = new TextQuestion(OppijaConstants.ELEMENT_ID_NICKNAME, ElementUtil.createI18NAsIs(thisField));

        HashMap<String, String> values = new HashMap<String, String>();
        values.put(thatField, "First-Name");
        values.put(thisField, "Last");
        values.put(OppijaConstants.ELEMENT_ID_NICKNAME, "Last");

        ValidationResult result = validator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.background));
        assertTrue(result.hasErrors());

        result = validator.validate(new ValidationInput(elementNickName, values, null, "", ValidationInput.ValidationContext.officer_modify));
        assertTrue(result.hasErrors());

        result = validator.validate(new ValidationInput(elementNickName, values, null, "", ValidationInput.ValidationContext.background));
        assertFalse(result.hasErrors());
    }
}
