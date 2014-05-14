package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
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
    AuthenticationService authenticationService;
    OrganizationService organizationService;
    HakuPermissionService hakuPermissionService;
    SuoritusrekisteriService suoritusrekisteriService;

    String SSN = "250584-3847";
    String OID = "1.2.3.4.5.12345678901";
    String SHORT_OID = "12345678901";
    String NAME = "Test Example";
    String AS_ID = "1.2.246.562.5.741585101110";
    String AO_ID = "1.2.246.562.14.299022856910";
    Map<String, String> answerMap;
    private ApplicationQueryParameters applicationQueryParameters;
    private ApplicationServiceImpl service;
    private ElementTreeValidator elementTreeValidator;

    @Before
    public void setUp() {
        applicationQueryParameters = new ApplicationQueryParameters(null, null, "", "", "", false, "", "", new Date(), 0, Integer.MAX_VALUE, "fullName", 1);
        application = new Application();
        Map<String, String> answers = new HashMap<String, String>();
        answers.put("avain", "arvo");
        application.addVaiheenVastaukset("test", answers);
        applicationDAO = mock(ApplicationDAO.class);
        applicationOidService = mock(ApplicationOidService.class);
        authenticationService = new AuthenticationServiceMockImpl();
        organizationService = mock(OrganizationService.class);
        hakuPermissionService = mock(HakuPermissionService.class);
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
                hakuPermissionService, suoritusrekisteriService, elementTreeValidator);

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
    public void testSaveApplicationAdditionalInfo() {
        Map<String, String> additionalInfo = new HashMap<String, String>();
        additionalInfo.put("key", "value");
        service.saveApplicationAdditionalInfo(OID, additionalInfo);
        verify(applicationDAO, times(1)).update(any(Application.class), any(Application.class));
    }

    @Test
    public void testGetApplicationKeyValue()  {
        String value = service.getApplicationKeyValue(OID, "avain");
        assertNotNull(value);
        assertEquals("arvo", value);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetApplicationKeyValueKeyNotExists() {
        service.getApplicationKeyValue(OID, "nonExistingKey");
    }

    @Test
    public void testPutApplicationAdditionalInfoKeyValue()  {
        service.putApplicationAdditionalInfoKeyValue(OID, "key", "value");
        verify(applicationDAO, times(1)).updateKeyValue(eq(OID), eq("additionalInfo.key"), eq("value"));
    }

    @Test
    public void testPutApplicationAdditionalInfoKeyValueIllegalKey() {
        service.putApplicationAdditionalInfoKeyValue(OID, "avain", "value");
        verify(applicationDAO, times(1)).updateKeyValue(eq(OID), eq("additionalInfo.avain"), eq("value"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutApplicationAdditionalInfoKeyValueNullValue() {
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

    @Test
    public void testSendingSchool() {
        OpiskelijaDTO opiskelija = new OpiskelijaDTO("oppilaitos", "9", "9A", "henkiloOid", null);
        SuoritusDTO suoritus = new SuoritusDTO("suoritusId", "peruskoulu", "1.2.3", "KESKEN",
                new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24), "henkiloOid",
                "Ei", "SV");

        List<OpiskelijaDTO> opiskelijat = Lists.newArrayList(opiskelija);
        List<SuoritusDTO> suoritukset = Lists.newArrayList(suoritus);
        when(suoritusrekisteriService.getOpiskelijat(any(String.class))).thenReturn(opiskelijat);
        when(suoritusrekisteriService.getSuoritukset(any(String.class))).thenReturn(suoritukset);

        Application application = new Application();
        application.setPersonOid("1.2.3");
        application.setOid("4.5.6");
        Map<String, String> koulutustausta = new HashMap<String, String>();
        koulutustausta.put(OppijaConstants.ELEMENT_ID_BASE_EDUCATION, OppijaConstants.YLIOPPILAS);
        koulutustausta.put(OppijaConstants.LUKIO_KIELI, "FI");
        koulutustausta.put(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI, "2013");
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, koulutustausta);
        application = service.addSendingSchool(application);
        application = service.addBaseEducation(application);
        koulutustausta = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);

        assertEquals("SV", koulutustausta.get(OppijaConstants.PERUSOPETUS_KIELI));
        assertEquals("FI", koulutustausta.get(OppijaConstants.LUKIO_KIELI));
        assertEquals(OppijaConstants.PERUSKOULU, koulutustausta.get(OppijaConstants.ELEMENT_ID_BASE_EDUCATION));
        assertEquals(OppijaConstants.YLIOPPILAS, koulutustausta.get(OppijaConstants.ELEMENT_ID_BASE_EDUCATION_USER));
        assertEquals("9", koulutustausta.get(OppijaConstants.ELEMENT_ID_CLASS_LEVEL));
        assertNull(koulutustausta.get(OppijaConstants.ELEMENT_ID_CLASS_LEVEL + "_user"));
        assertEquals("9A", koulutustausta.get(OppijaConstants.ELEMENT_ID_SENDING_CLASS));
        assertEquals("oppilaitos", koulutustausta.get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL));
    }

    @Test
    public void testSendingSchoolEqual() {
        OpiskelijaDTO opiskelija = new OpiskelijaDTO("oppilaitos", "9", "9A", "henkiloOid", null);
        SuoritusDTO suoritus = new SuoritusDTO("suoritusId", "peruskoulu", "1.2.3", "KESKEN",
                new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24), "henkiloOid",
                "Ei", "SV");

        List<OpiskelijaDTO> opiskelijat = Lists.newArrayList(opiskelija);
        List<SuoritusDTO> suoritukset = Lists.newArrayList(suoritus);
        when(suoritusrekisteriService.getOpiskelijat(any(String.class))).thenReturn(opiskelijat);
        when(suoritusrekisteriService.getSuoritukset(any(String.class))).thenReturn(suoritukset);

        Calendar cal = GregorianCalendar.getInstance();

        Application application = new Application();
        application.setPersonOid("1.2.3");
        Map<String, String> koulutustausta = new HashMap<String, String>();
        koulutustausta.put(OppijaConstants.ELEMENT_ID_BASE_EDUCATION, OppijaConstants.PERUSKOULU);
        koulutustausta.put(OppijaConstants.LUKIO_KIELI, "SV");
        koulutustausta.put(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI, String.valueOf(cal.get(Calendar.YEAR)));
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, koulutustausta);
        application = service.addSendingSchool(application);
        koulutustausta = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);

        assertEquals("SV", koulutustausta.get(OppijaConstants.LUKIO_KIELI));
        assertEquals(OppijaConstants.PERUSKOULU, koulutustausta.get(OppijaConstants.ELEMENT_ID_BASE_EDUCATION));
        assertEquals("oppilaitos", koulutustausta.get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL));
        assertNull(koulutustausta.get(OppijaConstants.LUKIO_KIELI + "_user"));
        assertNull(koulutustausta.get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL + "_user"));
        assertNull(koulutustausta.get(OppijaConstants.ELEMENT_ID_BASE_EDUCATION + "_user"));
    }
}
