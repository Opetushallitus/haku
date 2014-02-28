package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class DateOfBirthValidatorTest {
    public static final String FIELD_NAME = "dateOfBirth";
    public static final String VALID_DATE = "02.11.1998";
    public static final String FUTURE_DATE = "01.03.2051";
    public static final String INVALID_DATE = "13.13.2051";
    public static final I18nText TEXT = ElementUtil.createI18NAsIs("text");

    @Test
    public void testValidDate() throws Exception {
        DateOfBirthValidator dobValidator = new DateOfBirthValidator(FIELD_NAME, TEXT, "form_errors_yhteishaku_kevat");
        ValidationResult validate = dobValidator.validate(new ValidationInput(null, ImmutableMap.of(FIELD_NAME, VALID_DATE),
          null, null));
        assertFalse(validate.hasErrors());

    }
    @Test
    public void testFutureDate() throws Exception {
        DateOfBirthValidator dobValidator = new DateOfBirthValidator(FIELD_NAME, TEXT, "form_errors_yhteishaku_kevat");
        ValidationResult validationResult = dobValidator.validate(new ValidationInput(null, ImmutableMap.of(FIELD_NAME, FUTURE_DATE),
          null, null));
        assertTrue(validationResult.hasErrors());
    }
    @Test
    public void testInvalidDate() throws Exception {
        DateOfBirthValidator dobValidator = new DateOfBirthValidator(FIELD_NAME, TEXT, "form_errors_yhteishaku_kevat");
        ValidationResult validate = dobValidator.validate(new ValidationInput(null, ImmutableMap.of(FIELD_NAME, INVALID_DATE),
          null, null));
        assertTrue(validate.hasErrors());
    }
}
