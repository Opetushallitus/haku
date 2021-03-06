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
package fi.vm.sade.haku.oppija.lomake.service.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.service.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession implements Serializable, Session {
    private static final int MAX_PREFILL_PARAMETERS = 100;
    private static final long serialVersionUID = 8093993846121110534L;

    private final Map<String, Application> applications = new ConcurrentHashMap<String, Application>();
    private final Map<String, String> userPrefillData = new ConcurrentHashMap<String, String>();
    private Application submittedApplication = null;

    private Map<String, I18nText> notes = new ConcurrentHashMap<String, I18nText>();

    @Override
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new User(authentication.getName());
    }

    @Override
    public void addPrefillData(final String applicationSystemId, final Map<String, String> data) {
        if (data.size() > MAX_PREFILL_PARAMETERS) {
            throw new IllegalArgumentException("Too many prefill data values");
        }
        this.applications.remove(applicationSystemId);
        this.userPrefillData.clear();
        this.userPrefillData.putAll(data);
    }

    @Override
    public Map<String, String> populateWithPrefillData(final Map<String, String> data) {
        Map<String, String> populated = new HashMap<String, String>(userPrefillData);
        populated.putAll(data);
        return populated;
    }

    @Override
    public Application getApplication(final String applicationSystemId) {
        if (applications.containsKey(applicationSystemId)) {
            return applications.get(applicationSystemId);
        } else {
            Application application = new Application(applicationSystemId, getUser());
            this.applications.put(applicationSystemId, application);
            this.submittedApplication = null;
            return application;
        }

    }

    @Override
    public boolean hasApplication(final String applicationSystemId) {
        if (applications.containsKey(applicationSystemId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Application savePhaseAnswers(ApplicationPhase applicationPhase) {
        Application application = this.getApplication(applicationPhase.getApplicationSystemId());
        application.setVaiheenVastauksetAndSetPhaseId(applicationPhase.getPhaseId(), applicationPhase.getAnswers());
        return application;
    }

    @Override
    public void removeApplication(final Application application) {
        if (null != this.applications.remove(application.getApplicationSystemId())) {
            this.submittedApplication = application;
        }
    }

    @Override
    public Application getSubmittedApplication() {
        return submittedApplication;
    }

    @Override
    public Map<String, I18nText> getNotes() {
        return this.notes;
    }

    @Override
    public void addNote(String key, I18nText note) {
        this.notes.put(key, note);
    }

    @Override
    public void clearNotes() {
        this.notes = new ConcurrentHashMap<String, I18nText>();
    }
}
