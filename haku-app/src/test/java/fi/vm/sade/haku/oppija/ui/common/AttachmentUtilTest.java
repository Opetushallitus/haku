package fi.vm.sade.haku.oppija.ui.common;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.util.AttachmentUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AttachmentUtilTest {

    public static Application mergePohjakoulutus(Application application, Application ... applications) {
        final Map<String, String> answers = new HashMap<>(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));
        for (final Application b : applications) {
            answers.putAll(b.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));
        }
        return new Application() {{
            addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, answers);
        }};
    }

    public static ApplicationOptionDTO mergeLiitepyynnot(ApplicationOptionDTO ao, ApplicationOptionDTO ... aos) {
        final List<String> liitepyynnot = new ArrayList<>(ao.getPohjakoulutusLiitteet());
        for (final ApplicationOptionDTO b : aos) {
            liitepyynnot.addAll(b.getPohjakoulutusLiitteet());
        }
        return new ApplicationOptionDTO() {{
            setPohjakoulutusLiitteet(liitepyynnot);
        }};
    }

    public static final Application hakemusLukio = new Application() {{
        addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, ImmutableMap.of(
                "pohjakoulutus_yo", "true",
                "pohjakoulutus_yo_vuosi", "2000",
                "pohjakoulutus_yo_tutkinto", "lk"));
    }};

    public static final Application hakemusYoVanha = new Application() {{
        addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, ImmutableMap.of(
                "pohjakoulutus_yo", "true",
                "pohjakoulutus_yo_vuosi", "1980",
                "pohjakoulutus_yo_tutkinto", "fi"));
    }};

    public static final Application hakemusYoKv = new Application() {{
        addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, ImmutableMap.of("pohjakoulutus_yo_kansainvalinen_suomessa", "true"));
    }};

    public static final Application hakemusYoKvUlk = new Application() {{
        addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, ImmutableMap.of("pohjakoulutus_yo_ulkomainen", "true"));
    }};

    public static final Application hakemusKK = new Application() {{
        addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, ImmutableMap.of("pohjakoulutus_kk", "true"));
    }};

    public static final Application hakemusKKUlk = new Application() {{
        addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, ImmutableMap.of("pohjakoulutus_kk_ulk", "true"));
    }};

    public static final ApplicationOptionDTO hakukohdeKK = new ApplicationOptionDTO() {{
        setPohjakoulutusLiitteet(newArrayList("pohjakoulutuskklomake_pohjakoulutuskk"));
    }};

    public static final ApplicationOptionDTO hakukohdeKKUlk = new ApplicationOptionDTO() {{
        setPohjakoulutusLiitteet(newArrayList("pohjakoulutuskklomake_pohjakoulutuskkulk"));
    }};

    public static final ApplicationOptionDTO hakukohdeYo = new ApplicationOptionDTO() {{
        setPohjakoulutusLiitteet(newArrayList("pohjakoulutuskklomake_yosuomi"));
    }};

    public static final ApplicationOptionDTO hakukohdeLukio = new ApplicationOptionDTO() {{
        setPohjakoulutusLiitteet(newArrayList("pohjakoulutuskklomake_pohjakoulutuslk"));
    }};

    @Test
    public void pohjakoulutusliitteetFromThoseSelectedInTarjontaTest() {
        ApplicationOptionDTO hakukohdeKKandKKUlk = mergeLiitepyynnot(hakukohdeKK, hakukohdeKKUlk);
        Map<String, List<ApplicationOptionDTO>> liiteet = AttachmentUtil.pohjakoulutusliitepyynnot(
                mergePohjakoulutus(hakemusYoVanha, hakemusKK, hakemusKKUlk),
                newArrayList(hakukohdeKK, hakukohdeKKandKKUlk));

        assertEquals(2, liiteet.keySet().size());
        assertEquals(2, liiteet.get("form.valmis.todistus.kk").size());
        assertEquals(1, liiteet.get("form.valmis.todistus.kk_ulk").size());
        assertTrue(liiteet.get("form.valmis.todistus.kk").contains(hakukohdeKKandKKUlk));
        assertTrue(liiteet.get("form.valmis.todistus.kk").contains(hakukohdeKK));
        assertTrue(liiteet.get("form.valmis.todistus.kk_ulk").contains(hakukohdeKKandKKUlk));
    }

    @Test
    public void pohjakoulutusliitteetFromYoAndKVYoOnlyIfSoSelectedInTarjontaTest() {
        ApplicationOptionDTO hakukohdeKKandKKUlk = mergeLiitepyynnot(hakukohdeKK, hakukohdeKKUlk);
        hakukohdeKKandKKUlk.setJosYoEiMuitaLiitepyyntoja(true);
        ApplicationOptionDTO hakukohdeYoAndKK = mergeLiitepyynnot(hakukohdeYo, hakukohdeKK);
        List<ApplicationOptionDTO> aos = newArrayList(hakukohdeKKandKKUlk, hakukohdeYoAndKK);
        Map<String, List<ApplicationOptionDTO>> liiteet1 = AttachmentUtil.pohjakoulutusliitepyynnot(
                mergePohjakoulutus(hakemusYoVanha, hakemusKK, hakemusKKUlk), aos);

        assertEquals(2, liiteet1.keySet().size());
        assertEquals(1, liiteet1.get("form.valmis.todistus.kk").size());
        assertEquals(1, liiteet1.get("form.valmis.todistus.yo").size());
        assertTrue(liiteet1.get("form.valmis.todistus.kk").contains(hakukohdeYoAndKK));
        assertTrue(liiteet1.get("form.valmis.todistus.yo").contains(hakukohdeYoAndKK));

        Map<String, List<ApplicationOptionDTO>> liiteet2 = AttachmentUtil.pohjakoulutusliitepyynnot(
                mergePohjakoulutus(hakemusYoKv, hakemusKK, hakemusKKUlk), aos);

        assertEquals(1, liiteet2.keySet().size());
        assertEquals(1, liiteet2.get("form.valmis.todistus.kk").size());
        assertTrue(liiteet2.get("form.valmis.todistus.kk").contains(hakukohdeYoAndKK));

        Map<String, List<ApplicationOptionDTO>> liiteet3 = AttachmentUtil.pohjakoulutusliitepyynnot(
                mergePohjakoulutus(hakemusYoKvUlk, hakemusKK, hakemusKKUlk), aos);

        assertEquals(1, liiteet3.keySet().size());
        assertEquals(1, liiteet3.get("form.valmis.todistus.kk").size());
        assertTrue(liiteet3.get("form.valmis.todistus.kk").contains(hakukohdeYoAndKK));
    }

    @Test
    public void pohjakoulutusliitteetFromYoTest() {
        Map<String, List<ApplicationOptionDTO>> liiteet = AttachmentUtil.pohjakoulutusliitepyynnot(
                hakemusYoVanha,
                newArrayList(hakukohdeYo, hakukohdeLukio));

        assertEquals(1, liiteet.keySet().size());
        assertEquals(1, liiteet.get("form.valmis.todistus.yo").size());
        assertTrue(liiteet.get("form.valmis.todistus.yo").contains(hakukohdeYo));
    }

    @Test
    public void pohjakoulutusliitteetFromOnlyLukioTest() {
        Map<String, List<ApplicationOptionDTO>> liiteet = AttachmentUtil.pohjakoulutusliitepyynnot(
                hakemusLukio,
                newArrayList(hakukohdeYo, hakukohdeLukio));

        assertEquals(1, liiteet.keySet().size());
        assertEquals(1, liiteet.get("form.valmis.todistus.lukio").size());
        assertTrue(liiteet.get("form.valmis.todistus.lukio").contains(hakukohdeLukio));
    }
}
