package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile(value = {"dev", "it"})
public class HakuPermissionServiceMockImpl implements HakuPermissionService {
    @Override
    public List<String> userCanReadApplications(List<String> organizations) {
        return organizations;
    }

    @Override
    public boolean userCanReadApplication(Application application) {
        return true;
    }

    @Override
    public boolean userCanUpdateApplication(Application application) {
        if (application.getPersonOid() != null && application.getPersonOid().equals("1.2.246.562.24.00000000001")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean userCanDeleteApplication(Application application) {
        return true;
    }

    @Override
    public boolean userCanPostProcess(Application application) {
        return true;
    }
}
