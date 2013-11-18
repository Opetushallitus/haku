package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
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
        ValidationResult validate = equalsValidator.validate(new ValidationInput(null, new HashMap<String, String>(),
                null, null));
        assertTrue(validate.hasErrors());
    }

    @Test
    public void testValidateNotEqual() throws Exception {
        EqualsValidator equalsValidator = new EqualsValidator(FIELD_NAME, TEXT, VALID_VALUE);
        ValidationResult validate = equalsValidator.validate(new ValidationInput(null, ImmutableMap.of(FIELD_NAME, VALID_VALUE + "1"),
                null, null));
        assertTrue(validate.hasErrors());
    }

    @Test
    public void testValidatePass() throws Exception {
        EqualsValidator equalsValidator = new EqualsValidator(FIELD_NAME, TEXT, VALID_VALUE);
        ValidationResult validate = equalsValidator.validate(new ValidationInput(null, ImmutableMap.of(FIELD_NAME, VALID_VALUE),
                null, null));
        assertFalse(validate.hasErrors());
    }
}
