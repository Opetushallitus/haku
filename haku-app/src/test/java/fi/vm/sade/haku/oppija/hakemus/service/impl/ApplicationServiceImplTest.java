package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPreferenceMeta;
import fi.vm.sade.haku.oppija.hakemus.domain.AuthorizationMeta;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
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
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService.YO_TUTKINTO_KOMO;
import static fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility.Status.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
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
    private ApplicationQueryParameters applicationQueryParameters;
    private ApplicationFilterParameters filterParameters;
    private ApplicationServiceImpl service;
    private ElementTreeValidator elementTreeValidator;
    private I18nBundleService i18nBundleService;

    @Before
    public void setUp() {

        applicationQueryParameters = new ApplicationQueryParametersBuilder()
                .setStates(null)
                .setAsIds(null)
                .setAoId("")
                .setLopOid("")
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
        filterParameters = new ApplicationFilterParameters(6, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), "", "");
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
        i18nBundleService = mock(I18nBundleService.class);

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
                hakuPermissionService, applicationSystemService, koulutusinformaatioService, i18nBundleService, null, null, elementTreeValidator, null);
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
    public void testAuthorizationMetaEmptyApplication() {
        Application application = new Application();
        application.setApplicationSystemId("myAsId");
        ApplicationSystem as = new ApplicationSystem("myAsId", null, new I18nText(new HashMap<String, String>()),
                "JULKAISTU", null, null, true, null, null, null,
                OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA, null, null, null, null, null,
                new ArrayList<String>(), new ArrayList<String>(), null);
        when(applicationSystemService.getApplicationSystem(eq("myAsId"))).thenReturn(as);

        try {
            application = service.updateAuthorizationMeta(application);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        AuthorizationMeta authorizationMeta = application.getAuthorizationMeta();
        assertTrue(authorizationMeta.isOpoAllowed());

        as = new ApplicationSystem("myAsId", null, new I18nText(new HashMap<String, String>()), "JULKAISTU", null,
                null, true, null, null, null, OppijaConstants.KOHDEJOUKKO_KORKEAKOULU, null, null, null, null, null,
                new ArrayList<String>(), new ArrayList<String>(), null);
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
        ApplicationSystem as = new ApplicationSystem("myAsId", null, new I18nText(new HashMap<String, String>()),
                "JULKAISTU", null, null, true, null, null, null,
                OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA, null, null, null, null, null,
                new ArrayList<String>(), new ArrayList<String>(), null);
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
        ApplicationSystem as = new ApplicationSystem("myAsId", null, new I18nText(new HashMap<String, String>()),
                "JULKAISTU", null, null, true, null, null, null,
                OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA, null, null, null, null, null,
                new ArrayList<String>(), new ArrayList<String>(), null);

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

    @Test
    public void testAuthorizationMetaPreferenceData() throws IOException {
        Application application = new Application();
        application.setApplicationSystemId("myAsId");
        ApplicationSystem as = new ApplicationSystem("myAsId", null, new I18nText(new HashMap<String, String>()),
                "JULKAISTU", null, null, true, null, null, null,
                OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA, null, null, null, null, null,
                new ArrayList<String>(), new ArrayList<String>(), null);

        Map<String, String> aoAnswers = new HashMap<>();

        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_ID, 1), "1.2.3");
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_ID, 2), "4.5.6");
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_DISCRETIONARY, 2), "true");
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_NAME, 1), "kohde1");
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_NAME, 2), "kohde2");
        //Preference 3 should be skilled as no id
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_ID, 3), "");
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_DISCRETIONARY, 3), "true");

        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_ID, 55), "7.8.9");
        aoAnswers.put(String.format(OppijaConstants.PREFERENCE_NAME, 55), "kohde55");

        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, aoAnswers);
        when(applicationSystemService.getApplicationSystem(eq("myAsId"))).thenReturn(as);

        application = service.updateAuthorizationMeta(application);
        List<ApplicationPreferenceMeta> authorizationMeta = application.getAuthorizationMeta().getApplicationPreferences();
        assertTrue("Expected size 3", authorizationMeta.size() == 3);
        for (final ApplicationPreferenceMeta applicationPreferenceMeta : authorizationMeta) {
            if (applicationPreferenceMeta.getOrdinal().equals(Integer.valueOf(1))) {
                Map<String, String> preferenceData = applicationPreferenceMeta.getPreferenceData();
                assertEquals("kohde1", preferenceData.remove(OppijaConstants.PREFERENCE_FRAGMENT_NAME));
                assertEquals("1.2.3", preferenceData.remove(OppijaConstants.PREFERENCE_FRAGMENT_OPTION_ID));
                assertEquals("Expected to be empty already", new HashMap(), preferenceData);
            } else if (applicationPreferenceMeta.getOrdinal().equals(Integer.valueOf(2))) {
                Map<String, String> preferenceData = applicationPreferenceMeta.getPreferenceData();
                assertEquals("kohde2", preferenceData.remove(OppijaConstants.PREFERENCE_FRAGMENT_NAME));
                assertEquals("4.5.6", preferenceData.remove(OppijaConstants.PREFERENCE_FRAGMENT_OPTION_ID));
                assertEquals("true", preferenceData.remove(OppijaConstants.PREFERENCE_FRAGMENT_DISCRETIONARY));
                assertEquals("Expected to be empty already", new HashMap(), preferenceData);
            } else if (applicationPreferenceMeta.getOrdinal().equals(Integer.valueOf(55))) {
                Map<String, String> preferenceData = applicationPreferenceMeta.getPreferenceData();
                assertEquals("kohde55", preferenceData.remove(OppijaConstants.PREFERENCE_FRAGMENT_NAME));
                assertEquals("7.8.9", preferenceData.remove(OppijaConstants.PREFERENCE_FRAGMENT_OPTION_ID));
                assertEquals("Expected to be empty already", new HashMap(), preferenceData);
            } else
                fail(applicationPreferenceMeta.getOrdinal() + " not Expected");
        }
    }

    @Test
    public void testRemoveOrphanedAnswers() {
        Form form = new Form("formId", createI18NAsIs("myForm"));
        form.addChild(
                new Phase(OppijaConstants.PHASE_PERSONAL, createI18NAsIs("persPhase"), true, new ArrayList<String>()).addChild(
                        TextQuestionBuilder.TextQuestion("persQuestion1").build(),
                        TextQuestionBuilder.TextQuestion("preference1-pers").build()
                ),
                new Phase(OppijaConstants.PHASE_EDUCATION, createI18NAsIs("eduPhase"), true, new ArrayList<String>()).addChild(
                        TextQuestionBuilder.TextQuestion("eduQuestion1").build(),
                        TextQuestionBuilder.TextQuestion("preference1-edu").build()
                ),
                new Phase(OppijaConstants.PHASE_APPLICATION_OPTIONS, createI18NAsIs("aoPhase"), true, new ArrayList<String>()).addChild(
                        TextQuestionBuilder.TextQuestion("aoQuestion1").build(),
                        TextQuestionBuilder.TextQuestion("preference1-ao").build()
                ));
        Application application = new Application("appOid");
        application.setApplicationSystemId("myAs");
        application.addVaiheenVastaukset(OppijaConstants.PHASE_PERSONAL, new HashMap<String, String>() {{
            put("persQuestion1", "persAnswer1"); put("persQuestion2", "persAnswer2");
            put("preference1-pers", "preference1-pers"); put("preference2-pers", "preference2-pers");
        }});
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, new HashMap<String, String>() {{
            put("eduQuestion1", "eduAnswer1"); put("lahtokoulu", "eduAnswer2"); put(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL, "koulu");
            put("preference1-edu", "preference1-edu"); put("preference2-edu", "preference2-edu");
        }});
        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, new HashMap<String, String>() {{
            put("aoQuestion1", "aoAnswer1"); put("aoQuestion2", "aoAnswer2");
            put("preference1-ao", "preference1-ao"); put("preference2-ao", "preference2-ao");
        }});
        ApplicationSystem as = new ApplicationSystemBuilder().setId("myAs").setName(createI18NAsIs("myAs")).setForm(form).get();
        when(applicationSystemService.getApplicationSystem("myAs")).thenReturn(as);

        ApplicationServiceImpl applicationService = new ApplicationServiceImpl(null, null, null, null, null, null,
                null, applicationSystemService, null, null, null, null, null, null);
        application = applicationService.removeOrphanedAnswers(application);
        Map<String, String> persAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_PERSONAL);
        Map<String, String> eduAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        Map<String, String> aoAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        assertEquals("persAnswer1", persAnswers.get("persQuestion1"));
        assertEquals("eduAnswer1", eduAnswers.get("eduQuestion1"));
        assertEquals("aoAnswer1", aoAnswers.get("aoQuestion1"));

        assertNull(persAnswers.get("persQuestion2"));
        assertNull(eduAnswers.get("eduQuestion2"));
        assertNull(aoAnswers.get("aoQuestion2"));

        assertEquals("preference1-pers", persAnswers.get("preference1-pers"));
        assertEquals("preference1-edu", eduAnswers.get("preference1-edu"));
        assertEquals("preference1-ao", aoAnswers.get("preference1-ao"));

        assertNull(persAnswers.get("preference2-pers"));
        assertNull(eduAnswers.get("preference2-edu"));
        assertEquals("preference2-ao", aoAnswers.get("preference2-ao"));

        assertEquals("koulu", eduAnswers.get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL));

    }

    @Test
    public void testAutomaticEligibility() {
        Application application = applicationForAutoEligibility(sureServiceForAutoEligibility(true),
                hakuServiceForAutoEligibility());

        for (PreferenceEligibility eligibility : application.getPreferenceEligibilities()) {
            switch (eligibility.getAoId()) {
                case "automaticallyEligibile1":
                    assertEquals(AUTOMATICALLY_CHECKED_ELIGIBLE, eligibility.getStatus());
                    break;
                case "automaticallyEligibile2":
                    assertEquals(ELIGIBLE, eligibility.getStatus());
                    break;
                case "automaticallyEligibile3":
                    assertEquals(INELIGIBLE, eligibility.getStatus());
                    break;
                case "automaticallyEligibile4":
                    assertEquals(AUTOMATICALLY_CHECKED_ELIGIBLE, eligibility.getStatus());
                    break;
                case "manuallyEligibile":
                    assertEquals(NOT_CHECKED, eligibility.getStatus());
                    break;
            }
        }
    }
    @Test
    public void testAutomaticEligibilityNotYo() {
        Application application = applicationForAutoEligibility(sureServiceForAutoEligibility(false),
                hakuServiceForAutoEligibility());

        for (PreferenceEligibility eligibility : application.getPreferenceEligibilities()) {
            switch (eligibility.getAoId()) {
                case "automaticallyEligibile1":
                    assertEquals(NOT_CHECKED, eligibility.getStatus());
                    break;
                case "automaticallyEligibile2":
                    assertEquals(ELIGIBLE, eligibility.getStatus());
                    break;
                case "automaticallyEligibile3":
                    assertEquals(INELIGIBLE, eligibility.getStatus());
                    break;
                case "automaticallyEligibile4":
                    assertEquals(NOT_CHECKED, eligibility.getStatus());
                    break;
                case "manuallyEligibile":
                    assertEquals(NOT_CHECKED, eligibility.getStatus());
                    break;
            }
        }
    }

    private Application applicationForAutoEligibility(SuoritusrekisteriService suoritusrekisteriService,
                                                      HakuService hakuService) {
        Application application = new Application();
        application.setApplicationSystemId("hakuOid");
        application.setPreferenceEligibilities(new ArrayList<PreferenceEligibility>() {{
            add(new PreferenceEligibility("automaticallyEligibile1", NOT_CHECKED, null, null));
            add(new PreferenceEligibility("automaticallyEligibile2", ELIGIBLE, null, null));
            add(new PreferenceEligibility("automaticallyEligibile3", INELIGIBLE, null, null));
            add(new PreferenceEligibility("automaticallyEligibile4", AUTOMATICALLY_CHECKED_ELIGIBLE, null, null));
            add(new PreferenceEligibility("manuallyEligibile", NOT_CHECKED, null, null));
        }});

        ApplicationServiceImpl applicationService = new ApplicationServiceImpl(null, null, null, null, null, null, null,
                null, null, null, suoritusrekisteriService, hakuService, null, null);

        application = applicationService.updateAutomaticEligibilities(application);

        return application;
    }

    private SuoritusrekisteriService sureServiceForAutoEligibility(final boolean valmis) {
        SuoritusrekisteriService suoritusrekisteriService = mock(SuoritusrekisteriService.class);
        Map<String, List<SuoritusDTO>> suoritusMap = new HashMap<String, List<SuoritusDTO>>() {{
            put(YO_TUTKINTO_KOMO, new ArrayList<SuoritusDTO>() {{
                add(new SuoritusDTO(null, null, null, valmis ? SuoritusDTO.TILA_VALMIS : SuoritusDTO.TILA_KESKEN,
                        null, null, null, null, null, null));
            }});
        }};
        when(suoritusrekisteriService.getSuoritukset(any(String.class), eq(YO_TUTKINTO_KOMO)))
                .thenReturn(suoritusMap);
        return suoritusrekisteriService;
    }

    private HakuService hakuServiceForAutoEligibility() {
        HakuService hakuService = mock(HakuService.class);
        ApplicationSystem as = new ApplicationSystemBuilder()
                .setId("asId")
                .setName(ElementUtil.createI18NAsIs("asName"))
                .setAosForAutomaticEligibility(new ArrayList<String>() {{
                    add("automaticallyEligibile1");
                    add("automaticallyEligibile2");
                    add("automaticallyEligibile3");
                    add("automaticallyEligibile4");
                }})
                .get();
        when(hakuService.getApplicationSystem(eq("hakuOid"))).thenReturn(as);
        return hakuService;
    }


}
