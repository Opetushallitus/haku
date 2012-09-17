package fi.vm.sade.oppija.haku.it;


import net.sourceforge.jwebunit.util.TestingEngineRegistry;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author Hannu Lyytikainen
 */
public class FormIT extends AbstractIT {

    @BeforeClass
    public static void prepare() {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);    // use HtmlUnit
        setBaseUrl("http://localhost:8080/haku");

    }

    @Before
    public void setUp() throws Exception {
        this.jsonModelFileName = "navigation-test.json";
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