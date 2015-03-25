package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class ApplicationPostProcessorService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPostProcessorService.class);

    private final ApplicationService applicationService;
    private final BaseEducationService baseEducationService;
    private final ElementTreeValidator elementTreeValidator;
    private final FormService formService;

    @Value("${scheduler.skipSendingSchool.automatic:false}")
    private boolean skipSendingSchoolAutomatic;

    @Autowired
    public ApplicationPostProcessorService(final ApplicationService applicationService,
                                           final BaseEducationService baseEducationService,
                                           final FormService formService,
                                           final ElementTreeValidator elementTreeValidator){
        this.applicationService = applicationService;
        this.baseEducationService = baseEducationService;
        this.formService = formService;
        this.elementTreeValidator = elementTreeValidator;
    }

    public Application process(Application application) throws IOException{
        application = applicationService.addPersonOid(application);
        if (!skipSendingSchoolAutomatic) {
            application = baseEducationService.addSendingSchool(application);
            application = baseEducationService.addBaseEducation(application);
        }
        application = applicationService.updateAuthorizationMeta(application);
        application = applicationService.ensureApplicationOptionGroupData(application);
        application = validateApplication(application);
        application.setRedoPostProcess(Application.PostProcessingState.DONE);
        if (null == application.getModelVersion())
            application.setModelVersion(Application.CURRENT_MODEL_VERSION);
        return application;
    }

    private Application validateApplication(Application application) {
        Map<String, String> allAnswers = application.getVastauksetMerged();
        Form form = formService.getForm(application.getApplicationSystemId());
        ValidationInput validationInput = new ValidationInput(form, allAnswers,
                application.getOid(), application.getApplicationSystemId(), ValidationInput.ValidationContext.background);
        ValidationResult formValidationResult = elementTreeValidator.validate(validationInput);
        if (formValidationResult.hasErrors()) {
            application.incomplete();
        } else {
            application.activate();
        }
        return application;
    }
}
