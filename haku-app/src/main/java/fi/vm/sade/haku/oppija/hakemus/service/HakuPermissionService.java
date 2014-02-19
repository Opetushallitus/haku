package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;

import java.util.List;
import java.util.Map;

public interface HakuPermissionService {

    List<String> userCanReadApplications(List<String> organizations);

    List<String> userHasOpoRole(List<String> organizations);

    boolean userCanReadApplication(Application application);

    Map<String, Boolean> userHasEditRoleToPhases(Application application, Form form);

    boolean userCanDeleteApplication(Application application);

    boolean userCanPostProcess(Application application);

    boolean userCanEnterApplication();

    boolean userCanSearchBySendingSchool();
}
