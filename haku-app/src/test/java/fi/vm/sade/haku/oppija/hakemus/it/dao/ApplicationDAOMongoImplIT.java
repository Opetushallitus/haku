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

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fi.vm.sade.haku.oppija.common.dao.AbstractDAOTest;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.util.JsonTestData;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/tomcat-container-context.xml")
@ActiveProfiles(profiles = {"it"})
public class ApplicationDAOMongoImplIT extends AbstractDAOTest {
    public static final User TEST_USER = new User("test");
    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    private String applicationSystemId;

    protected static List<DBObject> applicationTestDataObject = JsonTestData.readTestData("application-test-data.json");

    @Before
    public void setUpMongo() throws Exception {
        mongoTemplate.getCollection(getCollectionName()).insert(applicationTestDataObject);
        this.applicationSystemId = ElementUtil.randomId();
    }

    @Test
    public void testFindAll() throws Exception {
        final HashMap<String, String> vaiheenVastaukset = new HashMap<String, String>() {{
            put("avain", "arvo");
        }};
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", vaiheenVastaukset)).setOid("1.2.246.562.11.00000000258"));
        assertEquals(1, applicationDAO.find(new Application(applicationSystemId, TEST_USER)).size());
    }

    @Test
    public void testFindAllNotFound() throws Exception {
        assertTrue(applicationDAO.find(new Application(applicationSystemId, TEST_USER)).isEmpty());
    }

    @Test
    public void testCheckIfExistsBySocialSecurityNumber() {
        assertTrue(applicationDAO.checkIfExistsBySocialSecurityNumber("1.2.246.562.29.90697286251", "010101-123N"));
        assertFalse(applicationDAO.checkIfExistsBySocialSecurityNumber("1.2.246.562.29.90697286251", "040404-123T"));
        assertFalse(applicationDAO.checkIfExistsBySocialSecurityNumber("1.2.246.562.29.90697286251", ""));
        assertFalse(applicationDAO.checkIfExistsBySocialSecurityNumber("1.2.246.562.29.90697286251", null));
    }

    @Test
    public void testFindAllQueriedByApplicationSystemAndApplicationOption() {
        assertEquals(2, findByQuery(query().setSearchTerms("").setAsId("1.2.246.562.29.90697286251").setAoId("000")).getResults().size());
    }

    @Test
    public void testSearchWithMultipleuserOids() {
        assertEquals(2, findByQuery(query().setPersonOids(asList("1.2.246.562.24.00000000001", "1.2.246.562.24.00000000002"))).getResults().size());
    }

    // TODO: test findAllQueried with aoOids and primaryPreferenceOnly


    @Test
    public void testfindAllQueriedByApplicationSystemAndMultipleApplicationOptions() {
        assertEquals(1, findByQuery(query().setAoOids(asList("1.2.246.562.20.52010929637"))).getResults().size());

        assertEquals(2, findByQuery(query().setAoOids(asList("1.2.246.562.20.18097797874"))).getResults().size());

        assertEquals(3, findByQuery(query().setAoOids(asList("1.2.246.562.20.18097797874", "1.2.246.562.20.52010929637"))).getResults().size());
    }

    private ApplicationQueryParametersBuilder query() {
        return new ApplicationQueryParametersBuilder();
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }

    private ApplicationSearchResultDTO findByQuery(final ApplicationQueryParametersBuilder queryBuilder) {
        ApplicationQueryParameters applicationQueryParameters = queryBuilder.build();
        AuthenticationServiceMockImpl authenticationServiceMock = new AuthenticationServiceMockImpl();
        ApplicationFilterParameters filterParameters = new ApplicationFilterParameters(5, authenticationServiceMock.getOrganisaatioHenkilo(), authenticationServiceMock.getOrganisaatioHenkilo(), authenticationServiceMock.getOrganisaatioHenkilo(), null, null);
        return applicationDAO.findAllQueried(applicationQueryParameters, filterParameters);
    }
}
