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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {

    public static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);
    private boolean run;
    private boolean runModelUpgrade;
    private boolean sendMail;

    private fi.vm.sade.haku.oppija.yksilointi.YksilointiWorker worker;

    @Autowired
    public Scheduler(YksilointiWorker worker) {
        this.worker = worker;
    }

    public void runProcess() {
        if (run) {
            try {
                LOGGER.debug("Running identification scheduler {}", System.currentTimeMillis());
                worker.processApplications(sendMail);
                LOGGER.debug("Finished running identification scheduler {}", System.currentTimeMillis());
            } catch (Exception e) {
                LOGGER.error("Error processing applications", e);
                //run = false;
            }
        }
    }

    public void runIdentification() {
        if (run) {
            LOGGER.debug("Running identification scheduler {}", System.currentTimeMillis());
            worker.processIdentification();
            LOGGER.debug("Finished running identification scheduler {}", System.currentTimeMillis());
        }
    }

    public void redoPostprocess() {
        if (run) {
            LOGGER.debug("Running identification scheduler {}", System.currentTimeMillis());
            worker.redoPostprocess(sendMail);
            LOGGER.debug("Finished running identification scheduler {}", System.currentTimeMillis());
        }
    }

    public void runModelUpgrade() {
        if (run && runModelUpgrade) {
            LOGGER.debug("Running identification scheduler {}", System.currentTimeMillis());
            worker.processModelUpdate();
            LOGGER.debug("Finished running identification scheduler {}", System.currentTimeMillis());
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
