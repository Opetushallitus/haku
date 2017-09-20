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

import static com.google.common.collect.Maps.filterKeys;
import static fi.vm.sade.haku.oppija.hakemus.service.ApplicationModelUtil.removeAuthorizationMeta;
import static fi.vm.sade.haku.oppija.hakemus.service.ApplicationModelUtil.restoreV0ModelLOPParentsToApplicationMap;
import static fi.vm.sade.haku.oppija.lomake.util.StringUtil.safeToString;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.ELEMENT_ID_BASE_EDUCATION;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.ELEMENT_ID_PERSON_OID;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.KESKEYTYNYT;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PREFERENCE_DISCRETIONARY;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.ULKOMAINEN_TUTKINTO;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimaps;

import com.google.gson.Gson;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import fi.vm.sade.haku.ApiAuditLogger;
import fi.vm.sade.haku.HakuOperation;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationAttachmentRequest;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPreferenceMeta;
import fi.vm.sade.haku.oppija.hakemus.domain.AuthorizationMeta;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.OhjausparametritService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain.Ohjausparametrit;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.haku.virkailija.valinta.ValintaServiceCallFailedException;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionAttachmentDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.OrganizationGroupDTO;
import org.apache.commons.lang.StringUtils;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.*;

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
    private final ApiAuditLogger apiAuditLogger;

    // Tee vain background-validointi tälle lomakkeelle
    private final String onlyBackgroundValidation;

    private static final String REGEX_NOT_DIGIT = "[^0-9]";

    @Context
    private HttpServletRequest httpServletRequest;

    @Context
    private Request request;

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private HttpSession httpSession;

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
                                  @Value("${disableHistory:false}") String disableHistory,
                                  ApiAuditLogger apiAuditLogger) {
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
        this.apiAuditLogger = apiAuditLogger;
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

            Gson g = new Gson();
            fi.vm.sade.auditlog.User apiUser = apiAuditLogger.getUser();
            Target.Builder target = new Target.Builder();
            Changes.Builder changes = new Changes.Builder();
            target.setField("hakemusOid", application.getOid())
                    .setField("hakuOid", applicationSystem.getId());
            changes.added("hakemus", g.toJson(application));

            apiAuditLogger.log(apiUser, HakuOperation.SAVE_APPLICATION, target.build(), changes.build());

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
    public List<Map<String, Object>> findApplicationsWithKeys(
            final ApplicationQueryParameters applicationQueryParameters,
            final String... keys) {
        return applicationDAO.findAllQueriedWithKeys(applicationQueryParameters,
                buildFilterParams(applicationQueryParameters), keys);
    }

    @Override
    public ApplicationSearchResultDTO findApplications(final ApplicationQueryParameters applicationQueryParameters) {
        return applicationDAO.findAllQueried(applicationQueryParameters,
                buildFilterParams(applicationQueryParameters));
    }

    @Override
    public Map<String, Collection<Map<String, Object>>> findApplicationsByPersonOid(Set<String> personOids, boolean allKeys, boolean removeSensitiveInfo) {
        List<Map<String, Object>> applications = applicationDAO.findApplicationsByPersonOid(personOids, allKeys, removeSensitiveInfo);
        return transformApplicationsByKey(convertApplications(applications), ELEMENT_ID_PERSON_OID);
    }

    @Override
    public Set<String> findPersonOidsByApplicationSystemOids(Collection<String> applicationSystemOids, String organizationOid) {
        return applicationDAO.findPersonOidsByApplicationSystemOids(applicationSystemOids, organizationOid);
    }

    @Override
    public Set<String> findPersonOidsByApplicationOptionOids(Collection<String> applicationOptionOids, String organizationOid) {
        return applicationDAO.findPersonOidsByApplicationOptionOids(applicationOptionOids, organizationOid);
    }

    private Map<String, Collection<Map<String, Object>>> transformApplicationsByKey(List<Map<String, Object>> applications, final String key) {

        Map<String, Collection<Map<String, Object>>> applicationsByKey = Multimaps.index(applications, new Function<Map<String, Object>, String>() {
            @Override
            public String apply(Map<String, Object> application) {
                return (String) application.get(key);
            }
        }).asMap();

        return applicationsByKey;
    }

    private List<Map<String, Object>> convertApplications(List<Map<String, Object>> applications) {
        for (Map<String, Object> application : applications) {
            restoreV0ModelLOPParentsToApplicationMap(application);
            removeAuthorizationMeta(application);
        }
        return applications;
    }

    @Override
    public List<Map<String, Object>> findFullApplications(final ApplicationQueryParameters applicationQueryParameters) {
        List<Map<String, Object>> applications = applicationDAO.findAllQueriedFull(applicationQueryParameters,
                buildFilterParams(applicationQueryParameters));
        return convertApplications(applications);
    }

    private ApplicationFilterParameters buildFilterParams(final ApplicationQueryParameters applicationQueryParameters) {
        List<String> queryASIds = applicationQueryParameters.getAsIds();

        ApplicationFilterParametersBuilder builder = new ApplicationFilterParametersBuilder()
                .addOrganizationsReadable(hakuPermissionService.userCanReadApplications())
                .addOrganizationsOpo(hakuPermissionService.userHasOpoRole())
                .setOrganizationFilter(applicationQueryParameters.getOrganizationFilter())
                .addOrganizationsHetuttomienKasittely(hakuPermissionService.userHasHetuttomienKasittelyRole());
        if (queryASIds != null) {
            try {
                builder.setMaxApplicationOptions(applicationSystemService.getMaxApplicationOptions(applicationQueryParameters.getAsIds()));
            } catch (ResourceNotFoundException e) { }
        }
        if (queryASIds != null && queryASIds.size() == 1) {
            try {
                ApplicationSystem as = applicationSystemService.getApplicationSystem(queryASIds.get(0), "kohdejoukkoUri", "hakutapa");
                builder.setKohdejoukko(as.getKohdejoukkoUri());
                builder.setHakutapa(as.getHakutapa());
            } catch (ResourceNotFoundException e) { }
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

                if(!eligibility.getStatus().equals(status)) {
                    updateEligibilityStatusToApplicationNotes(application, eligibility);
                    application.setEligibilitiesAndAttachmentsUpdated(new Date());
                }
            }
        }
        return application;
    }

    private void updateEligibilityStatusToApplicationNotes(Application application,
                                                           PreferenceEligibility preferenceEligibility) {

        String eligibilityNote = ApplicationUtil.getApplicationOptionName(application, preferenceEligibility) +
          ". Hakukelpoisuutta muutettu: " + PreferenceEligibility.getStatusMessage(preferenceEligibility.getStatus()) +
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
                        note += orig.getApplicationAttachment().getName().getText("fi");
                    } else if(orig.getApplicationAttachment() != null && orig.getApplicationAttachment().getHeader() != null) {
                        note += orig.getApplicationAttachment().getHeader().getText("fi");
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
    public Application removeOrphanedAnswers(Application application) throws ValintaServiceCallFailedException {
        return removeOrphanedAnswers(application, getApplicationWithValintadata(application.clone()));
    }

    private Application removeOrphanedAnswers(Application application, Application applicationWithValintaData) {
        Map<String, String> applicationWithValintaDataAnswers = new HashMap<>(applicationWithValintaData.getVastauksetMerged());
        Form form = applicationSystemService.getApplicationSystem(application.getApplicationSystemId()).getForm();
        boolean answersRemoved = true;

        while (answersRemoved) {
            answersRemoved = false;
            Set<String> questions = new HashSet<>();
            Deque<Element> children = new LinkedList<>();
            children.push(form);
            while (children.size() > 0) {
                Element e = children.pop();
                questions.add(e.getId());
                for (Element child : e.getChildren(applicationWithValintaDataAnswers)) {
                    children.push(child);
                }
            }

            questions.addAll(OppijaConstants.SENDING_SCHOOL_ELEMENT_IDS);
            questions.addAll(OppijaConstants.HENKILOTUNNUS_BASED_ELEMENT_IDS);
            questions.add(OppijaConstants.ELEMENT_ID_SECURITY_ORDER);

            for (Map.Entry<String, Map<String, String>> phase : application.getAnswers().entrySet()) {
                String phaseId = phase.getKey();
                Map<String, String> newAnswers = new HashMap<>();
                for (Map.Entry<String, String> answer : phase.getValue().entrySet()) {
                    String answerKey = answer.getKey();
                    if (questions.contains(answerKey)
                        || !keyCanBePruned(phaseId, answerKey)
                        ){
                        newAnswers.put(answerKey, answer.getValue());
                    } else {
                        LOGGER.info("Removing orphaned answer with key " +  answerKey + " from application " + application.getOid());
                        applicationWithValintaDataAnswers.remove(answerKey);
                        answersRemoved = true;
                    }
                }
                application.setVaiheenVastauksetAndSetPhaseId(phaseId, newAnswers);
            }
        }
        return application;
    }

    private boolean keyCanBePruned(String phaseId, String answerKey) {
        if(OppijaConstants.PHASE_APPLICATION_OPTIONS.equals(phaseId)
           && answerKey.startsWith(OppijaConstants.PREFERENCE_PREFIX)) {
            if(answerKey.contains(OppijaConstants.PREFERENCE_FRAGMENT_NAME)) {
                return false;
            }
            return answerKey.contains(OppijaConstants.PREFERENCE_FRAGMENT_DISCRETIONARY);
        }
        return true;
    }

    public Application getApplicationWithValintadata(Application application, Optional<Duration> valintaTimeout) throws ValintaServiceCallFailedException {
        ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        Form form = as.getForm();
        Phase educationPhase = (Phase) form.getChildById(OppijaConstants.PHASE_EDUCATION);

        HashSet<String> educationElementIds = new HashSet(OppijaConstants.SENDING_SCHOOL_ELEMENT_IDS);
        for (Element elem : educationPhase.getAllChildren()) {
            educationElementIds.add(elem.getId());
        }

        HashMap<String, String> educationAnswers = new HashMap<>();
        HashMap<String, String> preferenceAnswers = new HashMap<>();
        HashMap<String, String> newGradeAnswers = new HashMap<>();
        Map<String, String> valintaData = valintaService.fetchValintaData(application, valintaTimeout);
        for (Map.Entry<String, String> entry : valintaData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (educationElementIds.contains(key)) {
                educationAnswers.put(key, value);
            } else if (isPreferenceKey(key)) {
                preferenceAnswers.put(key, value);
            } else if (isArvosanaKey(key)) {
                newGradeAnswers.put(key, value);
            }
        }
        if(!educationAnswers.isEmpty()) {
            application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_EDUCATION, educationAnswers);
        }
        addNewAnswersForPhase(application, OppijaConstants.PHASE_APPLICATION_OPTIONS, preferenceAnswers);
        // BUG-856 remove grades from application data before adding derived ones from valintalaskentakoostepalvelu
        removeGradesFromApplication(application);
        addNewAnswersForPhase(application, OppijaConstants.PHASE_GRADES, newGradeAnswers);
        return application;
    }

    @Override
    public Application getApplicationWithValintadata(Application application) throws ValintaServiceCallFailedException {
        return getApplicationWithValintadata(application, Optional.empty());
    }


    private void removeGradesFromApplication(final Application application) {
        final Map<String, String> filteredGrades = filterKeys(application.getPhaseAnswers(OppijaConstants.PHASE_GRADES), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input != null && !isArvosanaKey(input);
            }
        });
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_GRADES, filteredGrades);
    }

    private void addNewAnswersForPhase(Application application, String phaseId, HashMap<String, String> newAnswers) {
        if(newAnswers.isEmpty()) {
            return;
        }
        Map<String, String> oldAnswers = application.getPhaseAnswers(phaseId);
        for (Map.Entry<String, String> entry : oldAnswers.entrySet()) {
            String key = entry.getKey();
            if (newAnswers.containsKey(key)) {
                continue;
            }
            newAnswers.put(key, entry.getValue());
        }
        application.setVaiheenVastauksetAndSetPhaseId(phaseId, newAnswers);
    }

    private boolean isPreferenceKey(String key) {
        return key.startsWith(OppijaConstants.PREFERENCE_PREFIX);
    }

    private static boolean isArvosanaKey(String key) {
        return key.startsWith("PK_") || key.startsWith("LK_");
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
    public void update(Application application, boolean postProcess) {
        Application queryApplication = new Application(application.getOid(), application.getVersion());
        this.update(queryApplication, application, postProcess);
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
        Gson g = new Gson();


        Target.Builder target = new Target.Builder()
                .setField("hakemusOid", application.getOid());
        Changes.Builder changes = new Changes.Builder()
                .added("application", g.toJson(application));

        apiAuditLogger.log(apiAuditLogger.getUser(), HakuOperation.UPDATE_APPLICATION, target.build(), changes.build());

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

            Target.Builder target = new Target.Builder().setField("applicationOid",applicationOid);
            Changes.Builder changes = new Changes.Builder().updated("additionaliInfo."+key, "",value);
            apiAuditLogger.log(apiAuditLogger.getUser(), HakuOperation.UPDATE_ADDITIONAL_INFO_KEY_VALUE, target.build(), changes.build());
        }
    }

    public List<String> findMaksuvelvolliset(final String applicationSystemId, final String aoId) {
        return applicationDAO.findMaksuvelvolliset(applicationSystemId, aoId);
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

        Target.Builder target = new Target.Builder().setField("applicationSystemId", asId).setField("applicationOid", application.getOid());
        Changes.Builder changes = new Changes.Builder()
                .added("state", application.getState().name())
                .added("received", application.getReceived().toString())
                .added("appplicationOid", application.getOid());
        apiAuditLogger.log(apiAuditLogger.getUser(), HakuOperation.CREATE_NEW_APPLICATION, target.build(), changes.build());

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
    public Application postProcessApplicationAnswers(Application application, Duration postProcessorValintaTimeout) throws ValintaServiceCallFailedException {
        Map<String, String> hakutoiveetAnswers = application.getAnswers().get(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        String lang = application.getMeta().get(Application.META_FILING_LANGUAGE);
        hakutoiveetAnswers = ensureApplicationOptionGroupData(hakutoiveetAnswers, lang);
        ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        if(FormParameters.kysytaankoHarkinnanvaraisuus(as)) {
            checkKoulutusToAutomaticDiscretionary(application, hakutoiveetAnswers);
        }
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, hakutoiveetAnswers);

        if (hakuService.kayttaaJarjestelmanLomaketta(application.getApplicationSystemId()) && !application.isDraft()) {
            Application applicationWithValintaData = getApplicationWithValintadata(application.clone(), Optional.of(postProcessorValintaTimeout));
            application = removeOrphanedAnswers(application, applicationWithValintaData);
            ValidationResult validationResult = validateApplication(applicationWithValintaData);
            if (validationResult.hasErrors()) {
                application.incomplete();
            } else {
                application.activate();
            }
        }
        return application;
    }

    private ValidationResult validateApplication(final Application application) {
        Map<String, String> allAnswers = application.getVastauksetMerged();
        Form form = formService.getForm(application.getApplicationSystemId());
        ValidationInput validationInput = new ValidationInput(form, allAnswers,
                application.getOid(), application.getApplicationSystemId(), ValidationInput.ValidationContext.background);
        return elementTreeValidator.validate(validationInput);
    }

    private void checkKoulutusToAutomaticDiscretionary(final Application application, Map<String, String> hakutoiveetAnswers) {
        final Map<String, String> koulutustaustaAnswers = application.getAnswers().get(OppijaConstants.PHASE_EDUCATION);
        if (onkoKeskeytynytTaiUlkomainenTutkinto(koulutustaustaAnswers)) {
            updateKoulutusToDiscretionary(application.getOid(), hakutoiveetAnswers);
        }
    }

    private void updateKoulutusToDiscretionary(String oid, Map<String, String> hakutoiveetAnswers) {
        for (int i = 1; i < 20; i++) {
          if (hakutoiveetAnswers.containsKey("preference" + i +"-Koulutus-id")) {
              final String discretionary = String.format(PREFERENCE_DISCRETIONARY, i);
              final String followUp = String.format(PREFERENCE_DISCRETIONARY, i) + "-follow-up";
              updateAndLog(oid, hakutoiveetAnswers, discretionary, "true");
              updateAndLog(oid, hakutoiveetAnswers, followUp, HakutoiveetPhase.TODISTUSTENPUUTTUMINEN);
          }
        }
    }

    private boolean onkoKeskeytynytTaiUlkomainenTutkinto(Map<String, String> koulutustaustaAnswers) {
        return KESKEYTYNYT.equals(koulutustaustaAnswers.get(ELEMENT_ID_BASE_EDUCATION))
                || ULKOMAINEN_TUTKINTO.equals(koulutustaustaAnswers.get(ELEMENT_ID_BASE_EDUCATION));
    }

    private void updateAndLog(String oid, Map<String, String> hakutoiveet, String key, String value) {
        LOGGER.info("PostProcess discretionary update application oid={} {}={}", oid, key, value);
        hakutoiveet.put(key, value);
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
