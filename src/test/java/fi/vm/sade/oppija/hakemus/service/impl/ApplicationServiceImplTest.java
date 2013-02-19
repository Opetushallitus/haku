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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationOidService;

public class ApplicationServiceImplTest {

    ApplicationDAO applicationDAO;
    ApplicationOidService applicationOidService;
    Application application;

    String SSN = "250584-3847";
    String OID = "1.2.3.4.5.12345678901";
    String SHORT_OID = "12345678901";
    String NAME = "Test Example";
    
    @Before
    public void setUp() {
        application = new Application();
        applicationDAO = mock(ApplicationDAO.class);
        applicationOidService = mock(ApplicationOidService.class);
        when(applicationDAO.findByApplicantSsn(eq(SSN), anyString(), anyBoolean(), anyString())).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.findByApplicantName(eq(NAME), anyString(), anyBoolean(), anyString())).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.findByApplicationOid(eq(OID), anyString(), anyBoolean(), anyString())).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.findByOid(eq(SHORT_OID), anyString(), anyBoolean(), anyString())).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.find(any(Application.class))).thenReturn(Lists.newArrayList(application));
        when(applicationOidService.getOidPrefix()).thenReturn("1.2.3.4.5");
    }

    @Test
    public void testFindApplicationBySsn() {
        ApplicationServiceImpl service = new ApplicationServiceImpl
                (applicationDAO, null, null, null, applicationOidService, null);
        List<Application> results = service.findApplications(SSN, "", Boolean.FALSE, "");
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByApplicantSsn(eq(SSN), anyString(), anyBoolean(), anyString());
    }
    
    @Test
    public void testFindApplicationByName() {
        ApplicationServiceImpl service = new ApplicationServiceImpl
                (applicationDAO, null, null, null, applicationOidService, null);
        List<Application> results = service.findApplications(NAME, "", false, "");
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByApplicantName(eq(NAME), anyString(), anyBoolean(), anyString());
    }
    
    @Test
    public void testFindApplicationByOid() {
        ApplicationServiceImpl service = new ApplicationServiceImpl
                (applicationDAO, null, null, null, applicationOidService, null);
        application.setOid(OID);
        List<Application> results = service.findApplications(OID, "", false, "");
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByApplicationOid(eq(OID), anyString(), anyBoolean(), anyString());
    }
    
    @Test
    public void testFindApplicationByShortOid() {
        ApplicationServiceImpl service = new ApplicationServiceImpl
                (applicationDAO, null, null, null, applicationOidService, null);
        application.setOid(OID);
        List<Application> results = service.findApplications(SHORT_OID, "", false, "");
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByOid(eq(SHORT_OID), anyString(), anyBoolean(), anyString());
    }

    @Test
    public void testSaveApplicationAdditionalInfo() throws ResourceNotFoundException {
        ApplicationServiceImpl service = new ApplicationServiceImpl
                (applicationDAO, null, null, null, applicationOidService, null);
        Map<String, String> additionalInfo = new HashMap<String, String>();
        additionalInfo.put("key", "value");
        service.saveApplicationAdditionalInfo(OID, additionalInfo);
        verify(applicationDAO, times(1)).update(any(Application.class), any(Application.class));
    }
}
