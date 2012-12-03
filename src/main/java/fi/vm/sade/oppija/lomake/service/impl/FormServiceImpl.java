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
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.HakemusState;
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
            throw new ResourceNotFoundException("Model not found");
        }
        return model;
    }

    @Override
    public Form getActiveForm(String applicationPeriodId, String formId) {
        final ApplicationPeriod applicationPeriod = getApplicationPeriodById(applicationPeriodId);
        if (applicationPeriod == null) {
            throw new ResourceNotFoundException("not found");
        }
        if (!applicationPeriod.isActive()) {
            throw new ResourceNotFoundException("Not active");
        }
        return applicationPeriod.getFormById(formId);
    }

    @Override
    public Form getForm(String applicationPeriodId, String formId) {
        return getApplicationPeriodById(applicationPeriodId).getFormById(formId);
    }

    @Override
    public Phase getFirstCategory(String applicationPeriodId, String formId) {
        Phase firstPhase = getActiveForm(applicationPeriodId, formId).getFirstCategory();
        if (firstPhase == null) {
            throw new ResourceNotFoundException("First category not found");
        }
        return firstPhase;
    }

    @Override
    public Map<String, ApplicationPeriod> getApplicationPerioidMap() {
        FormModel model = getModel();
        return model.getApplicationPerioidMap();
    }

    public List<Validator> getVaiheValidators(final FormId formId, final String vaiheId) {
        final Phase phase = getActiveForm(formId.getApplicationPeriodId(), formId.getFormId()).getCategory(vaiheId);
        return phase.getValidators();
    }

    @Override
    public List<Validator> getVaiheValidators(HakemusState hakemusState) {
        final FormId formId = hakemusState.getHakemus().getFormId();
        if (!hakemusState.isFinalStage()) {
            return getVaiheValidators(formId, hakemusState.getVaiheId());
        } else {
            return getAllValidators(formId);
        }
    }

    public List<Validator> getAllValidators(final FormId formId) {
        final ArrayList<Validator> validators = new ArrayList<Validator>();
        final Form activeForm = getActiveForm(formId.getApplicationPeriodId(), formId.getFormId());
        for (Phase phase : activeForm.getCategories()) {
            validators.addAll(phase.getValidators());
        }
        return validators;
    }

    @Override
    public ApplicationPeriod getApplicationPeriodById(String applicationPeriodId) {
        return getModel().getApplicationPeriodById(applicationPeriodId);
    }

}
