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

import java.util.*;

import com.google.common.collect.ImmutableMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.joda.time.LocalDateTime;
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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.test.util.ReflectionTestUtils;

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
        removeTestData();
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
    public void testFindAllQueriedByState() {
        assertEquals(3, countQueried(query().setState("ACTIVE")));
        assertEquals(0, countQueried(query().setState("SUBMITTED")));
    }

    @Test
    public void testFindAllQueriedByEmptyQuery() {
        assertEquals(4, countQueried(query()));
    }

    @Test
    public void testFindAllQueriedPaging() {
        assertEquals(1, countQueried(query().setRows(1)));
        assertEquals(1, countQueried(query().setStart(3)));
    }

    @Test
    public void testFindAllQueriedSorting() {
        assertEquals("Erillishaku", findAllQueried(query().setOrderBy("fullName")).getResults().get(0).getLastName());
        assertEquals("Romppainen", findAllQueried(query().setOrderBy("fullName").setOrderDir(-1)).getResults().get(0).getLastName());
    }


    @Test
    public void testFindAllQueriedByApplicationSystem() {
        assertEquals(3, countQueried(query().setAsId("1.2.246.562.29.90697286251")));
    }

    @Test
    public void testFindAllQueriedByApplicationSystemAndApplicationOption() {
        assertEquals(2, countQueried(query().setSearchTerms("").setAsId("1.2.246.562.29.90697286251").setAoId("000")));
    }

    @Test
    public void testFindAllQueriedBySendingSchool() {
        assertEquals(2, countQueried(query().setSendingSchool("1.2.246.562.10.16546622305")));
    }

    @Test
    public void testFindAllQueriedBySendingClass() {
        assertEquals(3, countQueried(query().setSendingClass("9A")));
        assertEquals(3, countQueried(query().setSendingClass("9")));
        assertEquals(0, countQueried(query().setSendingClass("FAIL")));
    }

    @Test
    public void testFindAllQueriedByUpdatedAfter() {
        assertEquals(0, countQueried(query().setUpdatedAfter(new Date())));
        assertEquals(4, countQueried(query().setUpdatedAfter(new Date(0))));
    }

    @Test
    public void testFindAllQueriesByBaseEducation() {
        // pohjakoulutuksella hakeminen toimii vain korkeakouluhauille, joissa käytetään pohjakoulutus_yo:true -tyyppisiä arvoja
        // toisen asteen haussa käytettän POHJAKOULUTU:1 tyyppistä yhtä arvoa
        assertEquals(1, countQueried(query().setBaseEducation("yo")));
        assertEquals(0, countQueried(query().setBaseEducation("fail")));
    }

    @Test
    public void testFindAllQueriedBySearchTerms() {
        assertEquals(4, countQueried(query().setSearchTerms("")));
        assertEquals(0, countQueried(query().setSearchTerms("FAIL")));
        assertEquals(2, countQueried(query().setSearchTerms("Mäkinen")));
        assertEquals(2, countQueried(query().setSearchTerms("mäkinen")));
        assertEquals(1, countQueried(query().setSearchTerms("Jill")));
        assertEquals(1, countQueried(query().setSearchTerms("14.11.1940")));
        assertEquals(1, countQueried(query().setSearchTerms("1.2.246.562.11.00000000259")));
        assertEquals(1, countQueried(query().setSearchTerms("1.2.246.562.24.00000000001")));
        assertEquals(1, countQueried(query().setSearchTerms("00000000001")));
        assertEquals(1, countQueried(query().setSearchTerms("00000000259")));
        assertEquals(0, countQueried(query().setSearchTerms("00000001259")));
        assertEquals(1, countQueried(query().setSearchTerms("010101-123N")));
        assertEquals(0, countQueried(query().setSearchTerms("010101A123K")));
    }

    @Test
    public void testFindAllQueriedByPersonOids() {
        assertEquals(2, countQueried(query().setPersonOids(asList("1.2.246.562.24.00000000001", "1.2.246.562.24.00000000002"))));
    }

    @Test
    public void testFindAllQueriedByApplicationOptionOids() {
        assertEquals(2, countQueried(query().setAoOids(asList("1.2.246.562.20.52010929637"))));
        assertEquals(2, countQueried(query().setAoOids(asList("1.2.246.562.20.18097797874"))));
        assertEquals(3, countQueried(query().setAoOids(asList("1.2.246.562.20.18097797874", "1.2.246.562.20.52010929637"))));
    }

    @Test
    public void testFindAllQueriedByApplicationOptionsWithPrimaryPreferenceOnly() {
        assertEquals(2, countQueried(query().setAoOids(asList("1.2.246.562.20.52010929637"))));
        assertEquals(1, countQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true)));
    }

    @Test
    public void testFindAllQueriedByApplicationOptionId() {
        assertEquals(2, countQueried(query().setAoId("000")));
        assertEquals(2, countQueried(query().setAoId("864")));
        assertEquals(1, countQueried(query().setAoId("864").setPrimaryPreferenceOnly(true)));
    }

    @Test
    public void testFindAllQueriedByLopOids() {
        assertEquals(0, countQueried(query().setLopOid("FAIL")));
        assertEquals(2, countQueried(query().setLopOid("1.2.246.562.10.10464399921")));
        assertEquals(2, countQueried(query().setLopOid("1.2.246.562.10.84451661825")));
    }

    @Test
    public void testFindAllQueriedWithQueryCombinations() {
        assertEquals(1, countQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true)));
        assertEquals(1, countQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true).setAsId("1.2.246.562.29.90697286251")));
        assertEquals(0, countQueried(query().setLopOid("1.2.246.562.10.84451661825").setAsId("FAIL")));
        assertEquals(0, countQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true).setDiscretionaryOnly(true)));
        assertEquals(1, countQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true).setDiscretionaryOnly(false)));
        assertEquals(0, countQueried(query().setLopOid("1.2.246.562.10.84451661825").setPrimaryPreferenceOnly(true).setAoOids(asList("1.2.246.562.20.18097797874"))));
        assertEquals(1, countQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true).setPreferenceChecked(false)));
        assertEquals(0, countQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true).setPreferenceChecked(true)));
    }

    @Test
    public void ignoresEmptyStringsInOidLists() {
        assertEquals(4, countQueried(query().setAoOids(asList(""))));
        assertEquals(4, countQueried(query().setPersonOids(asList("", null, ""))));
        assertEquals(1, countQueried(query().setPersonOids(asList("", null, "1.2.246.562.24.00000000001"))));
    }

    @Test
    public void testFindAllQueriedPreferencesChecked() {
        assertEquals(4, countQueried(query().setPreferenceChecked(false)));
        assertEquals(1, countQueried(query().setPreferenceChecked(true)));
        assertEquals(2, countQueried(query().setAoOids(asList("1.2.246.562.20.52010929637"))));
        assertEquals(1, countQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPreferenceChecked(true)));
        assertEquals(1, countQueried(query().setAoOids(asList("1.2.246.562.20.52010929637")).setPreferenceChecked(false)));
    }

    private int countQueried(final ApplicationQueryParametersBuilder queryBuilder) {
        return findAllQueried(queryBuilder).getResults().size();
    }

    @Test
    public void testFindAllQueriedApplicationOptionGroups() {
        assertEquals(1, countQueried(query().setGroupOid("1.2.246.562.28.26750186798")));
        assertEquals(0, countQueried(query().setGroupOid("1.2.246.562.28.26750186798").setPrimaryPreferenceOnly(true)));
    }

    @Test
    public void testFindAllQueriedApplicationOptionGroupsAndApplicationOptions() {
        assertEquals(1, countQueried(query().setGroupOid("1.2.246.562.28.26750186798").setAoOids(asList("1.2.246.562.20.52010929637"))));
        // hakukohteen 52010929637 tulee olla hakemuksella ensisijainen niistä hakukohteista jotka kuuluvat ryhmään 26750186798
        assertEquals(1, countQueried(query().setGroupOid("1.2.246.562.28.26750186798").setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true)));
        // hakukohteen 52010929637 tulee olla hakemuksella ensisijainen niistä hakukohteista jotka kuuluvat ryhmään 92529355477 (ei ole!)
        assertEquals(0, countQueried(query().setGroupOid("1.2.246.562.28.92529355477").setAoOids(asList("1.2.246.562.20.52010929637")).setPrimaryPreferenceOnly(true)));
        // hakukohteen 18097797874 tulee olla hakemuksella ensisijainen niistä hakukohteista jotka kuuluvat ryhmään 92529355477
        assertEquals(1, countQueried(query().setGroupOid("1.2.246.562.28.92529355477").setAoOids(asList("1.2.246.562.20.18097797874")).setPrimaryPreferenceOnly(true)));
    }

    @Test
    public void testGetNextForPaymentDueDateProcessing() {
        final List <String> aoids = new ArrayList<>();
        for (int i=0; i < 14; ++i) {
            aoids.add(String.format("1.2.246.562.11.1337133713371337.%d", i));
        }

        final ListIterator<String> i = aoids.listIterator();

        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setState(State.PASSIVE);
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setPaymentDueDate(new Date());
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setRequiredPaymentState(PaymentState.OK);
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setState(State.PASSIVE);
            setPaymentDueDate(new Date());
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setState(State.PASSIVE);
            setRequiredPaymentState(PaymentState.OK);
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setPaymentDueDate(new Date());
            setRequiredPaymentState(PaymentState.OK);
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setState(State.PASSIVE);
            setPaymentDueDate(new Date());
            setRequiredPaymentState(PaymentState.OK);
        }});

        int matchingApplications = 0;
        assertEquals(matchingApplications, applicationDAO.getNextForPaymentDueDateProcessing(10000).size());

        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setState(State.ACTIVE);
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setPaymentDueDate(new Date(0));
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setRequiredPaymentState(PaymentState.NOT_OK);
        }});

        matchingApplications = 0;
        assertEquals(matchingApplications, applicationDAO.getNextForPaymentDueDateProcessing(10000).size());

        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setState(State.ACTIVE);
            setPaymentDueDate(new Date(0));
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setState(State.INCOMPLETE);
            setRequiredPaymentState(PaymentState.NOTIFIED);
        }});

        matchingApplications = 0;
        assertEquals(matchingApplications, applicationDAO.getNextForPaymentDueDateProcessing(10000).size());

        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setState(State.ACTIVE);
            setPaymentDueDate(new Date(0));
            setRequiredPaymentState(PaymentState.NOTIFIED);
        }});
        applicationDAO.save(new Application(TEST_USER, new ApplicationPhase(applicationSystemId, "vaihe1", ImmutableMap.<String, String>of())) {{
            setOid(i.next());
            setState(State.INCOMPLETE);
            setPaymentDueDate(LocalDateTime.now().minusDays(3).toDate());
            setRequiredPaymentState(PaymentState.NOT_OK);
        }});

        matchingApplications = 2;
        assertEquals(matchingApplications, applicationDAO.getNextForPaymentDueDateProcessing(10000).size());

        for (String aoid : aoids) {
            DBCollection collection = (DBCollection) ReflectionTestUtils.invokeGetterMethod(applicationDAO, "getCollection");
            collection.remove(new BasicDBObject("oid", aoid));
        }
        matchingApplications = 0;
        assertEquals(matchingApplications, applicationDAO.getNextForPaymentDueDateProcessing(10000).size());
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
        ApplicationFilterParameters filterParameters = new ApplicationFilterParameters(5, authenticationServiceMock.getOrganisaatioHenkilo(), authenticationServiceMock.getOrganisaatioHenkilo(), authenticationServiceMock.getOrganisaatioHenkilo(), null, OppijaConstants.KOHDEJOUKKO_KORKEAKOULU, null);
        final ApplicationSearchResultDTO result = applicationDAO.findAllQueried(applicationQueryParameters, filterParameters);
        return result;
    }
}
