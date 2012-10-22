package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class NavigationIT extends AbstractRemoteTest {

    @Before
    public void setUp() throws Exception {
        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();
        initModel(dummyMem.getModel());
    }

    @Test
    public void testNavigationExists() throws IOException {
        beginAt("/lomake/test/yhteishaku/henkilotiedot");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkPresent("nav-koulutustausta");
    }

    @Test
    public void testFirstGategoryNavButtons() throws IOException {
        beginAt("/lomake/test/yhteishaku/henkilotiedot");
        assertElementPresentByXPath("//button[@name='nav-next']");
        assertElementNotPresentByXPath("//button[@name='nav-prev']");
    }

    @Test
    public void testMiddleGategoryNavButtons() throws IOException {
        beginAt("/lomake/test/yhteishaku/koulutustausta");
        assertElementPresentByXPath("//button[@name='nav-next']");
        assertElementPresentByXPath("//button[@name='nav-prev']");
    }

    @Test
    public void testLastGategoryNavButtons() throws IOException {
        beginAt("/lomake/test/yhteishaku/yhteenveto");
        assertElementNotPresentByXPath("//button[@name='nav-next']");
        assertElementPresentByXPath("//button[@name='nav-prev']");
        assertElementPresentByXPath("//button[@name='nav-send']");
    }
}
