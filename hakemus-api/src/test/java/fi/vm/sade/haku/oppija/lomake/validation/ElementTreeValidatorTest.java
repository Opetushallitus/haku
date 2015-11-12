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

import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.lomake.util.SpringInjector;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RequiredFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.FormConfigurationDAOMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.ThemeQuestionDAOMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGenerator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGeneratorImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.KoodistoServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.FormConfigurationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.impl.HakuServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElementTreeValidatorTest {

    private static final String ASID = "haku1";
    private TextQuestion textQuestion;
    private ElementTreeValidator elementTreeValidator;
    private ApplicationSystemService applicationSystemServiceMock;
    private ThemeQuestionDAO themeQuestionDAOMock;
    private FormConfigurationDAO formConfigurationDAOMock;

    @Before
    public void setUp() throws Exception {
        SpringInjector.setTestMode(true);
        textQuestion = (TextQuestion) new TextQuestionBuilder("id").i18nText(createI18NAsIs("title")).build();
        themeQuestionDAOMock = new ThemeQuestionDAOMockImpl();
        formConfigurationDAOMock = new FormConfigurationDAOMockImpl();
        HakuService hakuServiceMock = new HakuServiceMockImpl();
        //TODO : Not Mocked
        I18nBundleService i18nBundleService = new I18nBundleService(null);
        //TODO: Not Mocked
        FormConfigurationService formConfigurationService = new FormConfigurationService(new KoodistoServiceMockImpl(), new HakuServiceMockImpl(), themeQuestionDAOMock, mock(HakukohdeService.class), mock(OrganizationService.class), formConfigurationDAOMock, i18nBundleService);
        FormGenerator formGeneratorMock = new FormGeneratorImpl(hakuServiceMock, formConfigurationService, false, null);
        applicationSystemServiceMock = mock(ApplicationSystemService.class);
        when(applicationSystemServiceMock.getApplicationSystem(anyString())).thenReturn(formGeneratorMock.generate(ASID));
        SsnUniqueConcreteValidator ssnUniqueConcreteValidator = mock(SsnUniqueConcreteValidator.class);
        SsnAndPreferenceUniqueConcreteValidator ssnAndPreferenceUniqueConcreteValidator = mock(SsnAndPreferenceUniqueConcreteValidator.class);
        PreferenceConcreteValidator preferenceConcreteValidator = mock(PreferenceConcreteValidator.class);
        when(ssnUniqueConcreteValidator.validate(any(ValidationInput.class))).thenReturn(new ValidationResult());
        EmailUniqueConcreteValidator emailUniqueConcreteValidator = mock(EmailUniqueConcreteValidator.class);
        when(emailUniqueConcreteValidator.validate(any(ValidationInput.class))).thenReturn(new ValidationResult());
        ValidatorFactory validatorFactory = new ValidatorFactory(ssnUniqueConcreteValidator, ssnAndPreferenceUniqueConcreteValidator,
                preferenceConcreteValidator, emailUniqueConcreteValidator);

        elementTreeValidator = new ElementTreeValidator(validatorFactory);
    }

    @Test(expected = NullPointerException.class)
    public void testValidateNulls() throws Exception {
        elementTreeValidator.validate(new ValidationInput(null, null, null, null, ValidationInput.ValidationContext.officer_modify));
    }

    @Test(expected = NullPointerException.class)
    public void testValidateNullElement() throws Exception {
        elementTreeValidator.validate(new ValidationInput(null, new HashMap<String, String>(), null, null, ValidationInput.ValidationContext.officer_modify));
    }

    @Test(expected = NullPointerException.class)
    public void testValidateNullValues() throws Exception {
        elementTreeValidator.validate(new ValidationInput(textQuestion, null, null, null, ValidationInput.ValidationContext.officer_modify));
    }

    @Test()
    public void testValidateRequiredElement() throws Exception {
        textQuestion.setValidator
                (new RequiredFieldValidator("id", "error.message.key"));
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(textQuestion, new HashMap<String, String>(),
                null, null, ValidationInput.ValidationContext.officer_modify));
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
        ApplicationSystem applicationSystem = applicationSystemServiceMock.getApplicationSystem(ASID);
        Element phase = ElementTree.getFirstChild(applicationSystem.getForm());
        HashMap<String, String> values = fillFormWithoutAsuinmaa();
        values.put("asuinmaa", asuinmaa);
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(phase, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertEquals(errorCount, validationResult.getErrorMessages().size());
    }

    private HashMap<String, String> fillFormWithoutAsuinmaa() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put("Etunimet", "Mika Ville");
        values.put("Sukunimi", "Rajapaju");
        values.put("Kutsumanimi", "Mika");
        values.put("kansalaisuus", "FIN");
        values.put("onkosinullakaksoiskansallisuus", "false");
        values.put("Henkilotunnus", "110293-906X");
        values.put(OppijaConstants.ELEMENT_ID_SEX, "2");
        values.put("aidinkieli", "FI");
        //values.put("kotikunta", "janakkala");
        return values;
    }

}
