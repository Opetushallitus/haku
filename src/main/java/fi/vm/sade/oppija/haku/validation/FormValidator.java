package fi.vm.sade.oppija.haku.validation;

import java.util.HashMap;
import java.util.Map;

public class FormValidator {

    public ValidationResult validate(final Map<String, String> values, final Map<String, Validator> validators) {
        Map<String, String> errors = new HashMap<String, String>();
        if (values == null || validators == null) {
            return new ValidationResult(errors);
        }
        Validator validator;
        String valueAndValidatorKey;
        for (Map.Entry<String, Validator> validatorEntry : validators.entrySet()) {
            validator = validatorEntry.getValue();
            valueAndValidatorKey = validatorEntry.getKey();
            if (!validator.validate(values)) {
                errors.put(valueAndValidatorKey, validator.getErrorMessage());
            }
        }
        return new ValidationResult(errors);
    }
}
