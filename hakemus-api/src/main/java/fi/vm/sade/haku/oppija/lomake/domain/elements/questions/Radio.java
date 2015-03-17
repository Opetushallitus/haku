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

package fi.vm.sade.haku.oppija.lomake.domain.elements.questions;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;

import java.util.List;

public class Radio extends OptionQuestion {

    private static final long serialVersionUID = 7715723881547491714L;

    public Radio(final String id, final I18nText i18nText, final List<Option> options) {
        super(id, i18nText, options, null);
    }
}
