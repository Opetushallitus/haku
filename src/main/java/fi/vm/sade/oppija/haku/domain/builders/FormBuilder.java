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

import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;


/**
 * @author jukka
 * @version 9/7/1212:54 PM}
 * @since 1.1
 */
public class FormBuilder extends ElementBuilder {

    public FormBuilder(final String id, final String name) {
        super(new Form(id, name));
    }

    @Override
    public FormBuilder withChild(Element... categories) {
        super.withChild(categories);
        return this;
    }

    public Form build() {
        final Form form = (Form) element;
        form.init();
        return form;
    }
}
