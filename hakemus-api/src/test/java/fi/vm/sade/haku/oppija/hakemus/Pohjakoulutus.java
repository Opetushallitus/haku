package fi.vm.sade.haku.oppija.hakemus;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public enum Pohjakoulutus {

    SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_AMMATTIKORKEAKOULUTUTKINTO("Suomessa suoritettu korkeakoulututkinto, ammattikorkeakoulututkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_taso", "1"), new KoulutustaustaEntry("pohjakoulutus_kk_nimike", "amk"))),
    SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_ALEMPI_YLIOPISTOTUTKINTO_KANDIDAATTI("Suomessa suoritettu korkeakoulututkinto, alempi yliopistotutkinto (kandidaatti)",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_taso", "2"), new KoulutustaustaEntry("pohjakoulutus_kk_nimike", "kandi"))),
    SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_AMMATIKORKEAKOULUTUTKINTO("Suomessa suoritettu korkeakoulututkinto, ylempi ammatikorkeakoulututkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_taso", "3"), new KoulutustaustaEntry("pohjakoulutus_kk_nimike", "ylempi amk"))),
    SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI("Suomessa suoritettu korkeakoulututkinto, ylempi yliopistotutkinto (maisteri)",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_taso", "4"), new KoulutustaustaEntry("pohjakoulutus_kk_nimike", "maisteri"))),
    SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_LISENSIAATTI_TOHTORI("Suomessa suoritettu korkeakoulututkinto, lisensiaatti/tohtori",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_taso", "5"), new KoulutustaustaEntry("pohjakoulutus_kk_nimike", "tohtori"))),
    SUOMESSA_SUORITETTU_AMMATILLINEN_PERUSTUTKINTO_KOULUASTEEN_OPISTOASTEEN_TAI_AMMATILLISEN_KORKEA_ASTEEN_TUTKINTO("Suomessa suoritettu ammatillinen perustutkinto, kouluasteen, opistoasteen tai ammatillisen korkea-asteen tutkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_am", "true"), new KoulutustaustaEntry("pohjakoulutus_am_nimike", "ammatillinen"))),
    SUOMESSA_SUORITETTU_AMMATTI_TAI_ERIKOISAMMATTITUTKINTO("Suomessa suoritettu ammatti-tai erikoisammattitutkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_amt", "true"), new KoulutustaustaEntry("pohjakoulutus_amt_nimike", "amt"))),
    SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_INTERNATIONAL_BACCALAUREATE_IB_TUTKINTO("Suomessa suoritettu kansainvälinen ylioppilastutkinto, International Baccalaureate (IB) -tutkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo_kansainvalinen_suomessa", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_kansainvalinen_suomessa_tutkinto", "ib"))),
    SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_EUROPEAN_BACCALAUREATE_EB_TUTKINTO("Suomessa suoritettu kansainvälinen ylioppilastutkinto, European Baccalaureate (EB) -tutkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo_kansainvalinen_suomessa", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_kansainvalinen_suomessa_tutkinto", "eb"))),
    SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_REIFEPRUFUNG_RB_TUTKINTO("Suomessa suoritettu kansainvälinen ylioppilastutkinto, Reifeprüfung (RB) -tutkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo_kansainvalinen_suomessa", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_kansainvalinen_suomessa_tutkinto", "rp"))),
    SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_LUKION_OPPIMAARA_JA_YLIOPPILASTUTKINTO("Suomessa suoritettu ylioppilastutkinto ja/tai lukion oppimäärä, lukion oppimäärä ja ylioppilastutkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_tutkinto", "fi"))),
    SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_YLIOPPILASTUTKINTO_ILMAN_LUKION_OPPIMAARAA("Suomessa suoritettu ylioppilastutkinto ja/tai lukion oppimäärä, ylioppilastutkinto ilman lukion oppimäärää",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_tutkinto", "lkOnly"))),
    SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_LUKION_OPPIMAARA_ILMAN_YLIOPPILASTUTKINTOA("Suomessa suoritettu ylioppilastutkinto ja/tai lukion oppimäärä, lukion oppimäärä ilman ylioppilastutkintoa",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_tutkinto", "lk"))),
    SUOMESSA_SUORITETTU_YLIOPPILASTUTKINTO_JA_TAI_LUKION_OPPIMAARA_OLEN_SUORITTANUT_YLIOPPILASTUTKINNON_OHELLA_AMMATILLISEN_TUTKINNON_KAKSOISTUTKINTO("Suomessa suoritettu ylioppilastutkinto ja/tai lukion oppimäärä, olen suorittanut ylioppilastutkinnon ohella ammatillisen tutkinnon (kaksoistutkinto)",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_ammatillinen", "true"))),
    KORKEAKOULUN_EDELLYTTAMAT_AVOIMEN_KORKEAKOULUN_OPINNOT("Korkeakoulun edellyttämät avoimen korkeakoulun opinnot",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_avoin", "true"))),
    MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_AMMATTIKORKEAKOULUTUTKINTO_ETA_TAI_SVEITSI("Muualla kuin Suomessa suoritettu korkeakoulututkinto, ammattikorkeakoulututkinto (ETA tai Sveitsi)",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk_ulk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_taso", "1"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_maa", "SWE"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_nimike", "amk"))),
    MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_ALEMPI_YLIOPISTOTUTKINTO_KANDIDAATTI_ETA_TAI_SVEITSI("Muualla kuin Suomessa suoritettu korkeakoulututkinto, alempi yliopistotutkinto (kandidaatti) (ETA tai Sveitsi",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk_ulk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_taso", "2"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_maa", "SWE"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_nimike", "kandi"))),
    MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_AMMATIKORKEAKOULUTUTKINTO_ETA_TAI_SVEITSI("Muualla kuin Suomessa suoritettu korkeakoulututkinto, ylempi ammatikorkeakoulututkinto (ETA tai Sveitsi",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk_ulk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_taso", "3"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_maa", "SWE"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_nimike", "ylempi amk"))),
    MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_YLEMPI_YLIOPISTOTUTKINTO_MAISTERI_ETA_TAI_SVEITSI("Muualla kuin Suomessa suoritettu korkeakoulututkinto, ylempi yliopistotutkinto (maisteri) (ETA tai Sveitsi)",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk_ulk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_taso", "4"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_maa", "SWE"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_nimike", "maisteri"))),
    MUUALLA_KUIN_SUOMESSA_SUORITETTU_KORKEAKOULUTUTKINTO_LISENSIAATTI_TOHTORI_ETA_TAI_SVEITSI("Muualla kuin Suomessa suoritettu korkeakoulututkinto, lisensiaatti/tohtori (ETA tai Sveitsi)",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_kk_ulk", "true"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_taso", "5"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_maa", "SWE"), new KoulutustaustaEntry("pohjakoulutus_kk_ulk_nimike", "tohtori"))),
    MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_INTERNATIONAL_BACCALAUREATE_IB_TUTKINTO("Muualla kuin Suomessa suoritettu kansainvälinen ylioppilastutkinto, International Baccalaureate (IB) -tutkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo_ulkomainen", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_ulkomainen_tutkinto", "ib"))),
    MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_EUROPEAN_BACCALAUREATE_EB_TUTKINTO("Muualla kuin Suomessa suoritettu kansainvälinen ylioppilastutkinto, European Baccalaureate (EB) -tutkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo_ulkomainen", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_ulkomainen_tutkinto", "eb"))),
    MUUALLA_KUIN_SUOMESSA_SUORITETTU_KANSAINVALINEN_YLIOPPILASTUTKINTO_REIFEPRUFUNG_RB_TUTKINTO("Muualla kuin Suomessa suoritettu kansainvälinen ylioppilastutkinto, Reifeprüfung (RB) -tutkinto",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_yo_ulkomainen", "true"), new KoulutustaustaEntry("pohjakoulutus_yo_ulkomainen_tutkinto", "rp"))),
    MUUALLA_KUIN_SUOMESSA_SUORITETTU_MUU_TUTKINTO_JOKA_ASIANOMAISESSA_MAASSA_ANTAA_HAKUKELPOISUUDEN_KORKEAKOULUUN_ETA_TAI_SVEITSI("Muualla kuin Suomessa suoritettu muu tutkinto, joka asianomaisessa maassa antaa hakukelpoisuuden korkeakouluun (ETA tai Sveitsi)",
            getKoulutustausta(new KoulutustaustaEntry("pohjakoulutus_ulk", "true"), new KoulutustaustaEntry("pohjakoulutus_ulk_suoritusmaa", "SWE"), new KoulutustaustaEntry("pohjakoulutus_ulk_nimike", "muu")));

    private final String name;
    
    private final Map<String, String> koulutustausta;
    
    Pohjakoulutus(final String name, final Map<String, String> koulutustausta) {
        this.name = name;
        this.koulutustausta = koulutustausta;
    }
    
    public String getName() {
        return name;
    }

    public Map<String, String> getKoulutustausta() {
        return koulutustausta;
    }
    
    @SafeVarargs
    private static Map<String, String> getKoulutustausta(final Map.Entry<String, String>... entries) {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : entries) {
            builder.put(entry);
        }
        return builder.build();
    }

}
