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


import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Change;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.lomake.service.Session;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
@Service
public class HistoryAspect {

    public static final Logger LOGGER = LoggerFactory.getLogger(HistoryAspect.class);
    private final Session userSession;
    private final ApplicationDAO applicationDAO;
    private final Boolean disableHistory;

    @Autowired
    public HistoryAspect(final Session userSession, final ApplicationDAO applicationDAO,
                         @Value("${disableHistory:false}") String disableHistory) {
        this.userSession = userSession;
        this.applicationDAO = applicationDAO;
        this.disableHistory = Boolean.valueOf(disableHistory);
    }

    @Before("execution(* fi.vm.sade.haku.oppija.hakemus.service.ApplicationService.update(..)) && args(queryApplication,application)")
    public void addChangeHistoryToApplication(final Application queryApplication, final Application application) {
        LOGGER.debug("addChangeHistoryToApplication");
        if (!disableHistory) {
            Application oldApplication = applicationDAO.find(queryApplication).get(0);
            ApplicationDiffUtil.addHistoryBasedOnChangedAnswers(application, oldApplication, userSession.getUser().getUserName(), "update");
        }
    }
}
