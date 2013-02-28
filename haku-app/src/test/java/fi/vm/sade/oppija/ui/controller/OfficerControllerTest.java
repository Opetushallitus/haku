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

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.InputParameter;
import fi.vm.sade.oppija.common.valintaperusteet.ValintaperusteetService;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;

/**
 * @author Mikko Majapuro
 */
public class OfficerControllerTest {

    private OfficerController officerController;
    private static final String OID = "1.2.3.4.5.0";

    @Before
    public void setUp() throws Exception {
        officerController = new OfficerController();
        ApplicationService applicationService = mock(ApplicationService.class);
        FormService formService = mock(FormService.class);
        ValintaperusteetService valintaperusteetService = mock(ValintaperusteetService.class);

        FormId formId = new FormId("Yhteishaku", "yhteishaku");
        Application app = new Application(formId, OID);
        app.setPhaseId("valmis");
        when(applicationService.getApplication(OID)).thenReturn(app);
        when(applicationService.getApplicationPreferenceOids(anyString())).thenReturn(new ArrayList<String>());

        Phase phase = new Phase("esikatselu", createI18NText("esikatselu"), true);
        when(formService.getLastPhase("Yhteishaku", "yhteishaku")).thenReturn(phase);

        Form form = new Form("yhteishaku", createI18NText("yhteishaku"));
        form.addChild(new Phase("henkilotiedot", createI18NText("henkilotiedot"), false));
        form.addChild(phase);
        form.init();
        when(formService.getActiveForm("Yhteishaku", "yhteishaku")).thenReturn(form);

        ApplicationState applicationState = new ApplicationState(app, "henkilotiedot");
        when(applicationService.saveApplicationPhase(any(ApplicationPhase.class), eq(OID), eq(false))).thenReturn(applicationState);

        AdditionalQuestions additionalQuestions = new AdditionalQuestions();
        additionalQuestions.addParameter(OID, new InputParameter("avain", "KOKONAISLUKU", "1"));
        when(valintaperusteetService.retrieveAdditionalQuestions(anyList())).thenReturn(additionalQuestions);

        officerController.applicationService = applicationService;
        officerController.formService = formService;
        officerController.valintaperusteetService = valintaperusteetService;
    }

    @Test
    public void testGetApplication() throws Exception {
        Response response = officerController.redirectToLastPhase(OID);
        assertEquals("/virkailija/hakemus/Yhteishaku/yhteishaku/esikatselu/" + OID, getLocationHeader(response));
    }

    @Test
    public void testGetPhase() throws Exception {
        Viewable viewable = officerController.getPhase("Yhteishaku", "yhteishaku", "esikatselu", OID);
        Map<String, Object> model = (Map<String, Object>) viewable.getModel();
        assertEquals("esikatselu", ((Element) model.get("element")).getId());
        assertEquals(OID, model.get("oid"));
        assertEquals("yhteishaku", ((Form) model.get("form")).getId());
        assertEquals("valmis", model.get("applicationPhaseId"));
        assertEquals("yhteishaku", ((FormId) model.get("hakemusId")).getFormId());
    }

    @Test
    public void testSavePhase() throws URISyntaxException {
        Response response = officerController.savePhase("Yhteishaku", "yhteishaku", "henkilotiedot", OID, new MultivaluedMapImpl());
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
    }

    @Test
    public void testChangeApplicationProcessState() throws URISyntaxException, ResourceNotFoundException {
        Response response = officerController.changeApplicationProcessState(OID, "PASSIVE");
        assertEquals("/virkailija/hakemus/Yhteishaku/yhteishaku/esikatselu/" + OID, getLocationHeader(response));
        verify(officerController.applicationService, times(1)).setApplicationState(OID, "PASSIVE");
    }

    @Test
    public void testGetAdditionalInfo() throws ResourceNotFoundException, IOException {
        Viewable viewable = officerController.getAdditionalInfo(OID);
        Map<String, Object> model = (Map<String, Object>) viewable.getModel();
        assertNotNull(model);
        assertTrue(model.containsKey("application"));
        assertEquals(OID, ((Application) model.get("application")).getOid());
        assertTrue(model.containsKey("additionalQuestions"));
        assertEquals(1, ((AdditionalQuestions) model.get("additionalQuestions")).getAllQuestions().size());
    }

    @Test
    public void testSaveAdditionalInfo() throws ResourceNotFoundException, URISyntaxException {
        MultivaluedMap<String, String> additionalInfo = new MultivaluedMapImpl();
        List<String> values = new ArrayList<String>();
        values.add("value");
        additionalInfo.put("key", values);
        Response response = officerController.saveAdditionalInfo(OID, additionalInfo);
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
        verify(officerController.applicationService, times(1)).saveApplicationAdditionalInfo(eq(OID), anyMap());
    }

    private String getLocationHeader(final Response response) {
        return response.getMetadata().get("Location").get(0).toString();
    }
}
