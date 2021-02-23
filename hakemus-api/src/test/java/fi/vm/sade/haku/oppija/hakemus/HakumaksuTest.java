package fi.vm.sade.haku.oppija.hakemus;


import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import fi.vm.sade.haku.http.MockedRestClient;
import fi.vm.sade.haku.http.MockedRestClient.Captured;
import fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PaymentState;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.service.EducationRequirementsUtil.Eligibility;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.testfixtures.Hakukelpoisuusvaatimus;
import fi.vm.sade.haku.testfixtures.Pohjakoulutus;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.SafeString;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Iterables.find;
import static fi.vm.sade.haku.testfixtures.Pohjakoulutus.MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA;
import static fi.vm.sade.haku.testfixtures.HakumaksuMockData.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.APPLICATION_PAYMENT_GRACE_PERIOD_MILLIS;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.junit.Assert.*;

public class HakumaksuTest {

    private MockedRestClient mockRestClient = new MockedRestClient(testMappings());
    private String oppijanTunnistusUrl = "https://localhost:9090/oppijan-tunnistus/api/v1/token";
    private String hakuperusteetUrlSv = "https://localhost-sv";
    private CloseableHttpClient mockHttpClient = Mockito.mock(CloseableHttpClient.class);
    private long PAYMENT_DUE_DATE_MILLIS = 1453893850027L;
    ImmutableList<ApplicationPeriod> applicationPeriods = ImmutableList.of(
            new ApplicationPeriod(new Date(0), new Date(new Date().getTime() + 20000))
    );

    private final UrlConfiguration urls = new UrlConfiguration(UrlConfiguration.SPRING_IT_PROFILE);

    public HakumaksuService service;

    public HakumaksuTest() {
        urls.addDefault("host.virkailija","localhost:9090")
                .addDefault("host.alb.virkailija","localhost:9090")
                .addDefault("schema.alb.virkailija","https")
                .addDefault("host.haku","localhost:9090")
                .addDefault("host.haku.sv","localhost-sv");

        service = new HakumaksuService(urls,
            new HakumaksuUtil(
                mockRestClient,
                urls,
                mockHttpClient,
                "",""));
    }

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
                        ImmutableSet.of(Eligibility.ulkomainen("maisteri", Types.IsoCountryCode.of("ABW"))))));
    }

    private ImmutableMap<String, String> ulkomainenPohjakoulutus = ImmutableMap.of(
            "pohjakoulutus_ulk", "true",
            "pohjakoulutus_ulk_nimike", "Ulkomaalainen korkeakoulutus",
            "pohjakoulutus_ulk_suoritusmaa", "abw");
    private String hakutoiveenOid = Hakukelpoisuusvaatimus.YLEINEN_YLIOPISTOKELPOISUUS.getArvo();

    private PaymentEmail testEmail = new PaymentEmail(SafeString.of("email title"),
            SafeString.of("empty tempalote"), OppijanTunnistusDTO.LanguageCodeISO6391.sv, new Date(0));

    // The real utility is in haku-app but unfortunately this code is not
    // easily movable there, need to duplicate for test
    private Function<Application, PaymentEmail> returnTestEmail = new Function<Application, PaymentEmail>() {
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
            setVaiheenVastauksetAndSetPhaseId(PHASE_PERSONAL, ImmutableMap.of(
                    ELEMENT_ID_EMAIL, expectedEmail));
            setVaiheenVastauksetAndSetPhaseId(PHASE_MISC, ImmutableMap.of(
                    ELEMENT_ID_CONTACT_LANGUAGE, "ruotsi"));
            setVaiheenVastauksetAndSetPhaseId(PHASE_EDUCATION, ulkomainenPohjakoulutus);
            setVaiheenVastauksetAndSetPhaseId(PHASE_APPLICATION_OPTIONS, ImmutableMap.of(
                    String.format(PREFERENCE_ID, 1), hakutoiveenOid));
        }};
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(response.getStatusLine()).thenReturn(new StatusLine() {
            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }

            @Override
            public int getStatusCode() {
                return 200;
            }

            @Override
            public String getReasonPhrase() {
                return null;
            }
        });
        Mockito.when(mockHttpClient.execute(Mockito.any())).thenReturn(response);
        assertNull(application.getRequiredPaymentState());

        // Payment requirement must also be visible in logs
        Application processedApplication = service.processPayment(application, applicationPeriods);

        Date expectedDueDate = new DateTime(new Date().getTime() + APPLICATION_PAYMENT_GRACE_PERIOD_MILLIS, DateTimeZone.UTC).toDateMidnight().toDate();
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
        assertEquals(PaymentState.OK, service.processPayment(application, applicationPeriods).getRequiredPaymentState());
    }

    @Test
    public void previouslyNotOkPaymentStateIsDroppedIfNoLongerExempt() throws ExecutionException, InterruptedException, IOException {
        Application application = new Application() {{
            setRequiredPaymentState(PaymentState.NOT_OK);
        }};
        assertNull(service.processPayment(application, applicationPeriods).getRequiredPaymentState());
    }

    @Test
    public void previouslyNotifiedPaymentStateIsDroppedIfNoLongerExempt() throws ExecutionException, InterruptedException, IOException {
        Application application = new Application() {{
            setRequiredPaymentState(PaymentState.NOTIFIED);
        }};
        assertNull(service.processPayment(application, applicationPeriods).getRequiredPaymentState());
    }

    @Test
    public void dueDateDoesNotChangeIfAlreadyPresent() throws ExecutionException, InterruptedException {
        Application application = new Application() {{
            setRequiredPaymentState(PaymentState.NOTIFIED);
            setPaymentDueDate(new Date(PAYMENT_DUE_DATE_MILLIS));
        }};

        assertEquals(PAYMENT_DUE_DATE_MILLIS, service.processPayment(application, applicationPeriods).getPaymentDueDate().getTime());
    }
}
