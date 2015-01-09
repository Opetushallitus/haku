package fi.vm.sade.haku.oppija.hakemus.it;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableList;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.SyntheticApplication;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.SyntheticApplicationService;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/tomcat-container-context.xml")
@ActiveProfiles(profiles = {"it"})
public class SyntheticApplicationIT {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSystemService applicationSystemService;

    @Autowired
    private SyntheticApplicationService syntheticApplicationService;

    @Autowired
    private ApplicationResource applicationResource;

    @Before
    public void setUp() {
        this.applicationResource = new ApplicationResource(this.applicationService, this.applicationSystemService, null, syntheticApplicationService);
    }

    @Test
    public void testInvalidInput() {
        Response response = put("1", "1", null, null);
        assertEquals(400, response.getStatus());
    }

    final String hakuOid = "1.2.3";
    String hakukohde1 = "1";
    String hakukohde2 = "2";

    @Test
    public void testCreate() {
        Response resp1 = put(hakukohde1, "1", "hakijaOid1", "010101-123N");

        final List<Application> apps1 = verifyPutResponse(resp1);

        Application app = apps1.get(0);
        Map<String, String> hakutoiveet = app.getPhaseAnswers("hakutoiveet");
        assertEquals(hakukohde1, hakutoiveet.get("preference1-Koulutus-id"));
    }

    @Test
    public void testCreateAndUpdate() {
        final String hetu = "070195-953K";
        final String hakijaOid = "hakijaOid1";

        Response resp1 = put(hakukohde1, "1", hakijaOid, hetu);
        verifyPutResponse(resp1);

        Response resp2 = put(hakukohde2, "2", hakijaOid, hetu);
        final List<Application> apps2 = verifyPutResponse(resp2);

        Application app = apps2.get(0);
        Map<String, String> hakutoiveet = app.getPhaseAnswers("hakutoiveet");
        assertEquals(hakukohde1, hakutoiveet.get("preference1-Koulutus-id"));
        assertEquals(hakukohde2, hakutoiveet.get("preference2-Koulutus-id"));
    }

    @Test
    public void testCreateRoundTrip() {
        Response resp1 = put(hakukohde1, "1", "hakijaOid2", "070195-991T");
        verifyPutResponse(resp1);
        final List<Map<String, Object>> applications = applicationResource.findFullApplications("", Arrays.asList("ACTIVE", "INCOMPLETE"), null, null, null, null, null, hakuOid, null, null, hakukohde1, null, null, null, null, null, 0, 10000);
        assertEquals(1, applications.size());
    }

    private Response put(String hakukohdeOid, String tarjoajaOid, String hakijaOid, String hetu) {
        SyntheticApplication firstInput = new SyntheticApplication(
                hakukohdeOid, hakuOid,
                tarjoajaOid,
                ImmutableList.of(new SyntheticApplication.Hakemus(hakijaOid, "Etu", "Suku", hetu, null))
        );
        return applicationResource.putSyntheticApplication(firstInput);
    }

    private List<Application> verifyPutResponse(final Response response) {
        assertEquals(200, response.getStatus());
        List<Application> applications = (List<Application>) response.getEntity();
        assertEquals(1, applications.size());
        return applications;
    }
}
