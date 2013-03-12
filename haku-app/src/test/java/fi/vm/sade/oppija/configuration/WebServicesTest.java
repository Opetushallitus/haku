package fi.vm.sade.oppija.configuration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class WebServicesTest {

    private WebServices webServices;

    @Before
    public void setUp() throws Exception {
        webServices = new WebServices();
    }

    @Test
    public void testGetOrganisaatioService() throws Exception {
        assertNotNull(webServices.getKoodiService(""));
    }

    @Test
    public void testGetKoodiService() throws Exception {
        assertNotNull(webServices.getOrganisaatioService(""));
    }
}
