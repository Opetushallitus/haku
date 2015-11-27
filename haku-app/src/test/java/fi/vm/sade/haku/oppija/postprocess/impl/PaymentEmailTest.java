package fi.vm.sade.haku.oppija.postprocess.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.LanguageCodeISO6391;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import static fi.vm.sade.haku.oppija.postprocess.MailTemplateUtil.calculateDueDate;
import static fi.vm.sade.haku.oppija.postprocess.MailTemplateUtil.localizedDateString;
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

        Application application = new Application() {{
            setOid(expectedHakemusOid);
            setPersonOid(expectedPersonOid);
            addVaiheenVastaukset(PHASE_PERSONAL, ImmutableMap.of(
                    ELEMENT_ID_EMAIL, expectedEmail));
            addVaiheenVastaukset(PHASE_MISC, ImmutableMap.of(
                    ELEMENT_ID_CONTACT_LANGUAGE, "englanti"));
        }};

        Date expectedExpidateionDate = fromString("2222-06-01");
        ApplicationSystemBuilder builder = new ApplicationSystemBuilder()
                .setApplicationPeriods(ImmutableList.of(
                        new ApplicationPeriod(fromString("2000-01-01"), fromString("2000-06-01")),
                        new ApplicationPeriod(fromString("2222-01-01"), expectedExpidateionDate),
                        new ApplicationPeriod(fromString("2111-01-01"), fromString("2111-06-01"))))
                .setId("1.2.3")
                .setName(new I18nText(ImmutableMap.<String, String>of()));

        PaymentEmail paymentEmail = paymentEmailFromApplication(builder.get()).apply(application);

        String subject = paymentEmail.subject.getValue();
        assertEquals("Studyinfo - payment link", subject);
        assertEquals(expectedExpidateionDate, paymentEmail.expirationDate); // Expiration date for the redirect link
        assertEquals(LanguageCodeISO6391.en, paymentEmail.language);
        String template = paymentEmail.template.getValue();
        assertTrue(template.contains(subject));
        assertTrue(template.contains("{{verification-link}}"));
        assertTrue(template.contains("Saturday, June 1, 2222 3:00:00 AM EEST")); // Last of the ending dates
    }

    @Test
    public void testSwedishLocaleDateFormat() {
        assertEquals("den 1 januari 1970 kl 2:00 EET", localizedDateString(new Date(0), LanguageCodeISO6391.sv));
    }

    @Test
    public void testFinnishLocaleDateFormat() {
        assertEquals("1. tammikuuta 1970 2.00.00 EET", localizedDateString(new Date(0), LanguageCodeISO6391.fi));
    }

    @Test
    public void testDueDateCalculationWhenNearOriginalExpirationDate() throws ParseException {
        Date relativeEndDate = fromString("2000-01-20");
        ApplicationSystem applicationSystem = new ApplicationSystemBuilder()
                .setApplicationPeriods(ImmutableList.of(
                        new ApplicationPeriod(fromString("2000-01-01"), relativeEndDate)))
                .setId("1.2.3")
                .setName(new I18nText(ImmutableMap.<String, String>of()))
                .get();

        long gracePeriod = TimeUnit.DAYS.toMillis(10);

        // Before end but less than grace period
        assertEquals(fromString("2000-01-25"), calculateDueDate(applicationSystem, fromString("2000-01-15"), gracePeriod));
        // After end
        assertEquals(fromString("2000-01-31"), calculateDueDate(applicationSystem, fromString("2000-01-21"), gracePeriod));
        // Before end and more than grace period
        assertEquals(relativeEndDate, calculateDueDate(applicationSystem, fromString("2000-01-05"), gracePeriod));
    }
}
