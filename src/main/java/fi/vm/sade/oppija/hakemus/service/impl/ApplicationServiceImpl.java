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
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.exception.IllegalStateException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.lomake.validation.ElementTreeValidator;
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

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                  final UserHolder userHolder,
                                  @Qualifier("formServiceImpl") final FormService formService) {
        this.applicationDAO = applicationDAO;
        this.userHolder = userHolder;
        this.formService = formService;
    }

    @Override
    public ApplicationState tallennaVaihe(ApplicationPhase applicationPhase) {
        ApplicationState ApplicationState = new ApplicationState(new Application(this.userHolder.getUser(), applicationPhase), applicationPhase.getVaiheId());
        Form activeForm = formService.getActiveForm(ApplicationState.getHakemus().getFormId().getApplicationPeriodId(), ApplicationState.getHakemus().getFormId().getFormId());
        ValidationResult validationResult = ElementTreeValidator.validate(activeForm.getCategory(applicationPhase.getVaiheId()), applicationPhase.getVastaukset());
        ApplicationState.addError(validationResult.getErrorMessages());
        if (ApplicationState.isValid()) {
            this.applicationDAO.tallennaVaihe(ApplicationState);
        }
        return ApplicationState;
    }

    @Override
    public Application getHakemus(String oid) {
        return this.applicationDAO.find(oid);
    }

    @Override
    public void laitaVireille(final FormId formId) {
        Application application = applicationDAO.find(formId, userHolder.getUser());
        Form form = formService.getForm(formId.getApplicationPeriodId(), formId.getFormId());
        ValidationResult validationResult = ElementTreeValidator.validate(form, application.getVastauksetMerged());
        if (!validationResult.hasErrors()) {
            this.applicationDAO.laitaVireille(formId, userHolder.getUser());
        } else {
            throw new IllegalStateException("Could not send the application");
        }
    }

    @Override
    public List<ApplicationInfo> findAll() {
        List<ApplicationInfo> all = new ArrayList<ApplicationInfo>();
        final List<Application> listOfApplications = applicationDAO.findAll(userHolder.getUser());
        for (Application application : listOfApplications) {
            final ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(application.getFormId().getApplicationPeriodId());
            final String id = applicationPeriod.getId();
            final String formId = application.getFormId().getFormId();
            final Form form = formService.getForm(id, formId);
            all.add(new ApplicationInfo(application, form, applicationPeriod));
        }
        return all;
    }

    @Override
    public Application getHakemus(final FormId formId) {
        return applicationDAO.find(formId, userHolder.getUser());
    }
}
