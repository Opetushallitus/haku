package fi.vm.sade.oppija.haku.validation;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ValidatorTest {

    public static final String FIELD_NAME = "field_name";
    public static final String ERROR_MESSAGE = "error_message";

    @Test
    public void testFieldNameConstructor() throws Exception {
        Validator validator = createValidator(FIELD_NAME, ERROR_MESSAGE);
        assertEquals(FIELD_NAME, validator.fieldName);
    }
    
    @Test
    public void testErrorMessageConstructor() throws Exception {
        Validator validator = createValidator(FIELD_NAME, ERROR_MESSAGE);
        assertEquals(ERROR_MESSAGE, validator.errorMessage);
    }

    @Test
    public void testValidConstructor() throws Exception {
        createValidator(FIELD_NAME, ERROR_MESSAGE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullField() throws Exception {
        createValidator(null, "");
    }

    @Test(expected = NullPointerException.class)
    public void testNullErrorMessage() throws Exception {
        createValidator("", null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullNUll() throws Exception {
        createValidator(null, null);
    }

    private Validator createValidator(String fieldName, String errorMessage) {
        return new Validator(fieldName, errorMessage) {
            @Override
            public ValidationResult validate(Map<String, String> values) {
                throw new NotImplementedException();
            }
        };
    }
}
