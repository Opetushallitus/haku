package fi.vm.sade.haku.oppija.hakemus.it;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.core.MongoTemplate;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoImpl;
import fi.vm.sade.haku.testfixtures.JsonFixtureImporter;

public class ApplicationDaoIT {
    @Test
    public void fetchApplication() throws IOException {
        final AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.getEnvironment().setActiveProfiles("it");
        appContext.register(TestConfiguration.class);
        appContext.refresh();

        JsonFixtureImporter.importJsonFixtures(appContext.getBean(MongoTemplate.class));

        final ApplicationDAOMongoImpl dao = appContext.getBean(ApplicationDAOMongoImpl.class);
        final List<Application> applications = dao.find(new Application());
        assertEquals(1, applications.size());
        final Application application = applications.get(0);
        assertEquals("aho minna wa", application.getFullName());
    }
}

@Configuration
@ComponentScan(basePackages = {"fi.vm.sade.haku"})
@PropertySource(value = {"config/it/haku.properties", "config/it/ext.properties", "haku-test.properties"})
@Profile("it")
@ImportResource("/META-INF/spring/logger-mock-context.xml")
class TestConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
