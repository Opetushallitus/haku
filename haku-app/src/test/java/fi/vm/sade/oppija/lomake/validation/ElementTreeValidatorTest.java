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

import fi.vm.sade.oppija.lomakkeenhallinta.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.*;

public class ElementTreeValidatorTest {

    private TextQuestion textQuestion;
    private FormServiceMockImpl formModelDummyMemoryDao;

    @Before
    public void setUp() throws Exception {
        textQuestion = new TextQuestion("id", createI18NAsIs("title"));
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
        testAsuinmaa("SV", 1);
    }

    @Test
    public void testValidateAsuinmaaFI() throws Exception {
        testAsuinmaa("FI", 3);
    }

    private void testAsuinmaa(final String asuinmaa, final int errorCount) {
        Element phase = formModelDummyMemoryDao.getFirstPhase(Yhteishaku2013.ASID, "yhteishaku");
        HashMap<String, String> values = fillFormWithoutAsuinmaa();
        values.put("asuinmaa", asuinmaa);
        ValidationResult validationResult = ElementTreeValidator.validate(phase, values);
        assertEquals(errorCount, validationResult.getErrorMessages().size());
    }

    private HashMap<String, String> fillFormWithoutAsuinmaa() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put("Etunimet", "Mika Ville");
        values.put("Sukunimi", "Rajapaju");
        values.put("Kutsumanimi", "Mika");
        values.put("kansalaisuus", "FI");
        values.put("Henkilotunnus", "110293-906X");
        values.put("Sukupuoli", "n");
        values.put("aidinkieli", "FI");
        //values.put("kotikunta", "janakkala");
        return values;
    }

}
