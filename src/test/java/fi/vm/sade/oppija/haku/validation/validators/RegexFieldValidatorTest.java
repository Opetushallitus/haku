package fi.vm.sade.oppija.haku.validation.validators;

import fi.vm.sade.oppija.haku.validation.FormValidator;
import fi.vm.sade.oppija.haku.validation.ValidationResult;
import fi.vm.sade.oppija.haku.validation.Validator;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class RegexFieldValidatorTest extends TestCase {

    public static final String ERROR_MESSAGE = "Virhe";
    public static final String FIELD_NAME = "kenttä";
    public static final String PATTERN = "[A-Za-z]{3}";
    public static final String VALID_VALUE = "ABC";
    public static final String INVALID_VALUE = "AB";

    final Map<String, String> values = new HashMap<String, String>();
    final Map<String, Validator> validators = new HashMap<String, Validator>();
    private FormValidator formValidator = new FormValidator();

    @Test
    public void testValidateValid() throws Exception {
        ValidationResult validationResult = createValidValidationResult();
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testValidateInValid() throws Exception {
        createValidator(PATTERN);
        values.put(FIELD_NAME, INVALID_VALUE);
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertTrue(validationResult.hasErrors());
        assertEquals(validationResult.getErrorMessages().get(FIELD_NAME), ERROR_MESSAGE);
    }

    @Test
    public void testErrorMessage() throws Exception {
        createValidator(PATTERN);
        values.put(FIELD_NAME, INVALID_VALUE);
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertEquals(ERROR_MESSAGE, validationResult.getErrorMessages().get(FIELD_NAME));
    }

    @Test
    public void testErrorMessageNotExists() throws Exception {
        ValidationResult validationResult = createValidValidationResult();
        assertEquals(null, validationResult.getErrorMessages().get(FIELD_NAME + 1));
    }

    private void createValidator(final String pattern) {
        RegexFieldValidator regexFieldValidator = new RegexFieldValidator(ERROR_MESSAGE, FIELD_NAME, pattern);
        validators.put(FIELD_NAME, regexFieldValidator);
    }

    private ValidationResult createValidValidationResult() {
        createValidator(PATTERN);
        values.put(FIELD_NAME, VALID_VALUE);
        return formValidator.validate(values, validators);
    }
}
