/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.haku.validation;


import fi.vm.sade.oppija.haku.domain.Hakemus;

import java.util.HashMap;
import java.util.Map;

public class HakemusState {


    protected static final String HAKEMUS_KEY = "hakemus";

    protected final Map<String, String> errors;
    protected final Map<String, Object> modelObjects = new HashMap<String, Object>();
    protected boolean mustValidate = true;
    protected boolean navigateNext = false;
    protected boolean navigatePrev = false;
    protected String vaiheId;

    public HakemusState(Hakemus hakemus, String vaiheId) {
        this.errors = new HashMap<String, String>();
        modelObjects.put(HAKEMUS_KEY, hakemus);
        modelObjects.put("categoryData", hakemus.getVastaukset());
        modelObjects.put("errorMessages", errors);
        this.vaiheId = vaiheId;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Map<String, String> getErrorMessages() {
        return errors;
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

    public void skipValidation() {
        mustValidate = false;
    }

    public void toggleNavigateNext() {
        this.navigateNext = true;
    }

    public boolean isNavigateNext() {
        return navigateNext;
    }

    public void toggleNavigatePrev() {
        this.navigatePrev = true;
    }

    public boolean isNavigatePrev() {
        return navigatePrev;
    }

    public String getVaiheId() {
        return vaiheId;
    }

    public void setVaiheId(String vaiheId) {
        this.vaiheId = vaiheId;
    }

    public boolean isFinalStage() {
        return false;
    }
}
