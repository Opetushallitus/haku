package fi.vm.sade.haku.oppija.postprocess.impl;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import org.junit.Test;

import java.io.IOException;

import static fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import static fi.vm.sade.haku.oppija.postprocess.MailTemplateUtil.paymentEmailFromApplication;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.junit.Assert.*;

public class PaymentEmailTest {

    @Test
    public void testEmailTemplate() throws IOException {
        final String expectedEmail = "test@example.com";
        final String expectedHakemusOid = "1.2.3.4.5.6.7.8.9";
        final String expectedPersonOid = "9.8.7.6.6.5.4.3.2.1";

        Application application = new Application() {{
            setOid(expectedHakemusOid);
            setPersonOid(expectedPersonOid);
            addVaiheenVastaukset(PHASE_PERSONAL, ImmutableMap.of(
                    ELEMENT_ID_EMAIL, expectedEmail));
            addVaiheenVastaukset(PHASE_MISC, ImmutableMap.of(
                    ELEMENT_ID_CONTACT_LANGUAGE, "ruotsi"));
        }};

        PaymentEmail paymentEmail = paymentEmailFromApplication.apply(application);

        String subject = paymentEmail.subject.getValue();
        assertEquals("Studieinfo – maksulinkki", subject);
        String template = paymentEmail.template.getValue();
        assertTrue(template.contains(subject));
        assertTrue(template.contains("{{expires}}"));
        assertTrue(template.contains("{{verification-link}}"));
        assertTrue(!template.contains("{{submit_time}}")); // Replaced by current timestamp
    }
}
