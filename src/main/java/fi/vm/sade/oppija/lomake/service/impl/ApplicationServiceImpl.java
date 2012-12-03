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

package fi.vm.sade.oppija.lomake.service.impl;

import fi.vm.sade.oppija.lomake.dao.ApplicationDAO;
import fi.vm.sade.oppija.lomake.domain.*;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.event.ValidationEvent;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.ApplicationService;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.validation.HakemusState;
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
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationDAO applicationDAO;
    private final UserHolder userHolder;
    private final FormService formService;
    private final ValidationEvent validationEvent;

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
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
        HakemusState hakemusState = new HakemusState(new Application(this.userHolder.getUser(), vaihe), vaihe.getVaiheId());
        validationEvent.process(hakemusState);
        return this.applicationDAO.tallennaVaihe(hakemusState);
    }

    @Override
    public Application getHakemus(String oid) {
        return this.applicationDAO.find(oid);
    }

    @Override
    public void laitaVireille(HakuLomakeId hakulomakeId) {
        Application application = applicationDAO.find(hakulomakeId, userHolder.getUser());
        VaiheenVastaukset vaiheenVastaukset = new VaiheenVastaukset(hakulomakeId, "valmis", application.getVastauksetMerged());
        HakemusState hakemusState = new HakemusState(new Application(this.userHolder.getUser(), vaiheenVastaukset), vaiheenVastaukset.getVaiheId());
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
        final List<Application> applicationList = applicationDAO.findAll(userHolder.getUser());
        for (Application application : applicationList) {
            final ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(application.getHakuLomakeId().getApplicationPeriodId());
            final String id = applicationPeriod.getId();
            final String formId = application.getHakuLomakeId().getFormId();
            final Form form = formService.getForm(id, formId);
            all.add(new HakemusInfo(application, form, applicationPeriod));
        }
        return all;
    }

    @Override
    public Application getHakemus(HakuLomakeId hakuLomakeId) {
        return applicationDAO.find(hakuLomakeId, userHolder.getUser());
    }
}
