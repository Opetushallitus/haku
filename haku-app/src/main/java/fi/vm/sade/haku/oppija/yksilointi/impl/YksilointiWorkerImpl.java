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
package fi.vm.sade.haku.oppija.yksilointi.impl;

import fi.vm.sade.haku.healthcheck.StatusRepository;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PostProcessingState;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.impl.SystemSession;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.repository.AuditLogRepository;
import fi.vm.sade.haku.oppija.yksilointi.YksilointiWorker;
import fi.vm.sade.haku.upgrade.Level4;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis.ValmisPhase.MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.EDUCATION_CODE_KEY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;

@Service
public class YksilointiWorkerImpl implements YksilointiWorker {

    public static final Logger LOGGER = LoggerFactory.getLogger(YksilointiWorkerImpl.class);
    public static final String TRUE = "true";
    private static final SystemSession systemSession = new SystemSession();
    private final ApplicationService applicationService;
    private final ApplicationSystemService applicationSystemService;
    private final BaseEducationService baseEducationService;
    private final ApplicationDAO applicationDAO;
    private final ElementTreeValidator elementTreeValidator;
    private final StatusRepository statusRepository;
    private final LoggerAspect loggerAspect;
    private FormService formService;

    private Map<String, Template> templateMap;
    private Map<String, Template> templateMapHigherEducation;

    @Value("${scheduler.maxBatchSize:10}")
    private int maxBatchSize;

    @Value("${scheduler.skipSendingSchool.automatic:false}")
    private boolean skipSendingSchoolAutomatic;
    @Value("${scheduler.skipSendingSchool.manual:false}")
    private boolean skipSendingSchoolManual;

    @Value("${email.smtp.debug:false}")
    private boolean smtpDebug;

    @Value("${email.smtp.host}")
    private String smtpHost;
    @Value("${email.smtp.port}")
    private Integer smtpPort;
    @Value("${email.replyTo}")
    private String replyTo;

    final String SYSTEM_USER = "järjestelmä";



    @Value("${scheduler.modelUpgrade.enableV2:false}")
    private boolean enableUpgradeV2;
    @Value("${scheduler.modelUpgrade.enableV3:true}")
    private boolean enableUpgradeV3;
    @Value("${scheduler.modelUpgrade.enableV4:true}")
    private boolean enableUpgradeV4;

