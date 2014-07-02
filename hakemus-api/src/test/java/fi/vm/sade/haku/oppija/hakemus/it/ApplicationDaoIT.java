package fi.vm.sade.haku.oppija.hakemus.it;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoImpl;
import fi.vm.sade.haku.testfixtures.JsonFixtureImporter;

public class ApplicationDaoIT {
    private static AnnotationConfigApplicationContext appContext;

    @BeforeClass
    public static void createApplicationContextWithFixtures() throws IOException {
        appContext = ApiIntegrationTestSpringConfiguration.createApplicationContext();
        JsonFixtureImporter.importJsonFixtures(appContext.getBean(MongoTemplate.class));
    }

    @Test
    public void fetchApplication() throws IOException {
        final ApplicationDAOMongoImpl dao = appContext.getBean(ApplicationDAOMongoImpl.class);
        final List<Application> applications = dao.find(new Application());
        assertEquals(1, applications.size());
        final Application application = applications.get(0);
        assertEquals("aho minna wa", application.getFullName());
    }

    @AfterClass
    public static void shutdownApplicationContext() {
        appContext.stop();
    }
}

