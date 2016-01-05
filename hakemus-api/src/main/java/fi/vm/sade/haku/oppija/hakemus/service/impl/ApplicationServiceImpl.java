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
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil;
import fi.vm.sade.haku.oppija.hakemus.domain.*;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.UpdatePreferenceResult;
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
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.exception.ApplicationDeadlineExpiredException;
import fi.vm.sade.haku.oppija.lomake.exception.ApplicationSystemNotFound;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.Session;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.OhjausparametritService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain.Ohjausparametrit;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
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
    private final Session userSession;
    private final FormService formService;
    private final AuthenticationService authenticationService;
    private final OrganizationService organizationService;
    private final HakuPermissionService hakuPermissionService;
    private final ApplicationSystemService applicationSystemService;
    private final KoulutusinformaatioService koulutusinformaatioService;
    private final I18nBundleService i18nBundleService;
    private final SuoritusrekisteriService suoritusrekisteriService;
    private final HakuService hakuService;
    private final ElementTreeValidator elementTreeValidator;
    private final ValintaService valintaService;
    private final Boolean disableHistory;
    private final OhjausparametritService ohjausparametritService;

    // Tee vain background-validointi tälle lomakkeelle
    private final String onlyBackgroundValidation;

    private static final String REGEX_NOT_DIGIT = "[^0-9]";

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                  final Session userSession,
                                  @Qualifier("formServiceImpl") final FormService formService,
                                  @Qualifier("applicationOidServiceImpl") ApplicationOidService applicationOidService,
                                  AuthenticationService authenticationService,
                                  OrganizationService organizationService,
                                  HakuPermissionService hakuPermissionService,
                                  ApplicationSystemService applicationSystemService,
                                  KoulutusinformaatioService koulutusinformaatioService,
                                  I18nBundleService i18nBundleService,
                                  SuoritusrekisteriService suoritusrekisteriService,
                                  HakuService hakuService, ElementTreeValidator elementTreeValidator,
                                  ValintaService valintaService,
                                  OhjausparametritService ohjausparametritService,
                                  @Value("${onlyBackgroundValidation}") String onlyBackgroundValidation,
                                  @Value("${disableHistory:false}") String disableHistory) {
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
        this.suoritusrekisteriService = suoritusrekisteriService;
        this.hakuService = hakuService;
        this.valintaService = valintaService;
        this.ohjausparametritService = ohjausparametritService;
        this.elementTreeValidator = elementTreeValidator;
        this.onlyBackgroundValidation = onlyBackgroundValidation;
        this.disableHistory = Boolean.valueOf(disableHistory);
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
        final Element phase = activeForm.getChildById(applicationPhase.getPhaseId());
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
        if (userSession.hasApplication(applicationSystemId)) {
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
            application.setModelVersion(Application.CURRENT_MODEL_VERSION);

            application = updatePreferenceBasedData(application).getApplication();
            this.applicationDAO.save(application);
            this.userSession.removeApplication(application);
            return application;
        } else {
            LOGGER.error("Could not send the application | " + getApplicationLogMessage(application, validationResult));
            if (validationResult.isExpired()) {
                LOGGER.error("Application deadline has expired");
                throw new ApplicationDeadlineExpiredException();
            }
            throw new IllegalStateException("Could not send the application ");
        }
    }

    private String getApplicationLogMessage(Application application, ValidationResult validationResult) {
        if (application == null) return "Application was null.";

        StringBuilder sb = new StringBuilder();
        sb.append("Hakemus: ").append(application.getOid());
        sb.append("\r\n").append(application.getAnswers().toString());
        if (validationResult != null) {
            sb.append("\r\n").append(validationResult.getErrorMessages());
        }

        return sb.toString();
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
            return new Application(applicationSystemId, userSession.getUser());
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
    public Application updateAuthorizationMeta(final Application application) throws IOException {
        final Map<String, Set<String>> aoOrganizations = new HashMap<>();
        final Set<String> allOrganizations = new HashSet<>();
        final Map<Integer, Map<String, String>> applicationPreferenceData = new HashMap<>();

        final Map<String, String> aoAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        for (final Map.Entry<String, String> originalEntry : aoAnswers.entrySet()) {
            final String originalKey = originalEntry.getKey();
            if (StringUtils.isEmpty(originalEntry.getValue()) || null == originalKey)
                continue;
            if (!originalKey.startsWith(OppijaConstants.PREFERENCE_PREFIX) || originalKey.equals(OppijaConstants.PREFERENCES_VISIBLE))
                continue;
            final String[] splitKey = originalKey.substring(OppijaConstants.PREFERENCE_PREFIX.length()).split(REGEX_NOT_DIGIT, 2);
            try {
                final Integer ordinal = Integer.valueOf(splitKey[0]);
                final String dataKey = splitKey[1];
                Map<String, String> preferenceData = applicationPreferenceData.get(ordinal);
                if (null == preferenceData) {
                    preferenceData = new HashMap<>();
                    applicationPreferenceData.put(ordinal, preferenceData);
                }
                preferenceData.put(dataKey, originalEntry.getValue());

                if (OppijaConstants.PREFERENCE_FRAGMENT_ORGANIZATION_ID.equals(dataKey)) {
                    final String lop = originalEntry.getValue();
                    final HashSet<String> parents = new HashSet<>(organizationService.findParentOids(lop));
                    aoOrganizations.put(String.valueOf(ordinal), parents);
                    allOrganizations.addAll(parents);
                }
            } catch (NumberFormatException exp) {
                LOGGER.error("Getting of preference ordinal for application {} failed for entry key: {}  value: {}. Aborting updated Authorization meta", application.getOid(), originalKey, originalEntry.getValue());
                throw exp;
            }

        }

        final AuthorizationMeta authorizationMeta = new AuthorizationMeta();
        authorizationMeta.setOpoAllowed(resolveOpoAllowed(application));
        authorizationMeta.setAoOrganizations(aoOrganizations);
        authorizationMeta.setAllAoOrganizations(allOrganizations);
        authorizationMeta.setSendingSchool(getSendingSchool(application));
        authorizationMeta.setApplicationPreferences(buildPreferenceMetas(applicationPreferenceData));

        application.setAuthorizationMeta(authorizationMeta);
        return application;
    }

    @Override
    public Application updateAutomaticEligibilities(Application application) {
        ApplicationSystem as = hakuService.getApplicationSystem(application.getApplicationSystemId());
        List<String> aosForAutomaticEligibility = as.getAosForAutomaticEligibility();
        if (aosForAutomaticEligibility == null || aosForAutomaticEligibility.isEmpty() || !hasValidOhjausparametriWithAutomaticHakukelpoisuus(as)) {
            return application;
        }
        Map<String, List<SuoritusDTO>> suoritusMap = suoritusrekisteriService
                .getSuoritukset(application.getPersonOid(), SuoritusrekisteriService.YO_TUTKINTO_KOMO);
        boolean acceptedYo = false;
        if (suoritusMap != null && !suoritusMap.isEmpty()) {
            List<SuoritusDTO> yoSuoritukset = suoritusMap.get(SuoritusrekisteriService.YO_TUTKINTO_KOMO);
            for (SuoritusDTO suoritus : yoSuoritukset) {
                if (SuoritusDTO.TILA_VALMIS.equals(suoritus.getTila())) {
                    acceptedYo = true;
                    break;
                }
            }
        }
        for (PreferenceEligibility eligibility : application.getPreferenceEligibilities()) {
            PreferenceEligibility.Status status = eligibility.getStatus();
            if ((PreferenceEligibility.Status.NOT_CHECKED.equals(status)
                            || PreferenceEligibility.Status.AUTOMATICALLY_CHECKED_ELIGIBLE.equals(status))
                    && aosForAutomaticEligibility.contains(eligibility.getAoId())) {
                eligibility.setStatus(acceptedYo
                        ? PreferenceEligibility.Status.AUTOMATICALLY_CHECKED_ELIGIBLE
                        : PreferenceEligibility.Status.NOT_CHECKED);
                eligibility.setSource(PreferenceEligibility.Source.REGISTER);

                updateEligibilityStatusToApplicationNotes(application, eligibility);
                
            }
        }
        return application;
    }

    private void updateEligibilityStatusToApplicationNotes(Application application,
                                                           PreferenceEligibility preferenceEligibility) {

        int preferenceEligibilityIndex = application.getPreferenceEligibilities().indexOf(preferenceEligibility) + 1;

        String eligibilityNote = preferenceEligibilityIndex + ". hakukelpoisuutta muutettu: " +
          PreferenceEligibility.getStatusMessage(preferenceEligibility.getStatus()) + 
          ", " + PreferenceEligibility.getSourceMessage(preferenceEligibility.getSource());
        application.addNote(new ApplicationNote(eligibilityNote, new Date(), "järjestelmä"));
    }

    private boolean hasValidOhjausparametriWithAutomaticHakukelpoisuus(ApplicationSystem as) {
        Ohjausparametrit ohjausparametrit;
        try {
            ohjausparametrit = ohjausparametritService.fetchOhjausparametritForHaku(as.getId());
            if(ohjausparametrit == null) return true;
        } catch(Throwable t) {
            LOGGER.error("Unable to fetch 'ohjausparametrit' to 'haku' {}!", as.getId(), t);
            return true;
        }
        if(ohjausparametrit.getPH_AHP() != null && ohjausparametrit.getPH_AHP().getDate() != null) {
            final Date NOW = new Date();
            final Date automaattinenHakukelpoisuusPaattyy = ohjausparametrit.getPH_AHP().getDate();
            if(NOW.after(automaattinenHakukelpoisuusPaattyy)) {
                return false;
            }
        }
        return true;
    }

    private Set<String> getSendingSchool(final Application application) throws IOException {
        final Set<String> sendingSchool = new HashSet<>();
        final Map<String, String> educationAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        final String sendingSchoolOrg = educationAnswers.get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL);
        if (sendingSchoolOrg != null) {
            sendingSchool.addAll(organizationService.findParentOids(sendingSchoolOrg));
        }
        return sendingSchool;
    }

    private List<ApplicationPreferenceMeta> buildPreferenceMetas(final Map<Integer, Map<String, String>> applicationPreferenceData){
        final List<ApplicationPreferenceMeta> preferenceMetas = new ArrayList<>(applicationPreferenceData.size());
        for (final Integer ordinal : applicationPreferenceData.keySet()) {
            Map<String, String> preferenceData = applicationPreferenceData.get(ordinal);
            if (preferenceData.containsKey(OppijaConstants.PREFERENCE_FRAGMENT_OPTION_ID))
                preferenceMetas.add(new ApplicationPreferenceMeta(ordinal, preferenceData));
        }
        return preferenceMetas;
    }

    @Override
    public UpdatePreferenceResult updatePreferenceBasedData(final Application application) {
        List<ApplicationAttachmentRequest> appReqOrig = application.cloneAttachmentRequests();

        List<String> preferenceAoIds = ApplicationUtil.getPreferenceAoIds(application);

        application.setPreferenceEligibilities(ApplicationUtil.checkAndCreatePreferenceEligibilities(
                application.getPreferenceEligibilities(), preferenceAoIds));
        application.setPreferencesChecked(ApplicationUtil.checkAndCreatePreferenceCheckedData(
                application.getPreferencesChecked(), preferenceAoIds));

        ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        application.setAttachmentRequests(AttachmentUtil.resolveAttachmentRequests(applicationSystem, application,
                koulutusinformaatioService, i18nBundleService.getBundle(applicationSystem)));

        UpdatePreferenceResult resp = new UpdatePreferenceResult(application);

        for(ApplicationAttachmentRequest orig: appReqOrig) {
            if(ApplicationAttachmentRequest.ReceptionStatus.ARRIVED.equals(orig.getReceptionStatus()) ||
                    ApplicationAttachmentRequest.ReceptionStatus.ARRIVED_LATE.equals(orig.getReceptionStatus())) {
                boolean foundMatch = false;
                for(ApplicationAttachmentRequest newReq : application.getAttachmentRequests()) {
                    if(orig.equals(newReq)) {
                        foundMatch = true;
                        newReq.setReceptionStatus(orig.getReceptionStatus());
                        newReq.setProcessingStatus(orig.getProcessingStatus());
                        break;
                    }
                }
                if(foundMatch == false) {
                    String note = "Liitemerkintä poistettu.";
                    note +=" Saapunut: " + (ApplicationAttachmentRequest.ReceptionStatus.ARRIVED.equals(orig.getReceptionStatus())?"Kyllä.":"Myöhässä.");
                    if(orig.getPreferenceAoId() != null) {
                        note += " Hakukohde: (" + orig.getPreferenceAoId() + ").";
                        try {
                            final ApplicationOptionDTO applicationOption = koulutusinformaatioService.getApplicationOption(orig.getPreferenceAoId());
                            if (applicationOption != null) {
                                if(applicationOption.getProvider() != null) {
                                    note += " " + applicationOption.getProvider().getName() + ".";
                                }
                                note += " " + applicationOption.getName() + ".";
                            }
                        } catch(Exception e) {
                            LOGGER.error("Failed to query KI aoId:" + orig.getPreferenceAoId(), e);
                        }
                    }
                    if(orig.getPreferenceAoGroupId() != null) {
                        note += " Hakukohderyhmä: (" +orig.getPreferenceAoGroupId() + ").";

                        try {
                            Organization organization = this.organizationService.findByOid(orig.getPreferenceAoGroupId());
                            if(organization != null && organization.getName() != null) {
                                note += " " + organization.getName().getText("fi");
                            }
                        } catch(Exception e) {
                            LOGGER.error("Failed to query organization:" + orig.getPreferenceAoGroupId(), e);
                        }
                    }
                    note += " Kuvaus: ";

                    if(orig.getApplicationAttachment() != null && orig.getApplicationAttachment().getName() != null) {
                        I18nText nameText = orig.getApplicationAttachment().getName();
                        if (nameText.getTranslations().containsKey("fi")) {
                            note += nameText.getText("fi");
                        } else if (nameText.getTranslations().containsKey("sv")) {
                            note += nameText.getText("sv");
                        } else if (nameText.getTranslations().containsKey("en")) {
                            note += nameText.getText("en");
                        }
                    } else if(orig.getApplicationAttachment() != null && orig.getApplicationAttachment().getHeader() != null) {
                        I18nText headerText = orig.getApplicationAttachment().getHeader();
                        if (headerText.getTranslations().containsKey("fi")) {
                            note += headerText.getText("fi");
                        } else if (headerText.getTranslations().containsKey("sv")) {
                            note += headerText.getText("sv");
                        } else if (headerText.getTranslations().containsKey("en")) {
                            note += headerText.getText("en");
                        }
                    }
                    application.addNote(new ApplicationNote(note, new Date(), userSession.getUser().getUserName()));

                    ValidationResult vr = new ValidationResult("liitetiedot.status.poistettu", this.i18nBundleService.getBundle(applicationSystem).get("liitetiedot.status.poistettu"));
                    resp.setValidationResult(vr);
                }
            }
        }

        return resp;

    }

    @Override
    public Application removeOrphanedAnswers(Application application) {
        Form form = applicationSystemService.getApplicationSystem(application.getApplicationSystemId()).getForm();
        boolean answersRemoved = true;

        while (answersRemoved) {
            answersRemoved = false;
            Map<String, String> answers = application.getVastauksetMerged();
            Set<String> questions = new HashSet<>();
            Deque<Element> children = new LinkedList<>();
            children.push(form);
            while (children.size() > 0) {
                Element e = children.pop();
                questions.add(e.getId());
                for (Element child : e.getChildren(answers)) {
                    children.push(child);
                }
            }

            questions.add(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL);
            questions.add(OppijaConstants.ELEMENT_ID_SENDING_CLASS);
            questions.add(OppijaConstants.ELEMENT_ID_CLASS_LEVEL);
            questions.add(OppijaConstants.ELEMENT_ID_SECURITY_ORDER);

            for (Map.Entry<String, Map<String, String>> phase : application.getAnswers().entrySet()) {
                String phaseId = phase.getKey();
                Map<String, String> newAnswers = new HashMap<>();
                for (Map.Entry<String, String> answer : phase.getValue().entrySet()) {
                    String answerKey = answer.getKey();
                    if (questions.contains(answerKey)
                            || (OppijaConstants.PHASE_APPLICATION_OPTIONS.equals(phaseId) && answerKey.startsWith("preference"))) {
                        newAnswers.put(answerKey, answer.getValue());
                    } else {
                        answersRemoved = true;
                    }
                }
                application.setVaiheenVastauksetAndSetPhaseId(phaseId, newAnswers);
            }
        }
        return application;
    }

    @Override
    public Application getApplicationWithValintadata(String oid) {
        Application application = getApplicationByOid(oid);
        ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        Form form = as.getForm();
        Phase educationPhase = (Phase) form.getChildById(OppijaConstants.PHASE_EDUCATION);

        HashMap<String, Element> educationElements = new HashMap<>();
        for (Element elem : educationPhase.getAllChildren()) {
            educationElements.put(elem.getId(), elem);
        }

        HashMap<String, String> educationAnswers = new HashMap<>();
        HashMap<String, String> newGradeAnswers = new HashMap<>();
        Map<String, String> valintaData = valintaService.fetchValintaData(application);
        for (Map.Entry<String, String> entry : valintaData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (educationElements.containsKey(key)) {
                educationAnswers.put(key, value);
            } else if (key.startsWith("PK_") || key.startsWith("LK_")) {
                newGradeAnswers.put(key, value);
            }
        }
        HashMap<String, String> oldGradeAnswers = new HashMap<>(application.getPhaseAnswers(OppijaConstants.PHASE_GRADES));
        for (Map.Entry<String, String> entry : oldGradeAnswers.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("PK_") || key.startsWith("LK_")) {
                continue;
            }
            newGradeAnswers.put(key, entry.getValue());
        }
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_EDUCATION, educationAnswers);
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_GRADES, newGradeAnswers);
        return application;
    }

    private boolean resolveOpoAllowed(Application application) {
        try {
            boolean opoAllowed = true;
            ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
            if (as.getKohdejoukkoUri().equals(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU)) {
                opoAllowed = false;
            }
            return opoAllowed;
        } catch (ApplicationSystemNotFound e) {
            // Probably doesn't use system form. Defaulting to opo not allowed
            return false;
        }
    }

    @Override
    public void saveApplicationAdditionalInfo(List<ApplicationAdditionalDataDTO> applicationAdditionalData) {
        if (applicationAdditionalData != null) {
            for (ApplicationAdditionalDataDTO data : applicationAdditionalData) {
                Map<String, String> additionalData = data.getAdditionalData();
                if (additionalData != null && !additionalData.isEmpty()) {
                    saveApplicationAdditionalInfo(data.getOid(), additionalData);
                }
            }
        }
    }

    @Override
    public void saveApplicationAdditionalInfo(String oid, Map<String, String> additionalInfo) {
        Application query = new Application(oid);
        Application current = getApplication(query);
        hakuPermissionService.userCanEditApplicationAdditionalData(current);
        current.getAdditionalInfo().putAll(additionalInfo);
        update(query, current);
    }

    @Override
    public void update(final Application queryApplication, final Application application) {
        this.update(queryApplication, application, false);
    }

    @Override
    public void update(final Application queryApplication, final Application application,
                       final boolean postProcess) {
        if (postProcess) {
            application.setRedoPostProcess(Application.PostProcessingState.NOMAIL);
        }
        if (!disableHistory) {
            LOGGER.debug("addChangeHistoryToApplication");
            Application oldApplication = applicationDAO.find(queryApplication).get(0);
            ApplicationDiffUtil.addHistoryBasedOnChangedAnswers(application, oldApplication, userSession.getUser().getUserName(), "update");
        }
        this.applicationDAO.update(queryApplication, application);
    }

    @Override
    public List<String> massRedoPostProcess(List<String> applicationOids, Application.PostProcessingState newState) {
        return applicationDAO.massRedoPostProcess(applicationOids, newState);
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
    public List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(final List<String> oids) {
        ApplicationFilterParametersBuilder builder = new ApplicationFilterParametersBuilder()
                .addOrganizationsReadable(hakuPermissionService.userCanReadApplications())
                .addOrganizationsOpo(hakuPermissionService.userHasOpoRole());
        return applicationDAO.findApplicationAdditionalData(oids, builder.build());
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
    public Map<String, String> ensureApplicationOptionGroupData(final Map<String, String> originalAnswers, final String lang) {
        final HashMap<String, String> ensuredAnswers = new HashMap<>(originalAnswers);
        for (final String key : originalAnswers.keySet()) {
            if (null != key
                    && key.startsWith(OppijaConstants.PREFERENCE_PREFIX)
                    && key.endsWith(OppijaConstants.OPTION_ID_POSTFIX)
                    && isNotEmpty(ensuredAnswers.get(key))) {
                final String basekey = key.replace(OppijaConstants.OPTION_ID_POSTFIX, "");

                final ApplicationOptionDTO applicationOption = koulutusinformaatioService.getApplicationOption(ensuredAnswers.get(key), lang);
                final List<String> teachingLangs = applicationOption.getTeachingLanguages();
                final String teachingLang = teachingLangs != null && teachingLangs.size() > 0
                        ? teachingLangs.get(0) : "";

                ensuredAnswers.put(basekey + "-Opetuspiste", safeToString(applicationOption.getProvider().getName()));
                ensuredAnswers.put(basekey + "-Opetuspiste-id", safeToString(applicationOption.getProvider().getId()));
                ensuredAnswers.put(basekey + "-Koulutus", safeToString(applicationOption.getName()));
                ensuredAnswers.put(basekey + "-Koulutus-id", safeToString(applicationOption.getId()));
                ensuredAnswers.put(basekey + "-Koulutus-educationDegree", safeToString(applicationOption.getEducationDegree()));
                ensuredAnswers.put(basekey + "-Koulutus-id-sora", String.valueOf(applicationOption.isSora()));
                ensuredAnswers.put(basekey + "-Koulutus-id-lang", safeToString(teachingLang));
                ensuredAnswers.put(basekey + "-Koulutus-id-athlete", String.valueOf(applicationOption.isAthleteEducation()
                        || applicationOption.getProvider().isAthleteEducation()));
                ensuredAnswers.put(basekey + "-Koulutus-id-aoIdentifier", safeToString(applicationOption.getAoIdentifier()));
                ensuredAnswers.put(basekey + "-Koulutus-id-kaksoistutkinto", String.valueOf(applicationOption.isKaksoistutkinto()));
                ensuredAnswers.put(basekey + "-Koulutus-id-vocational", String.valueOf(applicationOption.isVocational()));
                ensuredAnswers.put(basekey + "-Koulutus-id-educationcode", safeToString(applicationOption.getEducationCodeUri()));
                ensuredAnswers.put(basekey + "-Koulutus-id-discretionary", String.valueOf(applicationOption.isKysytaanHarkinnanvaraiset()));

                final ArrayList<String> aoGroupList = new ArrayList<String>();
                final List<OrganizationGroupDTO> organizationGroups = applicationOption.getOrganizationGroups();
                if (null != organizationGroups && organizationGroups.size() > 0) {
                    for (final OrganizationGroupDTO organizationGroup : organizationGroups) {
                        aoGroupList.add(organizationGroup.getOid());
                    }

                }
                ensuredAnswers.put(basekey + OppijaConstants.OPTION_GROUP_POSTFIX, StringUtils.join(aoGroupList, ","));

                final String attachments = ensuredAnswers.get(basekey + OppijaConstants.OPTION_ATTACHMENTS_POSTFIX);
                if (isEmpty(attachments)) {
                    final List<ApplicationOptionAttachmentDTO> attachmentList = applicationOption.getAttachments();
                    if (attachmentList != null && !attachmentList.isEmpty()) {
                        ensuredAnswers.put(basekey + OppijaConstants.OPTION_ATTACHMENTS_POSTFIX, "true");
                    }
                }
            }
        }
        return ensuredAnswers;
    }

    @Override
    public Application ensureApplicationOptionGroupData(final Application application) {
        Map<String, String> phaseAnswers = application.getAnswers().get(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        String lang = application.getMeta().get(Application.META_FILING_LANGUAGE);
        phaseAnswers = ensureApplicationOptionGroupData(phaseAnswers, lang);
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, phaseAnswers);
        return application;
    }

    public Application getApplication(final Application queryApplication) {

        LOGGER.debug("Entering ApplicationServiceImpl.getApplication()");
        List<Application> listOfApplications;
        try {
            listOfApplications = applicationDAO.find(queryApplication);
            LOGGER.debug("Got " + listOfApplications.size() + " applications");
        } catch (IllegalArgumentException iae) {
            LOGGER.error("Error getting application: ", iae);
            throw new ResourceNotFoundException("Error getting application", iae);
        } catch (RuntimeException t) {
            LOGGER.error("Getting application failed: " + t);
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
            throw new ResourceNotFoundException("User " + authenticationService.getCurrentHenkilo().getPersonOid() + " is not allowed to read application " + application.getOid());
        }
        return application;
    }
}
