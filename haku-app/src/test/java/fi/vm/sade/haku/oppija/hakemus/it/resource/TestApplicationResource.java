package fi.vm.sade.haku.oppija.hakemus.it.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.resource.XlsModel;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.servlet.jsp.jstl.core.Config;
import java.util.*;

import static org.junit.Assert.*;

@ActiveProfiles("it")
public class TestApplicationResource extends IntegrationTestSupport {

    ApplicationResource applicationResource = appContext.getBean(ApplicationResource.class);

    @Test
    public void testKorkeakouluExcel() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(Config.FMT_LOCALE + ".session", new Locale("fi"));

        XlsModel xlsModel = applicationResource.getApplicationsByOids(
                request, "1.2.246.562.29.95390561488", "test", "", "", null, null,
                null, "", "1.2.246.562.20.92555013215", "", Sets.newHashSet("ulk"), false, false,
                "", "", null, 0, 100);

        String[][] expected = new String[][]{
                {"1.2.246.562.11.00001544594", "1.2.246.562.24.39736979832", "X", "Heppalahti", "Joel IX", "Joel", "Ei", "Ei", "nainen", "19.09.1988", "Dhading, Nepal", null, "hakija-19995@oph.fi", "050 1813961", null, "Lorem osoite", "44600", "Kathmandu", null, null, "englanti", "X", "2014", "Proficiency Certificate Level in Nursing", "Green Tara College of Health Science", "XXX", "Kyllä", "X", "X", "X", "X", "Englanti", "1", "Ei hakukelpoinen", "Applicant has not provided compulsory proof of English language skills.", "Tuntematon", "Saapunut", "Tarkistettu"},
                {"1.2.246.562.11.00001307032", "1.2.246.562.24.14229104472", "", "Vitsivuori", "Elias II", "Elias", "Ei", "Kyllä", "mies", "01.01.1901", null, "010101-123N", "hakija-123969@oph.fi", "050 11440811", "Suomi", null, null, null, "Kuumakallio 271", "02210", "englanti", "X", "2012", "Higher Secondary Education", "Texas International Higher Secondary School", "XXX", "Kyllä", "X", "X", "X", "X", "Englanti", "1", "Hakukelpoinen", "", "Kopio", "Saapunut", "Tarkistettu"}
        };

