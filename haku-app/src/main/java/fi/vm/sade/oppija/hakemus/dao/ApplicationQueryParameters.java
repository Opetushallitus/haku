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

public class ApplicationQueryParameters {
    private final String state;
    private final boolean fetchPassive;
    private final String preference;
    private final String LOPOid;

    public ApplicationQueryParameters(final String state, final boolean fetchPassive,
                                      final String preference, final String lopOid) {
        this.state = state;
        this.fetchPassive = fetchPassive;
        this.preference = preference;
        LOPOid = lopOid;
    }

    public String getState() {
        return state;
    }

    public boolean isFetchPassive() {
        return fetchPassive;
    }

    public String getPreference() {
        return preference;
    }

    public String getLOPOid() {
        return LOPOid;
    }
}
