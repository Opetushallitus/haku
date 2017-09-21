package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import fi.vm.sade.haku.HakuOperation;
import fi.vm.sade.haku.VirkailijaAuditLogger;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.State;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoImpl.PaymentDueDateRules;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;
import static fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.SYSTEM_USER;
import static fi.vm.sade.haku.oppija.lomake.util.StringUtil.nameOrEmpty;

@Service
public class PaymentDueDateProcessingWorker {
    public static final String PAYMENT_DUE_DATE_PROCESSING = "PAYMENT DUE DATE PROCESSING";
    private static final Logger log = LoggerFactory.getLogger(PaymentDueDateProcessingWorker.class);
    public static final int BATCH_SIZE = 10000;
    public static final int MAX_RETRIES = 3;

    private ApplicationDAO applicationDAO;
    private HakumaksuService hakumaksuService;
    private final VirkailijaAuditLogger virkailijaAuditLogger;

    @Autowired
    public PaymentDueDateProcessingWorker(ApplicationDAO applicationDAO, HakumaksuService hakumaksuService,
                                          VirkailijaAuditLogger virkailijaAuditLogger) {
        this.applicationDAO = applicationDAO;
        this.hakumaksuService = hakumaksuService;
        this.virkailijaAuditLogger = virkailijaAuditLogger;
    }

    private void addPassivationNoteToApplication(Application application) {
        application.addNote(new ApplicationNote(
                "Hakemus passivoitu, koska käsittelymaksua ei maksettu eräpäivään mennessä",
                new Date(),
                SYSTEM_USER));
    }

    private void addPaymentStateNoteToApplication(Application application, Application.PaymentState paymentState) {
        application.addNote(new ApplicationNote(
                String.format("Hakemuksen maksun tila muutettu arvoon %s", paymentState.name()),
                new Date(),
                SYSTEM_USER));
    }

    private int updateApplicationStates(Application application, final Application original) {

        boolean addHistoryAndUpdate = false;

        if (hakumaksuService.allApplicationOptionsRequirePayment(application)) {
            application.setState(State.PASSIVE);
            addPassivationNoteToApplication(application);
            addHistoryAndUpdate = true;
        }

        if(!Application.PaymentState.NOT_OK.equals(original.getRequiredPaymentState())) {
            application.setRequiredPaymentState(Application.PaymentState.NOT_OK);
            addPaymentStateNoteToApplication(application, Application.PaymentState.NOT_OK);
            addHistoryAndUpdate = true;
        }

        if(addHistoryAndUpdate) {
            addHistoryBasedOnChangedAnswers(application, original, SYSTEM_USER, "Payment Due Date Post Processing");

            return applicationDAO.update(new Application() {{
                setOid(original.getOid());
                setUpdated(original.getUpdated());
            }}, application);
        } else {
            return 1;
        }
    }

    /**
     * Logic for payment due date processing. Should only be called from scheduler.
     */
    public void processPaymentDueDates() {
        List<Application> applications = this.applicationDAO.getNextForPaymentDueDateProcessing(BATCH_SIZE);
        for(Application application : applications) {
            int retries = 0;
            Application original = application.clone();
            while (retries < MAX_RETRIES) {
                int status = updateApplicationStates(application, original);
                if (status == 0) {
                    application = applicationDAO.getApplication(application.getOid());
                    if (application == null) {
                        log.warn("Application with oid {} went missing during payment due date processing", original.getOid());
                        break;
                    }
                    original = application.clone();
                    retries++;
                } else if (status == 1) {
                    Target target= new Target.Builder().setField("hakemusOid", application.getOid()).build();
                    Changes changes = new Changes.Builder()
                            .updated("paymentDueDate", original.getState().name(), application.getState().name())
                            .build();
                    virkailijaAuditLogger.log(null, HakuOperation.CHANGE_APPLICATION_STATE, target, changes);
                    break;
                } else {
                    throw new RuntimeException(String.format("update of single application (oid: %s) modified more than one applications", application.getOid()));
                }
            }
            if (retries == MAX_RETRIES) {
                log.warn("Cannot update application {}: max update retries exceeded", application.getOid());
            }
        }
    }

    /**
     * For post processing, returns mutated application.
     * @param application
     * @return
     */
    public Application processPaymentDueDate(Application application) {
        if (PaymentDueDateRules.evaluatePaymentDueDateRules(application)) {
            if(!Application.PaymentState.NOT_OK.equals(application.getRequiredPaymentState())) {
                application.setRequiredPaymentState(Application.PaymentState.NOT_OK);
                addPaymentStateNoteToApplication(application, Application.PaymentState.NOT_OK);
                log.info("Application {} requiredPaymentState set to NOT_OK", application.getOid());
            }

            if (hakumaksuService.allApplicationOptionsRequirePayment(application)) {
                application.setState(State.PASSIVE);
                addPassivationNoteToApplication(application);
                log.info("Application {} state set to PASSIVE", application.getOid());
            }
        }
        return application;
    }

}
