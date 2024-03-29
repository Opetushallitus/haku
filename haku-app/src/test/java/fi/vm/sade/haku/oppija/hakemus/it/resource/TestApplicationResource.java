package fi.vm.sade.haku.oppija.hakemus.it.resource;

import com.google.common.collect.Sets;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParametersBuilder;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.resource.XlsModel;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.servlet.jsp.jstl.core.Config;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

@ActiveProfiles("it")
public class TestApplicationResource extends IntegrationTestSupport {

    ApplicationResource applicationResource = appContext.getBean(ApplicationResource.class);
    ApplicationService applicationService = appContext.getBean(ApplicationService.class);

    @Test
    public void testKorkeakouluExcel() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(Config.FMT_LOCALE + ".session", new Locale("fi"));

        XlsModel xlsModel = applicationResource.getApplicationsByOids(
                request, "1.2.246.562.29.95390561488", "test", "", "", null, null,
                null, "", "1.2.246.562.20.92555013215", "", Sets.newHashSet("ulk"), false, false,
                "", "", null, 0, 100);

        String[][] expected = new String[][]{
                {"1.2.246.562.11.00001544594", "1.2.246.562.24.39736979832", "X", "Heppalahti", "Joel IX", "Joel", "Ei", "Ei", "nainen", "19.09.1988", "Dhading, Nepal", null, "hakija-19995@oph.fi", "050 1813961", null, "Lorem osoite", "44600", "Kathmandu", null, null, "englanti", "X", "2014", "Proficiency Certificate Level in Nursing", "Green Tara College of Health Science", "XXX", "Kyllä", "X", "X", "X", "X", "Englanti", "1", "Ei hakukelpoinen", "Applicant has not provided compulsory proof of English language skills.", "Tuntematon", "Ei tarkistettu", "Saapunut", "Tarkistettu"},
                {"1.2.246.562.11.00001307032", "1.2.246.562.24.14229104472", "", "Vitsivuori", "Elias II", "Elias", "Ei", "Kyllä", "mies", "01.01.2000", null, "010100A939R", "hakija-123969@oph.fi", "050 11440811", "Suomi", null, null, null, "Kuumakallio 271", "02210", "englanti", "X", "2012", "Higher Secondary Education", "Texas International Higher Secondary School", "XXX", "Kyllä", "X", "X", "X", "X", "Englanti", "1", "Hakukelpoinen", "", "Kopio", "Ei tarkistettu", "Saapunut", "Tarkistettu"}
        };

