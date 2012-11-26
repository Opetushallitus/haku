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

package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.dao.impl.ApplicationDAOMemoryImpl;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.service.UserHolder;
import fi.vm.sade.oppija.haku.service.impl.HakemusServiceImpl;
import fi.vm.sade.oppija.haku.service.impl.UserDataStorage;
import fi.vm.sade.oppija.haku.service.impl.UserPrefillDataServiceImpl;
import fi.vm.sade.oppija.haku.ui.controller.FormController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


public class FormControllerTest {

    private final String applicationPeriodId = "Yhteishaku";
    private final String formId = "yhteishaku";
    private final String firstCategoryId = "henkilotiedot";
    private FormController formController;


    @Before
    public void setUp() throws Exception {
        final FormModelDummyMemoryDaoImpl formService = new FormModelDummyMemoryDaoImpl(formId, firstCategoryId);
        UserHolder userHolder = new UserHolder();
        final HakemusServiceImpl hakemusService = new HakemusServiceImpl(new UserDataStorage(new ApplicationDAOMemoryImpl(), userHolder),
                formService);
        final UserPrefillDataServiceImpl userPrefillDataService = new UserPrefillDataServiceImpl(userHolder);
        this.formController = new FormController(formService, hakemusService, userPrefillDataService);
    }

    @Test
    public void testGetFormAndRedirectToFirstCategory() throws Exception {
        String actual = formController.getFormAndRedirectToFirstCategory("Yhteishaku", formId);
        String expected = "redirect:" + formId + "/" + firstCategoryId;
        assertEquals(expected, actual);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormAndRedirectToFirstCategoryNotFound() throws Exception {
        formController.getFormAndRedirectToFirstCategory(applicationPeriodId, "väärä");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormAndRedirectToFirstCategoryNullFromId() throws Exception {
        formController.getFormAndRedirectToFirstCategory(applicationPeriodId, null);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationId() throws Exception {
        formController.getFormAndRedirectToFirstCategory(null, formId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationIdAndFormId() throws Exception {
        formController.getFormAndRedirectToFirstCategory(null, null);
    }

    @Test
    public void testGetCategoryMVCategory() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(applicationPeriodId, formId, firstCategoryId);
        assertEquals(firstCategoryId, ((Vaihe) actualModelAndView.getModel().get("element")).getId());
    }

    @Test
    public void testGetCategoryMVForm() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(applicationPeriodId, formId, firstCategoryId);
        assertEquals(formId, ((Vaihe) actualModelAndView.getModel().get("element")).getParent().getId());
    }

    @Test
    public void testGetCategoryModelSize() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(applicationPeriodId, formId, firstCategoryId);
        assertEquals(3, actualModelAndView.getModel().size());
    }

    @Test
    public void testGetCategoryView() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(applicationPeriodId, formId, firstCategoryId);
        assertEquals("/elements/Vaihe", actualModelAndView.getViewName());
    }

    @Test
    public void testGetCategoryWrongView() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(applicationPeriodId, formId, firstCategoryId);
        assertNotSame(null, actualModelAndView.getViewName());
    }

    @Test
    public void testResourceNotFoundExceptionMessage() throws Exception {
        String message = "text";
        ResourceNotFoundException rnfv = new ResourceNotFoundException(message);
        ModelAndView modelAndView = formController.resourceNotFoundExceptions(rnfv);
        assertEquals(message, modelAndView.getModel().get("message"));
    }

    @Test
    public void testResourceNotFoundExceptionView() throws Exception {
        String message = "text";
        ResourceNotFoundException rnfv = new ResourceNotFoundException(message);
        ModelAndView modelAndView = formController.resourceNotFoundExceptions(rnfv);
        assertEquals(FormController.ERROR_NOTFOUND, modelAndView.getViewName());
    }

    @Test
    public void testExceptions() throws Exception {
        String message = "text";
        NullPointerException nullPointerException = new NullPointerException(message);
        ModelAndView modelAndView = formController.exceptions(nullPointerException);
        assertEquals(FormController.ERROR_SERVERERROR, modelAndView.getViewName());
    }
}
