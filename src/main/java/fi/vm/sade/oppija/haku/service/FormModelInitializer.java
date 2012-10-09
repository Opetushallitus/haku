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

package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Form;

import java.util.Map;
import java.util.Set;

public class FormModelInitializer {
    private final FormModel model;

    public FormModelInitializer(FormModel model) {
        this.model = model;
    }

    public void initModel() {
        //init forms
        for (Map.Entry<String, ApplicationPeriod> stringApplicationPeriodEntry : model.getApplicationPerioidMap().entrySet()) {
            final Set<Map.Entry<String, Form>> entries = stringApplicationPeriodEntry.getValue().getForms().entrySet();
            for (Map.Entry<String, Form> entry : entries) {
                entry.getValue().init();
            }
        }
    }
}
