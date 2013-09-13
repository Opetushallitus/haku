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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import fi.vm.sade.authentication.service.GenericFault;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.PersonBuilder;
import fi.vm.sade.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.util.ElementTree;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.oppija.ui.HakuPermissionService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.join;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private static final String OPH_ORGANIZATION = "1.2.246.562.10.00000000001";
    private final ApplicationDAO applicationDAO;
    private final ApplicationOidService applicationOidService;
    private final UserHolder userHolder;
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
    private final ElementTreeValidator elementTreeValidator;

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                  final UserHolder userHolder,
                                  @Qualifier("formServiceImpl") final FormService formService,
                                  @Qualifier("applicationOidServiceImpl") ApplicationOidService applicationOidService,
                                  AuthenticationService authenticationService,
                                  OrganizationService organizationService,
                                  HakuPermissionService hakuPermissionService,
                                  ElementTreeValidator elementTreeValidator) {

        this.applicationDAO = applicationDAO;
        this.userHolder = userHolder;
        this.formService = formService;
        this.applicationOidService = applicationOidService;
        this.authenticationService = authenticationService;
        this.organizationService = organizationService;
        this.hakuPermissionService = hakuPermissionService;
        this.elementTreeValidator = elementTreeValidator;

        this.socialSecurityNumberPattern = Pattern.compile(SOCIAL_SECURITY_NUMBER_PATTERN);
        this.dobPattern = Pattern.compile(DATE_OF_BIRTH_PATTERN);
        this.oidPattern = Pattern.compile(OID_PATTERN);
        this.shortOidPattern = Pattern.compile(SHORT_OID_PATTERN);
    }


    @Override
    public ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase, boolean skipValidators) {
        final Application application = new Application(this.userHolder.getUser(), applicationPhase);
        return saveApplicationPhase(applicationPhase, application, skipValidators);
    }

    @Override
    public ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase, String oid, boolean skipValidators) {
        final Application application = new Application(oid, applicationPhase);
        return saveApplicationPhase(applicationPhase, application, skipValidators);
    }

    private ApplicationState saveApplicationPhase(final ApplicationPhase applicationPhase,
                                                  final Application application,
                                                  final boolean skipValidators) {
        final ApplicationState applicationState = new ApplicationState(application, applicationPhase.getPhaseId());
        final String applicationSystemId = applicationState.getApplication().getApplicationSystemId();
        final Form activeForm = formService.getActiveForm(applicationSystemId);
        ElementTree elementTree = new ElementTree(activeForm);
        final Element phase = elementTree.getChildById(applicationPhase.getPhaseId());
        final Map<String, String> vastaukset = applicationPhase.getAnswers();

        Map<String, String> allAnswers = new HashMap<String, String>();
        // if the current phase has previous phase, get all the answers for
        // validating rules
        if (!elementTree.isFirstChild(phase)) {
            Application current = userHolder.getApplication(applicationSystemId);
            allAnswers.putAll(current.getVastauksetMergedIgnoringPhase(applicationPhase.getPhaseId()));
        }
        allAnswers.putAll(vastaukset);

        if (!skipValidators) {
            ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(phase, allAnswers,
                    application.getOid(), applicationSystemId));
            applicationState.addError(validationResult.getErrorMessages());
        }
        if (applicationState.isValid()) {
            this.userHolder.savePhaseAnswers(applicationPhase);
        }
        // sets all answers merged, needed for re-rendering view if errors
        applicationState.setAnswersMerged(allAnswers);
        return applicationState;
    }

    @Override
    public String submitApplication(final String applicationSystemId) {
        final User user = userHolder.getUser();
        Application application = userHolder.getApplication(applicationSystemId);
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
            addNote(application, "Hakemus vastaanotettu");
            this.applicationDAO.save(application);
            this.userHolder.removeApplication(application.getApplicationSystemId());
            return application.getOid();
        } else {
            throw new IllegalStateException("Could not send the application ");
        }
    }

    @Override
    public Application addPersonOid(Application application) {
        Map<String, String> allAnswers = application.getVastauksetMerged();

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
            application.setPersonOid(this.authenticationService.addPerson(personBuilder.get()));
        } catch (GenericFault fail) {
            LOGGER.info(fail.getMessage());
        }

        application.activate();
        this.applicationDAO.save(application);
        return application;
    }

    @Override
    public Application addPersonOid(String applicationOid) {
        DBObject query = QueryBuilder.start("oid").is(applicationOid).get();
        List<Application> applications = applicationDAO.find(query);
        return addPersonOid(applications.get(0));
    }

    @Override
    public Application addStudentOid(String applicationOid) {
        DBObject query = QueryBuilder.start("oid").is(applicationOid).get();
        List<Application> applications = applicationDAO.find(query);
        return addStudentOid(applications.get(0));
    }

    @Override
    public Application addStudentOid(Application application) {
        String studentOid = authenticationService.getStudentOid(application.getPersonOid());
        if (studentOid != null) {
            application.setStudentOid(studentOid);
        }
        return null;
    }

    @Override
    public Application passivateApplication(String applicationOid) {
        DBObject query = QueryBuilder.start("oid").is(applicationOid).get();
        List<Application> applications = applicationDAO.find(query);
        Application application = applications.get(0);
        application.passivate();
        Application queryApplication = new Application(applicationOid);
        applicationDAO.update(queryApplication, application);
        return application;
    }

    @Override
    public void addNote(Application application, String noteText) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = "[not authenticated]";
        if (principal != null) {
            if (UserDetails.class.isAssignableFrom(principal.getClass())) {
                UserDetails userDetails = (UserDetails) principal;
                user = userDetails.getUsername();
            } else {
                user = principal.toString();
            }
        }
        application.addNote(new ApplicationNote(noteText, new Date(), user));
        Application query = new Application(application.getOid());
        applicationDAO.update(query, application);
    }

    @Override
    public Application getPendingApplication(String applicationSystemId, String oid) throws ResourceNotFoundException {
        final User user = userHolder.getUser();
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
        User user = userHolder.getUser();
        if (user.isKnown()) {
            Application application = new Application(applicationSystemId, userHolder.getUser());
            List<Application> listOfApplications = applicationDAO.find(application);
            if (listOfApplications.isEmpty() || listOfApplications.size() > 1) {
                return application;
            }
            return listOfApplications.get(0);
        } else {
            return userHolder.getApplication(applicationSystemId);

        }
    }

    @Override
    public Application getApplicationByOid(String oid) throws ResourceNotFoundException {
        return getApplication(new Application(oid));
    }

    @Override
    public ApplicationSearchResultDTO findApplications(final String term,
                                                       final ApplicationQueryParameters applicationQueryParameters) {
        if (shortOidPattern.matcher(term).matches()) {
            return applicationDAO.findByOid(term, applicationQueryParameters);
        } else if (oidPattern.matcher(term).matches()) {
            if (term.startsWith(applicationOidService.getOidPrefix())) {
                return applicationDAO.findByApplicationOid(term, applicationQueryParameters);
            } else {
                return applicationDAO.findByUserOid(term, applicationQueryParameters);
            }
        } else if (socialSecurityNumberPattern.matcher(term).matches()) {
            return applicationDAO.findByApplicantSsn(term, applicationQueryParameters);
        } else if (dobPattern.matcher(term).matches()) {
            return applicationDAO.findByApplicantDob(term, applicationQueryParameters);
        } else if (!StringUtils.isEmpty(term)) {
            return applicationDAO.findByApplicantName(term, applicationQueryParameters);
        } else if (isEmpty(term)) {
            LOGGER.debug("Find all applications, empty term");
            ApplicationSearchResultDTO ret = applicationDAO.findAllFiltered(applicationQueryParameters);
            LOGGER.debug("Found {} results", ret.getResults().size());
            return ret;
        }
        return new ApplicationSearchResultDTO(0, null);
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
    public Application fillLOPChain(Application application) {
        String[] ids = new String[]{
                "preference1-Opetuspiste-id",
                "preference2-Opetuspiste-id",
                "preference3-Opetuspiste-id",
                "preference4-Opetuspiste-id",
                "preference5-Opetuspiste-id"};

        HashMap<String, String> answers = new HashMap<String, String>(application.getAnswers().get("hakutoiveet"));
        for (String id : ids) {
            String opetuspiste = answers.get(id);
            if (!isEmpty(opetuspiste)) {
                List<String> parentOids = organizationService.findParentOids(opetuspiste);
                // OPH-guys have access to all organizations
                parentOids.add(OPH_ORGANIZATION);
                // Also add organization itself
                parentOids.add(opetuspiste);
                answers.put(id + "-parents", join(parentOids, ","));
            }
        }
        application.addVaiheenVastaukset("hakutoiveet", answers);
        this.applicationDAO.save(application);
        return application;
    }

    @Override
    public Application getNextWithoutPersonOid() {
        BasicDBObject query = new BasicDBObject();
        query.put("personOid", new BasicDBObject("$exists", false));
        query.put("oid", new BasicDBObject("$exists", true));
        query.put("state", new BasicDBObject("$exists", false));
        List<Application> apps = applicationDAO.find(query);
        if (apps.size() > 0) {
            return apps.get(0);
        }
        return null;
    }

    @Override
    public Application getNextWithoutStudentOid() {
        BasicDBObject query = new BasicDBObject();
        query.put("studentOid", new BasicDBObject("$exists", false));
        query.put("oid", new BasicDBObject("$exists", true));
        query.put("state", new BasicDBObject("$exists", true));

        List<Application> apps = applicationDAO.find(query);
        if (apps.size() > 0) {
            return apps.get(0);
        }
        return null;
    }

    @Override
    public Application officerCreateNewApplication(String asId) {
        Application application = new Application();
        application.setApplicationSystemId(asId);
        application.setReceived(new Date());
        application.setState(Application.State.INCOMPLETE);
        addNote(application, "Hakemus vastaanotettu");
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
