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

package fi.vm.sade.oppija.hakemus.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.domain.Application;

public class ApplicationServiceImplTest {

    ApplicationDAO applicationDAO;
    Application application;

    String SSN = "250584-3847";
    String OID = "1.2.3.4.5.0";
    String NAME = "Test Example";
    
    @Before
    public void setUp() {
        application = new Application();
        applicationDAO = mock(ApplicationDAO.class);
        when(applicationDAO.findByApplicantSsn(eq(SSN), anyString(), anyBoolean(), anyString())).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.findByApplicantName(eq(NAME), anyString(), anyBoolean(), anyString())).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.find(any(Application.class), anyString(), anyBoolean(), anyString())).thenReturn(Lists.newArrayList(application));
    }

    @Test
    public void testFindApplicationBySsn() {
        ApplicationServiceImpl service = new ApplicationServiceImpl
                (applicationDAO, null, null, null, null, null);
        List<Application> results = service.findApplications(SSN, "", Boolean.FALSE, "");
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByApplicantSsn(eq(SSN), anyString(), anyBoolean(), anyString());
    }
    
    @Test
    public void testFindApplicationByName() {
        ApplicationServiceImpl service = new ApplicationServiceImpl
                (applicationDAO, null, null, null, null, null);
        List<Application> results = service.findApplications(NAME, "", false, "");
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByApplicantName(eq(NAME), anyString(), anyBoolean(), anyString());
    }
    
    @Test
    public void testFindApplicationByOid() {
        ApplicationServiceImpl service = new ApplicationServiceImpl
                (applicationDAO, null, null, null, null, null);
        application.setOid(OID);
        List<Application> results = service.findApplications(OID, null, false, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).find(any(Application.class), anyString(), anyBoolean(), anyString());
    }
}
