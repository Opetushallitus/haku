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

package fi.vm.sade.oppija.haku.service.impl;


import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.service.FormModelHolder;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Vaihe getFirstCategory(String applicationPeriodId, String formId) {
        Vaihe firstVaihe = getActiveForm(applicationPeriodId, formId).getFirstCategory();
        if (firstVaihe == null) {
            throw new ResourceNotFoundException("First category not found");
        }
        return firstVaihe;
    }

    @Override
    public Map<String, ApplicationPeriod> getApplicationPerioidMap() {
        FormModel model = getModel();
        return model.getApplicationPerioidMap();
    }

    @Override
    public List<Validator> getCategoryValidators(final HakemusId hakemusId, final String vaiheId) {
        final Vaihe vaihe = getActiveForm(hakemusId.getApplicationPeriodId(), hakemusId.getFormId()).getCategory(vaiheId);
        return vaihe.getValidators();
    }

    @Override
    public ApplicationPeriod getApplicationPeriodById(String applicationPeriodId) {
        return getModel().getApplicationPeriodById(applicationPeriodId);
    }

}
