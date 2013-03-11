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

package fi.vm.sade.oppija.ui.controller;

import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonalServicesTest {

    PersonalServices personalServices;
    private ArrayList<ApplicationInfo> applicationInfos;

    @Before
    public void setUp() throws Exception {
        personalServices = new PersonalServices();
        personalServices.applicationService = mock(ApplicationService.class);
        applicationInfos = new ArrayList<ApplicationInfo>(0);
        when(personalServices.applicationService.getUserApplicationInfo()).thenReturn(applicationInfos);
    }

    @Test
    public void testHautKoulutuksiin() throws Exception {
        Viewable viewable = personalServices.hautKoulutuksiin();
        assertEquals(viewable.getTemplateName(), PersonalServices.PERSONAL_TEMPLATE_VIEW);

    }

    @Test
    public void testGetApplicationsTemplateName() throws Exception {
        Viewable viewable = personalServices.getApplications();
        assertEquals(viewable.getTemplateName(), PersonalServices.PERSONAL_TEMPLATE_VIEW);
    }

    @Test
    public void testGetApplicationsModelSize() throws Exception {
        @SuppressWarnings("rawtypes")
        Map model = getApplicationsModel();
        assertTrue(model.size() == 2);
    }

    @Test
    public void testGetApplicationsModelInfo() throws Exception {
        @SuppressWarnings("rawtypes")
        Map model = getApplicationsModel();
        assertEquals(model.get(PersonalServices.USER_APPLICATION_INFO_MODEL), applicationInfos);
    }

    @SuppressWarnings("rawtypes")
    private Map getApplicationsModel() {
        Viewable viewable = personalServices.getApplications();
        return (Map) viewable.getModel();
    }

    @Test
    public void testGetApplicationsModelSection() throws Exception {
        @SuppressWarnings("rawtypes")
        Map model = getApplicationsModel();
        assertEquals(model.get(PersonalServices.SECTION_MODEL), "applications");
    }


    @Test
    public void testGetUserNoteList() throws Exception {
        Viewable viewable = personalServices.getUserNoteList();
        assertEquals(viewable.getTemplateName(), PersonalServices.NOTELIST_VIEW);
    }

    @Test
    public void testGetUserComparison() throws Exception {
        Viewable viewable = personalServices.getUserComparison();
        assertEquals(viewable.getTemplateName(), PersonalServices.COMPARISON_VIEW);
    }

    @Test
    public void testAddApplicationOptionToNoteList() throws Exception {
        personalServices.addApplicationOptionToNoteList("1");
    }

    @Test
    public void testAddApplicationOptionToComparison() throws Exception {
        personalServices.addApplicationOptionToComparison("1");
    }
}
