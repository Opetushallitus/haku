package fi.vm.sade.haku.oppija.lomake.service;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;

import java.util.List;

public interface ApplicationSystemService {
    ApplicationSystem getApplicationSystem(final String Id);

    void save(final ApplicationSystem applicationSystem);

    List<ApplicationSystem> getAllApplicationSystems(final String... includeFields);

    List<String> findByYearAndSemester(String asSemester, String asYear);

    ApplicationSystem getDefaultApplicationSystem(List<ApplicationSystem> systems);
}
