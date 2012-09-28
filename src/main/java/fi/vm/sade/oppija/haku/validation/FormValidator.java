package fi.vm.sade.oppija.haku.validation;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class FormValidator {

    private final Map<String, Validator> validators;

    @Autowired
    public FormValidator(Map<String, Validator> validators) {
        this.validators = validators;
    }

    public void validate(HakemusState hakemusState) {
        for (Map.Entry<String, Validator> validatorEntry : validators.entrySet()) {
            Validator validator = validatorEntry.getValue();
            String valueAndValidatorKey = validatorEntry.getKey();
            if (!validator.validate(hakemusState.getHakemus().getValues())) {
                hakemusState.addError(valueAndValidatorKey, validator.getErrorMessage());
            }
        }
    }


}
