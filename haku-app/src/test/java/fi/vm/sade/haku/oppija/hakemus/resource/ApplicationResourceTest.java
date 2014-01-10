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
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
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
    private ConversionService conversionService;

    private final String OID = "1.2.3.4.5.100";
    private final String INVALID_OID = "1.2.3.4.5.999";
    private final String ASID = "yhteishaku";

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        this.applicationService = mock(ApplicationService.class);
        this.applicationSystemService = mock(ApplicationSystemService.class);
        this.conversionService = mock(ConversionService.class);


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

        ArrayList<Application> applications = new ArrayList<Application>();
        applications.add(this.application);
        ApplicationSearchResultDTO searchResultDTO = new ApplicationSearchResultDTO(1, Lists.newArrayList(new ApplicationSearchResultItemDTO()));
        ApplicationSearchResultDTO emptySearchResultDTO = new ApplicationSearchResultDTO(0, null);
        when(applicationService.getApplicationsByApplicationOption(anyList())).thenReturn(applications);
        when(applicationService.findApplications(eq(OID), any(ApplicationQueryParameters.class))).thenReturn(searchResultDTO);
        when(applicationService.findApplications(eq(INVALID_OID), any(ApplicationQueryParameters.class))).thenReturn(emptySearchResultDTO);
        when(applicationSystemService.findByYearAndSemester(any(String.class), any(String.class))).thenReturn(new ArrayList<String>());
        this.applicationResource = new ApplicationResource(this.applicationService, this.applicationSystemService);
    }

    @Test
    public void testFindApplications() {
        ApplicationSearchResultDTO applications = this.applicationResource.findApplications(OID, null, "", null, null, null, null, null, null, null, null, 0, Integer.MAX_VALUE);
        assertEquals(1, applications.getResults().size());
    }

    @Test
    public void testFindApplicationsNoMatch() {
        ApplicationSearchResultDTO applications = this.applicationResource.findApplications(INVALID_OID, null, "", null, null, null, null, null, null, null, null, 0, Integer.MAX_VALUE);
        assertEquals(0, applications.getTotalCount());
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
    public void testPutApplicationAdditionalInfoKeyValue() throws ResourceNotFoundException {
        applicationResource.putApplicationAdditionalInfoKeyValue(OID, "newKey", "value");
        verify(applicationService, times(1)).putApplicationAdditionalInfoKeyValue(eq(OID), eq("newKey"), eq("value"));
    }

    @Test(expected = JSONException.class)
    public void testPutApplicationAdditionalInfoKeyValueIllegalKey() throws ResourceNotFoundException {
        applicationResource.putApplicationAdditionalInfoKeyValue(OID, "key", "value");
    }

    @Test(expected = JSONException.class)
    public void testPutApplicationAdditionalInfoKeyValueNullValue() throws ResourceNotFoundException {
        applicationResource.putApplicationAdditionalInfoKeyValue(OID, "newKey", null);
    }
}
