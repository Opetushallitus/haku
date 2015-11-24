package fi.vm.sade.haku.oppija.hakemus.service;

import com.google.common.base.*;
import com.google.common.collect.*;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PaymentState;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.BaseEducations;
import fi.vm.sade.haku.oppija.lomake.exception.IllegalStateException;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.EducationRequirements;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.LanguageCodeISO6391;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;
import static fi.vm.sade.haku.oppija.hakemus.domain.BaseEducations.*;
import static fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil.getPreferenceAoIds;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.LanguageCodeISO6391.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.*;

@Service
public class HakumaksuService {
    public static final Logger LOGGER = LoggerFactory.getLogger(HakumaksuService.class);

    private final String koodistoServiceUrl;
    private final String koulutusinformaatioUrl;
    private final HakumaksuUtil util;
    private final String oppijanTunnistusUrl;

    private final ImmutableMap<LanguageCodeISO6391, String> languageCodeToServiceUrlMap;

    @Autowired
    public HakumaksuService(
            @Value("${cas.service.koodisto-service}") final String koodistoServiceUrl,
            @Value("${koulutusinformaatio.ao.resource.url}") final String koulutusinformaatioUrl,
            @Value("${oppijantunnistus.create.url}") final String oppijanTunnistusUrl,
            @Value("${hakuperusteet.url.fi}") final String hakuperusteetUrlFi,
            @Value("${hakuperusteet.url.sv}") final String hakuperusteetUrlSv,
            @Value("${hakuperusteet.url.en}") final String hakuperusteetUrlEn,
            RestClient restClient
    ) {
        this.koodistoServiceUrl = koodistoServiceUrl;
        this.koulutusinformaatioUrl = koulutusinformaatioUrl;
        this.oppijanTunnistusUrl = oppijanTunnistusUrl;

        this.languageCodeToServiceUrlMap = ImmutableMap.of(
                fi, hakuperusteetUrlFi,
                sv, hakuperusteetUrlSv,
                en, hakuperusteetUrlEn
        );

        util = new HakumaksuUtil(restClient);
    }

    public static class Eligibility {
        String nimike;
        AsciiCountryCode suoritusmaa;

        public Eligibility(String nimike, AsciiCountryCode suoritusmaa) {
            this.nimike = nimike;
            this.suoritusmaa = suoritusmaa;
        }

        // Suomalainen koulutus
        public Eligibility(String nimike) {
            this(nimike, AsciiCountryCode.of("FIN"));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Eligibility that = (Eligibility) o;

            if (nimike != null ? !nimike.equals(that.nimike) : that.nimike != null)
                return false;
            return !(suoritusmaa != null ? !suoritusmaa.equals(that.suoritusmaa) : that.suoritusmaa != null);

        }

