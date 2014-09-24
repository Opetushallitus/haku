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

package fi.vm.sade.haku.oppija.lomake.domain;

import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;

public class ApplicationState {

    private final Application application;
    private final String phaseId;
    private final Map<String, String> answers = new HashMap<String, String>();
    private final Map<String, I18nText> errors = new HashMap<String, I18nText>();

    public ApplicationState(final Application application, final String phaseId) {
        this.application = application;
        this.phaseId = phaseId;
    }

    public ApplicationState(final Application application, final String phaseId, final Map<String, String> answers) {
        this(application, phaseId);
        this.answers.putAll(answers);
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
        Map<String, Object> modelObjects = new HashMap<String, Object>();
        modelObjects.put(ModelResponse.APPLICATION, application);
        if (answers.isEmpty()) {
            modelObjects.put(ModelResponse.ANSWERS, application.getVastauksetMerged());
        } else {
            modelObjects.put(ModelResponse.ANSWERS, this.answers);
        }
        modelObjects.put(ModelResponse.ERROR_MESSAGES, errors);
        modelObjects.put(ModelResponse.APPLICATION_PHASE_ID, application.getPhaseId());
        return modelObjects;
    }

    public Application getApplication() {
        return this.application;
    }

    public String getPhaseId() {
        return phaseId;
    }
}
