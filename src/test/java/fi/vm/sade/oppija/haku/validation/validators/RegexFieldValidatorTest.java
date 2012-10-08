package fi.vm.sade.oppija.haku.validation.validators;

import fi.vm.sade.oppija.haku.validation.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegexFieldValidatorTest {

    public static final String ERROR_MESSAGE = "kenttä on virheellinen";
    public static final String FIELD_NAME = "kenttä";
    public static final String PATTERN = "[A-Za-z]{3}";
    public static final String TEST_VALUE = "ABC";

    private Map<String, String> values;

    @Before
    public void setUp() throws Exception {
        values = new HashMap<String, String>();
    }

    @Test
    public void testValidateValid() throws Exception {
        values.put(FIELD_NAME, TEST_VALUE);
        RegexFieldValidator validator = createValidator(TEST_VALUE);
        ValidationResult validationResult = validator.validate(values);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testValidateInvalid() throws Exception {
        values.put(FIELD_NAME, TEST_VALUE + "Ä");
        RegexFieldValidator validator = createValidator(TEST_VALUE);
        ValidationResult validationResult = validator.validate(values);
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidatePattern() throws Exception {
        values.put(FIELD_NAME, TEST_VALUE);
        RegexFieldValidator validator = createValidator(PATTERN);
        ValidationResult validationResult = validator.validate(values);
        assertFalse(validationResult.hasErrors());
    }

    @Test(expected = NullPointerException.class)
    public void testNullPattern() throws Exception {
        values.put(FIELD_NAME, TEST_VALUE);
        RegexFieldValidator validator = createValidator(null);
    }


    private RegexFieldValidator createValidator(final String pattern) {
        return new RegexFieldValidator(FIELD_NAME, ERROR_MESSAGE, pattern);

    }


}
