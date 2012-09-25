package fi.vm.sade.oppija.haku.validation;

import fi.vm.sade.oppija.haku.validation.validators.RequiredFieldValidator;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class FormValidatorTest {

    FormValidator formValidator;
    RequiredFieldValidator requiredFieldValidator;

    public FormValidatorTest() {
        this.formValidator = new FormValidator();
        requiredFieldValidator = new RequiredFieldValidator("etunimi", "Etunimi on pakollinen kenttä");
    }

    @Test
    public void testNoValidation() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        Map<String, Validator> validators = new HashMap<String, Validator>();
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testNullValues() throws Exception {
        Map<String, String> values = null;
        Map<String, Validator> validators = new HashMap<String, Validator>();
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testNullValidators() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        Map<String, Validator> validators = null;
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testNullValuesAndValidators() throws Exception {
        Map<String, String> values = null;
        Map<String, Validator> validators = null;
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testRequiredValueMissing() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertTrue(validationResult.hasErrors());
        assertEquals(requiredFieldValidator.getErrorMessage(), validationResult.getErrorMessages().get(requiredFieldValidator.fieldName));
    }

    @Test
    public void testRequiredValue() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put(requiredFieldValidator.fieldName, "Urho");
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testMultipleRequiredValue() throws Exception {
        RequiredFieldValidator sukunimiFieldValidator = new RequiredFieldValidator("Sukunimi on pakollinen tieto", "sukunimi");
        Map<String, String> values = new HashMap<String, String>();
        values.put(requiredFieldValidator.fieldName, "Urho");
        values.put(sukunimiFieldValidator.fieldName, "Kekkonen");
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        validators.put(sukunimiFieldValidator.fieldName, sukunimiFieldValidator);
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testMultipleRequiredValueMissing() throws Exception {
        RequiredFieldValidator sukunimiFieldValidator = new RequiredFieldValidator("Sukunimi on pakollinen tieto", "sukunimi");
        Map<String, String> values = new HashMap<String, String>();
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        validators.put(sukunimiFieldValidator.fieldName, sukunimiFieldValidator);
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testRequiredValueIsNull() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put(requiredFieldValidator.fieldName, null);
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        ValidationResult validationResult = formValidator.validate(values, validators);
        assertTrue(validationResult.hasErrors());

    }

}
