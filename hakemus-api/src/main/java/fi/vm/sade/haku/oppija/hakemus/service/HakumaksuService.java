package fi.vm.sade.haku.oppija.hakemus.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil.getPreferenceAoIds;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.*;

@Service
public class HakumaksuService {
    private final String koodistoServiceUrl;
    private final String koulutusinformaatioUrl;

    @Autowired
    public HakumaksuService(
            @Value("${cas.service.koodisto-service}") final String koodistoServiceUrl,
            @Value("${koulutusinformaatio.ao.resource.url}") final String koulutusinformaatioUrl
    ) {
        this.koodistoServiceUrl = koodistoServiceUrl;
        this.koulutusinformaatioUrl = koulutusinformaatioUrl;
    }

    static class Eligibility {
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

    private static Function<Application, List<Eligibility>> multipleChoiceKkUlkEquals(final String multipleChoiceField, final String value) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                String taso = baseEducation.get(multipleChoiceField + "_taso");
                AsciiCountryCode maa = AsciiCountryCode.of(baseEducation.get(multipleChoiceField + "_maa"));
                String nimike = baseEducation.get(multipleChoiceField + "_nimike");
                return value.equals(taso)
                        ? Lists.newArrayList(new Eligibility(nimike, maa))
                        : Lists.<Eligibility>newArrayList();
            }
        };
    }

    private static Function<Application, List<Eligibility>> multipleChoiceKkEquals(final String multipleChoiceField, final String value) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                String taso = baseEducation.get(multipleChoiceField + "_taso");
                String nimike = baseEducation.get(multipleChoiceField + "_nimike");
                return value.equals(taso)
                        ? Lists.newArrayList(new Eligibility(nimike))
                        : Lists.<Eligibility>newArrayList();
            }
        };
    }

    private static final Function<Application, List<Eligibility>> pohjakoulutusUlkCheckbox =  new Function<Application, List<Eligibility>>() {
        @Override
        public List<Eligibility> apply(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            AsciiCountryCode maa = AsciiCountryCode.of(baseEducation.get("pohjakoulutus_ulk_suoritusmaa"));
            return "true".equals(baseEducation.get("pohjakoulutus_ulk"))
                    ? Lists.newArrayList(new Eligibility(baseEducation.get("pohjakoulutus_ulk_nimike"), maa))
                    : Lists.<Eligibility>newArrayList();
        }
    };

    private static Function<Application, List<Eligibility>> checkboxSelected(final String fieldName, final String descriptionField) {
        return new Function<Application, List<Eligibility>>() {
            @Override
            public List<Eligibility> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                return "true".equals(baseEducation.get(fieldName))
                        ? Lists.newArrayList(new Eligibility(baseEducation.get(descriptionField)))
                        : Lists.<Eligibility>newArrayList();
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

    private static final Function<Application, List<Eligibility>> opistoTaiAmmatillisenKorkeaAsteenTutkinto = checkboxSelected("pohjakoulutus_am", "pohjakoulutus_am_nimike");
    private static final Function<Application, List<Eligibility>> ammattiTaiErikoisammattitutkinto = checkboxSelected("pohjakoulutus_amt", "pohjakoulutus_amt_nimike");
    private static final Function<Application, List<Eligibility>> suomalaisenLukionOppimaaaraTaiYlioppilastutkinto = mergeEligibilities(
            multipleChoiceKkEquals("pohjakoulutusYo", "lk"),
            multipleChoiceKkEquals("pohjakoulutusYo", "fi"),
            multipleChoiceKkEquals("pohjakoulutusYo", "lkOnly"));
    private static final Function<Application,List<Eligibility>> europeanBaccalaureateTutkinto = mergeEligibilities(
            multipleChoiceKkEquals("pohjakoulutus_yo_kansainvalinen_suomessa", "eb"),
            multipleChoiceKkEquals("pohjakoulutus_yo_ulkomainen_tutkinto", "eb"));
    private static final Function<Application,List<Eligibility>> internationalBaccalaureateTutkinto = mergeEligibilities(
            multipleChoiceKkEquals("pohjakoulutus_yo_kansainvalinen_suomessa", "ib"),
            multipleChoiceKkEquals("pohjakoulutus_yo_ulkomainen_tutkinto", "ib"));
    private static final Function<Application,List<Eligibility>> suomalainenYlioppilastutkinto = mergeEligibilities(
            multipleChoiceKkEquals("pohjakoulutusYo", "fi"),
            multipleChoiceKkEquals("pohjakoulutusYo", "lkOnly"));
    private static final Function<Application,List<Eligibility>> reifeprufungTutkinto = mergeEligibilities(
            multipleChoiceKkEquals("pohjakoulutus_yo_kansainvalinen_suomessa", "rp"),
            multipleChoiceKkEquals("pohjakoulutus_yo_ulkomainen_tutkinto", "rp"));

    /* Determine which ApplicationSystem fields fullfills the given base education requirements */
    // Pohjakoulutuskoodit: https://testi.virkailija.opintopolku.fi/koodisto-service/rest/codeelement/codes/pohjakoulutusvaatimuskorkeakoulut/1
    private static final ImmutableMap<String, Function<Application, List<Eligibility>>> kkBaseEducationRequirements = ImmutableMap.<String, Function<Application, List<Eligibility>>>builder()
            // Ylempi korkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_103", multipleChoiceKkEquals("pohjakoulutus_kk", "1"))
            // Ylempi ammattikorkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_119", multipleChoiceKkEquals("pohjakoulutus_kk", "3"))
            // Ulkomainen korkeakoulututkinto (Master)
            .put("pohjakoulutusvaatimuskorkeakoulut_117", mergeEligibilities(
                    multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "3"),
                    multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "4")))
            // Ulkomainen korkeakoulututkinto (Bachelor)
            .put("pohjakoulutusvaatimuskorkeakoulut_116", mergeEligibilities(
                    multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "1"),
                    multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "2")))
            // Lisensiaatin tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_120", mergeEligibilities(
                    multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "5"),
                    multipleChoiceKkEquals("pohjakoulutus_kk", "5")))
            // Alempi korkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_102", multipleChoiceKkEquals("pohjakoulutus_kk", "2"))
            // Ammattikorkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_101", multipleChoiceKkUlkEquals("pohjakoulutus_kk", "1"))
            // Ammatillinen perustutkinto tai vastaava aikaisempi tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_104", checkboxSelected("pohjakoulutus_am", "pohjakoulutus_am_nimike"))
            // Ammatti- tai erikoisammattitutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_105", ammattiTaiErikoisammattitutkinto)
            // Avoimen ammattikorkeakoulun opinnot
            .put("pohjakoulutusvaatimuskorkeakoulut_115", checkboxSelected("pohjakoulutus_avoin", "pohjakoulutus_avoin_kokonaisuus"))
            // Avoimen yliopiston opinnot
            .put("pohjakoulutusvaatimuskorkeakoulut_118", checkboxSelected("pohjakoulutus_avoin", "pohjakoulutus_avoin_kokonaisuus"))
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
                    multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "5"),
                    multipleChoiceKkEquals("pohjakoulutus_kk", "5")))
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
            .put("pohjakoulutusvaatimuskorkeakoulut_107", checkboxSelected("pohjakoulutus_yo_ammatillinen", "pohjakoulutus_yo_tutkinto"))
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
