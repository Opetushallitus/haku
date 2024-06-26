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

package fi.vm.sade.haku.oppija.hakemus.it;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoImpl;
import fi.vm.sade.haku.oppija.lomake.service.mock.UserSessionMock;
import fi.vm.sade.haku.oppija.repository.AuditLogRepository;
import fi.vm.sade.log.model.Tapahtuma;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;
import static java.lang.ClassLoader.getSystemResourceAsStream;

public class LoggerAspectTestIT extends IntegrationTestSupport {
    protected MongoTemplate mongoTemplate;
    private Application application;
    private LoggerAspect LOGGER_ASPECT;

    @Before
    public void setUp() throws Exception {
        mongoTemplate = IntegrationTestSupport.appContext.getBean(MongoTemplate.class);
        AuditLogRepository audit = IntegrationTestSupport.appContext.getBean(AuditLogRepository.class);
        ApplicationDAO applicationDAO = IntegrationTestSupport.appContext.getBean(ApplicationDAOMongoImpl.class);

        LOGGER_ASPECT = new LoggerAspect(new UserSessionMock("test"), audit, "localhost");

        String content = IOUtils.toString(getSystemResourceAsStream("hakemus_audit_log.json"), "UTF-8");
        DBObject applicationFromJson = (DBObject) JSON.parse(content);
        mongoTemplate.getCollection(getCollectionName()).insert(applicationFromJson);
        application = applicationDAO.find(new Application((String) applicationFromJson.get("oid"))).get(0);
    }

    @After
    public void removeTestData() {
        mongoTemplate.getCollection(getCollectionName()).drop();
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
        newEligibilities.add(new PreferenceEligibility("1.2.246.562.5.14273398983", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null, null));
        newEligibilities.add(new PreferenceEligibility("1.2.246.562.5.41197971199", PreferenceEligibility.Status.ELIGIBLE, PreferenceEligibility.Source.UNKNOWN, null, null));
        newApplication.setPreferenceEligibilities(newEligibilities);
        final List<Map<String, String>> changes = addHistoryBasedOnChangedAnswers(newApplication, application, "junit", "Post Processing");
        LOGGER_ASPECT.logUpdateApplicationInPostProcessing(application, changes, "LoggerAspectTest");
        //todo: add asserts
    }

    protected String getCollectionName() {
        return "application";
    }
}
