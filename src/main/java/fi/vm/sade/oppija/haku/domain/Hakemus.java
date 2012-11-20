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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:48 PM}
 * @since 1.1
 */
public class Hakemus implements Serializable {
    public static final String HAKEMUS_OID = "HakemusOid";

    private static final long serialVersionUID = -7491168801255850954L;

    private final HakuLomakeId hakuLomakeId;
    private final User user;

    private final Map<String, String> meta = new HashMap<String, String>();
    private final Map<String, Map<String, String>> vastaukset = new HashMap<String, Map<String, String>>();

    public Hakemus(final HakuLomakeId hakuLomakeId, final User user) {
        this.hakuLomakeId = hakuLomakeId;
        this.user = user;
    }

    public Hakemus(HakuLomakeId hakuLomakeId, User user, Map<String, Map<String, String>> vastaukset) {
        this(hakuLomakeId, user);
        this.vastaukset.putAll(vastaukset);
    }

    public Hakemus(User user, VaiheenVastaukset vaihe) {
        this(vaihe.getHakuLomakeId(), user);
        addVaiheenVastaukset(vaihe.getVaiheId(), vaihe.getVastaukset());
    }

    public Hakemus addMeta(final Map<String, String> meta) {
        this.meta.putAll(meta);
        return this;
    }

    public Hakemus addMeta(final String name, final String value) {
        this.meta.put(name, value);
        return this;
    }


    public Hakemus addVaiheenVastaukset(final String vaiheId, Map<String, String> vastaukset) {
        this.vastaukset.put(vaiheId, vastaukset);
        return this;
    }

    public User getUser() {
        return user;
    }

    public HakuLomakeId getHakuLomakeId() {
        return hakuLomakeId;
    }

    public Map<String, String> getMeta() {
        return Collections.unmodifiableMap(meta);
    }

    public Map<String, String> getVastaukset() {
        final Map<String, String> vastaukset = new HashMap<String, String>();
        for (Map<String, String> vaiheenVastaukset : this.vastaukset.values()) {
            vastaukset.putAll(vaiheenVastaukset);
        }
        return vastaukset;
    }

}
