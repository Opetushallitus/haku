package fi.vm.sade.haku.oppija.ui.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.ui.service.ModelResponse;
import fi.vm.sade.haku.oppija.ui.service.OfficerUIService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OfficerUIServiceImpl implements OfficerUIService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerUIServiceImpl.class);
    public static final String PHASE_ID_PREVIEW = "esikatselu";

    private final ApplicationService applicationService;
    private final FormService formService;
    private final KoodistoService koodistoService;
    private final HakuPermissionService hakuPermissionService;
    private final String koulutusinformaatioBaseUrl;
    private final LoggerAspect loggerAspect;
    private final ElementTreeValidator elementTreeValidator;
    private final ApplicationSystemService applicationSystemService;
    private final AuthenticationService authenticationService;
    private final OrganizationService organizationService;
    private final UserSession userSession;

    private static final List<Integer> syyskausi = ImmutableList.of(Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER,
            Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER);

    @Autowired
    public OfficerUIServiceImpl(final ApplicationService applicationService,
                                final FormService formService,
                                final KoodistoService koodistoService,
                                final HakuPermissionService hakuPermissionService,
                                final LoggerAspect loggerAspect,
                                @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl,
                                final ElementTreeValidator elementTreeValidator,
                                final ApplicationSystemService applicationSystemService,
                                final AuthenticationService authenticationService,
                                final OrganizationService organizationService,
                                final UserSession userSession) {
        this.applicationService = applicationService;
        this.formService = formService;
        this.koodistoService = koodistoService;
        this.hakuPermissionService = hakuPermissionService;
        this.loggerAspect = loggerAspect;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
        this.elementTreeValidator = elementTreeValidator;
        this.applicationSystemService = applicationSystemService;
        this.authenticationService = authenticationService;
        this.organizationService = organizationService;
        this.userSession = userSession;
    }

    @Override
    public ModelResponse getApplicationElement(
            final String oid,
            final String phaseId,
            final String elementId,
            final boolean validate) {
        Application application = this.applicationService.getApplicationByOid(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationSystemId());
        Element element = new ElementTree(form).getChildById(elementId);
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, application.getVastauksetMerged(),
                oid, application.getApplicationSystemId()));
        return new ModelResponse(application, form, element, validationResult, koulutusinformaatioBaseUrl);
    }

    @Override
    public ModelResponse getValidatedApplication(final String oid, final String phaseId) {
        Application application = this.applicationService.getApplicationByOid(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationSystemId());
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, application.getVastauksetMerged(),
                oid, application.getApplicationSystemId()));
        Element element = form;
        if (!PHASE_ID_PREVIEW.equals(phaseId)) {
            element = new ElementTree(form).getChildById(application.getPhaseId());
        }
        String asId = application.getApplicationSystemId();
        ApplicationSystem as = applicationSystemService.getApplicationSystem(asId);
        ModelResponse modelResponse =
                new ModelResponse(application, form, element, validationResult, koulutusinformaatioBaseUrl);
        modelResponse.addObjectToModel("preview", PHASE_ID_PREVIEW.equals(phaseId));
        modelResponse.addObjectToModel("phaseEditAllowed", hakuPermissionService.userHasEditRoleToPhases(application, form));
        modelResponse.addObjectToModel("virkailijaDeleteAllowed", hakuPermissionService.userCanDeleteApplication(application));
        modelResponse.addObjectToModel("postProcessAllowed", hakuPermissionService.userCanPostProcess(application));
        modelResponse.addObjectToModel("applicationSystem", as);

        String sendingSchoolOid = application.getVastauksetMerged().get("lahtokoulu");
        if (sendingSchoolOid != null) {
            Organization sendingSchool = organizationService.findByOid(sendingSchoolOid);
            String sendingClass = application.getVastauksetMerged().get("lahtoluokka");
            modelResponse.addObjectToModel("sendingSchool", sendingSchool.getName());
            modelResponse.addObjectToModel("sendingClass", sendingClass);
        }

        String userOid = userSession.getUser().getUserName();
        if (userOid == null || userOid.equals(application.getPersonOid())) {
            Map<String, I18nText> errors = modelResponse.getErrorMessages();
            errors.put("common", ElementUtil.createI18NText("virkailija.hakemus.omanMuokkausKielletty", "messages", null));
            modelResponse.setErrorMessages(errors);
        }
        return modelResponse;
    }

    @Override
    public ModelResponse getAdditionalInfo(String oid) {
        return new ModelResponse(applicationService.getApplicationByOid(oid));
    }

    @Override
    public ModelResponse updateApplication(final String oid, final ApplicationPhase applicationPhase, User user) {

        Application application = this.applicationService.getApplicationByOid(oid);
        Application queryApplication = new Application(oid, application.getVersion());
        Application.State state = application.getState();
        if (state != null && state.equals(Application.State.PASSIVE)) {
            throw new ResourceNotFoundException("Passive application");
        }

        final Form form = formService.getForm(application.getApplicationSystemId());
        checkUpdatePermission(application, form, applicationPhase.getPhaseId());

        loggerAspect.logUpdateApplication(application, applicationPhase);

        Map<String, String> newPhaseAnswers = applicationPhase.getAnswers();
        application.addVaiheenVastaukset(applicationPhase.getPhaseId(), newPhaseAnswers);

        Map<String, String> allAnswers = application.getVastauksetMergedIgnoringPhase(applicationPhase.getPhaseId());

        allAnswers.putAll(newPhaseAnswers);
        ValidationResult formValidationResult = elementTreeValidator.validate(new ValidationInput(form,
                allAnswers, oid, application.getApplicationSystemId()));
        if (formValidationResult.hasErrors()) {
            application.incomplete();
        } else {
            application.activate();
        }
        Element phase = new ElementTree(form).getChildById(applicationPhase.getPhaseId());
        ValidationResult phaseValidationResult = elementTreeValidator.validate(new ValidationInput(phase,
                allAnswers, oid, application.getApplicationSystemId()));

        String noteText = "Päivitetty vaihetta '" + applicationPhase.getPhaseId() + "'";
        application.addNote(createNote(noteText));
        this.applicationService.fillLOPChain(application, false);
        this.applicationService.addSendingSchool(application);
        this.applicationService.update(queryApplication, application);
        application.setPhaseId(applicationPhase.getPhaseId());
        return new ModelResponse(application, form, phase, phaseValidationResult, koulutusinformaatioBaseUrl);
    }

    private void checkUpdatePermission(Application application, Form form, String phaseId) {
        Boolean permission = hakuPermissionService.userHasEditRoleToPhases(application, form).get(phaseId);
        if (permission == null || !permission) {
            throw new ResourceNotFoundException("User can not update application " + application.getOid());
        }
    }

    @Override
    public Application getApplicationWithLastPhase(final String oid) {
        Application application = applicationService.getApplicationByOid(oid);
        application.setPhaseId("esikatselu");
        return application;
    }

    @Override
    public ModelResponse getOrganizationAndLearningInstitutions() {
        ModelResponse modelResponse = new ModelResponse();

        List<Option> organizationTypes = new ArrayList<Option>();
        for (OrganisaatioTyyppi ot : OrganisaatioTyyppi.values()) {
            organizationTypes.add(new Option(ElementUtil.createI18NAsIs(ot.value()), ot.value()));
        }
        List<ApplicationSystem> applicationSystems =
                applicationSystemService.getAllApplicationSystems("id", "name", "hakukausiUri", "hakukausiVuosi");
        modelResponse.addObjectToModel("applicationSystems", applicationSystems);
        modelResponse.addObjectToModel("organizationTypes", organizationTypes);
        modelResponse.addObjectToModel("learningInstitutionTypes", koodistoService.getLearningInstitutionTypes());
        modelResponse.addObjectToModel("hakukausiOptions", koodistoService.getHakukausi());
        modelResponse.addObjectToModel("applicationEnterAllowed", hakuPermissionService.userCanEnterApplication());
        modelResponse.addObjectToModel("sendingSchoolAllowed", hakuPermissionService.userCanSearchBySendingSchool());
        Calendar today = GregorianCalendar.getInstance();
        String semester = "kausi_k";
        if (syyskausi.contains(Integer.valueOf(today.get(Calendar.MONTH)))) {
            semester = "kausi_s";
        }
        modelResponse.addObjectToModel("defaultYear", String.valueOf(today.get(Calendar.YEAR)));
        modelResponse.addObjectToModel("defaultSemester", semester);
        return modelResponse;
    }

    @Override
    public List<ApplicationSystem> getApplicationSystems() {
        return applicationSystemService.getAllApplicationSystems("id", "name", "hakukausiUri", "hakukausiVuosi");
    }

    @Override
    public void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo) {
        applicationService.saveApplicationAdditionalInfo(oid, additionalInfo);
    }

    @Override
    public void addPersonAndAuthenticate(String oid) {
        Application application = applicationService.getApplicationByOid(oid);
        applicationService.fillLOPChain(application, false);
        applicationService.addPersonOid(application);
        application.activate();
        applicationService.update(new Application(oid), application);
    }

    @Override
    public Application activateApplication(String oid, String reason) {
        reason = "Hakemus aktivoitu: " + reason;
        addNote(oid, reason);
        return applicationService.activateApplication(oid);
    }

    @Override
    public ModelResponse getMultipleApplicationResponse(String applicationList, String selectedApplication) {
        Application application = applicationService.getApplicationByOid(selectedApplication);

        String[] apps = applicationList.split(",");
        String prev = null;
        String next = null;
        int curr = 0;
        for (int i = 0; i < apps.length; i++) {
            if (apps[i].equals(selectedApplication)) {
                curr = i + 1;
                if (i >= 1) {
                    prev = apps[i - 1];
                }
                if (i <= apps.length - 2) {
                    next = apps[i + 1];
                }
                break;
            }
        }

        String prevApplicant = null;
        if (null != prev) {
            Application prevApp = applicationService.getApplicationByOid(prev);
            Map<String, String> prevHenk = prevApp.getPhaseAnswers("henkilotiedot");
            prevApplicant = prevHenk.get("Etunimet") + " " + prevHenk.get("Sukunimi");
        }
        String nextApplicant = null;
        if (null != next) {
            Application nextApp = applicationService.getApplicationByOid(next);
            Map<String, String> nextHenk = nextApp.getPhaseAnswers("henkilotiedot");
            nextApplicant = nextHenk.get("Etunimet") + " " + nextHenk.get("Sukunimi");
        }
        ModelResponse response = getValidatedApplication(application.getOid(), "esikatselu");
        response.addObjectToModel("previousApplication", prev);
        response.addObjectToModel("previousApplicant", prevApplicant);
        response.addObjectToModel("nextApplication", next);
        response.addObjectToModel("nextApplicant", nextApplicant);
        response.addObjectToModel("currentApplication", String.valueOf(curr));
        response.addObjectToModel("applicationCount", apps.length);
        response.addObjectToModel("applicationList", applicationList);
        response.addObjectToModel("selectedApplication", selectedApplication);
        return response;
    }

    @Override
    public List<Map<String, Object>> getSchools(String term) {
        OrganisaatioSearchCriteria crit = new OrganisaatioSearchCriteria();
        crit.setOrganisaatioTyyppi(OrganisaatioTyyppi.OPPILAITOS.value());
        crit.setSearchStr(term);
        crit.setSkipParents(true);
        List<Organization> orgs = organizationService.search(crit);
        List<Map<String, Object>> schools = new ArrayList<Map<String, Object>>(orgs.size());
        LOGGER.debug("Fetching schools with term: '{}', got {} organizations", term, orgs.size());
        int resultCount = 20;
        for (Organization org : orgs) {
            // This IF is stupid, but necessary. Asking organisaatioService for 'oppilaitos' returns also
            // organisaatios with type of 'opetuspiste' and 'oppisopimustoimipiste'.
            LOGGER.debug("Org: " + org.getOid() + " Types: [" + Joiner.on(",").join(org.getTypes()) + "]");
            if (!org.getTypes().contains("OPPILAITOS")) {
                continue;
            }
            I18nText name = org.getName();
            Map<String, Object> school = new HashMap<String, Object>();
            school.put("name", name.getTranslations());
            school.put("dataId", org.getOid());
            schools.add(school);
            if (--resultCount < 0) {
                break;
            }
        }
        return schools;
    }

    @Override
    public List<Map<String, Object>> getPreferences(String term) {
        term = term.toLowerCase();
        List<Option> preferences = koodistoService.getHakukohdekoodit();
        List<Map<String, Object>> matchingPreferences = new ArrayList<Map<String, Object>>(20);
        Iterator<Option> prefIterator = preferences.iterator();
        while (prefIterator.hasNext() && matchingPreferences.size() <= 20) {
            Option pref = prefIterator.next();
            Map<String, String> translations = pref.getI18nText().getTranslations();
            for (String tran : translations.values()) {
                if (tran.toLowerCase().startsWith(term) || pref.getValue().equals(term)) {
                    Map<String, Object> matchingPref = new HashMap<String, Object>(2);
                    matchingPref.put("name", translations);
                    matchingPref.put("dataId", pref.getValue());
                    matchingPreferences.add(matchingPref);
                    break;
                }
            }
        }
        return matchingPreferences;
    }

    @Override
    public void passivateApplication(final String oid, final String reason) {
        Application application = applicationService.getApplicationByOid(oid);
        application.addNote(createNote("Hakemus passivoitu: " + reason));
        application.passivate();
        applicationService.update(new Application(oid), application);
    }

    @Override
    public void addNote(String applicationOid, String note) {
        Application application = applicationService.getApplicationByOid(applicationOid);
        application.addNote(createNote(note));
        applicationService.update(new Application(applicationOid), application);
    }

    @Override
    public Application createApplication(final String asId) {
        return applicationService.officerCreateNewApplication(asId);
    }

    @Override
    public ModelResponse addStudentOid(String oid) {
        Application application = applicationService.getApplicationByOid(oid);
        String studentOid = application.getPersonOid();
        if (!Strings.isNullOrEmpty(application.getStudentOid())) {
            throw new IllegalStateException("Student oid is already set");
        } else if (Strings.isNullOrEmpty(studentOid)) {
            throw new IllegalArgumentException("Invalid student oid");
        }
        authenticationService.getStudentOid(studentOid);
        application.setStudentOid(studentOid);
        application.addNote(createNote("Oppijanumero syötetty"));
        Application queryApplication = new Application(oid);
        applicationService.update(queryApplication, application);
        return getValidatedApplication(oid, PHASE_ID_PREVIEW);
    }

    @Override
    public ModelResponse postProcess(String oid) {
        Application application = applicationService.getApplicationByOid(oid);
        application = applicationService.fillLOPChain(application, false);
        application = applicationService.addPersonOid(application);
        application = applicationService.addSendingSchool(application);
        application.activate();
        applicationService.update(new Application(oid), application);
        return getValidatedApplication(oid, PHASE_ID_PREVIEW);
    }

    @Override
    public ModelResponse getApplicationPrint(final String oid) {
        Application application = applicationService.getApplicationByOid(oid);
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(application.getApplicationSystemId());
        List<String> discretionaryAttachmentAOIds = ApplicationUtil.getDiscretionaryAttachmentAOIds(application);
        return new ModelResponse(application, activeApplicationSystem, discretionaryAttachmentAOIds, koulutusinformaatioBaseUrl);
    }

    private ApplicationNote createNote(String note) {
        return new ApplicationNote(note, new Date(), userSession.getUser().getUserName());
    }

}
