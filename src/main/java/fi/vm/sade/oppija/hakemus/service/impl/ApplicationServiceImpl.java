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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import fi.vm.sade.oppija.common.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.oppija.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.application.process.service.ApplicationProcessStateService;
import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.oppija.lomake.domain.exception.IllegalStateException;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;

/**
 * @author jukka
 * @version 9/26/122:43 PM}
 * @since 1.1
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final ApplicationDAO applicationDAO;
    private final ApplicationOidService applicationOidService;
    private final UserHolder userHolder;
    private final FormService formService;
    private final ApplicationProcessStateService applicationProcessStateService;
    private static final String SOCIAL_SECURITY_NUMBER_PATTERN = "([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))";
    private static final String OID_PATTERN = "^[0-9]+.[0-9]+.[0-9]+.[0-9]+.[0-9]+.[0-9]+$";
    private final Pattern socialSecurityNumberPattern;
    private final Pattern oidPattern;
    private final AuthenticationService authenticationService;

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                  final UserHolder userHolder,
                                  @Qualifier("formServiceImpl") final FormService formService,
                                  @Qualifier("applicationProcessStateServiceImpl") final ApplicationProcessStateService applicationProcessStateService,
                                  @Qualifier("applicationOidServiceImpl") ApplicationOidService applicationOidService,
                                  @Qualifier("authenticationServiceMockImpl") AuthenticationService authenticationService) {
        this.applicationDAO = applicationDAO;
        this.userHolder = userHolder;
        this.formService = formService;
        this.applicationProcessStateService = applicationProcessStateService;
        this.authenticationService = authenticationService;
        this.socialSecurityNumberPattern = Pattern.compile(SOCIAL_SECURITY_NUMBER_PATTERN);
        this.oidPattern = Pattern.compile(OID_PATTERN);
        this.applicationOidService = applicationOidService;
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
        final ApplicationState applicationState = new ApplicationState(application, applicationPhase.getPhaseId());
        final String applicationPeriodId = applicationState.getHakemus().getFormId().getApplicationPeriodId();
        final String formId = applicationState.getHakemus().getFormId().getFormId();
        final Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        final Phase phase = activeForm.getPhase(applicationPhase.getPhaseId());
        final Map<String, String> vastaukset = applicationPhase.getAnswers();

        Map<String, String> allAnswers = new HashMap<String, String>();
        //if the current phase has previous phase, get all the answers for validating rules
        if (phase.isHasPrev()) {
            Application current = getApplication(applicationState.getHakemus().getFormId());
            allAnswers.putAll(current.getVastauksetMerged());
        }
        allAnswers.putAll(vastaukset);

        ValidationResult validationResult = ElementTreeValidator.validate(phase, allAnswers);
        applicationState.addError(validationResult.getErrorMessages());
        if (applicationState.isValid()) {
            if (application.getOid() == null) {
                checkIfExistsBySocialSecurityNumber(applicationPeriodId, vastaukset.get(SocialSecurityNumber.HENKILOTUNNUS));
            }
            this.applicationDAO.tallennaVaihe(applicationState);
        }
        //sets all answers merged, needed for re-rendering view if errors
        applicationState.setAnswersMerged(allAnswers);
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
        Map<String, String> allAnswers = application.getVastauksetMerged();
        ValidationResult validationResult = ElementTreeValidator.validate(form, allAnswers);
        if (!validationResult.hasErrors()) {
            checkIfExistsBySocialSecurityNumber(formId.getApplicationPeriodId(), allAnswers.get(SocialSecurityNumber.HENKILOTUNNUS));
            String newOid = applicationOidService.generateNewOid();
            application.setOid(newOid);
            if (!user.isKnown()) {
                application.removeUser();
            }

            // invoke authentication service to obtain oid
            Person person = new Person(allAnswers.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES), allAnswers.get(OppijaConstants.ELEMENT_ID_NICKNAME),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_LAST_NAME), allAnswers.get(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER),
                    false, allAnswers.get(OppijaConstants.ELEMENT_ID_EMAIL), allAnswers.get(OppijaConstants.ELEMENT_ID_SEX),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_HOME_CITY), false, allAnswers.get(OppijaConstants.ELEMENT_ID_LANGUAGE),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_NATIONALITY), allAnswers.get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE));

            String personOid = null;

            try {
                personOid = this.authenticationService.addPerson(person);
            } catch (Exception e) {
                LOGGER.warn("Could not obtain person oid by invoking authentication service, using mock as a fall back, " +
                        "reason: " + e.getMessage());
                AuthenticationService auth = new AuthenticationServiceMockImpl();
                personOid = auth.addPerson(person);
            }

            application.setPersonOid(personOid);

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
    public List<Application> getApplicationsByApplicationOption(String applicationOptionId) {
        return applicationDAO.findByApplicationOption(applicationOptionId);
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

    @Override
    public List<Application> findApplications(final String term, final String state, final boolean fetchPassive, final String preference) {
        Application application = new Application();
        List<Application> applications = new LinkedList<Application>();
        if (oidPattern.matcher(term).matches()) {
            application.setOid(term);
            application.setState(fetchPassive ? null : Application.State.ACTIVE);
            applications.addAll(applicationDAO.find(application, state, fetchPassive, preference));
        } else if (socialSecurityNumberPattern.matcher(term).matches()){
            applications.addAll(applicationDAO.findByApplicantSsn(term, state, fetchPassive, preference));
        } else if (!StringUtils.isEmpty(term)){
            applications.addAll(applicationDAO.findByApplicantName(term, state, fetchPassive, preference));
        } else {
            applications.addAll(applicationDAO.find(application, state, fetchPassive, preference));
        }
        return applications;
    }

    private Application getApplication(final Application application) throws ResourceNotFoundException {
        List<Application> listOfApplications = applicationDAO.find(application);
        if (listOfApplications.isEmpty() || listOfApplications.size() > 1) {
            throw new ResourceNotFoundException("Could not find application " + listOfApplications.size());
        }
        return listOfApplications.get(0);

    }

    private void checkIfExistsBySocialSecurityNumber(String asId, String ssn) {
        if (ssn != null) {
            Matcher matcher = socialSecurityNumberPattern.matcher(ssn);
            if (matcher.matches() && this.applicationDAO.checkIfExistsBySocialSecurityNumber(asId, ssn)) {
                throw new IllegalStateException("Application already exists by social security number " + ssn);
            }
        }
    }

}
