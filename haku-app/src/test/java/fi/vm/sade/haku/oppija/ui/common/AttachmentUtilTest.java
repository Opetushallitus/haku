package fi.vm.sade.haku.oppija.ui.common;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
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
                add("pohjakoulutuskklomake_pohjakoulutuslk");
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
                add("pohjakoulutuskklomake_pohjakoulutuslk");
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
