package fi.vm.sade.haku.oppija.hakemus.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.BaseEducations;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.haku.oppija.hakemus.domain.BaseEducations.*;
import static fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil.getPreferenceAoIds;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.AsciiCountryCode;

@Service
public class HakumaksuService {
    public static final Logger LOGGER = LoggerFactory.getLogger(HakumaksuService.class);

    private final String koodistoServiceUrl;
    private final String koulutusinformaatioUrl;
    private final HakumaksuUtil util;

    @Autowired
    public HakumaksuService(
            @Value("${cas.service.koodisto-service}") final String koodistoServiceUrl,
            @Value("${koulutusinformaatio.ao.resource.url}") final String koulutusinformaatioUrl,
            RestClient restClient
    ) {
        this.koodistoServiceUrl = koodistoServiceUrl;
        this.koulutusinformaatioUrl = koulutusinformaatioUrl;
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

    private static <T> List<T> toImmutable(Iterable<T> it) {
        return ImmutableList.<T>builder().addAll(it).build();
    }

    private static <T> Function<Application, List<Eligibility>> wrapSetWhere(final Function<Application, Set<T>> set,
                                                                             final Predicate<T> filter,
                                                                             final Function<T, Eligibility> transform) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                return toImmutable(Iterables.transform(Iterables.filter(set.apply(application), filter), transform));
            }
        };
    }

    private static <T> Function<Application, List<Eligibility>> wrapSet(final Function<Application, Set<T>> set, final Function<T, Eligibility> transform) {
        return wrapSetWhere(set, Predicates.<T>alwaysTrue(), transform);
    }

    private static Function<Application, List<Eligibility>> multipleChoiceKkEquals(final String value) {
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

    private static Function<Application, List<Eligibility>> multipleChoiceKkUlkEquals(final String value) {
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


    private static Function<Application, List<Eligibility>> suomalainenYo(final String value) {
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

    private static Function<Application, List<Eligibility>> suomalainenKansainvalinenYo(final String value) {
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

    private static Function<Application, List<Eligibility>> ulkomainenKansainvalinenYo(final String value) {
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

    private static final Function<Application, List<Eligibility>> ulkomainenPohjakoulutus = wrapSet(
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

    private static Function<Application, List<Eligibility>> mergeEligibilities(final Function<Application, List<Eligibility>>... validators) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                ImmutableList.Builder<Eligibility> results = ImmutableList.builder();
                for (Function<Application, List<Eligibility>> v : validators) {
                    results.addAll(v.apply(application));
                }
                return results.build();
            }
        };
    }

    private final static Function<Application, List<Eligibility>> ignore = new Function<Application, List<Eligibility>>() {
        @Override
        public List<Eligibility> apply(Application application) {
            return Lists.newArrayList();
        }
    };

    private static final Function<Application, List<Eligibility>> suomalainenYoAmmatillinen = wrapSet(SuomalainenYoAmmatillinen.of, HakumaksuService.<SuomalainenYoAmmatillinen>transformWithNimike());
    private static final Function<Application, List<Eligibility>> suomalainenAvoinTutkinto = wrapSet(SuomalainenAvoinKoulutus.of, HakumaksuService.<SuomalainenAvoinKoulutus>transformWithNimike());
    private static final Function<Application, List<Eligibility>> opistoTaiAmmatillisenKorkeaAsteenTutkinto = wrapSet(SuomalainenAmKoulutus.of, HakumaksuService.<SuomalainenAmKoulutus>transformWithNimike());
    private static final Function<Application, List<Eligibility>> ammattiTaiErikoisammattitutkinto = wrapSet(SuomalainenAmtKoulutus.of, HakumaksuService.<SuomalainenAmtKoulutus>transformWithNimike());
    private static final Function<Application, List<Eligibility>> suomalaisenLukionOppimaaaraTaiYlioppilastutkinto = mergeEligibilities(
            suomalainenYo("lk"),
            suomalainenYo("fi"),
            suomalainenYo("lkOnly"));
    private static final Function<Application,List<Eligibility>> europeanBaccalaureateTutkinto = mergeEligibilities(
            suomalainenKansainvalinenYo("eb"),
            ulkomainenKansainvalinenYo("eb"));
    private static final Function<Application,List<Eligibility>> internationalBaccalaureateTutkinto = mergeEligibilities(
            suomalainenKansainvalinenYo("ib"),
            ulkomainenKansainvalinenYo("ib"));
    private static final Function<Application,List<Eligibility>> suomalainenYlioppilastutkinto = mergeEligibilities(
            suomalainenYo("fi"),
            suomalainenYo("lkOnly"));
    private static final Function<Application,List<Eligibility>> reifeprufungTutkinto = mergeEligibilities(
            suomalainenKansainvalinenYo("rp"),
            ulkomainenKansainvalinenYo("rp"));

    /* Determine which ApplicationSystem fields fullfills the given base education requirements */
    // Pohjakoulutuskoodit: https://testi.virkailija.opintopolku.fi/koodisto-service/rest/codeelement/codes/pohjakoulutusvaatimuskorkeakoulut/1
    private static final ImmutableMap<String, Function<Application, List<Eligibility>>> kkBaseEducationRequirements = ImmutableMap.<String, Function<Application, List<Eligibility>>>builder()
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
    public Map<ApplicationOptionOid, List<Eligibility>> paymentRequirements(Application application) throws ExecutionException {
        ImmutableMap.Builder<ApplicationOptionOid, List<Eligibility>> applicationPaymentEligibilities = ImmutableMap.builder();

        for (HakumaksuUtil.EducationRequirements applicationOptionRequirement : util.getEducationRequirements(koulutusinformaatioUrl, getPreferenceAoIds(application))) {
            ImmutableList.Builder<Eligibility> aoPaymentEligibilityBuilder = ImmutableList.builder();
            boolean exemptingAoFound = false;

            for (String baseEducationRequirement : applicationOptionRequirement.baseEducationRequirements) {
                List<Eligibility> allEligibilities = kkBaseEducationRequirements.get(baseEducationRequirement).apply(application);
                List<Eligibility> paymentEligibilities = Lists.newLinkedList(Iterables.filter(allEligibilities, onlyNonExempt));

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

            List<Eligibility> aoPaymentEligibilities = exemptingAoFound ? ImmutableList.<Eligibility>of() : aoPaymentEligibilityBuilder.build();
            applicationPaymentEligibilities.put(applicationOptionRequirement.applicationOptionId, aoPaymentEligibilities);
        }

        return applicationPaymentEligibilities.build();
    }

    public boolean isPaymentRequired(Application application) throws ExecutionException {
        final Map<Types.ApplicationOptionOid, List<HakumaksuService.Eligibility>> requirements = paymentRequirements(application);
        for (final List<HakumaksuService.Eligibility> eligibilities : requirements.values()) {
            if (!eligibilities.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public Application processPayment(Application application) throws ExecutionException {
        if (!applicationSystemRequiresPaymentCheck(application)) {
            return application;
        }

        Map<ApplicationOptionOid, List<Eligibility>> paymentRequirements = paymentRequirements(application);
        // TODO: Audit/log reason for payment requirement, e.g. which hakukohde and what base education reason
        boolean isExemptFromPayment = paymentRequirements.size() == 0;
        LOGGER.info("Application " + application.getOid() + " payment requirements: " + (isExemptFromPayment ? "none" : paymentRequirements));
        return isExemptFromPayment ? application : markPaymentRequirements(application);
    }

    private static boolean applicationSystemRequiresPaymentCheck(Application application) {
        // TODO: Korkeakouluhaku + syksy 2016 ->
        return application != null;
    }

    private static Application markPaymentRequirements(Application application) {
        // TODO: Aseta hakemukselle maksuvelvoite
        return application;
    }
}
