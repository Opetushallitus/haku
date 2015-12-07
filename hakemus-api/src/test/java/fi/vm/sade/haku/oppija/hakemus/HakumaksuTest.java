package fi.vm.sade.haku.oppija.hakemus;


import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import fi.vm.sade.haku.oppija.hakemus.MockedRestClient.Captured;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PaymentState;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.hakemus.service.EducationRequirementsUtil.Eligibility;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.OppijanTunnistus;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.SafeString;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Iterables.find;
import static fi.vm.sade.haku.oppija.hakemus.Pohjakoulutus.MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA;
import static fi.vm.sade.haku.oppija.hakemus.TestApplicationData.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.junit.Assert.*;

public class HakumaksuTest {

    static final MockedRestClient mockRestClient = new MockedRestClient(testMappings());
    static final String oppijanTunnistusUrl = "http://localhost/oppijan-tunnistus";
    static final String hakuperusteetUrlFi = "http://localhost/hakuperusteet-fi";
    static final String hakuperusteetUrlSv = "http://localhost/hakuperusteet-sv";
    static final String hakuperusteetUrlEn = "http://localhost/hakuperusteet-en";

    final protected HakumaksuService service = new HakumaksuService(
            "http://localhost/koodisto-service",
            "http://localhost/ao",
            oppijanTunnistusUrl,
            hakuperusteetUrlFi,
            hakuperusteetUrlSv,
            hakuperusteetUrlEn,
            mockRestClient);

    @Before
    public void before() {
        mockRestClient.clearCaptured();
    }

    @Test
    public void shouldBeExempt() throws ExecutionException {
        for (Map.Entry<Hakukelpoisuusvaatimus, List<Pohjakoulutus>> entry : exemptions.entrySet()) {
            for (Pohjakoulutus pohjakoulutus : entry.getValue()) {
                final boolean paymentRequired = service.isPaymentRequired(getMergedAnswers(entry.getKey(), pohjakoulutus));

                assertTrue(
                        "paymentRequirements were not empty for '" + entry.getKey().getName() + "', '" + pohjakoulutus.getName() + "': " + paymentRequired,
                        !paymentRequired
                );
            }
        }
    }

    @Test
    public void shouldNotBeExempt() throws ExecutionException {
        for (Map.Entry<Hakukelpoisuusvaatimus, List<Pohjakoulutus>> entry : nonExempting.entrySet()) {
            for (Pohjakoulutus pohjakoulutus : entry.getValue()) {
                final boolean paymentRequired =
                        service.isPaymentRequired(getMergedAnswers(entry.getKey(), pohjakoulutus));

                assertTrue(
                        "paymentRequirements were empty for '" + entry.getKey().getName() + "', '" + pohjakoulutus.getName() + "'",
                        paymentRequired);
            }
        }
    }

    @Test
    public void exemptingBaseEducationOverridesOneRequiringPayment() throws ExecutionException {
        ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = service.paymentRequirements(getMergedAnswers(
                ImmutableSet.of(APPLICATION_OPTION_WITH_MULTIPLE_BASE_EDUCATION_REQUIREMENTS),
                ImmutableSet.of(
                        MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA, // Requires payment
                        Pohjakoulutus.SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI // Does exempt
                )));

        assertTrue(
                "Exempting base education did not remove need for payment, result: " + paymentRequirements,
                paymentRequirements.equals(ImmutableMap.of(ApplicationOptionOid.of(APPLICATION_OPTION_WITH_MULTIPLE_BASE_EDUCATION_REQUIREMENTS), ImmutableSet.of())));
    }

    @Test
    public void harkinnanvarainenAloneDoesntTriggerPayment() throws ExecutionException {
        ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = service.paymentRequirements(getMergedAnswers(
                Hakukelpoisuusvaatimus.HARKINNANVARAISUUS_TAI_ERIVAPAUS,
                MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA));

        assertTrue(
                "Harkinnanvaraisuus affected expected outcome: " + paymentRequirements,
                paymentRequirements.equals(ImmutableMap.of(ApplicationOptionOid.of(Hakukelpoisuusvaatimus.HARKINNANVARAISUUS_TAI_ERIVAPAUS.getArvo()), ImmutableSet.of())));
    }

    @Test
    public void harkinnanvarainenDoesntExemptFromPayment() throws ExecutionException {
        ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = service.paymentRequirements(getMergedAnswers(
                ImmutableSet.of(APPLICATION_OPTION_WITH_IGNORE_AND_PAYMENT_EDUCATION_REQUIREMENTS),
                ImmutableSet.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA)));