        @Override
        public int hashCode() {
            int result = nimike != null ? nimike.hashCode() : 0;
            result = 31 * result + (suoritusmaa != null ? suoritusmaa.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Eligibility{" +
                    "nimike='" + nimike + '\'' +
                    ", suoritusmaa=" + suoritusmaa +
                    '}';
        }
    }

    private final Predicate<Eligibility> onlyNonExempt = new Predicate<Eligibility>() {
        @Override
        public boolean apply(Eligibility kelpoisuus) {
            try {
                return !util.isExemptFromPayment(koodistoServiceUrl, kelpoisuus.suoritusmaa);
            } catch (ExecutionException e) {
                // TODO: log + let pass as our system is unexpectedly broken?
                return false;
            }
        }
    };

    private static <T> ImmutableSet<T> toImmutable(Iterable<T> it) {
        return ImmutableSet.<T>builder().addAll(it).build();
    }

    private static <T> Function<MergedAnswers, ImmutableSet<Eligibility>> wrapSetWhere(final Function<MergedAnswers, ImmutableSet<T>> set,
                                                                               final Predicate<T> filter,
                                                                               final Function<T, Eligibility> transform) {
        return new Function<MergedAnswers, ImmutableSet<Eligibility>>() {
            @Override
            public ImmutableSet<Eligibility> apply(MergedAnswers answers) {
                return toImmutable(Iterables.transform(filter(set.apply(answers), filter), transform));
            }
        };
    }

    private static <T> Function<MergedAnswers, ImmutableSet<Eligibility>> wrapSet(final Function<MergedAnswers, ImmutableSet<T>> set, final Function<T, Eligibility> transform) {
        return wrapSetWhere(set, Predicates.<T>alwaysTrue(), transform);
    }

    private static Function<MergedAnswers, ImmutableSet<Eligibility>> multipleChoiceKkEquals(final String value) {
        return wrapSetWhere(
                SuomalainenKorkeakoulutus.of,
                new Predicate<SuomalainenKorkeakoulutus>() {
                    @Override
                    public boolean apply(BaseEducations.SuomalainenKorkeakoulutus input) {
                        return input.taso.equals(value);
                    }
                },
                HakumaksuService.<BaseEducations.SuomalainenKorkeakoulutus>transformWithNimike());
    }

    private static Function<MergedAnswers, ImmutableSet<Eligibility>> multipleChoiceKkUlkEquals(final String value) {
        return wrapSetWhere(
                UlkomaalainenKorkeakoulutus.of,
                new Predicate<UlkomaalainenKorkeakoulutus>() {
                    @Override
                    public boolean apply(UlkomaalainenKorkeakoulutus input) {
                        return input.taso.equals(value);
                    }
                },
                new Function<UlkomaalainenKorkeakoulutus, Eligibility>() {
                    @Override
                    public Eligibility apply(UlkomaalainenKorkeakoulutus koulutus) {
                        return new Eligibility(koulutus.nimike, koulutus.maa);
                    }
                });
    }


    private static Function<MergedAnswers, ImmutableSet<Eligibility>> suomalainenYo(final String value) {
        return wrapSetWhere(
                SuomalainenYo.of,
                new Predicate<SuomalainenYo>() {
                    @Override
                    public boolean apply(SuomalainenYo input) {
                        return input.tutkinto.equals(value);
                    }
                },
                new Function<SuomalainenYo, Eligibility>() {
                    @Override
                    public Eligibility apply(SuomalainenYo koulutus) {
                        return new Eligibility(koulutus.tutkinto);
                    }
                });
    }

    private static Function<MergedAnswers, ImmutableSet<Eligibility>> suomalainenKansainvalinenYo(final String value) {
        return wrapSetWhere(
                SuomalainenKansainvalinenYo.of,
                new Predicate<SuomalainenKansainvalinenYo>() {
                    @Override
                    public boolean apply(SuomalainenKansainvalinenYo input) {
                        return input.tutkinto.equals(value);
                    }
                },
                new Function<SuomalainenKansainvalinenYo, Eligibility>() {
                    @Override
                    public Eligibility apply(SuomalainenKansainvalinenYo koulutus) {
                        return new Eligibility(koulutus.tutkinto);
                    }
                });
    }

    private static Function<MergedAnswers, ImmutableSet<Eligibility>> ulkomainenKansainvalinenYo(final String value) {
        return wrapSetWhere(
                UlkomainenKansainvalinenYo.of,
                new Predicate<UlkomainenKansainvalinenYo>() {
                    @Override
                    public boolean apply(UlkomainenKansainvalinenYo input) {
                        return input.tutkinto.equals(value);
                    }
                },
                new Function<UlkomainenKansainvalinenYo, Eligibility>() {
                    @Override
                    public Eligibility apply(UlkomainenKansainvalinenYo koulutus) {
                        return new Eligibility(koulutus.tutkinto, koulutus.maa);
                    }
                });
    }

    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> ulkomainenPohjakoulutus = wrapSet(
            UlkomaalainenKoulutus.of,
            new Function<UlkomaalainenKoulutus, Eligibility>() {
                @Override
                public Eligibility apply(UlkomaalainenKoulutus koulutus) {
                    return new Eligibility(koulutus.nimike, koulutus.maa);
                }
            });

    private static <T extends ProvideNimike> Function<T, Eligibility> transformWithNimike() {
        return new Function<T, Eligibility>() {
            @Override
            public Eligibility apply(T koulutus) {
                return new Eligibility(koulutus.getNimike());
            }
        };
    }

    private static Function<MergedAnswers, ImmutableSet<Eligibility>> mergeEligibilities(final Function<MergedAnswers, ImmutableSet<Eligibility>>... validators) {
        return new Function<MergedAnswers, ImmutableSet<Eligibility>>() {
            @Override
            public ImmutableSet<Eligibility> apply(MergedAnswers answers) {
                ImmutableSet.Builder<Eligibility> results = ImmutableSet.builder();
                for (Function<MergedAnswers, ImmutableSet<Eligibility>> v : validators) {
                    results.addAll(v.apply(answers));
                }
                return results.build();
            }
        };
    }

    private final static Function<MergedAnswers, ImmutableSet<Eligibility>> ignore = new Function<MergedAnswers, ImmutableSet<Eligibility>>() {
        @Override
        public ImmutableSet<Eligibility> apply(MergedAnswers answers) {
            return ImmutableSet.of();
        }
    };

    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> suomalainenYoAmmatillinen = wrapSet(SuomalainenYoAmmatillinen.of, HakumaksuService.<SuomalainenYoAmmatillinen>transformWithNimike());
    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> suomalainenAvoinTutkinto = wrapSet(SuomalainenAvoinKoulutus.of, HakumaksuService.<SuomalainenAvoinKoulutus>transformWithNimike());
    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> opistoTaiAmmatillisenKorkeaAsteenTutkinto = wrapSet(SuomalainenAmKoulutus.of, HakumaksuService.<SuomalainenAmKoulutus>transformWithNimike());
    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> ammattiTaiErikoisammattitutkinto = wrapSet(SuomalainenAmtKoulutus.of, HakumaksuService.<SuomalainenAmtKoulutus>transformWithNimike());
    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> suomalaisenLukionOppimaaaraTaiYlioppilastutkinto = mergeEligibilities(
            suomalainenYo("lk"),
            suomalainenYo("fi"),
            suomalainenYo("lkOnly"));
    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> europeanBaccalaureateTutkinto = mergeEligibilities(
            suomalainenKansainvalinenYo("eb"),
            ulkomainenKansainvalinenYo("eb"));
    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> internationalBaccalaureateTutkinto = mergeEligibilities(
            suomalainenKansainvalinenYo("ib"),
            ulkomainenKansainvalinenYo("ib"));
    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> suomalainenYlioppilastutkinto = mergeEligibilities(
            suomalainenYo("fi"),
            suomalainenYo("lkOnly"));
    private static final Function<MergedAnswers, ImmutableSet<Eligibility>> reifeprufungTutkinto = mergeEligibilities(
            suomalainenKansainvalinenYo("rp"),
            ulkomainenKansainvalinenYo("rp"));

    /* Determine which ApplicationSystem fields fullfills the given base education requirements */
    // Pohjakoulutuskoodit: https://testi.virkailija.opintopolku.fi/koodisto-service/rest/codeelement/codes/pohjakoulutusvaatimuskorkeakoulut/1
    private static final ImmutableMap<String, Function<MergedAnswers, ImmutableSet<Eligibility>>> kkBaseEducationRequirements = ImmutableMap.<String, Function<MergedAnswers, ImmutableSet<Eligibility>>>builder()
            // Ylempi korkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_103", multipleChoiceKkEquals("4"))
            // Ylempi ammattikorkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_119", multipleChoiceKkEquals("3"))
            // Ulkomainen korkeakoulututkinto (Master)
            .put("pohjakoulutusvaatimuskorkeakoulut_117", mergeEligibilities(
                    multipleChoiceKkUlkEquals("3"),
                    multipleChoiceKkUlkEquals("4")))
            // Ulkomainen korkeakoulututkinto (Bachelor)
            .put("pohjakoulutusvaatimuskorkeakoulut_116", mergeEligibilities(
                    multipleChoiceKkUlkEquals("1"),
                    multipleChoiceKkUlkEquals("2")))
            // Lisensiaatin tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_120", mergeEligibilities(
                    multipleChoiceKkUlkEquals("5"),
                    multipleChoiceKkEquals("5")))
            // Alempi korkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_102", multipleChoiceKkEquals("2"))
            // Ammattikorkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_101", multipleChoiceKkEquals("1"))
            // Ammatillinen perustutkinto tai vastaava aikaisempi tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_104", opistoTaiAmmatillisenKorkeaAsteenTutkinto)
            // Ammatti- tai erikoisammattitutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_105", ammattiTaiErikoisammattitutkinto)
            // Avoimen ammattikorkeakoulun opinnot
            .put("pohjakoulutusvaatimuskorkeakoulut_115", suomalainenAvoinTutkinto)
            // Avoimen yliopiston opinnot
            .put("pohjakoulutusvaatimuskorkeakoulut_118", suomalainenAvoinTutkinto)
            // European baccalaureate -tutkinto
            // TODO: tällä ei ole _nimike-kenttää
            .put("pohjakoulutusvaatimuskorkeakoulut_110", europeanBaccalaureateTutkinto)
            // Harkinnanvaraisuus tai erivapaus
            .put("pohjakoulutusvaatimuskorkeakoulut_106", ignore)
            // International Baccalaureate -tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_112", internationalBaccalaureateTutkinto)
            // Opisto- tai ammatillisen korkea-asteen tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_108", opistoTaiAmmatillisenKorkeaAsteenTutkinto)
            // Reifeprüfung-tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_111", reifeprufungTutkinto)
            // Suomalainen ylioppilastutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_109", suomalainenYlioppilastutkinto)
            // Suomalaisen lukion oppimäärä tai ylioppilastutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_122", suomalaisenLukionOppimaaaraTaiYlioppilastutkinto)
            // Tohtorin tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_121", mergeEligibilities(
                    multipleChoiceKkUlkEquals("5"),
                    multipleChoiceKkEquals("5")))
            // Ulkomainen toisen asteen tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_114", ulkomainenPohjakoulutus)
            // Yleinen ammattikorkeakoulukelpoisuus
            .put("pohjakoulutusvaatimuskorkeakoulut_100", mergeEligibilities(
                    suomalaisenLukionOppimaaaraTaiYlioppilastutkinto,
                    opistoTaiAmmatillisenKorkeaAsteenTutkinto,
                    ammattiTaiErikoisammattitutkinto,
                    europeanBaccalaureateTutkinto,
                    internationalBaccalaureateTutkinto,
                    reifeprufungTutkinto,
                    ulkomainenPohjakoulutus))
            // Yleinen yliopistokelpoisuus
            .put("pohjakoulutusvaatimuskorkeakoulut_123", mergeEligibilities(
                    suomalainenYlioppilastutkinto,
                    europeanBaccalaureateTutkinto,
                    internationalBaccalaureateTutkinto,
                    reifeprufungTutkinto,
                    ammattiTaiErikoisammattitutkinto,
                    ulkomainenPohjakoulutus))
            // Ylioppilastutkinto ja ammatillinen perustutkinto (120 ov)
            .put("pohjakoulutusvaatimuskorkeakoulut_107", suomalainenYoAmmatillinen)
            .build();

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
    public ImmutableMap<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements(MergedAnswers answers) throws ExecutionException {
        ImmutableMap.Builder<ApplicationOptionOid, ImmutableSet<Eligibility>> applicationPaymentEligibilities = ImmutableMap.builder();

        for (EducationRequirements applicationOptionRequirement : util.getEducationRequirements(koulutusinformaatioUrl, getPreferenceAoIds(answers))) {
            ImmutableSet.Builder<Eligibility> aoPaymentEligibilityBuilder = ImmutableSet.builder();
            boolean exemptingAoFound = false;

            for (String baseEducationRequirement : applicationOptionRequirement.baseEducationRequirements) {
                ImmutableSet<Eligibility> allEligibilities = kkBaseEducationRequirements.get(baseEducationRequirement).apply(answers);
                ImmutableSet<Eligibility> paymentEligibilities = ImmutableSet.copyOf(filter(allEligibilities, onlyNonExempt));

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
            applicationPaymentEligibilities.put(applicationOptionRequirement.applicationOptionId, aoPaymentEligibilities);
        }

        return applicationPaymentEligibilities.build();
    }

    public boolean isPaymentRequired(MergedAnswers mergedAnswers) throws ExecutionException {
        return any(paymentRequirements(mergedAnswers).values(), new Predicate<Set<Eligibility>>() {
            @Override
            public boolean apply(Set<Eligibility> input) {
                return !input.isEmpty();
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

    public Application processPayment(Application application) throws ExecutionException, InterruptedException {
        Optional<PaymentState> requiredPaymentState = Optional.fromNullable(application.getRequiredPaymentState());
        if (alreadyPaid(requiredPaymentState)) {
            return application;
        } else {
            Map<ApplicationOptionOid, ImmutableSet<Eligibility>> paymentRequirements = paymentRequirements(MergedAnswers.of(application));

            boolean exemptFromPayment = isExemptFromPayment(paymentRequirements);

            // Tullut maksuvelvolliseksi tai pyydetty maksulinkin uudelleenlähetystä
            if (!exemptFromPayment
                    && (!requiredPaymentState.isPresent() || requireResend(application, requiredPaymentState))) {

                Oid applicationOid = Oid.of(application.getOid());
                paymentRequestOrThrow(
                        SafeString.of(application.getPhaseAnswers(OppijaConstants.PHASE_PERSONAL).get("Sähköposti")),
                        SafeString.of(application.getPhaseAnswers(OppijaConstants.PHASE_MISC).get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE)),
                        applicationOid,
                        Oid.of(application.getPersonOid()));

                application.setRequiredPaymentState(PaymentState.NOTIFIED);

                if (!requiredPaymentState.isPresent() && application.getRequiredPaymentState() == PaymentState.NOTIFIED) {
                    addPaymentRequiredNote(application, paymentRequirements);
                }

                // TODO: Audit/log reason for payment requirement, e.g. which hakukohde and what base education reason
                LOGGER.info("Marked application " + applicationOid + " payment requirements: " + paymentRequirements + ", payment state: " + application.getRequiredPaymentState());

                return application;
            } else {
                if (exemptFromPayment && requiredPaymentState.isPresent()) {
                    addPaymentNote(application, new ApplicationNote("Hakija ei enää maksuvelvollinen", new Date(), "järjestelmä"));

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

        addPaymentNote(application, new ApplicationNote("Hakija maksuvelvollinen: " + hakukohteet, new Date(), "järjestelmä"));
    }

    private static boolean alreadyPaid(Optional<PaymentState> requiredPaymentState) {
        return requiredPaymentState.isPresent() && requiredPaymentState.get() == PaymentState.OK;
    }

    private static boolean requireResend(Application application, Optional<PaymentState> requiredPaymentState) {
        return requiredPaymentState.isPresent()
                && requiredPaymentState.get() == PaymentState.NOTIFIED
                && application.getRedoPostProcess() == Application.PostProcessingState.FULL;
    }

    static final ImmutableMap<String, LanguageCodeISO6391> applicationLanguageToLanguageCodeMap = ImmutableMap.of(
            "suomi", fi,
            "ruotsi", sv,
            "englanti", en
    );

    private LanguageCodeISO6391 languageCodeFromApplication(SafeString contactLanguage) {
        return Optional.fromNullable(applicationLanguageToLanguageCodeMap.get(contactLanguage.getValue())).or(en);
    }

    private void paymentRequestOrThrow(SafeString emailAddress,
                                       SafeString contactLanguage,
                                       Oid applicationOid,
                                       Oid personOid) throws ExecutionException, InterruptedException {
        LanguageCodeISO6391 languageCode = languageCodeFromApplication(contactLanguage);
        if (!util.sendPaymentRequest(
                oppijanTunnistusUrl,
                getServiceUrl(applicationOid, languageCode),
                languageCode,
                applicationOid,
                personOid,
                emailAddress).get()) {
            throw new IllegalStateException("Could not send payment processing request to oppijan-tunnistus: hakemusOid " +
                    applicationOid + ", personOid " + personOid + ", emailAddress " + emailAddress);
        }
    }

    private String getServiceUrl(Oid applicationOid, LanguageCodeISO6391 languageCode) {
        return languageCodeToServiceUrlMap.get(languageCode) + "/app/" + applicationOid + "#/token/";
    }
}
