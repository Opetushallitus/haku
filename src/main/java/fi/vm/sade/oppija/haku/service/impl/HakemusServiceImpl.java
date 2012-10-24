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

import fi.vm.sade.oppija.haku.controller.HakemusInfo;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.event.EventHandler;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private final EventHandler eventHandler;
    private final FormService formService;
    private final UserDataStorage userDataStorage;

    @Autowired
    public HakemusServiceImpl(final UserDataStorage userDataStorage, final EventHandler eventHandler, @Qualifier("formServiceImpl") final FormService formService) {
        this.userDataStorage = userDataStorage;
        this.eventHandler = eventHandler;
        this.formService = formService;
    }


    @Override
    public HakemusState save(HakemusId hakemusId, Map<String, String> values) {
        LOGGER.info("save");

        final Hakemus hakemus = userDataStorage.initHakemus(hakemusId, values);
        final HakemusState hakemusState = new HakemusState(hakemus);
        eventHandler.processEvents(hakemusState);

        userDataStorage.updateApplication(hakemus);
        return hakemusState;
    }

    @Override
    public List<HakemusInfo> findAll() {
        List<HakemusInfo> all = new ArrayList<HakemusInfo>();
        final List<Hakemus> hakemusList = userDataStorage.findAll();
        for (Hakemus hakemus : hakemusList) {
            final ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(hakemus.getHakemusId().getApplicationPeriodId());
            final String id = applicationPeriod.getId();
            final String formId = hakemus.getHakemusId().getFormId();
            final Form form = formService.getForm(id, formId);
            all.add(new HakemusInfo(hakemus, form, applicationPeriod));
        }
        return all;
    }

    @Override
    public Hakemus getHakemus(HakemusId hakemusId) {
        return userDataStorage.find(hakemusId);
    }

}
