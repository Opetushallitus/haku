package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.LanguageCodeISO6391;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.MailTemplateUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PaymentEmailTest {

    public static Date fromString(String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.parse(dateString);
    }

    public static Date today() {
        return new DateTime(DateTimeZone.UTC).toDateMidnight().toDate();
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

        Date expectedExpirationDate = new Date(today().getTime() + GRACE_PERIOD);
        ApplicationSystemBuilder builder = new ApplicationSystemBuilder()
                .setApplicationPeriods(ImmutableList.of(
                        new ApplicationPeriod(fromString("2000-01-01"), fromString("2000-06-01")),
                        new ApplicationPeriod(fromString("2222-01-01"), fromString("2222-06-01")),
                        new ApplicationPeriod(fromString("2111-01-01"), fromString("2111-06-01"))))
                .setId("1.2.3")
                .setName(new I18nText(ImmutableMap.<String, String>of()));

        PaymentEmail paymentEmail = paymentEmailFromApplication(builder.get().getApplicationPeriods()).apply(application);

        String subject = paymentEmail.subject.getValue();
        assertEquals("Studyinfo - payment link", subject);
        assertEquals(expectedExpirationDate.getYear(), paymentEmail.expirationDate.getYear()); // Expiration date for the redirect link
        assertEquals(expectedExpirationDate.getMonth(), paymentEmail.expirationDate.getMonth()); // Expiration date for the redirect link
        assertEquals(expectedExpirationDate.getDate(), paymentEmail.expirationDate.getDate()); // Expiration date for the redirect link
        assertEquals(LanguageCodeISO6391.en, paymentEmail.language);
        String template = paymentEmail.template.getValue();
        assertTrue(template.contains(subject));
        assertTrue(template.contains("{{verification-link}}"));
        assertTrue(template.contains(localizedDateString(expectedExpirationDate, LanguageCodeISO6391.en))); // Next upcoming end date
        System.out.println(template);
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
        assertEquals(fromString("2000-01-25"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-01-15"), gracePeriod));
        // After end
        assertEquals(fromString("2000-01-31"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-01-21"), gracePeriod));
        // Before end and more than grace period
        assertEquals(relativeEndDate, calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-01-05"), gracePeriod));
    }

    @Test
    public void testDueDateWithNextUpcomingEndDateIsSelected() throws ParseException {
        ApplicationSystem applicationSystem = new ApplicationSystemBuilder()
                .setApplicationPeriods(ImmutableList.of(
                        // Keep unordered to validate ordering
                        new ApplicationPeriod(fromString("2000-06-01"), fromString("2000-07-01")),
                        new ApplicationPeriod(fromString("2000-02-01"), fromString("2000-03-01")),
                        new ApplicationPeriod(fromString("2000-04-01"), fromString("2000-05-01"))))
                .setId("1.2.3")
                .setName(new I18nText(ImmutableMap.<String, String>of()))
                .get();

        long gracePeriod = TimeUnit.DAYS.toMillis(10);

        // Before first
        assertEquals(fromString("2000-01-11"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-01-01"), gracePeriod));
        // Near first start
        assertEquals(fromString("2000-02-04"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-01-25"), gracePeriod));
        // Within first
        assertEquals(fromString("2000-03-01"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-02-10"), gracePeriod));
        // Between first and second, grace period should be applied
        assertEquals(fromString("2000-03-20"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-03-10"), gracePeriod));
        // Within second
        assertEquals(fromString("2000-05-01"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-04-10"), gracePeriod));
        // Within second, near end date, grace period should be applied
        assertEquals(fromString("2000-05-05"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-04-25"), gracePeriod));
        // After last period, grace period should be applied
        assertEquals(fromString("2000-07-12"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-07-02"), gracePeriod));
    }

    @Test
    public void testDueDateWithSingleApplicationPeriod() throws ParseException {
        ApplicationSystem applicationSystem = new ApplicationSystemBuilder()
                .setApplicationPeriods(ImmutableList.of(
                        new ApplicationPeriod(fromString("2000-04-01"), fromString("2000-05-01"))
                ))
                .setId("1.2.3")
                .setName(new I18nText(ImmutableMap.<String, String>of()))
                .get();

        long gracePeriod = TimeUnit.DAYS.toMillis(10);

        // Before start
        assertEquals(fromString("2000-03-11"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-03-01"), gracePeriod));
        // Within
        assertEquals(fromString("2000-05-01"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-04-02"), gracePeriod));
        // Within, near end date
        assertEquals(fromString("2000-05-05"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-04-25"), gracePeriod));
        // After end
        assertEquals(fromString("2000-05-15"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-05-05"), gracePeriod));
    }

    @Test
    public void testDueDateWithShortApplicationPeriod() throws ParseException {
        ApplicationSystem applicationSystem = new ApplicationSystemBuilder()
                .setApplicationPeriods(ImmutableList.of(
                        new ApplicationPeriod(fromString("2000-04-01"), fromString("2000-04-02"))
                ))
                .setId("1.2.3")
                .setName(new I18nText(ImmutableMap.<String, String>of()))
                .get();

        long gracePeriod = TimeUnit.DAYS.toMillis(10);

        // Before start
        assertEquals(fromString("2000-03-11"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-03-01"), gracePeriod));
        // Near start
        assertEquals(fromString("2000-04-10"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-03-31"), gracePeriod));
        // Within
        assertEquals(fromString("2000-04-11"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-04-01"), gracePeriod));
        // After end
        assertEquals(fromString("2000-05-15"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-05-05"), gracePeriod));
    }


    @Test
    public void testDueDateWithNeverEndingApplicationPeriod() throws ParseException {
        ApplicationSystem applicationSystem = new ApplicationSystemBuilder()
                .setApplicationPeriods(ImmutableList.of(
                        new ApplicationPeriod(fromString("2000-04-01"), null)
                ))
                .setId("1.2.3")
                .setName(new I18nText(ImmutableMap.<String, String>of()))
                .get();

        long gracePeriod = TimeUnit.DAYS.toMillis(10);

        // Before start
        assertEquals(fromString("2000-03-11"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-03-01"), gracePeriod));
        // Near start
        assertEquals(fromString("2000-04-10"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-03-31"), gracePeriod));
        // After start
        assertEquals(fromString("2000-05-15"), calculateDueDate(applicationSystem.getApplicationPeriods(), fromString("2000-05-05"), gracePeriod));
    }

}
