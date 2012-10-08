package fi.vm.sade.oppija.haku.validation;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class ValidationResultTest {

    public static final String TEST_MESSAGE = "test message";
    public static final String FIELD_NAME = "test";
    private ValidationResult validationResult;

    @Before
    public void setUp() throws Exception {
        this.validationResult = new ValidationResult();
    }

    @Test
    public void testHasErrorsFalse() throws Exception {
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testHasErrorsTrue() throws Exception {
        validationResult = new ValidationResult("key", "error");
        assertTrue(validationResult.hasErrors());
    }

    @Test(expected = NullPointerException.class)
    public void testHasErrorsNullMap() throws Exception {
        final Map<String, String> errors = null;
        validationResult = new ValidationResult(errors);
        assertFalse(validationResult.hasErrors());
    }
    @Test(expected = NullPointerException.class)
    public void testHasErrorsNullList() throws Exception {
        final List<ValidationResult> validationResults = null;
        validationResult = new ValidationResult(validationResults);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testGetMessages() throws Exception {
        Map<String, String> errorMessages = validationResult.getErrorMessages();
        assertEquals(0, errorMessages.size());
    }


}
