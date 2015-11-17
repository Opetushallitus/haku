package fi.vm.sade.haku.oppija.hakemus;


import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.haku.oppija.hakemus.TestApplicationData.*;
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
                    service.paymentRequirements(getApplication(entry.getKey(), pohjakoulutus));
                } catch (ExecutionException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

}
