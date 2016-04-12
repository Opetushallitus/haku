package fi.vm.sade.haku.oppija.hakemus.it;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.State;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.SyntheticApplication;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.SyntheticApplicationService;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private I18nBundleService i18nBundleService;

    @Before
    public void setUp() {
        this.applicationResource = new ApplicationResource(this.applicationService, this.applicationSystemService, null, syntheticApplicationService, i18nBundleService);
    }

    @Test
    public void testInvalidInput() {
        Response response = put("1", "1", null, null, null);
        assertEquals(400, response.getStatus());
    }

    final String hakuOid = "1.2.3";
    String hakukohde1 = "1";
    String hakukohde2 = "2";
    final String email1 = "etu.suku1@example.com";
    final String email2 = "etu.suku2@example.com";

    @Test
    public void testCreate() {
        Response resp1 = put(hakukohde1, "1", "hakijaOid1", "010101-123N", email1);

        final List<Application> apps1 = verifyPutResponse(resp1);

        Application app = apps1.get(0);
        Map<String, String> hakutoiveet = app.getPhaseAnswers("hakutoiveet");
        assertEquals(hakukohde1, hakutoiveet.get("preference1-Koulutus-id"));

        Map<String, String> henkilotiedot = app.getPhaseAnswers("henkilotiedot");
        assertEquals(email1, henkilotiedot.get("Sähköposti"));
    }

    @Test
    public void testCreateAndUpdate() {
        final String hetu = "070195-953K";
        final String hakijaOid = "hakijaOid1";

        Response resp1 = put(hakukohde1, "1", hakijaOid, hetu, email1);
        verifyPutResponse(resp1);

        Response resp2 = put(hakukohde2, "2", hakijaOid, hetu, email2);
        final List<Application> apps2 = verifyPutResponse(resp2);

        Application app = apps2.get(0);
        Map<String, String> hakutoiveet = app.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        assertEquals(hakukohde1, hakutoiveet.get("preference1-Koulutus-id"));
        assertEquals(hakukohde2, hakutoiveet.get("preference2-Koulutus-id"));

        Map<String, String> henkilotiedot = app.getPhaseAnswers(OppijaConstants.PHASE_PERSONAL);
        assertEquals(email2, henkilotiedot.get("Sähköposti"));
        assertEquals("Etu", henkilotiedot.get("Etunimet"));
        assertEquals("Suku", henkilotiedot.get("Sukunimi"));
        assertEquals("Etu", henkilotiedot.get("Kutsumanimi"));
    }

    @Test
    public void testCreateAndUpdateWithEmptyEmail() {
        final String hetu = "070195-953K";
        final String hakijaOid = "hakijaOid1";

        Response resp1 = put(hakukohde1, "1", hakijaOid, hetu, email1);
        verifyPutResponse(resp1);

        Response resp2 = put(hakukohde1, "2", hakijaOid, hetu, "");
        final List<Application> apps2 = verifyPutResponse(resp2);

        Application app = apps2.get(0);
        Map<String, String> henkilotiedot = app.getPhaseAnswers(OppijaConstants.PHASE_PERSONAL);
        assertEquals(email1, henkilotiedot.get("Sähköposti"));
    }

    @Test
    public void testCreateRoundTrip() {
        Response resp1 = put(hakukohde1, "1", "hakijaOid2", "070195-991T", email1);
        verifyPutResponse(resp1);
        final List<Map<String, Object>> applications = applicationResource.findFullApplications("", Arrays.asList("ACTIVE", "INCOMPLETE"), null, null, null, null, null, null, hakuOid, null, null, hakukohde1, null, null, null, null, null, 0, 10000);

        ApplicationSearchDTO asdto = new ApplicationSearchDTO("", Arrays.asList(hakukohde1), Arrays.asList(hakuOid), Arrays.asList("ACTIVE", "INCOMPLETE"), Arrays.asList("oid"));
        final List<Map<String, Object>> applicationsPost = applicationResource.findFullApplicationsPost(asdto);
        System.out.println(applicationsPost.get(0));
        assertEquals(2, applications.size());
        assertEquals(2, applicationsPost.size());
    }

    @Test
    public void testPassivatedApplicationsGetIgnored() throws URISyntaxException {
        final String hakijaOid = "hakijaOid";
        final String hetu = "210495-995C";

        // 1. haku
        Response resp1 = put("hakukohde1", "tarjoaja1", hakijaOid, hetu, email1);
        verifyPutResponse(resp1);
        Application app1 = takeApplication(resp1, 0);
        assertEquals(app1.getState(), State.ACTIVE);
        Map<String, String> hakutoiveet = app1.getAnswers().get("hakutoiveet");
        assertEquals("hakukohde1", hakutoiveet.get("preference1-Koulutus-id"));
        assertEquals("tarjoaja1", hakutoiveet.get("preference1-Opetuspiste-id"));

        // 1. haun passivointi
        String oid = takeApplication(resp1, 0).getOid();
        Application application = applicationService.getApplicationByOid(oid);
        application.setState(State.PASSIVE);
        applicationDAO.update(new Application(oid), application);

        // 2. haku
        Response resp2 = put("hakukohde2", "tarjoaja2", hakijaOid, hetu, email1);
        verifyPutResponse(resp2);
        Application app2 = takeApplication(resp2, 0);
        assertEquals(app2.getState(), State.ACTIVE);
        hakutoiveet = app2.getAnswers().get("hakutoiveet");
        assertEquals("hakukohde2", hakutoiveet.get("preference1-Koulutus-id"));
        assertEquals("tarjoaja2", hakutoiveet.get("preference1-Opetuspiste-id"));
    }

    private Response put(String hakukohdeOid, String tarjoajaOid, String hakijaOid, String hetu, String email) {
        SyntheticApplication firstInput = new SyntheticApplication(
                hakukohdeOid, hakuOid,
                tarjoajaOid,
                ImmutableList.of(new SyntheticApplication.Hakemus(hakijaOid, "Etu", "Suku","","", hetu, email, null))
        );
        return applicationResource.putSyntheticApplication(firstInput);
    }

    private List<Application> verifyPutResponse(final Response response) {
        assertEquals(200, response.getStatus());
        List<Application> applications = (List<Application>) response.getEntity();
        assertEquals(1, applications.size());
        return applications;
    }

    private static Application takeApplication(Response response, int nth) {
        return ((List<Application>)response.getEntity()).get(nth);
    }
}
