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

import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ElementTreeValidatorTest {

    private TextQuestion textQuestion;
    private FormModelDummyMemoryDaoImpl formModelDummyMemoryDao;

    @Before
    public void setUp() throws Exception {
        textQuestion = new TextQuestion("id", "title");
        formModelDummyMemoryDao = new FormModelDummyMemoryDaoImpl();
        Form form = formModelDummyMemoryDao.getForm("Yhteishaku", "yhteishaku");
        form.init();

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
        textQuestion.getValidators().add(new RequiredFieldFieldValidator("id", "Error message"));
        ValidationResult validationResult = ElementTreeValidator.validate(textQuestion, new HashMap<String, String>());
        assertTrue(validationResult.hasErrors());
    }

    @Test()
    public void testValidateAsuinMaaSV() throws Exception {
        Phase phase = formModelDummyMemoryDao.getFirstCategory("Yhteishaku", "yhteishaku");
        HashMap<String, String> values = fillFormWithoutAsuinmaa();
        values.put("asuinmaa", "sv");
        ValidationResult validationResult = ElementTreeValidator.validate(phase, values);
        assertTrue(validationResult.getErrorMessages().size() == 1);
    }

    @Test()
    public void testValidateAsuinMaaFI() throws Exception {
        Phase phase = formModelDummyMemoryDao.getFirstCategory("Yhteishaku", "yhteishaku");
        HashMap<String, String> values = fillFormWithoutAsuinmaa();
        values.put("asuinmaa", "fi");
        ValidationResult validationResult = ElementTreeValidator.validate(phase, values);
        assertTrue(validationResult.getErrorMessages().size() == 3);
    }

    private HashMap<String, String> fillFormWithoutAsuinmaa() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put("äidinkieli", "Suomi");
        values.put("kansalaisuus", "Suomi");
        values.put("Etunimet", "Ville");
        values.put("Sukunimi", "Rajapaju");
        values.put("Kutsumanimi", "Mika Ville");
        values.put("Sukupuoli", "m");
        values.put("Henkilotunnus", "010188-123X");
        return values;
    }

}
