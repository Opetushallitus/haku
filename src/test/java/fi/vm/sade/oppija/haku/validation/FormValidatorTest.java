package fi.vm.sade.oppija.haku.validation;

import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.validation.validators.RequiredFieldValidator;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class FormValidatorTest {

    FormValidator formValidator;
    RequiredFieldValidator requiredFieldValidator;

    public FormValidatorTest() {
        requiredFieldValidator = new RequiredFieldValidator("etunimi", "Etunimi on pakollinen kenttä");
    }

    private class MockService extends FormModelDummyMemoryDaoImpl {
        Map<String, Validator> validators = new HashMap<String, Validator>();

        public MockService() {
            super();
        }

        @Override
        public Map<String, Validator> getCategoryValidators(HakemusId hakemusId) {
            return validators;
        }

        public void setValidators(Map<String, Validator> validators) {
            this.validators = validators;
        }
    }

    @Test
    public void testNoValidation() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        formValidator = new FormValidator(new MockService());
        ValidationResult validationResult = formValidator.validate(createHakemus(values));
        assertFalse(validationResult.hasErrors());
    }

    private Hakemus createHakemus(Map<String, String> values) {
        return new Hakemus(new HakemusId("test", "yhteishaku", "henkilotiedot", ""), values);
    }

    private ValidationResult validate(Map<String, String> values, Map<String, Validator> validators) {
        createValidator(validators);
        return formValidator.validate(createHakemus(values));
    }

    private FormValidator createValidator(Map<String, Validator> validators) {
        final MockService formService = new MockService();
        formService.setValidators(validators);
        formValidator = new FormValidator(formService);
        return formValidator;
    }


    @Test
    public void testRequiredValueMissing() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        ValidationResult validationResult = validate(values, validators);
        assertTrue(validationResult.hasErrors());
        assertEquals(requiredFieldValidator.getErrorMessage(), validationResult.getErrorMessages().get(requiredFieldValidator.fieldName));
    }

    @Test
    public void testRequiredValue() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put(requiredFieldValidator.fieldName, "Urho");
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        ValidationResult validationResult = validate(values, validators);
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
        ValidationResult validationResult = validate(values, validators);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testMultipleRequiredValueMissing() throws Exception {
        RequiredFieldValidator sukunimiFieldValidator = new RequiredFieldValidator("Sukunimi on pakollinen tieto", "sukunimi");
        Map<String, String> values = new HashMap<String, String>();
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        validators.put(sukunimiFieldValidator.fieldName, sukunimiFieldValidator);
        ValidationResult validationResult = validate(values, validators);
        assertTrue(validationResult.hasErrors());
    }

    @Test
    public void testRequiredValueIsNull() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put(requiredFieldValidator.fieldName, null);
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        ValidationResult validationResult = validate(values, validators);
        assertTrue(validationResult.hasErrors());

    }

}
