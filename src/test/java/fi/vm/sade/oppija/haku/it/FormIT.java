package fi.vm.sade.oppija.haku.it;


import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;
import org.junit.Before;
import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

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
        assertLinkPresent("test");
    }

    @Test
    public void testForm() throws Exception {
        beginAt("/lomake/test");
        assertLinkPresent("yhteishaku");
    }

    @Test
    public void testCategory() throws Exception {
        beginAt("/lomake/test/yhteishaku/henkilotiedot");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkPresent("nav-koulutustausta");
    }
}