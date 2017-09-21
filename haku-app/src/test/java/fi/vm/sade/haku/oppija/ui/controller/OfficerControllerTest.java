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

import fi.vm.sade.haku.OppijaAuditLogger;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.impl.UserSession;
import fi.vm.sade.haku.oppija.ui.service.OfficerUIService;

import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.common.collect.Lists.newArrayList;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mikko Majapuro
 */
public class OfficerControllerTest {

    public static final String ASID = "dummyAsid";
    public static final String PREVIEW_PHASE = "esikatselu";
    private OfficerController officerController;
    private static final String OID = "1.2.3.4.5.0";

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {

        //officerController.oppijaAuditLogger

        ApplicationService applicationService = mock(ApplicationService.class);
        FormService formService = mock(FormService.class);

        Application app = new Application(ASID, OID);
        app.setPhaseId("valmis");
        when(applicationService.getApplication(OID)).thenReturn(app);

        Element phase = new PhaseBuilder(PREVIEW_PHASE).setEditAllowedByRoles("TESTING")
                .preview()
                .i18nText(createI18NAsIs(PREVIEW_PHASE)).build();

        Form form = new Form("yhteishaku", createI18NAsIs("yhteishaku"));
        form.addChild(new PhaseBuilder("henkilotiedot").setEditAllowedByRoles("TESTING")
                .i18nText(createI18NAsIs("henkilotiedot")).build());

        form.addChild(phase);
        when(formService.getActiveForm(ASID)).thenReturn(form);

        OfficerUIService officerApplicationService = mock(OfficerUIService.class);
        ModelResponse modelResponse = new ModelResponse();
        when(officerApplicationService.getValidatedApplication(OID, PREVIEW_PHASE,true)).thenReturn(modelResponse);
        when(officerApplicationService.getAdditionalInfo(OID)).thenReturn(modelResponse);
        when(officerApplicationService.updateApplication(eq(OID), any(ApplicationPhase.class), any(User.class))).thenReturn(modelResponse);
        when(officerApplicationService.getApplicationWithLastPhase(eq(OID))).thenReturn(app);

        officerController = new OfficerController(officerApplicationService, null, formService, null, null,
                null, null, mock(OppijaAuditLogger.class), mock(AuthenticationService.class));
        officerController.userSession = mock(UserSession.class);
    }

    @Test
    public void testUpdatePhase() throws URISyntaxException, IOException {
        Response response = null;
        response = officerController.updatePhase(mock(HttpServletRequest.class), ASID, "henkilotiedot", OID, new MultivaluedMapImpl());
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAdditionalInfo() throws IOException {
        Viewable viewable = officerController.getAdditionalInfo(mock(HttpServletRequest.class), OID);
        assertEquals(OfficerController.ADDITIONAL_INFO_VIEW, viewable.getTemplateName());
    }

    @Test
    public void testSaveAdditionalInfo() throws URISyntaxException, IOException {
        MultivaluedMap<String, String> additionalInfo = new MultivaluedMapImpl();
        additionalInfo.put("key", newArrayList("value"));
        Response response = officerController.saveAdditionalInfo(mock(HttpServletRequest.class), OID, additionalInfo);
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
    }

    private String getLocationHeader(final Response response) {
        return response.getMetadata().get("Location").get(0).toString();
    }
}
