package fi.vm.sade.haku.oppija.hakemus.resource;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;

import java.util.List;
import java.util.Map;

public class XlsParameter {
    private final ApplicationSystem applicationSystem;
    private final List<Map<String, Object>> applications;
    private final Map<String, Question> questions;

    public XlsParameter(ApplicationSystem applicationSystem, List<Map<String, Object>> applications, Map<String,Question> questions) {
        this.applicationSystem = applicationSystem;
        this.applications = applications;
        this.questions = questions;
    }

    public ApplicationSystem getApplicationSystem() {
        return applicationSystem;
    }

    public List<Map<String, Object>> getApplications() {
        return applications;
    }

    public Map<String, Question> getQuestions() {
        return questions;
    }

    public String getHakukohteenNimi() {
        return "hakukohteen nimi";
    }
}
