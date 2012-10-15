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

package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.event.EventHandler;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.service.SessionDataHolder;
import fi.vm.sade.oppija.haku.service.UserHolder;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:43 PM}
 * @since 1.1
 */
@Service
public class HakemusServiceImpl implements HakemusService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HakemusServiceImpl.class);

    private final ApplicationDAO sessionDataHolder;
    private final ApplicationDAO applicationDAO;
    private final EventHandler eventHandler;
    private final UserHolder userHolder;

    @Autowired
    public HakemusServiceImpl(@Qualifier("sessionDataHolder") final SessionDataHolder sessionDataHolder,
                              @Qualifier("applicationDAOMongoImpl") final ApplicationDAO applicationDAO,
                              final EventHandler eventHandler, final UserHolder userHolder) {
        this.sessionDataHolder = sessionDataHolder;
        this.applicationDAO = applicationDAO;
        this.eventHandler = eventHandler;
        this.userHolder = userHolder;
    }


    @Override
    public HakemusState save(HakemusId hakemusId, Map<String, String> values) {
        LOGGER.info("save");
        final Hakemus hakemus = new Hakemus(hakemusId, values, userHolder.getUser());

        final HakemusState hakemusState = new HakemusState(hakemus);
        eventHandler.processEvents(hakemusState);

        updateApplication(hakemus);
        return hakemusState;
    }

    @Override
    public List<Hakemus> findAll() {
        return selectDao().findAll(userHolder.getUser());
    }

    @Override
    public Hakemus getHakemus(HakemusId hakemusId) {
        return selectDao().find(hakemusId, userHolder.getUser());
    }

    private ApplicationDAO selectDao() {
        ApplicationDAO dao = sessionDataHolder;
        if (userHolder.isUserKnown()) {
            dao = applicationDAO;
        }
        return dao;
    }


    private void updateApplication(Hakemus hakemus) {
        selectDao().update(hakemus);
    }


}