        assertTrue(
                "Harkinnanvaraisuus affected expected outcome: " + paymentRequirements,
                paymentRequirements.equals(ImmutableMap.of(
                        ApplicationOptionOid.of(APPLICATION_OPTION_WITH_IGNORE_AND_PAYMENT_EDUCATION_REQUIREMENTS),
                        ImmutableSet.of(Eligibility.ulkomainen("maisteri", Types.AsciiCountryCode.of("ABW"))))));
    }

    private static final ImmutableMap<String, String> ulkomainenPohjakoulutus = ImmutableMap.of(
            "pohjakoulutus_ulk", "true",
            "pohjakoulutus_ulk_nimike", "Ulkomaalainen korkeakoulutus",
            "pohjakoulutus_ulk_suoritusmaa", "abw");
    private static final String hakutoiveenOid = Hakukelpoisuusvaatimus.YLEINEN_YLIOPISTOKELPOISUUS.getArvo();

    private static final PaymentEmail testEmail = new PaymentEmail(SafeString.of("email title"),
            SafeString.of("empty tempalote"), HakumaksuUtil.LanguageCodeISO6391.sv, new Date(0));

    // The real utility is in haku-app but unfortunately this code is not
    // easily movable there, need to duplicate for test
    private static final Function<Application, PaymentEmail> returnTestEmail = new Function<Application, PaymentEmail>() {
        @Nullable
        @Override
        public PaymentEmail apply(@Nullable Application input) {
            return testEmail;
        }
    };

    @Test
    public void successfulProcessingSetsPaymentStateToNotified() throws ExecutionException, InterruptedException, IOException {
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
            addVaiheenVastaukset(PHASE_EDUCATION, ulkomainenPohjakoulutus);
            addVaiheenVastaukset(PHASE_APPLICATION_OPTIONS, ImmutableMap.of(
                    String.format(PREFERENCE_ID, 1), hakutoiveenOid));
        }};

        assertNull(application.getRequiredPaymentState());

        // Payment requirement must also be visible in logs
        Application processedApplication = service.processPayment(application, returnTestEmail);
        List<Captured> captured = mockRestClient.getCaptured();
        Captured match = find(captured, new Predicate<Captured>() {
            @Override
            public boolean apply(Captured input) {
                return input.url.equals(oppijanTunnistusUrl);
            }
        });

        assertEquals("POST", match.method);
        assertEquals(oppijanTunnistusUrl, match.url);

        OppijanTunnistus body = (OppijanTunnistus)match.body;
        assertEquals(expectedEmail, body.email);
        assertEquals(hakuperusteetUrlSv + "/app/" + expectedHakemusOid + "#/token/", body.url);
        assertEquals(HakumaksuUtil.LanguageCodeISO6391.sv, body.lang);
        assertEquals(expectedHakemusOid, body.metadata.hakemusOid);
        assertEquals(expectedPersonOid, body.metadata.personOid);
        assertEquals(testEmail.subject.getValue(), body.subject);
        assertEquals(testEmail.template.getValue(), body.template);
        assertEquals(testEmail.expirationDate.getTime(), body.expires);

        assertTrue(processedApplication == application);
        assertEquals(PaymentState.NOTIFIED, processedApplication.getRequiredPaymentState());

        List<ApplicationNote> notes = processedApplication.getNotes();
        assertEquals(1, notes.size());

        ApplicationNote applicationNote = notes.get(0);
        assertEquals("Hakija maksuvelvollinen: hakukohde 123 vaatii hakumaksun pohjakoulutusten 'Ulkomaalainen korkeakoulutus' johdosta", applicationNote.getNoteText());
        assertEquals("järjestelmä", applicationNote.getUser());

        assertTrue(processedApplication.getHistory().isEmpty());
    }

    @Test
    public void previouslyOkPaymentStateIsPreserved() throws ExecutionException, InterruptedException, IOException {
        Application application = new Application() {{
            setRequiredPaymentState(PaymentState.OK);
        }};
        assertEquals(PaymentState.OK, service.processPayment(application, returnTestEmail).getRequiredPaymentState());
    }

    @Test
    public void previouslyNotOkPaymentStateIsDroppedIfNoLongerExempt() throws ExecutionException, InterruptedException, IOException {
        Application application = new Application() {{
            setRequiredPaymentState(PaymentState.NOT_OK);
        }};
        assertNull(service.processPayment(application, returnTestEmail).getRequiredPaymentState());
    }

    @Test
    public void previouslyNotifiedPaymentStateIsDroppedIfNoLongerExempt() throws ExecutionException, InterruptedException, IOException {
        Application application = new Application() {{
            setRequiredPaymentState(PaymentState.NOTIFIED);
        }};
        assertNull(service.processPayment(application, returnTestEmail).getRequiredPaymentState());
    }
}
