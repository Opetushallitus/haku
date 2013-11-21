package fi.vm.sade.haku.oppija.lomake.exception;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ElementNotFoundTest {

    @Test
    public void testName() throws Exception {
        String id = ElementUtil.randomId();
        ElementNotFound elementNotFound = new ElementNotFound(id);
        assertTrue(elementNotFound.getMessage().contains(id));
    }
}
