package fi.vm.sade.oppija.ui.service.impl;

import com.google.common.base.Strings;
import com.sun.scenario.effect.Merge;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.util.ElementTree;
import fi.vm.sade.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.ui.HakuPermissionService;
import fi.vm.sade.oppija.ui.service.OfficerUIService;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.HEAD;
import java.io.IOException;
import java.util.Map;

@Service
public class OfficerUIServiceImpl implements OfficerUIService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerUIServiceImpl.class);

    private final ApplicationService applicationService;
    private final FormService formService;
    private final KoodistoService koodistoService;
    private final HakuPermissionService hakuPermissionService;
    private final String koulutusinformaatioBaseUrl;
    private final LoggerAspect loggerAspect;
    private final ElementTreeValidator elementTreeValidator;
    private final ApplicationSystemService applicationSystemService;

    @Autowired
    public OfficerUIServiceImpl(final ApplicationService applicationService,
                                final FormService formService,
                                final KoodistoService koodistoService,
                                final HakuPermissionService hakuPermissionService,
                                final LoggerAspect loggerAspect,
                                @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl,
                                final ElementTreeValidator elementTreeValidator,
                                final ApplicationSystemService applicationSystemService
    )

    {
        this.applicationService = applicationService;
        this.formService = formService;
        this.koodistoService = koodistoService;
        this.hakuPermissionService = hakuPermissionService;
        this.loggerAspect = loggerAspect;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
        this.elementTreeValidator = elementTreeValidator;
        this.applicationSystemService = applicationSystemService;
    }

    @Override
    public UIServiceResponse getValidatedApplicationElement(
            final String oid,
            final String phaseId,
            final String elementId) throws ResourceNotFoundException {
        Application application = this.applicationService.getApplication(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationSystemId());
        Element element = new ElementTree(form).getChildById(elementId);
        OfficerApplicationPreviewResponse officerApplicationResponse = new OfficerApplicationPreviewResponse();
        officerApplicationResponse.setApplication(application);
        officerApplicationResponse.setElement(element);
        officerApplicationResponse.setForm(form);
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, application.getVastauksetMerged(),
                oid, application.getApplicationSystemId()));
        officerApplicationResponse.setErrorMessages(validationResult.getErrorMessages());
        officerApplicationResponse.addObjectToModel("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);
        return officerApplicationResponse;
    }

    @Override
    public UIServiceResponse getValidatedApplication(final String oid, final String phaseId) throws IOException, ResourceNotFoundException {
        Application application = this.applicationService.getApplicationByOid(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationSystemId());
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, application.getVastauksetMerged(),
                oid, application.getApplicationSystemId()));
        OfficerApplicationPreviewResponse officerApplicationResponse = new OfficerApplicationPreviewResponse();
        officerApplicationResponse.setApplication(application);
        officerApplicationResponse.setElement(new ElementTree(form).getChildById(application.getPhaseId()));
        officerApplicationResponse.setForm(form);
        officerApplicationResponse.setErrorMessages(validationResult.getErrorMessages());
        officerApplicationResponse.addObjectToModel("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);
        officerApplicationResponse.addObjectToModel("virkailijaEditAllowed", hakuPermissionService.userCanUpdateApplication(application));
        officerApplicationResponse.addObjectToModel("virkailijaDeleteAllowed", hakuPermissionService.userCanDeleteApplication(application));
        return officerApplicationResponse;
    }

    @Override
    public UIServiceResponse getAdditionalInfo(String oid) throws ResourceNotFoundException, IOException {
        OfficerAdditionalInfoResponse officerAdditionalInfoResponse = new OfficerAdditionalInfoResponse();
        Application application = applicationService.getApplicationByOid(oid);
        officerAdditionalInfoResponse.setApplication(application);
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

        checkUpdatePermission(application);

        loggerAspect.logUpdateApplication(application, applicationPhase);

        application.addVaiheenVastaukset(applicationPhase.getPhaseId(), applicationPhase.getAnswers());
        final Form form = formService.getForm(application.getApplicationSystemId());
        Map<String, String> allAnswers = application.getVastauksetMergedIgnoringPhase(applicationPhase.getPhaseId());
        allAnswers.putAll(applicationPhase.getAnswers());
        ValidationResult formValidationResult = elementTreeValidator.validate(new ValidationInput(form,
                allAnswers, oid, application.getApplicationSystemId()));
        if (formValidationResult.hasErrors()) {
            application.incomplete();
        } else {
            application.activate();
        }
        Element phase = new ElementTree(form).getChildById(applicationPhase.getPhaseId());
        ValidationResult phaseValidationResult = elementTreeValidator.validate(new ValidationInput(phase,
                applicationPhase.getAnswers(), oid, application.getApplicationSystemId()));

        String noteText = "Päivitetty vaihetta '" + applicationPhase.getPhaseId() + "'";
        applicationService.addNote(application, noteText);

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

    private void checkUpdatePermission(Application application) throws ResourceNotFoundException {
        if (!hakuPermissionService.userCanUpdateApplication(application)) {
            throw new ResourceNotFoundException("User can not update application "+application.getOid());
        }
    }

    @Override
    public Application getApplicationWithLastPhase(final String oid) throws ResourceNotFoundException {
        Application application = applicationService.getApplicationByOid(oid);
        Element phase = formService.getLastPhase(application.getApplicationSystemId());
        application.setPhaseId(phase.getId());
        return application;
    }

    @Override
    public UIServiceResponse getOrganizationAndLearningInstitutions() {
        UIServiceResponse uiServiceResponse = new UIServiceResponse();
        uiServiceResponse.addObjectToModel("organizationTypes", koodistoService.getOrganizationtypes());
        uiServiceResponse.addObjectToModel("learningInstitutionTypes", koodistoService.getLearningInstitutionTypes());
        uiServiceResponse.addObjectToModel("applicationSystems", applicationSystemService.getAllApplicationSystems("id", "name"));
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
        applicationService.addNote(application, note);
    }

    @Override
    public Application createApplication(final String asId) {
        return applicationService.officerCreateNewApplication(asId);
    }

    @Override
    public void addPersonOid(String oid, String personOid) throws ResourceNotFoundException {
        Application application = applicationService.getApplicationByOid(oid);
        if (!Strings.isNullOrEmpty(application.getPersonOid())) {
            throw new IllegalStateException("Person oid is already set");
        } else if (Strings.isNullOrEmpty(personOid)) {
            throw new IllegalArgumentException("Invalid person oid");
        }
        application.setPersonOid(personOid);
        applicationService.addNote(application, "Oppijanumero syötetty");
    }
}
