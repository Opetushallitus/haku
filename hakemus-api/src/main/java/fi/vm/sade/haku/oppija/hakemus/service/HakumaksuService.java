package fi.vm.sade.haku.oppija.hakemus.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.BaseEducations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil.getPreferenceAoIds;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.AsciiCountryCode;

@Service
public class HakumaksuService {
    private final String koodistoServiceUrl;
    private final String koulutusinformaatioUrl;

    @Autowired
    RestClient restClient;

    @Autowired
    public HakumaksuService(
            @Value("${cas.service.koodisto-service}") final String koodistoServiceUrl,
            @Value("${koulutusinformaatio.ao.resource.url}") final String koulutusinformaatioUrl
    ) {
        this.koodistoServiceUrl = koodistoServiceUrl;
        this.koulutusinformaatioUrl = koulutusinformaatioUrl;
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
    }

    private final Predicate<Eligibility> onlyNonExempt = new Predicate<Eligibility>() {
        @Override
        public boolean apply(Eligibility kelpoisuus) {
            try {
                return !isExemptFromPayment(koodistoServiceUrl, kelpoisuus.suoritusmaa);
            } catch (ExecutionException e) {
                // TODO: log + let pass as our system is unexpectedly broken?
                return false;
            }
        }
    };

    private static Function<Application, List<Eligibility>> multipleChoiceKkEquals(final String value) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                Iterable<SuomalainenKorkeakoulutus> tasoaVastaavatKoulutukset = Iterables.filter(
                        SuomalainenKorkeakoulutus.of(application),
                        new Predicate<SuomalainenKorkeakoulutus>() {
                            @Override
                            public boolean apply(SuomalainenKorkeakoulutus input) {
                                return input.taso.equals(value);
                            }
                        });
                return toImmutable(Iterables.transform(tasoaVastaavatKoulutukset, new Function<SuomalainenKorkeakoulutus, Eligibility>() {
                    @Override
                    public Eligibility apply(SuomalainenKorkeakoulutus koulutus) {
                        return new Eligibility(koulutus.nimike);
                    }
                }));
            }
        };
    }

    private static Function<Application, List<Eligibility>> multipleChoiceKkUlkEquals(final String value) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                Iterable<UlkomaalainenKorkeakoulutus> tasoaVastaavatKoulutukset = Iterables.filter(
                        UlkomaalainenKorkeakoulutus.of(application),
                        new Predicate<UlkomaalainenKorkeakoulutus>() {
                            @Override
                            public boolean apply(UlkomaalainenKorkeakoulutus input) {
                                return input.taso.equals(value);
                            }
                        });
                return toImmutable(Iterables.transform(tasoaVastaavatKoulutukset, new Function<UlkomaalainenKorkeakoulutus, Eligibility>() {
                    @Override
                    public Eligibility apply(UlkomaalainenKorkeakoulutus koulutus) {
                        return new Eligibility(koulutus.nimike, koulutus.maa);
                    }
                }));
            }
        };
    }

    private static Function<Application, List<Eligibility>> suomalainenYo(final String value) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                return SuomalainenYo.of(application).transform(new Function<SuomalainenYo, List<Eligibility>>() {
                    @Override
                    public List<Eligibility> apply(SuomalainenYo koulutus) {
                        return value.equals(koulutus.tutkinto)
                                ? ImmutableList.of(new Eligibility(koulutus.tutkinto))
                                : ImmutableList.<Eligibility>of();
                    }
                }).or(ImmutableList.<Eligibility>of());
            }
        };
    }

    private static final Function<Application, List<Eligibility>> suomalainenYoAmmatillinen = new Function<Application, List<Eligibility>>() {
        @Override
        public List<Eligibility> apply(Application application) {
            return SuomalainenYoAmmatillinen.of(application).transform(new Function<SuomalainenYoAmmatillinen, List<Eligibility>>() {
                @Override
                public List<Eligibility> apply(SuomalainenYoAmmatillinen koulutus) {
                    return ImmutableList.of(new Eligibility(koulutus.nimike));
                }
            }).or(ImmutableList.<Eligibility>of());
        }
    };

    private static Function<Application, List<Eligibility>> suomalainenKansainvalinenYo(final String value) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                return SuomalainenKansainvalinenYo.of(application).transform(new Function<SuomalainenKansainvalinenYo, List<Eligibility>>() {
                    @Override
                    public List<Eligibility> apply(SuomalainenKansainvalinenYo koulutus) {
                        return value.equals(koulutus.tutkinto)
                                ? ImmutableList.of(new Eligibility(koulutus.tutkinto))
                                : ImmutableList.<Eligibility>of();
                    }
                }).or(ImmutableList.<Eligibility>of());
            }
        };
    }

    private static Function<Application, List<Eligibility>> ulkomainenKansainvalinenYo(final String value) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                return UlkomainenKansainvalinenYo.of(application).transform(new Function<UlkomainenKansainvalinenYo, List<Eligibility>>() {
                    @Override
                    public List<Eligibility> apply(UlkomainenKansainvalinenYo koulutus) {
                        return value.equals(koulutus.tutkinto)
                                ? ImmutableList.of(new Eligibility(koulutus.tutkinto, koulutus.maa))
                                : ImmutableList.<Eligibility>of();
                    }
                }).or(ImmutableList.<Eligibility>of());
            }
        };
    }

    private static <T> List<T> toImmutable(Iterable<T> it) {
        return ImmutableList.<T>builder().addAll(it).build();
    }

    private static final Function<Application, List<Eligibility>> pohjakoulutusUlkCheckbox =  new Function<Application, List<Eligibility>>() {
        @Override
        public List<Eligibility> apply(Application application) {
            return toImmutable(Iterables.transform(UlkomaalainenKoulutus.of(application), new Function<UlkomaalainenKoulutus, Eligibility>() {
                @Override
                public Eligibility apply(UlkomaalainenKoulutus koulutus) {
                    return new Eligibility(koulutus.nimike, koulutus.maa);
                }
            }));
        }
    };

    private static final Function<Application, List<Eligibility>> suomalainenAvoinTutkinto = new Function<Application, List<Eligibility>>() {
        @Override
        public List<Eligibility> apply(Application application) {
            return SuomalainenAvoinKoulutus.of(application).transform(new Function<SuomalainenAvoinKoulutus, List<Eligibility>>() {
                @Override
                public List<Eligibility> apply(SuomalainenAvoinKoulutus koulutus) {
                    return ImmutableList.of(new Eligibility(koulutus.nimike));
                }
            }).or(ImmutableList.<Eligibility>of());
        }
    };

    private static final Function<Application, List<Eligibility>> suomalainenAmtTutkinto = new Function<Application, List<Eligibility>>() {
        @Override
        public List<Eligibility> apply(Application application) {
            return SuomalainenAmtKoulutus.of(application).transform(new Function<SuomalainenAmtKoulutus, List<Eligibility>>() {
                @Override
                public List<Eligibility> apply(SuomalainenAmtKoulutus koulutus) {
                    return ImmutableList.of(new Eligibility(koulutus.nimike));
                }
            }).or(ImmutableList.<Eligibility>of());
        }
    };

    private static final Function<Application, List<Eligibility>> suomalainenAmTutkinto = new Function<Application, List<Eligibility>>() {
        @Override
        public List<Eligibility> apply(Application application) {
            return SuomalainenAmKoulutus.of(application).transform(new Function<SuomalainenAmKoulutus, List<Eligibility>>() {
                @Override
                public List<Eligibility> apply(SuomalainenAmKoulutus koulutus) {
                    return ImmutableList.of(new Eligibility(koulutus.nimike));
                }
            }).or(ImmutableList.<Eligibility>of());
        }
    };

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

    private static final Function<Application, List<Eligibility>> opistoTaiAmmatillisenKorkeaAsteenTutkinto = suomalainenAmTutkinto;
    private static final Function<Application, List<Eligibility>> ammattiTaiErikoisammattitutkinto = suomalainenAmtTutkinto;
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
            .put("pohjakoulutusvaatimuskorkeakoulut_104", suomalainenAmTutkinto)
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
            .put("pohjakoulutusvaatimuskorkeakoulut_114", pohjakoulutusUlkCheckbox)
            // Yleinen ammattikorkeakoulukelpoisuus
            .put("pohjakoulutusvaatimuskorkeakoulut_100", mergeEligibilities(
                    suomalaisenLukionOppimaaaraTaiYlioppilastutkinto,
                    opistoTaiAmmatillisenKorkeaAsteenTutkinto,
                    ammattiTaiErikoisammattitutkinto,
                    europeanBaccalaureateTutkinto,
                    internationalBaccalaureateTutkinto,
                    reifeprufungTutkinto,
                    pohjakoulutusUlkCheckbox))
            // Yleinen yliopistokelpoisuus
            .put("pohjakoulutusvaatimuskorkeakoulut_123", mergeEligibilities(
                    suomalainenYlioppilastutkinto,
                    europeanBaccalaureateTutkinto,
                    internationalBaccalaureateTutkinto,
                    reifeprufungTutkinto,
                    ammattiTaiErikoisammattitutkinto,
                    pohjakoulutusUlkCheckbox))
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
        ImmutableMap.Builder<ApplicationOptionOid, List<Eligibility>> maksullisetKelpoisuudet = ImmutableMap.builder();

        for (EducationRequirements applicationOptionRequirement : getEducationRequirements(koulutusinformaatioUrl, getPreferenceAoIds(application))) {
            ImmutableList.Builder<Eligibility> kelpoisuudet = ImmutableList.builder();

            for (String key : applicationOptionRequirement.baseEducationRequirements) {
                List<Eligibility> allKelpoisuudet = kkBaseEducationRequirements.get(key).apply(application);
                List<Eligibility> nonExempt = Lists.newLinkedList(Iterables.filter(allKelpoisuudet, onlyNonExempt));

                // Ei löytynyt yhtään maksusta vapauttavaa kelpoisuutta,
                // otetaan talteen logitusta / UI:ta varten
                if (allKelpoisuudet.size() == nonExempt.size()) {
                    kelpoisuudet.addAll(nonExempt);
                }
            }

            maksullisetKelpoisuudet.put(applicationOptionRequirement.applicationOptionId, kelpoisuudet.build());
        }

        return maksullisetKelpoisuudet.build();
    }

    public Application processPayment(Application application) throws ExecutionException {
        if (applicationSystemRequiresPaymentCheck(application)) {
            return application;
        }

        Map<ApplicationOptionOid, List<Eligibility>> paymentRequirements = paymentRequirements(application);
        // TODO: Audit/log reason for payment requirement, e.g. which hakukohde and what base education reason
        boolean isExemptFromPayment = paymentRequirements.size() == 0;
        System.err.println("Application " + application.getOid() + " is exempt from payment: " + isExemptFromPayment);
        return isExemptFromPayment ? application : markPaymentRequirements(application);
    }

    private static boolean applicationSystemRequiresPaymentCheck(Application application) {
        // TODO: Korkeakouluhaku + syksy 2016 ->
        return application.getApplicationSystemId() != null;
    }

    private static Application markPaymentRequirements(Application application) {
        // TODO: Aseta hakemukselle maksuvelvoite
        return application;
    }
}
