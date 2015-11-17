package fi.vm.sade.haku.oppija.hakemus;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.Eligibility;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.haku.oppija.hakemus.TestApplicationData.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HakumaksuTest {

    final HakumaksuService service = new HakumaksuService(
            "http://localhost/koodisto-service", "http://localhost/ao",
            new MockedRestClient(testMappings())
    );

    @Test
    public void shouldBeExempt() {
        for (Map.Entry<Hakukelpoisuusvaatimus, List<Pohjakoulutus>> entry : exemptions.entrySet()) {
            for (Pohjakoulutus pohjakoulutus : entry.getValue()) {
                try {
                    final Map<ApplicationOptionOid, List<Eligibility>> requirements =
                            service.paymentRequirements(getApplication(entry.getKey(), pohjakoulutus));

                    assertTrue(
                            "paymentRequirements were not empty for '" + entry.getKey().getName() + "', '" + pohjakoulutus.getName() + "': " + requirements.entrySet().iterator().next().getValue().toString(),
                            !requirements.isEmpty() && requirements.entrySet().iterator().next().getValue().isEmpty()
                    );
                } catch (ExecutionException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    @Test
    public void shouldNotBeExempt() {
        for (Map.Entry<Hakukelpoisuusvaatimus, List<Pohjakoulutus>> entry : nonExempting.entrySet()) {
            for (Pohjakoulutus pohjakoulutus : entry.getValue()) {
                try {
                    final Map<Types.ApplicationOptionOid, List<HakumaksuService.Eligibility>> requirements =
                            service.paymentRequirements(getApplication(entry.getKey(), pohjakoulutus));

                    assertTrue(
                            "paymentRequirements were empty for '" + entry.getKey().getName() + "', '" + pohjakoulutus.getName() + "'",
                            !requirements.isEmpty() && !requirements.entrySet().iterator().next().getValue().isEmpty()
                    );
                } catch (ExecutionException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    @Test
    public void exemptingBaseEducationOverridesOneRequiringPayment() throws ExecutionException {
        Map<ApplicationOptionOid, List<Eligibility>> paymentRequirements = service.paymentRequirements(getApplication(
                ImmutableSet.of(APPLICATION_OPTION_WITH_MULTIPLE_BASE_EDUCATION_REQUIREMENTS),
                ImmutableSet.of(
                        Pohjakoulutus.MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA, // Requires payment
                        Pohjakoulutus.SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI // Does exempt
                )));
        assertTrue(
                "Exempting base education did not remove need for payment, result: " + paymentRequirements,
                paymentRequirements.equals(ImmutableMap.of(ApplicationOptionOid.of(APPLICATION_OPTION_WITH_MULTIPLE_BASE_EDUCATION_REQUIREMENTS), ImmutableList.of())));
    }
}
