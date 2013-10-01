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

package fi.vm.sade.oppija.lomake.domain;


import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.I18nText;

import java.util.HashMap;
import java.util.Map;

public class ApplicationState {

    private static final String APPLICATION_KEY = "application";
    public static final String VALMIS = "valmis";
    private final Map<String, I18nText> errors = new HashMap<String, I18nText>();
    private final Map<String, Object> modelObjects = new HashMap<String, Object>();
    private final String phaseId;

    public ApplicationState(final Application application, final String phaseId) {
        modelObjects.put(APPLICATION_KEY, application);
        modelObjects.put("categoryData", application.getVastauksetMerged());
        modelObjects.put("errorMessages", errors);
        modelObjects.put("applicationPhaseId", application.getPhaseId());
        this.phaseId = phaseId;
    }

    public void addModelObject(final String key, final Object object) {
        this.modelObjects.put(key, object);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public void addError(Map<String, I18nText> errorMessages) {
        this.errors.putAll(errorMessages);
    }

    public Map<String, I18nText> getErrors() {
        return this.errors;
    }

    public Map<String, Object> getModelObjects() {
        return modelObjects;
    }

    public Application getApplication() {
        return (Application) modelObjects.get(APPLICATION_KEY);
    }

    public String getPhaseId() {
        return phaseId;
    }

    public boolean isFinalStage() {
        return VALMIS.equals(phaseId);
    }

    public void setAnswersMerged(Map<String, String> answersMerged) {
        modelObjects.put("categoryData", answersMerged);
    }
}
