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

package fi.vm.sade.haku.oppija.hakemus.aspect;


import com.google.common.collect.MapDifference;
import fi.vm.sade.haku.oppija.common.diff.AnswersDifference;
import fi.vm.sade.haku.oppija.common.diff.Difference;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.lomake.service.UserHolder;
import fi.vm.sade.haku.oppija.ui.service.impl.ApplicationUtil;
import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.model.Tapahtuma;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

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

    private final Logger logger;
    private final UserHolder userHolder;

    @Autowired
    public LoggerAspect(final Logger logger, final UserHolder userHolder) {
        this.logger = logger;
        this.userHolder = userHolder;
    }


    /**
     * Logs event when a form phase is successfully saved
     * as application data in data store.
     */
    @AfterReturning(pointcut = "execution(* fi.vm.sade.haku.oppija.hakemus.service.ApplicationService.submitApplication(..)) && args(applicationSystemId,..)",
            returning = "oid")
    public void logSubmitApplication(final String applicationSystemId, final String oid) {
        try {
            Tapahtuma t = new Tapahtuma();

            t.setTarget("Haku: " + applicationSystemId
                    + ", käyttäjä: " + userHolder.getUser().getUserName() + ", hakemus oid: " + oid);
            t.setTimestamp(new Date());
            t.setUserActsForUser("" + userHolder.getUser().getUserName());
            t.setType("Hakemus lähetetty");
            t.setUser("Hakemus Service");
            t.addValueChange("STATE", "DRAFT", "SUBMITTED");
            LOGGER.debug(t.toString());
            logger.log(t);
        } catch (Exception e) {
            LOGGER.warn("Could not log laitaVireille event");
        }
    }

    public void logUpdateApplication(final Application application, final ApplicationPhase applicationPhase) {
        try {

            MapDifference<String, String> diffAnswers = ApplicationUtil.diffAnswers(application, applicationPhase);
            AnswersDifference answersDifference = new AnswersDifference(diffAnswers);
            List<Difference> differences = answersDifference.getDifferences();
            Tapahtuma tapahtuma = new Tapahtuma();
            tapahtuma.setTarget("hakemus: " + application.getOid() +
                    ", vaihe: " + applicationPhase.getPhaseId());
            tapahtuma.setTimestamp(new Date());
            tapahtuma.setUserActsForUser(userHolder.getUser().getUserName());
            tapahtuma.setType("Hakemuksen muokkaus");
            tapahtuma.setUser(userHolder.getUser().getUserName());
            for (Difference difference : differences) {
                tapahtuma.addValueChange(difference.getKey(), difference.getOldValue(), difference.getNewValue());
            }
            LOGGER.debug(tapahtuma.toString());
            logger.log(tapahtuma);

        } catch (Exception e) {
            LOGGER.warn("Could not log update application event");
        }
    }

}
