package fi.vm.sade.haku.oppija.hakemus.it;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.http.HttpRestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.EducationRequirementsUtil.Eligibility;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.MergedAnswers;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class HakumaksuServiceIT {
    private static final HakumaksuService hakumaksuService = new HakumaksuService(
            "https://testi.virkailija.opintopolku.fi/koodisto-service",
            "https://testi.opintopolku.fi/ao",
            "https://testi.virkailija.opintopolku.fi/oppijan-tunnistus",
            "https://testi.opintopolku.fi/hakuperusteet",
            "https://testi.studieinfo.fi/hakuperusteet",
            "https://testi.studyinfo.fi/hakuperusteet",
            new HttpRestClient());

    private static final String koulutuksenNimike = "Ulkomaalainen korkeakoulutus";
    private static final String koulutuksenMaa = "AFG";
    private static final ImmutableMap<String, String> ulkomainenPohjakoulutus = ImmutableMap.of(
            "pohjakoulutus_ulk", "true",
            "pohjakoulutus_ulk_nimike", koulutuksenNimike,
            "pohjakoulutus_ulk_suoritusmaa", koulutuksenMaa);
    private static final ApplicationOptionOid hakutoiveenOid = ApplicationOptionOid.of("1.2.246.562.20.40822369126");

    @Test
    public void endToEndPaymentRequirementTest() throws ExecutionException {
        Application application = new Application() {{
            setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_EDUCATION, ulkomainenPohjakoulutus);
            setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, ImmutableMap.of(
                    String.format(OppijaConstants.PREFERENCE_ID, 1), hakutoiveenOid.toString()));
        }};

        assertEquals(
                hakumaksuService.paymentRequirements(MergedAnswers.of(application)).get(hakutoiveenOid),
                ImmutableList.of(Eligibility.ulkomainen(koulutuksenNimike, Types.IsoCountryCode.of(koulutuksenMaa))));
    }
}
