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

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ApplicationServiceImplTest {

    ApplicationDAO applicationDAO;
    ApplicationOidService applicationOidService;
    Application application;
    FormService formService;
    AuthenticationService authenticationService;

    String SSN = "250584-3847";
    String OID = "1.2.3.4.5.12345678901";
    String SHORT_OID = "12345678901";
    String PERSON_OID = "9.8.7.6.5";
    String NAME = "Test Example";
    String AS_ID = "1.2.246.562.5.741585101110";
    String AO_ID = "1.2.246.562.14.299022856910";
    Map<String, String> answerMap;
    private ApplicationQueryParameters applicationQueryParameters;
    private ApplicationServiceImpl service;

    @Before
    public void setUp() {
        applicationQueryParameters = new ApplicationQueryParameters("", "", "", 0, Integer.MAX_VALUE);
        application = new Application();
        Map<String, String> answers = new HashMap<String, String>();
        answers.put("avain", "arvo");
        application.addVaiheenVastaukset("test", answers);
        applicationDAO = mock(ApplicationDAO.class);
        applicationOidService = mock(ApplicationOidService.class);
        formService = mock(FormService.class);
        authenticationService = mock(AuthenticationService.class);

        ApplicationSearchResultDTO searchResultDTO = new ApplicationSearchResultDTO(1, Lists.newArrayList(new ApplicationSearchResultItemDTO()));
        when(applicationDAO.findByApplicantSsn(eq(SSN), eq(applicationQueryParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.findByApplicantName(eq(NAME), eq(applicationQueryParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.findByApplicationOid(eq(OID), eq(applicationQueryParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.findByOid(eq(SHORT_OID), eq(applicationQueryParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.find(any(Application.class))).thenReturn(Lists.newArrayList(application));
        when(applicationOidService.getOidPrefix()).thenReturn("1.2.3.4.5");
        when(authenticationService.addPerson(any(Person.class))).thenReturn(PERSON_OID);
        when(applicationDAO.findByApplicationSystemAndApplicationOption(eq(AS_ID), eq(AO_ID))).thenReturn(Lists.newArrayList(application));
        service = new ApplicationServiceImpl(applicationDAO, null, null, applicationOidService, authenticationService);

        answerMap = new HashMap<String, String>();
        answerMap.put(OppijaConstants.ELEMENT_ID_FIRST_NAMES, "Etunimi");
        answerMap.put(OppijaConstants.ELEMENT_ID_NICKNAME, "Etunimi");
        answerMap.put(OppijaConstants.ELEMENT_ID_LAST_NAME, "Sukunimi");
        answerMap.put(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER, "030506-229W");
        answerMap.put(OppijaConstants.ELEMENT_ID_SEX, "Mies");
        answerMap.put(OppijaConstants.ELEMENT_ID_HOME_CITY, "Kaupunki");
        answerMap.put(OppijaConstants.ELEMENT_ID_LANGUAGE, "fi");
        answerMap.put(OppijaConstants.ELEMENT_ID_NATIONALITY, OppijaConstants.NATIONALITY_CODE_FI);
        answerMap.put(OppijaConstants.ELEMENT_ID_FIRST_LANGUAGE, "fi");
    }

    @Test
    public void testFindApplicationBySsn() {
        ApplicationSearchResultDTO results = service.findApplications(SSN, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findByApplicantSsn(eq(SSN), eq(applicationQueryParameters));
    }

    @Test
    public void testFindApplicationByName() {
        ApplicationSearchResultDTO results = service.findApplications(NAME, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findByApplicantName(eq(NAME), eq(applicationQueryParameters));
    }

    @Test
    public void testFindApplicationByOid() {
        application.setOid(OID);
        ApplicationSearchResultDTO results = service.findApplications(OID, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findByApplicationOid(eq(OID), eq(applicationQueryParameters));
    }

    @Test
    public void testFindApplicationByShortOid() {
        application.setOid(OID);
        ApplicationSearchResultDTO results = service.findApplications(SHORT_OID, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
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

    @Test
    public void testPutApplicationAdditionalInfoKeyValue() throws ResourceNotFoundException {
        service.putApplicationAdditionalInfoKeyValue(OID, "key", "value");
        verify(applicationDAO, times(1)).find(any(Application.class));
        verify(applicationDAO, times(1)).update(any(Application.class), any(Application.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testPutApplicationAdditionalInfoKeyValueIllegalKey() throws ResourceNotFoundException {
        service.putApplicationAdditionalInfoKeyValue(OID, "avain", "value");
        verify(applicationDAO, times(1)).find(any(Application.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutApplicationAdditionalInfoKeyValueNullValue() throws ResourceNotFoundException {
        service.putApplicationAdditionalInfoKeyValue(OID, "key", null);
    }

    @Test
    public void testSetPersonFi() {
        Application application = new Application();

        application.addVaiheenVastaukset("henkilotiedot", answerMap);
        application = service.addPersonAndAuthenticate(application);
        assertNotNull("PersonOid should not be null", application.getPersonOid());
        assertEquals("Wrong person oid", PERSON_OID, application.getPersonOid());
    }

    @Test
    public void testSetPersonNotFi() {
        Application application = new Application();
        answerMap.remove(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER);
        answerMap.put(OppijaConstants.ELEMENT_ID_NATIONALITY, "swe");
        application.addVaiheenVastaukset("henkilotiedot", answerMap);
        application = service.addPersonAndAuthenticate(application);
        assertNull("PersonOid should be null", application.getPersonOid());
    }

    @Test
    public void testGetApplicationsByApplicationSystemAndApplicationOption() {
        List<Application> results = service.getApplicationsByApplicationSystemAndApplicationOption(AS_ID, AO_ID);
        assertNotNull(results);
        assertEquals(1, results.size());
    }
}
