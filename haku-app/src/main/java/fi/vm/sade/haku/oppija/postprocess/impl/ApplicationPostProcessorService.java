package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
public class ApplicationPostProcessorService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPostProcessorService.class);

    private final ApplicationService applicationService;
    private final BaseEducationService baseEducationService;
    private final ElementTreeValidator elementTreeValidator;
    private final FormService formService;
    private final AuthenticationService authenticationService;

    @Value("${scheduler.skipSendingSchool.automatic:false}")
    private boolean skipSendingSchoolAutomatic;

    @Autowired
    public ApplicationPostProcessorService(final ApplicationService applicationService,
                                           final BaseEducationService baseEducationService,
                                           final FormService formService,
                                           final ElementTreeValidator elementTreeValidator,
                                           final AuthenticationService authenticationService){
        this.applicationService = applicationService;
        this.baseEducationService = baseEducationService;
        this.formService = formService;
        this.elementTreeValidator = elementTreeValidator;
        this.authenticationService = authenticationService;
    }

    public Application process(Application application) throws IOException{
        application = addPersonOid(application);
        if (!skipSendingSchoolAutomatic) {
            application = baseEducationService.addSendingSchool(application);
//            application = baseEducationService.addBaseEducation(application);
        }
        application = applicationService.updateAuthorizationMeta(application);
        application = applicationService.ensureApplicationOptionGroupData(application);
        application = applicationService.updateAutomaticEligibilities(application);
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

    /**
     * Set proper user for this application. If user can be authenticated, activate application. Otherwise, set
     * application as incomplete.
     *
     * @param application to process
     * @return processed application
     */
    Application addPersonOid(Application application) {
        Map<String, String> allAnswers = application.getVastauksetMerged();

        LOGGER.debug("start addPersonAndAuthenticate, {}", System.currentTimeMillis() / 1000L);

        PersonBuilder personBuilder = PersonBuilder.start()
                .setFirstNames(allAnswers.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES))
                .setNickName(allAnswers.get(OppijaConstants.ELEMENT_ID_NICKNAME))
                .setLastName(allAnswers.get(OppijaConstants.ELEMENT_ID_LAST_NAME))
                .setSex(allAnswers.get(OppijaConstants.ELEMENT_ID_SEX))
                .setLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_LANGUAGE))
                .setNationality(allAnswers.get(OppijaConstants.ELEMENT_ID_NATIONALITY))
                .setContactLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE))
                .setSocialSecurityNumber(allAnswers.get(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER))
                .setNoSocialSecurityNumber(!Boolean.valueOf(allAnswers.get(OppijaConstants.ELEMENT_ID_HAS_SOCIAL_SECURITY_NUMBER)))
                .setDateOfBirth(allAnswers.get(OppijaConstants.ELEMENT_ID_DATE_OF_BIRTH))
                .setPersonOid(application.getPersonOid())
                .setSecurityOrder(false);

        Person personBefore = personBuilder.get();
        LOGGER.debug("Calling addPerson");
        try {
            Person personAfter = authenticationService.addPerson(personBefore);
            LOGGER.debug("Called addPerson");
            LOGGER.debug("Calling modifyPersonalData");
            application = application.modifyPersonalData(personAfter);
            LOGGER.debug("Called modifyPersonalData");
        } catch (Throwable t) {
            LOGGER.error("Unexpected happened: ", t);
        }
        return application;
    }


    Application checkStudentOid(Application application) {
        String personOid = application.getPersonOid();

        if (isEmpty(personOid)) {
            application = addPersonOid(application);
            personOid = application.getPersonOid();
        }

        final String studentOid = application.getStudentOid();

        if (isNotEmpty(studentOid)){
            application.flagStudentIdentificationDone();
        } else if (isNotEmpty(personOid)) {
            final Person person = authenticationService.checkStudentOid(application.getPersonOid());
            if (person != null && isNotEmpty(person.getStudentOid())) {
                application.modifyPersonalData(person);
                application.flagStudentIdentificationDone();
            }
        }
        return application;
    }
}
