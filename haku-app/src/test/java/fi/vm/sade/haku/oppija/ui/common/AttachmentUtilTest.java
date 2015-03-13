package fi.vm.sade.haku.oppija.ui.common;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AttachmentUtilTest {

    @Test
    public void higherEdAttachmentEmptyTest() {
        Application application = new Application();

        Map<String, List<String>> attachmentOids = ApplicationUtil.getHigherEdAttachmentAOIds(application);
        assertTrue(attachmentOids.isEmpty());
    }

    @Test
    public void higherEdAttachmentAmkTest() {
        Application application = new Application();

        Map<String, String> baseEd = new HashMap<String, String>();
        Map<String, String> prefs = new HashMap<String, String>();

        baseEd.put("pohjakoulutus_yo", "true");
        baseEd.put("pohjakoulutus_yo_vuosi", "2012");
        baseEd.put("pohjakoulutus_yo_tutkinto", "eb");
        baseEd.put("pohjakoulutus_muu", "true");

        prefs.put("preference1-amkLiite", "true");
        prefs.put("preference1-Koulutus-id", "1.2.3");

        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, baseEd);
        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, prefs);

        Map<String, List<String>> attachmentOids = ApplicationUtil.getHigherEdAttachmentAOIds(application);
        assertFalse(attachmentOids.isEmpty());

        assertEquals(2, attachmentOids.size());
        assertTrue(attachmentOids.containsKey("form.valmis.todistus.muu"));
        assertEquals(1, attachmentOids.get("form.valmis.todistus.muu").size());
        assertEquals("1.2.3", attachmentOids.get("form.valmis.todistus.muu").get(0));
    }

    @Test
    public void higherEdAttachmentAmkAndYoTest() {
        Application application = new Application();

        Map<String, String> baseEd = new HashMap<String, String>();
        Map<String, String> prefs = new HashMap<String, String>();

        baseEd.put("pohjakoulutus_yo", "true");
        baseEd.put("pohjakoulutus_yo_vuosi", "2012");
        baseEd.put("pohjakoulutus_yo_tutkinto", "eb");
        baseEd.put("pohjakoulutus_muu", "true");

        prefs.put("preference1-amkLiite", "true");
        prefs.put("preference1-Koulutus-id", "1.2.3");

        prefs.put("preference2-yoLiite", "true");
        prefs.put("preference2-Koulutus-id", "4.5.6");

        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, baseEd);
        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, prefs);

        Map<String, List<String>> attachmentOids = ApplicationUtil.getHigherEdAttachmentAOIds(application);
        assertFalse(attachmentOids.isEmpty());

        assertEquals(2, attachmentOids.size());
        assertTrue(attachmentOids.containsKey("form.valmis.todistus.muu"));
        assertTrue(attachmentOids.containsKey("form.valmis.todistus.yo"));
        assertEquals(2, attachmentOids.get("form.valmis.todistus.muu").size());
        assertTrue(attachmentOids.get("form.valmis.todistus.muu").contains("1.2.3"));
        assertTrue(attachmentOids.get("form.valmis.todistus.muu").contains("4.5.6"));
        assertEquals(2, attachmentOids.get("form.valmis.todistus.yo").size());
        assertTrue(attachmentOids.get("form.valmis.todistus.yo").contains("4.5.6"));
    }
}
