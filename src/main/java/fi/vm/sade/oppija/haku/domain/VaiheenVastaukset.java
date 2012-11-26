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

package fi.vm.sade.oppija.haku.domain;

import java.util.HashMap;
import java.util.Map;

public class VaiheenVastaukset {

    private final HakuLomakeId hakuLomakeId;
    private final String vaiheId;
    private final Map<String, String> vastaukset = new HashMap<String, String>();

    public VaiheenVastaukset(final HakuLomakeId hakuLomakeId, final String vaiheId, final Map<String, String> vastaukset) {
        this.hakuLomakeId = hakuLomakeId;
        this.vaiheId = vaiheId;
        this.vastaukset.putAll(vastaukset);
    }

    public HakuLomakeId getHakuLomakeId() {
        return hakuLomakeId;
    }

    public String getVaiheId() {
        return vaiheId;
    }

    public String removeVastaus(final String key) {
        return this.vastaukset.remove(key);
    }

    public Map<String, String> getVastaukset() {
        return this.vastaukset;
    }
}
