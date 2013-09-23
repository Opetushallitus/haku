package fi.vm.sade.oppija.ui.service.impl;

import com.google.common.base.Strings;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class OfficerUIServiceImpl implements OfficerUIService {

    private final ApplicationService applicationService;
    private final FormService formService;
    private final KoodistoService koodistoService;
    private final HakuPermissionService hakuPermissionService;
    private final String koulutusinformaatioBaseUrl;
    private final LoggerAspect loggerAspect;
    private final ElementTreeValidator elementTreeValidator;
    private final ApplicationSystemService applicationSystemService;
    private final AuthenticationService authenticationService;

    @Autowired
    public OfficerUIServiceImpl(final ApplicationService applicationService,
                                final FormService formService,
                                final KoodistoService koodistoService,
                                final HakuPermissionService hakuPermissionService,
                                final LoggerAspect loggerAspect,
                                @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl,
                                final ElementTreeValidator elementTreeValidator,
                                final ApplicationSystemService applicationSystemService,
                                final AuthenticationService authenticationService
    ) {
        this.applicationService = applicationService;
        this.formService = formService;
        this.koodistoService = koodistoService;
        this.hakuPermissionService = hakuPermissionService;
        this.loggerAspect = loggerAspect;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
        this.elementTreeValidator = elementTreeValidator;
        this.applicationSystemService = applicationSystemService;
        this.authenticationService = authenticationService;
    }

    @Override
    public UIServiceResponse getApplicationElement(
            final String oid,
            final String phaseId,
            final String elementId,
            final boolean validate) throws ResourceNotFoundException {
        Application application = this.applicationService.getApplicationByOid(oid);
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
    public UIServiceResponse getValidatedApplication(final String oid, final String phaseId) throws ResourceNotFoundException {
        Application application = this.applicationService.getApplicationByOid(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form form = this.formService.getForm(application.getApplicationSystemId());
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, application.getVastauksetMerged(),
                oid, application.getApplicationSystemId()));
        OfficerApplicationPreviewResponse officerApplicationResponse = new OfficerApplicationPreviewResponse();
        officerApplicationResponse.setApplication(application);
        officerApplicationResponse.setForm(form);
        if (!"esikatselu".equals(phaseId)) {
            officerApplicationResponse.setElement(new ElementTree(form).getChildById(application.getPhaseId()));
        } else {
            officerApplicationResponse.setElement(form);

        }
        officerApplicationResponse.setErrorMessages(validationResult.getErrorMessages());
        officerApplicationResponse.addObjectToModel("preview", "esikatselu".equals(phaseId));
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
                allAnswers, oid, application.getApplicationSystemId()));

        String noteText = "Päivitetty vaihetta '" + applicationPhase.getPhaseId() + "'";
        this.applicationService.addNote(application, noteText, false);
        this.applicationService.fillLOPChain(application, false);
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
            throw new ResourceNotFoundException("User can not update application " + application.getOid());
        }
    }

    @Override
    public Application getApplicationWithLastPhase(final String oid) throws ResourceNotFoundException {
        Application application = applicationService.getApplicationByOid(oid);
        application.setPhaseId("esikatselu");
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
        Application application = applicationService.getApplicationByOid(oid);
        applicationService.fillLOPChain(application, false);
        applicationService.addPersonOid(application);
    }

    @Override
    public Application passivateApplication(String oid, String reason) throws ResourceNotFoundException {
        reason = "Hakemus passivoitu: " + reason;
        addNote(oid, reason);
        return applicationService.passivateApplication(oid);
    }

    @Override
    public void addNote(String applicationOid, String note) throws ResourceNotFoundException {
        Application application = applicationService.getApplicationByOid(applicationOid);
        applicationService.addNote(application, note, true);
    }

    @Override
    public Application createApplication(final String asId) {
        return applicationService.officerCreateNewApplication(asId);
    }

    @Override
    public void addStudentOid(String oid) throws ResourceNotFoundException {
        Application application = applicationService.getApplicationByOid(oid);
        String studentOid = application.getPersonOid();
        if (!Strings.isNullOrEmpty(application.getStudentOid())) {
            throw new IllegalStateException("Student oid is already set");
        } else if (Strings.isNullOrEmpty(studentOid)) {
            throw new IllegalArgumentException("Invalid student oid");
        }
        authenticationService.getStudentOid(studentOid);
        application.setStudentOid(studentOid);
        applicationService.addNote(application, "Oppijanumero syötetty", true);
    }
}
