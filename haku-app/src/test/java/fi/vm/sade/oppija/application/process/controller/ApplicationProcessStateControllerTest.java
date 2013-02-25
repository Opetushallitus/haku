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
package fi.vm.sade.oppija.application.process.controller;

import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.application.process.service.ApplicationProcessStateService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Mikko Majapuro
 */
public class ApplicationProcessStateControllerTest {

    private ApplicationProcessStateController applicationProcessStateController;
    private ApplicationProcessStateService applicationProcessStateService;
    private static final String oid = "1.2.3.4.5.0";

    @Before
    public void setUp() {
        applicationProcessStateService = mock(ApplicationProcessStateService.class);
        applicationProcessStateController = new ApplicationProcessStateController();
        applicationProcessStateController.applicationProcessStateService = applicationProcessStateService;
        ApplicationProcessState state = new ApplicationProcessState(oid, ApplicationProcessStateStatus.ACTIVE.toString());
        when(applicationProcessStateService.get(oid)).thenReturn(state);
    }

    @Test
    public void testGetApplicationProcessState() {
        ApplicationProcessState state = applicationProcessStateController.getApplicationProcessState(oid);
        Assert.assertEquals(oid, state.getOid());
        Assert.assertEquals(ApplicationProcessStateStatus.ACTIVE.toString(), state.getStatus());
    }

    @Test
    public void putToCancelledProcessStates() {
        applicationProcessStateController.putToCancelledProcessStates(oid);
        verify(applicationProcessStateService, times(1)).setApplicationProcessStateStatus(oid, ApplicationProcessStateStatus.CANCELLED);

    }

    @Test
    public void putToActiveProcessStates() {
        applicationProcessStateController.putToActiveProcessStates(oid);
        verify(applicationProcessStateService, times(1)).setApplicationProcessStateStatus(oid, ApplicationProcessStateStatus.ACTIVE);
    }
}
