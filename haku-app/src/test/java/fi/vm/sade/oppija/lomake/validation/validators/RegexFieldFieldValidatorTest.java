package fi.vm.sade.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.ISO88591_NAME_REGEX;
import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createRegexValidator;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class RegexFieldFieldValidatorTest {

    private static final String FIELD_NAME = "name";

    private final Validator regexFieldFieldValidator = createRegexValidator(FIELD_NAME, ISO88591_NAME_REGEX, true);

    private final List<String> listOfValidNames = ImmutableList.of(
            "etunimi sukunimi",
            "etunimi, sukunimi",
            "etunimi-sukunimi",
            " etunimi sukunimi",
            " etunimi sukunimi ",
            "etunimi sukunimi ",
            "etunimi sukunimi           ",
            "                etunimi sukunimi           ",
            "nimi ",
            " nimi ",
            "   nimi ",
            "\u00C0",
            "\u00D6");
    private final List<String> listOfInvalidNames = ImmutableList.of(
            ",etunimi sukunimi",
            "-etunimi sukunimi",
            "nimi-",
            "nimi.",
            " ",
            "                          ",
            "\"",
            ".",
            "_",
            "*",
            "\u00D7",
            "\u00F7",
            "\u20ac");

    @Test
    public void validateValid() throws Exception {
        Map<String, String> values = ImmutableMap.of(FIELD_NAME, "test");
        Validator test = createRegexValidator(FIELD_NAME, "test");
        ValidationResult validationResult = test.validate(new ValidationInput(null, values, null, null));
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void validateInvalid() throws Exception {
        Map<String, String> values = ImmutableMap.of(FIELD_NAME, "test2");
        Validator test = createRegexValidator(FIELD_NAME, "test");
        ValidationResult validationResult = test.validate(new ValidationInput(null, values, null, null));
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testValidNames() throws Exception {
        for (String validName : listOfValidNames) {
            validateValidName(validName);
        }
    }

    @Test
    public void testInvalidNames() throws Exception {
        for (String invalidName : listOfInvalidNames) {
            validateInvalidName(invalidName);
        }
    }

    private void validateValidName(String name) {
        assertFalse("Valid name '" + name + "'", validate(name).hasErrors());
    }

    private void validateInvalidName(final String name) {
        assertTrue("Invalid name '" + name + "'", validate(name).hasErrors());
    }

    private ValidationResult validate(String name) {
        Map<String, String> values = ImmutableMap.of(FIELD_NAME, name);
        return regexFieldFieldValidator.validate(new ValidationInput(null, values, null, null));
    }
}
