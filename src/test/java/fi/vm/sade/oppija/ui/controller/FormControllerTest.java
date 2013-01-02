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

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.service.impl.UserPrefillDataServiceImpl;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.ui.common.RedirectToPendingViewPath;
import fi.vm.sade.oppija.ui.common.RedirectToPhaseViewPath;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FormControllerTest {

    public static final String OID = "1.1.1";
    private static final String FIRST_CATEGORY_ID = "henkilotiedot";
    private static final String APPLICATION_PERIOD_ID = "Yhteishaku";
    private static final String FORM_ID = "yhteishaku";
    public static final String TEST_PHASE = "test_phase";
    public static final String PHASE_TITLE = "title";
    public static final Phase PHASE = new Phase(FIRST_CATEGORY_ID, PHASE_TITLE, false);
    public static final Form FORM = new Form("id", "title");
    private FormController formController;
    public static final UserHolder USER_HOLDER = new UserHolder();
    private ApplicationService applicationService;
    private FormService formService;
    private Application application;
    private ApplicationState applicationState;

    @Before
    public void setUp() throws Exception {
        this.applicationService = mock(ApplicationService.class);
        this.formService = mock(FormService.class);
        final UserPrefillDataServiceImpl userPrefillDataService = new UserPrefillDataServiceImpl(USER_HOLDER);
        this.formController = new FormController(formService, applicationService, userPrefillDataService);
        this.application = new Application();
        FORM.addChild(PHASE);
        when(applicationService.getApplication(Matchers.<FormId>any())).thenReturn(this.application);
        when(formService.getFirstPhase(APPLICATION_PERIOD_ID, FORM_ID)).thenReturn(PHASE);
        when(formService.getActiveForm(APPLICATION_PERIOD_ID, FORM_ID)).thenReturn(FORM);
        applicationState = new ApplicationState(application, FIRST_CATEGORY_ID);
        application.setVaiheId(FIRST_CATEGORY_ID);
        when(applicationService.saveApplicationPhase(Matchers.<ApplicationPhase>any())).thenReturn(applicationState);
    }

    @Test
    public void testGetFormAndRedirectToFirstCategory() throws Exception {
        this.application.setVaiheId(TEST_PHASE);
        String expected = new RedirectToPhaseViewPath(APPLICATION_PERIOD_ID, FORM_ID, TEST_PHASE).getPath();
        String actual = formController.getApplication(APPLICATION_PERIOD_ID, FORM_ID);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetFormAndRedirectToFirstCategoryNew() throws Exception {
        application.setVaiheId(null);
        when(formService.getFirstPhase(APPLICATION_PERIOD_ID, FORM_ID)).thenReturn(new Phase(FIRST_CATEGORY_ID, "title", false));
        String expected = "redirect:" + FORM_ID + "/" + FIRST_CATEGORY_ID;
        String actual = formController.getApplication(APPLICATION_PERIOD_ID, FORM_ID);
        assertEquals(expected, actual);
    }

    @Test(expected = ResourceNotFoundExceptionRuntime.class)
    public void testGetFormAndRedirectToFirstCategoryNotFound() throws Exception {
        when(applicationService.getApplication(Matchers.<FormId>any())).thenThrow(new ResourceNotFoundExceptionRuntime(""));
        formController.getApplication(APPLICATION_PERIOD_ID, "väärä");
    }

    @Test(expected = NullPointerException.class)
    public void testGetFormAndRedirectToFirstCategoryNullFromId() throws Exception {
        formController.getApplication(APPLICATION_PERIOD_ID, null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationId() throws Exception {
        formController.getApplication(null, FORM_ID);
    }

    @Test(expected = NullPointerException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationIdAndFormId() throws Exception {
        formController.getApplication(null, null);
    }

    @Test
    public void testGetCategoryMVCategory() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(APPLICATION_PERIOD_ID, FORM_ID, FIRST_CATEGORY_ID);
        assertEquals(FIRST_CATEGORY_ID, ((Phase) actualModelAndView.getModel().get("element")).getId());
    }

    @Test
    public void testGetCategoryModelSize() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(APPLICATION_PERIOD_ID, FORM_ID, FIRST_CATEGORY_ID);
        assertEquals(4, actualModelAndView.getModel().size());
    }

    @Test
    public void testGetCategoryView() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(APPLICATION_PERIOD_ID, FORM_ID, FIRST_CATEGORY_ID);
        assertEquals("/elements/Phase", actualModelAndView.getViewName());
    }

    @Test
    public void testGetCategoryWrongView() throws Exception {
        ModelAndView actualModelAndView = formController.getElement(APPLICATION_PERIOD_ID, FORM_ID, FIRST_CATEGORY_ID);
        assertNotSame(null, actualModelAndView.getViewName());
    }

    @Test
    public void testResourceNotFoundExceptionMessage() throws Exception {
        String message = "text";
        ResourceNotFoundExceptionRuntime rnfv = new ResourceNotFoundExceptionRuntime(message);
        ModelAndView modelAndView = formController.resourceNotFoundExceptions(rnfv);
        assertEquals(message, modelAndView.getModel().get("message"));
    }

    @Test
    public void testResourceNotFoundExceptionView() throws Exception {
        String message = "text";
        ResourceNotFoundExceptionRuntime rnfv = new ResourceNotFoundExceptionRuntime(message);
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

    @Test()
    public void sendInvalid() throws Exception {
        when(applicationService.submitApplication(Matchers.<FormId>any())).thenReturn(OID);
        ModelAndView modelAndView = formController.submitApplication(APPLICATION_PERIOD_ID, FORM_ID);
        RedirectToPendingViewPath redirectToPendingViewPath = new RedirectToPendingViewPath(APPLICATION_PERIOD_ID, FORM_ID, OID);
        assertEquals(redirectToPendingViewPath.getPath(), modelAndView.getViewName());
    }

    @Test
    public void testSaveCategoryInvalid() throws Exception {
        HashMap<String, String> errorMessages = new HashMap<String, String>();
        errorMessages.put("", "");
        applicationState.addError(errorMessages);
        ModelAndView modelAndView = formController.savePhase(APPLICATION_PERIOD_ID, FORM_ID, FIRST_CATEGORY_ID, new LinkedMultiValueMap<String, String>());
        assertEquals(FormController.DEFAULT_VIEW, modelAndView.getViewName());
    }

    @Test
    public void testSaveCategoryValid() throws Exception {
        ModelAndView modelAndView = formController.savePhase(APPLICATION_PERIOD_ID, FORM_ID, FIRST_CATEGORY_ID, new LinkedMultiValueMap<String, String>());
        assertEquals(new RedirectToPhaseViewPath(APPLICATION_PERIOD_ID, FORM_ID, FIRST_CATEGORY_ID).getPath(), modelAndView.getViewName());
    }
}
