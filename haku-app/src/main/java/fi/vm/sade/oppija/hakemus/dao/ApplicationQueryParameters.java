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

import java.util.ArrayList;
import java.util.List;

public class ApplicationQueryParameters {
    private final String state;
    private final boolean fetchPassive;
    private final List<String> preferences = new ArrayList<String>();
    private final String LOPOid;
    private final boolean fetchSubmittedOnly = true;

    public ApplicationQueryParameters() {
        this.state = "";
        this.fetchPassive = false;
        this.LOPOid = "";
    }

    public ApplicationQueryParameters(final List<String> preferences) {
        this();
        this.preferences.addAll(preferences);
    }

    public ApplicationQueryParameters(final String state, final boolean fetchPassive,
                                      final String preference, final String lopOid) {
        this.state = state;
        this.fetchPassive = fetchPassive;
        addPreference(preference);
        LOPOid = lopOid;
    }

    public String getState() {
        return state;
    }

    public boolean isFetchPassive() {
        return fetchPassive;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void addPreference(String preference) {
        if (preference != null && !preference.isEmpty()) {
            this.preferences.add(preference);
        }
    }

    public String getLOPOid() {
        return LOPOid;
    }

    public boolean isFetchSubmittedOnly() {
        return fetchSubmittedOnly;
    }
}
