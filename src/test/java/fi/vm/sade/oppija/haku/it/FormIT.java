package fi.vm.sade.oppija.haku.it;


import net.sourceforge.jwebunit.util.TestingEngineRegistry;
import org.junit.Before;
import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author Hannu Lyytikainen
 */
public class FormIT extends AbstractIT {

    public FormIT() {
        this.jsonModelFileName = "navigation-test.json";
    }

    @Before
    public void setUp() throws Exception {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);    // use HtmlUnit
        setBaseUrl(getBaseUrl());

    }

    @Test
    public void testApplicationPeriod() {
        beginAt("/fi/");
        assertLinkPresent("test");
    }

    @Test
    public void testForm() throws Exception {
        beginAt("/fi/test");
        assertLinkPresent("yhteishaku");
    }

    @Test
    public void testCategory() throws Exception {
        beginAt("/fi/test/yhteishaku/henkilotiedot");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkPresent("nav-koulutustausta");
        assertLinkPresent("nav-yhteenveto");
    }
}