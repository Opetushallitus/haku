package fi.vm.sade.oppija.lomake.service;

import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;

import java.util.List;

public interface ApplicationSystemService {
    ApplicationSystem getApplicationSystem(final String Id);

    void save(final ApplicationSystem applicationSystem);

    List<ApplicationSystem> getAllApplicationSystems(final String... includeFields);
}
