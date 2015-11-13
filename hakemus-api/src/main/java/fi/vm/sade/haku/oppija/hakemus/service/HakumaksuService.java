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

    @Autowired
    public HakumaksuService(@Value("${cas.service.koodisto-service}") final String koodistoServiceUrl) {
        this.koodistoServiceUrl = koodistoServiceUrl;
    }

    static class Eligibility {
        String nimike;
        AsciiCountryCode suoritusmaa;

        public Eligibility(String nimike, AsciiCountryCode suoritusmaa) {
            this.nimike = nimike;
            this.suoritusmaa = suoritusmaa;
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

    /* Determine which ApplicationSystem fields fullfills the given base education requirements */
    private static final ImmutableMap<String, Function<Application, List<Eligibility>>> kkBaseEducationRequirements = ImmutableMap.<String, Function<Application, List<Eligibility>>>builder()
            // Ylempi korkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_103", multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "1"))
            // Ulkomainen korkeakoulututkinto (Bachelor)
            .put("pohjakoulutusvaatimuskorkeakoulut_116", multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "2"))
            // Ylempi ammattikorkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_119", multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "3"))
            // Ulkomainen korkeakoulututkinto (Master)
            .put("pohjakoulutusvaatimuskorkeakoulut_117", multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "4"))
            // Lisensiaatin tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_120", multipleChoiceKkUlkEquals("pohjakoulutus_kk_ulk", "5"))

            // Alempi korkeakoulututkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_102", multipleChoiceKkUlkEquals("", ""))
            // Ylioppilastutkinto ja ammatillinen perustutkinto (120 ov)
            .put("pohjakoulutusvaatimuskorkeakoulut_107", multipleChoiceKkUlkEquals("", ""))
            // Suomalainen ylioppilastutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_109", multipleChoiceKkUlkEquals("", ""))
            // Opisto- tai ammatillisen korkea-asteen tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_108", multipleChoiceKkUlkEquals("", ""))
            // Yrkeshögskoleexamen
            .put("pohjakoulutusvaatimuskorkeakoulut_101", multipleChoiceKkUlkEquals("", ""))
            // Avoimen yliopiston opinnot
            .put("pohjakoulutusvaatimuskorkeakoulut_118", multipleChoiceKkUlkEquals("", ""))
            // Tohtorin tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_121", multipleChoiceKkUlkEquals("", ""))
            // Avoimen ammattikorkeakoulun opinnot
            .put("pohjakoulutusvaatimuskorkeakoulut_115", multipleChoiceKkUlkEquals("", ""))
            // Ammatti- tai erikoisammattitutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_105", multipleChoiceKkUlkEquals("", ""))
            // Reifeprüfung-tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_111", multipleChoiceKkUlkEquals("", ""))
            // Yleinen ammattikorkeakoulukelpoisuus
            .put("pohjakoulutusvaatimuskorkeakoulut_100", multipleChoiceKkUlkEquals("", ""))
            // Harkinnanvaraisuus tai erivapaus
            .put("pohjakoulutusvaatimuskorkeakoulut_106", multipleChoiceKkUlkEquals("", ""))
            // Suomalaisen lukion oppimÃ¤Ã¤rÃ¤ tai ylioppilastutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_122", multipleChoiceKkUlkEquals("", ""))
            // Ulkomainen toisen asteen tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_114", multipleChoiceKkUlkEquals("", ""))
            // European baccalaureate -tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_110", multipleChoiceKkUlkEquals("", ""))
            // Yleinen yliopistokelpoisuus
            .put("pohjakoulutusvaatimuskorkeakoulut_123", multipleChoiceKkUlkEquals("", ""))
            // Ammatillinen perustutkinto tai vastaava aikaisempi tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_104", multipleChoiceKkUlkEquals("", ""))
            // International Baccalaureate -tutkinto
            .put("pohjakoulutusvaatimuskorkeakoulut_112", multipleChoiceKkUlkEquals("", ""))
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

        for (EducationRequirements applicationOptionRequirement : getEducationRequirements(getPreferenceAoIds(application))) {
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

            maksullisetKelpoisuudet.put(ApplicationOptionOid.of(applicationOptionRequirement.applicationOptionId), kelpoisuudet.build());
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
