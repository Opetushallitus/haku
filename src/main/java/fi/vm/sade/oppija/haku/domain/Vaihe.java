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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.haku.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Vaihe {

    private final HakemusId hakemusId;
    private final String vaiheId;
    private final Map<String, String> vastaukset = new HashMap<String, String>();

    public Vaihe(final HakemusId hakemusId, final String vaiheId, final Map<String, String> vastaukset) {
        this.hakemusId = hakemusId;
        this.vaiheId = vaiheId;
        this.vastaukset.putAll(vastaukset);
    }

    public HakemusId getHakemusId() {
        return hakemusId;
    }

    public String getVaiheId() {
        return vaiheId;
    }

    public Map<String, String> getVastaukset() {
        return Collections.unmodifiableMap(this.vastaukset);
    }
}
