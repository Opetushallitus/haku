package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
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
    public static final String TEXT_KEY = "text";
    public static final Element question = new TextQuestion(FIELD_NAME, ElementUtil.createI18NAsIs(TEXT_KEY));

    @Test
    public void testValidateNotFound() throws Exception {
        EqualsValidator equalsValidator = new EqualsValidator(TEXT_KEY, VALID_VALUE);
        ValidationResult validate = equalsValidator.validate(new ValidationInput(question, new HashMap<String, String>(),
                null, null, ValidationInput.ValidationContext.officer_modify));
        assertTrue(validate.hasErrors());
    }

    @Test
    public void testValidateNotEqual() throws Exception {
        EqualsValidator equalsValidator = new EqualsValidator(TEXT_KEY, VALID_VALUE);
        ValidationResult validate = equalsValidator.validate(new ValidationInput(question, ImmutableMap.of(FIELD_NAME, VALID_VALUE + "1"),
                null, null, ValidationInput.ValidationContext.officer_modify));
        assertTrue(validate.hasErrors());
    }

    @Test
    public void testValidatePass() throws Exception {
        EqualsValidator equalsValidator = new EqualsValidator(TEXT_KEY, VALID_VALUE);
        ValidationResult validate = equalsValidator.validate(new ValidationInput(question, ImmutableMap.of(FIELD_NAME, VALID_VALUE),
                null, null, ValidationInput.ValidationContext.officer_modify));
        assertFalse(validate.hasErrors());
    }
}
