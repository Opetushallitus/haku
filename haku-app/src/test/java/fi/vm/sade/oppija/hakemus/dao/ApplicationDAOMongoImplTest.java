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

package fi.vm.sade.oppija.hakemus.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.common.dao.AbstractDAOTest;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@ActiveProfiles(profiles = "dev")
public class ApplicationDAOMongoImplTest extends AbstractDAOTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDAOMongoImplTest.class);
    public static final User TEST_USER = new User("test");
    public static final String ARVO = "arvo";
    public static final String TEST_PHASE = "vaihe1";
    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    private FormId formId;

    protected static List<DBObject> applicationTestDataObject;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void readTestData() throws IOException {
        String content = IOUtils.toString(getSystemResourceAsStream("application-test-data.json"), "UTF-8");
        applicationTestDataObject = (List<DBObject>) JSON.parse(content);
    }

    @Before
    public void setUp() throws Exception {
        try {
            getDbFactory().getObject().getCollection(getCollectionName()).insert(applicationTestDataObject);
        } catch (Exception e) {
            LOGGER.error("Error set up test", e);
        }

        final String id = String.valueOf(System.currentTimeMillis());
        this.formId = new FormId(ElementUtil.randomId(), id);
    }

    @Test
    public void testTallennaVaihe() {
        final HashMap<String, String> vaiheenVastaukset = new HashMap<String, String>();
        vaiheenVastaukset.put("avain", ARVO);
        final Application application1 = new Application(TEST_USER, new ApplicationPhase(formId, TEST_PHASE, vaiheenVastaukset));
        final ApplicationState application = applicationDAO.tallennaVaihe(new ApplicationState(application1, TEST_PHASE));
        assertEquals(ARVO, application.getHakemus().getVastauksetMerged().get("avain"));
    }

    @Test
    public void testFindPersonOidExists() {
        BasicDBObject query = new BasicDBObject();
        query.put("personOid", new BasicDBObject("$exists", false));
        List<Application> notExists = applicationDAO.find(query);
        assertEquals(2, notExists.size());

        query = new BasicDBObject();
        query.put("personOid", new BasicDBObject("$exists", true));
        List<Application> exists = applicationDAO.find(query);
        assertEquals(1, exists.size());
    }

    @Test
    public void testFindAll() throws Exception {
        testTallennaVaihe();
        List<Application> listOfApplications = applicationDAO.find(new Application(formId, TEST_USER));
        assertEquals(1, listOfApplications.size());
    }

    @Test
    public void testFindAllNotFound() throws Exception {
        List<Application> applications = applicationDAO.find(new Application(formId, TEST_USER));
        assertTrue(applications.isEmpty());
    }

    @Test(expected = ResourceNotFoundExceptionRuntime.class)
    public void testFindPendingApplicationNotFound() throws Exception {
        applicationDAO.findDraftApplication(new Application(formId, TEST_USER));
    }

    @Test
    public void testFindByApplicationOption() {
        List<String> aoids = new ArrayList<String>();
        aoids.add("776");
        List<Application> applications = applicationDAO.findByApplicationOption(aoids);
        assertEquals(2, applications.size());
    }

    @Test
    public void testFindByHetuActive() {
        ApplicationSearchResultDTO applications = applicationDAO.findByApplicantSsn("050998-957M",
                new ApplicationQueryParameters(Application.State.ACTIVE.toString(), null, null, 0, Integer.MAX_VALUE));
        assertEquals(2, applications.getResults().size());
        assertEquals(2, applications.getTotalCount());
    }

    @Test
    public void testFindByHetuPassive() {
        ApplicationSearchResultDTO applications = applicationDAO.findByApplicantSsn("050998-957M",
                new ApplicationQueryParameters(Application.State.PASSIVE.toString(), null, null, 0, Integer.MAX_VALUE));
        assertEquals(1, applications.getResults().size());
    }

    @Test
    public void testFindByName() {
        // Heikki
        ApplicationSearchResultDTO applications = applicationDAO.findByApplicantName("Heikki", new ApplicationQueryParameters(
                null, null, null, 0, Integer.MAX_VALUE));
        assertEquals(1, applications.getResults().size());
        // Hessu * 2
        applications = applicationDAO.findByApplicantName("Hessu", new ApplicationQueryParameters(null, null, null, 0,
                Integer.MAX_VALUE));
        assertEquals(2, applications.getResults().size());
        // Hessut ja Heikki
        applications = applicationDAO.findByApplicantName("he", new ApplicationQueryParameters(null, null, null, 0,
                Integer.MAX_VALUE));
        assertEquals(3, applications.getResults().size());
        // Hessu, active
        ApplicationQueryParameters activeParameters = new ApplicationQueryParameters(Application.State.ACTIVE.toString(),
                null, null, 0, Integer.MAX_VALUE);
        applications = applicationDAO.findByApplicantName("Hessu", activeParameters);
        assertEquals(1, applications.getResults().size());
    }

    @Test
    public void testCheckIfExistsBySocialSecurityNumber() {
        assertTrue(applicationDAO.checkIfExistsBySocialSecurityNumber("Yhteishaku", "050998-957M"));
        assertFalse(applicationDAO.checkIfExistsBySocialSecurityNumber("Yhteishaku", "040597-334D"));
        assertFalse(applicationDAO.checkIfExistsBySocialSecurityNumber("Yhteishaku", ""));
        assertFalse(applicationDAO.checkIfExistsBySocialSecurityNumber("Yhteishaku", null));
    }

    @Test
    public void testFindByApplicationSystemAndApplicationOption() {
        List<Application> applications = applicationDAO.findByApplicationSystemAndApplicationOption("Yhteishaku", "776");
        assertNotNull(applications);
        assertEquals(2, applications.size());
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }
}
