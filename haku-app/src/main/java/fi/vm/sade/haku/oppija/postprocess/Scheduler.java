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
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class Scheduler {

    public static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);
    private boolean run;
    private boolean runModelUpgrade;
    private boolean runEligibilityCheck;
    private boolean sendMail;

    private PostProcessWorker processWorker;
    private UpgradeWorker upgradeWorker;
    private EligibilityCheckWorker eligibilityCheckWorker;
    private StatusRepository statusRepository;

    @Autowired
    public Scheduler(PostProcessWorker processWorker, UpgradeWorker upgradeWorker, StatusRepository statusRepository,
                     EligibilityCheckWorker eligibilityCheckWorker) {
        this.processWorker = processWorker;
        this.upgradeWorker = upgradeWorker;
        this.statusRepository = statusRepository;
        this.eligibilityCheckWorker = eligibilityCheckWorker;
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
        final String statusOperation = processingType.toString()+ " scheduler";
        if (!run) {
            statusRepository.write(statusOperation, new HashMap<String, String>() {{
                put("state", "halted");
            }});
            return;
        }
        statusRepository.write(statusOperation, new HashMap<String, String>() {{
            put("state", "start");
        }});

        try {
            processWorker.processApplications(processingType, sendMail);
            statusRepository.write(statusOperation, new HashMap<String, String>() {{
                put("state", "done");
            }});
        } catch (final Exception e) {
            statusRepository.write(statusOperation, new HashMap<String, String>() {{
                put("state", "error");
                put("error", e.getMessage());
            }});
            LOGGER.error("Error processing application with {}",statusOperation,  e);
            // run could be set to false. But first it need a method to re-enable it at runtime.
        }
    }

    public void runModelUpgrade() {
        if (run && runModelUpgrade) {
            statusRepository.write("MODEL UPGRAGE scheduler", new HashMap<String, String>() {{ put("state", "start");}});
            upgradeWorker.processModelUpdate();
            statusRepository.write("MODEL UPGRAGE scheduler", new HashMap<String, String>() {{
                put("state", "done");
            }});
        } else {
            statusRepository.write("MODEL UPGRAGE scheduler", new HashMap<String, String>() {{ put("state", "halted");}});
        }
    }

    public void runEligibilityCheck() {
        if (run && runEligibilityCheck) {
            statusRepository.write("ELIGIBILITY CHECK scheduler", new HashMap<String, String>() {{ put("state", "start");}});
            eligibilityCheckWorker.checkEligibilities();
            statusRepository.write("ELIGIBILITY CHECK scheduler", new HashMap<String, String>() {{
                put("state", "done");
            }});
        } else {
            statusRepository.write("ELIGIBILITY CHECK scheduler", new HashMap<String, String>() {{ put("state", "halted");}});
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
}
