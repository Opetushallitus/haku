package fi.vm.sade.oppija.haku.validation;

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


    @Test
    public void testNoValidation() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        formValidator = new FormValidator(new HashMap<String, Validator>());
        final HakemusState hakemusState = createHakemusState(values);
        formValidator.validate(hakemusState);
        assertFalse(hakemusState.hasErrors());
    }

    private HakemusState createHakemusState(Map<String, String> values) {
        return new HakemusState(createHakemus(values));
    }

    private Hakemus createHakemus(Map<String, String> values) {
        return new Hakemus(new HakemusId("test", "yhteishaku", "henkilotiedot", ""), values);
    }

    private HakemusState validate(Map<String, String> values, Map<String, Validator> validators) {
        createValidator(validators);
        final HakemusState hakemusState = createHakemusState(values);
        formValidator.validate(hakemusState);
        return hakemusState;
    }

    private FormValidator createValidator(Map<String, Validator> validators) {
        formValidator = new FormValidator(validators);
        return formValidator;
    }


    @Test
    public void testRequiredValueMissing() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        HakemusState hakemusState = validate(values, validators);
        assertTrue(hakemusState.hasErrors());
        assertEquals(requiredFieldValidator.getErrorMessage(), hakemusState.getErrorMessages().get(requiredFieldValidator.fieldName));
    }

    @Test
    public void testRequiredValue() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put(requiredFieldValidator.fieldName, "Urho");
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        HakemusState hakemusState = validate(values, validators);
        assertFalse(hakemusState.hasErrors());
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
        HakemusState hakemusState = validate(values, validators);
        assertFalse(hakemusState.hasErrors());
    }

    @Test
    public void testMultipleRequiredValueMissing() throws Exception {
        RequiredFieldValidator sukunimiFieldValidator = new RequiredFieldValidator("Sukunimi on pakollinen tieto", "sukunimi");
        Map<String, String> values = new HashMap<String, String>();
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        validators.put(sukunimiFieldValidator.fieldName, sukunimiFieldValidator);
        HakemusState hakemusState = validate(values, validators);
        assertTrue(hakemusState.hasErrors());
    }

    @Test
    public void testRequiredValueIsNull() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put(requiredFieldValidator.fieldName, null);
        Map<String, Validator> validators = new HashMap<String, Validator>();
        validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        HakemusState hakemusState = validate(values, validators);
        assertTrue(hakemusState.hasErrors());

    }

}
