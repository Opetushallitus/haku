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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;

public class ApplicationControllerTest {

    public static final String TERM = "term";

    private ApplicationService applicationService;
    private Application application;
    private ApplicationController applicationController;

    @Test
    public void testSearchApplicationsFound() throws Exception {
        this.application = new Application();
        this.applicationService = mock(ApplicationService.class);
        when(applicationService.findApplications(TERM)).thenReturn(Lists.newArrayList(this.application));
        applicationController = new ApplicationController();
        applicationController.applicationService = this.applicationService;
        
        List<Application> applications = applicationController.searchApplications(TERM);
        assertFalse(applications.isEmpty());
    }
    
    @Test
    public void testSearchApplicationsNotFound() throws Exception {
        this.application = new Application();
        this.applicationService = mock(ApplicationService.class);
        when(applicationService.findApplications(TERM)).thenReturn(new LinkedList<Application>());
        applicationController = new ApplicationController();
        applicationController.applicationService = this.applicationService;
        
        List<Application> applications = applicationController.searchApplications(TERM);
        assertNotNull(applications);
        assertTrue(applications.isEmpty());
    }

    @Test
    public void testGetApplication() throws Exception {
        this.application = new Application();
        this.applicationService = mock(ApplicationService.class);
        when(applicationService.getApplication(TERM)).thenReturn(this.application);
        applicationController = new ApplicationController();
        applicationController.applicationService = this.applicationService;
        
        Application application = applicationController.getApplication(TERM);
        assertEquals(this.application, application);
    }

}
