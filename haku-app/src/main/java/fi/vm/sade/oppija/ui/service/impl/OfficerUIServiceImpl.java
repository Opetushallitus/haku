package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.ValintaperusteetService;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.ui.service.OfficerUIService;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OfficerUIServiceImpl implements OfficerUIService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerUIServiceImpl.class);

    private final ApplicationService applicationService;
    private final FormService formService;
    private final ValintaperusteetService valintaperusteetService;
    private final KoodistoService koodistoService;

    @Autowired
    public OfficerUIServiceImpl(final ApplicationService applicationService,
                                final FormService formService,
                                final ValintaperusteetService valintaperusteetService,
                                final KoodistoService koodistoService) {
        this.applicationService = applicationService;
        this.formService = formService;
        this.valintaperusteetService = valintaperusteetService;
        this.koodistoService = koodistoService;

    }

    @Override
    public UIServiceResponse getValidatedApplication(final String oid, final String phaseId) throws IOException, ResourceNotFoundException {
        Application application = this.applicationService.getApplication(oid);
        application.setPhaseId(phaseId); // TODO active applications does not have phaseId?
        Form activeForm = this.formService.getActiveForm(application.getFormId());
        ValidationResult validationResult = ElementTreeValidator.validateForm(activeForm, application);
        OfficerApplicationPreviewResponse officerApplicationResponse = new OfficerApplicationPreviewResponse();
        officerApplicationResponse.setApplication(application);
        officerApplicationResponse.setElement(activeForm.getPhase(application.getPhaseId()));
        officerApplicationResponse.setForm(activeForm);
        officerApplicationResponse.setErrorMessages(validationResult.getErrorMessages());
        officerApplicationResponse.setAdditionalQuestions(getAdditionalQuestions(application));
        return officerApplicationResponse;
    }

    @Override
    public UIServiceResponse getAdditionalInfo(String oid) throws ResourceNotFoundException, IOException {
        OfficerAdditionalInfoResponse officerAdditionalInfoResponse = new OfficerAdditionalInfoResponse();
        Application application = applicationService.getApplication(oid);
        officerAdditionalInfoResponse.setApplication(application);
        officerAdditionalInfoResponse.setAdditionalQuestions(getAdditionalQuestions(application));
        return officerAdditionalInfoResponse;
    }

    @Override
    public UIServiceResponse updateApplication(final String oid, final ApplicationPhase applicationPhase) throws ResourceNotFoundException {

        Application queryApplication = new Application(oid);
        Application application = this.applicationService.getApplication(oid);
        application.addVaiheenVastaukset(applicationPhase.getPhaseId(), applicationPhase.getAnswers());
        final Form activeForm = formService.getForm(application.getFormId());
        ValidationResult formValidationResult = ElementTreeValidator.validateForm(activeForm, application);
        if (formValidationResult.hasErrors()) {
            application.incomplete();
        } else {
            application.activate();
        }
        Phase phase = activeForm.getPhase(applicationPhase.getPhaseId());
        ValidationResult phaseValidationResult = ElementTreeValidator.validate(phase, applicationPhase.getAnswers());

        this.applicationService.update(queryApplication, application);
        application.setPhaseId(applicationPhase.getPhaseId());
        OfficerApplicationPreviewResponse officerApplicationResponse = new OfficerApplicationPreviewResponse();
        officerApplicationResponse.setApplication(application);
        officerApplicationResponse.setElement(phase);
        officerApplicationResponse.setForm(activeForm);
        officerApplicationResponse.setErrorMessages(phaseValidationResult.getErrorMessages());

        return officerApplicationResponse;
    }

    @Override
    public Application getApplicationWithLastPhase(final String oid) throws ResourceNotFoundException {
        Application application = applicationService.getApplication(oid);
        FormId formId = application.getFormId();
        Phase phase = formService.getLastPhase(formId.getApplicationPeriodId(), formId.getFormId());
        application.setPhaseId(phase.getId());
        return application;
    }

    @Override
    public UIServiceResponse getOrganizationAndLearningInstitutions() {
        UIServiceResponse uiServiceResponse = new UIServiceResponse();
        uiServiceResponse.addObjectToModel("organizationTypes", koodistoService.getOrganizationtypes());
        uiServiceResponse.addObjectToModel("learningInstitutionTypes", koodistoService.getLearningInstitutionTypes());
        return uiServiceResponse;
    }

    @Override
    public void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo) throws ResourceNotFoundException {
        applicationService.saveApplicationAdditionalInfo(oid, additionalInfo);
    }

    private AdditionalQuestions getAdditionalQuestions(final Application application) throws IOException {
        List<String> applicationPreferenceOids = this.applicationService.getApplicationPreferenceOids(application);
        return this.valintaperusteetService.retrieveAdditionalQuestions(applicationPreferenceOids);
    }
}
