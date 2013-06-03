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

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.InputParameter;
import fi.vm.sade.oppija.common.valintaperusteet.ValintaperusteetService;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.ui.service.OfficerUIService;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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
        officerController = new OfficerController();
        ApplicationService applicationService = mock(ApplicationService.class);
        FormService formService = mock(FormService.class);
        ValintaperusteetService valintaperusteetService = mock(ValintaperusteetService.class);

        FormId formId = new FormId(ASID, "yhteishaku");
        Application app = new Application(formId, OID);
        app.setPhaseId("valmis");
        when(applicationService.getApplication(OID)).thenReturn(app);
        when(applicationService.getApplicationPreferenceOids(anyString())).thenReturn(new ArrayList<String>());

        Phase phase = new Phase(PREVIEW_PHASE, createI18NAsIs(PREVIEW_PHASE), true);
        when(formService.getLastPhase(ASID, "yhteishaku")).thenReturn(phase);

        Form form = new Form("yhteishaku", createI18NAsIs("yhteishaku"));
        form.addChild(new Phase("henkilotiedot", createI18NAsIs("henkilotiedot"), false));
        form.addChild(phase);
        when(formService.getActiveForm(ASID, "yhteishaku")).thenReturn(form);

        ApplicationState applicationState = new ApplicationState(app, "henkilotiedot");
        when(applicationService.saveApplicationPhase(any(ApplicationPhase.class), eq(OID), eq(false))).thenReturn(applicationState);

        AdditionalQuestions additionalQuestions = new AdditionalQuestions();
        additionalQuestions.addParameter(OID, new InputParameter("avain", "KOKONAISLUKU", "1"));
        when(valintaperusteetService.retrieveAdditionalQuestions(anyList())).thenReturn(additionalQuestions);

        OfficerUIService officerApplicationService = mock(OfficerUIService.class);
        UIServiceResponse uiServiceResponse = new UIServiceResponse();
        when(officerApplicationService.getValidatedApplication(OID, PREVIEW_PHASE)).thenReturn(uiServiceResponse);
        when(officerApplicationService.getAdditionalInfo(OID)).thenReturn(uiServiceResponse);
        when(officerApplicationService.updateApplication(eq(OID), any(ApplicationPhase.class))).thenReturn(uiServiceResponse);
        when(officerApplicationService.getApplicationWithLastPhase(eq(OID))).thenReturn(app);
        officerController.officerUIService = officerApplicationService;
    }

    @Test
    public void testGetApplication() throws Exception {
        Response response = officerController.redirectToLastPhase(OID);
        assertEquals("/virkailija/hakemus/" + ASID + "/yhteishaku/valmis/" + OID, getLocationHeader(response));
    }

    @Test
    public void testGetPhase() throws Exception {
        Viewable viewable = officerController.getPreview(ASID, "yhteishaku", PREVIEW_PHASE, OID);
        assertEquals(OfficerController.VIRKAILIJA_PHASE_VIEW, viewable.getTemplateName());
    }

    @Test
    public void testSavePhase() throws URISyntaxException, ResourceNotFoundException {
        Response response = officerController.updatePhase(ASID, "yhteishaku", "henkilotiedot", OID, new MultivaluedMapImpl());
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAdditionalInfo() throws ResourceNotFoundException, IOException {
        Viewable viewable = officerController.getAdditionalInfo(OID);
        assertEquals(OfficerController.ADDITIONAL_INFO_VIEW, viewable.getTemplateName());
    }

    @Test
    public void testSaveAdditionalInfo() throws ResourceNotFoundException, URISyntaxException, IOException {
        MultivaluedMap<String, String> additionalInfo = new MultivaluedMapImpl();
        additionalInfo.put("key", newArrayList("value"));
        Response response = officerController.saveAdditionalInfo(OID, additionalInfo);
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
    }

    private String getLocationHeader(final Response response) {
        return response.getMetadata().get("Location").get(0).toString();
    }
}
