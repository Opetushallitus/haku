package fi.vm.sade.haku.oppija.hakemus.it;

import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.testfixtures.MongoFixtureImporter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

public class IntegrationTestSupport {
    static AnnotationConfigApplicationContext appContext;

    @BeforeClass
    public static void createApplicationContextWithFixtures() throws IOException {
        appContext = ApiIntegrationTestSpringConfiguration.createApplicationContext();
        MongoFixtureImporter.importJsonFixtures(appContext.getBean(MongoTemplate.class), appContext.getBean(ApplicationDAO.class));
    }

    @AfterClass
    public static void shutdownApplicationContext() { appContext.close();
        appContext.stop();
    }
}
