package fi.vm.sade.oppija.haku.validation;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationResult {
    private final Map<String, String> errors;

    public ValidationResult(final Map<String, String> errors) {
        this.errors = Collections.unmodifiableMap(errors);
    }

    public ValidationResult() {
        this.errors = Collections.unmodifiableMap(Collections.<String, String>emptyMap());
    }

    public ValidationResult(final String key, final String error) {
        HashMap<String, String> errorMessages = new HashMap<String, String>();
        errorMessages.put(key, error);
        this.errors = Collections.unmodifiableMap(errorMessages);
    }

    public ValidationResult(final List<ValidationResult> validationResults) {
        HashMap<String, String> errorMessages = new HashMap<String, String>();
        for (ValidationResult validationResult : validationResults) {
            if (validationResult.hasErrors()) {
                errorMessages.putAll(validationResult.getErrorMessages());
            }
        }
        this.errors = Collections.unmodifiableMap(errorMessages);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Map<String, String> getErrorMessages() {
        return errors;
    }

}
