package fi.vm.sade.oppija.haku.validation;


import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.elements.Form;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {
    private final Map<String, String> errors;
    private final Map<String, Object> modelObjects = new HashMap<String, Object>();

    public ValidationResult(Hakemus hakemus) {
        this.errors = new HashMap<String, String>();
        modelObjects.put("categoryData", hakemus);
        modelObjects.put("errorMessages", errors);
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


    public Form getActiveForm() {
        return null;
    }

    public void addError(String key, String message) {
        this.errors.put(key, message);
    }

    public void addModelObject(String key, Object data) {
        this.modelObjects.put(key, data);
    }

    public Map<String, Object> getModelObjects() {
        return modelObjects;
    }

}
