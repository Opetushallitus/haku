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

package fi.vm.sade.oppija.hakemus.controller;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class HakemusControllerTest {

    public static final String OID = "oid";
    public static final String APPLICATION_PERIOD_ID = "aid";
    public static final String FORM_ID = "fid";
    public static final String USERNAME = "username";
    public static final Application APPLICATION = new Application(new FormId(APPLICATION_PERIOD_ID, FORM_ID), new User(USERNAME));
    private ApplicationController hakemusController;

    @Before
    public void setUp() throws Exception {
        hakemusController = new ApplicationController();
        hakemusController.applicationService = new ApplicationService() {
            @Override
            public List<ApplicationInfo> getUserApplicationInfo() {
                return null;
            }

            @Override
            public Application getApplication(FormId formId) {
                return APPLICATION;  //To change body of implemented methods use File | Settings | File Templates.
            }


            @Override
            public ApplicationState saveApplicationPhase(ApplicationPhase vaihe) {
                return null;
            }

            @Override
            public ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase, String oid) {
                return null;
            }

            @Override
            public ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase, Application application) {
                return null;
            }

            @Override
            public Application getApplication(String oid) {
                return APPLICATION;
            }

            @Override
            public String submitApplication(FormId vaihe) {
                return "1.1.1.1.";
            }

            @Override
            public Application getApplication(FormId formId, String oid) {
                return APPLICATION;
            }

        };
    }

    @Test
    public void testGetHakemus() throws Exception {
        Application application = hakemusController.getApplication(OID);
        assertEquals(APPLICATION.getFormId(), application.getFormId());
    }
}
