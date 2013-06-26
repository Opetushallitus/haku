package fi.vm.sade.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import java.util.HashMap;

import static com.mongodb.util.MyAsserts.assertFalse;
import static com.mongodb.util.MyAsserts.assertTrue;

public class EqualsValidatorTest {

    public static final String FIELD_NAME = "fieldname";
    public static final String VALID_VALUE = "42";
    public static final I18nText TEXT = ElementUtil.createI18NAsIs("text");

    @Test
    public void testValidateNotFound() throws Exception {
        EqualsValidator equalsValidator = new EqualsValidator(FIELD_NAME, TEXT, VALID_VALUE);
        ValidationResult validate = equalsValidator.validate(new HashMap<String, String>());
        assertTrue(validate.hasErrors());
    }

    @Test
    public void testValidateNotEqual() throws Exception {
        EqualsValidator equalsValidator = new EqualsValidator(FIELD_NAME, TEXT, VALID_VALUE);
        ValidationResult validate = equalsValidator.validate(ImmutableMap.of(FIELD_NAME, VALID_VALUE + "1"));
        assertTrue(validate.hasErrors());
    }

    @Test
    public void testValidatePass() throws Exception {
        EqualsValidator equalsValidator = new EqualsValidator(FIELD_NAME, TEXT, VALID_VALUE);
        ValidationResult validate = equalsValidator.validate(ImmutableMap.of(FIELD_NAME, VALID_VALUE));
        assertFalse(validate.hasErrors());
    }
}
