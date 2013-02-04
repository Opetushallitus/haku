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

package fi.vm.sade.oppija.hakemus.domain;

import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import org.junit.Before;
import org.junit.Test;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static org.junit.Assert.*;

public class ApplicationInfoTest {

    private ApplicationInfo applicationInfo;
    private ApplicationPeriod applicationPeriod;
    private Form form;
    private Application application;

    @Before
    public void setUp() throws Exception {
        applicationPeriod = new ApplicationPeriod();
        form = new Form("id", createI18NText("title"));
        application = new Application();
        applicationInfo = new ApplicationInfo(application, form, applicationPeriod);
    }

    @Test
    public void testGetApplication() throws Exception {
        assertEquals(application, applicationInfo.getApplication());
    }

    @Test
    public void testGetForm() throws Exception {
        assertEquals(form, applicationInfo.getForm());
    }

    @Test
    public void testGetApplicationPeriod() throws Exception {
        assertEquals(applicationPeriod, applicationInfo.getApplicationPeriod());
    }

    @Test
    public void testIsNotPending() throws Exception {
        assertFalse(applicationInfo.isPending());
    }

    @Test
    public void testIsPending() throws Exception {
        this.applicationInfo.application.setPhaseId(ApplicationInfo.STATE_PENDING);
        assertTrue(applicationInfo.isPending());
    }
}
