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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;

public class ApplicationServiceImplTest {

    ApplicationDAO applicationDAO;
    ApplicationOidService applicationOidService;
    Application application;
    FormService formService;

    String SSN = "250584-3847";
    String OID = "1.2.3.4.5.12345678901";
    String SHORT_OID = "12345678901";
    String NAME = "Test Example";
    private ApplicationQueryParameters applicationQueryParameters;
    private ApplicationServiceImpl service;

    @Before
    public void setUp() {
        applicationQueryParameters = new ApplicationQueryParameters("", false, "", "");
        application = new Application();
        Map<String, String> answers = new HashMap<String, String>();
        answers.put("avain", "arvo");
        application.addVaiheenVastaukset("test", answers);
        applicationDAO = mock(ApplicationDAO.class);
        applicationOidService = mock(ApplicationOidService.class);
        formService = mock(FormService.class);
        when(applicationDAO.findByApplicantSsn(eq(SSN), eq(applicationQueryParameters))).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.findByApplicantName(eq(NAME), eq(applicationQueryParameters))).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.findByApplicationOid(eq(OID), eq(applicationQueryParameters))).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.findByOid(eq(SHORT_OID), eq(applicationQueryParameters))).thenReturn(Lists.newArrayList(application));
        when(applicationDAO.find(any(Application.class))).thenReturn(Lists.newArrayList(application));
        when(applicationOidService.getOidPrefix()).thenReturn("1.2.3.4.5");
        service = new ApplicationServiceImpl(applicationDAO, null, null, applicationOidService, null);
    }

    @Test
    public void testFindApplicationBySsn() {
        List<Application> results = service.findApplications(SSN, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByApplicantSsn(eq(SSN), eq(applicationQueryParameters));
    }

    @Test
    public void testFindApplicationByName() {
        List<Application> results = service.findApplications(NAME, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByApplicantName(eq(NAME), eq(applicationQueryParameters));
    }

    @Test
    public void testFindApplicationByOid() {
        application.setOid(OID);
        List<Application> results = service.findApplications(OID, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByApplicationOid(eq(OID), eq(applicationQueryParameters));
    }

    @Test
    public void testFindApplicationByShortOid() {
        application.setOid(OID);
        List<Application> results = service.findApplications(SHORT_OID, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(applicationDAO, only()).findByOid(eq(SHORT_OID), eq(applicationQueryParameters));
    }

    @Test
    public void testSaveApplicationAdditionalInfo() throws ResourceNotFoundException {
        Map<String, String> additionalInfo = new HashMap<String, String>();
        additionalInfo.put("key", "value");
        service.saveApplicationAdditionalInfo(OID, additionalInfo);
        verify(applicationDAO, times(1)).update(any(Application.class), any(Application.class));
    }

    @Test
    public void testGetApplicationKeyValue() throws ResourceNotFoundException {
        String value = service.getApplicationKeyValue(OID, "avain");
        assertNotNull(value);
        assertEquals("arvo", value);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetApplicationKeyValueKeyNotExists() throws ResourceNotFoundException {
        service.getApplicationKeyValue(OID, "nonExistingKey");
    }
}
