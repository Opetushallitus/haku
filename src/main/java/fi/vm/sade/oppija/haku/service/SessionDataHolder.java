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

package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author jukka
 * @version 9/27/129:21 AM}
 * @since 1.1
 */
@Component("sessionDataHolder")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionDataHolder implements Serializable, ApplicationDAO {

    private static final long serialVersionUID = -3751714345380438532L;
    private HashMap<HakemusId, Hakemus> map = new HashMap<HakemusId, Hakemus>();

    public Hakemus find(HakemusId id, User user) {
        Hakemus hakemus = map.get(id);
        if (hakemus == null) {
            final HashMap<String, String> values = new HashMap<String, String>();
            new TimeStampModifier(values).updateCreated();
            hakemus = new Hakemus(id, values, user);
            map.put(id, hakemus);
        }
        return hakemus;
    }

    @Override
    public List<Hakemus> findAll(User user) {
        return new ArrayList<Hakemus>(map.values());
    }

    public void update(Hakemus hakemus) {
        new TimeStampModifier(hakemus.getValues()).updateModified();
        this.map.put(hakemus.getHakemusId(), hakemus);
    }

}
