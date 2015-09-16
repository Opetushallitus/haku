package fi.vm.sade.haku.oppija.lomake.service.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.service.Session;

import java.util.Map;

public class SystemSession implements Session {
    final User user = new User("järjestelmä");

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void addPrefillData(String applicationSystemId, Map<String, String> data) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public Map<String, String> populateWithPrefillData(Map<String, String> data) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public Application getApplication(String applicationSystemId) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public boolean hasApplication(String applicationSystemId) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public Application savePhaseAnswers(ApplicationPhase applicationPhase) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void removeApplication(Application application) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public Application getSubmittedApplication() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public Map<String, I18nText> getNotes() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void addNote(String key, I18nText note) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void clearNotes() {
        throw new RuntimeException("Not supported");
    }
}
