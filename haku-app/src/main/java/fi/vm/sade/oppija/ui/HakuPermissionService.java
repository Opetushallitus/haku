package fi.vm.sade.oppija.ui;

import fi.vm.sade.oppija.hakemus.domain.Application;

import java.util.List;

public interface HakuPermissionService {

    List<String> userCanReadApplications(List<String> organizations);

    boolean userCanReadApplication(Application application);

    boolean userCanUpdateApplication(Application application);

    boolean userCanDeleteApplication(Application application);

}
