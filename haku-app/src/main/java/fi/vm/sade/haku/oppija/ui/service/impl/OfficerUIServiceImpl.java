package fi.vm.sade.haku.oppija.ui.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fi.vm.sade.auditlog.haku.HakuOperation;
import fi.vm.sade.auditlog.haku.LogMessage;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationGroupRestDTO;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.*;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.Pistetieto;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.UpdatePreferenceResult;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.oppija.hakemus.domain.util.AttachmentUtil;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
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
import fi.vm.sade.haku.oppija.lomake.service.Session;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.ui.controller.dto.AttachmentDTO;
import fi.vm.sade.haku.oppija.ui.controller.dto.AttachmentsAndEligibilityDTO;
import fi.vm.sade.haku.oppija.ui.controller.dto.EligibilitiesDTO;
import fi.vm.sade.haku.oppija.ui.service.OfficerUIService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.haku.virkailija.valinta.ValintaServiceCallFailedException;
import fi.vm.sade.haku.virkailija.valinta.dto.*;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import fi.vm.sade.properties.OphProperties;
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

import static fi.vm.sade.haku.AuditHelper.AUDIT;
import static fi.vm.sade.haku.AuditHelper.builder;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.paymentNotificationAnswers;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class OfficerUIServiceImpl implements OfficerUIService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerUIServiceImpl.class);
    public static final String PHASE_ID_PREVIEW = "esikatselu";

    private final ApplicationService applicationService;
    private final FormService formService;
    private final KoodistoService koodistoService;
    private final HakuPermissionService hakuPermissionService;
    private final LoggerAspect loggerAspect;
    private final OphProperties urlConfiguration;
    private final ElementTreeValidator elementTreeValidator;
    private final ApplicationSystemService applicationSystemService;
    private final AuthenticationService authenticationService;
    private final OrganizationService organizationService;
    private ValintaService valintaService;
    private final Session userSession;
    private final I18nBundleService i18nBundleService;
    private final HakumaksuService hakumaksuService;

    private static final DecimalFormat PISTE_FMT = new DecimalFormat("#.##");

    private static final String KAUSI_FORMAT_STRING = "dd.MM.yyyy";
    private final String kevatkausi;

    @Autowired
    public OfficerUIServiceImpl(final ApplicationService applicationService,
                                final FormService formService,
                                final KoodistoService koodistoService,
                                final HakuPermissionService hakuPermissionService,
                                final LoggerAspect loggerAspect,
                                OphProperties urlConfiguration,
                                final ElementTreeValidator elementTreeValidator,
                                final ApplicationSystemService applicationSystemService,
                                final AuthenticationService authenticationService,
                                final OrganizationService organizationService,
                                final ValintaService valintaService,
                                final Session userSession,
                                final I18nBundleService i18nBundleService,
                                HakumaksuService hakumaksuService, @Value("${hakukausi.kevat}") final String kevatkausi) {
        this.applicationService = applicationService;
        this.formService = formService;
        this.koodistoService = koodistoService;
        this.hakuPermissionService = hakuPermissionService;
        this.loggerAspect = loggerAspect;
        this.urlConfiguration = urlConfiguration;
        this.elementTreeValidator = elementTreeValidator;
        this.applicationSystemService = applicationSystemService;
        this.authenticationService = authenticationService;
        this.organizationService = organizationService;
        this.valintaService = valintaService;
        this.userSession = userSession;
        this.i18nBundleService = i18nBundleService;
        this.hakumaksuService = hakumaksuService;
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
        ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        Form form = as.getForm();
        Element element = form.getChildById(elementId);
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, application.getVastauksetMerged(),
                oid, application.getApplicationSystemId(), ValidationInput.ValidationContext.officer_modify));
        ModelResponse modelResponse = new ModelResponse(application, form, element, validationResult);
        modelResponse.addObjectToModel("baseEducationDoesNotRestrictApplicationOptions", as.baseEducationDoesNotRestrictApplicationOptions());
        return modelResponse;
    }

    @Override
    public ModelResponse getApplicationMultiElement(
            final String oid,
            final String phaseId,
            final List<String> elementIds,
            final boolean validate,
            final Map<String, String> currentAnswers) {
        Application application = this.applicationService.getApplicationByOid(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationSystemId());

        List<Element> elements = new ArrayList<>();
        if (elementIds != null) {
            for (String elementId : elementIds) {
                elements.add(form.getChildById(elementId));
            }
        }

        final Map<String, String> answers = new HashMap<>();

        answers.putAll(application.getVastauksetMerged());
        answers.putAll(currentAnswers);

        if ((PHASE_APPLICATION_OPTIONS.equals(phaseId) || PHASE_EDUCATION.equals(phaseId))
                && applicationSystemService.getApplicationSystem(application.getApplicationSystemId()).isMaksumuuriKaytossa()) {
            try {
                answers.putAll(paymentNotificationAnswers(answers, hakumaksuService.paymentRequirements(Types.MergedAnswers.of(answers))));
            } catch (NullPointerException | IllegalArgumentException e) {
                // FIXME: Answereissa voi olla puutteellista dataa, koska tätä ajetaan joka kentän syöttämisen jälkeen. Validoi HakumaksuServicessä?
            }
        }

        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, application.getVastauksetMerged(),
                oid, application.getApplicationSystemId(), ValidationInput.ValidationContext.officer_modify));
        ModelResponse modelResponse = new ModelResponse(application, form, elements, validationResult);
        modelResponse.addAnswers(answers);

        return modelResponse;
    }


    @Override
    public ModelResponse getValidatedApplication(final String oid, final String phaseId) throws IOException {
        Map<String, I18nText> virkailijaErrors = new HashMap<>();
        Application application = getApplicationWithValintadataIfNotDraft(oid, virkailijaErrors);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationSystemId());
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, application.getVastauksetMerged(),
                oid, application.getApplicationSystemId(), ValidationInput.ValidationContext.officer_modify));
        Element element = form;
        if (!PHASE_ID_PREVIEW.equals(phaseId)) {
            element = form.getChildById(application.getPhaseId());
        }
        String asId = application.getApplicationSystemId();
        boolean postProcessAllowed = hakuPermissionService.userCanPostProcess(application)
                && !Application.State.PASSIVE.equals(application.getState());
        ApplicationSystem as = applicationSystemService.getApplicationSystem(asId);

        if ((phaseId.equals(PHASE_APPLICATION_OPTIONS) || phaseId.equals(PHASE_EDUCATION)) && as.isMaksumuuriKaytossa()) {
            Map<String, String> vastauksetMerged = application.getVastauksetMerged();
            ImmutableMap<String, String> answersForPaymentNotification = paymentNotificationAnswers(vastauksetMerged, hakumaksuService.paymentRequirements(Types.MergedAnswers.of(vastauksetMerged)));
            String originalPhaseId = application.getPhaseId();
            application.setVaiheenVastauksetAndSetPhaseId(PHASE_APPLICATION_OPTIONS,
                    ImmutableMap.<String, String>builder()
                            .putAll(application.getPhaseAnswers(phaseId))
                            .putAll(answersForPaymentNotification)
                            .build()).setPhaseId(originalPhaseId);
        }

        ModelResponse modelResponse =
                new ModelResponse(application, form, element, validationResult);
        modelResponse.addObjectToModel("preview", PHASE_ID_PREVIEW.equals(phaseId));
        modelResponse.addObjectToModel("phaseEditAllowed", hakuPermissionService.userHasEditRoleToPhases(as, application, form));
        modelResponse.addObjectToModel("virkailijaDeleteAllowed", hakuPermissionService.userCanDeleteApplication(application));
        modelResponse.addObjectToModel("postProcessAllowed", postProcessAllowed);
        modelResponse.addObjectToModel("applicationSystem", as);

        modelResponse.addObjectToModel("hakukohteet", getValintatiedot(application));
        modelResponse.addObjectToModel("baseEducationDoesNotRestrictApplicationOptions", as.baseEducationDoesNotRestrictApplicationOptions());

        String sendingSchoolOid = application.getVastauksetMerged().get(ELEMENT_ID_SENDING_SCHOOL);
        if (sendingSchoolOid != null) {
            Organization sendingSchool = organizationService.findByOid(sendingSchoolOid);
            String sendingClass = application.getVastauksetMerged().get("lahtoluokka");
            modelResponse.addObjectToModel("sendingSchool", sendingSchool.getName());
            modelResponse.addObjectToModel("sendingClass", sendingClass);
        }
        modelResponse.addObjectToModel("officerUi", true);
        modelResponse.addAnswers(new HashMap<String, String>(){{put("_meta_officerUi", "true");}});
        String userOid = userSession.getUser().getUserName();
        if (userOid == null || userOid.equals(application.getPersonOid())) {
            virkailijaErrors.put("virkailija.hakemus.omanmuokkauskielletty", createI18NText("virkailija.hakemus.omanmuokkauskielletty", MESSAGES_BUNDLE_NAME));
        }
        if(!virkailijaErrors.isEmpty()) {
            modelResponse.setErrorMessages(virkailijaErrors);
        }
        return modelResponse;
    }

    private Application getApplicationWithValintadataIfNotDraft(String oid, Map<String, I18nText> errors) {
        Application application = this.applicationService.getApplicationByOid(oid);
        if(!application.isDraft()) {
            try {
                application = this.applicationService.getApplicationWithValintadata(application);
            }
            catch (ValintaServiceCallFailedException e) {
                errors.put("virkailija.hakemus.valintaservicefail", createI18NText("virkailija.hakemus.valintaservicefail", MESSAGES_BUNDLE_NAME));
            }
        }
        return application;
    }

    private List<ApplicationOptionDTO> getValintatiedot(Application application) {
        HakijaDTO hakijaDTO = valintaService.getHakija(application.getApplicationSystemId(), application.getOid());
        Map<String, String> aoAnswers = application.getPhaseAnswers(PHASE_APPLICATION_OPTIONS);

        Map<String, String> koulutusAnswers = application.getPhaseAnswers(PHASE_EDUCATION);
        String education = koulutusAnswers.get(ELEMENT_ID_BASE_EDUCATION);
        boolean showScores = education == null ||
                (!KESKEYTYNYT.equals(education)
                        && !ULKOMAINEN_TUTKINTO.equals(education));

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
        boolean reallyShowScores = showScores && pisteet != null && pisteet.compareTo(new BigDecimal(0)) > 0;
        ao.setYhteispisteet(reallyShowScores ? PISTE_FMT.format(pisteet) : "");
        ao.setJonoId(jono.getValintatapajonoOid());
        ao.setSijoittelunTulos(jono.getTila());
        ao.setVastaanottoTieto(hakutoive.getVastaanottotieto());

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
        Map<String, I18nText> errors = new HashMap<>();

        if (Objects.equals(application.getState(), Application.State.PASSIVE)) {
            throw new ResourceNotFoundException("Passive application");
        }

        final Form form = formService.getForm(application.getApplicationSystemId());
        final ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        checkUpdatePermission(as, application, form, applicationPhase.getPhaseId());

        Map<String, String> newPhaseAnswers = applicationPhase.getAnswers();
        newPhaseAnswers = applicationService.ensureApplicationOptionGroupData(newPhaseAnswers, application.getMetaValue(Application.META_FILING_LANGUAGE));

        loggerAspect.logUpdateApplication(application, applicationPhase);

        application.setVaiheenVastauksetAndSetPhaseId(applicationPhase.getPhaseId(), newPhaseAnswers);
        application.setPhaseId(applicationPhase.getPhaseId());

        if (isKoulutustaustaUpdateToKeskeytynytOrUlkomainenTutkinto(applicationPhase)) {
            application.setVaiheenVastauksetAndSetPhaseId(PHASE_APPLICATION_OPTIONS,
                    updateHakutoiveDiscretionaryIfKoulutusDiscretionary(application));
        }

        if (isKoulutustaustaUpdateToNotKeskeytynytOrNotUlkomainenTutkinto(applicationPhase)) {
            application.setVaiheenVastauksetAndSetPhaseId(PHASE_APPLICATION_OPTIONS,
                    updateHakutoiveNotDiscretionary(application));
        }

        try {
            if (StringUtils.isEmpty(application.getStudentOid())) {
                LOGGER.info("Skipping orphan removal for new application: {}", oid);
            } else {
                application = applicationService.removeOrphanedAnswers(application);
            }
        }
        catch (ValintaServiceCallFailedException e) {
            errors.put(
                    "virkailija.hakemus.valintaservicefail",
                    createI18NText("virkailija.hakemus.valinnatfail.block.save", MESSAGES_BUNDLE_NAME)
            );
        }

        ValidationResult formValidationResult = elementTreeValidator.validate(new ValidationInput(form,
                application.getVastauksetMerged(), oid, application.getApplicationSystemId(),
                ValidationInput.ValidationContext.officer_modify));
        if (!application.isDraft()) {
            if (formValidationResult.hasErrors()) {
                application.incomplete();
            } else {
                application.activate();
            }
        }
        Element phase = form.getChildById(applicationPhase.getPhaseId());
        ValidationResult phaseValidationResult = elementTreeValidator.validate(new ValidationInput(phase,
                application.getVastauksetMerged(), oid, application.getApplicationSystemId(),
                ValidationInput.ValidationContext.officer_modify));

        ModelResponse response = new ModelResponse(application, form, phase, phaseValidationResult);

        if (errors.isEmpty()) {
            application.addNote(createNote(String.format("Päivitetty vaihetta '%s'", applicationPhase.getPhaseId())));

            UpdatePreferenceResult prefRes = this.applicationService.updatePreferenceBasedData(application);
            if (prefRes != null && prefRes.getValidationResult() != null) {
                for (Map.Entry<String, I18nText> entry : prefRes.getValidationResult().getErrorMessages().entrySet()) {
                    this.userSession.addNote(entry.getKey(), entry.getValue());
                }
            }

            this.applicationService.updateAuthorizationMeta(application);
            this.applicationService.update(application, true);
        } else {
            response.getErrorMessages().putAll(errors);
        }

        response.addObjectToModel("ongoing", false);
        return response;
    }

    private boolean isKoulutustaustaUpdateToKeskeytynytOrUlkomainenTutkinto(ApplicationPhase applicationPhase) {
        return applicationPhase.getPhaseId().equals(PHASE_EDUCATION) && (
                KESKEYTYNYT.equals(applicationPhase.getAnswers().get(ELEMENT_ID_BASE_EDUCATION))
                        || ULKOMAINEN_TUTKINTO.equals(applicationPhase.getAnswers().get(ELEMENT_ID_BASE_EDUCATION))
        );
    }

    private boolean isKoulutustaustaUpdateToNotKeskeytynytOrNotUlkomainenTutkinto(ApplicationPhase applicationPhase) {
        return applicationPhase.getPhaseId().equals(PHASE_EDUCATION) && !(
                KESKEYTYNYT.equals(applicationPhase.getAnswers().get(ELEMENT_ID_BASE_EDUCATION))
                        || ULKOMAINEN_TUTKINTO.equals(applicationPhase.getAnswers().get(ELEMENT_ID_BASE_EDUCATION))
        );
    }

    private Map<String, String> updateHakutoiveDiscretionaryIfKoulutusDiscretionary(Application application) {
        final Map<String, String> hakutoiveet = application.getAnswers().get(PHASE_APPLICATION_OPTIONS);
        if (application.getAnswers().containsKey(PHASE_APPLICATION_OPTIONS)) {
            for (int i = 1; i < 7; i++) {
                if ("true".equals(hakutoiveet.get("preference" + i +"-Koulutus-id-discretionary"))) {
                    final String discretionary = "preference" + i + "-discretionary";
                    LOGGER.info("Application oid={} updating {} to true", application.getOid(), discretionary);
                    hakutoiveet.put(discretionary, "true");
                }
            }
        }
        return hakutoiveet;
    }

    private Map<String, String> updateHakutoiveNotDiscretionary(Application application) {
        final Map<String, String> hakutoiveet = application.getAnswers().get(PHASE_APPLICATION_OPTIONS);
        if (application.getAnswers().containsKey(PHASE_APPLICATION_OPTIONS)) {
            for (int i = 1; i < 7; i++) {
                final String discretionary = "preference" + i + "-discretionary";
                final String removed = hakutoiveet.remove(discretionary);
                if (removed != null) {
                    LOGGER.info("Application oid={} {} removed from hakutoiveet", application.getOid(), discretionary);
                }
            }
        }
        return hakutoiveet;
    }

    private void checkUpdatePermission(ApplicationSystem as, Application application, Form form, String phaseId) {
        Boolean permission = hakuPermissionService.userHasEditRoleToPhases(as, application, form).get(phaseId);
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
        String defaultYear = String.valueOf(today.get(Calendar.YEAR));
        String[] kevatkausiDates = kevatkausi.split("-");
        SimpleDateFormat dateFormat = new SimpleDateFormat(KAUSI_FORMAT_STRING);
        try {
            Date kevatkausiAlkaa = dateFormat.parse(kevatkausiDates[0].trim() + "." + today.get(Calendar.YEAR));
            Date kevatkausiLoppuu = dateFormat.parse(kevatkausiDates[1].trim() +"."+today.get(Calendar.YEAR));
            if (today.getTime().after(kevatkausiAlkaa) && today.getTime().before(kevatkausiLoppuu)) {
                semester = "kausi_k";
            } else if (today.getTime().before(kevatkausiAlkaa)) {
                defaultYear = String.valueOf(today.get(Calendar.YEAR) - 1);
            }
        } catch (ParseException e) {
            LOGGER.error("Couldn't parse kevatkausi dates: {}", kevatkausi);
        }
        modelResponse.addObjectToModel("defaultYear", defaultYear);
        modelResponse.addObjectToModel("defaultSemester", semester);
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
        String[] baseEducations = new String[] {
                "am",
                "amt",
                "avoin",
                "kk",
                "kk_ulk",
                "muu",
                "ulk",
                "yo_ammatillinen",
                "yo_kansainvalinen_suomessa",
                "yo",
                "yo_ulkomainen"
        };
        for (String ed : baseEducations) {
            Map<String, String> opt = new HashMap<String, String>();
            opt.put("value", ed);

            Map<String, String> trans = Maps.newHashMap(createI18NText("pohjakoulutus_" + ed, FORM_COMMON_BUNDLE_NAME).getTranslations());
            Map<String, String> transOverride = createI18NText("virkailija.hakemus.pohjakoulutus." + ed, MESSAGES_BUNDLE_NAME).getTranslations();
            trans.putAll(transOverride);
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
            application.logStudentOidIfChanged(userSession.getUser().getUserName(), null);
            application.addNote(createNote("Oppijanumero syötetty"));
            AUDIT.log(builder()
                    .setOperaatio(HakuOperation.CHANGE_APPLICATION_STATE)
                    .hakuOid(application.getApplicationSystemId())
                    .add("studentOid",person.getStudentOid())
                    .hakemusOid(application.getOid()).build());
        }
        Application queryApplication = new Application(oid);
        applicationService.update(queryApplication, application);
    }

    @Override
    public void postProcess(String oid, boolean email) {
        Application application = applicationService.getApplicationByOid(oid);
        application.setRedoPostProcess(email ? Application.PostProcessingState.FULL : Application.PostProcessingState.NOMAIL);
        applicationService.update(new Application(oid), application);
    }

    @Override
    public ModelResponse getApplicationPrint(final String oid) {
        Application application = getApplicationWithValintadataIfNotDraft(oid, new HashMap<String, I18nText>());
        final ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());

        ModelResponse response = new ModelResponse(application,
                applicationSystem,
                AttachmentUtil.resolveAttachments(application));
        response.addObjectToModel("alatunnisterivit", new ArrayList<I18nText>(4) {{
            add(i18nBundleService.getBundle(applicationSystem).get("lomake.tulostus.alatunniste.rivi1"));
            add(i18nBundleService.getBundle(applicationSystem).get("lomake.tulostus.alatunniste.rivi2"));
            add(i18nBundleService.getBundle(applicationSystem).get("lomake.tulostus.alatunniste.rivi3"));
            add(i18nBundleService.getBundle(applicationSystem).get("lomake.tulostus.alatunniste.rivi4"));
        }});
        return response;
    }

    @Override
    public Map<String, String> getNamesForNoteUsers(List<String> oids) {
        List<Person> persons = authenticationService.getHenkiloList(oids);
        Map<String, String> result = new HashMap<String, String>();
        for(Person person: persons) {
            result.put(person.getPersonOid(), person.getNickName() + " " + person.getLastName());
        }
        return result;
    }

    private ApplicationNote createNote(String note) {
        return new ApplicationNote(note, new Date(), userSession.getUser().getUserName());
    }

    public void setValintaService(ValintaService valintaService) {
        this.valintaService = valintaService;
    }

    @Override
    public void processAttachmentsAndEligibilities(String oid, EligibilitiesDTO attachmentsAndEligibilities) {
        LOGGER.debug("Got attachementsAndEligibilities " + StringUtils.join(attachmentsAndEligibilities.getEligibilities(), ","));
        final Application application = applicationService.getApplicationByOid(oid);

        checkUpdateDate(application.getEligibilitiesAndAttachmentsUpdated(), attachmentsAndEligibilities.getUpdated());

        final Map<String, PreferenceEligibility> preferenceEligibilities = new HashMap<>();
        final Map<String, PreferenceChecked> preferenceCheckeds = new HashMap<>();
        final Map<String, ApplicationAttachmentRequest> attachmentRequests = new HashMap<>();
        for (PreferenceEligibility e : application.getPreferenceEligibilities()) {
            preferenceEligibilities.put(e.getAoId(), e);
        }
        for (PreferenceChecked e : application.getPreferencesChecked()) {
            preferenceCheckeds.put(e.getPreferenceAoOid(), e);
        }
        for (ApplicationAttachmentRequest e : application.getAttachmentRequests()) {
            attachmentRequests.put(e.getId(), e);
        }
        for (AttachmentsAndEligibilityDTO dto : attachmentsAndEligibilities.getEligibilities()) {
            PreferenceEligibility preferenceEligibility = preferenceEligibilities.get(dto.getAoId());
            PreferenceChecked preferenceChecked = preferenceCheckeds.get(dto.getAoId());
            if (null == preferenceEligibility || null == preferenceChecked) {
                String msg = "No preference found with " + dto.getAoId() + " for application " + oid;
                LOGGER.error(msg);
                throw new IncoherentDataException(msg);
            }
            updateEligibilityStatus(application, dto, preferenceEligibility, preferenceChecked, userSession.getUser().getUserName());
            for (AttachmentDTO attachmentDTO : dto.getAttachments()) {
                checkDuplicateAttachmentDTO(attachmentDTO, dto.getAttachments());
                ApplicationAttachmentRequest attachment = attachmentRequests.get(attachmentDTO.getId());
                if (null == attachment) {
                    String msg = "No attachment request " + attachmentDTO.getId() + " found in application " + oid;
                    LOGGER.error(msg);
                    throw new IncoherentDataException(msg);
                }
                updateAttachmentRequestStatus(application, attachmentDTO, attachment);
            }
        }
        application.setEligibilitiesAndAttachmentsUpdated(new Date());
        applicationService.update(new Application(oid), application);
    }

    private void checkUpdateDate(Date lastApplicationUpdateDate, Date updateDateInUsersVersion) {
        if(null == lastApplicationUpdateDate) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastApplicationUpdateDate);
        calendar.set(Calendar.MILLISECOND, 0);

        if(null == updateDateInUsersVersion || calendar.getTime().getTime() > updateDateInUsersVersion.getTime()) {
            LOGGER.warn("Last update date {} is after update date {} of the application version user is updating.", lastApplicationUpdateDate, updateDateInUsersVersion);
            throw new IllegalStateException("Application has been updated since last reload.");
        }
    }

    private static void checkDuplicateAttachmentDTO(AttachmentDTO attachmentDTO, List<AttachmentDTO> allAttachmentDTOs) {
        for (AttachmentDTO old : allAttachmentDTOs) {
            if (attachmentDTO.getId().equals(old.getId()) && !attachmentDTO.equals(old)) {
                String msg = "Duplicate attachment requests with non matching data. " + old + ", " + attachmentDTO;
                LOGGER.error(msg);
                throw new IncoherentDataException(msg);
            }
        }
    }

    private static LogMessage.LogMessageBuilder eligibilityAuditLogBuilder(Application application, AttachmentsAndEligibilityDTO dto) {
        return builder()
                .setOperaatio(HakuOperation.UPDATE_ELIGIBILITY)
                .hakuOid(application.getApplicationSystemId())
                .hakukohdeOid(dto.getAoId())
                .hakemusOid(application.getOid());
    }

    private static void updateEligibilityStatus(Application application,
                                                AttachmentsAndEligibilityDTO dto,
                                                PreferenceEligibility preferenceEligibility,
                                                PreferenceChecked preferenceChecked,
                                                String officerOid) {
        PreferenceEligibility.Status newStatus = PreferenceEligibility.Status.valueOf(dto.getStatus());
        PreferenceEligibility.Source newSource = PreferenceEligibility.Source.valueOf(dto.getSource());
        String newRejectionBasis = dto.getRejectionBasis();
        Boolean newChecked = dto.getPreferencesChecked();
        
        boolean updateStatus = newStatus != preferenceEligibility.getStatus();
        if (updateStatus) {
            AUDIT.log(eligibilityAuditLogBuilder(application, dto)
                    .add("status", newStatus, preferenceEligibility.getStatus())
                    .build());
            preferenceEligibility.setStatus(newStatus);
        }
        
        boolean updateSource = newSource != preferenceEligibility.getSource();
        if (updateSource) {
            AUDIT.log(eligibilityAuditLogBuilder(application, dto)
                    .add("source", newSource, preferenceEligibility.getSource())
                    .build());
            preferenceEligibility.setSource(newSource);
        }

        if (updateStatus || updateSource) {
            updateEligibilityStatusToApplicationNotes(application, preferenceEligibility, newStatus, newSource, officerOid);
        }
        
        if (!newRejectionBasis.equals(preferenceEligibility.getRejectionBasis())) {
            AUDIT.log(eligibilityAuditLogBuilder(application, dto)
                    .add("rejectionBasis", newRejectionBasis, preferenceEligibility.getRejectionBasis())
                    .build());
            preferenceEligibility.setRejectionBasis(newRejectionBasis);
        }
        if (!newChecked.equals(preferenceChecked.isChecked())) {
            AUDIT.log(eligibilityAuditLogBuilder(application, dto)
                    .add("checked", newChecked, preferenceChecked.getChecked())
                    .build());
            preferenceChecked.setChecked(newChecked);
            if (newChecked) {
                preferenceChecked.setCheckedByOfficerOid(officerOid);
            }
        }
    }

    private static void updateEligibilityStatusToApplicationNotes(Application application,
                                                                  PreferenceEligibility preferenceEligibility,
                                                                  PreferenceEligibility.Status status,
                                                                  PreferenceEligibility.Source source,
                                                                  String officerOid) {
        String eligibilityNote = ApplicationUtil.getApplicationOptionName(application, preferenceEligibility)
          + ". Hakukelpoisuutta muutettu: " + PreferenceEligibility.getStatusMessage(status);
        if (PreferenceEligibility.Source.UNKNOWN != source) {
            eligibilityNote += ", " + PreferenceEligibility.getSourceMessage(source);
        }
        application.addNote(new ApplicationNote(eligibilityNote, new Date(), officerOid));
    }

    private static void updateAttachmentRequestStatus(Application application, AttachmentDTO attachmentDTO, ApplicationAttachmentRequest attachment) {
        ApplicationAttachmentRequest.ReceptionStatus newReceptionStatus = ApplicationAttachmentRequest.ReceptionStatus.valueOf(attachmentDTO.getReceptionStatus());
        if (newReceptionStatus != attachment.getReceptionStatus()) {
            AUDIT.log(builder()
                    .setOperaatio(HakuOperation.UPDATE_ATTACHMENT_RECEPTION_STATUS)
                    .hakuOid(application.getApplicationSystemId())
                    .hakukohdeOid(attachment.getPreferenceAoId())
                    .hakukohderyhmaOid(attachment.getPreferenceAoGroupId())
                    .hakemusOid(application.getOid())
                    .add("receptionStatus", newReceptionStatus, attachment.getReceptionStatus())
                    .build());
            attachment.setReceptionStatus(newReceptionStatus);
        }
        ApplicationAttachmentRequest.ProcessingStatus newProcessingStatus = ApplicationAttachmentRequest.ProcessingStatus.valueOf(attachmentDTO.getProcessingStatus());
        if (newProcessingStatus != attachment.getProcessingStatus()) {
            AUDIT.log(builder()
                    .setOperaatio(HakuOperation.UPDATE_ATTACHMENT_PROCESSING_STATUS)
                    .hakuOid(application.getApplicationSystemId())
                    .hakukohdeOid(attachment.getPreferenceAoId())
                    .hakukohderyhmaOid(attachment.getPreferenceAoGroupId())
                    .hakemusOid(application.getOid())
                    .add("processingStatus", newProcessingStatus, attachment.getProcessingStatus())
                    .build());
            attachment.setProcessingStatus(newProcessingStatus);
        }
    }
}
