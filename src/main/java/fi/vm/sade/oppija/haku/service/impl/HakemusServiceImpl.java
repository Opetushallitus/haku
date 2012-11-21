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

import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.model.Tapahtuma;
import fi.vm.sade.oppija.haku.domain.*;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.event.NavigationEvent;
import fi.vm.sade.oppija.haku.event.PreValidationEvent;
import fi.vm.sade.oppija.haku.event.ValidationEvent;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jukka
 * @version 9/26/122:43 PM}
 * @since 1.1
 */
@Service
public class HakemusServiceImpl implements HakemusService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HakemusServiceImpl.class);

    private final FormService formService;
    private final UserDataStorage userDataStorage;
    private final Logger log;
    private final PreValidationEvent preValidationEvent;
    private final ValidationEvent validationEvent;
    private final NavigationEvent navigationEvent;

    @Autowired
    public HakemusServiceImpl(final UserDataStorage userDataStorage,
                              @Qualifier("formServiceImpl") final FormService formService, final Logger logger) {

        this.userDataStorage = userDataStorage;
        this.formService = formService;
        this.log = logger;
        this.preValidationEvent = new PreValidationEvent();
        validationEvent = new ValidationEvent(formService);
        navigationEvent = new NavigationEvent(formService);
    }

    @Override
    public HakemusState tallennaVaihe(VaiheenVastaukset vaihe) {
        LOGGER.info("save");
        try {
            log.log(new Tapahtuma());
        } catch (Exception e) {
            LOGGER.warn("Could not log tallennaVaihe event");
        }

        final HakemusState hakemus = userDataStorage.initHakemusState(vaihe);
        preValidationEvent.process(hakemus);
        validationEvent.process(hakemus);
        final HakemusState tallenna = userDataStorage.tallenna(hakemus);
        navigationEvent.process(tallenna);
        return tallenna;
    }

    @Override
    public void tallennaHakemus(HakuLomakeId hakuLomakeId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Hakemus getHakemus(String oid) {
        return userDataStorage.applicationDAO.find(oid);
    }

    @Override
    public List<HakemusInfo> findAll() {
        List<HakemusInfo> all = new ArrayList<HakemusInfo>();
        final List<Hakemus> hakemusList = userDataStorage.findAll();
        for (Hakemus hakemus : hakemusList) {
            final ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(hakemus.getHakuLomakeId().getApplicationPeriodId());
            final String id = applicationPeriod.getId();
            final String formId = hakemus.getHakuLomakeId().getFormId();
            final Form form = formService.getForm(id, formId);
            all.add(new HakemusInfo(hakemus, form, applicationPeriod));
        }
        return all;
    }

    @Override
    public Hakemus getHakemus(HakuLomakeId hakuLomakeId) {
        return userDataStorage.find(hakuLomakeId);
    }

}
