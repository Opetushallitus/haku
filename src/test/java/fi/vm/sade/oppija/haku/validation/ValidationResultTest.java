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
        ValidationResult validationResult = new ValidationResult(createHakemus());
        assertFalse(validationResult.hasErrors());
    }

    private Hakemus createHakemus() {
        return new Hakemus(new HakemusId("", "", "", ""), new HashMap<String, String>());
    }

    @Test
    public void testHasErrorsWithErrors() throws Exception {
        ValidationResult validationResult = createValidationResultContainingOneError();
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testGetErrors() throws Exception {
        ValidationResult validationResult = createValidationResultContainingOneError();
        assertEquals(validationResult.errorCount(), 1);
    }

    @Test
    public void testSize() throws Exception {
        ValidationResult validationResult = new ValidationResult(createHakemus());
        assertEquals(0, validationResult.errorCount());

    }

    @Test
    public void testSizeOne() throws Exception {
        ValidationResult validationResult = createValidationResultContainingOneError();
        assertEquals(1, validationResult.errorCount());

    }

    private ValidationResult createValidationResultContainingOneError() {

        final ValidationResult validationResult = new ValidationResult(createHakemus());
        validationResult.addError(FIELD_NAME, TEST_MESSAGE);
        return validationResult;
    }
}
