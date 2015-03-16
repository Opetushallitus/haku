package fi.vm.sade.haku.oppija.lomake.service;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.lomake.domain.User;

import java.util.Map;

public interface Session {
    User getUser();

    void addPrefillData(String applicationSystemId, Map<String, String> data);

    Map<String, String> populateWithPrefillData(Map<String, String> data);

    Application getApplication(String applicationSystemId);

    boolean hasApplication(String applicationSystemId);

    Application savePhaseAnswers(ApplicationPhase applicationPhase);

    void removeApplication(Application application);

    Application getSubmittedApplication();
}
