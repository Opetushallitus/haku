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

package fi.vm.sade.oppija.hakemus.service.impl;

import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.lomake.domain.*;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.event.ValidationEvent;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.oppija.lomake.validation.HakemusState;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
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
    public HakemusState tallennaVaihe(ApplicationPhase vaihe) {
        HakemusState hakemusState = new HakemusState(new Application(this.userHolder.getUser(), vaihe), vaihe.getVaiheId());
        //validationEvent.process(hakemusState);
        Form activeForm = formService.getActiveForm(hakemusState.getHakemus().getFormId().getApplicationPeriodId(), hakemusState.getHakemus().getFormId().getFormId());
        ValidationResult validationResult = ElementTreeValidator.validate(activeForm.getCategory(vaihe.getVaiheId()), vaihe.getVastaukset());
        hakemusState.addError(validationResult.getErrorMessages());
        if (hakemusState.isValid()) {
            this.applicationDAO.tallennaVaihe(hakemusState);
        }
        return hakemusState;
    }

    @Override
    public Application getHakemus(String oid) {
        return this.applicationDAO.find(oid);
    }

    @Override
    public void laitaVireille(FormId hakulomakeId) {
        Application application = applicationDAO.find(hakulomakeId, userHolder.getUser());
        ApplicationPhase applicationPhase = new ApplicationPhase(hakulomakeId, "valmis", application.getVastauksetMerged());
        HakemusState hakemusState = new HakemusState(new Application(this.userHolder.getUser(), applicationPhase), applicationPhase.getVaiheId());
        validationEvent.process(hakemusState);
        if (hakemusState.isValid()) {
            this.applicationDAO.laitaVireille(hakulomakeId, userHolder.getUser());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public List<ApplicationInfo> findAll() {
        List<ApplicationInfo> all = new ArrayList<ApplicationInfo>();
        final List<Application> hakemusList = applicationDAO.findAll(userHolder.getUser());
        for (Application hakemus : hakemusList) {
            final ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(hakemus.getFormId().getApplicationPeriodId());
            final String id = applicationPeriod.getId();
            final String formId = hakemus.getFormId().getFormId();
            final Form form = formService.getForm(id, formId);
            all.add(new ApplicationInfo(hakemus, form, applicationPeriod));
        }
        return all;
    }

    @Override
    public Application getHakemus(FormId formId) {
        return applicationDAO.find(formId, userHolder.getUser());
    }
}