    @Autowired
    public YksilointiWorkerImpl(ApplicationService applicationService,
                                ApplicationSystemService applicationSystemService,
                                BaseEducationService baseEducationService, FormService formService, ApplicationDAO applicationDAO,
                                ElementTreeValidator elementTreeValidator, StatusRepository statusRepository,
                                fi.vm.sade.log.client.Logger logger, AuditLogRepository auditLogRepository) {
        this.loggerAspect = new LoggerAspect(logger, systemSession, auditLogRepository);
        this.applicationService = applicationService;
        this.applicationSystemService = applicationSystemService;
        this.baseEducationService = baseEducationService;
        this.formService = formService;
        this.applicationDAO = applicationDAO;
        this.elementTreeValidator = elementTreeValidator;
        this.statusRepository = statusRepository;

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(VelocityEngine.INPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty(VelocityEngine.OUTPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.setProperty("class.resource.loader.path", "email");
        velocityEngine.setProperty("class.resource.loader.cache", "true");
        velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        velocityEngine.init();
        templateMap = new HashMap<String, Template>();
        templateMap.put("suomi", velocityEngine.getTemplate("email/application_received_fi.vm", "UTF-8"));
        templateMap.put("ruotsi", velocityEngine.getTemplate("email/application_received_sv.vm", "UTF-8"));
        templateMap.put("englanti", velocityEngine.getTemplate("email/application_received_en.vm", "UTF-8"));
        templateMapHigherEducation = new HashMap<String, Template>();
        templateMapHigherEducation.put("suomi", velocityEngine.getTemplate("email/application_received_higher_ed_fi.vm", "UTF-8"));
        templateMapHigherEducation.put("ruotsi", velocityEngine.getTemplate("email/application_received_higher_ed_sv.vm", "UTF-8"));
        templateMapHigherEducation.put("englanti", velocityEngine.getTemplate("email/application_received_higher_ed_en.vm", "UTF-8"));
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
            application.setModelVersion(Application.CURRENT_MODEL_VERSION);
            //TODO =RS= add Version
            this.applicationDAO.update(new Application(application.getOid()), application);
            if (sendMail) {
                try {
                    sendMail(application);
                } catch (EmailException e) {
                    LOGGER.error("Send mail failed for application:" + application.getOid(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("post process failed for application: " +application.getOid(), e);
            setProcessingStateToFailed(application.getOid(), e.getMessage());
        }
    }

    @Override
    public void processModelUpdate() {
        LOGGER.info("Start upgrading application model");
        oldProcess();
        upgradeModelVersion2to3();
        upgradeModelVersion3to4();
        LOGGER.info("Done upgrading application model");
    }

    private void oldProcess() {
        final Integer baseVersion =1;
        final Integer targetVersion =2;
        if ((!enableUpgradeV2) || (!applicationDAO.hasApplicationsWithModelVersion(baseVersion)))
            return;

        List<Application> applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        while (applications.size() > 0) {
            for (Application application : applications) {
                Application queryApplication = new Application(application.getOid(), application.getVersion());
                try {
                    LOGGER.info("Start upgrading model version for application: " + application.getOid());
                    if (null == application.getAuthorizationMeta()) {
                        application = applicationService.updateAuthorizationMeta(application);
                    }
                    if (null == application.getPreferenceEligibilities() || 0 == application.getPreferenceEligibilities().size() ||
                        null == application.getPreferencesChecked() || 0 == application.getPreferencesChecked().size()){
                        application = applicationService.updatePreferenceBasedData(application);
                    }
                    application.setModelVersion(targetVersion);
                    LOGGER.info("Done upgrading model version for application: " + application.getOid());
                } catch (IOException e) {
                    application.setModelVersion(-1 * targetVersion);
                    LOGGER.error("Upgrading model to "+ targetVersion+" failed for application: " + application.getOid() + " " + e.getMessage());
                } catch (RuntimeException e) {
                    application.setModelVersion(-1 * targetVersion);
                    LOGGER.error("Upgrading model to "+ targetVersion+" failed for application: " + application.getOid() + " " + e.getMessage());
                } finally {
                    applicationDAO.update(queryApplication, application);
                }
            }
            applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        }
    }

    private void upgradeModelVersion2to3() {
        final Integer baseVersion = 2;
        final Integer targetVersion = 3;
        if ((!enableUpgradeV3) || (!applicationDAO.hasApplicationsWithModelVersion(baseVersion)))
            return;

        List<String> pk = new ArrayList<String>() {{
            add(OppijaConstants.PERUSKOULU);
            add(OppijaConstants.OSITTAIN_YKSILOLLISTETTY);
            add(OppijaConstants.ALUEITTAIN_YKSILOLLISTETTY);
            add(OppijaConstants.YKSILOLLISTETTY);
        }};

        List<Application> applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        while (applications.size() > 0) {
            for (Application application : applications) {
                Application updateQuery = new Application(application.getOid(), application.getVersion());
                Map<String, String> pohjakoulutus = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);

                if (pk.contains(pohjakoulutus.get(OppijaConstants.ELEMENT_ID_BASE_EDUCATION))) {
                    Map<String, String> osaaminen = application.getPhaseAnswers(OppijaConstants.PHASE_GRADES);
                    Map<String, String> toAdd = new HashMap<String, String>();
                    for (Map.Entry<String, String> entry : osaaminen.entrySet()) {
                        String key = entry.getKey();
                        String prefix = key.substring(0, 5);
                        String val3Key = prefix + "_VAL3";
                        if (!osaaminen.containsKey(val3Key)) {
                            toAdd.put(val3Key, "Ei arvosanaa");
                        }
                    }
                    if (toAdd.size() > 0) {
                        Application original = application.clone();
                        toAdd.putAll(osaaminen);
                        application.addVaiheenVastaukset(OppijaConstants.PHASE_GRADES, toAdd);
                        application.setModelVersion(targetVersion);
                        addHistoryBasedOnChangedAnswers(application, original, SYSTEM_USER, "model upgrade 2-3" );
                        applicationDAO.update(updateQuery, application);
                        loggerAspect.logUpdateApplication(original,
                          new ApplicationPhase(original.getApplicationSystemId(),
                            OppijaConstants.PHASE_GRADES, application.getPhaseAnswers(OppijaConstants.PHASE_GRADES)));
                    } else {
                        applicationDAO.updateModelVersion(updateQuery, targetVersion);
                    }
                }
            }
            applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        }
    }

    private void upgradeModelVersion3to4() {
        final Integer baseVersion =3;
        final Integer targetVersion =4;
        if ((!enableUpgradeV4) || (!applicationDAO.hasApplicationsWithModelVersion(baseVersion)))
            return;

        List<Application> applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        while(applications.size() > 0) {
            for (Application application : applications) {
                Application queryApplication = new Application(application.getOid(), application.getVersion());
                if (Level4.requiresPatch(application)) {
                    applicationDAO.update(queryApplication, Level4.fixAmmatillisenKoulutuksenKeskiarvo(application, loggerAspect));
                } else {
                    applicationDAO.updateModelVersion(queryApplication, targetVersion);
                }
            }
            applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        }
    }

    private void reprocessOneApplication(Application application, final boolean sendMail){
        try {
            PostProcessingState redo = application.getRedoPostProcess();
            if (redo != null) {
                if (PostProcessingState.FULL.equals(redo) || PostProcessingState.NOMAIL.equals(redo)) {
                    application = applicationService.addPersonOid(application);
                    if (!skipSendingSchoolManual) {
                        application = baseEducationService.addSendingSchool(application);
                        application = baseEducationService.addBaseEducation(application);
                    }
                    application = applicationService.updateAuthorizationMeta(application);
                    application = applicationService.ensureApplicationOptionGroupData(application);
                    application = validateApplication(application);
                    application.setRedoPostProcess(PostProcessingState.DONE);
                    application.setModelVersion(Application.CURRENT_MODEL_VERSION);
                    //TODO =RS= add Version
                    this.applicationDAO.update(new Application(application.getOid()), application);
                }
                if (sendMail && PostProcessingState.FULL.equals(redo)) {
                    try {
                        sendMail(application);
                    } catch (EmailException e) {
                        LOGGER.error("Send mail failed in redo for application:" + application.getOid(), e);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("redoPostProcess failed for application " + application.getOid(), e);
            setProcessingStateToFailed(application.getOid(), e.getMessage());
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

    private void sendMail(Application application) throws EmailException {
        Map<String, String> answers = application.getVastauksetMerged();
        String email = answers.get(OppijaConstants.ELEMENT_ID_EMAIL);
        if (!isEmpty(email)) {
            sendConfirmationMail(application);
        }
    }

    private void sendConfirmationMail(Application application) throws EmailException {
        Map<String, String> answers = application.getVastauksetMerged();
        String emailAddress = answers.get(OppijaConstants.ELEMENT_ID_EMAIL);
        String lang = answers.get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE);

        Template tmpl = templateMap.get(lang);
        String asOid = application.getApplicationSystemId();
        ApplicationSystem as = applicationSystemService.getApplicationSystem(asOid);
        if (as.getKohdejoukkoUri().equals(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU)) {
            tmpl = templateMapHigherEducation.get(lang);
        }

        Locale locale = new Locale("fi");
        if ("ruotsi".equals(lang)) {
            locale = new Locale("sv");
        } else if ("englanti".equals(lang)) {
            locale = new Locale("en");
        }
        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);

        String subject = messages.getString("email.application.received.title");
        Email email = basicEmail(emailAddress, subject);
        email.setDebug(smtpDebug);
        StringWriter sw = new StringWriter();
        VelocityContext ctx = buildContext(application, as);
        tmpl.merge(ctx, sw);
        email.setMsg(sw.toString());
        email.send();

    }

    private VelocityContext buildContext(Application application, ApplicationSystem applicationSystem) {
        VelocityContext ctx = new VelocityContext();
        DateFormat dateFmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String applicationDate = dateFmt.format(application.getReceived());
        String applicationId = application.getOid();
        applicationId = applicationId.substring(applicationId.lastIndexOf('.') + 1);

        ctx.put("applicationSystemId", getFormName(application));
        ctx.put("applicant", getApplicantName(application));
        ctx.put("applicationId", applicationId);
        ctx.put("applicationDate", applicationDate);
        ctx.put("preferences", getPreferences(application, applicationSystem));
        ctx.put("athlete", isAthlete(application));
        ctx.put("discretionary", isDiscretionary(application));
        ctx.put("musiikkiTanssiLiikuntaEducationCode", isMusiikkiTanssiLiikuntaEducationCode(application));

        return ctx;
    }

    private boolean isDiscretionary(final Application application) {
        return !ApplicationUtil.getDiscretionaryAttachmentAOIds(application).isEmpty();
    }

    private Object getPreferences(Application application, ApplicationSystem applicationSystem) {
        Map<String, String> answers = application.getVastauksetMerged();
        int maxPrefs = applicationSystem.getMaxApplicationOptions();
        List<String> preferences = new ArrayList<String>(maxPrefs);
        for (int i = 1; i <= maxPrefs; i++) {
            String koulutus = answers.get(String.format(OppijaConstants.PREFERENCE_NAME, i));
            String koulu = answers.get(String.format(OppijaConstants.PREFERENCE_ORGANIZATION, i));
            if (isEmpty(koulutus) && isEmpty(koulu)) {
                break;
            }
            koulutus = isEmpty(koulutus) ? "" : koulutus;
            koulu = isEmpty(koulu) ? "" : koulu;
            preferences.add(i + ". " + koulu + "\n   " + koulutus);
        }
        return preferences;
    }

    private boolean isMusiikkiTanssiLiikuntaEducationCode(Application application) {
        Map<String, String> answers = application.getVastauksetMerged();
        return (MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 1))) ||
                MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 2))) ||
                MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 3))) ||
                MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 4))) ||
                MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 5))));
    }

    private boolean isAthlete(Application application) {
        Map<String, String> answers = application.getVastauksetMerged();
        return (TRUE.equals(answers.get("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference1_urheilijalinjan_lisakysymys")) ||
                TRUE.equals(answers.get("preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference2_urheilijalinjan_lisakysymys")) ||
                TRUE.equals(answers.get("preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference3_urheilijalinjan_lisakysymys")) ||
                TRUE.equals(answers.get("preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference4_urheilijalinjan_lisakysymys")) ||
                TRUE.equals(answers.get("preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference5_urheilijalinjan_lisakysymys")));
    }

    private String getApplicantName(Application application) {
        String firstName = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_FIRST_NAMES);
        String lastName = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_LAST_NAME);
        return firstName + " " + lastName;
    }

    private String getFormName(Application application) {
        Form form = formService.getForm(application.getApplicationSystemId());
        Map<String, String> translations = form.getI18nText().getTranslations();
        String lang = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE);
        String realLang = "fi";
        if (lang.equals("ruotsi")) {
            realLang = "sv";
        } else if (lang.equals("englanti")) {
            realLang = "en";
        }
        String formName = translations.get(realLang);
        if (isEmpty(formName)) {
            formName = translations.get("fi");
        }
        return formName;
    }

    private Email basicEmail(String toAddress, String subject) throws EmailException {
        Email email = new SimpleEmail();
        email.setHostName(smtpHost);
        email.setSmtpPort(smtpPort);
        //email.setAuthenticator(new DefaultAuthenticator("username", "password"));
        //email.setSSLOnConnect(true);
        email.setFrom(replyTo);
        email.setSubject(subject);
        email.addTo(toAddress);
        email.setCharset("utf-8");
        return email;
    }

    private Application getNextApplicationFor(final ProcessingType processingType){
        switch (processingType){
            case IDENTIFICATION:
                return applicationDAO.getNextWithoutStudentOid();
            case POST_PROCESS:
                return applicationDAO.getNextSubmittedApplication();
            case REDO_POST_PROCESS:
                return  applicationDAO.getNextRedo();
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
