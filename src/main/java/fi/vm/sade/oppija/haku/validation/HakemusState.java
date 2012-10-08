package fi.vm.sade.oppija.haku.validation;


import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.elements.Form;

import java.util.HashMap;
import java.util.Map;

public class HakemusState {
    private static final String HAKEMUS_KEY = "hakemus";
    private final Map<String, String> errors;
    private final Map<String, Object> modelObjects = new HashMap<String, Object>();
    private boolean mustValidate = true;

    public HakemusState(Hakemus hakemus) {
        this.errors = new HashMap<String, String>();
        modelObjects.put(HAKEMUS_KEY, hakemus);
        modelObjects.put("categoryData", hakemus.getValues());
        modelObjects.put("errorMessages", errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
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

    public void addError(Map<String, String> errorMessages) {
        this.errors.putAll(errorMessages);
    }

    public void addError(final String key, final String message) {
        this.errors.put(key, message);
    }

    public void addModelObject(String key, Object data) {
        this.modelObjects.put(key, data);
    }

    public Map<String, Object> getModelObjects() {
        return modelObjects;
    }

    public Hakemus getHakemus() {
        return (Hakemus) modelObjects.get(HAKEMUS_KEY);
    }

    public boolean mustValidate() {
        return mustValidate;
    }
}
