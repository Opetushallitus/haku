package fi.vm.sade.haku.oppija.hakemus.it;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ApplicationDaoSaveIT extends IntegrationTestSupport {

    @Test
    public void saveApplication() {
        final Application application = getTestApplication("1.2.246.562.11.00004587493");
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
}

