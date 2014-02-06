package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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

public class ApplicationServiceImplTest {

    ApplicationDAO applicationDAO;
    ApplicationOidService applicationOidService;
    Application application;
    FormService formService;
    AuthenticationService authenticationService;
    OrganizationService organizationService;
    HakuPermissionService hakuPermissionService;
    SuoritusrekisteriService suoritusrekisteriService;

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
    private ApplicationSystemService applicationSystemService;
    private ElementTreeValidator elementTreeValidator;

    @Before
    public void setUp() {
        applicationQueryParameters = new ApplicationQueryParameters(null, null, "", "", "", false, "", "", 0, Integer.MAX_VALUE, "fullName", 1);
        application = new Application();
        Map<String, String> answers = new HashMap<String, String>();
        answers.put("avain", "arvo");
        application.addVaiheenVastaukset("test", answers);
        applicationDAO = mock(ApplicationDAO.class);
        applicationOidService = mock(ApplicationOidService.class);
        formService = mock(FormService.class);
        authenticationService = new AuthenticationServiceMockImpl();
        organizationService = mock(OrganizationService.class);
        hakuPermissionService = mock(HakuPermissionService.class);
        applicationSystemService = mock(ApplicationSystemService.class);
        suoritusrekisteriService = mock(SuoritusrekisteriService.class);
        ValidatorFactory validatorFactory = mock(ValidatorFactory.class);
        elementTreeValidator = new ElementTreeValidator(validatorFactory);

        ApplicationSearchResultDTO searchResultDTO = new ApplicationSearchResultDTO(1, Lists.newArrayList(new ApplicationSearchResultItemDTO()));
        when(applicationDAO.findAllQueried(eq(SSN), eq(applicationQueryParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.findAllQueried(eq(NAME), eq(applicationQueryParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.findAllQueried(eq(OID), eq(applicationQueryParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.findAllQueried(eq(SHORT_OID), eq(applicationQueryParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.find(any(Application.class))).thenReturn(Lists.newArrayList(application));
        //when(authenticationService.addPerson(any(Person.class))).thenReturn(PERSON_OID);
        when(applicationDAO.findByApplicationSystemAndApplicationOption(eq(AS_ID), eq(AO_ID))).thenReturn(Lists.newArrayList(application));
        when(hakuPermissionService.userCanReadApplication(any(Application.class))).thenReturn(true);
//        when(suoritusrekisteriService.getLahtokoulu(any(String.class))).thenReturn("1.2.246.562.10.56695937518");
//        when(suoritusrekisteriService.getLahtoluokka(any(String.class))).thenReturn("9A");

        service = new ApplicationServiceImpl(applicationDAO, null, null, applicationOidService, authenticationService, organizationService,
                hakuPermissionService, applicationSystemService, suoritusrekisteriService, elementTreeValidator);

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
        verify(applicationDAO, only()).findAllQueried(eq(SSN), eq(applicationQueryParameters));
    }

    @Test
    public void testFindApplicationByName() {
        ApplicationSearchResultDTO results = service.findApplications(NAME, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findAllQueried(eq(NAME), eq(applicationQueryParameters));
    }

    @Test
    public void testFindApplicationByOid() {
        application.setOid(OID);
        ApplicationSearchResultDTO results = service.findApplications(OID, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findAllQueried(eq(OID), eq(applicationQueryParameters));
    }

    @Test
    public void testFindApplicationByShortOid() {
        application.setOid(OID);
        ApplicationSearchResultDTO results = service.findApplications(SHORT_OID, applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findAllQueried(eq(SHORT_OID), eq(applicationQueryParameters));
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
        verify(applicationDAO, times(1)).updateKeyValue(eq(OID), eq("additionalInfo.key"), eq("value"));
    }

    @Test
    public void testPutApplicationAdditionalInfoKeyValueIllegalKey() throws ResourceNotFoundException {
        service.putApplicationAdditionalInfoKeyValue(OID, "avain", "value");
        verify(applicationDAO, times(1)).updateKeyValue(eq(OID), eq("additionalInfo.avain"), eq("value"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutApplicationAdditionalInfoKeyValueNullValue() throws ResourceNotFoundException {
        service.putApplicationAdditionalInfoKeyValue(OID, "key", null);
    }

    @Test
    public void testSetPersonFi() {
        Application application = new Application();

        application.addVaiheenVastaukset("henkilotiedot", answerMap);
        application = service.addPersonOid(application);
        assertNotNull("PersonOid should not be null", application.getPersonOid());
    }

    @Test
    public void testSetPersonNotFi() {
        Application application = new Application();
        answerMap.remove(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER);
        answerMap.put(OppijaConstants.ELEMENT_ID_NATIONALITY, "swe");
        application.addVaiheenVastaukset("henkilotiedot", answerMap);
        application = service.addPersonOid(application);
        assertNotNull("PersonOid should not be null", application.getPersonOid());
    }
}
