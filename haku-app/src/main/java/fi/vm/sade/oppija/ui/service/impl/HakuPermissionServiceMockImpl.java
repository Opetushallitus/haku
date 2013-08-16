package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.ui.HakuPermissionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("dev")
public class HakuPermissionServiceMockImpl implements HakuPermissionService {
    @Override
    public List<String> userCanReadApplications(List<String> organizations) {
        return organizations;
    }

    @Override
    public boolean userCanReadApplication(Application application) {
        return true;
    }
}
