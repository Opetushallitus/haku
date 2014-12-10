package fi.vm.sade.haku.oppija.hakemus.it;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.SyntheticApplication;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@ActiveProfiles(profiles = {"it"})
public class SyntheticApplicationIT {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSystemService applicationSystemService;

    private ApplicationResource applicationResource ;

    @Before
    public void setUp() {
        this.applicationResource = new ApplicationResource(this.applicationService, this.applicationSystemService);
    }

    @Test
    public void testInvalidInput() {
        Response response = put("1", "1", null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void testCreateAndUpdate() {
        String hakukohde1 = "1";
        String hakukohde2 = "2";

        Response resp1 = put(hakukohde1, "1", "010101-123N");
        assertEquals(200, resp1.getStatus());
        List<Application> apps1 = (List<Application>) resp1.getEntity();
        assertEquals(1, apps1.size());

        Response resp2 = put(hakukohde2, "2", "010101-123N");
        assertEquals(200, resp1.getStatus());
        List<Application> apps2 = (List<Application>) resp2.getEntity();
        assertEquals(1, apps2.size());

        Application app = apps2.get(0);
        Map<String, String> hakutoiveet = app.getPhaseAnswers("hakutoiveet");
        assertEquals(hakukohde1, hakutoiveet.get("preference1-koulutus-id"));
        assertEquals(hakukohde2, hakutoiveet.get("preference2-koulutus-id"));
    }

    private Response put(String hakukohdeOid, String tarjoajaOid, String hetu) {
        SyntheticApplication firstInput = new SyntheticApplication(
                hakukohdeOid,
                "1.2.3",
                tarjoajaOid,
                ImmutableList.of(new SyntheticApplication.Hakemus(hetu, "Etu", "Suku", "foobar", "barfoo"))
        );
        return applicationResource.putSyntheticApplication(firstInput);
    }
}
