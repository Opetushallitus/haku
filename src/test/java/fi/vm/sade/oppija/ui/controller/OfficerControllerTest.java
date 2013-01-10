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

import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.application.process.service.ApplicationProcessStateService;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Mikko Majapuro
 */
public class OfficerControllerTest {

    private OfficerController officerController;

    @Before
    public void setUp() throws Exception {
        officerController = new OfficerController();
        ApplicationService applicationService = mock(ApplicationService.class);
        FormService formService = mock(FormService.class);
        ApplicationProcessStateService applicationProcessStateService = mock(ApplicationProcessStateService.class);

        FormId formId = new FormId("Yhteishaku", "yhteishaku");
        String oid = "1.2.3.4.5.0";
        Application app = new Application(formId, oid);
        app.setVaiheId("valmis");
        when(applicationService.getApplication(oid)).thenReturn(app);

        Phase phase = new Phase("esikatselu", "esikatselu", true);
        when(formService.getLastPhase("Yhteishaku", "yhteishaku")).thenReturn(phase);

        Form form = new Form("yhteishaku", "yhteishaku");
        form.addChild(new Phase("henkilotiedot", "henkilotiedot", false));
        form.addChild(phase);
        form.init();
        when(formService.getActiveForm("Yhteishaku", "yhteishaku")).thenReturn(form);

        ApplicationState applicationState = new ApplicationState(app, "henkilotiedot");
        when(applicationService.saveApplicationPhase(any(ApplicationPhase.class), eq("1.2.3.4.5.0"))).thenReturn(applicationState);

        ApplicationProcessState processState = new ApplicationProcessState(oid, ApplicationProcessStateStatus.ACTIVE.toString());
        when(applicationProcessStateService.get(oid)).thenReturn(processState);

        officerController.applicationService = applicationService;
        officerController.formService = formService;
        officerController.applicationProcessStateService = applicationProcessStateService;
    }

    @Test
    public void testGetApplication() throws Exception {
        String redirect = officerController.getApplication("1.2.3.4.5.0");
        assertEquals("redirect:/virkailija/hakemus/Yhteishaku/yhteishaku/esikatselu/1.2.3.4.5.0/", redirect);
    }

    @Test
    public void testGetPhase() throws Exception {
        ModelAndView mv = officerController.getPhase("Yhteishaku", "yhteishaku", "esikatselu", "1.2.3.4.5.0");
        assertEquals("esikatselu", ((Element) mv.getModel().get("element")).getId());
        assertEquals("1.2.3.4.5.0", mv.getModel().get("oid"));
        assertEquals("yhteishaku", ((Form) mv.getModel().get("form")).getId());
        assertEquals("valmis", mv.getModel().get("applicationPhaseId"));
        assertEquals("yhteishaku", ((FormId) mv.getModel().get("hakemusId")).getFormId());
    }

    @Test
    public void testSavePhase() {
        MultiValueMap<String, String> multiValues = new LinkedMultiValueMap<String, String>();
        ModelAndView mv = officerController.savePhase("Yhteishaku", "yhteishaku", "henkilotiedot", "1.2.3.4.5.0", multiValues);
        assertEquals("redirect:/virkailija/hakemus/Yhteishaku/yhteishaku/esikatselu/1.2.3.4.5.0/", mv.getViewName());
    }

    @Test
    public void testChangeApplicationProcessState() {
        String redirect = officerController.changeApplicationProcessState("1.2.3.4.5.0", "CANCELLED");
        assertEquals("redirect:/virkailija/hakemus/Yhteishaku/yhteishaku/esikatselu/1.2.3.4.5.0/", redirect);
        verify(officerController.applicationProcessStateService, times(1)).setApplicationProcessStateStatus("1.2.3.4.5.0", ApplicationProcessStateStatus.CANCELLED);
    }
}
