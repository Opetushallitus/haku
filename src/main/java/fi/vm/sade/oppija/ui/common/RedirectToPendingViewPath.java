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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedirectToPendingViewPath implements ViewPath {

    public static final Logger LOGGER = LoggerFactory.getLogger(RedirectToPendingViewPath.class);
    public static final String VALMIS_VIEW = "valmis";
    final private String path;

    public RedirectToPendingViewPath(final String applicationPeriodId, final String formId, final String oid) {
        Preconditions.checkNotNull(applicationPeriodId, formId, oid);
        Preconditions.checkNotNull(formId);
        Preconditions.checkNotNull(oid);
        Joiner joiner = Joiner.on("/").skipNulls();
        path = joiner.join(REDIRECT_LOMAKE, applicationPeriodId, formId, VALMIS_VIEW, oid, "");
    }


    @Override
    public String getPath() {
        return this.path;
    }
}
