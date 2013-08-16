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
    private final String aoId;
    private final String lopOid;
    private final int start;
    private final int rows;

    public ApplicationQueryParameters(final String state, final String aoId, final String lopOid, final int start, final int rows) {
        this.lopOid = isEmpty(lopOid) ? null : lopOid;
        this.state = isEmpty(state) ? null : state;
        this.aoId = isEmpty(aoId) ? null : aoId;
        this.start = start;
        this.rows = rows;
    }

    public String getState() {
        return state;
    }

    public String getAoId() {
        return aoId;
    }

    public String getLopOid() {
        return lopOid;
    }

    public int getStart() {
        return start;
    }

    public int getRows() {
        return rows;
    }
}
