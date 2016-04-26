package fi.vm.sade.haku.oppija.hakemus.service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO.LanguageCodeISO6391;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PaymentState;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.EducationRequirements;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.properties.OphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Iterables.*;
import static fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil.getPreferenceAoIds;
import static fi.vm.sade.haku.oppija.hakemus.service.EducationRequirementsUtil.Eligibility;
import static fi.vm.sade.haku.oppija.hakemus.service.EducationRequirementsUtil.kkBaseEducationRequirements;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.APPLICATION_PAYMENT_GRACE_PERIOD_MILLIS;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.MailTemplateUtil.calculateDueDate;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.MailTemplateUtil.paymentEmailFromApplication;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.OPTION_ID_POSTFIX;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PREFERENCE_PREFIX;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.*;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class HakumaksuService {
    public static final Logger LOGGER = LoggerFactory.getLogger(HakumaksuService.class);
    public static final String SYSTEM_USER = "järjestelmä";

    private final HakumaksuUtil util;

    private final OphProperties urlConfiguration;

    public HakumaksuService(
            final OphProperties urlConfiguration,
            final RestClient restClient
    ) {
        this.urlConfiguration = urlConfiguration;
        util = new HakumaksuUtil(restClient, urlConfiguration);
    }

    private final Predicate<Eligibility> eligibilityRequiresPayment = new Predicate<Eligibility>() {
        @Override
        public boolean apply(Eligibility kelpoisuus) {
            try {
                return !kelpoisuus.pohjakoulutusVapauttaaHakumaksusta &&
                        !util.isEducationCountryExemptFromPayment(kelpoisuus.suoritusmaa);
            } catch (ExecutionException e) {
                // TODO: log + let pass as our system is unexpectedly broken?
                return false;
            }
        }
    };

    /*
     * - Kutsuja tarkistaa: Tarkista onko maksua ylipäänsä -> jos EI ole korkeakoulu && syksy 2016 eteenpäin -> EI MAKSUA
     * - Per hakijan hakutoive:
     *   - Jos hakijan pohjakoulutuksesta löytyy YKSIKIN suomalainen hakutoiveen vaatimuksen täyttävä koulutus -> EI MAKSUA
     *   - Jos ei löydy yhtään suomalaista vaatimusta täyttävää, löytyykö ulkomaista täyttävää?
     *     - Jos ei löydy -> EI MAKSUA (hakutoive hylätään myöhemmin koska ei löydy yhtään vaatimusta täyttävää pohjakoulutusta)
     *     - Jos löytyy
     *       - Per pohjakoulutusvaatimuksen täyttävä pohjakoulutus:
     *         - Koulutus on ETA/Sveitsi-alueelta -> EI MAKSUA (tämän hakutoiveen seurauksena)
     *       - Jos mikään koulutus ei ETA/Sveitsi -> MAKSU (ei-ETA/Sveitsi-alueen pohjakoulutusten seurauksena)
     */
    public ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements(MergedAnswers answers) {
        ImmutableMap.Builder<ApplicationOptionOid, ImmutableSet<Eligibility>> builder = ImmutableMap.builder();
        ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> applicationPaymentEligibilities = builder.build();

        List<ApplicationOptionOid> preferenceAoIds = asApplicationOptionOids(getPreferenceAoIds(answers));
        for (EducationRequirements applicationOptionRequirement : util.getEducationRequirements(preferenceAoIds)) {
            ImmutableSet.Builder<Eligibility> aoPaymentEligibilityBuilder = ImmutableSet.builder();
            boolean exemptingAoFound = false;

            for (String baseEducationRequirement : applicationOptionRequirement.baseEducationRequirements) {
                // Ohita pohjakoulutusvaatimus jos sitä ei ole mäpätty
                if (!kkBaseEducationRequirements.containsKey(baseEducationRequirement)) {
                    continue;
                }

                ImmutableSet<Eligibility> allEligibilities = kkBaseEducationRequirements.get(baseEducationRequirement).apply(answers);
                ImmutableSet<Eligibility> paymentEligibilities = ImmutableSet.copyOf(filter(allEligibilities, eligibilityRequiresPayment));

                if (allEligibilities.size() == paymentEligibilities.size()) {
                    // Ei löytynyt yhtään maksusta vapauttavaa kelpoisuutta,
                    // otetaan talteen logitusta / UI:ta varten
                    aoPaymentEligibilityBuilder.addAll(paymentEligibilities);
                } else {
                    // Löytyi yksikin maksusta vapauttava kohde,
                    // vapautetaan koko kohde maksuista
                    exemptingAoFound = true;
                    break;
                }
            }

            ImmutableSet<Eligibility> aoPaymentEligibilities = exemptingAoFound ? ImmutableSet.<Eligibility>of() : aoPaymentEligibilityBuilder.build();
            // We need to check whether the applicationPaymentEligibilities already contains this application option.
            // This is because the applicationPaymentEligibilities is built prior to form validation, so there might
            // exist duplicate application options in the application, which violates the uniqueness constraint of the
            // immutable maps.
            if (! applicationPaymentEligibilities.containsKey(applicationOptionRequirement.applicationOptionId)) {
                builder.put(applicationOptionRequirement.applicationOptionId, aoPaymentEligibilities);
                applicationPaymentEligibilities = builder.build();
            }
        }
        return applicationPaymentEligibilities;
    }

    private List<ApplicationOptionOid> asApplicationOptionOids(List<String> preferenceAoIds) {
        return Lists.transform(preferenceAoIds, new Function<String, ApplicationOptionOid>() {
            @Override
            public ApplicationOptionOid apply(String input) {
                return ApplicationOptionOid.of(input);
            }
        });
    }

    public boolean isPaymentRequired(MergedAnswers mergedAnswers) {
        return any(paymentRequirements(mergedAnswers).values(), new Predicate<Set<Eligibility>>() {
            @Override
            public boolean apply(Set<Eligibility> input) {
                return !input.isEmpty();
            }
        });
    }

    public static ImmutableSet<ApplicationOptionOid> getAoOidsFromAnswers(Map<String, String> answers) {
        ImmutableSet.Builder<ApplicationOptionOid> aoListBuilder = ImmutableSet.builder();

        for (String key : answers.keySet()) {
            if (key != null && key.startsWith(PREFERENCE_PREFIX) && key.endsWith(OPTION_ID_POSTFIX) && isNotEmpty(answers.get(key))) {
                aoListBuilder.add(ApplicationOptionOid.of(answers.get(key)));
            }
        }

        return aoListBuilder.build();
    }

    public ImmutableMap<ApplicationOptionOid, Boolean> getPaymentRequirementsForApplicationOptions(Map<String, String> answers) {
        final ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = paymentRequirements(MergedAnswers.of(answers));

        return ImmutableMap.copyOf(Maps.asMap(getAoOidsFromAnswers(answers), new Function<ApplicationOptionOid, Boolean>() {
            @Override
            public Boolean apply(ApplicationOptionOid applicationOptionOid) {
                return !paymentRequirements.get(applicationOptionOid).isEmpty();
            }
        }));
    }

    public boolean allApplicationOptionsRequirePayment(Application application) {
        ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = paymentRequirements(MergedAnswers.of(application));
        return all(paymentRequirements.values(), new Predicate<Set<Eligibility>>() {
            @Override
            public boolean apply(Set<Eligibility> input) {
                return ! input.isEmpty();
            }
        });
    }

    private static boolean isExemptFromPayment(Map<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements) {
        return all(paymentRequirements.values(), new Predicate<Set<Eligibility>>() {
            @Override
            public boolean apply(Set<Eligibility> input) {
                return input.isEmpty();
            }
        });
    }

    public static class PaymentEmail {
        public final SafeString subject;
        public final SafeString template;
        public final Date expirationDate;
        public final LanguageCodeISO6391 language;

        public PaymentEmail(SafeString subject, SafeString template, LanguageCodeISO6391 language, Date expirationDate) {
            this.subject = subject;
            this.template = template;
            this.language = language;
            this.expirationDate = expirationDate;
        }
    }

    private void setPaymentDueDateIfNotSet(Application application, List<ApplicationPeriod> applicationPeriods) {
        Optional<Date> fromApplication = Optional.fromNullable(application.getPaymentDueDate());

        Date dueDate = fromApplication
                .or(calculateDueDate(applicationPeriods, new Date(), APPLICATION_PAYMENT_GRACE_PERIOD_MILLIS));

        if (!fromApplication.isPresent()) {
            application.setPaymentDueDate(dueDate);
        }
    }

    public Application processPayment(Application application, List<ApplicationPeriod> applicationPeriods) throws ExecutionException, InterruptedException {
        Function<Application, PaymentEmail> buildEmail = paymentEmailFromApplication(applicationPeriods);

        Optional<PaymentState> requiredPaymentState = Optional.fromNullable(application.getRequiredPaymentState());

        // Jos hakumaksu on suoritettu, edes maksuvelvollisuuden katoaminen ei poista onnistunutta maksumerkintää
        if (alreadyPaid(requiredPaymentState)) {
            return application;
        } else {
            Map<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = paymentRequirements(MergedAnswers.of(application));

            boolean exemptFromPayment = isExemptFromPayment(paymentRequirements);

            // Tullut maksuvelvolliseksi tai pyydetty maksulinkin uudelleenlähetystä
            if (!exemptFromPayment
                    && (!requiredPaymentState.isPresent() || requireResend(application, requiredPaymentState))) {

                Date fromApplication = application.getPaymentDueDate();
                setPaymentDueDateIfNotSet(application, applicationPeriods);

                ApplicationOid applicationOid;
                try {
                    applicationOid = ApplicationOid.of(application.getOid());
                    paymentRequestOrThrow(
                            buildEmail.apply(application),
                            SafeString.of(application.getPhaseAnswers(OppijaConstants.PHASE_PERSONAL).get("Sähköposti")),
                            applicationOid,
                            PersonOid.of(application.getPersonOid()));
                } catch (Exception e) {
                    // Rollback jos Oppijantunnistus-pyyntö epäonnistuu
                    application.setPaymentDueDate(fromApplication);
                    throw e;
                }

                application.setRequiredPaymentState(PaymentState.NOTIFIED);

                if (!requiredPaymentState.isPresent() && application.getRequiredPaymentState() == PaymentState.NOTIFIED) {
                    addPaymentRequiredNote(application, paymentRequirements);
                }

                LOGGER.info("Marked application " + applicationOid + " payment requirements: " + paymentRequirements + ", payment state: " + application.getRequiredPaymentState());

                return application;
            } else {
                if (exemptFromPayment && requiredPaymentState.isPresent()) {
                    addPaymentNote(application, new ApplicationNote("Hakija ei enää maksuvelvollinen", new Date(), SYSTEM_USER));

                    // Muut kuin onnistuneet maksumerkinnät poistetaan jos maksuvelvollisuus poistuu
                    application.setRequiredPaymentState(null);
                }

                return application;
            }
        }
    }

    private void addPaymentNote(Application application, ApplicationNote järjestelmä) {
        application.addNote(järjestelmä);
    }

    private void addPaymentRequiredNote(Application application, Map<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements) {
        final Joiner joiner = Joiner.on(", ");

        Iterable<String> requirements = Iterables.transform(Iterables.filter(paymentRequirements.entrySet(), new Predicate<Map.Entry<ApplicationOptionOid, ImmutableSet<Eligibility>>>() {
            @Override
            public boolean apply(Map.Entry<ApplicationOptionOid, ImmutableSet<Eligibility>> input) {
                return !input.getValue().isEmpty();
            }
        }), new Function<Map.Entry<ApplicationOptionOid, ImmutableSet<Eligibility>>, String>() {
            @Override
            public String apply(Map.Entry<ApplicationOptionOid, ImmutableSet<Eligibility>> input) {
                String pohjakoulutukset = joiner.join(Iterables.transform(input.getValue(), new Function<Eligibility, String>() {
                    @Override
                    public String apply(Eligibility input) {
                        return "'" + input.nimike + "'";
                    }
                }));
                return "hakukohde " + input.getKey().getValue() + " vaatii hakumaksun pohjakoulutusten " + pohjakoulutukset + " johdosta";
            }
        });

        String hakukohteet = joiner.join(requirements);

        addPaymentNote(application, new ApplicationNote("Hakija maksuvelvollinen: " + hakukohteet, new Date(), SYSTEM_USER));
    }

    private static boolean alreadyPaid(Optional<PaymentState> requiredPaymentState) {
        return requiredPaymentState.isPresent() && requiredPaymentState.get() == PaymentState.OK;
    }

    private static boolean requireResend(Application application, Optional<PaymentState> requiredPaymentState) {
        return requiredPaymentState.isPresent()
                && requiredPaymentState.get() == PaymentState.NOTIFIED
                && application.getRedoPostProcess() == Application.PostProcessingState.FULL;
    }

    private void paymentRequestOrThrow(PaymentEmail paymentEmail,
                                       SafeString emailAddress,
                                       ApplicationOid applicationOid,
                                       PersonOid personOid) throws ExecutionException, InterruptedException {
        if (!util.sendPaymentRequest(
                paymentEmail,
                urlConfiguration.url("oppijan-tunnistus.create"),
                urlConfiguration.url("hakuperusteet.tokenUrl." + paymentEmail.language.toString(), applicationOid),
                applicationOid,
                personOid,
                emailAddress).get()) {
            throw new IllegalStateException("Could not send payment processing request to oppijan-tunnistus: hakemusOid " +
                    applicationOid + ", personOid " + personOid + ", emailAddress " + emailAddress);
        }
    }
}
