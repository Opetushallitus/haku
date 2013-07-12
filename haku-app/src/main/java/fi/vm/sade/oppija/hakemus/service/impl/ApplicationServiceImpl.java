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
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import fi.vm.sade.authentication.service.GenericFault;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import fi.vm.sade.oppija.common.valintaperusteet.ValintaperusteetService;
import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

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
    private static final String SOCIAL_SECURITY_NUMBER_PATTERN = "([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))";
    private static final String DATE_OF_BIRTH_PATTERN = "[0-9]{6}";
    private static final String OID_PATTERN = "^[0-9]+.[0-9]+.[0-9]+.[0-9]+.[0-9]+.[0-9]+$";
    private static final String SHORT_OID_PATTERN = "^[0-9]{11}$";
    private final Pattern socialSecurityNumberPattern;
    private final Pattern dobPattern;
    private final Pattern oidPattern;
    private final Pattern shortOidPattern;
    private final AuthenticationService authenticationService;

    @Autowired
    ValintaperusteetService valintaperusteetService;

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                  final UserHolder userHolder, @Qualifier("formServiceImpl") final FormService formService,
                                  @Qualifier("applicationOidServiceImpl") ApplicationOidService applicationOidService,
                                  AuthenticationService authenticationService) {

        this.applicationDAO = applicationDAO;
        this.userHolder = userHolder;
        this.formService = formService;
        this.authenticationService = authenticationService;
        this.socialSecurityNumberPattern = Pattern.compile(SOCIAL_SECURITY_NUMBER_PATTERN);
        this.dobPattern = Pattern.compile(DATE_OF_BIRTH_PATTERN);
        this.oidPattern = Pattern.compile(OID_PATTERN);
        this.shortOidPattern = Pattern.compile(SHORT_OID_PATTERN);
        this.applicationOidService = applicationOidService;
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

    private ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase, Application application,
                                                  boolean skipValidators) {
        final ApplicationState applicationState = new ApplicationState(application, applicationPhase.getPhaseId());
        final String applicationPeriodId = applicationState.getHakemus().getFormId().getApplicationPeriodId();
        final String formId = applicationState.getHakemus().getFormId().getFormId();
        final Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        final Element phase = activeForm.getPhase(applicationPhase.getPhaseId());
        final Map<String, String> vastaukset = applicationPhase.getAnswers();

        Map<String, String> allAnswers = new HashMap<String, String>();
        // if the current phase has previous phase, get all the answers for
        // validating rules
        if (!activeForm.isFirstChild(phase)) {
            Application current = userHolder.getApplication(applicationState.getHakemus().getFormId());
            allAnswers.putAll(current.getVastauksetMerged());
        }
        allAnswers.putAll(vastaukset);

        if (!skipValidators) {
            ValidationResult validationResult = ElementTreeValidator.validate(phase, allAnswers);
            if (application.getOid() == null) {
                validationResult = checkIfExistsBySocialSecurityNumber(applicationPeriodId,
                        vastaukset.get(SocialSecurityNumber.HENKILOTUNNUS), validationResult);
            }
            applicationState.addError(validationResult.getErrorMessages());
        }
        if (applicationState.isValid()) {
            if (application.getOid() == null) {
                checkIfExistsBySocialSecurityNumber(applicationPeriodId,
                        vastaukset.get(SocialSecurityNumber.HENKILOTUNNUS));
            }
            this.userHolder.savePhaseAnswers(applicationPhase);
            this.applicationDAO.tallennaVaihe(applicationState);
        }
        // sets all answers merged, needed for re-rendering view if errors
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
        Application application = userHolder.getApplication(formId);
        //Application application = applicationDAO.findDraftApplication(application1);
        Form form = formService.getForm(formId.getApplicationPeriodId(), formId.getFormId());
        Map<String, String> allAnswers = application.getVastauksetMerged();
        ValidationResult validationResult = ElementTreeValidator.validate(form, allAnswers);
        validationResult = checkIfExistsBySocialSecurityNumber(formId.getApplicationPeriodId(),
                allAnswers.get(SocialSecurityNumber.HENKILOTUNNUS), validationResult);
        if (!validationResult.hasErrors()) {

            application.setOid(applicationOidService.generateNewOid());
            if (!user.isKnown()) {
                application.removeUser();
            }

            application.resetUser();
            application.setReceived(new Date());
            application.addNote(new ApplicationNote("Hakemus vastaanotettu", new Date(), user));
            this.applicationDAO.save(application);
            this.userHolder.removeApplication(application.getFormId());
            return application.getOid();
        } else {
            throw new IllegalStateException("Could not send the application ");
        }
    }

    @Override
    public Application addPersonAndAuthenticate(Application application) {
        Map<String, String> allAnswers = application.getVastauksetMerged();
        // create student id for finnish applicants

        if (allAnswers.get(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER) != null) {

            // invoke authentication service to obtain oid
            Person person = new Person(allAnswers.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_NICKNAME),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_LAST_NAME),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER), false,
                    allAnswers.get(OppijaConstants.ELEMENT_ID_EMAIL),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_SEX),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_HOME_CITY), false,
                    allAnswers.get(OppijaConstants.ELEMENT_ID_LANGUAGE),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_NATIONALITY),
                    allAnswers.get(OppijaConstants.ELEMENT_ID_FIRST_LANGUAGE));

            try {
                application.setPersonOid(this.authenticationService.addPerson(person));
            } catch (GenericFault fail) {
                LOGGER.info(fail.getMessage());
            }
        }

        application.activate();
        this.applicationDAO.save(application);
        return application;
    }
    @Override
    public Application addPersonAndAuthenticate(String applicationOid) {
        DBObject query = QueryBuilder.start("oid").is(applicationOid).get();
        List<Application> applications = applicationDAO.find(query);
        return addPersonAndAuthenticate(applications.get(0));
    }

    @Override
    public Application passivateApplication(String applicationOid) {
        DBObject query = QueryBuilder.start("oid").is(applicationOid).get();
        List<Application> applications = applicationDAO.find(query);
        Application application = applications.get(0);
        application.passivate();
        applicationDAO.save(application);
        return application;
    }

    @Override
    public void addNote(Application application, String noteText, User user) {
        application.addNote(new ApplicationNote(noteText, new Date(), user));
        applicationDAO.save(application);
    }

    @Override
    public Application getPendingApplication(FormId formId, String oid) throws ResourceNotFoundException {
        final User user = userHolder.getUser();
        Application application = new Application(formId, user, oid);
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
    public List<Application> getApplicationsByApplicationSystemAndApplicationOption(String asId, String aoId) {
        return applicationDAO.findByApplicationSystemAndApplicationOption(asId, aoId);
    }

    @Override
    public List<Application> getApplicationsByApplicationOption(List<String> applicationOptionIds) {
        return applicationDAO.findByApplicationOption(applicationOptionIds);
    }

    @Override
    public Application getApplication(final FormId formId) {
        User user = userHolder.getUser();
        if (user.isKnown()) {
            Application application = new Application(formId, userHolder.getUser());
            List<Application> listOfApplications = applicationDAO.find(application);
            if (listOfApplications.isEmpty() || listOfApplications.size() > 1) {
                return application;
            }
            return listOfApplications.get(0);
        } else {
            return userHolder.getApplication(formId);

        }
    }

    @Override
    public List<Application> findApplications(final String term,
                                              final ApplicationQueryParameters applicationQueryParameters) {
        List<Application> applications = new LinkedList<Application>();
        if (shortOidPattern.matcher(term).matches()) {
            applications.addAll(applicationDAO.findByOid(term, applicationQueryParameters));
        } else if (oidPattern.matcher(term).matches()) {
            if (term.startsWith(applicationOidService.getOidPrefix())) {
                applications.addAll(applicationDAO.findByApplicationOid(term, applicationQueryParameters));
            } else {
                applications.addAll(applicationDAO.findByUserOid(term, applicationQueryParameters));
            }
        } else if (socialSecurityNumberPattern.matcher(term).matches()) {
            applications.addAll(applicationDAO.findByApplicantSsn(term, applicationQueryParameters));
        } else if (dobPattern.matcher(term).matches()) {
            applications.addAll(applicationDAO.findByApplicantDob(term, applicationQueryParameters));
        } else if (!StringUtils.isEmpty(term)) {
            applications.addAll(applicationDAO.findByApplicantName(term, applicationQueryParameters));
        } else if (isEmpty(term)) {
            applications.addAll(applicationDAO.findAllFiltered(applicationQueryParameters));
        }
        return applications;
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
        FormId formId = application.getFormId();
        final Form activeForm = formService.getActiveForm(formId.getApplicationPeriodId(), formId.getFormId());
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
        Application application = getApplication(applicationOid);
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
        Application query = new Application(applicationOid);
        Application application = getApplication(query);
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null");
        } else if (application.getVastauksetMerged().containsKey(key)) {
            throw new IllegalStateException(String.format(
                    "Key of the given additional information is found on the application form : key %s", key));
        } else {
            application.getAdditionalInfo().put(key, value);
            applicationDAO.update(query, application);
        }
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
    public Application officerCreateNewApplication(String asId) {
        ApplicationPeriod as = formService.getApplicationPeriodById(asId);
        FormId formId = new FormId(asId, as.getFormIds().iterator().next());
        Application application = new Application();
        application.setFormId(formId);
        application.setReceived(new Date());
        application.setState(Application.State.INCOMPLETE);
        final User user = userHolder.getUser();
        application.addNote(new ApplicationNote("Hakemus vastaanotettu", new Date(), user));
        application.setOid(applicationOidService.generateNewOid());
        this.applicationDAO.save(application);
        return application;
    }

    private Application getApplication(final Application application) throws ResourceNotFoundException {

        List<Application> listOfApplications = applicationDAO.find(application);
        if (listOfApplications.isEmpty() || listOfApplications.size() > 1) {
            throw new ResourceNotFoundException("Could not find application " + application.getOid());
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

    private ValidationResult checkIfExistsBySocialSecurityNumber(String asId, String ssn,
                                                                 ValidationResult validationResult) {
        if (validationResult == null) {
            validationResult = new ValidationResult();
        }
        if (ssn != null) {
            Matcher matcher = socialSecurityNumberPattern.matcher(ssn);
            if (matcher.matches() && this.applicationDAO.checkIfExistsBySocialSecurityNumber(asId, ssn)) {
                ValidationResult result = new ValidationResult("Henkilotunnus",
                        ElementUtil.createI18NTextError("henkilotiedot.hetuKaytetty"));
                return new ValidationResult(Arrays.asList(new ValidationResult[]{validationResult, result}));
            }
        }
        return validationResult;
    }

}
