package fi.vm.sade.oppija.haku.validation;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;


public class ValidationResultTest {

    public static final String TEST_MESSAGE = "test message";
    public static final String FIELD_NAME = "test";

    @Test
    public void testHasErrors() throws Exception {
        HakemusState hakemusState = new HakemusState(createHakemus());
        assertFalse(hakemusState.hasErrors());
    }

    private Hakemus createHakemus() {
        return new Hakemus(new HakemusId("", "", "", ""), new HashMap<String, String>());
    }

    @Test
    public void testHasErrorsWithErrors() throws Exception {
        HakemusState hakemusState = createValidationResultContainingOneError();
        assertTrue(hakemusState.hasErrors());
    }

    @Test
    public void testGetErrors() throws Exception {
        HakemusState hakemusState = createValidationResultContainingOneError();
        assertEquals(hakemusState.errorCount(), 1);
    }

    @Test
    public void testSize() throws Exception {
        HakemusState hakemusState = new HakemusState(createHakemus());
        assertEquals(0, hakemusState.errorCount());

    }

    @Test
    public void testSizeOne() throws Exception {
        HakemusState hakemusState = createValidationResultContainingOneError();
        assertEquals(1, hakemusState.errorCount());

    }

    private HakemusState createValidationResultContainingOneError() {

        final HakemusState hakemusState = new HakemusState(createHakemus());
        hakemusState.addError(FIELD_NAME, TEST_MESSAGE);
        return hakemusState;
    }
}
