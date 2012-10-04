package fi.vm.sade.oppija.haku.it;

import net.sourceforge.jwebunit.api.IElement;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

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
        assertElementPresentByXPath("//button[@name='nav-next']");
        assertElementNotPresentByXPath("//button[@name='nav-prev']");
    }

    @Test
    public void testMiddleGategoryNavButtons() throws IOException {
        beginAt("/fi/test/yhteishaku/koulutustausta");
        assertElementPresentByXPath("//button[@name='nav-next']");
        assertElementPresentByXPath("//button[@name='nav-prev']");
    }

    @Test
    public void testLastGategoryNavButtons() throws IOException {
        beginAt("/fi/test/yhteishaku/yhteenveto");
        assertElementNotPresentByXPath("//button[@name='nav-next']");
        assertElementPresentByXPath("//button[@name='nav-prev']");
        assertElementPresentByXPath("//button[@name='nav-save']");
    }
}
