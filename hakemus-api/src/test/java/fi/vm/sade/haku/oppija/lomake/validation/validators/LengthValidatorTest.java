package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LengthValidatorTest {

    private static final String FIELD_NAME = "a";
    private I18nText errorMessage;
    private static final Element element = new TextQuestion(FIELD_NAME, ElementUtil.createI18NAsIs(FIELD_NAME));

    @Before
    public void setUp() throws Exception {
        errorMessage = ElementUtil.createI18NAsIs("error");
    }

    @Test
    public void testInValid() throws Exception {
        LengthValidator lengthValidator = getLengthValidator(5);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(element, ImmutableMap.of(FIELD_NAME, "123456"), null, null, false));
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValid() throws Exception {
        LengthValidator lengthValidator = getLengthValidator(5);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(element, ImmutableMap.of(FIELD_NAME, "12345"), null, null, false));
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testNull() throws Exception {
        LengthValidator lengthValidator = getLengthValidator(5);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(element, new HashMap<String, String>(), null, null, false));
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testEmpty() throws Exception {
        LengthValidator lengthValidator = getLengthValidator(5);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(element, ImmutableMap.of(FIELD_NAME, ""), null, null, false));
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testEmptyAndEmptyLength() throws Exception {
        LengthValidator lengthValidator = getLengthValidator(0);
        ValidationResult validationResult = lengthValidator.validate(new ValidationInput(element, ImmutableMap.of(FIELD_NAME, ""), null, null, false));
        assertFalse(validationResult.hasErrors());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyAndNegativeLength() throws Exception {
        getLengthValidator(-1);
    }

    private LengthValidator getLengthValidator(int length) {
        return new LengthValidator(errorMessage, length);
    }
}
