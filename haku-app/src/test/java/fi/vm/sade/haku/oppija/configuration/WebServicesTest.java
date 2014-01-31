package fi.vm.sade.haku.oppija.configuration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class WebServicesTest {

    private WebServices webServices;

    @Before
    public void setUp() throws Exception {
        webServices = new WebServices();
    }

    @Ignore("Can't read common.properties")
    @Test
    public void testGetCachingKoodistoClient() throws Exception {
        assertNotNull(webServices.getCachingKoodistoClient());
    }
}
