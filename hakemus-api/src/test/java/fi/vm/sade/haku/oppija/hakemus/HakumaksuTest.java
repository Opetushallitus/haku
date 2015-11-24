package fi.vm.sade.haku.oppija.hakemus;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.Eligibility;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.haku.oppija.hakemus.Pohjakoulutus.MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA;
import static fi.vm.sade.haku.oppija.hakemus.TestApplicationData.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PHASE_EDUCATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HakumaksuTest {

    final HakumaksuService service = new HakumaksuService(
            "http://localhost/koodisto-service",
            "http://localhost/ao",
            "http://localhost/oppijan-tunnistus",
            "http://localhost/hakuperusteet",
            "http://localhost/hakuperusteet",
            "http://localhost/hakuperusteet",
            new MockedRestClient(testMappings())
    );

    @Test
    public void shouldBeExempt() throws ExecutionException {
        for (Map.Entry<Hakukelpoisuusvaatimus, List<Pohjakoulutus>> entry : exemptions.entrySet()) {
            for (Pohjakoulutus pohjakoulutus : entry.getValue()) {
                final boolean paymentRequired = service.isPaymentRequired(getAnswers(entry.getKey(), pohjakoulutus));

                assertTrue(
                        "paymentRequirements were not empty for '" + entry.getKey().getName() + "', '" + pohjakoulutus.getName() + "': " + paymentRequired,
                        !paymentRequired);
            }
        }
    }

    @Test
    public void shouldNotBeExempt() throws ExecutionException {
        for (Map.Entry<Hakukelpoisuusvaatimus, List<Pohjakoulutus>> entry : nonExempting.entrySet()) {
            for (Pohjakoulutus pohjakoulutus : entry.getValue()) {
                final boolean paymentRequired =
                        service.isPaymentRequired(getAnswers(entry.getKey(), pohjakoulutus));

                assertTrue(
                        "paymentRequirements were empty for '" + entry.getKey().getName() + "', '" + pohjakoulutus.getName() + "'",
                        paymentRequired);
            }
        }
    }

    @Test
    public void exemptingBaseEducationOverridesOneRequiringPayment() throws ExecutionException {
        ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = service.paymentRequirements(getAnswers(
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
        ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = service.paymentRequirements(getAnswers(
                Hakukelpoisuusvaatimus.HARKINNANVARAISUUS_TAI_ERIVAPAUS,
                MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA));

        assertTrue(
                "Harkinnanvaraisuus affected expected outcome: " + paymentRequirements,
                paymentRequirements.equals(ImmutableMap.of(ApplicationOptionOid.of(Hakukelpoisuusvaatimus.HARKINNANVARAISUUS_TAI_ERIVAPAUS.toString()), ImmutableSet.of())));
    }

    @Test
    public void harkinnanvarainenDoesntExemptFromPayment() throws ExecutionException {
        ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = service.paymentRequirements(getAnswers(
                ImmutableSet.of(APPLICATION_OPTION_WITH_IGNORE_AND_PAYMENT_EDUCATION_REQUIREMENTS),
                ImmutableSet.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA)));

        assertTrue(
                "Harkinnanvaraisuus affected expected outcome: " + paymentRequirements,
                paymentRequirements.equals(ImmutableMap.of(
                        ApplicationOptionOid.of(APPLICATION_OPTION_WITH_IGNORE_AND_PAYMENT_EDUCATION_REQUIREMENTS),
                        ImmutableSet.of(new Eligibility("maisteri", Types.AsciiCountryCode.of("ABW"))))));
    }

    private static final ImmutableMap<String, String> ulkomainenPohjakoulutus = ImmutableMap.of(
            "pohjakoulutus_ulk", "true",
            "pohjakoulutus_ulk_nimike", "Ulkomaalainen korkeakoulutus",
            "pohjakoulutus_ulk_suoritusmaa", "abw");
    private static final String hakutoiveenOid = Hakukelpoisuusvaatimus.YLEINEN_YLIOPISTOKELPOISUUS.toString();

    @Test
    public void successfulProcessingSetsPaymentStateToNotified() throws ExecutionException, InterruptedException {
        Application application = new Application() {{
            setOid("");
            addVaiheenVastaukset(PHASE_EDUCATION, ulkomainenPohjakoulutus);
            addVaiheenVastaukset(PHASE_APPLICATION_OPTIONS, ImmutableMap.of(
                    String.format(PREFERENCE_ID, 1), hakutoiveenOid));
        }};

        assertNull(application.getRequiredPaymentState());

        // Payment requirement must also be visible in logs
        Application processedApplication = service.processPayment(application);

        assertTrue(processedApplication == application);
        assertEquals(Application.PaymentState.NOTIFIED, processedApplication.getRequiredPaymentState());
    }
}
