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

package fi.vm.sade.haku.oppija.hakemus.it.dao;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.haku.oppija.common.dao.AbstractDAOTest;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.HashMap;
import java.util.List;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/tomcat-container-context.xml")
@ActiveProfiles(profiles = {"it"})
public class ApplicationDAOMongoImplIT extends AbstractDAOTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDAOMongoImplIT.class);
    public static final User TEST_USER = new User("test");
    public static final String ARVO = "arvo";
    public static final String TEST_PHASE = "vaihe1";
    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    private String applicationSystemId;

    protected static List<DBObject> applicationTestDataObject;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void readTestData() throws IOException {
        String content = IOUtils.toString(getSystemResourceAsStream("application-test-data.json"), "UTF-8");
        applicationTestDataObject = (List<DBObject>) JSON.parse(content);
    }

    @Before
    public void setUpMongo() throws Exception {
        try {
            mongoTemplate.getCollection(getCollectionName()).insert(applicationTestDataObject);
        } catch (Exception e) {
            LOGGER.error("Error set up test", e);
        }
        this.applicationSystemId = ElementUtil.randomId();
    }

//    @Test
//    public void testFindPersonOidExists() {
//        BasicDBObject query = new BasicDBObject();
//        query.put("personOid", new BasicDBObject("$exists", false));
//        List<Application> notExists = applicationDAO.find(query);
//        assertEquals(2, notExists.size());
//
//        query = new BasicDBObject();
//        query.put("personOid", new BasicDBObject("$exists", true));
//        List<Application> exists = applicationDAO.find(query);
//        assertEquals(1, exists.size());
//    }

    @Test
    public void testFindAll() throws Exception {
        final HashMap<String, String> vaiheenVastaukset = new HashMap<String, String>();
        vaiheenVastaukset.put("avain", ARVO);
        final Application application = new Application(TEST_USER, new ApplicationPhase(applicationSystemId, TEST_PHASE, vaiheenVastaukset));
        application.setOid("oid_arvo");
        applicationDAO.save(application);
        List<Application> listOfApplications = applicationDAO.find(new Application(applicationSystemId, TEST_USER));
        assertEquals(1, listOfApplications.size());
    }

    @Test
    public void testFindAllNotFound() throws Exception {
        List<Application> applications = applicationDAO.find(new Application(applicationSystemId, TEST_USER));
        assertTrue(applications.isEmpty());
    }

    @Test
    public void testCheckIfExistsBySocialSecurityNumber() {
        assertTrue(applicationDAO.checkIfExistsBySocialSecurityNumber("Yhteishaku", "050998-957M"));
        assertFalse(applicationDAO.checkIfExistsBySocialSecurityNumber("Yhteishaku", "040597-334D"));
        assertFalse(applicationDAO.checkIfExistsBySocialSecurityNumber("Yhteishaku", ""));
        assertFalse(applicationDAO.checkIfExistsBySocialSecurityNumber("Yhteishaku", null));
    }

    @Test
    public void testfindAllQueriedByApplicationSystemAndApplicationOption() {
        ApplicationQueryParameters applicationQueryParameters  = new ApplicationQueryParametersBuilder().setSearchTerms("").setAsId("Yhteishaku").setAoId("776").build();
        AuthenticationServiceMockImpl authenticationServiceMock = new AuthenticationServiceMockImpl();
        ApplicationFilterParameters filterParameters = new ApplicationFilterParameters(5,
                authenticationServiceMock.getOrganisaatioHenkilo(), authenticationServiceMock.getOrganisaatioHenkilo(),
                authenticationServiceMock.getOrganisaatioHenkilo(), null, null);
        ApplicationSearchResultDTO resultDTO = applicationDAO.findAllQueried(applicationQueryParameters, filterParameters);
        assertFalse(CollectionUtils.isEmpty(resultDTO.getResults()));
        assertEquals(2, resultDTO.getResults().size());
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }
}
