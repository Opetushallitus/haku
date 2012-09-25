package fi.vm.sade.oppija.haku.validation;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;


public class ValidationResultTest {

    public static final String TEST_MESSAGE = "test message";
    public static final String FIELD_NAME = "test";

    @Test
    public void testHasErrors() throws Exception {
        ValidationResult validationResult = new ValidationResult(new HashMap<String, String>());
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testHasErrorsWithErrors() throws Exception {
        ValidationResult validationResult = createValidationResultContainingOneError();
        assertTrue(validationResult.hasErrors());
    }

    @Test(expected = NullPointerException.class)
    public void testHasErrorsNullErrors() throws Exception {
        ValidationResult validationResult = new ValidationResult(null);
    }

    @Test
    public void testGetErrors() throws Exception {
        ValidationResult validationResult = createValidationResultContainingOneError();
        assertEquals(validationResult.errorCount(), 1);
    }

    @Test
    public void testSize() throws Exception {
        ValidationResult validationResult = new ValidationResult(new HashMap<String, String>());
        assertEquals(0, validationResult.errorCount());

    }
    @Test
    public void testSizeOne() throws Exception {
        ValidationResult validationResult = createValidationResultContainingOneError();
        assertEquals(1, validationResult.errorCount());

    }

    private ValidationResult createValidationResultContainingOneError() {
        HashMap<String, String> errors = new HashMap<String, String>();
        errors.put(FIELD_NAME, TEST_MESSAGE);
        return new ValidationResult(errors);
    }
}
