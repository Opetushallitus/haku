package fi.vm.sade.haku.oppija.hakemus;


import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
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
                    final Map<Types.ApplicationOptionOid, List<HakumaksuService.Eligibility>> requirements =
                            service.paymentRequirements(getApplication(entry.getKey(), pohjakoulutus));

                    assertTrue(
                            "paymentRequirements were not empty for " + entry.getKey() + ", " + pohjakoulutus.name() + ": " + requirements.toString(),
                            !requirements.isEmpty() && requirements.entrySet().iterator().next().getValue().isEmpty()
                    );
                } catch (ExecutionException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

}
