package fi.vm.sade.oppija.haku.validation.validators;

import fi.vm.sade.oppija.haku.validation.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: ville
 * Date: 10/5/12
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequiredFieldValidatorTest {
    public static final String ERROR_MESSAGE = "kenttä on virheellinen";
    public static final String FIELD_NAME = "kenttä";
    private Map<String, String> values;

    @Before
    public void setUp() throws Exception {
        values = new HashMap<String, String>();
    }

    @Test
    public void testValidateValid() throws Exception {
        values.put(FIELD_NAME, "1");
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator(FIELD_NAME, ERROR_MESSAGE);
        ValidationResult validationResult = requiredFieldValidator.validate(values);
        assertFalse(validationResult.hasErrors());
    }
    @Test
    public void testValidateInvalid() throws Exception {
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator(FIELD_NAME, ERROR_MESSAGE);
        ValidationResult validationResult = requiredFieldValidator.validate(values);
        assertTrue(validationResult.hasErrors());
    }
    
}
