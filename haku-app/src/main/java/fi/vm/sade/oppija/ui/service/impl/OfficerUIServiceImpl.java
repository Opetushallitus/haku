package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.ValintaperusteetService;
import fi.vm.sade.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.ui.service.OfficerUIService;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OfficerUIServiceImpl implements OfficerUIService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerUIServiceImpl.class);

    private final ApplicationService applicationService;
    private final FormService formService;
    private final ValintaperusteetService valintaperusteetService;
    private final KoodistoService koodistoService;
    private final String koulutusinformaatioBaseUrl;
    private final LoggerAspect loggerAspect;

    @Autowired
    public OfficerUIServiceImpl(final ApplicationService applicationService,
                                final FormService formService,
                                final ValintaperusteetService valintaperusteetService,
                                final KoodistoService koodistoService,
                                final LoggerAspect loggerAspect,
                                @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl
    )

    {
        this.applicationService = applicationService;
        this.formService = formService;
        this.valintaperusteetService = valintaperusteetService;
        this.koodistoService = koodistoService;
        this.loggerAspect = loggerAspect;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
    }

    @Override
    public UIServiceResponse getValidatedApplicationElement(
            final String oid,
            final String phaseId,
            final String elementId) throws ResourceNotFoundException {
        Application application = this.applicationService.getApplication(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationPeriodId());
        Element element = form.getChildById(elementId);
        ValidationResult validationResult = ElementTreeValidator.validateForm(form, application);
        OfficerApplicationPreviewResponse officerApplicationResponse = new OfficerApplicationPreviewResponse();
        officerApplicationResponse.setApplication(application);
        officerApplicationResponse.setElement(element);
        officerApplicationResponse.setForm(form);
        officerApplicationResponse.setErrorMessages(validationResult.getErrorMessages());
        officerApplicationResponse.addObjectToModel("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);
        return officerApplicationResponse;
    }

    @Override
    public UIServiceResponse getValidatedApplication(final String oid, final String phaseId) throws IOException, ResourceNotFoundException {
        Application application = this.applicationService.getApplicationByOid(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationPeriodId());
        ValidationResult validationResult = ElementTreeValidator.validateForm(form, application);
        OfficerApplicationPreviewResponse officerApplicationResponse = new OfficerApplicationPreviewResponse();
        officerApplicationResponse.setApplication(application);
        officerApplicationResponse.setElement(form.getChildById(application.getPhaseId()));
        officerApplicationResponse.setForm(form);
        officerApplicationResponse.setErrorMessages(validationResult.getErrorMessages());
        officerApplicationResponse.setAdditionalQuestions(getAdditionalQuestions(application));
        officerApplicationResponse.addObjectToModel("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);
        return officerApplicationResponse;
    }

    @Override
    public UIServiceResponse getAdditionalInfo(String oid) throws ResourceNotFoundException, IOException {
        OfficerAdditionalInfoResponse officerAdditionalInfoResponse = new OfficerAdditionalInfoResponse();
        Application application = applicationService.getApplicationByOid(oid);
        officerAdditionalInfoResponse.setApplication(application);
        officerAdditionalInfoResponse.setAdditionalQuestions(getAdditionalQuestions(application));
        return officerAdditionalInfoResponse;
    }

    @Override
    public UIServiceResponse updateApplication(final String oid, final ApplicationPhase applicationPhase, User user)
            throws ResourceNotFoundException {

        Application queryApplication = new Application(oid);
        Application application = this.applicationService.getApplicationByOid(oid);
        Application.State state = application.getState();
        if (state != null && state.equals(Application.State.PASSIVE)) {
            throw new ResourceNotFoundException("Passive application");
        }

        loggerAspect.logUpdateApplication(application, applicationPhase);

        application.addVaiheenVastaukset(applicationPhase.getPhaseId(), applicationPhase.getAnswers());
        final Form form = formService.getForm(application.getApplicationPeriodId());
        ValidationResult formValidationResult = ElementTreeValidator.validateForm(form, application);
        if (formValidationResult.hasErrors()) {
            application.incomplete();
        } else {
            application.activate();
        }
        Element phase = form.getChildById(applicationPhase.getPhaseId());
        ValidationResult phaseValidationResult = ElementTreeValidator.validate(phase, applicationPhase.getAnswers());

        String noteText = "Päivitetty vaihetta '" + applicationPhase.getPhaseId() + "'";
        application.addNote(new ApplicationNote(noteText, new Date(), user));


        this.applicationService.update(queryApplication, application);
        application.setPhaseId(applicationPhase.getPhaseId());
        OfficerApplicationPreviewResponse officerApplicationResponse = new OfficerApplicationPreviewResponse();
        officerApplicationResponse.setApplication(application);
        officerApplicationResponse.setElement(phase);
        officerApplicationResponse.setForm(form);
        officerApplicationResponse.setErrorMessages(phaseValidationResult.getErrorMessages());
        officerApplicationResponse.addObjectToModel("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);
        return officerApplicationResponse;
    }

    @Override
    public Application getApplicationWithLastPhase(final String oid) throws ResourceNotFoundException {
        Application application = applicationService.getApplicationByOid(oid);
        Element phase = formService.getLastPhase(application.getApplicationPeriodId());
        application.setPhaseId(phase.getId());
        return application;
    }

    @Override
    public UIServiceResponse getOrganizationAndLearningInstitutions() {
        UIServiceResponse uiServiceResponse = new UIServiceResponse();
        uiServiceResponse.addObjectToModel("organizationTypes", koodistoService.getOrganizationtypes());
        uiServiceResponse.addObjectToModel("learningInstitutionTypes", koodistoService.getLearningInstitutionTypes());
        Map<String, ApplicationPeriod> applicationPerioidMap = formService.getApplicationPerioidMap();
        uiServiceResponse.addObjectToModel("applicationPeriods", applicationPerioidMap);
        return uiServiceResponse;
    }

    @Override
    public void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo) throws ResourceNotFoundException {
        applicationService.saveApplicationAdditionalInfo(oid, additionalInfo);
    }

    @Override
    public void addPersonAndAuthenticate(String oid) throws ResourceNotFoundException {
        applicationService.addPersonAndAuthenticate(oid);
    }

    @Override
    public Application passivateApplication(String oid, String reason, User user) throws ResourceNotFoundException {
        reason = "Hakemus passivoitu: " + reason;
        addNote(oid, reason, user);
        return applicationService.passivateApplication(oid);
    }

    @Override
    public void addNote(String applicationOid, String note, User user) throws ResourceNotFoundException {
        Application application = applicationService.getApplicationByOid(applicationOid);
        applicationService.addNote(application, note, user);
    }

    private AdditionalQuestions getAdditionalQuestions(final Application application) throws IOException {
        List<String> applicationPreferenceOids = this.applicationService.getApplicationPreferenceOids(application);
        return this.valintaperusteetService.retrieveAdditionalQuestions(applicationPreferenceOids);
    }

    @Override
    public Application createApplication(final String asId) {
        return applicationService.officerCreateNewApplication(asId);
    }
}
