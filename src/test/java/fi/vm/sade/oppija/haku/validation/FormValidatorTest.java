package fi.vm.sade.oppija.haku.validation;

import fi.vm.sade.oppija.haku.validation.validators.RequiredFieldValidator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class FormValidatorTest {

    RequiredFieldValidator EtunimiRequiredFieldValidator;
    RequiredFieldValidator sukunimiRequiredFieldValidator;
    private Map<String, String> values;
    private List<Validator> validators;

    @Before
    public void setUp() throws Exception {
        values = new HashMap<String, String>();
        validators = new ArrayList<Validator>();
        EtunimiRequiredFieldValidator = new RequiredFieldValidator("etunimi", "Etunimi on pakollinen kenttä");
        sukunimiRequiredFieldValidator = new RequiredFieldValidator("sukunimi", "Sukunimi on pakollinen tieto");
    }

    @Test
    public void testNoValidation() throws Exception {
        ValidationResult validate = FormValidator.validate(validators, values);
        assertFalse(validate.hasErrors());
    }

    @Test
    public void testRequiredValueMissing() throws Exception {
        validators.add(EtunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testRequiredValue() throws Exception {
        values.put(EtunimiRequiredFieldValidator.fieldName, "Urho");
        validators.add(EtunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testMultipleRequiredValue() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put(EtunimiRequiredFieldValidator.fieldName, "Urho");
        values.put(sukunimiRequiredFieldValidator.fieldName, "Kekkonen");
        validators.add(EtunimiRequiredFieldValidator);
        validators.add(sukunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testMultipleRequiredValueMissing() throws Exception {
        validators.add(EtunimiRequiredFieldValidator);
        validators.add(sukunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertTrue(validationResult.hasErrors());
        assertEquals(2, validationResult.getErrorMessages().size());
    }

    @Test
    public void testRequiredValueIsNull() throws Exception {
        values.put(EtunimiRequiredFieldValidator.fieldName, null);
        validators.add(EtunimiRequiredFieldValidator);
        ValidationResult validationResult = FormValidator.validate(validators, values);
        assertTrue(validationResult.hasErrors());

    }

}
