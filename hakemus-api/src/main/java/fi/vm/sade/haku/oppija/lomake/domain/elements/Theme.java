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

package fi.vm.sade.haku.oppija.lomake.domain.elements;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.springframework.data.annotation.Transient;

public class Theme extends Titled {

    private static final long serialVersionUID = -1394712276903310469L;
    final boolean previewable;
    @Transient
    boolean configurable;

    public Theme(final String id, final I18nText i18nText, final boolean previewable) {
        super(id, i18nText);
        this.previewable = previewable;
    }

    public boolean isPreviewable() {
        return previewable;
    }

    @Transient
    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }
}
