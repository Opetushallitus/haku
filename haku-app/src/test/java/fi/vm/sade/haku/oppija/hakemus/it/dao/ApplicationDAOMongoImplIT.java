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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.DBObject;

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
    public void testFindAllQueriedByEmptyQuery() {
        assertEquals(3, findAllQueried(query()).getResults().size());
    }

    @Test
    public void testFindAllQueriedByApplicationSystem() {
        assertEquals(3, findAllQueried(query().setAsId("1.2.246.562.29.90697286251")).getResults().size());
    }

    @Test
    public void testFindAllQueriedByApplicationSystemAndApplicationOption() {
        assertEquals(2, findAllQueried(query().setSearchTerms("").setAsId("1.2.246.562.29.90697286251").setAoId("000")).getResults().size());
    }

    @Test
    public void testFindAllQueriedBySearchTerms() {
        assertEquals(3, findAllQueried(query().setSearchTerms("")).getResults().size());
        assertEquals(0, findAllQueried(query().setSearchTerms("FAIL")).getResults().size());
        assertEquals(2, findAllQueried(query().setSearchTerms("Mäkinen")).getResults().size());
        assertEquals(2, findAllQueried(query().setSearchTerms("mäkinen")).getResults().size());
        assertEquals(1, findAllQueried(query().setSearchTerms("Jill")).getResults().size());
        assertEquals(1, findAllQueried(query().setSearchTerms("14.11.1940")).getResults().size());
        assertEquals(1, findAllQueried(query().setSearchTerms("1.2.246.562.11.00000000259")).getResults().size());
        assertEquals(1, findAllQueried(query().setSearchTerms("1.2.246.562.24.00000000001")).getResults().size());
        assertEquals(1, findAllQueried(query().setSearchTerms("00000000001")).getResults().size());
        assertEquals(1, findAllQueried(query().setSearchTerms("00000000259")).getResults().size());
        assertEquals(0, findAllQueried(query().setSearchTerms("00000001259")).getResults().size());
        assertEquals(1, findAllQueried(query().setSearchTerms("010101-123N")).getResults().size());
        assertEquals(0, findAllQueried(query().setSearchTerms("010101-123K")).getResults().size());
    }

    @Test
    public void testFindAllQueriedByPersonOids() {
        assertEquals(2, findAllQueried(query().setPersonOids(asList("1.2.246.562.24.00000000001", "1.2.246.562.24.00000000002"))).getResults().size());
    }

    @Test
    public void testFindAllQueriedByApplicationOptions() {
        assertEquals(2, findAllQueried(query().setAoOids(asList("1.2.246.562.20.52010929637"))).getResults().size());
        assertEquals(2, findAllQueried(query().setAoOids(asList("1.2.246.562.20.18097797874"))).getResults().size());
        assertEquals(3, findAllQueried(query().setAoOids(asList("1.2.246.562.20.18097797874", "1.2.246.562.20.52010929637"))).getResults().size());
    }

    @Test
    public void testFindAllQueriedByApplicationOptionsWithPrimaryPreferenceOnly() {
        assertEquals(2, findAllQueried(query().setAoOids(asList("1.2.246.562.20.52010929637"))).getResults().size());
        assertEquals(1, findAllQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true)).getResults().size());
    }

    @Test
    public void testFindAllQueriedByLopOids() {
        assertEquals(0, findAllQueried(query().setLopOid("FAIL")).getResults().size());
        assertEquals(2, findAllQueried(query().setLopOid("1.2.246.562.10.10464399921")).getResults().size());
        assertEquals(2, findAllQueried(query().setLopOid("1.2.246.562.10.84451661825")).getResults().size());
    }

    @Test
    public void testFindAllQueriedWithQueryCombinations() {
        assertEquals(1, findAllQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true)).getResults().size());
        assertEquals(1, findAllQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true).setAsId("1.2.246.562.29.90697286251")).getResults().size());
        assertEquals(0, findAllQueried(query().setLopOid("1.2.246.562.10.84451661825").setAsId("FAIL")).getResults().size());
        assertEquals(0, findAllQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true).setDiscretionaryOnly(true)).getResults().size());
        assertEquals(1, findAllQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true).setDiscretionaryOnly(false)).getResults().size());
        assertEquals(0, findAllQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true).setAoOids(asList("1.2.246.562.20.18097797874"))).getResults().size());
        assertEquals(1, findAllQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true).setPreferenceChecked(false)).getResults().size());
        assertEquals(0, findAllQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true).setPreferenceChecked(true)).getResults().size());
    }

    @Test
    public void testFindAllQueriedPreferencesChecked() {
        assertEquals(3, findAllQueried(query().setPreferenceChecked(false)).getResults().size());
        assertEquals(1, findAllQueried(query().setPreferenceChecked(true)).getResults().size());
        assertEquals(2, findAllQueried(query().setAoOids(asList("1.2.246.562.20.52010929637"))).getResults().size());
        assertEquals(1, findAllQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPreferenceChecked(true)).getResults().size());
        assertEquals(1, findAllQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPreferenceChecked(false)).getResults().size());
    }

    @Test
    public void testFindAllQueriedApplicationOptionGroups() {
        assertEquals(1, findAllQueried(query().setGroupOid("1.2.246.562.28.26750186798")).getResults().size());
        assertEquals(0, findAllQueried(query().setGroupOid("1.2.246.562.28.26750186798").setPrimaryPreferenceOnly(true)).getResults().size());
    }

    @Test
    public void testFindAllQueriedApplicationOptionGroupsAndApplicationOptions() {
        assertEquals(1, findAllQueried(query().setGroupOid("1.2.246.562.28.26750186798").setAoOids(asList("1.2.246.562.20.52010929637"))).getResults().size());
        // hakukohteen 52010929637 tulee olla hakemuksella ensisijainen niistä hakukohteista jotka kuuluvat ryhmään 26750186798
        assertEquals(1, findAllQueried(query().setGroupOid("1.2.246.562.28.26750186798").setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true)).getResults().size());
        // hakukohteen 52010929637 tulee olla hakemuksella ensisijainen niistä hakukohteista jotka kuuluvat ryhmään 92529355477 (ei ole!)
        assertEquals(0, findAllQueried(query().setGroupOid("1.2.246.562.28.92529355477").setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true)).getResults().size());
        // hakukohteen 18097797874 tulee olla hakemuksella ensisijainen niistä hakukohteista jotka kuuluvat ryhmään 92529355477
        assertEquals(1, findAllQueried(query().setGroupOid("1.2.246.562.28.92529355477").setAoOids(asList("1.2.246.562.20.18097797874")).setPrimaryPreferenceOnly(true)).getResults().size());
    }


    private ApplicationQueryParametersBuilder query() {
        return new ApplicationQueryParametersBuilder();
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }

    private ApplicationSearchResultDTO findAllQueried(final ApplicationQueryParametersBuilder queryBuilder) {
        ApplicationQueryParameters applicationQueryParameters = queryBuilder.build();
        AuthenticationServiceMockImpl authenticationServiceMock = new AuthenticationServiceMockImpl();
        ApplicationFilterParameters filterParameters = new ApplicationFilterParameters(5, authenticationServiceMock.getOrganisaatioHenkilo(), authenticationServiceMock.getOrganisaatioHenkilo(), authenticationServiceMock.getOrganisaatioHenkilo(), null, null);
        final ApplicationSearchResultDTO result = applicationDAO.findAllQueried(applicationQueryParameters, filterParameters);
        return result;
    }
}
