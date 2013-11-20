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

package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import fi.vm.sade.authentication.service.GenericFault;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.*;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private static final String OPH_ORGANIZATION = "1.2.246.562.10.00000000001";
    private final ApplicationDAO applicationDAO;
    private final ApplicationOidService applicationOidService;
    private final UserSession userSession;
    private final FormService formService;
    private static final String SOCIAL_SECURITY_NUMBER_PATTERN = "([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))";
    private static final String DATE_OF_BIRTH_PATTERN = "[0-9]{6}";
    private static final String OID_PATTERN = "^[0-9]+.[0-9]+.[0-9]+.[0-9]+.[0-9]+.[0-9]+$";
    private static final String SHORT_OID_PATTERN = "^[0-9]{11}$";
    private final Pattern socialSecurityNumberPattern;
    private final Pattern dobPattern;
    private final Pattern oidPattern;
    private final Pattern shortOidPattern;
    private final AuthenticationService authenticationService;
    private final OrganizationService organizationService;
    private final HakuPermissionService hakuPermissionService;
    private final ApplicationSystemService applicationSystemService;
    private final ElementTreeValidator elementTreeValidator;

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                  final UserSession userSession,
                                  @Qualifier("formServiceImpl") final FormService formService,
                                  @Qualifier("applicationOidServiceImpl") ApplicationOidService applicationOidService,
                                  AuthenticationService authenticationService,
                                  OrganizationService organizationService,
                                  HakuPermissionService hakuPermissionService,
                                  ApplicationSystemService applicationSystemService,
                                  ElementTreeValidator elementTreeValidator) {

        this.applicationDAO = applicationDAO;
        this.userSession = userSession;
        this.formService = formService;
        this.applicationOidService = applicationOidService;
        this.authenticationService = authenticationService;
        this.organizationService = organizationService;
        this.hakuPermissionService = hakuPermissionService;
        this.applicationSystemService = applicationSystemService;
        this.elementTreeValidator = elementTreeValidator;

        this.socialSecurityNumberPattern = Pattern.compile(SOCIAL_SECURITY_NUMBER_PATTERN);
        this.dobPattern = Pattern.compile(DATE_OF_BIRTH_PATTERN);
        this.oidPattern = Pattern.compile(OID_PATTERN);
        this.shortOidPattern = Pattern.compile(SHORT_OID_PATTERN);
    }


    @Override
    public ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase) {
        final Application application = new Application(this.userSession.getUser(), applicationPhase);
        return saveApplicationPhase(applicationPhase, application);
    }

    private ApplicationState saveApplicationPhase(final ApplicationPhase applicationPhase,
                                                  final Application application) {
        //
        final String applicationSystemId = application.getApplicationSystemId();
        final Form activeForm = formService.getActiveForm(applicationSystemId);
        ElementTree elementTree = new ElementTree(activeForm);
        final Element phase = elementTree.getChildById(applicationPhase.getPhaseId());
        final Map<String, String> answers = applicationPhase.getAnswers();

        Map<String, String> allAnswers = new HashMap<String, String>();
        // if the current phase has previous phase, get all the answers for
        // validating rules
        Application current = userSession.getApplication(applicationSystemId);

        elementTree.isStateValid(current.getPhaseId(), applicationPhase.getPhaseId());

        allAnswers.putAll(current.getVastauksetMergedIgnoringPhase(applicationPhase.getPhaseId()));
        allAnswers.putAll(answers);

        final ApplicationState applicationState = new ApplicationState(application, applicationPhase.getPhaseId());
        if (elementTree.isValidationNeeded(applicationPhase.getPhaseId(), application.getPhaseId())) {
            ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(phase, allAnswers,
                    application.getOid(), applicationSystemId));
            applicationState.addError(validationResult.getErrorMessages());
        }

        if (applicationState.isValid()) {
            this.userSession.savePhaseAnswers(applicationPhase);
        }
        // sets all answers merged, needed for re-rendering view if errors
        applicationState.setAnswersMerged(allAnswers);
        return applicationState;
    }

    @Override
    public String submitApplication(final String applicationSystemId) {
        final User user = userSession.getUser();
        Application application = userSession.getApplication(applicationSystemId);
        Form form = formService.getForm(applicationSystemId);
        Map<String, String> allAnswers = application.getVastauksetMerged();
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, allAnswers,
                application.getOid(), applicationSystemId));

        if (!validationResult.hasErrors()) {

            application.setOid(applicationOidService.generateNewOid());
            if (!user.isKnown()) {
                application.removeUser();
            }

            application.resetUser();
            application.setReceived(new Date());
            addNote(application, "Hakemus vastaanotettu", false);
            application.setPersonOidChecked(System.currentTimeMillis());
            application.setStudentOidChecked(System.currentTimeMillis());
            this.applicationDAO.save(application);
            this.userSession.removeApplication(application);
            return application.getOid();
        } else {
            throw new IllegalStateException("Could not send the application ");
        }
    }

    @Override
    public Application addPersonOid(Application application) {
        Map<String, String> allAnswers = application.getVastauksetMerged();

        LOGGER.debug("start addPersonAndAuthenticate, {}", System.currentTimeMillis() / 1000L);

        PersonBuilder personBuilder = PersonBuilder.start()
                .setFirstNames(allAnswers.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES))
                .setNickName(allAnswers.get(OppijaConstants.ELEMENT_ID_NICKNAME))
                .setLastName(allAnswers.get(OppijaConstants.ELEMENT_ID_LAST_NAME))
                .setSex(allAnswers.get(OppijaConstants.ELEMENT_ID_SEX))
                .setHomeCity(allAnswers.get(OppijaConstants.ELEMENT_ID_HOME_CITY))
                .setLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_LANGUAGE))
                .setNationality(allAnswers.get(OppijaConstants.ELEMENT_ID_NATIONALITY))
                .setContactLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_FIRST_LANGUAGE))
                .setSocialSecurityNumber(allAnswers.get(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER))
                .setSecurityOrder(false);

        try {
            application.setPersonOidChecked(System.currentTimeMillis());
            application.setPersonOid(this.authenticationService.addPerson(personBuilder.get()));
        } catch (GenericFault fail) {
            LOGGER.info(fail.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return application;
    }

    @Override
    public Application addPersonOid(String applicationOid) {
        DBObject query = QueryBuilder.start("oid").is(applicationOid).get();
        List<Application> applications = applicationDAO.find(query);
        return addPersonOid(applications.get(0));
    }

    @Override
    public Application checkStudentOid(String applicationOid) {
        DBObject query = QueryBuilder.start("oid").is(applicationOid).get();
        List<Application> applications = applicationDAO.find(query);
        return checkStudentOid(applications.get(0));
    }

    @Override
    public Application checkStudentOid(Application application) {
        String personOid = application.getPersonOid();

        if (isEmpty(personOid)) {
            application = addPersonOid(application);
            personOid = application.getPersonOid();
        }

        String studentOid = application.getStudentOid();

        if (isNotEmpty(personOid) && isEmpty(studentOid)) {
            studentOid = authenticationService.checkStudentOid(application.getPersonOid());
            application.setStudentOid(studentOid);
        }

        application.setStudentOidChecked(System.currentTimeMillis());
        applicationDAO.save(application);
        return application;
    }

    @Override
    public Application passivateApplication(String applicationOid) {
        Application query = new Application();
        query.setOid(applicationOid);
        List<Application> apps = applicationDAO.find(query);
        Application application = apps.get(0);
        application.passivate();
        Application queryApplication = new Application(applicationOid);
        applicationDAO.update(queryApplication, application);
        return application;
    }

    @Override
    public Application activateApplication(String applicationOid) {
        Application query = new Application();
        query.setOid(applicationOid);
        List<Application> apps = applicationDAO.find(query);
        Application application = apps.get(0);
        application.activate();
        Application queryApplication = new Application(applicationOid);
        applicationDAO.update(queryApplication, application);
        return application;
    }

    @Override
    public void addNote(final Application application, final String noteText, final boolean persist) {
        addNoteToApplicationObject(application, noteText);
        if (persist) {
            Application query = new Application(application.getOid());
            applicationDAO.update(query, application);
        }
    }

    private void addNoteToApplicationObject(final Application application, final String noteText) {
        application.addNote(new ApplicationNote(noteText, new Date(), userSession.getUser().getUserName()));
    }

    @Override
    public Application getSubmittedApplication(final String applicationSystemId, final String oid) {
        Application submittedApplication = userSession.getSubmittedApplication();
        if (submittedApplication != null &&
                submittedApplication.getApplicationSystemId().equals(applicationSystemId) &&
                submittedApplication.getOid().equals(oid)) {
            return submittedApplication;
        }
        throw new ResourceNotFoundExceptionRuntime("Could not found submitted application");
    }

    @Override
    public Application getPendingApplication(String applicationSystemId, String oid) throws ResourceNotFoundException {
        final User user = userSession.getUser();
        Application application = new Application(applicationSystemId, user, oid);
        if (!user.isKnown()) {
            application.removeUser();
        }

        List<Application> listOfApplications = applicationDAO.find(application);
        return listOfApplications.get(0);
    }

    @Override
    public List<Application> getApplicationsByApplicationOption(List<String> applicationOptionIds) {
        return applicationDAO.findByApplicationOption(applicationOptionIds);
    }

    @Override
    public Application getApplication(final String applicationSystemId) {
        User user = userSession.getUser();
        if (user.isKnown()) {
            Application application = new Application(applicationSystemId, userSession.getUser());
            List<Application> listOfApplications = applicationDAO.find(application);
            if (listOfApplications.isEmpty() || listOfApplications.size() > 1) {
                return application;
            }
            return listOfApplications.get(0);
        } else {
            return userSession.getApplication(applicationSystemId);

        }
    }

    @Override
    public Application getApplicationByOid(String oid) throws ResourceNotFoundException {
        return getApplication(new Application(oid));
    }

    @Override
    public ApplicationSearchResultDTO findApplications(final String term,
                                                       final ApplicationQueryParameters applicationQueryParameters) {
        return applicationDAO.findAllQueried(term, applicationQueryParameters);
    }

    @Override
    public void saveApplicationAdditionalInfo(String oid, Map<String, String> additionalInfo)
            throws ResourceNotFoundException {
        Application query = new Application(oid);
        Application current = getApplication(query);
        current.setAdditionalInfo(additionalInfo);
        applicationDAO.update(query, current);
    }

    @Override
    public List<String> getApplicationPreferenceOids(String applicationOid) throws ResourceNotFoundException {
        Application application = getApplication(applicationOid);
        return getApplicationPreferenceOids(application);
    }

    @Override
    public List<String> getApplicationPreferenceOids(Application application) {
        List<String> oids = new ArrayList<String>();
        final Form activeForm = formService.getForm(application.getApplicationSystemId());
        Map<String, PreferenceRow> preferenceRows = ElementUtil.findElementsByType(activeForm,
                PreferenceRow.class);
        Map<String, String> answers = application.getVastauksetMerged();
        for (PreferenceRow pr : preferenceRows.values()) {
            String oid = answers.get(pr.getEducationOidInputId());
            if (oid != null && !oid.trim().isEmpty()) {
                oids.add(oid);
            }
        }
        return oids;
    }

    @Override
    public void update(final Application queryApplication, final Application application) {
        // application.updateFullName();
        this.applicationDAO.update(queryApplication, application);
    }

    @Override
    public String getApplicationKeyValue(String applicationOid, String key) throws ResourceNotFoundException {
        Application application = getApplication(new Application(applicationOid));
        if (application.getAdditionalInfo().containsKey(key)) {
            return application.getAdditionalInfo().get(key);
        } else if (application.getVastauksetMerged().containsKey(key)) {
            return application.getVastauksetMerged().get(key);
        } else {
            throw new ResourceNotFoundException(String.format("Could not find application : %s value of key : %s",
                    applicationOid, key));
        }
    }

    @Override
    public void putApplicationAdditionalInfoKeyValue(String applicationOid, String key, String value)
            throws ResourceNotFoundException {
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null");
        } else {
            applicationDAO.updateKeyValue(applicationOid, "additionalInfo." + key, value);
        }
    }

    @Override
    public Application fillLOPChain(final Application application, final boolean save) {
        String[] ids = new String[]{
                "preference1-Opetuspiste-id",
                "preference2-Opetuspiste-id",
                "preference3-Opetuspiste-id",
                "preference4-Opetuspiste-id",
                "preference5-Opetuspiste-id"};

        Map<String, String> hakutoiveet = application.getAnswers().get("hakutoiveet");
        if (hakutoiveet != null) {
            HashMap<String, String> answers = new HashMap<String, String>(hakutoiveet);
            for (String id : ids) {
                String opetuspiste = answers.get(id);
                if (isNotEmpty(opetuspiste)) {
                    List<String> parentOids = organizationService.findParentOids(opetuspiste);
                    // OPH-guys have access to all organizations
                    parentOids.add(OPH_ORGANIZATION);
                    // Also add organization itself
                    parentOids.add(opetuspiste);
                    answers.put(id + "-parents", join(parentOids, ","));
                }
            }
            application.addVaiheenVastaukset("hakutoiveet", answers);
            if (save) {
                this.applicationDAO.save(application);
            }
        }
        return application;
    }

    @Override
    public Application getNextWithoutPersonOid() {
        Application application = applicationDAO.getNextWithoutPersonOid();
        if (application != null) {
            application.setPersonOidChecked(System.currentTimeMillis());
            applicationDAO.save(application);
        }
        return application;
    }

    @Override
    public Application getNextWithoutStudentOid() {
        Application application = applicationDAO.getNextWithoutStudentOid();
        if (application != null) {
            application.setStudentOidChecked(System.currentTimeMillis());
            applicationDAO.save(application);
        }
        return application;
    }

    @Override
    public Application officerCreateNewApplication(String asId) {
        Application application = new Application();
        application.setApplicationSystemId(asId);
        application.setReceived(new Date());
        application.setState(Application.State.INCOMPLETE);
        addNote(application, "Hakemus vastaanotettu", false);
        application.setOid(applicationOidService.generateNewOid());
        this.applicationDAO.save(application);
        return application;
    }

    private Application getApplication(final Application queryApplication) throws ResourceNotFoundException {

        LOGGER.debug("Entering ApplicationServiceImpl.getApplication()");
        List<Application> listOfApplications = applicationDAO.find(queryApplication);
        if (listOfApplications.isEmpty()) {
            throw new ResourceNotFoundException("Could not find application " + queryApplication.getOid());
        }
        if (listOfApplications.size() > 1) {
            throw new ResourceNotFoundException("Found multiple applications with oid " + queryApplication.getOid());
        }

        Application application = listOfApplications.get(0);

        if (!hakuPermissionService.userCanReadApplication(application)) {
            throw new ResourceNotFoundException("User is not allowed to read application " + application.getOid());
        }
        return application;
    }


}
