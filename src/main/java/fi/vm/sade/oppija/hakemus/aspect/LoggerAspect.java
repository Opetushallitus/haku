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

package fi.vm.sade.oppija.hakemus.aspect;


import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.model.Tapahtuma;
import fi.vm.sade.oppija.lomake.domain.HakuLomakeId;
import fi.vm.sade.oppija.lomake.domain.User;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * An aspect that handel different logging operations.
 * <p/>
 * Logging is handled with a logger client module that passes the log events
 * to log-service.
 *
 * @author Hannu Lyytikainen
 */

@Aspect
@Component
public class LoggerAspect {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoggerAspect.class);

    @Autowired
    private Logger logger;

    /**
     * Logs event when a form phase is successfully saved
     * as application data in data store.
     */
    @AfterReturning(pointcut = "execution(* fi.vm.sade.oppija.hakemus.dao.ApplicationDAO.laitaVireille(..)) && args(lomakeId,user,..)",
            returning = "oid")
    public void logSavePhase(HakuLomakeId lomakeId, User user, String oid) {
        try {
            Tapahtuma t = new Tapahtuma();
            t.setMuutoksenKohde("Haku: " + lomakeId.getApplicationPeriodId()
                    + ", käyttäjä: " + user.getUserName() + ", hakemus oid: " + oid);
            t.setAikaleima(new Date());
            t.setKenenTietoja("" + user.getUserName());
            t.setTapahtumatyyppi("Hakemus lähetetty");
            t.setTekija("Hakemus Service");
            t.setUusiArvo("SUBMITTED");
            t.setVanhaArvo("DRAFT");
            logger.log(t);
        } catch (Exception e) {
            LOGGER.warn("Could not log laitaVireille event");
        }
    }

}
