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
package fi.vm.sade.haku.oppija.yksilointi;

import fi.vm.sade.haku.healthcheck.StatusRepository;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import org.apache.commons.lang.StringUtils;
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
    private boolean sendMail;

    private fi.vm.sade.haku.oppija.yksilointi.YksilointiWorker worker;
    private StatusRepository statusRepository;

    @Autowired
    public Scheduler(YksilointiWorker worker, StatusRepository statusRepository) {
        this.worker = worker;
        this.statusRepository = statusRepository;
    }

    public void runProcess() {
        runProcessing(YksilointiWorker.ProcessingType.POST_PROCESS);
    }

    public void runIdentification() {
        runProcessing(YksilointiWorker.ProcessingType.IDENTIFICATION);
    }

    public void redoPostprocess() {
        runProcessing(YksilointiWorker.ProcessingType.REDO_POST_PROCESS);
    }

    private void runProcessing(YksilointiWorker.ProcessingType processingType){
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
            worker.processApplications(processingType, sendMail);
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
            statusRepository.write("model upgrade scheduler", new HashMap<String, String>() {{ put("state", "start");}});
            worker.processModelUpdate();
            statusRepository.write("model upgrade scheduler", new HashMap<String, String>() {{
                put("state", "done");
            }});
        } else {
            statusRepository.write("model upgrade scheduler", new HashMap<String, String>() {{ put("state", "halted");}});
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
}
