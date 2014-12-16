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
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.ui.common.RedirectToPhaseViewPath;
import fi.vm.sade.haku.oppija.ui.service.UIService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
    private static final String FIRST_PHASE_ID = "henkilotiedot";
    private static final String APPLICATION_SYSTEM_ID = ASID;
    public static final String PHASE_TITLE = "title";
    public static final  Element PHASE =  new PhaseBuilder(FIRST_PHASE_ID).setEditAllowedByRoles("TESTING")
      .i18nText(createI18NAsIs(PHASE_TITLE)).build();
    public static final Form FORM = new Form("id", createI18NAsIs("title"));

    private FormController formController;
    private ApplicationService applicationService;
    private AuthenticationService authenticationService;
    private FormService formService;
    private Application application;
    private ModelResponse modelResponse;

    @Before
    public void setUp() throws Exception {
        this.application = new Application();
        application.setPhaseId(FIRST_PHASE_ID);
        modelResponse = new ModelResponse(this.application);
        this.applicationService = mock(ApplicationService.class);
        this.formService = mock(FormService.class);
        this.authenticationService = mock(AuthenticationService.class);
        UIService uiService = mock(UIService.class);
        PDFService pdfService = mock(PDFService.class);
        when(uiService.ensureLanguage(Matchers.<HttpServletRequest>any(), Matchers.<String>any())).thenReturn("fi");
        when(uiService.getPhase(APPLICATION_SYSTEM_ID, FIRST_PHASE_ID, "fi")).thenReturn(modelResponse);
        when(uiService.savePhase(Matchers.<String>any(), Matchers.<String>any(), Matchers.<Map>any(), Matchers.<String>any())).thenReturn(modelResponse);
        when(authenticationService.getLangCookieName()).thenReturn("testi18next");
        this.formController = new FormController(uiService, pdfService, authenticationService);

        FORM.addChild(PHASE);
        when(applicationService.getApplication(Matchers.<String>any())).thenReturn(this.application);

        when(formService.getActiveForm(APPLICATION_SYSTEM_ID)).thenReturn(FORM);
    }

    @Test(expected = NullPointerException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationId() throws Exception {
        formController.getApplication(null, null);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGetPhaseModelSize() throws Exception {
        Viewable viewable = (Viewable) formController.getPhase(createRequest(), APPLICATION_SYSTEM_ID, FIRST_PHASE_ID).getEntity();
        assertEquals(4, ((Map) viewable.getModel()).size());
    }

    @Test
    public void testGetCategoryView() throws Exception {
        Viewable viewable = (Viewable) formController.getPhase(createRequest(), APPLICATION_SYSTEM_ID, FIRST_PHASE_ID).getEntity();
        assertEquals("/elements/Root", viewable.getTemplateName());
    }

    @Test
    public void testGetCategoryWrongView() throws Exception {
        Viewable viewable = (Viewable) formController.getPhase(createRequest(), APPLICATION_SYSTEM_ID, FIRST_PHASE_ID).getEntity();
        assertNotSame(null, viewable.getTemplateName());
    }

    @Test
    public void testSavePhaseInvalid() throws Exception {
        HashMap<String, I18nText> errorMessages = new HashMap<String, I18nText>();
        errorMessages.put("", ElementUtil.createI18NAsIs(""));
        this.modelResponse.setErrorMessages(errorMessages);
        HttpServletRequest request = createRequest();
        Viewable viewable = (Viewable) formController.savePhase(request, APPLICATION_SYSTEM_ID, FIRST_PHASE_ID, new MultivaluedMapImpl()).getEntity();
        assertEquals(FormController.ROOT_VIEW, viewable.getTemplateName());
    }

    @Test
    public void testSavePhaseValid() throws Exception {
        Response response = formController.savePhase(createRequest(), APPLICATION_SYSTEM_ID, FIRST_PHASE_ID, new MultivaluedMapImpl());
        String actual = ((URI) response.getMetadata().get("Location").get(0)).getPath();
        assertEquals(new RedirectToPhaseViewPath(APPLICATION_SYSTEM_ID, FIRST_PHASE_ID).getPath(), actual);
    }

    private HttpServletRequest createRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("testi18next", "fi");
        when(request.getCookies()).thenReturn(cookies);
        return request;
    }
}
