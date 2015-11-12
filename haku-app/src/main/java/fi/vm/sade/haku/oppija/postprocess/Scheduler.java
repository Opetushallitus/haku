/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.haku.oppija.postprocess;

import fi.vm.sade.haku.healthcheck.StatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

import static fi.vm.sade.haku.oppija.postprocess.EligibilityCheckWorker.SCHEDULER_ELIGIBILITY_CHECK;
import static fi.vm.sade.haku.oppija.postprocess.UpgradeWorker.SCHEDULER_MODEL_UPGRADE;

@Service
public class Scheduler {

    public static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);
    private boolean run;
    private boolean runModelUpgrade;
    private boolean runEligibilityCheck;
    private boolean sendMail;
    private boolean demoCleanup;

    private PostProcessWorker processWorker;
    private UpgradeWorker upgradeWorker;
    private EligibilityCheckWorker eligibilityCheckWorker;
    private StatusRepository statusRepository;
    private DemoCleanupWorker demoCleanupWorker;

    private boolean demoMode;

    @Autowired
    public Scheduler(PostProcessWorker processWorker, UpgradeWorker upgradeWorker, StatusRepository statusRepository,
                     EligibilityCheckWorker eligibilityCheckWorker, @Value("${mode.demo:false}") boolean demoMode,
                     DemoCleanupWorker demoCleanupWorker) {
        this.processWorker = processWorker;
        this.upgradeWorker = upgradeWorker;
        this.statusRepository = statusRepository;
        this.eligibilityCheckWorker = eligibilityCheckWorker;
        this.demoMode = demoMode;
        this.demoCleanupWorker = demoCleanupWorker;
    }

    public void runProcess() {
        runProcessing(PostProcessWorker.ProcessingType.POST_PROCESS);
    }

    public void runIdentification() {
        runProcessing(PostProcessWorker.ProcessingType.IDENTIFICATION);
    }

    public void redoPostprocess() {
        runProcessing(PostProcessWorker.ProcessingType.REDO_POST_PROCESS);
    }

    private void runProcessing(PostProcessWorker.ProcessingType processingType){
        final String statusOperation = processingType.toString();
        if (run) {
            statusRepository.startSchedulerRun(statusOperation);

            try {
                processWorker.processApplications(processingType, sendMail);
                statusRepository.endSchedulerRun(statusOperation);
            } catch (final Exception e) {
                statusRepository.schedulerError(statusOperation, e.getMessage());
                LOGGER.error("Error processing application with {} scheduler", statusOperation, e);
                // run could be set to false. But first it need a method to re-enable it at runtime.
            }

        } else {
            statusRepository.haltSchedulerRun(statusOperation);
        }
    }

    public void runModelUpgrade() {
        if (run && runModelUpgrade) {
            statusRepository.startSchedulerRun(SCHEDULER_MODEL_UPGRADE);
            upgradeWorker.processModelUpdate();
            statusRepository.endSchedulerRun(SCHEDULER_MODEL_UPGRADE);
        } else {
            statusRepository.haltSchedulerRun(SCHEDULER_MODEL_UPGRADE);
        }
    }

    public void runEligibilityCheck() {
        if (run && runEligibilityCheck) {
            statusRepository.startSchedulerRun(SCHEDULER_ELIGIBILITY_CHECK);
            Date started = new Date();
            eligibilityCheckWorker.checkEligibilities(statusRepository.getLastSuccessStarted(SCHEDULER_ELIGIBILITY_CHECK));
            statusRepository.recordLastSuccess(SCHEDULER_ELIGIBILITY_CHECK, started);
            statusRepository.endSchedulerRun(SCHEDULER_ELIGIBILITY_CHECK);
        } else {
            statusRepository.haltSchedulerRun(SCHEDULER_ELIGIBILITY_CHECK);
        }
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public void setSendMail(boolean sendMail) {
        this.sendMail = sendMail;
    }

    public void setRunModelUpgrade(boolean runModelUpgrade) {
        this.runModelUpgrade = runModelUpgrade;
    }

    public void setRunEligibilityCheck(boolean runEligibilityCheck) {
        this.runEligibilityCheck = runEligibilityCheck;
    }

    public void setDemoCleanup(boolean demoCleanup) {
        this.demoCleanup = demoCleanup;
    }

    public void runDemoModeCleanup() {
        if(this.demoMode && this.demoCleanup) {
            LOGGER.info("Starting cleanup for demo environment");
            int deleted = this.demoCleanupWorker.cleanup();
            LOGGER.info("Deleted rows: " + deleted);
        } else if(this.demoCleanup) {
            throw new RuntimeException("Tried to run demo mode mongo cleanup when not in demo environment!");
        }
    }

}
