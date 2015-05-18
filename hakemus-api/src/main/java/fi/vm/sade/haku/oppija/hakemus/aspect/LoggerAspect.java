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
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
import fi.vm.sade.haku.oppija.lomake.service.Session;
import fi.vm.sade.haku.oppija.repository.AuditLogRepository;
import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.model.Tapahtuma;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private final Session userSession;
    private final AuditLogRepository auditLogRepository;
    private final String serverName;

    @Autowired
    public LoggerAspect(final Logger logger, final Session userSession, final AuditLogRepository auditLogRepository, @Value("${server.name}") final String serverName ) {
        this.logger = logger;
        this.userSession = userSession;
        this.auditLogRepository = auditLogRepository;
        if (null == serverName) {
            this.serverName = "localhost";
        }else {
            this.serverName = serverName;
        }
    }

    /**
     * Logs event when a form phase is successfully saved
     * as application data in data store.
     */
    @AfterReturning(pointcut = "execution(* fi.vm.sade.haku.oppija.hakemus.service.ApplicationService.submitApplication(..)) && args(applicationSystemId,..)",
            returning = "application")
    public void logSubmitApplication(final String applicationSystemId, final Application application) {

        Tapahtuma t = null;
        try {
            t = createTapahtuma();
            t.setType("Hakemus lähetetty");

            t.setTarget("Haku: " + applicationSystemId
                    + ", käyttäjä: " + userSession.getUser().getUserName() + ", hakemus oid: " + application.getOid());
            t.setUserActsForUser("" + userSession.getUser().getUserName());
            t.setUser("Hakemus Service");
            Map<String, String> answers = application.getVastauksetMerged();
            for (Map.Entry<String, String> answer : answers.entrySet()) {
                t.addValueChange(answer.getKey(), null, answer.getValue());
            }
            for (PreferenceEligibility e: application.getPreferenceEligibilities()) {
                t.addValueChange(ApplicationDiffUtil.auditLogKey(e), null, ApplicationDiffUtil.auditLogValue(e));
            }
            LOGGER.debug(t.toString());
            auditLogRepository.save(t);
            //logger.log(t);
        } catch (Exception e) {
            LOGGER.error("Could not log submit application event. {}", t, e);
        }
    }

    public void logUpdateApplication(final Application application, final ApplicationPhase applicationPhase) {

        Tapahtuma tapahtuma = null;
        try {
            MapDifference<String, String> diffAnswers = ApplicationDiffUtil.diffAnswers(application, applicationPhase);
            AnswersDifference answersDifference = new AnswersDifference(diffAnswers);
            List<Difference> differences = answersDifference.getDifferences();

            tapahtuma = createTapahtuma();
            tapahtuma.setType("Hakemuksen muokkaus");
            tapahtuma.setTarget("hakemus: " + application.getOid() + ", vaihe: " + applicationPhase.getPhaseId());
            tapahtuma.setUserActsForUser(userSession.getUser().getUserName());
            tapahtuma.setUser(userSession.getUser().getUserName());

            for (Difference difference : differences) {
                tapahtuma.addValueChange(difference.getKey(), difference.getOldValue(), difference.getNewValue());
            }
            LOGGER.debug(tapahtuma.toString());

            auditLogRepository.save(tapahtuma);
            //logger.log(tapahtuma);

        } catch (Exception e) {
            LOGGER.error("Could not log update application event. {}", tapahtuma, e);
        }
    }

    public void logUpdateApplicationInPostProcessing(final Application application, final List<Map<String, String>> changes, final String changeType) {

        Tapahtuma tapahtuma = null;

        try {
            tapahtuma = createTapahtuma();
            tapahtuma.setTarget("hakemus: " + application.getOid());
            tapahtuma.setUserActsForUser(userSession.getUser().getUserName());
            tapahtuma.setType(changeType);
            tapahtuma.setUser(userSession.getUser().getUserName());
            for (Map<String, String> diff : changes) {
                tapahtuma.addValueChange(diff.get(ApplicationDiffUtil.FIELD), diff.get(ApplicationDiffUtil.OLD_VALUE), diff.get(ApplicationDiffUtil.NEW_VALUE));
            }
            LOGGER.debug(tapahtuma.toString());

            auditLogRepository.save(tapahtuma);
            //logger.log(tapahtuma);

        } catch (Exception e) {
            LOGGER.error("Could not log update application event. {}", tapahtuma, e);
        }
    }

    private Tapahtuma createTapahtuma(){
        final Tapahtuma tapahtuma = new Tapahtuma();
        tapahtuma.setTimestamp(new Date());
        tapahtuma.setSystem("haku");
        tapahtuma.setHost(serverName);
        return tapahtuma;
    }

}
