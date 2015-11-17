package fi.vm.sade.haku.oppija.hakemus;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static fi.vm.sade.haku.oppija.hakemus.Hakukelpoisuusvaatimus.*;
import static fi.vm.sade.haku.oppija.hakemus.Pohjakoulutus.*;

public class TestApplicationData {

    // hakukelpoisuusvaatimus -> lista maksusta vapauttavista pohjakoulutuksista
    public static final Map<Hakukelpoisuusvaatimus, List<Pohjakoulutus>> exemptions = ImmutableMap.<Hakukelpoisuusvaatimus, List<Pohjakoulutus>>builder()
            .put(ALEMPI_KORKEAKOULUTUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_ALEMPI_YLIOPISTOTUTKINTO_KANDIDAATTI))
            .put(AMMATILLINEN_PERUSTUTKINTO_TAI_VASTAAVA_AIKAISEMPI_TUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_AMMATILLINEN_PERUSTUTKINTO_KOULUASTEEN_OPISTOASTEEN_TAI_AMMATILLISEN_KORKEA_ASTEEN_TUTKINTO))
            .put(AMMATTI_TAI_ERIKOISAMMATTITUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_AMMATTI_TAI_ERIKOISAMMATTITUTKINTO))
            .put(AMMATTIKORKEAKOULUTUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_AMMATTIKORKEAKOULUTUTKINTO))
            .put(AVOIMEN_AMMATTIKORKEAKOULUN_OPINNOT,
                    ImmutableList.of(KORKEAKOULUN_EDELLYTTAMAT_AVOIMEN_KORKEAKOULUN_OPINNOT))
            .put(AVOIMEN_YLIOPISTON_OPINNOT,
                    ImmutableList.of(KORKEAKOULUN_EDELLYTTAMAT_AVOIMEN_KORKEAKOULUN_OPINNOT))
            .put(EUROPEAN_BACCALAUREATE_TUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_EUROPEAN_BACCALAUREATE_EB_TUTKINTO, MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_EUROPEAN_BACCALAUREATE_EB_TUTKINTO))
            .put(INTERNATIONAL_BACCALAUREATE_TUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_INTERNATIONAL_BACCALAUREATE_IB_TUTKINTO, MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_INTERNATIONAL_BACCALAUREATE_IB_TUTKINTO))
            .put(LISENSIAATIN_TUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_LISENSIAATTI_TOHTORI, MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_LISENSIAATTI_TOHTORI_ETA_TAI_SVEITSI))
            .put(OPISTO_TAI_AMMATILLISEN_KORKEA_ASTEEN_TUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_AMMATILLINEN_PERUSTUTKINTO_KOULUASTEEN_OPISTOASTEEN_TAI_AMMATILLISEN_KORKEA_ASTEEN_TUTKINTO))
            .put(REIFEPRUFUNG_TUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_REIFEPRUFUNG_RB_TUTKINTO, MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_REIFEPRUFUNG_RB_TUTKINTO))
            .put(SUOMALAINEN_YLIOPPILASTUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_LUKION_OPPIMAARA_JA_YLIOPPILASTUTKINTO,
                            SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_YLIOPPILASTUTKINTO_ILMAN_LUKION_OPPIMAARAA))
            .put(SUOMALAISEN_LUKION_OPPIMAARA_TAI_YLIOPPILASTUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_LUKION_OPPIMAARA_ILMAN_YLIOPPILASTUTKINTOA,
                            SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_LUKION_OPPIMAARA_JA_YLIOPPILASTUTKINTO,
                            SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_YLIOPPILASTUTKINTO_ILMAN_LUKION_OPPIMAARAA))
            .put(TOHTORIN_TUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_LISENSIAATTI_TOHTORI, MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_LISENSIAATTI_TOHTORI_ETA_TAI_SVEITSI))
            .put(ULKOMAINEN_KORKEAKOULUTUTKINTO_BACHELOR,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_ALEMPI_YLIOPISTOTUTKINTO_KANDIDAATTI_ETA_TAI_SVEITSI,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_AMMATTIKORKEAKOULUTUTKINTO_ETA_TAI_SVEITSI))
            .put(ULKOMAINEN_KORKEAKOULUTUTKINTO_MASTER,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ETA_TAI_SVEITSI,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_AMMATIKORKEAKOULUTUTKINTO_ETA_TAI_SVEITSI))
            .put(ULKOMAINEN_TOISEN_ASTEEN_TUTKINTO,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_MUU_TUTKINTO_JOKA_ASIANOMAISESSA_MAASSA_ANTAA_HAKUKELPOISUUDEN_KORKEAKOULUUN_ETA_TAI_SVEITSI))
            .put(YLEINEN_AMMATTIKORKEAKOULUKELPOISUUS,
                    ImmutableList.of(SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_LUKION_OPPIMAARA_ILMAN_YLIOPPILASTUTKINTOA,
                            SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_LUKION_OPPIMAARA_JA_YLIOPPILASTUTKINTO,
                            SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_YLIOPPILASTUTKINTO_ILMAN_LUKION_OPPIMAARAA,
                            SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_INTERNATIONAL_BACCALAUREATE_IB_TUTKINTO,
                            SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_EUROPEAN_BACCALAUREATE_EB_TUTKINTO,
                            SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_REIFEPRUFUNG_RB_TUTKINTO,
                            SUOMESSA_SUORITETTU_AMMATILLINEN_PERUSTUTKINTO_KOULUASTEEN_OPISTOASTEEN_TAI_AMMATILLISEN_KORKEA_ASTEEN_TUTKINTO,
                            SUOMESSA_SUORITETTU_AMMATTI_TAI_ERIKOISAMMATTITUTKINTO,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_INTERNATIONAL_BACCALAUREATE_IB_TUTKINTO,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_EUROPEAN_BACCALAUREATE_EB_TUTKINTO,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_REIFEPRUFUNG_RB_TUTKINTO,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_MUU_TUTKINTO_JOKA_ASIANOMAISESSA_MAASSA_ANTAA_HAKUKELPOISUUDEN_KORKEAKOULUUN_ETA_TAI_SVEITSI))
            .put(YLEINEN_YLIOPISTOKELPOISUUS,
                    ImmutableList.of(SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_LUKION_OPPIMAARA_JA_YLIOPPILASTUTKINTO,
                            SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_YLIOPPILASTUTKINTO_ILMAN_LUKION_OPPIMAARAA,
                            SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_INTERNATIONAL_BACCALAUREATE_IB_TUTKINTO,
                            SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_EUROPEAN_BACCALAUREATE_EB_TUTKINTO,
                            SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_REIFEPRUFUNG_RB_TUTKINTO,
                            SUOMESSA_SUORITETTU_AMMATILLINEN_PERUSTUTKINTO_KOULUASTEEN_OPISTOASTEEN_TAI_AMMATILLISEN_KORKEA_ASTEEN_TUTKINTO,
                            SUOMESSA_SUORITETTU_AMMATTI_TAI_ERIKOISAMMATTITUTKINTO,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_INTERNATIONAL_BACCALAUREATE_IB_TUTKINTO,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_EUROPEAN_BACCALAUREATE_EB_TUTKINTO,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_REIFEPRUFUNG_RB_TUTKINTO,
                            MUUALLA_KUIN_SUOMESSA_SUORITETTU_MUU_TUTKINTO_JOKA_ASIANOMAISESSA_MAASSA_ANTAA_HAKUKELPOISUUDEN_KORKEAKOULUUN_ETA_TAI_SVEITSI))
            .put(YLEMPI_AMMATTIKORKEAKOULUTUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_AMMATIKORKEAKOULUTUTKINTO))
            .put(YLEMPI_KORKEAKOULUTUTKINTO,
                    ImmutableList.of(SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI))
            .put(YLIOPPILASTUTKINTO_JA_AMMATILLINEN_PERUSTUTKINTO_120_OV,
                    ImmutableList.of(SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_OLEN_SUORITTANUT_YLIOPPILASTUTKINNON_OHELLA_AMMATILLISEN_TUTKINNON_KAKSOISTUTKINTO))
            .build();

    public static final Map<Hakukelpoisuusvaatimus, List<Pohjakoulutus>> nonExempting = ImmutableMap.<Hakukelpoisuusvaatimus, List<Pohjakoulutus>>builder()
            .put(LISENSIAATIN_TUTKINTO,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_LISENSIAATTI_TOHTORI_ARUBA))
            .put(TOHTORIN_TUTKINTO,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_LISENSIAATTI_TOHTORI_ARUBA))
            .put(ULKOMAINEN_KORKEAKOULUTUTKINTO_BACHELOR,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_ALEMPI_YLIOPISTOTUTKINTO_KANDIDAATTI_ARUBA, MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_AMMATTIKORKEAKOULUTUTKINTO_ARUBA))
            .put(ULKOMAINEN_KORKEAKOULUTUTKINTO_MASTER,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ARUBA, MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_AMMATIKORKEAKOULUTUTKINTO_ARUBA))
            .put(ULKOMAINEN_TOISEN_ASTEEN_TUTKINTO,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_MUU_TUTKINTO_JOKA_ASIANOMAISESSA_MAASSA_ANTAA_HAKUKELPOISUUDEN_KORKEAKOULUUN_ARUBA))
            .put(YLEINEN_AMMATTIKORKEAKOULUKELPOISUUS,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_MUU_TUTKINTO_JOKA_ASIANOMAISESSA_MAASSA_ANTAA_HAKUKELPOISUUDEN_KORKEAKOULUUN_ARUBA))
            .put(YLEINEN_YLIOPISTOKELPOISUUS,
                    ImmutableList.of(MUUALLA_KUIN_SUOMESSA_SUORITETTU_MUU_TUTKINTO_JOKA_ASIANOMAISESSA_MAASSA_ANTAA_HAKUKELPOISUUDEN_KORKEAKOULUUN_ARUBA))
            .build();

    public static Application getApplication(Hakukelpoisuusvaatimus hakukelpoisuusvaatimus, Pohjakoulutus pohjakoulutus) {
        final String asId = "foo";
        final User user = new User("bar");
        final ImmutableMap.Builder<String, Map<String, String>> answers = ImmutableMap.builder();
        final ImmutableMap.Builder<String, String> additionalInfo = ImmutableMap.builder();

        answers.put("koulutustausta", pohjakoulutus.getKoulutustausta());
        answers.put("hakutoiveet", ImmutableMap.of("preference1-Koulutus-id", hakukelpoisuusvaatimus.name()));

        return new Application(asId, user, answers.build(), additionalInfo.build());
    }

    public static Application getApplication(Iterable<String> hakutoiveIds,
                                             Iterable<Pohjakoulutus> pohjakoulutukset) {
        final String asId = "foo";
        final User user = new User("bar");
        final ImmutableMap.Builder<String, Map<String, String>> answers = ImmutableMap.builder();

        ImmutableMap.Builder<String, String> koulutustaustat = ImmutableMap.builder();
        for (Pohjakoulutus p : pohjakoulutukset) {
            koulutustaustat.putAll(p.getKoulutustausta());
        }

        ImmutableMap.Builder<String, String> hakutoiveet = ImmutableMap.builder();
        Iterator<String> iterator = hakutoiveIds.iterator();
        for (int i = 1; iterator.hasNext(); i++) {
            hakutoiveet.put(String.format("preference%d-Koulutus-id", i), iterator.next());
        }

        answers.putAll(ImmutableMap.of(
                "koulutustausta", koulutustaustat.build(),
                "hakutoiveet", hakutoiveet.build()));

        return new Application(asId, user, answers.build(), ImmutableMap.<String, String>of());
    }

    private static <T> ListenableFuture<T> future(final T t) {
        return new ListenableFuture<T>() {
            @Override
            public void addListener(Runnable listener, Executor executor) {
                listener.run();
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public T get() throws InterruptedException, ExecutionException {
                return t;
            }

            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return t;
            }
        };
    }

    public static final String APPLICATION_OPTION_WITH_MULTIPLE_BASE_EDUCATION_REQUIREMENTS = "multiple_pohjakoulutusvaatimuskorkeakoulut";

    public static Map<String, Object> testMappings() {
        final ImmutableMap.Builder<String, Object> builder = new ImmutableMap.Builder<>();

        for (Hakukelpoisuusvaatimus hakukelpoisuusvaatimus : Hakukelpoisuusvaatimus.values()) {
            HakumaksuUtil.BaseEducationRequirements r = new HakumaksuUtil.BaseEducationRequirements();
            r.requiredBaseEducations = ImmutableList.of("pohjakoulutusvaatimuskorkeakoulut_" + hakukelpoisuusvaatimus.getArvo());
            builder.put("http://localhost/ao/" + hakukelpoisuusvaatimus.name(), future(r));
        }

        HakumaksuUtil.BaseEducationRequirements r = new HakumaksuUtil.BaseEducationRequirements();
        r.requiredBaseEducations = Lists.transform(
                ImmutableList.of(
                        Hakukelpoisuusvaatimus.ULKOMAINEN_KORKEAKOULUTUTKINTO_MASTER, // Foreign masters
                        Hakukelpoisuusvaatimus.YLEMPI_KORKEAKOULUTUTKINTO), // Finnish masters
                new Function<Hakukelpoisuusvaatimus, String>() {
                    @Override
                    public String apply(Hakukelpoisuusvaatimus input) {
                        return "pohjakoulutusvaatimuskorkeakoulut_" + input.getArvo() ;
                    }});
        builder.put("http://localhost/ao/" + APPLICATION_OPTION_WITH_MULTIPLE_BASE_EDUCATION_REQUIREMENTS, future(r));


        HakumaksuUtil.KoodistoMaakoodi finKoodit = new HakumaksuUtil.KoodistoMaakoodi();
        HakumaksuUtil.CodeElement fin = new HakumaksuUtil.CodeElement();
        fin.codeElementUri = "maatjavaltiot2_246";
        fin.codeElementValue = "246";
        finKoodit.levelsWithCodeElements = ImmutableList.of(fin);
        builder.put("http://localhost/koodisto-service/rest/codeelement/maatjavaltiot1_fin/1", future(finKoodit));

        HakumaksuUtil.KoodistoMaakoodi sweKoodit = new HakumaksuUtil.KoodistoMaakoodi();
        HakumaksuUtil.CodeElement swe = new HakumaksuUtil.CodeElement();
        swe.codeElementUri = "maatjavaltiot2_752";
        swe.codeElementValue = "752";
        sweKoodit.levelsWithCodeElements = ImmutableList.of(swe);
        builder.put("http://localhost/koodisto-service/rest/codeelement/maatjavaltiot1_swe/1", future(sweKoodit));

        HakumaksuUtil.KoodistoMaakoodi abwKoodit = new HakumaksuUtil.KoodistoMaakoodi();
        HakumaksuUtil.CodeElement abw = new HakumaksuUtil.CodeElement();
        abw.codeElementUri = "maatjavaltiot2_533";
        abw.codeElementValue = "533";
        abwKoodit.levelsWithCodeElements = ImmutableList.of(abw);
        builder.put("http://localhost/koodisto-service/rest/codeelement/maatjavaltiot1_abw/1", future(abwKoodit));

        HakumaksuUtil.KoodistoEAA eea = new HakumaksuUtil.KoodistoEAA();
        eea.withinCodeElements = ImmutableList.of(fin, swe);
        builder.put("http://localhost/koodisto-service/rest/codeelement/valtioryhmat_2/1", future(eea));

        return builder.build();
    }

}
