package fi.vm.sade.oppija.lomake.service.impl;

import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.exception.ApplicationSystemNotFound;
import fi.vm.sade.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.oppija.repository.ApplicationSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApplicationSystemServiceImpl implements ApplicationSystemService {

    final Map<String, ApplicationSystem> applicationSystems = new ConcurrentHashMap<String, ApplicationSystem>();

    final ApplicationSystemRepository applicationSystemRepository;

    final boolean cacheApplicationSystems;

    @Autowired
    public ApplicationSystemServiceImpl(final ApplicationSystemRepository applicationSystemRepository, @Value("${application.system.cache:true}") boolean cacheApplicationSystems) {
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
}