        List<String> rowKeys = xlsModel.rowKeyList();
        List<Element> colKeys = xlsModel.columnKeyList();
        for (int row = 0; row < rowKeys.size(); row++) {
            for (int col = 0; col < colKeys.size(); col++) {
                assertEquals("Compared row " + row + ", col " + col, expected[row][col], xlsModel.getValue(rowKeys.get(row), colKeys.get(col)));
            }
        }

    }

    private ApplicationSearchResultDTO findApplicationsOrderedWithBaseEducationFilter(Set<String> baseEducations) {
        return applicationResource.findApplicationsOrdered(
                "fullName",
                "asc",
                "",
                null,
                null,
                null,
                null,
                "",
                "",
                baseEducations,
                "",
                "1.2.246.562.29.95390561488",
                "kausi_k",
                "2015",
                "1.2.246.562.20.63351226459",
                false,
                false,
                "",
                "",
                null,
                0,
                50,
                null
        );
    }

    @Test
    public void testBaseEducationFilter() {
        // Should not find any when using non existing base education
        ApplicationSearchResultDTO result = findApplicationsOrderedWithBaseEducationFilter(Sets.newHashSet("base_education_that_does_not_exist"));
        assertEquals(0, result.getTotalCount());

        result = findApplicationsOrderedWithBaseEducationFilter(Sets.newHashSet("ulk"));
        assertEquals(2, result.getTotalCount());

        result = findApplicationsOrderedWithBaseEducationFilter(Sets.newHashSet("ulk", "yo_ulkomainen"));
        assertEquals(3, result.getTotalCount());
    }

    @Test
    public void testFindFullApplicationsWithOrganizationFilter() {
        List<Map<String, Object>> result = applicationResource.findFullApplications(
                null, null, null, null, null, null, null, null, null, null, "1.2.246.562.10.2013102114310829376114",
                null, null, null, null, null, null, null, null, 0, 50, false
        );
        assertEquals(3, result.size());
    }

    @Test
    public void testFindApplicationsByPersonOids() {
        final String PERSON1_OID = "1.2.246.562.24.14229104472";
        final String PERSON2_OID = "1.2.246.562.24.40135708059";

        Map<String, Collection<Map<String, Object>>> applicationsByPersonOids =
                applicationResource.findApplicationsByPersonOid(Sets.newHashSet(PERSON1_OID, PERSON2_OID), false, true);

        assertEquals(23, applicationsByPersonOids.get(PERSON1_OID).size());
        assertNull(getHenkilotunnus(applicationsByPersonOids.get(PERSON1_OID)));
        assertEquals(1, applicationsByPersonOids.get(PERSON2_OID).size());
    }

    @Test
    public void testFindApplicationsByPersonOidsWithAllKeys() {
        final String PERSON1_OID = "1.2.246.562.24.14229104472";
        final String PERSON2_OID = "1.2.246.562.24.40135708059";

        Map<String, Collection<Map<String, Object>>> applicationsByPersonOids =
                applicationResource.findApplicationsByPersonOid(Sets.newHashSet(PERSON1_OID, PERSON2_OID), true, true);

        assertEquals(23, applicationsByPersonOids.get(PERSON1_OID).size());
        assertNull(getHenkilotunnus(applicationsByPersonOids.get(PERSON1_OID)));
        assertEquals(1, applicationsByPersonOids.get(PERSON2_OID).size());
    }

    @Test
    public void testFindApplicationsByPersonOidsWithAllKeysWithSensitiveInfo() {
        final String PERSON1_OID = "1.2.246.562.24.14229104472";
        final String PERSON2_OID = "1.2.246.562.24.40135708059";

        Map<String, Collection<Map<String, Object>>> applicationsByPersonOids =
                applicationResource.findApplicationsByPersonOid(Sets.newHashSet(PERSON1_OID, PERSON2_OID), true, false);

        assertEquals(23, applicationsByPersonOids.get(PERSON1_OID).size());
        assertTrue(applicationsByPersonOids.get(PERSON1_OID) != null);
        assertTrue(applicationsByPersonOids.get(PERSON2_OID) != null);
        assertNotNull(getHenkilotunnus(applicationsByPersonOids.get(PERSON1_OID)));
        assertEquals(1, applicationsByPersonOids.get(PERSON2_OID).size());
    }

    @Test
    public void testFindApplicationsByApplicationOptions() {
        List<String> aoOids = newArrayList("1.2.246.562.20.29983577775", "1.2.246.562.20.91374364379");
        List<Map<String, Object>> result = applicationService.findFullApplications(
                new ApplicationQueryParametersBuilder().setAoOids(aoOids).build());

        assertEquals(4, result.size());
    }

    @Test
    public void testFindApplicationsByApplicationOptionsWithOrganizationFilter() {
        List<String> aoOids = newArrayList(
                "1.2.246.562.20.91374364379",
                "1.2.246.562.20.29983577775"
        );
        List<Map<String, Object>> result = applicationService.findFullApplications(
                new ApplicationQueryParametersBuilder().setAoOids(aoOids)
                        .setOrganizationFilter("1.2.246.562.10.2013102114310829376114").build());
        assertEquals(3, result.size());
    }

    @Test
    public void testFindApplicationsByApplicationSystemsWithOrganizationFilter() {
        List<String> asOids = newArrayList("1.2.246.562.29.14662042044");

        List<Map<String, Object>> result1 = applicationService.findFullApplications(
                new ApplicationQueryParametersBuilder().setAsIds(asOids)
                        .setOrganizationFilter("nonExistingOrg").build());
        assertEquals(0, result1.size());

        List<Map<String, Object>> result2 = applicationService.findFullApplications(
                new ApplicationQueryParametersBuilder().setAsIds(asOids)
                        .setOrganizationFilter("1.2.246.562.10.40384720658").build());
        assertEquals(1, result2.size());
    }

    @Test
    public void testFindApplicationsByApplicationSystems() {
        List<String> asOids = newArrayList("1.2.246.562.29.14662042044");
        List<Map<String, Object>> result = applicationService.findFullApplications(new ApplicationQueryParametersBuilder().setAsIds(asOids).build());
        assertEquals(3, result.size());
    }

    @Test
    public void testFindPersonOIDsByApplicationSystem() {
        Set<String> asOids = Sets.newHashSet(
                "1.2.246.562.5.2013080813081926341927",
                "1.2.246.562.29.95390561488"
        );
        Collection<String> personOids = applicationResource.findPersonOIDsByApplicationSystem(asOids, null);
        assertEquals(3, personOids.size());
        assertTrue(personOids.contains("1.2.246.562.24.25780876607"));
        assertTrue(personOids.contains("1.2.246.562.24.14229104472"));
        Set<String> emptyAOSet = new HashSet<>();
        Collection<String> empty = applicationResource.findPersonOIDsByApplicationSystem(emptyAOSet, null);
        assertEquals(0, empty.size());
    }

    @Test
    public void testFindPersonOIDsByApplicationSystemWithOrganizationFilter() {
        Set<String> asOids = Sets.newHashSet(
                "1.2.246.562.5.2013080813081926341927",
                "1.2.246.562.29.95390561488"
        );
        Collection<String> res1 = applicationResource.findPersonOIDsByApplicationSystem(asOids, "1.2.246.562.10.2013102114310829376114");
        assertEquals(2, res1.size());
        Collection<String> res2 = applicationResource.findPersonOIDsByApplicationSystem(asOids, "nonExistingOrg");
        assertEquals(0, res2.size());
    }

    @Test
    public void testFindPersonOIDsByApplicationOption() {
        Set<String> applicationOptionOids = Sets.newHashSet(
                "1.2.246.562.20.92555013215"
        );
        Collection<String> personOids = applicationResource.findPersonOIDsByApplicationOption(applicationOptionOids, null);
        assertEquals(2, personOids.size());
        assertTrue(personOids.contains("1.2.246.562.24.14229104472"));
        assertTrue(personOids.contains("1.2.246.562.24.39736979832"));
        Set<String> emptyAOSet = new HashSet<>();
        Collection<String> empty = applicationResource.findPersonOIDsByApplicationSystem(emptyAOSet, null);
        assertEquals(0, empty.size());
    }

    @Test
    public void testFindPersonOIDsByApplicationOptionWithOrganizationFilter() {
        Set<String> applicationOptionOids = Sets.newHashSet(
                "1.2.246.562.20.92555013215"
        );
        Collection<String> res1 = applicationResource.findPersonOIDsByApplicationOption(applicationOptionOids, "1.2.246.562.10.2013102114310829376114");
        assertEquals(2, res1.size());
        Collection<String> res2 = applicationResource.findPersonOIDsByApplicationOption(applicationOptionOids, "nonExistingOrg");
        assertEquals(0, res2.size());
    }

    @Test
    public void testFindByPreferenceEligibilityWithEligible() {
        ApplicationQueryParameters params = new ApplicationQueryParametersBuilder()
                .setPreferenceEligibility(PreferenceEligibility.Status.ELIGIBLE.name()).build();
        ApplicationSearchResultDTO applications = applicationService.findApplications(params);
        assertEquals(3, applications.getTotalCount());
    }

    @Test
    public void testFindByPreferenceEligibilityWithIneligibleAndAoOid() {
        ApplicationQueryParameters params = new ApplicationQueryParametersBuilder()
                .addAoOid("1.2.246.562.20.91374364379")
                .setPreferenceEligibility(PreferenceEligibility.Status.INELIGIBLE.name()).build();
        ApplicationSearchResultDTO applications = applicationService.findApplications(params);
        assertEquals(2, applications.getTotalCount());
    }

    @Test
    public void testFindByPreferenceEligibilityWithAoOid() {
        ApplicationQueryParameters params = new ApplicationQueryParametersBuilder()
                .addAoOid("1.2.246.562.20.91374364379").build();
        ApplicationSearchResultDTO applications = applicationService.findApplications(params);
        assertEquals(3, applications.getTotalCount());
    }

    @Test
    public void testModifiedApplicationsOnlyFilter() {
        ApplicationQueryParameters params = new ApplicationQueryParametersBuilder()
            .setModifiedApplicationsOnly(true)
            .build();
        ApplicationSearchResultDTO applications = applicationService.findApplications(params);
        assertEquals(8, applications.getTotalCount());
    }

    private String getHenkilotunnus(Collection<Map<String, Object>> applications) {
        for (Map<String, Object> entry : applications) {
            if (! entry.containsKey("answers")) {
                continue;
            }

            Map<String, Object> answers = (HashMap<String, Object>) entry.get("answers");

            if (answers == null || !answers.containsKey("henkilotiedot")) {
                continue;
            }

            Map<String, Object> henkilotiedot = (HashMap<String, Object>) answers.get("henkilotiedot");
            if (henkilotiedot == null || !henkilotiedot.containsKey("Henkilotunnus")) {
                continue;
            }
            else {
                String hetu = (String) henkilotiedot.get("Henkilotunnus");
                if (hetu != null) {
                    return hetu;
                }
            }
        }
        return null;
    }


}
