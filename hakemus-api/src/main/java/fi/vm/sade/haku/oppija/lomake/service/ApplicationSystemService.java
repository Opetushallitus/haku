package fi.vm.sade.haku.oppija.lomake.service;

import com.google.common.cache.LoadingCache;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;

import java.util.List;

public interface ApplicationSystemService {
    ApplicationSystem getApplicationSystem(final String Id);

    ApplicationSystem getApplicationSystem(String id, String... includeFields);

    ApplicationSystem getActiveApplicationSystem(final String Id);

    void save(final ApplicationSystem applicationSystem);

    List<ApplicationSystem> getPublishedApplicationSystems(String... includeFields);

    List<ApplicationSystem> getAllApplicationSystems(final String... includeFields);

    List<String> findByYearAndSemester(String asSemester, String asYear);

    LoadingCache<String, ApplicationSystem> getCache();
}
