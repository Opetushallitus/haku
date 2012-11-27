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
import fi.vm.sade.oppija.haku.domain.*;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.event.ValidationEvent;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.service.UserHolder;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author jukka
 * @version 9/26/122:43 PM}
 * @since 1.1
 */
@Service
public class HakemusServiceImpl implements HakemusService {

    private final ApplicationDAO applicationDAO;
    private final UserHolder userHolder;
    private final FormService formService;
    private final ValidationEvent validationEvent;

    @Autowired
    public HakemusServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                              final UserHolder userHolder,
                              @Qualifier("formServiceImpl") final FormService formService,
                              final ValidationEvent validationEvent) {
        this.applicationDAO = applicationDAO;
        this.userHolder = userHolder;
        this.formService = formService;
        this.validationEvent = validationEvent;
    }

    @Override
    public HakemusState tallennaVaihe(VaiheenVastaukset vaihe) {
        HakemusState hakemusState = new HakemusState(new Hakemus(this.userHolder.getUser(), vaihe), vaihe.getVaiheId());
        validationEvent.process(hakemusState);
        return this.applicationDAO.tallennaVaihe(hakemusState);
    }

    @Override
    public Hakemus getHakemus(String oid) {
        return this.applicationDAO.find(oid);
    }

    @Override
    public void laitaVireille(HakuLomakeId hakulomakeId) {
        VaiheenVastaukset vaiheenVastaukset = new VaiheenVastaukset(hakulomakeId, "valmis", new HashMap<String, String>(0));
        HakemusState hakemusState = new HakemusState(new Hakemus(this.userHolder.getUser(), vaiheenVastaukset), vaiheenVastaukset.getVaiheId());
        validationEvent.process(hakemusState);
        if (hakemusState.isValid()) {
            this.applicationDAO.laitaVireille(hakulomakeId, userHolder.getUser());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public List<HakemusInfo> findAll() {
        List<HakemusInfo> all = new ArrayList<HakemusInfo>();
        final List<Hakemus> hakemusList = applicationDAO.findAll(userHolder.getUser());
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
        return applicationDAO.find(hakuLomakeId, userHolder.getUser());
    }
}
