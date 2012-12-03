package fi.vm.sade.oppija.ui.it;


import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import org.junit.Before;
import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;

/**
 * @author Hannu Lyytikainen
 */
public class FormIT extends AbstractRemoteTest {

    @Before
    public void setUp() throws Exception {
        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();

        initModel(dummyMem.getModel());

    }

    @Test
    public void testApplicationPeriod() {
        beginAt("/lomake");
        assertLinkPresent("Yhteishaku");
    }

    @Test
    public void testForm() throws Exception {
        beginAt("/lomake/Yhteishaku");
        assertLinkPresent("yhteishaku");
    }

    @Test
    public void testCategory() throws Exception {
        beginAt("/lomake/Yhteishaku/yhteishaku/henkilotiedot");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkPresent("nav-koulutustausta");
    }
}