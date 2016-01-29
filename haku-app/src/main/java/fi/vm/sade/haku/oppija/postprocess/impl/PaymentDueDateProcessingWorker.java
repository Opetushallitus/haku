package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.auditlog.haku.HakuOperation;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
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
    public static final Logger LOGGER = LoggerFactory.getLogger(PaymentDueDateProcessingWorker.class);
    public static final String PAYMENT_DUE_DATE_PROCESSING = "PAYMENT DUE DATE PROCESSING";
    public static final int BATCH_SIZE = 10000;

    private ApplicationDAO applicationDAO;
    private HakumaksuService hakumaksuService;

    @Autowired
    public PaymentDueDateProcessingWorker(ApplicationDAO applicationDAO, HakumaksuService hakumaksuService) {
        this.applicationDAO = applicationDAO;
        this.hakumaksuService = hakumaksuService;
    }

    public void processPaymentDueDates() {
        LOGGER.info("Start payment due dates prorcessing");
        Date processingStart = new Date();
        try {
            List<Application> applications = this.applicationDAO.getNextForPaymentDueDateProcessing(BATCH_SIZE);
            for(Application application : applications) {
                if (hakumaksuService.allApplicationOptionsRequirePayment(application)) {
                    final Application original = application.clone();
                    application.setState(Application.State.PASSIVE);
                    addHistoryBasedOnChangedAnswers(application, original, SYSTEM_USER, "Payment Due Date Post Processing");
                    applicationDAO.save(application);
                    AUDIT.log(builder()
                            .hakemusOid(application.getOid())
                            .setOperaatio(HakuOperation.CHANGE_APPLICATION_STATE)
                            .add("oldValue", nameOrEmpty(original.getState()))
                            .add("newValue", nameOrEmpty(application.getState()))
                            .build());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Payment due dates processing failed due to exception", e);
        } finally {
            Date processingEnd = new Date();
            LOGGER.info("End payment due dates processing (took {} ms)", (processingEnd.getTime() - processingStart.getTime()));
        }
    }
}
