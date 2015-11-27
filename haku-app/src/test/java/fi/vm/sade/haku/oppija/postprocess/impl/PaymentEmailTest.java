package fi.vm.sade.haku.oppija.postprocess.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import static fi.vm.sade.haku.oppija.postprocess.MailTemplateUtil.paymentEmailFromApplication;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PaymentEmailTest {

    public static Date fromString(String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.parse(dateString);
    }

    @Test
    public void testEmailTemplate() throws IOException, ParseException {
        final String expectedEmail = "test@example.com";
        final String expectedHakemusOid = "1.2.3.4.5.6.7.8.9";
        final String expectedPersonOid = "9.8.7.6.6.5.4.3.2.1";
        final String expectedEndDate = "2222-06-01";

        Application application = new Application() {{
            setOid(expectedHakemusOid);
            setPersonOid(expectedPersonOid);
            addVaiheenVastaukset(PHASE_PERSONAL, ImmutableMap.of(
                    ELEMENT_ID_EMAIL, expectedEmail));
            addVaiheenVastaukset(PHASE_MISC, ImmutableMap.of(
                    ELEMENT_ID_CONTACT_LANGUAGE, "ruotsi"));
        }};

        ApplicationSystemBuilder builder = new ApplicationSystemBuilder()
                .setApplicationPeriods(ImmutableList.of(
                        new ApplicationPeriod(fromString("2000-01-01"), fromString("2000-06-01")),
                        new ApplicationPeriod(fromString("2222-01-01"), fromString(expectedEndDate)),
                        new ApplicationPeriod(fromString("2111-01-01"), fromString("2111-06-01"))))
                .setId("1.2.3")
                .setName(new I18nText(ImmutableMap.<String, String>of()));

        PaymentEmail paymentEmail = paymentEmailFromApplication(builder.get()).apply(application);

        String subject = paymentEmail.subject.getValue();
        assertEquals("Studieinfo – maksulinkki", subject);
        String template = paymentEmail.template.getValue();
        assertTrue(template.contains(subject));
        assertTrue(template.contains("{{expires}}"));
        assertTrue(template.contains("{{verification-link}}"));
        assertTrue(template.contains(expectedEndDate + "T00:00+0000")); // Last of the ending dates
        assertTrue(!template.contains("{{submit_time}}")); // Replaced by current timestamp
    }
}
