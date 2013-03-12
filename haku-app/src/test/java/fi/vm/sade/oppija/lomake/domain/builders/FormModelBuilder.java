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

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;

public class FormModelBuilder {

    public static final String VAIHE_ID = "kategoria";
    public static final String VAIHE_TITLE = "category1";
    public static final String TEEMA_ID = "teema";
    public static final String TEEMA_TITLE = "teema1";
    public static final String APPLICATION_PERIOD_ID = "yhteishaku";
    public static final String FORM_ID = "form";
    FormModel formModel = new FormModel();

    ApplicationPeriodBuilder applicationPeriodBuilder = new ApplicationPeriodBuilder(APPLICATION_PERIOD_ID);
    private Phase phase = new Phase(VAIHE_ID, createI18NText(VAIHE_TITLE), false);
    private Theme theme = new Theme(TEEMA_ID, createI18NText(TEEMA_TITLE), null);

    private FormBuilder formBuilder =
            new FormBuilder(FORM_ID,
                    createI18NText("Tässä olisi kuvaava otsikko. Tämä on kuitenkin testiformi joka on luotu " + new Date()));

    Form form = createForm(phase);

    private Form createForm(Phase phase) {
        return formBuilder.withChild(phase.addChild(theme)).build();
    }

    public FormModelBuilder() {
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
