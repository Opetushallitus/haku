package fi.vm.sade.haku.oppija.hakemus.domain.util;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApplicationUtilTest {
    @Test
    public void hakutoiveetInSortedOrder() {
        final Map<String, String> hakutoiveet = new HashMap<>();
        hakutoiveet.put("preference2-Koulutus-id", "oid2");
        hakutoiveet.put("preference1-Koulutus-id", "oid1");
        hakutoiveet.put("preference6-Koulutus-id", "oid6");
        hakutoiveet.put("preference3-Koulutus-id", "oid3");
        hakutoiveet.put("preference5-Koulutus-id", "oid5");
        hakutoiveet.put("preference4-Koulutus-id", "oid4");
        List<String> oids = ApplicationUtil.getPreferenceAoIds(new Application() {{
            setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, hakutoiveet);
        }});
        assertEquals(Lists.newArrayList("oid1", "oid2", "oid3", "oid4", "oid5", "oid6"), oids);
    }
}
