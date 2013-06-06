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

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ApplicationQueryParameters {
    private final String state;
    private final List<String> preferences = new ArrayList<String>();
    private final String lopOid;

    public ApplicationQueryParameters() {
        this(null, null);
    }

    public ApplicationQueryParameters(final String state) {
        this(state, null);
    }

    public ApplicationQueryParameters(final String state, final String lopOid) {
        this.state = isEmpty(state) ? null : state;
        this.lopOid = isEmpty(lopOid) ? null : lopOid;
    }

    public ApplicationQueryParameters(final String state, final String preference, final String lopOid) {
        this(state, lopOid);
        if (!isEmpty(preference)) {
            this.preferences.add(preference);
        }
    }

    public String getState() {
        return state;
    }

    public List<String> getPreferences() {
        return ImmutableList.copyOf(preferences);
    }

    public String getLopOid() {
        return lopOid;
    }

    public boolean isFetchSubmittedOnly() {
        return true;
    }
}
