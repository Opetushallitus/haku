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

package fi.vm.sade.oppija.lomake.dao.impl;

import com.google.common.collect.Maps;
import fi.vm.sade.oppija.common.koodisto.impl.KoodistoServiceMockImpl;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.Yhteishaku2013;

import java.util.Map;

public class FormServiceMockImpl implements FormService {

    private FormModel formModel;

    public FormServiceMockImpl(final String asid, final String aoid) {
        Yhteishaku2013 yhteishaku2013 = new Yhteishaku2013(new KoodistoServiceMockImpl(), asid, aoid);
        FormModelHolder formModelHolder = new FormModelHolder(yhteishaku2013);
        this.formModel = formModelHolder.getModel();
        for (ApplicationPeriod ap : formModel.getApplicationPerioidMap().values()) {
            Map<String, String> translations = Maps.newHashMap();
            translations.put("fi", ap.getId());
            translations.put("sv", ap.getId());
            translations.put("en", ap.getId());
            ap.setName(new I18nText(translations));
        }
    }

    @Override
    public Form getActiveForm(final String applicationPeriodId, final String formId) {
        try {
            return formModel.getApplicationPeriodById(applicationPeriodId).getFormById(formId);
        } catch (Exception e) {
            throw new ResourceNotFoundExceptionRuntime("Not found", e);
        }
    }

    @Override
    public Element getFirstPhase(final String applicationPeriodId, final String formId) {
        try {
            return this.getActiveForm(applicationPeriodId, formId).getFirstChild();
        } catch (Exception e) {
            throw new ResourceNotFoundExceptionRuntime("Not found");
        }
    }

    @Override
    public Element getLastPhase(final String applicationPeriodId, final String formId) {
        try {
            return this.getActiveForm(applicationPeriodId, formId).getLastPhase();
        } catch (Exception e) {
            throw new ResourceNotFoundExceptionRuntime("Not found");
        }
    }

    @Override
    public Map<String, ApplicationPeriod> getApplicationPerioidMap() {
        return this.formModel.getApplicationPerioidMap();
    }

    @Override
    public ApplicationPeriod getApplicationPeriodById(final String applicationPeriodId) {
        return this.formModel.getApplicationPeriodById(applicationPeriodId);
    }

    @Override
    public Form getForm(final String applicationPeriodId, final String formId) {
        ApplicationPeriod applicationPeriod = getApplicationPeriodById(applicationPeriodId);
        return applicationPeriod.getFormById(formId);
    }

    @Override
    public Form getActiveForm(final FormId formId) {
        return this.getActiveForm(formId.getApplicationPeriodId(), formId.getFormId());
    }

    @Override
    public Form getForm(final FormId formId) {
        return this.getForm(formId.getApplicationPeriodId(), formId.getFormId());
    }

    public FormModel getModel() {
        return this.formModel;
    }

}
