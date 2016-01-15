package fi.vm.sade.haku.oppija.hakemus.it;

import java.io.File;
import java.io.IOException;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.ApplicationSystemNotFound;
import fi.vm.sade.haku.testfixtures.MongoFixtureImporter;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import static org.junit.Assert.*;

public class ApplicationDaoIT extends IntegrationTestSupport {

    @Test
    public void saveApplication() {
        final Application application = getTestApplication();
        final String capitalEmail = "EmailWithCapitalLetters@test.com";
        application.setOid("1.2.246.562.11.010101");
        application.getAnswers().get(OppijaConstants.PHASE_PERSONAL).put(OppijaConstants.ELEMENT_ID_EMAIL, capitalEmail);
        application.getAnswers().get(OppijaConstants.PHASE_PERSONAL).put(OppijaConstants.ELEMENT_ID_HUOLTAJANSAHKOPOSTI, capitalEmail);
        final Application savedApplication = save(application);
        final String savedEmail = savedApplication.getPhaseAnswers(OppijaConstants.PHASE_PERSONAL).get(OppijaConstants.ELEMENT_ID_EMAIL);
        final String savedHuoltajanEmail = savedApplication.getPhaseAnswers(OppijaConstants.PHASE_PERSONAL).get(OppijaConstants.ELEMENT_ID_EMAIL);
        assertNotEquals(capitalEmail, savedEmail);
        assertNotEquals(capitalEmail, savedHuoltajanEmail);
        assertEquals(capitalEmail.toLowerCase(), savedEmail);
        assertEquals(capitalEmail.toLowerCase(), savedHuoltajanEmail);
    }

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

