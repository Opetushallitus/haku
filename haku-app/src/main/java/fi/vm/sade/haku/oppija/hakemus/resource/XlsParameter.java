package fi.vm.sade.haku.oppija.hakemus.resource;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;

import java.util.List;
import java.util.Map;

public class XlsParameter {
    private final String asid;
    private final String aoid;
    private final ApplicationSystem applicationSystem;
    private final List<Map<String, Object>> applications;
    private final List<Element> questions;

    public XlsParameter(final String asid,
                        final String aoid,
                        final ApplicationSystem applicationSystem,
                        final List<Map<String, Object>> applications,
                        final List<Element> questions) {
        this.asid = asid;
        this.aoid = aoid;
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

    public List<Element> getQuestions() {
        return questions;
    }

    public String getAsid() {
        return asid;
    }

    public String getAoid() {
        return aoid;
    }
}
