package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.collect.Lists;

import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.AuthorizationMeta;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationFilterParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParametersBuilder;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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
    ApplicationSystemService applicationSystemService;
    SuoritusrekisteriService suoritusrekisteriService;
    KoulutusinformaatioService koulutusinformaatioService;

    String SSN = "250584-3847";
    String OID = "1.2.3.4.5.12345678901";
    String SHORT_OID = "12345678901";
    String NAME = "Test Example";
    String AS_ID = "1.2.246.562.5.741585101110";
    String AO_ID = "1.2.246.562.14.299022856910";
    Map<String, String> answerMap;
    private ApplicationQueryParameters applicationQueryParameters;
    private ApplicationFilterParameters filterParameters;
    private ApplicationServiceImpl service;
    private ElementTreeValidator elementTreeValidator;

    @Before
    public void setUp() {

        applicationQueryParameters = new ApplicationQueryParametersBuilder()
                .setStates(null)
                .setAsIds(null)
                .setAoId("")
                .setLopOid("")
                .setAoOid("")
                .setGroupOid("")
                .setBaseEducation("")
                .setDiscretionaryOnly(false)
                .setSendingSchool("")
                .setSendingClass("")
                .setUpdatedAfter(new Date())
                .setStart(0)
                .setRows(Integer.MAX_VALUE)
                .setOrderBy("fullName")
                .setOrderDir(1).build();
        filterParameters = new ApplicationFilterParameters(6, new ArrayList<String>(), new ArrayList<String>(), "");
        application = new Application();
        Map<String, String> answers = new HashMap<String, String>();
        answers.put("avain", "arvo");
        application.addVaiheenVastaukset("test", answers);
        applicationDAO = mock(ApplicationDAO.class);
        applicationOidService = mock(ApplicationOidService.class);
        authenticationService = new AuthenticationServiceMockImpl();
        organizationService = mock(OrganizationService.class);
        hakuPermissionService = mock(HakuPermissionService.class);
        applicationSystemService = mock(ApplicationSystemService.class);
        suoritusrekisteriService = mock(SuoritusrekisteriService.class);
        ValidatorFactory validatorFactory = mock(ValidatorFactory.class);
        elementTreeValidator = new ElementTreeValidator(validatorFactory);
        koulutusinformaatioService = mock(KoulutusinformaatioService.class);

        ApplicationSearchResultDTO searchResultDTO = new ApplicationSearchResultDTO(1, Lists.newArrayList(new ApplicationSearchResultItemDTO()));
        when(applicationDAO.findAllQueried(eq(applicationQueryParameters), eq(filterParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.findAllQueried(eq(applicationQueryParameters), eq(filterParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.findAllQueried(eq(applicationQueryParameters), eq(filterParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.findAllQueried(eq(applicationQueryParameters), eq(filterParameters))).thenReturn(searchResultDTO);
        when(applicationDAO.find(any(Application.class))).thenReturn(Lists.newArrayList(application));
        //when(authenticationService.addPerson(any(Person.class))).thenReturn(PERSON_OID);
        when(applicationDAO.findApplicationAdditionalData(eq(AS_ID), eq(AO_ID), eq(filterParameters))).thenReturn(Lists.newArrayList(new ApplicationAdditionalDataDTO()));
        when(hakuPermissionService.userCanReadApplication(any(Application.class))).thenReturn(true);
//        when(suoritusrekisteriService.getLahtokoulu(any(String.class))).thenReturn("1.2.246.562.10.56695937518");
//        when(suoritusrekisteriService.getLahtoluokka(any(String.class))).thenReturn("9A");

        service = new ApplicationServiceImpl(applicationDAO, null, null, applicationOidService, authenticationService, organizationService,
                hakuPermissionService, applicationSystemService, koulutusinformaatioService, elementTreeValidator);

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
        ApplicationSearchResultDTO results = service.findApplications(applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findAllQueried(eq(applicationQueryParameters), eq(filterParameters));
    }

    @Test
    public void testFindApplicationByName() {
        ApplicationSearchResultDTO results = service.findApplications(applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findAllQueried(eq(applicationQueryParameters), eq(filterParameters));
    }

    @Test
    public void testFindApplicationByOid() {
        application.setOid(OID);
        ApplicationSearchResultDTO results = service.findApplications(applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findAllQueried(eq(applicationQueryParameters), eq(filterParameters));
    }

    @Test
    public void testFindApplicationByShortOid() {
        application.setOid(OID);
        ApplicationSearchResultDTO results = service.findApplications(applicationQueryParameters);
        assertNotNull(results);
        assertEquals(1, results.getResults().size());
        verify(applicationDAO, only()).findAllQueried(eq(applicationQueryParameters), eq(filterParameters));
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
    public void testAuthorizationMetaEmptyApplication() {
        Application application = new Application();
        application.setApplicationSystemId("myAsId");
        ApplicationSystem as = new ApplicationSystem("myAsId", null, new I18nText(new HashMap<String, String>()), "JULKAISTU", null,
                null, true, null, null, null, OppijaConstants.KOHDEJOUKKO_PERVAKO, null, null, null, null, null);
        when(applicationSystemService.getApplicationSystem(eq("myAsId"))).thenReturn(as);

        try {
            application = service.updateAuthorizationMeta(application);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        AuthorizationMeta authorizationMeta = application.getAuthorizationMeta();
        assertTrue(authorizationMeta.isOpoAllowed());

        as = new ApplicationSystem("myAsId", null, new I18nText(new HashMap<String, String>()), "JULKAISTU", null,
                null, true, null, null, null, OppijaConstants.KOHDEJOUKKO_KORKEAKOULU, null, null, null, null, null);
        when(applicationSystemService.getApplicationSystem(eq("myAsId"))).thenReturn(as);

        try {
            application = service.updateAuthorizationMeta(application);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        authorizationMeta = application.getAuthorizationMeta();
        assertFalse(authorizationMeta.isOpoAllowed());
    }

    @Test
    public void testAuthorizationMetaSendingSchool() {
        Application application = new Application();
        application.setApplicationSystemId("myAsId");
        ApplicationSystem as = new ApplicationSystem("myAsId", null, new I18nText(new HashMap<String, String>()), "JULKAISTU", null,
                null, true, null, null, null, OppijaConstants.KOHDEJOUKKO_PERVAKO, null, null, null, null, null);
        Map<String, String> educationAnswers = new HashMap<String, String>();
        educationAnswers.put(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL, "1.2.3.4");

        when(applicationSystemService.getApplicationSystem(eq("myAsId"))).thenReturn(as);
        List<String> parents = new ArrayList<String>();
        parents.add("3.4");
        parents.add("5.6");
        try {
            when(organizationService.findParentOids(eq("1.2.3.4"))).thenReturn(parents);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, educationAnswers);
        try {
            application = service.updateAuthorizationMeta(application);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        AuthorizationMeta authorizationMeta = application.getAuthorizationMeta();
        assertTrue(authorizationMeta.isOpoAllowed());
        Set<String> sendingSchool = authorizationMeta.getSendingSchool();
        assertEquals(2, sendingSchool.size());
        assertTrue(sendingSchool.contains("3.4"));
        assertTrue(sendingSchool.contains("5.6"));
    }

    @Test
    public void testAuthorizationMetaAoParents() throws IOException {
        Application application = new Application();
        application.setApplicationSystemId("myAsId");
        ApplicationSystem as = new ApplicationSystem("myAsId", null, new I18nText(new HashMap<String, String>()), "JULKAISTU", null,
                null, true, null, null, null, OppijaConstants.KOHDEJOUKKO_PERVAKO, null, null, null, null, null);

        Map<String, String> aoAnswers = new HashMap<String, String>();
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_ID, 1), "1.2.3");
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_ID, 2), "4.5.6");
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_ORGANIZATION_ID, 1), "10.1.2");
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_ORGANIZATION_ID, 2), "10.3.4");
        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, aoAnswers);

        List<String> org1parents = new ArrayList<String>();
        org1parents.add("0.0.0");
        org1parents.add("11.1.2");
        org1parents.add("11.3.4");
        List<String> org2parents = new ArrayList<String>();
        org2parents.add("0.0.0");
        org2parents.add("12.1.2");
        org2parents.add("12.3.4");

        when(applicationSystemService.getApplicationSystem(eq("myAsId"))).thenReturn(as);
        when(organizationService.findParentOids(eq("10.1.2"))).thenReturn(org1parents);
        when(organizationService.findParentOids(eq("10.3.4"))).thenReturn(org2parents);

        application = service.updateAuthorizationMeta(application);
        AuthorizationMeta authorizationMeta = application.getAuthorizationMeta();
        assertTrue(authorizationMeta.isOpoAllowed());
        assertEquals(5, authorizationMeta.getAllAoOrganizations().size());
        assertTrue(authorizationMeta.getAllAoOrganizations().contains("0.0.0"));
        assertTrue(authorizationMeta.getAllAoOrganizations().contains("11.1.2"));
        assertTrue(authorizationMeta.getAllAoOrganizations().contains("11.3.4"));
        assertTrue(authorizationMeta.getAllAoOrganizations().contains("12.1.2"));
        assertTrue(authorizationMeta.getAllAoOrganizations().contains("12.3.4"));

        Set<String> ao1parents = authorizationMeta.getAoOrganizations().get("1");
        assertEquals(3, ao1parents.size());
        assertTrue(ao1parents.contains("0.0.0"));
        assertTrue(ao1parents.contains("11.1.2"));
        assertTrue(ao1parents.contains("11.3.4"));
    }

}
