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

package fi.vm.sade.haku.oppija.lomake.validation;

import fi.vm.sade.haku.oppija.lomake.dao.impl.FormServiceMockImpl;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RequiredFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGeneratorMock;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.KoodistoServiceMockImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElementTreeValidatorTest {

    private static final String ASID = "dummyAsid";
    private TextQuestion textQuestion;
    private FormServiceMockImpl formModelDummyMemoryDao;
    private ElementTreeValidator elementTreeValidator;

    @Before
    public void setUp() throws Exception {
        textQuestion = new TextQuestion("id", createI18NAsIs("title"));
        FormGeneratorMock formGeneratorMock = new FormGeneratorMock(new KoodistoServiceMockImpl(), ASID);
        ApplicationSystemService mock = mock(ApplicationSystemService.class);
        when(mock.getApplicationSystem(anyString())).thenReturn(formGeneratorMock.createApplicationSystem());
        formModelDummyMemoryDao = new FormServiceMockImpl(mock);
        SsnUniqueConcreteValidator ssnUniqueConcreteValidator = mock(SsnUniqueConcreteValidator.class);
        SsnAndPreferenceUniqueConcreteValidator ssnAndPreferenceUniqueConcreteValidator = mock(SsnAndPreferenceUniqueConcreteValidator.class);
        PreferenceConcreteValidator preferenceConcreteValidator = mock(PreferenceConcreteValidator.class);
        when(ssnUniqueConcreteValidator.validate(any(ValidationInput.class))).thenReturn(new ValidationResult());
        ValidatorFactory validatorFactory = new ValidatorFactory(ssnUniqueConcreteValidator, ssnAndPreferenceUniqueConcreteValidator,
                preferenceConcreteValidator);

        elementTreeValidator = new ElementTreeValidator(validatorFactory);
    }

    @Test(expected = NullPointerException.class)
    public void testValidateNulls() throws Exception {
        elementTreeValidator.validate(new ValidationInput(null, null, null, null));
    }

    @Test(expected = NullPointerException.class)
    public void testValidateNullElement() throws Exception {
        elementTreeValidator.validate(new ValidationInput(null, new HashMap<String, String>(), null, null));
    }

    @Test(expected = NullPointerException.class)
    public void testValidateNullValues() throws Exception {
        elementTreeValidator.validate(new ValidationInput(textQuestion, null, null, null));
    }

    @Test()
    public void testValidateRequiredElement() throws Exception {
        textQuestion.setValidator
                (new RequiredFieldValidator("id", createI18NText("Error message", "form_errors_yhteishaku_syksy")));
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(textQuestion, new HashMap<String, String>(),
                null, null));
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidateAsuinmaaSV() throws Exception {
        testAsuinmaa("SWE", 3);
    }

    @Test
    public void testValidateAsuinmaaFI() throws Exception {
        testAsuinmaa("FIN", 3);
    }

    private void testAsuinmaa(final String asuinmaa, final int errorCount) {
        Element phase = formModelDummyMemoryDao.getFirstPhase(ASID);
        HashMap<String, String> values = fillFormWithoutAsuinmaa();
        values.put("asuinmaa", asuinmaa);
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(phase, values, null, null));
        assertEquals(errorCount, validationResult.getErrorMessages().size());
    }

    private HashMap<String, String> fillFormWithoutAsuinmaa() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put("Etunimet", "Mika Ville");
        values.put("Sukunimi", "Rajapaju");
        values.put("Kutsumanimi", "Mika");
        values.put("kansalaisuus", "FIN");
        values.put("Henkilotunnus", "110293-906X");
        values.put("sukupuoli", "2");
        values.put("aidinkieli", "FI");
        //values.put("kotikunta", "janakkala");
        return values;
    }

}
