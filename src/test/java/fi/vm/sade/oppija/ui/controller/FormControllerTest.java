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

package fi.vm.sade.oppija.ui.controller;

import fi.vm.sade.oppija.hakemus.dao.ApplicationDAOMemoryImpl;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.impl.ApplicationServiceImpl;
import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.IllegalStateException;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.service.impl.UserPrefillDataServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


public class FormControllerTest {

    public static final String OID = "1.1.1.1";
    private final String applicationPeriodId = "Yhteishaku";
    private final String formId = "yhteishaku";
    private final String firstCategoryId = "henkilotiedot";
    private FormController formController;
    private ApplicationDAOMemoryImpl applicationDAO;
    public static final UserHolder USER_HOLDER = new UserHolder();

    @Before
    public void setUp() throws Exception {
        this.applicationDAO = new ApplicationDAOMemoryImpl();
        final FormModelDummyMemoryDaoImpl formService = new FormModelDummyMemoryDaoImpl(formId, firstCategoryId);
        final ApplicationServiceImpl applicationService = new ApplicationServiceImpl(applicationDAO, USER_HOLDER,
                formService);
        final UserPrefillDataServiceImpl userPrefillDataService = new UserPrefillDataServiceImpl(USER_HOLDER);
        this.formController = new FormController(formService, applicationService, userPrefillDataService);
    }

    @Test
    public void testGetFormAndRedirectToFirstCategory() throws Exception {
        String actual = formController.getApplication("Yhteishaku", formId);
        String expected = "redirect:" + formId + "/" + firstCategoryId;
        assertEquals(expected, actual);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormAndRedirectToFirstCategoryNotFound() throws Exception {
        formController.getApplication(applicationPeriodId, "väärä");
    }

    @Test(expected = NullPointerException.class)
    public void testGetFormAndRedirectToFirstCategoryNullFromId() throws Exception {
        formController.getApplication(applicationPeriodId, null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationId() throws Exception {
        formController.getApplication(null, formId);
    }

    @Test(expected = NullPointerException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationIdAndFormId() throws Exception {
        formController.getApplication(null, null);
    }

    @Test
    public void testGetCategoryMVCategory() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(applicationPeriodId, formId, firstCategoryId);
        assertEquals(firstCategoryId, ((Phase) actualModelAndView.getModel().get("element")).getId());
    }

    @Test
    public void testGetCategoryModelSize() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(applicationPeriodId, formId, firstCategoryId);
        assertEquals(4, actualModelAndView.getModel().size());
    }

    @Test
    public void testGetCategoryView() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(applicationPeriodId, formId, firstCategoryId);
        assertEquals("/elements/Phase", actualModelAndView.getViewName());
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

    @Test
    public void testGetComplete() throws Exception {
        Application application = new Application(new FormId(applicationPeriodId, formId), USER_HOLDER.getUser());
        application.setOid(OID);
        applicationDAO.hakemukset.add(application);
        ModelAndView complete = formController.getComplete(applicationPeriodId, formId, OID);
        assertEquals(FormController.VALMIS_VIEW, complete.getViewName());
    }

    @Test(expected = IllegalStateException.class)
    public void testSendForm() throws Exception {
        formController.submitApplication(applicationPeriodId, formId);

    }

    @Test
    public void testsaveCategory() throws Exception {
        ModelAndView modelAndView = formController.savePhase(applicationPeriodId, formId, firstCategoryId, new LinkedMultiValueMap<String, String>());
        assertEquals(FormController.DEFAULT_VIEW, modelAndView.getViewName());
    }
}
