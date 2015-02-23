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

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.AuthorizationMeta;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.oppija.hakemus.domain.util.AttachmentUtil;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationFilterParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationFilterParametersBuilder;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionAttachmentDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.OrganizationGroupDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static fi.vm.sade.haku.oppija.hakemus.service.ApplicationModelUtil.removeAuthorizationMeta;
import static fi.vm.sade.haku.oppija.hakemus.service.ApplicationModelUtil.restoreV0ModelLOPParentsToApplicationMap;
import static fi.vm.sade.haku.oppija.lomake.util.StringUtil.safeToString;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final ApplicationDAO applicationDAO;
    private final ApplicationOidService applicationOidService;
    private final UserSession userSession;
    private final FormService formService;
    private final AuthenticationService authenticationService;
    private final OrganizationService organizationService;
    private final HakuPermissionService hakuPermissionService;
    private final ApplicationSystemService applicationSystemService;
    private final KoulutusinformaatioService koulutusinformaatioService;
    private final I18nBundleService i18nBundleService;
    private final ElementTreeValidator elementTreeValidator;
    // Tee vain background-validointi tälle lomakkeelle
    private final String onlyBackgroundValidation;

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                  final UserSession userSession,
                                  @Qualifier("formServiceImpl") final FormService formService,
                                  @Qualifier("applicationOidServiceImpl") ApplicationOidService applicationOidService,
                                  AuthenticationService authenticationService,
                                  OrganizationService organizationService,
                                  HakuPermissionService hakuPermissionService,
                                  ApplicationSystemService applicationSystemService,
                                  KoulutusinformaatioService koulutusinformaatioService,
                                  I18nBundleService i18nBundleService,
                                  ElementTreeValidator elementTreeValidator,
                                  @Value("${onlyBackgroundValidation}") String onlyBackgroundValidation) {

        this.applicationDAO = applicationDAO;
        this.userSession = userSession;
        this.formService = formService;
        this.applicationOidService = applicationOidService;
        this.authenticationService = authenticationService;
        this.organizationService = organizationService;
        this.hakuPermissionService = hakuPermissionService;
        this.applicationSystemService = applicationSystemService;
        this.koulutusinformaatioService = koulutusinformaatioService;
        this.i18nBundleService = i18nBundleService;
        this.elementTreeValidator = elementTreeValidator;
        this.onlyBackgroundValidation = onlyBackgroundValidation;
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

        final ApplicationState applicationState = new ApplicationState(application, applicationPhase.getPhaseId(), allAnswers);

        if (elementTree.isValidationNeeded(applicationPhase.getPhaseId(), application.getPhaseId())) {
            ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(phase, allAnswers,
                    application.getOid(), applicationSystemId, resolveValidationContext(applicationSystemId)));
            applicationState.addError(validationResult.getErrorMessages());
        }

        if (applicationState.isValid()) {
            this.userSession.savePhaseAnswers(applicationPhase);
        }
        return applicationState;
    }

    private ValidationInput.ValidationContext resolveValidationContext(String asId) {
        ValidationInput.ValidationContext validationContext = ValidationInput.ValidationContext.applicant_submit;
        if (asId.equals(onlyBackgroundValidation)) {
            validationContext = ValidationInput.ValidationContext.background;
        }
        return validationContext;
    }

    @Override
    public Application submitApplication(final String applicationSystemId, String language) {
        final User user = userSession.getUser();

        Application application = null;
        if(userSession.hasApplication(applicationSystemId)) {
            application = userSession.getApplication(applicationSystemId);
        } else {
            LOGGER.error("Trying to submit application but no application was found from session. ApplicationSystemId: " + applicationSystemId);
            throw new IllegalStateException("Trying to submit application but no application was found from session. ApplicationSystemId: " + applicationSystemId);
        }

        ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(applicationSystemId);
        Form form = applicationSystem.getForm();
        Map<String, String> allAnswers = application.getVastauksetMerged();
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, allAnswers,
                application.getOid(), applicationSystemId, resolveValidationContext(applicationSystemId)));

        if (!validationResult.hasErrors()) {

            application.setOid(applicationOidService.generateNewOid());
            if (!user.isKnown()) {
                application.removeUser();
            }
            application.resetUser();
            application.setReceived(new Date());
            application.addNote(new ApplicationNote("Hakemus vastaanotettu", new Date(), userSession.getUser().getUserName()));
            application.setLastAutomatedProcessingTime(System.currentTimeMillis());
            application.submitted();
            application.flagStudentIdentificationRequired();
            application.addMeta(Application.META_FILING_LANGUAGE, language);
            application = updatePreferenceBasedData(application);
            this.applicationDAO.save(application);
            this.userSession.removeApplication(application);
            return application;
        } else {
            LOGGER.error("Could not send the application | " + getApplicationLogMessage(application, validationResult));
            throw new IllegalStateException("Could not send the application ");
        }
    }

    private String getApplicationLogMessage(Application application, ValidationResult validationResult) {

        if(application == null) return "Application was null.";

        StringBuilder sb = new StringBuilder();
        sb.append("Hakemus: ").append(application.getOid());
        sb.append("\r\n").append(application.getAnswers().toString());
        if(validationResult != null) {
            sb.append("\r\n").append(validationResult.getErrorMessages());
        }

        return sb.toString();

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
                .setLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_LANGUAGE))
                .setNationality(allAnswers.get(OppijaConstants.ELEMENT_ID_NATIONALITY))
                .setContactLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE))
                .setSocialSecurityNumber(allAnswers.get(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER))
                .setNoSocialSecurityNumber(!Boolean.valueOf(allAnswers.get(OppijaConstants.ELEMENT_ID_HAS_SOCIAL_SECURITY_NUMBER)))
                .setDateOfBirth(allAnswers.get(OppijaConstants.ELEMENT_ID_DATE_OF_BIRTH))
                .setPersonOid(application.getPersonOid())
                .setSecurityOrder(false);

        application.setLastAutomatedProcessingTime(System.currentTimeMillis());
        Person personBefore = personBuilder.get();
        LOGGER.debug("Calling addPerson");
        try {
            Person personAfter = authenticationService.addPerson(personBefore);
            LOGGER.debug("Called addPerson");
            LOGGER.debug("Calling modifyPersonalData");
            application = application.modifyPersonalData(personAfter);
            LOGGER.debug("Called modifyPersonalData");
        } catch (Throwable t) {
            LOGGER.debug("Unexpected happened: ", t);
        }
        return application;
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
            Person person = authenticationService.checkStudentOid(application.getPersonOid());
            application.modifyPersonalData(person);
            if (!isEmpty(person.getStudentOid())) {
                application.studentIdentificationDone();
            }
        }

        application.setLastAutomatedProcessingTime(System.currentTimeMillis());
        applicationDAO.save(application);
        return application;
    }

    @Override
    public Application getSubmittedApplication(final String applicationSystemId, final String oid) {
        Application submittedApplication = userSession.getSubmittedApplication();
        if (submittedApplication != null &&
                submittedApplication.getApplicationSystemId().equals(applicationSystemId) &&
                submittedApplication.getOid().equals(oid)) {
            return submittedApplication;
        }
        throw new ResourceNotFoundException("Could not found submitted application");
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
    public Application getApplicationByOid(String oid) {
        return getApplication(new Application(oid));
    }

    @Override
    public ApplicationSearchResultDTO findApplications(final ApplicationQueryParameters applicationQueryParameters) {
        return applicationDAO.findAllQueried(applicationQueryParameters,
                buildFilterParams(applicationQueryParameters));
    }

    @Override
    public List<Map<String, Object>> findFullApplications(final ApplicationQueryParameters applicationQueryParameters) {

        List<Map<String, Object>> applications = applicationDAO.findAllQueriedFull(applicationQueryParameters,
                buildFilterParams(applicationQueryParameters));
        for (Map<String, Object> application : applications) {
            restoreV0ModelLOPParentsToApplicationMap(application);
            removeAuthorizationMeta(application);
        }
        return applications;
    }

    private ApplicationFilterParameters buildFilterParams(final ApplicationQueryParameters applicationQueryParameters) {
        List<ApplicationSystem> ass = applicationSystemService.getAllApplicationSystems(
                "maxApplicationOptions", "kohdejoukkoUri", "hakutapa");
        int max = 0;
        String kohdejoukko = null;
        String hakutapa = null;
        List<String> queriedAss = applicationQueryParameters.getAsIds();
        for (ApplicationSystem as : ass) {
            if (queriedAss.isEmpty() || queriedAss.contains(as.getId())) {
                kohdejoukko = as.getKohdejoukkoUri();
                hakutapa = as.getHakutapa();
                if (as.getMaxApplicationOptions() > max) {
                    max = as.getMaxApplicationOptions();
                }
            }
        }

        ApplicationFilterParametersBuilder builder = new ApplicationFilterParametersBuilder()
                .addOrganizationsReadable(hakuPermissionService.userCanReadApplications())
                .addOrganizationsOpo(hakuPermissionService.userHasOpoRole())
                .addOrganizationsHetuttomienKasittely(hakuPermissionService.userHasHetuttomienKasittelyRole())
                .setMaxApplicationOptions(max);
        if (queriedAss != null && queriedAss.size() == 1) {
            builder.setKohdejoukko(kohdejoukko);
            builder.setHakutapa(hakutapa);
        }
        return builder.build();
    }

    @Override
    public Application updateAuthorizationMeta(Application application) throws IOException {
        boolean opoAllowed = resolveOpoAllowed(application);
        Map<String, Set<String>> aoOrganizations = new HashMap<String, Set<String>>();
        Set<String> allOrganizations = new HashSet<String>();
        Set<String> sendingSchool = new HashSet<String>();

        int i = 1;
        Map<String, String> aoAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        while (true) {
            String aoKey = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (!aoAnswers.containsKey(aoKey)) {
                break;
            }
            String lop = aoAnswers.get(String.format(OppijaConstants.PREFERENCE_ORGANIZATION_ID, i));
            if (isNotEmpty(lop)) {
                List<String> parents = organizationService.findParentOids(lop);
                aoOrganizations.put(String.valueOf(i), new HashSet<String>(parents));
                allOrganizations.addAll(parents);
            }
            i++;
        }

        Map<String, String> educationAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        String sendingSchoolOrg = educationAnswers.get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL);
        if (sendingSchoolOrg != null) {
            sendingSchool.addAll(organizationService.findParentOids(sendingSchoolOrg));
        }

        AuthorizationMeta authorizationMeta = new AuthorizationMeta();
        authorizationMeta.setOpoAllowed(opoAllowed);
        authorizationMeta.setAoOrganizations(aoOrganizations);
        authorizationMeta.setAllAoOrganizations(allOrganizations);
        authorizationMeta.setSendingSchool(sendingSchool);
        application.setAuthorizationMeta(authorizationMeta);
        return application;
    }

    @Override
    public Application updatePreferenceBasedData(final Application application){
        List<String> preferenceAoIds = ApplicationUtil.getPreferenceAoIds(application);

        application.setPreferenceEligibilities(ApplicationUtil.checkAndCreatePreferenceEligibilities(application.getPreferenceEligibilities(), preferenceAoIds));
        application.setPreferencesChecked(ApplicationUtil.checkAndCreatePreferenceCheckedData(application.getPreferencesChecked(), preferenceAoIds));

        ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        application.setAttachmentRequests(AttachmentUtil.resolveAttachmentRequests(applicationSystem, application,
                koulutusinformaatioService, i18nBundleService.getBundle(applicationSystem)));

        return application;
    }

    @Override
    public Application removeOrphanedAnswers(Application application) {
        ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        Form form = as.getForm();
        List<String> elementIds = new ArrayList<String>();
        for (Element element : form.getAllChildren(application.getVastauksetMerged())) {
            elementIds.add(element.getId());
        }
        for (Map.Entry<String, Map<String, String>> phase : application.getAnswers().entrySet()) {
            String phaseId = phase.getKey();
            Map<String, String> newAnswers = new HashMap<String, String>();
            for (Map.Entry<String, String> answer : phase.getValue().entrySet()) {
                String answerKey = answer.getKey();
                if (elementIds.contains(answerKey)
                        || (OppijaConstants.PHASE_APPLICATION_OPTIONS.equals(phaseId) && answerKey.startsWith("preference"))
                        ) {
                    newAnswers.put(answerKey, answer.getValue());
                }
            }
            application.addVaiheenVastaukset(phaseId, newAnswers);
        }
        return application;
    }

    private boolean resolveOpoAllowed(Application application) {
        boolean opoAllowed = true;
        ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        if (as.getKohdejoukkoUri().equals(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU)) {
            opoAllowed = false;
        }
        return opoAllowed;
    }

    @Override
    public void saveApplicationAdditionalInfo(List<ApplicationAdditionalDataDTO> applicationAdditionalData) {
        if (applicationAdditionalData != null) {
            for (ApplicationAdditionalDataDTO data : applicationAdditionalData) {
                saveApplicationAdditionalInfo(data.getOid(), data.getAdditionalData());
            }
        }
    }

    @Override
    public void saveApplicationAdditionalInfo(String oid, Map<String, String> additionalInfo) {
        Application query = new Application(oid);
        Application current = getApplication(query);
        hakuPermissionService.userCanEditApplicationAdditionalData(current);
        current.getAdditionalInfo().putAll(additionalInfo);
        applicationDAO.update(query, current);
    }

    @Override
    public void update(final Application queryApplication, final Application application) {
        this.applicationDAO.update(queryApplication, application);
    }


    @Override
    public String getApplicationKeyValue(String applicationOid, String key) {
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
    public void putApplicationAdditionalInfoKeyValue(String applicationOid, String key, String value) {
        //access application to verify permissions
        getApplication(new Application(applicationOid));
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null");
        } else {
            applicationDAO.updateKeyValue(applicationOid, "additionalInfo." + key, value);
        }
    }

    @Override
    public List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(final String applicationSystemId, final String aoId) {
        ApplicationSystem as = applicationSystemService.getApplicationSystem(applicationSystemId, "maxApplicationOptions");
        ApplicationFilterParametersBuilder builder = new ApplicationFilterParametersBuilder()
                .setMaxApplicationOptions(as.getMaxApplicationOptions())
                .addOrganizationsReadable(hakuPermissionService.userCanReadApplications())
                .addOrganizationsOpo(hakuPermissionService.userHasOpoRole());


        return applicationDAO.findApplicationAdditionalData(applicationSystemId, aoId, builder.build());
    }

    @Override
    public Application officerCreateNewApplication(String asId) {
        Application application = new Application();
        application.setApplicationSystemId(asId);
        application.setReceived(new Date());
        application.setState(Application.State.DRAFT);
        AuthorizationMeta authorizationMeta = new AuthorizationMeta();
        authorizationMeta.setAllAoOrganizations(new HashSet<String>(hakuPermissionService.userCanEnterApplications()));
        application.setAuthorizationMeta(authorizationMeta);
        application.addNote(new ApplicationNote("Hakemus vastaanotettu", new Date(), userSession.getUser().getUserName()));
        application.setOid(applicationOidService.generateNewOid());
        this.applicationDAO.save(application);
        return application;
    }

    @Override
    public Map<String, String> ensureApplicationOptionGroupData(Map<String, String> answers, String lang) {
        LOGGER.debug("Input map: " + answers.toString());
        Set<String> keys = new HashSet<String>(answers.keySet());
        for (String key: keys) {
            if (null != key
              && key.startsWith(OppijaConstants.PREFERENCE_PREFIX)
              && key.endsWith(OppijaConstants.OPTION_ID_POSTFIX)
              && isNotEmpty(answers.get(key))){
                String basekey = key.replace(OppijaConstants.OPTION_ID_POSTFIX, "");
                String aoGroups = answers.get(basekey + OppijaConstants.OPTION_GROUP_POSTFIX);
                String attachmentGroups = answers.get(basekey + OppijaConstants.OPTION_ATTACHMENT_GROUP_POSTFIX);
                String attachments = answers.get(basekey + OppijaConstants.OPTION_ATTACHMENTS_POSTFIX);

                ApplicationOptionDTO applicationOption = koulutusinformaatioService.getApplicationOption(answers.get(key), lang);
                List<String> teachingLangs = applicationOption.getTeachingLanguages();
                String teachingLang = teachingLangs != null && teachingLangs.size() > 0
                        ? teachingLangs.get(0) : "";

                answers.put(basekey + "-Opetuspiste", safeToString(applicationOption.getProvider().getName()));
                answers.put(basekey+"-Opetuspiste-id", safeToString(applicationOption.getProvider().getId()));
                answers.put(basekey+"-Koulutus", safeToString(applicationOption.getName()));
                answers.put(basekey+"-Koulutus-id", safeToString(applicationOption.getId()));
                answers.put(basekey+"-Koulutus-educationDegree", safeToString(applicationOption.getEducationDegree()));
                answers.put(basekey+"-Koulutus-id-sora", String.valueOf(applicationOption.isSora()));
                answers.put(basekey+"-Koulutus-id-lang", safeToString(teachingLang));
                answers.put(basekey+"-Koulutus-id-athlete", String.valueOf(applicationOption.isAthleteEducation()
                        || applicationOption.getProvider().isAthleteEducation()));
                answers.put(basekey+"-Koulutus-id-aoIdentifier", safeToString(applicationOption.getAoIdentifier()));
                answers.put(basekey+"-Koulutus-id-kaksoistutkinto", String.valueOf(applicationOption.isKaksoistutkinto()));
                answers.put(basekey+"-Koulutus-id-vocational", String.valueOf(applicationOption.isVocational()));
                answers.put(basekey+"-Koulutus-id-educationcode", safeToString(applicationOption.getEducationCodeUri()));

                if (isEmpty(aoGroups) || isEmpty(attachmentGroups)) {
                    List<OrganizationGroupDTO> organizationGroups = applicationOption.getOrganizationGroups();
                    if (null != organizationGroups && organizationGroups.size() > 0 ){
                        ArrayList<String> aoGroupList = new ArrayList<String>(organizationGroups.size());
                        ArrayList<String> attachmentGroupList = new ArrayList<String>();
                        for (OrganizationGroupDTO organizationGroup : organizationGroups) {
                            aoGroupList.add(organizationGroup.getOid());
                            if (organizationGroup.getUsageGroups().contains(OppijaConstants.OPTION_ATTACHMENT_GROUP_TYPE)){
                                attachmentGroupList.add(organizationGroup.getOid());
                            }
                        }
                        answers.put(basekey + OppijaConstants.OPTION_GROUP_POSTFIX, StringUtils.join(aoGroupList, ","));
                        answers.put(basekey + OppijaConstants.OPTION_ATTACHMENT_GROUP_POSTFIX, StringUtils.join(attachmentGroupList, ","));
                    }
                }

                if (isEmpty(attachments)) {
                    List<ApplicationOptionAttachmentDTO> attachmentList = applicationOption.getAttachments();
                    if (attachmentList != null && !attachmentList.isEmpty()) {
                        answers.put(basekey + OppijaConstants.OPTION_ATTACHMENTS_POSTFIX, "true");
                    }
                }
            }
        }
        LOGGER.debug("output map: " + answers.toString());
        return answers;
    }

    @Override
    public Application ensureApplicationOptionGroupData(final Application application) {
        Map<String, String> phaseAnswers =  application.getAnswers().get(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, phaseAnswers);
        return application;
    }

    public Application getApplication(final Application queryApplication) {

        LOGGER.debug("Entering ApplicationServiceImpl.getApplication()");
        List<Application> listOfApplications;
        try {
            listOfApplications = applicationDAO.find(queryApplication);
            LOGGER.debug("Got "+listOfApplications.size()+" applications");
        } catch (IllegalArgumentException iae) {
            LOGGER.error("Error getting application: ", iae);
            throw new ResourceNotFoundException("Error getting application", iae);
        } catch(RuntimeException t) {
            LOGGER.error("Getting application failed: "+t);
            throw t;
        }
        if (listOfApplications.isEmpty()) {
            throw new ResourceNotFoundException("Could not find application " + queryApplication.getOid());
        }
        if (listOfApplications.size() > 1) {
            throw new ResourceNotFoundException("Found multiple applications with oid " + queryApplication.getOid());
        }

        Application application = listOfApplications.get(0);

        if (!hakuPermissionService.userCanReadApplication(application)) {
            throw new ResourceNotFoundException("User "+  authenticationService.getCurrentHenkilo().getPersonOid()  +" is not allowed to read application " + application.getOid());
        }
        return application;
    }
}
