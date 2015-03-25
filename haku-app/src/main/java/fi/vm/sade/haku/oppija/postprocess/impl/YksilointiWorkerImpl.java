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
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.impl.SystemSession;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.postprocess.YksilointiWorker;
import fi.vm.sade.haku.oppija.repository.AuditLogRepository;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class YksilointiWorkerImpl implements YksilointiWorker {

    public static final Logger LOGGER = LoggerFactory.getLogger(YksilointiWorkerImpl.class);

    private static final SystemSession systemSession = new SystemSession();
    private final ApplicationService applicationService;
    private final ApplicationSystemService applicationSystemService;
    private final BaseEducationService baseEducationService;
    private final ApplicationDAO applicationDAO;
    private final ElementTreeValidator elementTreeValidator;
    private final StatusRepository statusRepository;
    private final LoggerAspect loggerAspect;
    private final FormService formService;
    private final SendMailService sendMailService;

    @Value("${scheduler.maxBatchSize:10}")
    private int maxBatchSize;

    @Value("${scheduler.skipSendingSchool.automatic:false}")
    private boolean skipSendingSchoolAutomatic;
    @Value("${scheduler.skipSendingSchool.manual:false}")
    private boolean skipSendingSchoolManual;

    final String SYSTEM_USER = "järjestelmä";

    @Autowired
    public YksilointiWorkerImpl(final ApplicationService applicationService,
                                final ApplicationSystemService applicationSystemService,
                                final BaseEducationService baseEducationService,
                                final FormService formService,
                                final ApplicationDAO applicationDAO,
                                final ElementTreeValidator elementTreeValidator,
                                final StatusRepository statusRepository,
                                final fi.vm.sade.log.client.Logger logger,
                                final AuditLogRepository auditLogRepository,
                                final SendMailService sendMailService,
                                @Value("${server.name}") final String serverName) {
        this.loggerAspect = new LoggerAspect(logger, systemSession, auditLogRepository, serverName);
        this.applicationService = applicationService;
        this.applicationSystemService = applicationSystemService;
        this.baseEducationService = baseEducationService;
        this.formService = formService;
        this.applicationDAO = applicationDAO;
        this.elementTreeValidator = elementTreeValidator;
        this.statusRepository = statusRepository;
        this.sendMailService = sendMailService;
    }

    @Override
    public void processApplications(final ProcessingType processingType, final boolean sendMail) {
        int count = 0;
        do {
            Application application = getNextApplicationFor(processingType);
            if (null == application)
                break;
            writeStatus(processingType.toString(), "start", application);
            switch (processingType) {
            case IDENTIFICATION:
                applicationService.checkStudentOid(application);
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
            writeStatus(processingType.toString(), "done", application);
        } while (++count < maxBatchSize);
    }

    private void processOneApplication(Application application, final boolean sendMail){
        try {
            application = applicationService.addPersonOid(application);
            if (!skipSendingSchoolAutomatic) {
                application = baseEducationService.addSendingSchool(application);
                application = baseEducationService.addBaseEducation(application);
            }
            application = applicationService.updateAuthorizationMeta(application);
            application = applicationService.ensureApplicationOptionGroupData(application);
            application = validateApplication(application);
            application.setRedoPostProcess(PostProcessingState.DONE);
            if (null == application.getModelVersion())
                application.setModelVersion(Application.CURRENT_MODEL_VERSION);
            //TODO =RS= add Version
            application.setLastAutomatedProcessingTime(System.currentTimeMillis());
            this.applicationDAO.update(new Application(application.getOid()), application);
            if (sendMail) {
                try {
                    sendMailService.sendMail(application);
                } catch (EmailException e) {
                    LOGGER.error("Send mail failed for application:" + application.getOid(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("post process failed for application: " +application.getOid(), e);
            setProcessingStateToFailed(application.getOid(), e.getMessage());
        }
    }

    private void reprocessOneApplication(Application application, final boolean sendMail){
        PostProcessingState redo = application.getRedoPostProcess();
        if (PostProcessingState.NOMAIL.equals(redo) || PostProcessingState.FULL.equals(redo)){
            processOneApplication(application, sendMail && PostProcessingState.FULL.equals(redo));
        }
        else {
            LOGGER.error("Application: {} in reprocess with incompatible flag: {}", application.getOid(), redo);
        }
    }

    private void writeStatus(String operation, String state, Application application) {
        Map<String, String> statusData = new HashMap<String, String>();
        statusData.put("applicationOid", application != null ? application.getOid() : "(null)");
        statusData.put("state", state);
        statusRepository.write(operation, statusData);
    }

    private Application validateApplication(Application application) {
        Map<String, String> allAnswers = application.getVastauksetMerged();
        Form form = formService.getForm(application.getApplicationSystemId());
        ValidationInput validationInput = new ValidationInput(form, allAnswers,
                application.getOid(), application.getApplicationSystemId(), ValidationInput.ValidationContext.background);
        ValidationResult formValidationResult = elementTreeValidator.validate(validationInput);
        if (formValidationResult.hasErrors()) {
            application.incomplete();
        } else {
            application.activate();
        }
        return application;
    }


    private Application getNextApplicationFor(final ProcessingType processingType){
        switch (processingType){
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

    private void setProcessingStateToFailed(String oid, String message){
        Application application = applicationDAO.find(new Application(oid)).get(0);
        ApplicationNote note = new ApplicationNote("Hakemuksen jälkikäsittely epäonnistui: "+message,
                new Date(), "");
        application.addNote(note);
        application.setRedoPostProcess(PostProcessingState.FAILED);
        this.applicationDAO.update(new Application(oid), application);
    }
}
