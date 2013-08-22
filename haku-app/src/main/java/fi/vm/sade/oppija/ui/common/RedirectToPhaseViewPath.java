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

package fi.vm.sade.oppija.ui.common;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class RedirectToPhaseViewPath implements ViewPath {

    private final String path;

    public RedirectToPhaseViewPath(final String applicationSystemId, final String phaseId) {
        Preconditions.checkNotNull(applicationSystemId);
        Preconditions.checkNotNull(phaseId);
        Joiner joiner = Joiner.on("/").skipNulls();
        this.path = joiner.join(REDIRECT_LOMAKE, applicationSystemId, phaseId);
    }

    @Override
    public String getPath() {
        return path;
    }
}
