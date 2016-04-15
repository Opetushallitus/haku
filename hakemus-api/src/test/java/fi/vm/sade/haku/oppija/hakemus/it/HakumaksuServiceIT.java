package fi.vm.sade.haku.oppija.hakemus.it;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.http.HttpRestClient;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
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
    private UrlConfiguration urlConfiguration = new UrlConfiguration();
    private HakumaksuService hakumaksuService = null;

    private String koulutuksenNimike = "Ulkomaalainen korkeakoulutus";
    private String koulutuksenMaa = "AFG";
    private ImmutableMap<String, String> ulkomainenPohjakoulutus = ImmutableMap.of(
            "pohjakoulutus_ulk", "true",
            "pohjakoulutus_ulk_nimike", koulutuksenNimike,
            "pohjakoulutus_ulk_suoritusmaa", koulutuksenMaa);
    private ApplicationOptionOid hakutoiveenOid = ApplicationOptionOid.of("1.2.246.562.20.40822369126");

    public HakumaksuServiceIT() {
        urlConfiguration.addDefault("host.virkailija","localhost:9090");
        hakumaksuService = new HakumaksuService(urlConfiguration, new HttpRestClient());
    }

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
