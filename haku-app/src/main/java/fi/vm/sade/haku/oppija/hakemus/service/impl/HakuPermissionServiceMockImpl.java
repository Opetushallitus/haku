package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile(value = {"dev", "it", "devluokka"})
public class HakuPermissionServiceMockImpl implements HakuPermissionService {
    @Override
    public List<String> userCanReadApplications(List<String> organizations) {
        return organizations;
    }

    @Override
    public List<String> userHasOpoRole(List<String> organizations) {
        return organizations;
    }

    @Override
    public boolean userCanReadApplication(Application application) {
        return true;
    }

    @Override
    public Map<String, Boolean> userHasEditRoleToPhases(Application application, Form form) {
        Map<String, Boolean> phaseEditAllowed = new HashMap<String, Boolean>();
        for (Element element : form.getChildren()) {
            phaseEditAllowed.put(element.getId(), true);
        }
        return phaseEditAllowed;
    }

    @Override
    public boolean userCanDeleteApplication(Application application) {
        return true;
    }

    @Override
    public boolean userCanPostProcess(Application application) {
        return true;
    }

    @Override
    public boolean userCanEnterApplication() {
        return true;
    }

    @Override
    public boolean userCanSearchBySendingSchool() {
        return true;
    }

    @Override
    public boolean userCanEditApplicationAdditionalData(Application application) {
        return true;
    }
}
