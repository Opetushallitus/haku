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

package fi.vm.sade.haku.oppija.lomake.domain.builders;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemCreator;
import fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;

import java.util.Date;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;

public class FormModelBuilder { // TODO rename to application system builder

    public static final String PHASE_ID = "phase";
    public static final String THEME_ID = "theme";
    public static final String APPLICATION_SYSTEM_ID = "applicationSystemId";
    public static final String FORM_ID = "form";

    private ApplicationSystem applicationSystem;
    private Element phase = new PhaseBuilder(PHASE_ID).setEditAllowedByRoles("TESTING")
            .i18nText(createI18NAsIs(PHASE_ID)).build();
    private Element theme = new ThemeBuilder(THEME_ID).previewable().build();

    private FormBuilder formBuilder =
            new FormBuilder(FORM_ID,
                    createI18NAsIs("Test form created at " + new Date()));

    Form form = (Form) createForm(phase);

    private Element createForm(Element phase) {
        return formBuilder.withChild(phase.addChild(theme)).build();
    }

    public ApplicationSystem build() {
        return applicationSystem;
    }

    public FormModelBuilder withDefaults() {
        this.applicationSystem = ApplicationSystemCreator.createActiveApplicationSystem(APPLICATION_SYSTEM_ID, form);
        return this;
    }


    public FormModelBuilder addChildToTeema(Element... element) {
        for (Element element1 : element) {
            this.theme.addChild(element1);
        }
        return this;
    }

    public ApplicationSystem buildDefaultFormWithFields(Element... elements) {
        return this.withDefaults().addChildToTeema(elements).build();
    }
}
