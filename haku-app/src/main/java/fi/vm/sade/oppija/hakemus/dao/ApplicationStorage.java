package fi.vm.sade.oppija.hakemus.dao;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.lomake.domain.FormId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("ApplicationStorage")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ApplicationStorage {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStorage.class);

    final Map<FormId, Application> applications = new HashMap<FormId, Application>();

    //TODO mieti miten estetään session kasvu ja synkronointi
    public Application getApplication(final FormId formId) {
        LOGGER.info("-----------------getApplication " + formId);
        Application application;
        if (applications.containsKey(formId)) {
            application = applications.get(formId);
        } else {
            application = new Application();
            application.setFormId(formId);
        }
        return application;

    }

    public Application savePhaseAnswers(ApplicationPhase applicationPhase) {
        LOGGER.info("---------------savePhaseAnswers " + applicationPhase);
        Application application = this.getApplication(applicationPhase.getFormId());
        application.addVaiheenVastaukset(applicationPhase.getPhaseId(), applicationPhase.getAnswers());
        return application;
    }
}
