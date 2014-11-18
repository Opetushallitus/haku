package fi.vm.sade.haku.oppija.hakemus.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.ApplicationSystemNotFound;
import fi.vm.sade.haku.testfixtures.MongoFixtureImporter;
import org.junit.Test;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ApplicationDaoIT extends IntegrationTestSupport {
    @Test
    public void fetchApplication() throws IOException {
        final Application application = getTestApplication();
        assertEquals("aho minna wa", application.getFullName());
    }

    @Test
    public void fetchAllTestApplications() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(MongoFixtureImporter.MONGOFIXTURES + "application/*.json");
        for(Resource resource: resources) {
            final Application application = getTestApplication("1.2.246.562.11." + getResourceBaseName(resource));
            assertNotNull(application);
        }
    }

    @Test
    public void fetchAllTestApplicationSystems() throws IOException {
        String[] oidPrefixes = {"1.2.246.562.5.","1.2.246.562.29."};
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(MongoFixtureImporter.MONGOFIXTURES + "applicationSystem/*.json");
        for(Resource resource: resources) {
            String oidPostfix = getResourceBaseName(resource);
            Exception lastError = null;
            for(String oidPrefix: oidPrefixes) {
                try {
                    getTestApplicationSystem(oidPrefix + oidPostfix);
                    lastError = null;
                    break;
                }
                catch (Exception error) {
                    lastError = error;
                    if(!(error instanceof ApplicationSystemNotFound || error.getCause() instanceof ApplicationSystemNotFound)) {
                        break;
                    }
                }
            }
            assertNull(lastError);
        }
    }
}

