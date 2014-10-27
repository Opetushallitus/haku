package fi.vm.sade.haku.oppija.lomake.service.impl;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.ApplicationSystemNotFound;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.repository.ApplicationSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class ApplicationSystemServiceImpl implements ApplicationSystemService {
    private final ApplicationSystemRepository applicationSystemRepository;
    private final LoadingCache<String, ApplicationSystem> cache;
    private final Boolean cacheApplicationSystems;
    private final Integer cacheRefreshTimer;

    @Autowired
    public ApplicationSystemServiceImpl(final ApplicationSystemRepository applicationSystemRepository,
                                        @Value("${application.system.cache:true}") final boolean cacheApplicationSystems,
                                        @Value("${application.system.cache.refresh:6}") final Integer cacheRefreshTimer) {
        this.applicationSystemRepository = applicationSystemRepository;
        this.cacheApplicationSystems = cacheApplicationSystems;
        this.cacheRefreshTimer = cacheRefreshTimer;
        this.cache = CacheBuilder.newBuilder()
                .maximumWeight(10)
                .refreshAfterWrite(cacheRefreshTimer, TimeUnit.MINUTES)
                .weigher(new Weigher<String, ApplicationSystem>() {
                    @Override
                    public int weigh(String key, ApplicationSystem value) {
                        if (value.isActive()) {
                            return 1;
                        } else {
                            return 2;
                        }
                    }
                }).build(new CacheLoader<String, ApplicationSystem>() {
                    @Override
                    public ApplicationSystem load(String key) throws Exception {
                        return findById(key);
                    }

                    @Override
                    public ListenableFuture<ApplicationSystem> reload(final String key, final ApplicationSystem oldValue) throws Exception {
                        return ListenableFutureTask.create(new Callable<ApplicationSystem>() {
                            @Override
                            public ApplicationSystem call() throws Exception {
                                Date dbLastGenerated = getLastGeneratedForId(key);
                                if (null != oldValue.getLastGenerated() && null != dbLastGenerated && oldValue.getLastGenerated().equals(dbLastGenerated))
                                    return oldValue;
                                else
                                    return findById(key);
                            }
                        });
                    }
                });
    }

    private Date getLastGeneratedForId(String key) {
       final ApplicationSystem applicationSystem = applicationSystemRepository.findById(key, "lastGenerated");
        if (applicationSystem != null) {
            return applicationSystem.getLastGenerated();
        } else {
            throw new ApplicationSystemNotFound(key);
        }
    }

    private ApplicationSystem findById(String key) {
        final ApplicationSystem applicationSystem = applicationSystemRepository.findById(key);
        if (applicationSystem != null) {
            return applicationSystem;
        } else {
            throw new ApplicationSystemNotFound(key);
        }
    }

    @Override
    public ApplicationSystem getApplicationSystem(final String id) {
        if(!cacheApplicationSystems) {
            return findById(id);
        }
        try {
            return cache.get(id);
        } catch (ApplicationSystemNotFound e) {
            throw new ApplicationSystemNotFound(id);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationSystem getApplicationSystem(String id, String... includeFields) {
        return applicationSystemRepository.findById(id, includeFields);
    }

    @Override
    public ApplicationSystem getActiveApplicationSystem(String id) {
        ApplicationSystem applicationSystem = getApplicationSystem(id);
        if (applicationSystem.isActive()) {
            return applicationSystem;
        }
        throw new ApplicationSystemNotFound("Active application system %s not found", id);
    }

    @Override
    public void save(ApplicationSystem applicationSystem) {
        applicationSystemRepository.save(applicationSystem);
        if(cacheApplicationSystems) {
            cache.put(applicationSystem.getId(), applicationSystem);
        }
    }

    @Override
    public List<ApplicationSystem> getPublishedApplicationSystems(String... includeFields) {
        return applicationSystemRepository.findAllPublished(includeFields);
    }

    @Override
    public List<ApplicationSystem> getAllApplicationSystems(String... includeFields) {
        return applicationSystemRepository.findAll(includeFields);
    }

    @Override
    public List<String> findByYearAndSemester(String asSemester, String asYear) {
        List<ApplicationSystem> ass = applicationSystemRepository.findBySemesterAndYear(asSemester, asYear, "id", "name");
        return Lists.transform(ass, new Function<ApplicationSystem, String>() {
            @Override
            public String apply(ApplicationSystem as) {
                return as.getId();
            }
        });
    }
}
