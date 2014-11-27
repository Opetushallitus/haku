package fi.vm.sade.haku.oppija.ui.service.impl;

import com.google.common.base.Strings;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationGroupRestDTO;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.*;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.Pistetieto;
import fi.vm.sade.haku.oppija.hakemus.domain.util.AttachmentUtil;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.exception.IllegalStateException;
import fi.vm.sade.haku.oppija.lomake.exception.IncoherentDataException;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.ui.controller.dto.AttachmentDTO;
import fi.vm.sade.haku.oppija.ui.controller.dto.AttachmentsAndEligibilityDTO;
import fi.vm.sade.haku.oppija.ui.service.OfficerUIService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.haku.virkailija.valinta.dto.*;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class OfficerUIServiceImpl implements OfficerUIService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerUIServiceImpl.class);
    public static final String PHASE_ID_PREVIEW = "esikatselu";

    private final ApplicationService applicationService;
    private final BaseEducationService baseEducationService;
    private final FormService formService;
    private final KoodistoService koodistoService;
    private final HakuPermissionService hakuPermissionService;
    private final String koulutusinformaatioBaseUrl;
    private final String tarjontaUrl;
    private final LoggerAspect loggerAspect;
    private final ElementTreeValidator elementTreeValidator;
    private final ApplicationSystemService applicationSystemService;
    private final AuthenticationService authenticationService;
    private final OrganizationService organizationService;
    private ValintaService valintaService;
    private final UserSession userSession;
    private final KoulutusinformaatioService koulutusinformaatioService;

    private static final DecimalFormat PISTE_FMT = new DecimalFormat("#.##");

    private static final String KAUSI_FORMAT_STRING = "dd.MM.yyyy";
    private final String kevatkausi;

    @Autowired
    public OfficerUIServiceImpl(final ApplicationService applicationService,
                                final BaseEducationService baseEducationService,
                                final FormService formService,
                                final KoodistoService koodistoService,
                                final HakuPermissionService hakuPermissionService,
                                final LoggerAspect loggerAspect,
                                @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl,
                                @Value("${tarjonta.v1.hakukohde.resource.url}") final String tarjontaUrl,
                                final ElementTreeValidator elementTreeValidator,
                                final ApplicationSystemService applicationSystemService,
                                final AuthenticationService authenticationService,
                                final OrganizationService organizationService,
                                final ValintaService valintaService,
                                final UserSession userSession,
                                final KoulutusinformaatioService koulutusinformaatioService,
                                @Value("${hakukausi.kevat}") final String kevatkausi) {
        this.applicationService = applicationService;
        this.baseEducationService = baseEducationService;
        this.formService = formService;
        this.koodistoService = koodistoService;
        this.hakuPermissionService = hakuPermissionService;
        this.loggerAspect = loggerAspect;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
        this.tarjontaUrl = tarjontaUrl;
        this.elementTreeValidator = elementTreeValidator;
        this.applicationSystemService = applicationSystemService;
        this.authenticationService = authenticationService;
        this.organizationService = organizationService;
        this.valintaService = valintaService;
        this.userSession = userSession;
        this.koulutusinformaatioService = koulutusinformaatioService;
        this.kevatkausi = kevatkausi;
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
                oid, application.getApplicationSystemId(), false));
        return new ModelResponse(application, form, element, validationResult, koulutusinformaatioBaseUrl);
    }

    @Override
    public ModelResponse getValidatedApplication(final String oid, final String phaseId) throws IOException {
        Application application = this.applicationService.getApplicationByOid(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationSystemId());
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, application.getVastauksetMerged(),
                oid, application.getApplicationSystemId(), false));
        Element element = form;
        if (!PHASE_ID_PREVIEW.equals(phaseId)) {
            element = new ElementTree(form).getChildById(application.getPhaseId());
        }
        String asId = application.getApplicationSystemId();
        boolean postProcessAllowed = hakuPermissionService.userCanPostProcess(application)
                && !Application.State.PASSIVE.equals(application.getState());
        ApplicationSystem as = applicationSystemService.getApplicationSystem(asId);
        ModelResponse modelResponse =
                new ModelResponse(application, form, element, validationResult, koulutusinformaatioBaseUrl);
        modelResponse.addObjectToModel("preview", PHASE_ID_PREVIEW.equals(phaseId));
        modelResponse.addObjectToModel("phaseEditAllowed", hakuPermissionService.userHasEditRoleToPhases(application, form));
        modelResponse.addObjectToModel("virkailijaDeleteAllowed", hakuPermissionService.userCanDeleteApplication(application));
        modelResponse.addObjectToModel("postProcessAllowed", postProcessAllowed);
        modelResponse.addObjectToModel("applicationSystem", as);

        modelResponse.addObjectToModel("hakukohteet", getValintatiedot(application));
        modelResponse.addObjectToModel("higherEd",
                OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(as.getKohdejoukkoUri()));

        String sendingSchoolOid = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL);
        if (sendingSchoolOid != null) {
            Organization sendingSchool = organizationService.findByOid(sendingSchoolOid);
            String sendingClass = application.getVastauksetMerged().get("lahtoluokka");
            modelResponse.addObjectToModel("sendingSchool", sendingSchool.getName());
            modelResponse.addObjectToModel("sendingClass", sendingClass);
        }

        String userOid = userSession.getUser().getUserName();
        if (userOid == null || userOid.equals(application.getPersonOid())) {
            Map<String, I18nText> errors = modelResponse.getErrorMessages();
            errors.put("common", ElementUtil.createI18NText("virkailija.hakemus.omanMuokkausKielletty", "messages"));
            modelResponse.setErrorMessages(errors);
        }
        return modelResponse;
    }

    private List<ApplicationOptionDTO> getValintatiedot(Application application) {
        HakijaDTO hakijaDTO = valintaService.getHakija(application.getApplicationSystemId(), application.getOid());
        Map<String, String> aoAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        Map<String, String> koulutusAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        String education = koulutusAnswers.get(OppijaConstants.ELEMENT_ID_BASE_EDUCATION);
        boolean showScores = education == null ||
                (!OppijaConstants.KESKEYTYNYT.equals(education)
                        && !OppijaConstants.ULKOMAINEN_TUTKINTO.equals(education));

        Map<String, HakutoiveDTO> hakijaMap = new HashMap<String, HakutoiveDTO>();
        for (HakutoiveDTO hakutoiveDTO : hakijaDTO.getHakutoiveet()) {
            hakijaMap.put(hakutoiveDTO.getHakukohdeOid(), hakutoiveDTO);
        }

        HakemusDTO hakemusDTO;
        Map<String, HakukohdeDTO> hakemusMap = new HashMap<String, HakukohdeDTO>();
        if (showScores) {
            hakemusDTO = valintaService.getHakemus(application.getApplicationSystemId(), application.getOid());
            for (HakukohdeDTO hakukohdeDTO : hakemusDTO.getHakukohteet()) {
                hakemusMap.put(hakukohdeDTO.getOid(), hakukohdeDTO);
            }
        }
        List<ApplicationOptionDTO> aos = new ArrayList<ApplicationOptionDTO>(5);
        for (int i = 1; i < 100; i++) {
            String aoPrefix = String.format("preference%d-", i);
            String aoKey = String.format("%sKoulutus-id", aoPrefix);
            String aoOid = aoAnswers.get(aoKey);
            if (isBlank(aoOid)) {
                break;
            }
            aos.add(createApplicationOption(i, aoAnswers, aoPrefix,
                    hakijaMap.get(aoOid), hakemusMap.get(aoAnswers.get(aoKey)), showScores));
        }
        return aos;
    }

    private ApplicationOptionDTO createApplicationOption(int index, Map<String, String> applicatioOptions,
                                                         String aoPrefix, HakutoiveDTO hakutoive,
                                                         HakukohdeDTO hakukohde, boolean showScores) {
        ApplicationOptionDTO ao = buildBasicAo(index, applicatioOptions, aoPrefix);

        if (hakutoive != null) {
            ao = addAdditionalApplicationOptionData(ao, hakutoive, showScores);
        }

        if (hakukohde == null || !showScores) {
            return ao;
        }

        // Lisätään pistetiedot valintakokeista ja kaikkien käytetyn valintatapajonon jonosijoista.
        for (ValinnanvaiheDTO vaihe : hakukohde.getValinnanvaihe()) {
            for (ValintakoeDTO koe : vaihe.getValintakokeet()) {
                ao.addPistetieto(buildPistetieto(hakutoive, koe));
            }

            for (ValintatapajonoDTO valintatapajonoDTO : vaihe.getValintatapajonot()) {
                if (ao.getJonoId() == null || valintatapajonoDTO.getOid().equals(ao.getJonoId())) {
                    for (JonosijaDTO jonosijaDTO : valintatapajonoDTO.getJonosijat()) {
                        for (FunktioTulosDTO funktioTulosDTO : jonosijaDTO.getFunktioTulokset()) {
                            ao.addPistetieto(buildPistetieto(funktioTulosDTO));
                        }
                    }
                }
            }
        }
        ao.sortPistetiedot();
        return ao;
    }

    private Pistetieto buildPistetieto(HakutoiveDTO hakutoive, ValintakoeDTO koe) {
        Pistetieto pistetieto = null;
        if (hakutoive != null) {
            for (PistetietoDTO pistetietoDTO : hakutoive.getPistetiedot()) {
                if (pistetietoDTO.getTunniste().equals(koe.getValintakoeTunniste())) {
                    pistetieto = new Pistetieto(pistetietoDTO);
                    break;
                }
            }
        }
        if (pistetieto == null) {
            pistetieto = new Pistetieto();
        }
        pistetieto.setNimi(ElementUtil.createI18NAsIs(koe.getNimi()));
        Osallistuminen osallistuminen = koe.getOsallistuminenTulos().getOsallistuminen();
        if (osallistuminen.equals(Osallistuminen.EI_OSALLISTU)) {
            pistetieto.setPisteet(null);
        }
        pistetieto.setOsallistuminen(osallistuminen);
        return pistetieto;
    }

    private ApplicationOptionDTO addAdditionalApplicationOptionData(ApplicationOptionDTO ao, HakutoiveDTO hakutoive,
                                                                    boolean showScores) {
        List<HakutoiveenValintatapajonoDTO> jonot = hakutoive.getHakutoiveenValintatapajonot();
        Collections.sort(jonot, new Comparator<HakutoiveenValintatapajonoDTO>() {
            @Override
            public int compare(HakutoiveenValintatapajonoDTO jono, HakutoiveenValintatapajonoDTO other) {
                return jono.getValintatapajonoPrioriteetti() - other.getValintatapajonoPrioriteetti();
            }
        });
        HakutoiveenValintatapajonoDTO jono = jonot.get(0);
        BigDecimal pisteet = jono.getPisteet();
        ao.setJonoId(jono.getValintatapajonoOid());
        ao.setYhteispisteet(pisteet != null && showScores ? PISTE_FMT.format(pisteet) : "");
        ao.setSijoittelunTulos(jono.getTila());
        ao.setVastaanottoTieto(jono.getVastaanottotieto());

        return ao;
    }

    private ApplicationOptionDTO buildBasicAo(int index, Map<String, String> applicatioOptions, String aoPrefix) {
        ApplicationOptionDTO ao = new ApplicationOptionDTO();
        String aoOid = applicatioOptions.get(String.format("%sKoulutus-id", aoPrefix));

        ao.setIndex(index);
        ao.setOpetuspiste(applicatioOptions.get(String.format("%sOpetuspiste", aoPrefix)));
        ao.setOpetuspisteOid(applicatioOptions.get(String.format("%sOpetuspiste-id", aoPrefix)));
        ao.setName(applicatioOptions.get(String.format("%sKoulutus", aoPrefix)));
        ao.setOid(aoOid);
        return ao;
    }

    private Pistetieto buildPistetieto(FunktioTulosDTO funktioTulosDTO) {
        Pistetieto pistetieto = new Pistetieto();
        pistetieto.setId(funktioTulosDTO.getTunniste());
        pistetieto.setNimi(buildFunktioNimet(funktioTulosDTO));
        pistetieto.setPisteet(funktioTulosDTO.getArvo());
        pistetieto.setOsallistuminen(null);
        return pistetieto;
    }

    private I18nText buildFunktioNimet(FunktioTulosDTO funktioTulosDTO) {
        String nimiFi = isNotBlank(funktioTulosDTO.getNimiFi()) ? funktioTulosDTO.getNimiFi() : funktioTulosDTO.getTunniste();
        String nimiSv = isNotBlank(funktioTulosDTO.getNimiSv()) ? funktioTulosDTO.getNimiSv() : funktioTulosDTO.getTunniste();
        String nimiEn = isNotBlank(funktioTulosDTO.getNimiEn()) ? funktioTulosDTO.getNimiEn() : funktioTulosDTO.getTunniste();
        Map<String, String> funktioNimet = new HashMap<String, String>(3);
        funktioNimet.put("fi", nimiFi);
        funktioNimet.put("sv", nimiSv);
        funktioNimet.put("en", nimiEn);
        return new I18nText(funktioNimet);
    }

    @Override
    public ModelResponse getAdditionalInfo(String oid) {
        return new ModelResponse(applicationService.getApplicationByOid(oid));
    }

    @Override
    public ModelResponse updateApplication(final String oid, final ApplicationPhase applicationPhase, User user) throws IOException {

        Application application = this.applicationService.getApplicationByOid(oid);
        Application queryApplication = new Application(oid, application.getVersion());
        Application.State state = application.getState();
        if (state != null && state.equals(Application.State.PASSIVE)) {
            throw new ResourceNotFoundException("Passive application");
        }

        final Form form = formService.getForm(application.getApplicationSystemId());
        checkUpdatePermission(application, form, applicationPhase.getPhaseId());

        Map<String, String> newPhaseAnswers = applicationPhase.getAnswers();
        newPhaseAnswers = handleGrades(application, applicationPhase, new HashMap<String, String>(newPhaseAnswers));

        loggerAspect.logUpdateApplication(application, applicationPhase);

        application.addVaiheenVastaukset(applicationPhase.getPhaseId(), newPhaseAnswers);

        Map<String, String> allAnswers = application.getVastauksetMergedIgnoringPhase(applicationPhase.getPhaseId());
        allAnswers.putAll(newPhaseAnswers);

        application = applicationService.removeOrphanedAnswers(application);

        ValidationResult formValidationResult = elementTreeValidator.validate(new ValidationInput(form,
                allAnswers, oid, application.getApplicationSystemId(), true));
        if (formValidationResult.hasErrors()) {
            application.incomplete();
        } else {
            application.activate();
        }
        Element phase = new ElementTree(form).getChildById(applicationPhase.getPhaseId());
        ValidationResult phaseValidationResult = elementTreeValidator.validate(new ValidationInput(phase,
                allAnswers, oid, application.getApplicationSystemId(), true));

        String noteText = "Päivitetty vaihetta '" + applicationPhase.getPhaseId() + "'";
        application.addNote(createNote(noteText));
        this.applicationService.updatePreferenceBasedData(application);
        this.applicationService.updateAuthorizationMeta(application);
        this.applicationService.update(queryApplication, application);
        application.setPhaseId(applicationPhase.getPhaseId());
        return new ModelResponse(application, form, phase, phaseValidationResult, koulutusinformaatioBaseUrl);
    }

    private Map<String, String> handleGrades(Application application, ApplicationPhase phase, Map<String, String> newAnswers) {
        Map<String, String> meta = application.getMeta();
        Boolean gradesTransferredLk = Boolean.valueOf(meta.get("grades_transferred_lk"));
        Boolean gradesTransferredPk = Boolean.valueOf(meta.get("grades_transferred_pk"));
        if (phase.getPhaseId().equals(OppijaConstants.PHASE_GRADES) && gradesTransferredLk || gradesTransferredPk) {
            Map<String, String> grades = new HashMap<String, String>(application.getPhaseAnswers(OppijaConstants.PHASE_GRADES));

            for (Map.Entry<String, String> entry : newAnswers.entrySet()) {
                String key = entry.getKey();
                if ((key.startsWith("LK_") || key.startsWith("PK_")) && grades.containsKey(key)) {
                    String value = entry.getValue();
                    String oldValue = grades.get(key);
                    if (!value.equals(oldValue)) {
                        throw new IllegalStateException("Trying to change transferred grades");
                    }
                }
            }
            for (Map.Entry<String, String> entry : grades.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("LK_") || key.startsWith("PK_")) {
                    newAnswers.put(key, entry.getValue());
                }
            }
        }

        return newAnswers;
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
            organizationTypes.add((Option) OptionBuilder.Option("organizationTypes")
                    .setValue(ot.value())
                    .i18nText(ElementUtil.createI18NAsIs(ot.value()))
                    .build());
        }
        List<ApplicationSystem> applicationSystems = getApplicationSystems();
        modelResponse.addObjectToModel("applicationSystems", applicationSystems);
        modelResponse.addObjectToModel("organizationTypes", organizationTypes);
        List<Option> institutionTypes = new ArrayList<Option>();
        for (Option institution : koodistoService.getLearningInstitutionTypes()) {
            String value = institution.getValue();
            value = value.replaceAll("#[0-9]$", "#*");
            institutionTypes.add(new Option(institution.getI18nText(), value));
        }
        modelResponse.addObjectToModel("learningInstitutionTypes", institutionTypes);
        modelResponse.addObjectToModel("hakukausiOptions", koodistoService.getHakukausi());
        modelResponse.addObjectToModel("applicationEnterAllowed", hakuPermissionService.userCanEnterApplication());
        modelResponse.addObjectToModel("sendingSchoolAllowed", hakuPermissionService.userCanSearchBySendingSchool());
        Calendar today = GregorianCalendar.getInstance();
        String semester = "kausi_s";
        String[] kevatkausiDates = kevatkausi.split("-");
        SimpleDateFormat dateFormat = new SimpleDateFormat(KAUSI_FORMAT_STRING);
        try {
            Date kevatkausiAlkaa = dateFormat.parse(kevatkausiDates[0].trim() + "." + today.get(Calendar.YEAR));
            Date kevatkausiLoppuu = dateFormat.parse(kevatkausiDates[1].trim() +"."+today.get(Calendar.YEAR));
            if (today.getTime().after(kevatkausiAlkaa) && today.getTime().before(kevatkausiLoppuu)) {
                semester = "kausi_k";
            }
        } catch (ParseException e) {
            LOGGER.error("Couldn't parse kevatkausi dates: {}", kevatkausi);
        }
        modelResponse.addObjectToModel("defaultYear", String.valueOf(today.get(Calendar.YEAR)));
        modelResponse.addObjectToModel("defaultSemester", semester);
        modelResponse.addObjectToModel("tarjontaUrl", tarjontaUrl);
        return modelResponse;
    }

    @Override
    public List<ApplicationSystem> getApplicationSystems() {
        return applicationSystemService.getPublishedApplicationSystems("id", "name", "hakukausiUri", "hakukausiVuosi",
                "maxApplicationOptions", "kohdejoukkoUri");
    }

    @Override
    public void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo) {
        applicationService.saveApplicationAdditionalInfo(oid, additionalInfo);
    }

    @Override
    public ModelResponse getMultipleApplicationResponse(String applicationList, String selectedApplication) throws IOException {
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
    public List<Map<String, Object>> getSchools(String term) throws UnsupportedEncodingException {
        OrganisaatioSearchCriteria crit = new OrganisaatioSearchCriteria();
        crit.setOrganisaatioTyyppi(OrganisaatioTyyppi.OPPILAITOS.value());
        crit.setSearchStr(term);
        crit.setSkipParents(true);
        crit.setVainAktiiviset(true);
        List<Organization> orgs = organizationService.search(crit);
        List<Map<String, Object>> schools = new ArrayList<Map<String, Object>>(orgs.size());
        LOGGER.debug("Fetching schools with term: '{}', got {} organizations", term, orgs.size());
        int resultCount = 20;
        for (Organization org : orgs) {
            if (org.getOppilaitostyyppi() == null) {
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
    public List<Map<String, Object>> getGroups(String term) throws IOException {
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for (OrganizationGroupRestDTO group : organizationService.findGroups(term)) {
            Map<String, Object> orgGroup = new HashMap<String, Object>();
            orgGroup.put("name", group.getNimi().getTranslations());
            orgGroup.put("dataId", group.getOid());
            groups.add(orgGroup);
        }
        return groups;
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
    public List<Map<String, String>> getHigherEdBaseEdOptions() {
        List<Map<String, String>> options = new ArrayList<Map<String, String>>();
        for (String ed : new String[] {"yo", "am", "amt", "kk", "ulk", "avoin", "muu"}) {
            Map<String, String> opt = new HashMap<String, String>();
            opt.put("value", ed);
            Map<String, String> trans = ElementUtil.createI18NText("virkailija.hakemus.pohjakoulutus."+ed, "messages").getTranslations();
            for (String lang : new String[] {"fi", "sv", "en"}) {
                opt.put("name_"+lang, trans.get(lang));
            }
            options.add(opt);
        }
        return options;
    }

    @Override
    public void changeState(final String oid, Application.State state, String reason) {
        Application application = applicationService.getApplicationByOid(oid);

        if (state.equals(Application.State.PASSIVE)) { // TODO no jaa
            reason = "Hakemus passivoitu: " + reason;
        } else if (state.equals(Application.State.ACTIVE)) {
            reason = "Hakemus aktivoitu: " + reason;
        }
        application.addNote(createNote(reason));
        application.setState(state);
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
    public void addStudentOid(String oid) {
        Application application = applicationService.getApplicationByOid(oid);
        String studentOid = application.getPersonOid();
        if (!Strings.isNullOrEmpty(application.getStudentOid())) {
            throw new IllegalStateException("Student oid is already set");
        } else if (Strings.isNullOrEmpty(studentOid)) {
            throw new IllegalArgumentException("Invalid student oid");
        }
        Person person = authenticationService.getStudentOid(studentOid);
        if (person != null) {
            application.modifyPersonalData(person);
            application.addNote(createNote("Oppijanumero syötetty"));
        }
        Application queryApplication = new Application(oid);
        applicationService.update(queryApplication, application);
    }

    @Override
    public void postProcess(String oid, boolean email) {
        Application application = applicationService.getApplicationByOid(oid);
        application.setRedoPostProcess(email ? Application.PostProcessingState.FULL: Application.PostProcessingState.NOMAIL);
        applicationService.update(new Application(oid), application);
    }

    @Override
    public ModelResponse getApplicationPrint(final String oid) {
        Application application = applicationService.getApplicationByOid(oid);
        ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());

        return new ModelResponse(application,
                applicationSystem,
                AttachmentUtil.resolveAttachments(application),
                koulutusinformaatioBaseUrl);
    }

    private ApplicationNote createNote(String note) {
        return new ApplicationNote(note, new Date(), userSession.getUser().getUserName());
    }

    public void setValintaService(ValintaService valintaService) {
        this.valintaService = valintaService;
    }

    @Override
    public void processAttachmentsAndEligibility(String oid, List<AttachmentsAndEligibilityDTO> attachementsAndEligibilities) {
        LOGGER.debug("Got attachementsAndEligibilities " + StringUtils.join(attachementsAndEligibilities, ","));
        final Application application = applicationService.getApplicationByOid(oid);
        final HashMap<String, AttachmentDTO> attachmentDTOs = new HashMap<String, AttachmentDTO>();
        for (AttachmentsAndEligibilityDTO dto : attachementsAndEligibilities){
            PreferencePredicate predicate = new PreferencePredicate(dto.getAoId());
            PreferenceEligibility preferenceEligibility = (PreferenceEligibility) CollectionUtils.find(application.getPreferenceEligibilities(), predicate);
            if (null == preferenceEligibility) {
                LOGGER.error("No preference found with " + dto.getAoId() + " for application " + oid);
                throw new IncoherentDataException("No preference found with " + dto.getAoId() + " for application " + oid);
            }
            preferenceEligibility.setStatus(PreferenceEligibility.Status.valueOf(dto.getStatus()));
            preferenceEligibility.setSource(PreferenceEligibility.Source.valueOf(dto.getSource()));
            preferenceEligibility.setRejectionBasis(dto.getRejectionBasis());

            PreferenceChecked preferenceChecked = (PreferenceChecked) CollectionUtils.find(application.getPreferencesChecked(), predicate);
            preferenceChecked.setChecked(dto.getPreferencesChecked());
            if (dto.getPreferencesChecked()){
                preferenceChecked.setCheckedByOfficerOid(userSession.getUser().getUserName());
            }
            for (AttachmentDTO attachmentDTO : dto.getAttachments()){
                AttachmentDTO old = attachmentDTOs.put(attachmentDTO.getId(), attachmentDTO);
                if (null != old){
                    LOGGER.debug("Got duplicates old: {}, new {}", old, attachmentDTO);
                    if (!old.equals(attachmentDTO)){
                        LOGGER.error("Duplicates do not match old: {}, new {}", old, attachmentDTO);
                        throw new IncoherentDataException("Multiple values for attachment proceesing with mismatching data");
                    }
                }
            }
        }
        for (ApplicationAttachmentRequest attachment : application.getAttachmentRequests()){
            AttachmentDTO dto = attachmentDTOs.get(attachment.getId());
            if (null != dto) {
                attachment.setReceptionStatus(ApplicationAttachmentRequest.ReceptionStatus.valueOf(dto.getReceptionStatus()));
                attachment.setProcessingStatus(ApplicationAttachmentRequest.ProcessingStatus.valueOf(dto.getProcessingStatus()));
            }
        }

        applicationService.update(new Application(oid), application);
    }

    private class PreferencePredicate implements Predicate {
        private final  String preferenceAoId;

        private PreferencePredicate(String preferenceAoId) {
            this.preferenceAoId = preferenceAoId;
        }

        @Override
        public boolean evaluate(Object object) {
            if (object instanceof PreferenceEligibility)
                return preferenceAoId.equals(((PreferenceEligibility) object).getAoId());
            if (object instanceof PreferenceChecked)
                return preferenceAoId.equals(((PreferenceChecked) object).getPreferenceAoOid());
            return false;
        }
    }
}
