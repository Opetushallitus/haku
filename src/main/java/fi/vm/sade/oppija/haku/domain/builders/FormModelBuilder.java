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

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;

/**
 * @author jukka
 * @version 9/7/1212:47 PM}
 * @since 1.1
 */
public class FormModelBuilder {

    FormModel formModel = new FormModel();

    ApplicationPeriodBuilder applicationPeriodBuilder = new ApplicationPeriodBuilder("yhteishaku");
    private Category category = new Category("kategoria", "category1");

    private FormBuilder formBuilder = new FormBuilder("form", "test");

    final Form form = formBuilder.withChild(category).build();

    public FormModelBuilder() {
    }

    public FormModelBuilder(Category category) {
        this.category = category;
    }

    public FormModel build() {
        return formModel;
    }

    public FormModelBuilder withApplicationPeriods(ApplicationPeriod... periods) {
        for (ApplicationPeriod applicationPeriod : periods) {
            this.formModel.addApplicationPeriod(applicationPeriod);
        }
        return this;
    }

    public FormModelBuilder withDefaults() {
        this.formModel.addApplicationPeriod(applicationPeriodBuilder.withForm(form).build());
        return this;
    }

    public FormModelBuilder addChildToForm(Element... element) {
        for (Element element1 : element) {
            this.form.addChild(element1);
        }
        return this;

    }

    public FormModelBuilder addChildToCategory(Element... element) {
        for (Element element1 : element) {
            this.category.addChild(element1);
        }
        return this;
    }

    public FormModel buildDefaultFormWithFields(Element... elements) {
        return this.withDefaults().addChildToCategory(elements).build();
    }
}
