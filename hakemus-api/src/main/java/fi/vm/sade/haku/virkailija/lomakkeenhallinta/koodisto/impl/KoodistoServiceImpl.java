/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodistoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class KoodistoServiceImpl implements KoodistoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KoodistoServiceImpl.class);
    public static final String CODE_POST = "posti";
    public static final String CODE_SUBJECT = "oppiaineetyleissivistava";
    public static final String CODE_GRADE_RANGE = "arvosanat";
    public static final String CODE_LEARNING_INSTITUTION_TYPES = "oppilaitostyyppi";
    public static final String CODE_COUNTRIES = "maatjavaltiot1";
    public static final String CODE_NATIONALITIES = CODE_COUNTRIES;
    public static final String CODE_LANGUAGES = "kieli";
    public static final String CODE_TEACHING_LANGUAGES = "opiskelukieli";
    public static final String CODE_MUNICIPALITY = "kunta";
    public static final String CODE_SUBJECT_LANGUAGES = "kielivalikoima";
    public static final String CODE_AIDINKIELI_JA_KIRJALLISUUS = "aidinkielijakirjallisuus";
    public static final String CODE_GENDER = "sukupuoli";
    public static final String CODE_HAKUKAUSI = "kausi";
    private static final String CODE_HAKUKOHDE = "hakukohteet";
    private static final String CODE_OPPILAITOSTYYPPI = "oppilaitostyyppi";
    private static final String CODE_LAAJUUSYKSIKKO = "opintojenlaajuusyksikko";
    private static final String CODE_TUTKINTOTYYPPI = "tutkintotyyppi";
    private static final String CODE_ARVOSTELUASTEIKKO= "ammatillisentutkinnonarvosteluasteikko";
    private static final String CODE_KKTUTKINNOT = "kktutkinnot";
    private static final String CODE_OPINTOALA = "opintoalaoph2002";
    private static final String CODE_KOULUTUSALA = "koulutusalaoph2002";
    private static final String CODE_AMMATILLISET_OPS_PERUSTAISET = "ammatillisetopsperustaiset";
    private static final String CODE_YOARVOSANAT = "yoarvosanatkansainvalinen";

    private static final String LUKIO = "15";
    private static final String LUKIO_JA_PERUSKOULU = "19";
    private static final String KANSANOPISTO = "63";
    private static final String AMMATTIKORKEAKOLU = "41";
    private static final String YLIOPISTO = "42";
    private static final String SOTILASKORKEAKOULU = "43";
    private static final String VALIAIKAINEN_AMK = "46";
    private static final String AMMATILLINEN_OPPILAITOS = "21";
    private static final String AMMATILLINEN_ERITYISOPPILAITOS = "22";
    private static final String AMMATILLINEN_ERIKOISOPPILAITOS = "23";
    private static final String AMMATILLINEN_AIKUISKOULUTUSKESKUS = "24";
    private static final String PALO_POLIISI_VARTIOINTI_OPPILAITOS = "28";
    private static final String SOTILASALAN_OPPILAITOS = "29";
    private static final String LIIKUNNAN_KOULUTUSKEKUS = "62";
    private static final String MUSIIKKIOPPILAITOS = "61";
    
    private static final String TOHTORIN_TUTKINTO = "16";
    private static final String LISENSIAATIN_TUTKINTO = "15";
    private static final String YLEMPI_KORKEAKOULUTUTKINTO = "14";
    private static final String ALEMPI_KORKEAKOULUTUTKINTO = "13";

    private static final String YLEMPI_AMMATTIKORKEAKOULUTUTKINTO = "12";
    private static final String AMMATTIKORKEAKOULUJEN_ERIKOISTUMISOPINNOT = "07";
    private static final String AMMATTIKORKEAKOULUTUS = "06";

    private final KoodistoClient koodiService;
    private final OrganizationService organisaatioService;

    @Value("${haku.koodisto.limit:99999}")
    private int fetchLimit;

    @Autowired
    public KoodistoServiceImpl(final KoodistoClient koodiService, final OrganizationService organisaatioService) {
        this.koodiService = koodiService;
        this.organisaatioService = organisaatioService;
    }

    @Override
    public List<SubjectRow> getSubjects() {
        return ImmutableList.copyOf(
                Lists.transform(
                        getKoodiTypes(CODE_SUBJECT),
                        new KoodiTypeToSubjectRowFunction(koodiService)));
    }

    @Override
    public List<Option> getGradeRanges() {
        // Sorting grades is tricky
        ArrayList<KoodiType> grades = new ArrayList(getKoodiTypes(CODE_GRADE_RANGE));
        Collections.sort(grades, new Comparator<KoodiType>() {
            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                String k1 = o1.getKoodiArvo();
                String k2 = o2.getKoodiArvo();
                return makeComparable(k1).compareTo(makeComparable(k2));
            }

            private String makeComparable(String koodiArvo) {
                if ("S".equals(koodiArvo)) {
                    return "11";
                } else if ("Ei arvosanaa".equals(koodiArvo)) {
                    return "12";
                } else if ("10".equals(koodiArvo)) {
                    return koodiArvo;
                }
                return "0"+koodiArvo;
            }
        });
        return ImmutableList.copyOf(
                Lists.reverse(
                        Lists.transform(
                                grades,
                                new KoodiTypeToOptionFunction())));
    }

    @Override
    public List<Option> getPostOffices() {
        return codesToOptions(CODE_POST);
    }

    @Override
    public List<Option> getSubjectLanguages() {
        return codesToOptions(CODE_SUBJECT_LANGUAGES);
    }

    @Override
    public List<Option> getLearningInstitutionTypes() {
        return urisToOptions(CODE_LEARNING_INSTITUTION_TYPES, true);
    }

    @Override
    public List<Option> getCountries() {
        return codesToOptions(CODE_COUNTRIES);
    }

    @Override
    public List<Option> getNationalities() {
        return codesToOptions(CODE_NATIONALITIES);
    }

    @Override
    public List<Option> getLanguages() {
        return codesToOptions(CODE_LANGUAGES);
    }

    @Override
    public List<Option> getMunicipalities() {
        return codesToOptions(CODE_MUNICIPALITY);
    }

    @Override
    public List<Option> getLanguageAndLiterature() {
        return codesToOptions(CODE_AIDINKIELI_JA_KIRJALLISUUS);
    }

    @Override
    public List<Option> getGenders() {
        return codesToOptions(CODE_GENDER);
    }

    @Override
    public List<Option> getHakukausi() {
        return urisToOptions(CODE_HAKUKAUSI, false);
    }

    @Override
    public List<Option> getTeachingLanguages() {
        return codesToOptions(CODE_TEACHING_LANGUAGES);
    }

    @Override
    public List<Option> getAmmatillisenTutkinnonArvosteluasteikko() {
        return codesToOptions(CODE_ARVOSTELUASTEIKKO);
    }

    @Override
    public List<Option> getYoArvosanaasteikko() {

        ArrayList<KoodiType> grades = new ArrayList(getKoodiTypes(CODE_YOARVOSANAT));
        Collections.sort(grades, new Comparator<KoodiType>() {
            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return o1.getKoodiArvo().compareTo(o2.getKoodiArvo());
            }
        });
        return ImmutableList.copyOf(
                        Lists.transform(
                                grades,
                                new KoodiTypeToOptionFunction()));
    }

    @Override
    public List<Option> getOpintoalat() {
        return codesToOptions(CODE_OPINTOALA);
    }

    @Override
    public List<Option> getOpintoalat(String koulutusala) {
        return Lists.transform(
                koodiService.getAlakoodis(CODE_KOULUTUSALA + "_" + koulutusala),
                new KoodiTypeToOptionFunction());
    }

    @Override
    public List<Option> getKoulutusalat() {
        return codesToOptions(CODE_KOULUTUSALA);
    }

    @Override
    public List<Code> getCodes(String koodistoUrl, int version) {
        return Lists.transform(getKoodiTypes(koodistoUrl, version), new KoodiTypeToCodeFunction());
    }

    @Override
    public List<Code> getYliopistokoulutukset() {
        List<KoodiType> tutkintotyypit = getKoodiTypes(CODE_TUTKINTOTYYPPI);
        List<KoodiType> yliopistoKoulutukset = new ArrayList<KoodiType>();
        for (KoodiType koodi : tutkintotyypit) {
            if (koodi.getKoodiArvo().equals(TOHTORIN_TUTKINTO) ||
                    koodi.getKoodiArvo().equals(LISENSIAATIN_TUTKINTO) ||
                    koodi.getKoodiArvo().equals(YLEMPI_KORKEAKOULUTUTKINTO) ||
                    koodi.getKoodiArvo().equals(ALEMPI_KORKEAKOULUTUTKINTO)) {
                List<KoodiType> ylakoodit = koodiService.getYlakoodis(koodi.getKoodiUri());
                yliopistoKoulutukset.addAll(ylakoodit);
            }
        }
        return Lists.transform(yliopistoKoulutukset, new KoodiTypeToCodeFunction());
    }

    @Override
    public List<Code> getAMKkoulutukset() {
        List<KoodiType> tutkintotyypit = getKoodiTypes(CODE_TUTKINTOTYYPPI);
        List<KoodiType> koulutukset = new ArrayList<KoodiType>();
        for (KoodiType koodi : tutkintotyypit) {
            if (koodi.getKoodiArvo().equals(YLEMPI_AMMATTIKORKEAKOULUTUTKINTO) ||
                    koodi.getKoodiArvo().equals(AMMATTIKORKEAKOULUJEN_ERIKOISTUMISOPINNOT) ||
                    koodi.getKoodiArvo().equals(AMMATTIKORKEAKOULUTUS)) {
                List<KoodiType> ylakoodit = koodiService.getYlakoodis(koodi.getKoodiUri());
                koulutukset.addAll(ylakoodit);
            }
        }
        return Lists.transform(koulutukset, new KoodiTypeToCodeFunction());
    }

    @Override
    public List<Code> getYlemmatAMKkoulutukset() {
        List<KoodiType> tutkintotyypit = getKoodiTypes(CODE_TUTKINTOTYYPPI);
        List<KoodiType> koulutukset = new ArrayList<KoodiType>();
        for (KoodiType koodi : tutkintotyypit) {
            if (koodi.getKoodiArvo().equals(YLEMPI_AMMATTIKORKEAKOULUTUTKINTO)) {
                List<KoodiType> ylakoodit = koodiService.getYlakoodis(koodi.getKoodiUri());
                koulutukset.addAll(ylakoodit);
            }
        }
        return Lists.transform(koulutukset, new KoodiTypeToCodeFunction());
    }

    @Override
    public List<Option> getAmmattioppilaitosKoulukoodit() {
        return getKoulukoodit(false, AMMATILLINEN_OPPILAITOS, AMMATILLINEN_ERITYISOPPILAITOS, AMMATILLINEN_ERIKOISOPPILAITOS,
                AMMATILLINEN_AIKUISKOULUTUSKESKUS, PALO_POLIISI_VARTIOINTI_OPPILAITOS, SOTILASALAN_OPPILAITOS,
                LIIKUNNAN_KOULUTUSKEKUS, MUSIIKKIOPPILAITOS, KANSANOPISTO);
    }

    @Override
    public List<Option> getLukioKoulukoodit() {
        return getKoulukoodit(false, LUKIO, LUKIO_JA_PERUSKOULU, KANSANOPISTO);
    }

    private List<Option> getKoulukoodit(boolean passives, String... oppilaitosyypit) {
        Set<String> tyypit = new HashSet<>(oppilaitosyypit.length);
        Collections.addAll(tyypit, oppilaitosyypit);
        List<String> oppilaitosnumerot = new ArrayList<>();
        for (KoodiType koodi : getKoodiTypes(CODE_OPPILAITOSTYYPPI)) {
            if (tyypit.contains(koodi.getKoodiArvo())) {
                for (KoodiType ylakoodi : koodiService.getYlakoodis(koodi.getKoodiUri())) {
                    if ("oppilaitosnumero".equals(ylakoodi.getKoodisto().getKoodistoUri())
                            && (passives || TilaType.PASSIIVINEN != ylakoodi.getTila())) {
                        oppilaitosnumerot.add(ylakoodi.getKoodiArvo());
                    }
                }
            }
        }

        List<Option> oppilaitokset = new ArrayList<>(oppilaitosnumerot.size());
        for (Organization org : organisaatioService.findByOppilaitosnumero(oppilaitosnumerot)) {
            if (org.getTypes().contains("Oppilaitos")) {
                oppilaitokset.add((Option) new OptionBuilder().setValue(org.getOid()).i18nText(org.getName()).build());
            }
        }
        return oppilaitokset;
    }

    @Override
    public List<Option> getKorkeakoulut() {
        return getKoulukoodit(false, AMMATTIKORKEAKOLU, YLIOPISTO, SOTILASKORKEAKOULU, VALIAIKAINEN_AMK);
    }

    @Override
    public List<Option> getKorkeakoulutMyosPassiiviset() {
        return getKoulukoodit(true, AMMATTIKORKEAKOLU, YLIOPISTO, SOTILASKORKEAKOULU, VALIAIKAINEN_AMK);
    }

    @Override
    public List<Option> getAmmattitutkinnot() {
        KoodiType yes = null;

        for (KoodiType k : getKoodiTypes(CODE_AMMATILLISET_OPS_PERUSTAISET)) {
            if ("1".equals(k.getKoodiArvo())) {
                yes = k;
            }
        }

        Map<String, KoodiType> koodit = new LinkedHashMap<>();
        for (KoodiType koodi : koodiService.getYlakoodis(yes.getKoodiUri())) {
            KoodiType oldKoodi = koodit.get(koodi.getKoodiArvo());
            if (oldKoodi == null || oldKoodi.getVersio() < koodi.getVersio()) {
                koodit.put(koodi.getKoodiArvo(), koodi);
            }
        }
        ArrayList<KoodiType> koodilista = new ArrayList<>(koodit.size());
        koodilista.addAll(koodit.values());
        return ImmutableList.copyOf(
                Lists.reverse(Lists.transform(koodilista, new KoodiTypeToOptionFunction())));
    }

    @Override
    public List<Option> getHakukohdekoodit() {
        return codesToOptions(CODE_HAKUKOHDE);
    }

    @Override
    public List<Option> getLaajuusYksikot() {
        return codesToOptions(CODE_LAAJUUSYKSIKKO);
    }

    @Override
    public List<Option> getKorkeakouluTutkintotasot() {
        return codesToOptions(CODE_KKTUTKINNOT);
    }

    private List<Option> codesToOptions(final String codeName) {
        return ImmutableList.copyOf(
                Lists.reverse(
                        Lists.transform(
                                getKoodiTypes(codeName),
                                new KoodiTypeToOptionFunction())));
    }

    private List<Option> urisToOptions(final String codeName, final boolean withVersion) {
        return ImmutableList.copyOf(
                Lists.reverse(
                        Lists.transform(
                                getKoodiTypes(codeName),
                                new Function<KoodiType, Option>() {
                                    @Override
                                    public Option apply(final KoodiType koodiType) {
                                        String version = withVersion ? "#" + koodiType.getVersio() : "";
                                        return (Option) new OptionBuilder().setValue(koodiType.getKoodiUri() + version).i18nText(new I18nText(TranslationsUtil.createTranslationsMap(koodiType))).build();
                                    }
                                })));
    }

    private List<KoodiType> getKoodiTypes(final String koodistoUri) {
        return getKoodiTypes(koodistoUri, null);
    }

    private List<KoodiType> getKoodiTypes(final String koodistoUri, final Integer version) {
        List<KoodiType> koodiTypes = new ArrayList<KoodiType>();
        try {
            koodiTypes = koodiService.getKoodisForKoodisto(koodistoUri, version, true);
        } catch (Exception t) {
            LOGGER.warn("Error calling koodisto", t);
        }
        return ImmutableList.copyOf(koodiTypes);
    }
}
