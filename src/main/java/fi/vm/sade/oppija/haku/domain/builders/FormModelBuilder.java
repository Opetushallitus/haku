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

package fi.vm.sade.oppija.haku.domain.builders;

import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Theme;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;

import java.util.Date;

/**
 * @author jukka
 * @version 9/7/1212:47 PM}
 * @since 1.1
 */
public class FormModelBuilder {

    public static final String VAIHE_ID = "kategoria";
    public static final String VAIHE_TITLE = "category1";
    public static final String TEEMA_ID = "teema";
    public static final String TEEMA_TITLE = "teema1";
    public static final String APPLICATION_PERIOD_ID = "yhteishaku";
    public static final String FORM_ID = "form";
    FormModel formModel = new FormModel();

    ApplicationPeriodBuilder applicationPeriodBuilder = new ApplicationPeriodBuilder(APPLICATION_PERIOD_ID);
    private Vaihe vaihe = new Vaihe(VAIHE_ID, VAIHE_TITLE, false);
    private Theme theme = new Theme(TEEMA_ID, TEEMA_TITLE, null);

    private FormBuilder formBuilder = new FormBuilder(FORM_ID, "Tässä olisi kuvaava otsikko. Tämä on kuitenkin testiformi joka on luotu " + new Date());

    Form form = createForm(vaihe);

    private Form createForm(Vaihe vaihe) {
        return formBuilder.withChild(vaihe.addChild(theme)).build();
    }

    public FormModelBuilder() {
    }

    public FormModelBuilder(Vaihe vaihe) {
        this.vaihe = vaihe;
        form = createForm(vaihe);
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
