/*
 *
 *  * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *  *
 *  * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 *  * soon as they will be approved by the European Commission - subsequent versions
 *  * of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * European Union Public Licence for more details.
 *
 */

package fi.vm.sade.oppija.application.process.service.impl;

import fi.vm.sade.oppija.application.process.dao.ApplicationProcessStateDAO;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.application.process.service.ApplicationProcessStateService;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Mikko Majapuro
 */
public class ApplicationProcessStateServiceImplTest {

    private ApplicationProcessStateService applicationProcessStateService;
    private ApplicationProcessStateDAO dao;
    private static final String oid = "1.2.3.4.5.0";

    @Before
    public void setUp() {
        dao = mock(ApplicationProcessStateDAO.class);
        ApplicationProcessState status = new ApplicationProcessState(oid, ApplicationProcessStateStatus.ACTIVE.toString());
        when(dao.findOne(any(ApplicationProcessState.class))).thenReturn(status);
        applicationProcessStateService = new ApplicationProcessStateServiceImpl(dao);
    }

    @Test
    public void testGet() {
        ApplicationProcessState state = applicationProcessStateService.get(oid);
        assertEquals(oid, state.getOid());
        assertEquals(ApplicationProcessStateStatus.ACTIVE.toString(), state.getStatus());
    }

    @Test
    public void testSetApplicationProcessStateStatus() {
        applicationProcessStateService.setApplicationProcessStateStatus(oid, ApplicationProcessStateStatus.CANCELLED);
        verify(dao, times(1)).findOne(any(ApplicationProcessState.class));
        verify(dao, times(1)).update(any(ApplicationProcessState.class), any(ApplicationProcessState.class));
    }
}
