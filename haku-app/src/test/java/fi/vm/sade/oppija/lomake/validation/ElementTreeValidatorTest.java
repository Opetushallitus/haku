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

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;

public class ElementTreeValidatorTest {

    private TextQuestion textQuestion;
    private FormServiceMockImpl formModelDummyMemoryDao;

    @Before
    public void setUp() throws Exception {
        textQuestion = new TextQuestion("id", createI18NText("title"));
        formModelDummyMemoryDao = new FormServiceMockImpl();
    }

    @Test(expected = NullPointerException.class)
    public void testValidateNulls() throws Exception {
        ElementTreeValidator.validate(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testValidateNullElement() throws Exception {
        ElementTreeValidator.validate(null, new HashMap<String, String>());
    }

    @Test()
    public void testValidateNullValues() throws Exception {
        ValidationResult validationResult = ElementTreeValidator.validate(textQuestion, null);
        assertFalse(validationResult.hasErrors());
    }

    @Test()
    public void testValidateRequiredElement() throws Exception {
        textQuestion.addValidator
                (new RequiredFieldFieldValidator("id", "Error message"));
        ValidationResult validationResult = ElementTreeValidator.validate(textQuestion, new HashMap<String, String>());
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidateAsuinmaaSV() throws Exception {
        testAsuinmaa("sv", 1);
    }

    @Test
    public void testValidateAsuinmaaFI() throws Exception {
        testAsuinmaa("fi", 3);
    }

    private void testAsuinmaa(final String asuinmaa, final int errorCount) {
        Phase phase = formModelDummyMemoryDao.getFirstPhase("Yhteishaku", "yhteishaku");
        HashMap<String, String> values = fillFormWithoutAsuinmaa();
        values.put("asuinmaa", asuinmaa);
        ValidationResult validationResult = ElementTreeValidator.validate(phase, values);
        assertEquals(validationResult.getErrorMessages().size(), errorCount);
    }

    private HashMap<String, String> fillFormWithoutAsuinmaa() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put("äidinkieli", "fi");
        values.put("kansalaisuus", "fi");
        values.put("Etunimet", "Mika Ville");
        values.put("Sukunimi", "Rajapaju");
        values.put("Kutsumanimi", "Mika");
        values.put("Sukupuoli", "n");
        values.put("Henkilotunnus", "110293-906X");
        return values;
    }

}