        List<String> rowKeys = xlsModel.rowKeyList();
        List<Element> colKeys = xlsModel.columnKeyList();
        for (int row = 0; row < rowKeys.size(); row++) {
            for (int col = 0; col < colKeys.size(); col++) {
                assertEquals("Compared row " + row + ", col " + col, expected[row][col], xlsModel.getValue(rowKeys.get(row), colKeys.get(col)));
            }
        }

    }

    @Test
    public void testBaseEducationFilter() {
        // Should not find any when using non existing base education
        ApplicationSearchResultDTO result = applicationResource.findApplicationsOrdered(
                "fullName",
                "asc",
                "",
                null,
                null,
                null,
                "",
                "",
                Sets.newHashSet("base_education_that_does_not_exist"),
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
                50
        );
        assertEquals(0, result.getTotalCount());

        // 2 applications exist with base education "ulk"
        result = applicationResource.findApplicationsOrdered(
                "fullName",
                "asc",
                "",
                null,
                null,
                null,
                "",
                "",
                Sets.newHashSet("ulk"),
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
                50
        );
        assertEquals(2, result.getTotalCount());

        // 2 applications with base education "ulk" and 1 with base education "yo_ulkomainen" exist
        result = applicationResource.findApplicationsOrdered(
                "fullName",
                "asc",
                "",
                null,
                null,
                null,
                "",
                "",
                Sets.newHashSet("ulk", "yo_ulkomainen"),
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
                50
        );
        assertEquals(3, result.getTotalCount());
    }

    @Test
    public void testFindApplicationsByPersonOids() {
        final String PERSON1_OID = "1.2.246.562.24.14229104472";
        final String PERSON2_OID = "1.2.246.562.24.40135708059";

        Map<String, Collection<Map<String, Object>>> applicationsByPersonOids =
                applicationResource.findApplicationsByPersonOid(Sets.newHashSet(PERSON1_OID, PERSON2_OID), false, true);

        assertEquals(22, applicationsByPersonOids.get(PERSON1_OID).size());
        assertNull(getHenkilotunnus(applicationsByPersonOids.get(PERSON1_OID)));
        assertEquals(1, applicationsByPersonOids.get(PERSON2_OID).size());
    }

    @Test
    public void testFindApplicationsByPersonOidsWithAllKeys() {
        final String PERSON1_OID = "1.2.246.562.24.14229104472";
        final String PERSON2_OID = "1.2.246.562.24.40135708059";

        Map<String, Collection<Map<String, Object>>> applicationsByPersonOids =
                applicationResource.findApplicationsByPersonOid(Sets.newHashSet(PERSON1_OID, PERSON2_OID), true, true);

        assertEquals(22, applicationsByPersonOids.get(PERSON1_OID).size());
        assertNull(getHenkilotunnus(applicationsByPersonOids.get(PERSON1_OID)));
        assertEquals(1, applicationsByPersonOids.get(PERSON2_OID).size());
    }

    @Test
    public void testFindApplicationsByPersonOidsWithAllKeysWithSensitiveInfo() {
        final String PERSON1_OID = "1.2.246.562.24.14229104472";
        final String PERSON2_OID = "1.2.246.562.24.40135708059";

        Map<String, Collection<Map<String, Object>>> applicationsByPersonOids =
                applicationResource.findApplicationsByPersonOid(Sets.newHashSet(PERSON1_OID, PERSON2_OID), true, false);

        assertEquals(22, applicationsByPersonOids.get(PERSON1_OID).size());
        assertTrue(applicationsByPersonOids.get(PERSON1_OID) != null);
        assertTrue(applicationsByPersonOids.get(PERSON2_OID) != null);
        assertNotNull(getHenkilotunnus(applicationsByPersonOids.get(PERSON1_OID)));
        assertEquals(1, applicationsByPersonOids.get(PERSON2_OID).size());
    }

    @Test
    public void testFindApplicationsByApplicationOptions() {
        Set<String> aos = new HashSet<>();
        aos.add("1.2.246.562.20.91374364379");
        aos.add("1.2.246.562.20.29983577775");
        Collection<Map<String, Object>> applicationsByAOs = applicationResource.findApplicationsByApplicationOption(aos);
        assertEquals(4, applicationsByAOs.size());

        Set<String> emptyAOSet = new HashSet<>();
        Collection<Map<String, Object>> empty = applicationResource.findApplicationsByApplicationOption(emptyAOSet);
        assertEquals(0, empty.size());
    }

    @Test
    public void testFindApplicationsByApplicationSystems() {
        Set<String> applicationSystemOids = Sets.newHashSet(
                "1.2.246.562.5.2014022711042555034240"
        );
        Collection<Map<String, Object>> applications = applicationResource.findApplicationsByApplicationSystem(applicationSystemOids);
        assertEquals(1, applications.size());
    }

    @Test
    public void testFindPersonOIDsByApplicationSystem() {
        Set<String> asIds = Sets.newHashSet(
                "1.2.246.562.5.2013080813081926341927",
                "1.2.246.562.29.95390561488"
        );
        Collection<String> personOids = applicationResource.findPersonOIDsByApplicationSystem(asIds);
        assertEquals(3, personOids.size());
        assertTrue(personOids.contains("1.2.246.562.24.25780876607"));
        assertTrue(personOids.contains("1.2.246.562.24.14229104472"));
        Set<String> emptyAOSet = new HashSet<>();
        Collection<String> empty = applicationResource.findPersonOIDsByApplicationSystem(emptyAOSet);
        assertEquals(0, empty.size());
    }

    @Test
    public void testFindPersonOIDsByApplicationOption() {
        Set<String> applicationOptionOids = Sets.newHashSet(
                "1.2.246.562.20.92555013215"
        );
        Collection<String> personOids = applicationResource.findPersonOIDsByApplicationOption(applicationOptionOids);
        assertEquals(2, personOids.size());
        assertTrue(personOids.contains("1.2.246.562.24.14229104472"));
        assertTrue(personOids.contains("1.2.246.562.24.39736979832"));
        Set<String> emptyAOSet = new HashSet<>();
        Collection<String> empty = applicationResource.findPersonOIDsByApplicationSystem(emptyAOSet);
        assertEquals(0, empty.size());
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
