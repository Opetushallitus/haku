package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile(value = {"dev", "it", "devluokka"})
public class HakuPermissionServiceMockImpl implements HakuPermissionService {
    @Override
    public List<String> userCanReadApplications() {
        AuthenticationServiceMockImpl authenticationService = new AuthenticationServiceMockImpl();
        return userCanReadApplications(authenticationService.getOrganisaatioHenkilo());
    }

    @Override
    public List<String> userCanReadApplications(List<String> organizations) {
        return organizations;
    }

    @Override
    public List<String> userHasOpoRole() {
        AuthenticationServiceMockImpl authenticationService = new AuthenticationServiceMockImpl();
        return userHasOpoRole(authenticationService.getOrganisaatioHenkilo());
    }

    @Override
    public List<String> userHasHetuttomienKasittelyRole() {
        AuthenticationServiceMockImpl authenticationService = new AuthenticationServiceMockImpl();
        return userHasHetuttomienKasittelyRole(authenticationService.getOrganisaatioHenkilo());
    }

    @Override
    public List<String> userHasHetuttomienKasittelyRole(List<String> organizations) {
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
        Map<String, String> meta = application.getMeta();
        for (Element element : form.getChildren()) {
            String phaseId = element.getId();
            boolean locked = Boolean.valueOf(meta.get(phaseId + "_locked"));
            phaseEditAllowed.put(element.getId(), !locked);
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
    public List<String> userCanEnterApplications() {
        return userCanReadApplications();
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
