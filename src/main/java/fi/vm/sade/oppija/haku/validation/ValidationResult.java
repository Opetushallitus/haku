package fi.vm.sade.oppija.haku.validation;


import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class ValidationResult implements Serializable {
    private final Map<String, String> errors;
    private transient Category category;

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

    public Category getCategory() {
        return category;
    }

    public Form getActiveForm() {
        return null;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isValid() {
        return hasErrors();
    }
}
