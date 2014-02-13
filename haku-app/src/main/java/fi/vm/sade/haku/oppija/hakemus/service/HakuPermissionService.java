package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;

import java.util.List;

public interface HakuPermissionService {

    List<String> userCanReadApplications(List<String> organizations);

    boolean userCanReadApplication(Application application);

    boolean userCanUpdateApplication(Application application);

    boolean userCanDeleteApplication(Application application);

    boolean userCanPostProcess(Application application);

    boolean userCanEnterApplication();
}
