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

package fi.vm.sade.haku.oppija.lomake.dao.impl;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.exception.ApplicationSystemNotFound;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;

public class FormServiceMockImpl implements FormService {

    private ApplicationSystemService applicationSystemService;

    @Autowired
    public FormServiceMockImpl(final ApplicationSystemService applicationSystemService) {
        this.applicationSystemService = applicationSystemService;
    }

    @Override
    public Form getActiveForm(final String applicationSystemId) {
        ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(applicationSystemId);
        if (applicationSystem.isActive()) {
            return applicationSystem.getForm();
        }
        throw new ApplicationSystemNotFound(applicationSystemId);
    }

    @Override
    public Form getForm(final String applicationSystemId) {
        ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(applicationSystemId);
        return applicationSystem.getForm();
    }

}
