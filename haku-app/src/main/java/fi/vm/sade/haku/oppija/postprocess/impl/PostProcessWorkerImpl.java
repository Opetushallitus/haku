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
import fi.vm.sade.haku.oppija.hakemus.service.impl.SendMailService;
import fi.vm.sade.haku.oppija.lomake.service.impl.SystemSession;
import fi.vm.sade.haku.oppija.postprocess.PostProcessWorker;
import fi.vm.sade.haku.oppija.repository.AuditLogRepository;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;

@Service
public class PostProcessWorkerImpl implements PostProcessWorker {

    public static final Logger LOGGER = LoggerFactory.getLogger(PostProcessWorkerImpl.class);

    private static final SystemSession systemSession = new SystemSession();
    private final ApplicationDAO applicationDAO;
    private final StatusRepository statusRepository;
    private final LoggerAspect loggerAspect;
    private final SendMailService sendMailService;
    private final ApplicationPostProcessorService applicationPostProcessorService;

    @Value("${scheduler.maxBatchSize:10}")
    private int maxBatchSize;

    final public String SYSTEM_USER = "järjestelmä";

    @Autowired
    public PostProcessWorkerImpl(final ApplicationDAO applicationDAO,
                                 final StatusRepository statusRepository,
                                 final fi.vm.sade.log.client.Logger logger,
                                 final AuditLogRepository auditLogRepository,
                                 final SendMailService sendMailService,
                                 final ApplicationPostProcessorService applicationPostProcessorService,
                                 @Value("${server.name}") final String serverName) {
        this.loggerAspect = new LoggerAspect(logger, systemSession, auditLogRepository, serverName);
        this.applicationDAO = applicationDAO;
        this.statusRepository = statusRepository;
        this.sendMailService = sendMailService;
        this.applicationPostProcessorService = applicationPostProcessorService;
    }

    @Override
    public void processApplications(final ProcessingType processingType, final boolean sendMail) {
        int count = 0;
        do {
            Application application = getNextApplicationFor(processingType);
            if (null == application)
                break;
            statusRepository.startOperation(processingType.toString(), application.getOid());
            switch (processingType) {
                case IDENTIFICATION:
                    runIdentification(application);
                    break;
                case POST_PROCESS:
                    processOneApplication(application, sendMail);
                    break;
                case REDO_POST_PROCESS:
                    reprocessOneApplication(application, sendMail);
                    break;
                default:
                    LOGGER.error("processApplication cannot handle process type {}", processingType);
            }
            statusRepository.endOperation(processingType.toString(), application.getOid());
        } while (++count < maxBatchSize);
    }

    private void processOneApplication(Application application, final boolean sendMail) {
        final Application queryApplication = new Application(application.getOid(), application.getVersion());
        try {
            //TODO =RS= add Version
            final Application original = application.clone();
            application = applicationPostProcessorService.process(application);
            final List<Map<String, String>> changes = addHistoryBasedOnChangedAnswers(application, original, SYSTEM_USER, "Post Processing");
            application.setLastAutomatedProcessingTime(System.currentTimeMillis());
            this.applicationDAO.update(queryApplication, application);
            loggerAspect.logUpdateApplicationInPostProcessing(application, changes, "Hakemuksen jälkikäsittely");
            if (sendMail) {
                try {
                    sendMailService.sendReceivedEmail(application);
                } catch (EmailException e) {
                    LOGGER.error("Send mail failed for application:" + application.getOid(), e);
                    throw e;
                }
            }
        } catch (Exception e) {
            LOGGER.error("post process failed for application: " + queryApplication, e);
            setProcessingStateToFailed(queryApplication, e.getMessage());
        }
    }

    private void reprocessOneApplication(Application application, final boolean sendMail) {
        PostProcessingState redo = application.getRedoPostProcess();
        if (PostProcessingState.NOMAIL.equals(redo) || PostProcessingState.FULL.equals(redo)) {
            processOneApplication(application, sendMail && PostProcessingState.FULL.equals(redo));
        } else {
            LOGGER.error("Application: {} in reprocess with incompatible flag: {}", application.getOid(), redo);
        }
    }

    private void runIdentification(Application application) {
        final Application updateQuery = new Application(application.getOid(), application.getVersion());
        try {
            final Application original = application.clone();
            application = applicationPostProcessorService.checkStudentOid(application);

            final List<Map<String, String>> changes = addHistoryBasedOnChangedAnswers(application, original, SYSTEM_USER, "Identification Post Processing");
            if (identificationModifiedApplication(application, original)) {
                application.setLastAutomatedProcessingTime(System.currentTimeMillis());
                applicationDAO.update(updateQuery, application);
                loggerAspect.logUpdateApplicationInPostProcessing(application, changes, "Hakemuksen yksilöinti");
            }
        } catch (Exception e) {
            LOGGER.error("post process (identification) failed for application: " + updateQuery.getOid(), e);
            setProcessingStateToFailed(updateQuery, null);
        }
    }

    private boolean identificationModifiedApplication(final Application application, final Application original){
        return !(Objects.equals(application.getPersonOid(), original.getPersonOid())
                && Objects.equals(application.getStudentOid(), original.getStudentOid())
                && application.getHistory().size() == original.getHistory().size()
                && Objects.equals(application.getStudentIdentificationDone(), original.getStudentIdentificationDone())
                && Objects.equals(application.getAutomatedProcessingFailCount(), original.getAutomatedProcessingFailCount()));
    }

    private Application getNextApplicationFor(final ProcessingType processingType) {
        switch (processingType) {
            case IDENTIFICATION:
                return applicationDAO.getNextWithoutStudentOid();
            case POST_PROCESS:
                return applicationDAO.getNextSubmittedApplication();
            case REDO_POST_PROCESS:
                return applicationDAO.getNextRedo();
            default:
                return null;
        }
    }
    private void setProcessingStateToFailed(final Application queryApplication, String message) {
        final Application application = applicationDAO.find(queryApplication).get(0);
        if(message != null) {
            final ApplicationNote note = new ApplicationNote("Hakemuksen jälkikäsittely epäonnistui: " + message, new Date(), SYSTEM_USER);
            application.addNote(note);
        }
        application.setRedoPostProcess(PostProcessingState.FAILED);
        this.applicationDAO.update(queryApplication, application);
    }
}
