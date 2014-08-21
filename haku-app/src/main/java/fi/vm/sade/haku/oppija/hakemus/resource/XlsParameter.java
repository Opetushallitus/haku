package fi.vm.sade.haku.oppija.hakemus.resource;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;

import java.util.List;
import java.util.Map;

public class XlsParameter {
    private final ApplicationSystem applicationSystem;
    private final List<Map<String, Object>> applications;
    private final Map<String, Question> elementsByType;

    public XlsParameter(ApplicationSystem applicationSystem, List<Map<String, Object>> applications, Map<String,Question> elementsByType) {
        this.applicationSystem = applicationSystem;
        this.applications = applications;
        this.elementsByType = elementsByType;
    }

    public ApplicationSystem getApplicationSystem() {
        return applicationSystem;
    }

    public List<Map<String, Object>> getApplications() {
        return applications;
    }

    public Map<String, Question> getElementsByType() {
        return elementsByType;
    }

    public String getHakukohteenNimi() {
        return "%hakukohteen nimi%";
    }
}
