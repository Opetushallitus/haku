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
package fi.vm.sade.oppija.yksilointi.impl;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.oppija.yksilointi.YksilointiWorker;
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

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class YksilointiWorkerImpl implements YksilointiWorker {

    public static final Logger LOGGER = LoggerFactory.getLogger(YksilointiWorkerImpl.class);
    private ApplicationService applicationService;
    private FormService formService;

    private static DateFormat dateFmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private Map<String, Template> templateMap;

    @Value("${email.smtp.debug:false}")
    private boolean smtpDebug;

    @Value("${email.smtp.host}")
    private String smtpHost;
    @Value("${email.smtp.port}")
    private Integer smtpPort;
    @Value("${email.replyTo}")
    private String replyTo;

    @Autowired
    public YksilointiWorkerImpl(ApplicationService applicationService, FormService formService) {
        this.applicationService = applicationService;
        this.formService = formService;

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.setProperty("class.resource.loader.path", "email");
        velocityEngine.setProperty("class.resource.loader.cache", "true");
        velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        velocityEngine.init();

        templateMap = new HashMap<String, Template>();
        templateMap.put("suomi", velocityEngine.getTemplate("email/application_received_fi.vm"));
        templateMap.put("ruotsi", velocityEngine.getTemplate("email/application_received_sv.vm"));
    }

    /**
     * Post-process applications.
     *
     * @param limit
     * @param sendMail
     */
    public void processApplications(int limit, boolean sendMail) {
        Application application = applicationService.getNextWithoutPersonOid();

        long endTime = System.currentTimeMillis() + (limit - 500);
        while (application != null && endTime > System.currentTimeMillis()) {
            applicationService.addPersonAndAuthenticate(application);
            if (sendMail) {
                try {
                    sendMail(application);
                } catch (EmailException e) {
                    LOGGER.info("Error process applications", e);
                }
            }
            application = applicationService.getNextWithoutPersonOid();
        }
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

        Locale locale = new Locale("ruotsi".equals(lang) ? "sv" : "fi");
        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);

        String subject = messages.getString("email.application.received.title");
        Email email = basicEmail(emailAddress, subject);
        email.setDebug(smtpDebug);
        StringWriter sw = new StringWriter();
        VelocityContext ctx = buildContext(application);
        tmpl.merge(ctx, sw);
        email.setMsg(sw.toString());
        email.send();

    }

    private VelocityContext buildContext(Application application) {
        VelocityContext ctx = new VelocityContext();

        String applicationDate = dateFmt.format(application.getReceived());
        String applicationId = application.getOid();
        applicationId = applicationId.substring(applicationId.lastIndexOf('.') + 1);

        ctx.put("applicationPeriodId", getFormName(application));
        ctx.put("applicant", getApplicantName(application));
        ctx.put("applicationId", applicationId);
        ctx.put("applicationDate", applicationDate);
        ctx.put("preferences", getPreferences(application));

        return ctx;
    }

    private Object getPreferences(Application application) {
        Map<String, String> answers = application.getVastauksetMerged();
        List<String> preferences = new ArrayList<String>(5);
        for (int i = 1; i <= 5; i++) {
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

    private String getApplicantName(Application application) {
        String firstName = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_FIRST_NAMES);
        String lastName = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_LAST_NAME);
        return firstName + " " + lastName;
    }

    private String getFormName(Application application) {
        Form form = formService.getForm(application.getApplicationPeriodId());
        Map<String, String> translations = form.getI18nText().getTranslations();
        String lang = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE);
        String realLang = "suomi".equals(lang) ? "fi" : "sv";
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
        return email;
    }
}
