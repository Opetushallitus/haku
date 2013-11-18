package fi.vm.sade.haku.oppija.lomake.service.impl;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.ApplicationSystemNotFound;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.repository.ApplicationSystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApplicationSystemServiceImpl implements ApplicationSystemService {

    private final static Logger log = LoggerFactory.getLogger(ApplicationSystemServiceImpl.class);
    final Map<String, ApplicationSystem> applicationSystems = new ConcurrentHashMap<String, ApplicationSystem>();

    final ApplicationSystemRepository applicationSystemRepository;

    final boolean cacheApplicationSystems;

    @Autowired
    public ApplicationSystemServiceImpl(final ApplicationSystemRepository applicationSystemRepository,
                                        @Value("${application.system.cache:true}") final boolean cacheApplicationSystems) {
        this.applicationSystemRepository = applicationSystemRepository;
        this.cacheApplicationSystems = cacheApplicationSystems;
    }

    @Override
    public ApplicationSystem getApplicationSystem(final String id) {
        if (applicationSystems.containsKey(id)) {
            return applicationSystems.get(id);
        }
        ApplicationSystem applicationSystem = applicationSystemRepository.findById(id);
        if (applicationSystem != null) {
            if (this.cacheApplicationSystems) {
                this.applicationSystems.put(applicationSystem.getId(), applicationSystem);
            }
            return applicationSystem;
        }
        throw new ApplicationSystemNotFound(id);
    }

    @Override
    public void save(final ApplicationSystem applicationSystem) {
        this.applicationSystemRepository.save(applicationSystem);
        this.applicationSystems.put(applicationSystem.getId(), applicationSystem);
    }

    @Override
    public List<ApplicationSystem> getAllApplicationSystems(String... includeFields) {
        return this.applicationSystemRepository.findAll(includeFields);
    }

    @Override
    public List<String> findByYearAndSemester(String asSemester, String asYear) {
        List<ApplicationSystem> ass = this.applicationSystemRepository.findBySemesterAndYear(asSemester, asYear);
        List<String> asIds = new ArrayList<String>(ass.size() + 1);
        for (ApplicationSystem as : ass) {
            asIds.add(as.getId());
        }
        asIds.add(null);
        return asIds;
    }
}
