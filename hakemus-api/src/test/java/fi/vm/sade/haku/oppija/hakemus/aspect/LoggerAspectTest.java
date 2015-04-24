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

package fi.vm.sade.haku.oppija.hakemus.aspect;

import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.haku.oppija.common.dao.AbstractDAOTest;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationFilterParameters;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.service.mock.UserSessionMock;
import fi.vm.sade.haku.oppija.repository.AuditLogRepository;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.model.Tapahtuma;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;
import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/tomcat-container-context.xml")
@ActiveProfiles(profiles = {"it"})
public class LoggerAspectTest extends AbstractDAOTest {

    @Autowired
    private AuditLogRepository audit;

    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    @Autowired
    @Qualifier("applicationServiceImpl")
    private ApplicationService applicationService;

    private Application application;

    private LoggerAspect LOGGER_ASPECT;

    @Before
    public void setUp() throws Exception {
        LOGGER_ASPECT = new LoggerAspect(new Logger() {
            @Override
            public void log(Tapahtuma tapahtuma) {
                System.err.println("foobar");
            }
        }, new UserSessionMock("test"), audit, "localhost");

        String content = IOUtils.toString(getSystemResourceAsStream("hakemus_audit_log.json"), "UTF-8");
        DBObject applicationFromJson = (DBObject) JSON.parse(content);
        mongoTemplate.getCollection(getCollectionName()).insert(applicationFromJson);
        application = applicationDAO.find(new Application((String) applicationFromJson.get("oid"))).get(0);
    }


    @Test
    public void testlogSubmitApplication() throws Exception {
        LOGGER_ASPECT.logSubmitApplication("aid", application);
        //todo: add asserts
    }

    @Test
    public void testlogUpdateApplication() throws Exception {
        final Application newApplication = application.clone();

        List<PreferenceEligibility> newEligibilities = new LinkedList<>();
        newEligibilities.add(new PreferenceEligibility("1.2.246.562.5.14273398983", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null));
        newEligibilities.add(new PreferenceEligibility("1.2.246.562.5.41197971199", PreferenceEligibility.Status.ELIGIBLE, PreferenceEligibility.Source.UNKNOWN, null));
        newApplication.setPreferenceEligibilities(newEligibilities);
        final List<Map<String, String>> changes = addHistoryBasedOnChangedAnswers(newApplication, application, "junit", "Post Processing");
        LOGGER_ASPECT.logUpdateApplicationInPostProcessing(application, changes, "LoggerAspectTest");
        //todo: add asserts
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }
}
