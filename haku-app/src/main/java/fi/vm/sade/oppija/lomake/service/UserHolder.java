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
package fi.vm.sade.oppija.lomake.service;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.lomake.domain.AnonymousUser;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 10/12/122:46 PM}
 * @since 1.1
 */
@Component("userHolder")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserHolder implements Serializable {

    private static final long serialVersionUID = 8093993846121110534L;
    public static final Logger LOGGER = LoggerFactory.getLogger(UserHolder.class);

    private final Map<FormId, Application> applications = new HashMap<FormId, Application>();
    private User user = new AnonymousUser();
    private final Map<String, String> userPrefillData = new HashMap<String, String>();

    public User getUser() {
        return user;
    }

    public void login(User user) {
        if (user.isKnown()) {
            userPrefillData.put("Sähköposti", "esitaytetty_email@autofill.com"); // TODO remove
        }
        this.user = user;
    }

    public void addPrefillData(final Map<String, String> data) {
        this.userPrefillData.putAll(data);
    }

    public Map<String, String> populateWithPrefillData(final Map<String, String> data) {
        Map<String, String> populated = new HashMap<String, String>(userPrefillData);
        populated.putAll(data);
        return populated;
    }

    public Application getApplication(final FormId formId) {
        if (applications.containsKey(formId)) {
            return applications.get(formId);
        } else {
            Application application = new Application(formId, user);
            this.applications.put(formId, application);
            return application;
        }

    }

    public Application savePhaseAnswers(ApplicationPhase applicationPhase) {
        Application application = this.getApplication(applicationPhase.getFormId());
        application.addVaiheenVastaukset(applicationPhase.getPhaseId(), applicationPhase.getAnswers());
        return application;
    }

    public void removeApplication(final FormId formId) {
        this.applications.remove(formId);
    }
}
