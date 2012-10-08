package fi.vm.sade.oppija.haku.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FormValidator {

    public static ValidationResult validate(final List<Validator> validators, final Map<String, String> values) {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        for (Validator validator : validators) {
            validationResults.add(validator.validate(values));
        }
        return new ValidationResult(validationResults);
    }


}
