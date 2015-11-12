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
package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.haku.healthcheck.StatusRepository;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PostProcessingState;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.lomake.service.impl.SystemSession;
import fi.vm.sade.haku.oppija.postprocess.DemoCleanupWorker;
import fi.vm.sade.haku.oppija.postprocess.PostProcessWorker;
import fi.vm.sade.haku.oppija.repository.AuditLogRepository;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;

@Service
public class DemoCleanupWorkerImpl implements DemoCleanupWorker {

    public static final Logger LOGGER = LoggerFactory.getLogger(DemoCleanupWorkerImpl.class);

    private final ApplicationDAO applicationDAO;

    @Autowired
    public DemoCleanupWorkerImpl(final ApplicationDAO applicationDAO) {
        this.applicationDAO = applicationDAO;
    }


    @Override
    public int cleanup() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -1);
        return applicationDAO.removeApplicationsReceivedBeforeDate(cal.getTime());
    }
}