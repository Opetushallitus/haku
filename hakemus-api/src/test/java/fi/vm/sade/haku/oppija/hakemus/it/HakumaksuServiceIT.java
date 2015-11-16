package fi.vm.sade.haku.oppija.hakemus.it;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

@Ignore
public class HakumaksuServiceIT {
    private static final HakumaksuService hakumaksuService = new HakumaksuService(
            "https://testi.virkailija.opintopolku.fi/koodisto-service",
            "https://testi.opintopolku.fi/ao");

    @Test
    public void endToEndPaymentRequirementTest() throws ExecutionException {
        final String koulutuksenNimike = "Ulkomaalainen korkeakoulutus";
        final String koulutuksenMaa = "AFG";
        final ApplicationOptionOid hakutoiveenOid = ApplicationOptionOid.of("1.2.246.562.20.40822369126");

        Application application = new Application() {{
            addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, ImmutableMap.of(
                    "pohjakoulutus_ulk", "true",
                    "pohjakoulutus_ulk_nimike", koulutuksenNimike,
                    "pohjakoulutus_ulk_suoritusmaa", koulutuksenMaa));
            addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, ImmutableMap.of(
                    String.format(OppijaConstants.PREFERENCE_ID, 1), hakutoiveenOid.toString()));
        }};

        assertEquals(
                hakumaksuService.paymentRequirements(application).get(hakutoiveenOid),
                ImmutableList.of(new HakumaksuService.Eligibility(koulutuksenNimike, Types.AsciiCountryCode.of(koulutuksenMaa))));
    }
}
