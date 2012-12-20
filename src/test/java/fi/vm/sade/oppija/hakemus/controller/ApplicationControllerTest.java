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

package fi.vm.sade.oppija.hakemus.controller;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationControllerTest {

    public static final String TERM = "term";

    private ApplicationService applicationService;
    private Application application;
    private ApplicationController applicationController;

    @Before
    public void setUp() throws Exception {
        this.application = new Application();
        this.applicationService = mock(ApplicationService.class);
        when(applicationService.getApplication(TERM)).thenReturn(this.application);
        applicationController = new ApplicationController();
        applicationController.applicationService = this.applicationService;
    }

    @Test
    public void testSearchApplicationsFound() throws Exception {
        List<Application> applications = applicationController.searchApplications(TERM);
        assertFalse(applications.isEmpty());
    }


    @Test
    public void testGetApplication() throws Exception {
        Application application = applicationController.getApplication(TERM);
        assertEquals(this.application, application);
    }
}
