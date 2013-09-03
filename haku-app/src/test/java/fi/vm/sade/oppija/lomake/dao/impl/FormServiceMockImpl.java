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

import com.google.common.collect.Iterables;
import fi.vm.sade.oppija.common.koodisto.impl.KoodistoServiceMockImpl;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.FormGeneratorMock;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.Yhteishaku2013;

import java.util.Map;

public class FormServiceMockImpl implements FormService {

    private FormModel formModel;

    public FormServiceMockImpl(final String asid) {
        FormGeneratorMock formGeneratorMock = new FormGeneratorMock(new KoodistoServiceMockImpl(), asid);
        FormModelHolder formModelHolder = new FormModelHolder(formGeneratorMock);
        formModelHolder.generateAndReplace();
        this.formModel = formModelHolder.getModel();
    }

    @Override
    public Form getActiveForm(final String applicationSystemId) {
        try {
            return formModel.getApplicationSystemById(applicationSystemId).getForm();
        } catch (Exception e) {
            throw new ResourceNotFoundExceptionRuntime("Not found", e);
        }
    }

    @Override
    public Element getFirstPhase(final String applicationSystemId) {
        Form activeForm = getActiveForm(applicationSystemId);
        Element firstPhase = Iterables.getFirst(activeForm.getChildren(), null);
        if (firstPhase instanceof Phase) {
            return firstPhase;
        }
        throw new ResourceNotFoundExceptionRuntime("Last phase not found");
    }

    @Override
    public Element getLastPhase(String applicationSystemId) {
        Form activeForm = getActiveForm(applicationSystemId);
        Element lastPhase = Iterables.getLast(activeForm.getChildren(), null);
        if (lastPhase instanceof Phase) {
            return lastPhase;
        }
        throw new ResourceNotFoundExceptionRuntime("Last phase not found");
    }

    @Override
    public Map<String, ApplicationSystem> getApplicationPerioidMap() {
        return this.formModel.getApplicationPerioidMap();
    }

    @Override
    public ApplicationSystem getApplicationSystem(final String applicationSystemId) {
        return this.formModel.getApplicationSystemById(applicationSystemId);
    }

    @Override
    public Form getForm(final String applicationSystemId) {
        return getApplicationSystem(applicationSystemId).getForm();
    }

    public FormModel getModel() {
        return this.formModel;
    }

}
