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
import org.springframework.data.annotation.Transient;

public class CheckBox extends Question {

    private static final long serialVersionUID = -3440726329489605608L;
    private static final String value = "true";

    public CheckBox(final String id, final I18nText i18nText) {
        super(id, i18nText);
    }

    @Transient
    public String getValue() {
        return value;
    }

    @Override
    public String getExcelValue(String value, String lang) {
        return Boolean.TRUE.toString().equals(value) ? "X" : "";
    }
}
