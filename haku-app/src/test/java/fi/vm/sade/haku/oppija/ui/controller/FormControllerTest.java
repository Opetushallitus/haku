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

package fi.vm.sade.haku.oppija.ui.controller;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserHolder;
import fi.vm.sade.haku.oppija.ui.common.RedirectToPendingViewPath;
import fi.vm.sade.haku.oppija.ui.common.RedirectToPhaseViewPath;
import fi.vm.sade.haku.oppija.ui.service.UIService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormControllerTest {

    public static final String ASID = "dummyAsid";
    public static final String OID = "1.1.1";
    private static final String FIRST_CATEGORY_ID = "henkilotiedot";
    private static final String APPLICATION_SYSTEM_ID = ASID;
    public static final String TEST_PHASE = "test_phase";
    public static final String PHASE_TITLE = "title";
    public static final Phase PHASE = new Phase(FIRST_CATEGORY_ID, createI18NAsIs(PHASE_TITLE), false);
    public static final Form FORM = new Form("id", createI18NAsIs("title"));
    private FormController formController;
    private ApplicationService applicationService;
    private FormService formService;
    private Application application;
    private ApplicationState applicationState;
    private ApplicationSystemService applicationSystemService;

    @Before
    public void setUp() throws Exception {

        this.applicationService = mock(ApplicationService.class);
        this.formService = mock(FormService.class);
        this.applicationSystemService = mock(ApplicationSystemService.class);
        UserHolder userHolder = mock(UserHolder.class);
        UIService uiService = mock(UIService.class);
        this.formController = new FormController(formService, applicationService, userHolder, "", uiService, applicationSystemService);
        this.application = new Application();
        FORM.addChild(PHASE);
        when(applicationService.getApplication(Matchers.<String>any())).thenReturn(this.application);
        when(formService.getFirstPhase(APPLICATION_SYSTEM_ID)).thenReturn(PHASE);
        when(formService.getActiveForm(APPLICATION_SYSTEM_ID)).thenReturn(FORM);
        when(userHolder.getApplication(Matchers.<String>any())).thenReturn(this.application);
        applicationState = new ApplicationState(application, FIRST_CATEGORY_ID);
        application.setPhaseId(FIRST_CATEGORY_ID);
        when(applicationService.saveApplicationPhase(Matchers.<ApplicationPhase>any())).thenReturn(applicationState);
    }

    private String resolveRedirectPath(Response response) {
        return ((URI) response.getMetadata().get("Location").get(0)).getPath();
    }

    @Test
    public void testGetFormAndRedirectToFirstCategory() throws Exception {
        this.application.setPhaseId(TEST_PHASE);
        String expected = new RedirectToPhaseViewPath(APPLICATION_SYSTEM_ID, TEST_PHASE).getPath();
        Response response = formController.getApplication(APPLICATION_SYSTEM_ID);
        String actual = ((URI) response.getMetadata().get("Location").get(0)).getPath();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetFormAndRedirectToFirstCategoryNew() throws Exception {
        application.setPhaseId(null);
        when(formService.getFirstPhase(APPLICATION_SYSTEM_ID)).thenReturn(new Phase(FIRST_CATEGORY_ID, createI18NAsIs("title"), false));
        String expected = "/lomake/" + APPLICATION_SYSTEM_ID + "/" + FIRST_CATEGORY_ID;
        Response response = formController.getApplication(APPLICATION_SYSTEM_ID);
        assertEquals(expected, resolveRedirectPath(response));
    }


    @Test(expected = NullPointerException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationId() throws Exception {
        formController.getApplication(null);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGetCategoryMVCategory() throws Exception {
        Viewable viewable = formController.getPhase(APPLICATION_SYSTEM_ID, FIRST_CATEGORY_ID);
        assertEquals(FIRST_CATEGORY_ID, ((Phase) ((Map) viewable.getModel()).get("element")).getId());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGetCategoryModelSize() throws Exception {
        Viewable viewable = formController.getPhase(APPLICATION_SYSTEM_ID, FIRST_CATEGORY_ID);
        assertEquals(6, ((Map) viewable.getModel()).size());
    }

    @Test
    public void testGetCategoryView() throws Exception {
        Viewable viewable = formController.getPhase(APPLICATION_SYSTEM_ID, FIRST_CATEGORY_ID);
        assertEquals("/elements/Root", viewable.getTemplateName());
    }

    @Test
    public void testGetCategoryWrongView() throws Exception {
        Viewable viewable = formController.getPhase(APPLICATION_SYSTEM_ID, FIRST_CATEGORY_ID);
        assertNotSame(null, viewable.getTemplateName());
    }

    @Test()
    public void sendInvalid() throws Exception {
        when(applicationService.submitApplication(Matchers.<String>any())).thenReturn(OID);
        Response response = formController.submitApplication(APPLICATION_SYSTEM_ID);
        RedirectToPendingViewPath redirectToPendingViewPath = new RedirectToPendingViewPath(APPLICATION_SYSTEM_ID, OID);
        String actual = ((URI) response.getMetadata().get("Location").get(0)).getPath();
        assertEquals(redirectToPendingViewPath.getPath(), actual);
    }

    @Test
    public void testSaveCategoryInvalid() throws Exception {
        HashMap<String, I18nText> errorMessages = new HashMap<String, I18nText>();
        errorMessages.put("", ElementUtil.createI18NText("", "form_messages_yhteishaku_syksy"));
        applicationState.addError(errorMessages);
        Viewable viewable = (Viewable) formController.savePhase(APPLICATION_SYSTEM_ID, FIRST_CATEGORY_ID, new MultivaluedMapImpl()).getEntity();
        assertEquals(FormController.ROOT_VIEW, viewable.getTemplateName());
    }

    @Test
    public void testSaveCategoryValid() throws Exception {
        Response response = formController.savePhase(APPLICATION_SYSTEM_ID, FIRST_CATEGORY_ID, new MultivaluedMapImpl());
        String actual = ((URI) response.getMetadata().get("Location").get(0)).getPath();
        assertEquals(new RedirectToPhaseViewPath(APPLICATION_SYSTEM_ID, FIRST_CATEGORY_ID).getPath(), actual);
    }
}
