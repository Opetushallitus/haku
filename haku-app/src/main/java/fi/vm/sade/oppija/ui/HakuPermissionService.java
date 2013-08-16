package fi.vm.sade.oppija.ui;

import fi.vm.sade.oppija.hakemus.domain.Application;

import java.util.List;

public interface HakuPermissionService {

    public List<String> userCanReadApplications(List<String> organizations);

    boolean userCanReadApplication(Application application);
}
