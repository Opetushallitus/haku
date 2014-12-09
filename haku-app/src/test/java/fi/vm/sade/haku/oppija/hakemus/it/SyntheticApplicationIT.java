package fi.vm.sade.haku.oppija.hakemus.it;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.SyntheticApplication;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@ActiveProfiles(profiles = {"it"})
public class SyntheticApplicationIT {

    @Autowired
    ApplicationResource applicationResource;

    @Test
    public void testInvalidInput() {
        SyntheticApplication input = new SyntheticApplication("1", "2", "3", ImmutableList.of(new SyntheticApplication.Hakemus(null, "Etu", "Suku", "foobar", "barfoo")));
        Response response = applicationResource.putSyntheticApplication(input);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void testCreateAndUpdate() {
        SyntheticApplication firstInput = new SyntheticApplication("1", "2", "3", ImmutableList.of(new SyntheticApplication.Hakemus("123", "Etu", "Suku", "foobar", "barfoo")));
        Response resp1 = applicationResource.putSyntheticApplication(firstInput);
        assertEquals(200, resp1.getStatus());
        List<Application> apps1 = (List<Application>) resp1.getEntity();
        assertEquals(1, apps1.size());

        SyntheticApplication second = new SyntheticApplication("2", "2", "4", ImmutableList.of(new SyntheticApplication.Hakemus("123", "Etu", "Suku", "foobar", "barfoo")));
        Response resp2 = applicationResource.putSyntheticApplication(second);
        assertEquals(200, resp1.getStatus());
        List<Application> apps2 = (List<Application>) resp2.getEntity();
        assertEquals(1, apps2.size());

        Application app = apps2.get(0);
        Map<String, String> hakutoiveet = app.getPhaseAnswers("hakutoiveet");
        assertEquals("1", hakutoiveet.get("preference1-koulutus-id"));
        assertEquals("2", hakutoiveet.get("preference2-koulutus-id"));
    }
}
