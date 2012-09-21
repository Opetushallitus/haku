package fi.vm.sade.oppija.haku.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FormValidator {

    public Map<String, String> validate(final Map<String, String> values, final Map<String, Validator> validators) {
        Map<String, String> errors = new HashMap<String, String>();
        if (values == null || validators == null) {
            return errors;
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
        return Collections.unmodifiableMap(errors);
    }
}
