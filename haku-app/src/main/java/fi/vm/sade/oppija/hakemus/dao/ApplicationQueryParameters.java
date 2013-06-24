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

package fi.vm.sade.oppija.hakemus.dao;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ApplicationQueryParameters {
    private final String state;
    private final String preference;
    private final String lopOid;

    public ApplicationQueryParameters() {
        this(null, null);
    }

    public ApplicationQueryParameters(final String state) {
        this(state, null);
    }

    public ApplicationQueryParameters(final String state, final String lopOid) {
        this(state, null, lopOid);
    }

    public ApplicationQueryParameters(final String state, final String preference, final String lopOid) {
        this.lopOid = isEmpty(lopOid) ? null : lopOid;
        this.state = isEmpty(state) ? null : state;
        this.preference = isEmpty(preference) ? null : preference;
    }

    public String getState() {
        return state;
    }

    public String getPreference() {
        return preference;
    }

    public String getLopOid() {
        return lopOid;
    }

    public boolean isFetchSubmittedOnly() {
        return true;
    }
}
