package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.auditlog.haku.HakuOperation;
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

import static fi.vm.sade.haku.AuditHelper.AUDIT;
import static fi.vm.sade.haku.AuditHelper.builder;
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

    @Autowired
    public PaymentDueDateProcessingWorker(ApplicationDAO applicationDAO, HakumaksuService hakumaksuService) {
        this.applicationDAO = applicationDAO;
        this.hakumaksuService = hakumaksuService;
    }

    private void addPassivationNoteToApplication(Application application) {
        application.addNote(new ApplicationNote(
                "Hakemus passivoitu, koska käsittelymaksua ei maksettu eräpäivään mennessä",
                new Date(),
                SYSTEM_USER));
    }

    private int passivate(Application application, final Application original) {
        application.setState(State.PASSIVE);
        application.setRequiredPaymentState(Application.PaymentState.NOT_OK);
        addHistoryBasedOnChangedAnswers(application, original, SYSTEM_USER, "Payment Due Date Post Processing");
        addPassivationNoteToApplication(application);
        return applicationDAO.update(new Application() {{
            setOid(original.getOid());
            setUpdated(original.getUpdated());
        }}, application);
    }

    /**
     * Logic for payment due date processing. Should only be called from scheduler.
     */
    public void processPaymentDueDates() {
        List<Application> applications = this.applicationDAO.getNextForPaymentDueDateProcessing(BATCH_SIZE);
        for(Application application : applications) {
            if (hakumaksuService.allApplicationOptionsRequirePayment(application)) {
                int retries = 0;
                Application original = application.clone();
                while (retries < MAX_RETRIES) {
                    int status = passivate(application, original);
                    if (status == 0) {
                        application = applicationDAO.getApplication(application.getOid());
                        if (application == null) {
                            log.warn("Application with oid {} went missing during payment due date processing", original.getOid());
                            break;
                        }
                        original = application.clone();
                        retries++;
                    } else if (status == 1) {
                        AUDIT.log(builder()
                                .hakemusOid(application.getOid())
                                .setOperaatio(HakuOperation.CHANGE_APPLICATION_STATE)
                                .add("oldValue", nameOrEmpty(original.getState()))
                                .add("newValue", nameOrEmpty(application.getState()))
                                .build());
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
    }

    /**
     * For post processing, returns mutated application.
     * @param application
     * @return
     */
    public Application processPaymentDueDate(Application application) {
        if (PaymentDueDateRules.evaluatePaymentDueDateRules(application)) {
            if (hakumaksuService.allApplicationOptionsRequirePayment(application)) {
                application.setState(State.PASSIVE);
                addPassivationNoteToApplication(application);
                application.setRequiredPaymentState(Application.PaymentState.NOT_OK);
                log.info("Application {} state set to PASSIVE and requiredPaymentState set to NOT_OK", application.getOid());
            } else {
                log.info("Not all application options require payment in application {}", application.getOid());
            }
        }
        return application;
    }

}
