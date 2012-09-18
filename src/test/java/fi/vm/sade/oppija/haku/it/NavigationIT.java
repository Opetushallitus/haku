package fi.vm.sade.oppija.haku.it;

import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class NavigationIT extends AbstractIT {

    public NavigationIT() {
        jsonModelFileName = "navigation-test.json";
        //
    }

    @Test
    public void testNavigationExists() throws IOException {
        beginAt("/fi/test/yhteishaku/henkilotiedot");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkPresent("nav-koulutustausta");
        assertLinkPresent("nav-yhteenveto");
    }

    @Test
    public void testFirstGategoryNavButtons() throws IOException {
        beginAt("/fi/test/yhteishaku/henkilotiedot");
        assertSubmitButtonPresent("nav-next");
        assertSubmitButtonNotPresent("nav-prev");
    }

    @Test
    public void testMiddleGategoryNavButtons() throws IOException {
        beginAt("/fi/test/yhteishaku/koulutustausta");
        assertSubmitButtonPresent("nav-next");
        assertSubmitButtonPresent("nav-prev");
    }

    @Test
    public void testLastGategoryNavButtons() throws IOException {
        beginAt("/fi/test/yhteishaku/yhteenveto");
        assertSubmitButtonNotPresent("nav-next");
        assertSubmitButtonPresent("nav-prev");
        assertSubmitButtonPresent("nav-save");
    }
}
