package fi.vm.sade.haku.oppija.hakemus.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.ApplicationSystemNotFound;
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
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("/mongofixtures/application/*.json");
        for(Resource resource: resources) {
            final Application application = getTestApplication("1.2.246.562.11." + resource.getFilename().substring(0, resource.getFilename().indexOf('.')));
            assertNotNull(application);
        }
    }

    @Test
    public void fetchAllTestApplicationSystems() throws IOException {
        String[] oidPrefixes = {"1.2.246.562.5.","1.2.246.562.29."};
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("/mongofixtures/applicationSystem/*.json");
        for(Resource resource: resources) {
            Exception error = null;
            for(String oidPrefix: oidPrefixes) {
                try {
                    getTestApplicationSystem(oidPrefix + resource.getFilename().substring(0, resource.getFilename().indexOf('.')));
                    error = null;
                    break;
                }
                catch (Exception notFound) {
                    error = notFound;
                }
            }
            assertNull(error);
        }
    }
}

