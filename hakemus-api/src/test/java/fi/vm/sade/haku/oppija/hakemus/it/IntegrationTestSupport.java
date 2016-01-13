package fi.vm.sade.haku.oppija.hakemus.it;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoImpl;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.IllegalStateException;
import fi.vm.sade.haku.oppija.lomake.service.impl.ApplicationSystemServiceImpl;
import fi.vm.sade.haku.testfixtures.MongoFixtureImporter;
import fi.vm.sade.hakutest.ApiIntegrationTestSpringConfiguration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.List;

public class IntegrationTestSupport {
    public static AnnotationConfigApplicationContext appContext;

    @BeforeClass
    public static void createApplicationContextWithFixtures() throws IOException {
        appContext = ApiIntegrationTestSpringConfiguration.createApplicationContext();
        MongoFixtureImporter.importJsonFixtures(appContext.getBean(MongoTemplate.class), appContext.getBean(ApplicationDAO.class));
    }

    @AfterClass
    public static void shutdownApplicationContext() { appContext.close();
        appContext.stop();
    }

    public static String getResourceBaseName(Resource resource) {
        return resource.getFilename().substring(0, resource.getFilename().indexOf('.'));
    }

    public static Application getTestApplication() {
        return getTestApplication("1.2.246.562.11.00000877107");
    }

    public static Application save(Application application) {
        appContext.getBean(ApplicationDAOMongoImpl.class).save(application);
        return getTestApplication(application.getOid());
    }

    public static Application getTestApplication(String oid) {
        List<Application> applications = appContext.getBean(ApplicationDAOMongoImpl.class).find(new Application().setOid(oid));
        if (applications.isEmpty())
            throw new IllegalStateException("application not found with oid " + oid);
        return applications.get(0);
    }

    public static ApplicationSystem getTestApplicationSystem() {
        return getTestApplicationSystem("1.2.246.562.5.2014022711042555034240");
    }

    public static ApplicationSystem getTestApplicationSystem(String oid) {
        return appContext.getBean(ApplicationSystemServiceImpl.class).getApplicationSystem(oid);
    }
}
