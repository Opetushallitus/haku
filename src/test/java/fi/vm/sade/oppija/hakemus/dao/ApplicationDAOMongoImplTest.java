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

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.common.dao.AbstractDAOTest;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.tools.FileHandling;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationDAOMongoImplTest extends AbstractDAOTest {

    public static final User TEST_USER = new User("test");
    public static final String ARVO = "arvo";
    public static final String TEST_PHASE = "vaihe1";
    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    private FormId formId;

    protected static DBObject applicationTestDataObject;

    @BeforeClass
    public static void readTestData() {

        String content = new FileHandling().readFile(getSystemResourceAsStream("application-test-data.json"));
        applicationTestDataObject = (DBObject) JSON.parse(content);

    }


    @Before
    public void setUp() throws Exception {

        try {
            getDbFactory().getObject().getCollection(getCollectionName()).insert(applicationTestDataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String id = String.valueOf(System.currentTimeMillis());
        this.formId = new FormId(id, id);
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
    public void testSequence() throws Exception {
        String oid1 = applicationDAO.getNewOid();
        String oid2 = applicationDAO.getNewOid();
        assertNotSame(oid1, oid2);
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }
}
