package fi.vm.sade.haku.oppija.configuration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {
    @Bean(name = "applicationSystemCacheBuilder")
    public CacheBuilder<String, ApplicationSystem> applicationSystemCacheBuilder(@Value("${application.system.cache.refresh:6}") final Integer cacheRefreshTimer) {
        return CacheBuilder
                .newBuilder()
                .recordStats()
                .maximumWeight(20)
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
                });
    }

}
