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


import com.google.common.collect.Iterables;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.oppija.lomake.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormServiceImpl implements FormService {


    private final ApplicationSystemService applicationSystemService;

    @Autowired
    public FormServiceImpl(final ApplicationSystemService applicationSystemService) {
        this.applicationSystemService = applicationSystemService;
    }

    @Override
    public Form getActiveForm(String applicationSystemId) {
        if (applicationSystemId == null) {
            throw new ResourceNotFoundExceptionRuntime("Application system not found");
        }
        final ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(applicationSystemId);

        if (!applicationSystem.isActive()) {
            throw new ResourceNotFoundExceptionRuntime("Application period is not active");
        }
        Form form = applicationSystem.getForm();
        if (form == null) {
            throw new ResourceNotFoundExceptionRuntime("Form not found");
        }
        return form;
    }

    @Override
    public Form getForm(final String applicationSystemId) {
        final ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(applicationSystemId);

        Form form = applicationSystem.getForm();
        if (form == null) {
            throw new ResourceNotFoundExceptionRuntime("Form not found");
        }
        return form;
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
    public Element getLastPhase(final String applicationSystemId) {
        Form activeForm = getForm(applicationSystemId);
        Element lastPhase = Iterables.getLast(activeForm.getChildren(), null);
        if (lastPhase instanceof Phase) {
            return lastPhase;
        }
        throw new ResourceNotFoundExceptionRuntime("Last phase not found");
    }
}

