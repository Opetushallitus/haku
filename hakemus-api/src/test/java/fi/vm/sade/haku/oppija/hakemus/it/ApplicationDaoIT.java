package fi.vm.sade.haku.oppija.hakemus.it;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;

public class ApplicationDaoIT extends IntegrationTestSupport {
    @Test
    public void fetchApplication() throws IOException {
        final Application application = getTestApplication();
        assertEquals("aho minna wa", application.getFullName());
    }
}

