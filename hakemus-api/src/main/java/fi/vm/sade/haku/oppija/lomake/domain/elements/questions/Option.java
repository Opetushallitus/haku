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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class Option extends Question{

    private static final long serialVersionUID = 2199056039532430243L;
    public static final String EMPTY_VALUE_PLACEHOLDER = "\u00A0";
    private final String value;
    private boolean defaultOption = false;

    public Option(final I18nText i18nText, final String value) {
        super(ElementUtil.randomId(), i18nText);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setDefaultOption(boolean defaultOption) {
        this.defaultOption = defaultOption;
    }

    public boolean isDefaultOption() {
        return defaultOption;
    }
}
