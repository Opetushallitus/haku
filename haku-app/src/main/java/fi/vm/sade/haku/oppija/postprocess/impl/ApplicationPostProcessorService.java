package fi.vm.sade.haku.oppija.postprocess.impl;

import static fi.vm.sade.haku.oppija.lomake.util.StringUtil.nameOrEmpty;
import static org.apache.commons.lang.StringUtils.isEmpty;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import fi.vm.sade.haku.HakuOperation;
import fi.vm.sade.haku.VirkailijaAuditLogger;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PaymentState;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.ValintaServiceCallFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Service
public class ApplicationPostProcessorService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPostProcessorService.class);

    private final ApplicationService applicationService;
    private final ApplicationSystemService applicationSystemService;
    private final BaseEducationService baseEducationService;
    private final ElementTreeValidator elementTreeValidator;
    private final FormService formService;
    private final AuthenticationService authenticationService;
    private final HakuService hakuService;
    private final HakumaksuService hakumaksuService;
    private final PaymentDueDateProcessingWorker paymentDueDateProcessingWorker;
    private final VirkailijaAuditLogger virkailijaAuditLogger;

    @Value("${scheduler.retryFailQuickCount:20}")
    private int retryFailQuickCount;

    @Value("${scheduler.retryFailedAgainTime:21600000}")
    private int retryFailedAgainTime;

    @Value("${application.postprocessor.valinta.timeout.millis:120000}")
    private int postProcessorValintaTimeoutMillis;

    @Autowired
    public ApplicationPostProcessorService(final ApplicationService applicationService,
                                           final ApplicationSystemService applicationSystemService,
                                           final BaseEducationService baseEducationService,
                                           final FormService formService,
                                           final ElementTreeValidator elementTreeValidator,
                                           final AuthenticationService authenticationService,
                                           final HakuService hakuService,
                                           final HakumaksuService hakumaksuService,
                                           final PaymentDueDateProcessingWorker paymentDueDateProcessingWorker,
                                           final VirkailijaAuditLogger virkailijaAuditLogger){
        this.applicationService = applicationService;
        this.applicationSystemService = applicationSystemService;
        this.baseEducationService = baseEducationService;
        this.formService = formService;
        this.elementTreeValidator = elementTreeValidator;
        this.authenticationService = authenticationService;
        this.hakuService = hakuService;
        this.hakumaksuService = hakumaksuService;
        this.paymentDueDateProcessingWorker = paymentDueDateProcessingWorker;
        this.virkailijaAuditLogger = virkailijaAuditLogger;
    }

    public Application process(Application application) throws IOException, ExecutionException, InterruptedException, ValintaServiceCallFailedException {
        application = addPersonOid(application, "jälkikäsittely");
        application = baseEducationService.addSendingSchool(application);
        application = applicationService.postProcessApplicationAnswers(application, Duration.ofMillis(postProcessorValintaTimeoutMillis));
        application = applicationService.updateAuthorizationMeta(application);
        application = applicationService.updateAutomaticEligibilities(application);

        ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        if (applicationSystem.isMaksumuuriKaytossa()) {
            PaymentState oldPaymentState = application.getRequiredPaymentState();
            Date oldDueDate = application.getPaymentDueDate();

            application = hakumaksuService.processPayment(application, applicationSystem.getApplicationPeriods());

            boolean hasChanges = false;
            Changes.Builder changesBuilder = new Changes.Builder();
            Target.Builder targetBuilder = new Target.Builder();

            targetBuilder.setField("hakemusOid", application.getOid());

            if (application.getRequiredPaymentState() != oldPaymentState) {
                changesBuilder.updated("payment_state_change", oldPaymentState.name(), application.getRequiredPaymentState().name());
                hasChanges = true;
            }

            if (application.getPaymentDueDate() != oldDueDate) {
                String newDate = application.getPaymentDueDate() != null ? String.format("%d", application.getPaymentDueDate().getTime()) : "";
                String oldDate = oldDueDate != null ? String.format("%d", oldDueDate.getTime()) : "";

                changesBuilder.updated("payment_due_date", oldDate, newDate);
                hasChanges = true;
            }
            if(hasChanges) {
                virkailijaAuditLogger.log(null, HakuOperation.PROCESS_PAYMENT_DATE_UPDATES, targetBuilder.build(), changesBuilder.build());
            }
        }
        if (applicationSystem.isMaksumuuriKaytossa()) {
            application = paymentDueDateProcessingWorker.processPaymentDueDate(application);
        }

        application.setRedoPostProcess(Application.PostProcessingState.DONE);
        if (null == application.getModelVersion())
            application.setModelVersion(Application.CURRENT_MODEL_VERSION);
        return application;
    }

    /**
     * Set proper user for this application. If user can be authenticated, activate application. Otherwise, set
     * application as incomplete.
     *
     * @param application to process
     * @return processed application
     */
    Application addPersonOid(Application application, String changedBy) {
        Map<String, String> allAnswers = application.getVastauksetMerged();
        final String originalPersonOid = application.getPersonOid();
        final String originalStudentOid = application.getStudentOid();

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
                .setEmail(allAnswers.get(OppijaConstants.ELEMENT_ID_EMAIL))
                .setPersonOid(application.getPersonOid())
                .setSecurityOrder(false);

        Person personBefore = personBuilder.get();

        Person personAfter = authenticationService.addPerson(personBefore);
        application.modifyPersonalData(personAfter);

        application.logPersonOidIfChanged(changedBy, originalPersonOid);
        application.logStudentOidIfChanged(changedBy, originalStudentOid);

        return application;
    }

    Application checkStudentOid(Application application) {
        Long lastFailedRetryTime = application.getAutomatedProcessingFailRetryTime();
        Integer failCount = application.getAutomatedProcessingFailCount();
        if(lastFailedRetryTime == null || failCount == null || failCount < this.retryFailQuickCount || lastFailedRetryTime < (System.currentTimeMillis() - retryFailedAgainTime)) {
            application = addPersonOid(application, "yksilöinti");
            if (isEmpty(application.getStudentOid())) {
                application.setAutomatedProcessingFailCount(application.getAutomatedProcessingFailCount() == null ? 1 : application.getAutomatedProcessingFailCount() + 1);
                application.setAutomatedProcessingFailRetryTime(System.currentTimeMillis());
            }
            else {
                application.flagStudentIdentificationDone();
            }
        }
        return application;
    }

    public int getRetryFailQuickCount() {
        return retryFailQuickCount;
    }

    public void setRetryFailQuickCount(int retryFailQuickCount) {
        this.retryFailQuickCount = retryFailQuickCount;
    }

    public int getRetryFailedAgainTime() {
        return retryFailedAgainTime;
    }

    public void setRetryFailedAgainTime(int retryFailedAgainTime) {
        this.retryFailedAgainTime = retryFailedAgainTime;
    }


}
