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

package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.collect.Lists;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.impl.ApplicationServiceImpl;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationResourceTest {

    private ApplicationService applicationService;
    private ApplicationSystemService applicationSystemService;
    private ApplicationResource applicationResource;
    private Application application;
    private I18nBundleService i18nBundleService;

    private final String OID = "1.2.3.4.5.100";
    private final String INVALID_OID = "1.2.3.4.5.999";
    private final String ASID = "yhteishaku";

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        this.applicationService = mock(ApplicationService.class);
        this.applicationSystemService = mock(ApplicationSystemService.class);
        this.i18nBundleService = mock(I18nBundleService.class);

        Map<String, String> phase1 = new HashMap<String, String>();
        phase1.put("nimi", "Alan Turing");
        Map<String, Map<String, String>> phases = new HashMap<String, Map<String, String>>();
        phases.put("henkilotiedot", phase1);

        this.application = new Application(ASID, new User(User.ANONYMOUS_USER), phases, null);
        this.application.setOid(OID);

        try {
            when(applicationService.getApplicationByOid(OID)).thenReturn(this.application);
            when(applicationService.getApplicationByOid(INVALID_OID)).thenThrow(new ResourceNotFoundException("Application Not Found"));
            when(applicationService.getApplicationKeyValue(eq(OID), eq("key"))).thenReturn("value");
            when(applicationService.getApplicationKeyValue(eq(INVALID_OID), eq("key"))).thenThrow(new ResourceNotFoundException("Application Not Found"));
            when(applicationService.getApplicationKeyValue(eq(OID), eq("nonExistingKey"))).thenThrow(new ResourceNotFoundException("Key of the application Not Found"));
            doThrow(new IllegalStateException()).when(applicationService).putApplicationAdditionalInfoKeyValue(eq(OID), eq("key"), anyString());
            doThrow(new IllegalStateException()).when(applicationService).putApplicationAdditionalInfoKeyValue(anyString(), anyString(), isNull(String.class));
        } catch (ResourceNotFoundException e) {
            // do nothing
        }

        ArrayList<String> asIds = new ArrayList<String>();
        asIds.add("asId1");
        asIds.add("asId2");
        asIds.add("asId3");
        ArrayList<Application> applications = new ArrayList<Application>();
        applications.add(this.application);
        ApplicationSearchResultDTO searchResultDTO = new ApplicationSearchResultDTO(1, Lists.newArrayList(new ApplicationSearchResultItemDTO()));
        ApplicationSearchResultDTO emptySearchResultDTO = new ApplicationSearchResultDTO(0, null);
        when(applicationService.findApplications(any(ApplicationQueryParameters.class))).thenReturn(searchResultDTO);
        when(applicationService.findApplications(any(ApplicationQueryParameters.class))).thenReturn(emptySearchResultDTO);
        when(applicationSystemService.findByYearAndSemester(any(String.class), any(String.class))).thenReturn(asIds);
        this.applicationResource = new ApplicationResource(this.applicationService, this.applicationSystemService, null, null, i18nBundleService);
    }

    @Test
    public void testGetApplicationsByOid() {
        Application application = this.applicationResource.getApplicationByOid(OID);
        assertNotNull(application);
    }

    @Test(expected = JSONException.class)
    public void testGetApplicationByInvalidOid() {
        this.applicationResource.getApplicationByOid(INVALID_OID);
    }

    @Test
    public void testGetApplicationKeyValue() {
        Map<String, String> value = applicationResource.getApplicationKeyValue(OID, "key");
        assertNotNull(value);
        assertTrue(value.containsKey("key"));
        assertEquals("value", value.get("key"));
        assertEquals(1, value.size());
    }

    @Test(expected = JSONException.class)
    public void testGetApplicationKeyValueNonExistingApplication() {
        applicationResource.getApplicationKeyValue(INVALID_OID, "key");
    }

    @Test(expected = JSONException.class)
    public void testGetApplicationKeyValueNonExistingKey() {
        applicationResource.getApplicationKeyValue(OID, "nonExistingKey");
    }

    @Test
    public void testPutApplicationAdditionalInfoKeyValue() {
        applicationResource.putApplicationAdditionalInfoKeyValue(OID, "newKey", "value");
        verify(applicationService, times(1)).putApplicationAdditionalInfoKeyValue(eq(OID), eq("newKey"), eq("value"));
    }

    @Test(expected = JSONException.class)
    public void testPutApplicationAdditionalInfoKeyValueIllegalKey() {
        applicationResource.putApplicationAdditionalInfoKeyValue(OID, "key", "value");
    }

    @Test(expected = JSONException.class)
    public void testPutApplicationAdditionalInfoKeyValueNullValue() {
        applicationResource.putApplicationAdditionalInfoKeyValue(OID, "newKey", null);
    }

    @Test
    public void testFindApplicationsOrdered() {
        ApplicationServiceMock myApplicationService = new ApplicationServiceMock();
        ApplicationResource resource = new ApplicationResource(myApplicationService, applicationSystemService, null, null, i18nBundleService);
        resource.findApplications("query", null, null, "aoId", "groupOid", "baseEducation", "lopOid", "", "", "", "aoId",
                false, false, "sendingSchool",
                "class", new DateParam("201403041506"), 0, 20);
        assertEquals("query", myApplicationService.applicationQueryParameters.getSearchTerms());
        ApplicationQueryParameters param = myApplicationService.applicationQueryParameters;
        assertEquals(1, param.getOrderDir());
        assertEquals("fullName", param.getOrderBy());
        assertEquals(0, param.getAsIds().size());


        resource.findApplications("query", null, null, "aoId", "groupOid", "baseEducation", "lopOid", "asId", "", "", "aoId",
                false, false, "sendingSchool",
                "class", new DateParam("201403041506"), 0, 20);
        param = myApplicationService.applicationQueryParameters;
        assertEquals(1, param.getAsIds().size());
        assertEquals("asId", param.getAsIds().get(0));

        resource.findApplications("query", null, null, "aoId", "groupOid", "baseEducation", "lopOid", "asId", "semester",
                "year", "aoId", false, false, "sendingSchool",
                "class", new DateParam("201403041506"), 0, 20);
        param = myApplicationService.applicationQueryParameters;
        assertEquals(1, param.getAsIds().size());
        assertEquals("asId", param.getAsIds().get(0));

        resource.findApplications("query", null, null, "aoId", "groupOid", "baseEducation", "lopOid", "", "semester",
                "year", "aoId", false, false, "sendingSchool",
                "class", new DateParam("201403041506"), 0, 20);
        param = myApplicationService.applicationQueryParameters;
        assertEquals(3, param.getAsIds().size());
        assertEquals("asId1", param.getAsIds().get(0));
        assertEquals("asId2", param.getAsIds().get(1));
        assertEquals("asId3", param.getAsIds().get(2));
    }

    class ApplicationServiceMock extends ApplicationServiceImpl {

        public ApplicationQueryParameters applicationQueryParameters;

        public ApplicationServiceMock() {
            super(null, null, null, null, null, null, null, applicationSystemService, null, null, null);
        }

        @Override
        public ApplicationSearchResultDTO findApplications(ApplicationQueryParameters applicationQueryParameters) {
            this.applicationQueryParameters = applicationQueryParameters;
            return null;
        }
    }
}
