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
import fi.vm.sade.oppija.util.OppijaConstants;
import fi.vm.sade.oppija.yksilointi.YksilointiWorker;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class YksilointiWorkerImpl implements YksilointiWorker {

    private ApplicationService applicationService;
    private FormService formService;

    private VelocityEngine velocityEngine;
    private Map<String, Template> templateMap;

    @Autowired
    public YksilointiWorkerImpl(ApplicationService applicationService, FormService formService) {
        this.applicationService = applicationService;
        this.formService = formService;

        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.setProperty("class.resource.loader.path", "email");
        velocityEngine.setProperty("class.resource.loader.cache", "true");
        velocityEngine.init();

        templateMap = new HashMap<String, Template>();
        templateMap.put("suomi", velocityEngine.getTemplate("email/application_received_fi.vm"));
        templateMap.put("ruotsi", velocityEngine.getTemplate("email/application_received_fi.vm"));
    }

    public void processApplications(int limit, boolean sendMail) {
        Application application = applicationService.getNextWithoutPersonOid();

        while (application != null && limit-- > 0) {
            applicationService.setPerson(application);
            if (sendMail) {
                try {
                    sendMail(application);
                } catch (EmailException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            application = applicationService.getNextWithoutPersonOid();
        }
    }

    private void sendMail(Application application) throws EmailException {
        Map<String, String> answers = application.getVastauksetMerged();
        String email = answers.get(OppijaConstants.ELEMENT_ID_EMAIL);
        if (!isEmpty(email)) {
            if (application.isActive()) {
                sendConfirmationMail();
            } else {
                sendTentativeMail(application);
            }
        }
    }

    private void sendConfirmationMail() throws EmailException {
        // NOP for now
    }

    private void sendTentativeMail(Application application) throws EmailException {
        Map<String, String> answers = application.getVastauksetMerged();
        String emailAddress = answers.get(OppijaConstants.ELEMENT_ID_EMAIL);
        String lang = answers.get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE);
        Template tmpl = templateMap.get(lang);

        Email email = basicEmail(emailAddress, "Hakulomakkeesi on vastaanotettu");
        StringWriter sw = new StringWriter();
        Context ctx = new VelocityContext();
        ctx.put("formId", getFormName(application));
        ctx.put("applicant", getApplicantName(application));
        ctx.put("applicationId", application.getOid());
        ctx.put("applicationDate", "NOT IMPLEMENTED");
        ctx.put("preferences", getPreferences(application));
        tmpl.merge(ctx, sw);
        email.setMsg(sw.toString());
        email.send();
    }

    private Object getPreferences(Application application) {
        Map<String, String> answers = application.getVastauksetMerged();
        List<String> preferences = new ArrayList<String>(5);
        for (int i = 1; i <= 5; i++) {
            String koulutus = answers.get(String.format(OppijaConstants.PREFERENCE_NAME, i));
            String koulu = answers.get(String.format(OppijaConstants.PREFERENCE_ORGANIZATION, i));

            if (!isEmpty(koulu) && !isEmpty(koulutus)) {
                preferences.add(koulutus + ", " + koulu);
            }
        }
        return preferences;
    }

    private String getApplicantName(Application application) {
        String firstName = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_FIRST_NAMES);
        String lastName = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_LAST_NAME);
        return firstName + " " + lastName;
    }

    private String getFormName(Application application) {
        Form form = formService.getForm(application.getFormId());
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
        email.setHostName("oph-mailtester.hard.ware.fi");
        email.setSmtpPort(25);
        //email.setAuthenticator(new DefaultAuthenticator("username", "password"));
        //email.setSSLOnConnect(true);
        email.setFrom("noreply@opintopolku.fi");
        email.setSubject(subject);
        email.addTo(toAddress);
        return email;
    }
}
