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

package fi.vm.sade.oppija.lomake.service.impl;


import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.lomake.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FormServiceImpl implements FormService {


    private final FormModelHolder holder;

    @Autowired
    public FormServiceImpl(final FormModelHolder holder) {
        this.holder = holder;
    }

    private FormModel getModel() {
        FormModel model = holder.getModel();
        if (model == null) {
            throw new ResourceNotFoundExceptionRuntime("Model not found");
        }
        return model;
    }

    @Override
    public Form getActiveForm(String applicationPeriodId, String formId) {
        final ApplicationPeriod applicationPeriod = getApplicationPeriodById(applicationPeriodId);
        if (applicationPeriod == null) {
            throw new ResourceNotFoundExceptionRuntime("not found");
        }
        if (!applicationPeriod.isActive()) {
            throw new ResourceNotFoundExceptionRuntime("Not active");
        }
        Form formById = applicationPeriod.getFormById(formId);
        return formById;
    }

    @Override
    public Form getForm(String applicationPeriodId, String formId) {
        return getApplicationPeriodById(applicationPeriodId).getFormById(formId);
    }

    @Override
    public Phase getFirstPhase(String applicationPeriodId, String formId) {
        Phase firstPhase = getActiveForm(applicationPeriodId, formId).getFirstPhase();
        if (firstPhase == null) {
            throw new ResourceNotFoundExceptionRuntime("First phase not found");
        }
        return firstPhase;
    }

    @Override
    public Phase getLastPhase(String applicationPeriodId, String formId) {
        Phase lastPhase = getActiveForm(applicationPeriodId, formId).getLastPhase();
        if (lastPhase == null) {
            throw new ResourceNotFoundExceptionRuntime("Last phase not found");
        }
        return lastPhase;
    }

    @Override
    public Map<String, ApplicationPeriod> getApplicationPerioidMap() {
        FormModel model = getModel();
        return model.getApplicationPerioidMap();
    }

    @Override
    public List<Validator> getVaiheValidators(ApplicationState applicationState) {
        final FormId formId = applicationState.getHakemus().getFormId();
        if (!applicationState.isFinalStage()) {
            return getPhaseValidators(formId, applicationState.getVaiheId());
        } else {
            return getAllValidators(formId);
        }
    }

    @Override
    public ApplicationPeriod getApplicationPeriodById(String applicationPeriodId) {
        ApplicationPeriod applicationPeriodById = getModel().getApplicationPeriodById(applicationPeriodId);
        if (applicationPeriodById == null) {
            throw new ResourceNotFoundExceptionRuntime("Application period " + applicationPeriodId + " not found");
        }
        return applicationPeriodById;
    }

    private List<Validator> getPhaseValidators(final FormId formId, final String phaseId) {
        final Phase phase = getPhaseById(formId, phaseId);
        return phase.getValidators();
    }

    private Phase getPhaseById(FormId formId, String phaseId) {
        Phase phase = getActiveForm(formId.getApplicationPeriodId(), formId.getFormId()).getPhase(phaseId);
        if (phase == null) {
            throw new ResourceNotFoundExceptionRuntime("Phase '" + phaseId + "' Not found");
        }
        return phase;
    }

    private List<Validator> getAllValidators(final FormId formId) {
        final ArrayList<Validator> validators = new ArrayList<Validator>();
        final Form activeForm = getActiveForm(formId.getApplicationPeriodId(), formId.getFormId());
        for (Phase phase : activeForm.getPhases()) {
            validators.addAll(phase.getValidators());
        }
        return validators;
    }

}
