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

package fi.vm.sade.oppija.hakemus.resource;

import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationDTO;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.AnonymousUser;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationResourceTest {

    private ApplicationService applicationService;
    private ApplicationResource applicationResource;
    private Application application;
    private ApplicationDTO applicationDTO;

    private final String OID = "1.2.3.4.5.100";
    private final String INVALID_OID = "1.2.3.4.5.999";
    private final String ASID = "yhteishaku";
    private final String AOID = "776";

    @Before
    public void setUp() {
        this.applicationService = mock(ApplicationService.class);

        FormId formId = new FormId(ASID, "fi");

        Map<String, String> phase1 = new HashMap<String, String>();
        phase1.put("nimi", "Alan Turing");
        Map<String, Map<String, String>> phases = new HashMap<String, Map<String, String>>();
        phases.put("henkilotiedot", phase1);

        this.application = new Application(formId, new AnonymousUser(), phases, null);
        this.application.setOid(OID);
        this.applicationDTO = new ApplicationDTO(this.application);

        try {
            when(applicationService.getApplication(OID)).thenReturn(this.application);
            when(applicationService.getApplication(INVALID_OID)).thenThrow(new ResourceNotFoundException("Application Not Found"));
        } catch (ResourceNotFoundException e) {
            // do nothing
        }

        ArrayList<Application> applications = new ArrayList<Application>();
        applications.add(this.application);
        when(applicationService.getApplicationsByApplicationOption(AOID)).thenReturn(applications);
        when(applicationService.findApplications(eq(OID), any(ApplicationQueryParameters.class))).thenReturn(applications);
        this.applicationResource = new ApplicationResource(this.applicationService);
    }

    @Test
    public void testGetApplication() {
        ApplicationDTO a = this.applicationResource.getApplication(OID);
        assertEquals(this.applicationDTO.getOid(), a.getOid());
    }

    @Test
    public void testGetApplicationWithInvalidOid() {
        try {
            this.applicationResource.getApplication(INVALID_OID);
            fail("ApplicationResource failed to throw exception");
        } catch (JSONException e) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void testGetApplications() {
        List<ApplicationDTO> applications = this.applicationResource.getApplicationsByAOId(AOID);
        assertEquals(1, applications.size());
    }

    @Test
    public void getApplicationsWithInvalidASID() {
        try {
            this.applicationResource.getApplicationsByAOId(null);
            fail("ApplicationResource failed to throw exception");
        } catch (JSONException e) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void testFindApplications() {
        List<Application> applications = this.applicationResource.findApplications(OID, "", false, "", null);
        assertEquals(1, applications.size());
    }

    @Test
    public void testFindApplicationsNoMatch() {
        List<Application> applications = this.applicationResource.findApplications(INVALID_OID, "", false, "", null);
        assertEquals(0, applications.size());
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
}
