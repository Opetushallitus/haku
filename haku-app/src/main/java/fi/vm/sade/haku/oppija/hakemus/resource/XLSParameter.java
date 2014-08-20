package fi.vm.sade.haku.oppija.hakemus.resource;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;

import java.util.List;
import java.util.Map;

public class XLSParameter {
    private final ApplicationSystem activeApplicationSystem;
    private final List<Application> applications;
    private final Map<String, Titled> elementsByType;

    public XLSParameter(ApplicationSystem activeApplicationSystem, List<Application> applications, Map<String, Titled> elementsByType) {
        this.activeApplicationSystem = activeApplicationSystem;
        this.applications = applications;
        this.elementsByType = elementsByType;
    }

    public ApplicationSystem getActiveApplicationSystem() {
        return activeApplicationSystem;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public Map<String, Titled> getElementsByType() {
        return elementsByType;
    }
}
