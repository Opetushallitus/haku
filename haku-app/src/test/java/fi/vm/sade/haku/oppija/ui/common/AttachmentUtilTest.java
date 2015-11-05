package fi.vm.sade.haku.oppija.ui.common;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.oppija.hakemus.domain.util.AttachmentUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import org.junit.Test;

import java.util.ArrayList;
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

    @Test
    public void higherEdAttachmentYlempiAMKTest() {
        Application application = new Application();
        Map<String, String> baseEd = new HashMap<String, String>();
        Map<String, String> prefs = new HashMap<String, String>();
        baseEd.put("pohjakoulutus_yo", "true");
        baseEd.put("pohjakoulutus_yo_vuosi", "2012");
        baseEd.put("pohjakoulutus_yo_tutkinto", "eb");
        baseEd.put("pohjakoulutus_kk", "true");
        baseEd.put("pohjakoulutus_kk_vuosi", "2015");
        baseEd.put("pohjakoulutus_kk_ulk", "true");
        baseEd.put("pohjakoulutus_kk_ulk_vuosi", "2015");
        prefs.put("preference1-ylempiAMKLiite", "true");
        prefs.put("preference1-Koulutus-id", "1.2.3");
        prefs.put("preference2-yoLiite", "true");
        prefs.put("preference2-Koulutus-id", "4.5.6");
        prefs.put("preference3-Koulutus-id", "7.8.9");
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, baseEd);
        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, prefs);
        Map<String, List<String>> attachmentOids = ApplicationUtil.getHigherEdAttachmentAOIds(application);

        assertFalse(attachmentOids.isEmpty());
        assertEquals(2, attachmentOids.size());
        assertTrue(attachmentOids.containsKey("form.valmis.todistus.yo"));
        assertTrue(attachmentOids.containsKey("form.valmis.todistus.kk_ulk"));
        assertFalse(attachmentOids.containsKey("form.valmis.todistus.kk"));
        assertEquals(3, attachmentOids.get("form.valmis.todistus.yo").size());
        assertTrue(attachmentOids.get("form.valmis.todistus.yo").contains("1.2.3"));
        assertTrue(attachmentOids.get("form.valmis.todistus.yo").contains("4.5.6"));
        assertTrue(attachmentOids.get("form.valmis.todistus.yo").contains("7.8.9"));
        assertEquals(1, attachmentOids.get("form.valmis.todistus.kk_ulk").size());
        assertTrue(attachmentOids.get("form.valmis.todistus.kk_ulk").contains("1.2.3"));
    }

    @Test
    public void pohjakoulutusliitteetFromThoseSelectedInTarjontaTest() {
        Application application = new Application() {{
            addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, new HashMap<String, String>() {{
                put("pohjakoulutus_yo", "true");
                put("pohjakoulutus_yo_vuosi", "1980");
                put("pohjakoulutus_yo_tutkinto", "fi");
                put("pohjakoulutus_kk", "true");
                put("pohjakoulutus_kk_ulk", "true");
            }});
        }};
        List<ApplicationOptionDTO> aos = new ArrayList<>();
        ApplicationOptionDTO ao1 = new ApplicationOptionDTO() {{
            setPohjakoulutusLiitteet(new ArrayList<String>() {{
                add("pohjakoulutuskklomake_pohjakoulutuskk");
                add("pohjakoulutuskklomake_pohjakoulutuskkulk");
            }});
            setJosYoEiMuitaLiitepyyntoja(false);
        }};
        ApplicationOptionDTO ao2 = new ApplicationOptionDTO() {{
            setPohjakoulutusLiitteet(new ArrayList<String>() {{
                add("pohjakoulutuskklomake_pohjakoulutuskk");
            }});
            setJosYoEiMuitaLiitepyyntoja(false);
        }};
        aos.add(ao1);
        aos.add(ao2);
        Map<String, List<ApplicationOptionDTO>> liiteet = AttachmentUtil.pohjakoulutusliitepyynnot(application, aos);

        assertEquals(2, liiteet.keySet().size());
        assertEquals(2, liiteet.get("form.valmis.todistus.kk").size());
        assertEquals(1, liiteet.get("form.valmis.todistus.kk_ulk").size());
        assertTrue(liiteet.get("form.valmis.todistus.kk").contains(ao1));
        assertTrue(liiteet.get("form.valmis.todistus.kk").contains(ao2));
        assertTrue(liiteet.get("form.valmis.todistus.kk_ulk").contains(ao1));
    }

    @Test
    public void pohjakoulutusliitteetFromYoAndKVYoOnlyIfSoSelectedInTarjontaTest() {
        Application application1 = new Application() {{
            addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, new HashMap<String, String>() {{
                put("pohjakoulutus_yo", "true");
                put("pohjakoulutus_yo_vuosi", "1980");
                put("pohjakoulutus_yo_tutkinto", "fi");
                put("pohjakoulutus_kk", "true");
                put("pohjakoulutus_kk_ulk", "true");
            }});
        }};
        Application application2 = new Application() {{
            addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, new HashMap<String, String>() {{
                put("pohjakoulutus_yo_kansainvalinen_suomessa", "true");
                put("pohjakoulutus_kk", "true");
                put("pohjakoulutus_kk_ulk", "true");
            }});
        }};
        Application application3 = new Application() {{
            addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, new HashMap<String, String>() {{
                put("pohjakoulutus_yo_ulkomainen", "true");
                put("pohjakoulutus_kk", "true");
                put("pohjakoulutus_kk_ulk", "true");
            }});
        }};
        List<ApplicationOptionDTO> aos = new ArrayList<>();
        ApplicationOptionDTO ao1 = new ApplicationOptionDTO() {{
            setPohjakoulutusLiitteet(new ArrayList<String>() {{
                add("pohjakoulutuskklomake_pohjakoulutuskk");
                add("pohjakoulutuskklomake_pohjakoulutuskkulk");
            }});
            setJosYoEiMuitaLiitepyyntoja(true);
        }};
        ApplicationOptionDTO ao2 = new ApplicationOptionDTO() {{
            setPohjakoulutusLiitteet(new ArrayList<String>() {{
                add("pohjakoulutuskklomake_yosuomi");
                add("pohjakoulutuskklomake_pohjakoulutuskk");
            }});
            setJosYoEiMuitaLiitepyyntoja(false);
        }};
        aos.add(ao1);
        aos.add(ao2);
        Map<String, List<ApplicationOptionDTO>> liiteet1 = AttachmentUtil.pohjakoulutusliitepyynnot(application1, aos);

        assertEquals(2, liiteet1.keySet().size());
        assertEquals(1, liiteet1.get("form.valmis.todistus.kk").size());
        assertEquals(1, liiteet1.get("form.valmis.todistus.yo").size());
        assertTrue(liiteet1.get("form.valmis.todistus.kk").contains(ao2));
        assertTrue(liiteet1.get("form.valmis.todistus.yo").contains(ao2));

        Map<String, List<ApplicationOptionDTO>> liiteet2 = AttachmentUtil.pohjakoulutusliitepyynnot(application2, aos);

        assertEquals(1, liiteet2.keySet().size());
        assertEquals(1, liiteet2.get("form.valmis.todistus.kk").size());
        assertTrue(liiteet2.get("form.valmis.todistus.kk").contains(ao2));

        Map<String, List<ApplicationOptionDTO>> liiteet3 = AttachmentUtil.pohjakoulutusliitepyynnot(application3, aos);

        assertEquals(1, liiteet3.keySet().size());
        assertEquals(1, liiteet3.get("form.valmis.todistus.kk").size());
        assertTrue(liiteet3.get("form.valmis.todistus.kk").contains(ao2));
    }

    @Test
    public void pohjakoulutusliitteetFromYoTest() {
        Application application = new Application() {{
            addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, new HashMap<String, String>() {{
                put("pohjakoulutus_yo", "true");
                put("pohjakoulutus_yo_vuosi", "1980");
                put("pohjakoulutus_yo_tutkinto", "fi");
            }});
        }};
        List<ApplicationOptionDTO> aos = new ArrayList<>();
        ApplicationOptionDTO ao1 = new ApplicationOptionDTO() {{
            setPohjakoulutusLiitteet(new ArrayList<String>() {{
                add("pohjakoulutuskklomake_yosuomi");
            }});
            setJosYoEiMuitaLiitepyyntoja(false);
        }};
        ApplicationOptionDTO ao2 = new ApplicationOptionDTO() {{
            setPohjakoulutusLiitteet(new ArrayList<String>() {{
                add(AttachmentUtil.LUKIO_POHJAKOULUTUSKOODI);
            }});
            setJosYoEiMuitaLiitepyyntoja(false);
        }};
        aos.add(ao1);
        aos.add(ao2);
        Map<String, List<ApplicationOptionDTO>> liiteet = AttachmentUtil.pohjakoulutusliitepyynnot(application, aos);

        assertEquals(1, liiteet.keySet().size());
        assertEquals(1, liiteet.get("form.valmis.todistus.yo").size());
        assertTrue(liiteet.get("form.valmis.todistus.yo").contains(ao1));
    }

    @Test
    public void pohjakoulutusliitteetFromOnlyLukioTest() {
        Application application = new Application() {{
            addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, new HashMap<String, String>() {{
                put("pohjakoulutus_yo", "true");
                put("pohjakoulutus_yo_vuosi", "2000");
                put("pohjakoulutus_yo_tutkinto", "lk");
            }});
        }};
        List<ApplicationOptionDTO> aos = new ArrayList<>();
        ApplicationOptionDTO ao1 = new ApplicationOptionDTO() {{
            setPohjakoulutusLiitteet(new ArrayList<String>() {{
                add("pohjakoulutuskklomake_yosuomi");
            }});
            setJosYoEiMuitaLiitepyyntoja(false);
        }};
        ApplicationOptionDTO ao2 = new ApplicationOptionDTO() {{
            setPohjakoulutusLiitteet(new ArrayList<String>() {{
                add(AttachmentUtil.LUKIO_POHJAKOULUTUSKOODI);
            }});
            setJosYoEiMuitaLiitepyyntoja(false);
        }};
        aos.add(ao1);
        aos.add(ao2);
        Map<String, List<ApplicationOptionDTO>> liiteet = AttachmentUtil.pohjakoulutusliitepyynnot(application, aos);

        assertEquals(1, liiteet.keySet().size());
        assertEquals(1, liiteet.get("form.valmis.todistus.lukio").size());
        assertFalse(liiteet.containsKey("form.valmis.todistus.yo"));
        assertTrue(liiteet.get("form.valmis.todistus.lukio").contains(ao2));
    }
}
