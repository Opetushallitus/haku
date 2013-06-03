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

package fi.vm.sade.oppija.lomake.domain.builders;

import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;

import java.util.Date;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;

public class FormModelBuilder {

    public static final String PHASE_ID = "phase";
    public static final String THEME_ID = "theme";
    public static final String APPLICATION_PERIOD_ID = "application_period";
    public static final String FORM_ID = "form";

    private FormModel formModel = new FormModel();
    ApplicationPeriodBuilder applicationPeriodBuilder = new ApplicationPeriodBuilder(APPLICATION_PERIOD_ID);
    private Phase phase = new Phase(PHASE_ID, createI18NAsIs(PHASE_ID), false);
    private Theme theme = new Theme(THEME_ID, createI18NAsIs(THEME_ID), null, true);

    private FormBuilder formBuilder =
            new FormBuilder(FORM_ID,
                    createI18NAsIs("Test form created at " + new Date()));

    Form form = (Form) createForm(phase);

    private Element createForm(Phase phase) {
        return formBuilder.withChild(phase.addChild(theme)).build();
    }

    public FormModel build() {
        return formModel;
    }

    public FormModelBuilder withDefaults() {
        this.formModel.addApplicationPeriod(applicationPeriodBuilder.withForm(form).build());
        return this;
    }


    public FormModelBuilder addChildToTeema(Element... element) {
        for (Element element1 : element) {
            this.theme.addChild(element1);
        }
        return this;
    }

    public FormModel buildDefaultFormWithFields(Element... elements) {
        return this.withDefaults().addChildToTeema(elements).build();
    }
}
