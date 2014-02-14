package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LengthValidatorTest {

    private static final String FIELD_ID = "a";
    private I18nText errorMessage;

    @Before
    public void setUp() throws Exception {
        errorMessage = ElementUtil.createI18NAsIs("error");
    }

    @Test
    public void testInValid() throws Exception {

        LengthValidator lengthValidator = getLengthValidator(5);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(null, ImmutableMap.of(FIELD_ID, "123456"), null, null));
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValid() throws Exception {
        LengthValidator lengthValidator = getLengthValidator(5);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(null, ImmutableMap.of(FIELD_ID, "12345"), null, null));
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testNull() throws Exception {
        LengthValidator lengthValidator = getLengthValidator(5);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(null, new HashMap<String, String>(), null, null));
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testEmpty() throws Exception {
        LengthValidator lengthValidator = getLengthValidator(5);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(null, ImmutableMap.of(FIELD_ID, ""), null, null));
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testEmptyAndEmptyLength() throws Exception {
        LengthValidator lengthValidator = getLengthValidator(0);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(null, ImmutableMap.of(FIELD_ID, ""), null, null));
        assertFalse(validationResult.hasErrors());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyAndNegativeLength() throws Exception {
        getLengthValidator(-1);
    }

    private LengthValidator getLengthValidator(int length) {
        return new LengthValidator(FIELD_ID, errorMessage, length);
    }
}
