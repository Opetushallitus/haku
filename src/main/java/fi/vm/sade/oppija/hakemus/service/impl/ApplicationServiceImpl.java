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

import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.application.process.service.ApplicationProcessStateService;
import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.IllegalStateException;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
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
import java.util.Map;

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
    private final ApplicationProcessStateService applicationProcessStateService;

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                  final UserHolder userHolder,
                                  @Qualifier("formServiceImpl") final FormService formService,
                                  @Qualifier("applicationProcessStateServiceImpl") final ApplicationProcessStateService applicationProcessStateService) {
        this.applicationDAO = applicationDAO;
        this.userHolder = userHolder;
        this.formService = formService;
        this.applicationProcessStateService = applicationProcessStateService;
    }

    @Override
    public ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase) {
        final Application application = new Application(this.userHolder.getUser(), applicationPhase);
        return saveApplicationPhase(applicationPhase, application);
    }

    @Override
    public ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase, String oid) {
        final Application application = new Application(oid, applicationPhase);
        return saveApplicationPhase(applicationPhase, application);
    }

    @Override
    public ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase, Application application) {
        final ApplicationState applicationState = new ApplicationState(application, applicationPhase.getVaiheId());
        final String applicationPeriodId = applicationState.getHakemus().getFormId().getApplicationPeriodId();
        final String formId = applicationState.getHakemus().getFormId().getFormId();
        final Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        final Phase phase = activeForm.getPhase(applicationPhase.getVaiheId());
        final Map<String, String> vastaukset = applicationPhase.getVastaukset();

        ValidationResult validationResult = ElementTreeValidator.validate(phase, vastaukset);
        applicationState.addError(validationResult.getErrorMessages());
        if (applicationState.isValid()) {
            this.applicationDAO.tallennaVaihe(applicationState);
        }
        return applicationState;
    }

    @Override
    public Application getApplication(String oid) throws ResourceNotFoundException {
        return getApplication(new Application(oid));
    }

    @Override
    public String submitApplication(final FormId formId) {
        final User user = userHolder.getUser();
        Application application1 = new Application(formId, user);
        Application application = applicationDAO.findDraftApplication(application1);
        Form form = formService.getForm(formId.getApplicationPeriodId(), formId.getFormId());
        ValidationResult validationResult = ElementTreeValidator.validate(form, application.getVastauksetMerged());
        if (!validationResult.hasErrors()) {
            String newOid = applicationDAO.getNewOid();
            application.setOid(newOid);
            if (!user.isKnown()) {
                application.removeUser();
            }
            this.applicationDAO.update(application1, application);
            this.applicationProcessStateService.setApplicationProcessStateStatus(newOid, ApplicationProcessStateStatus.ACTIVE);
            return newOid;
        } else {
            throw new IllegalStateException("Could not send the application");
        }
    }

    @Override
    public Application getPendingApplication(FormId formId, String oid) throws ResourceNotFoundException {
        final User user = userHolder.getUser();
        Application application = new Application(formId, user);
        application.setOid(oid);
        if (!user.isKnown()) {
            application.removeUser();
        }
        return getApplication(application);
    }

    @Override
    public List<Application> getApplicationsByApplicationSystem(String applicationSystemId) {

        return applicationDAO.findByApplicationSystem(applicationSystemId);
    }

    @Override
    public List<ApplicationInfo> getUserApplicationInfo() {
        List<ApplicationInfo> listOfApplicationInfos = new ArrayList<ApplicationInfo>();
        List<Application> listOfUserApplications = applicationDAO.find(new Application(userHolder.getUser()));
        for (Application application : listOfUserApplications) {
            final ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(application.getFormId().getApplicationPeriodId());
            final String id = applicationPeriod.getId();
            final String formId = application.getFormId().getFormId();
            final Form form = formService.getForm(id, formId);
            listOfApplicationInfos.add(new ApplicationInfo(application, form, applicationPeriod));
        }
        return listOfApplicationInfos;
    }

    @Override
    public Application getApplication(final FormId formId) {
        Application application = new Application(formId, userHolder.getUser());
        List<Application> listOfApplications = applicationDAO.find(application);
        if (listOfApplications.isEmpty() || listOfApplications.size() > 1) {
            return application;
        }
        return listOfApplications.get(0);
    }

    private Application getApplication(final Application application) throws ResourceNotFoundException {
        List<Application> listOfApplications = applicationDAO.find(application);
        if (listOfApplications.isEmpty() || listOfApplications.size() > 1) {
            throw new ResourceNotFoundException("Could not find application " + listOfApplications.size());
        }
        return listOfApplications.get(0);

    }
}
