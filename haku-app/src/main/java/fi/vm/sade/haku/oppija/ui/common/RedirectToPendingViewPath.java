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

package fi.vm.sade.haku.oppija.ui.common;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class RedirectToPendingViewPath implements ViewPath {

    public static final String VALMIS_VIEW = "valmis";
    private final String path;

    public RedirectToPendingViewPath(final String applicationSystemId, final String oid) {
        Preconditions.checkNotNull(applicationSystemId, "Application system id is null");
        Preconditions.checkNotNull(oid, "Phase oid is null");
        Joiner joiner = Joiner.on("/").skipNulls();
        path = joiner.join(REDIRECT_LOMAKE, applicationSystemId, VALMIS_VIEW, oid);
    }


    @Override
    public String getPath() {
        return this.path;
    }
}
