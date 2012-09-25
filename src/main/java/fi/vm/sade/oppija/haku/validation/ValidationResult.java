package fi.vm.sade.oppija.haku.validation;


import java.util.Collections;
import java.util.Map;

public class ValidationResult {
    private final Map<String, String> errors;

    public ValidationResult(Map<String, String> errors) {
        this.errors = Collections.unmodifiableMap(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Map<String, String> getErrorMessages() {
        return errors;
    }

    public int errorCount() {
        return errors.size();
    }
}
