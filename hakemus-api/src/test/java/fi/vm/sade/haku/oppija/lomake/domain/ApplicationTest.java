package fi.vm.sade.haku.oppija.lomake.domain;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApplicationTest {

    @Test
    public void testUpdateNameMetadata() {
        Application application = new Application();
        Map<String, String> answers = new HashMap<String, String>();
        answers.put(OppijaConstants.ELEMENT_ID_FIRST_NAMES, "Reino Matti-Jalmari");
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_PERSONAL, answers);
        application.updateNameMetadata();

        assertEquals("", application.getFullName());

        answers.put(OppijaConstants.ELEMENT_ID_LAST_NAME, "Aalto-setälä");
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_PERSONAL, answers);
        application.updateNameMetadata();

        assertEquals("aaltosetälä reino mattijalmari", application.getFullName());
    }
}
