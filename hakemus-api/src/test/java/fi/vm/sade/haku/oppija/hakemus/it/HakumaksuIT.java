package fi.vm.sade.haku.oppija.hakemus.it;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class HakumaksuIT extends IntegrationTestSupport {

    @Test
    public void hakumaksuvelvollisuusTunnistetaanTest() {
        Application application = IntegrationTestSupport.getTestApplication("1.2.246.562.11.00004580445");
        assertNotNull(application);
    }
}
