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
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Profile("default")
public class KoodistoServiceImpl implements KoodistoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KoodistoServiceImpl.class);
    public static final String CODE_POST = "posti";
    public static final String CODE_SUBJECT = "oppiaineetyleissivistava";
    public static final String CODE_GRADE_RANGE = "arvosanat";
    public static final String CODE_LEARNING_INSTITUTION_TYPES = "oppilaitostyyppi";
    public static final String CODE_ORGANIZATION_TYPES = "organisaatiotyyppi";
    public static final String CODE_COUNTRIES = "maatjavaltiot1";
    public static final String CODE_NATIONALITIES = CODE_COUNTRIES;
    public static final String CODE_LANGUAGES = "kieli";
    public static final String CODE_TEACHING_LANGUAGES = "opiskelukieli";
    public static final String CODE_MUNICIPALITY = "kunta";
    public static final String CODE_SUBJECT_LANGUAGES = "kielivalikoima";
    public static final String CODE_AIDINKIELI_JA_KIRJALLISUUS = "aidinkielijakirjallisuus";
    public static final String CODE_GENDER = "sukupuoli";
    public static final String CODE_HAKUKAUSI = "kausi";
    private static final String CODE_KOULUNUMERO = "oppilaitosnumero";

    private static final String LUKIO = "15";
    private static final String LUKIO_JA_PERUSKOULU = "19";
    private static final String KANSANOPISTO = "63";
    private static final String OPPILAITOSTYYPPI_LUKIO = "oppilaitostyyppi_15";
    private static final String OPPILAITOSTYYPPI_PK_JA_LUKIO = "oppilaitostyyppi_19";
    private static final String OPPILAITOSTYYPPI_KANSANOPISTO = "oppilaitostyyppi_63";

    private final KoodistoClient koodiService;
    private final OrganizationService organisaatioService;

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
                if (k1.length() != k2.length()) {
                    return k1.length() - k2.length();
                }
                return k1.compareTo(k2);
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
        return urisToOptions(CODE_LEARNING_INSTITUTION_TYPES);
    }

    @Override
    public List<Option> getOrganizationtypes() {
        return urisToOptions(CODE_ORGANIZATION_TYPES);
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
        return urisToOptions(CODE_HAKUKAUSI);
    }

    @Override
    public List<Option> getTeachingLanguages() {
        return codesToOptions(CODE_TEACHING_LANGUAGES);
    }

    @Override
    public List<Code> getCodes(String koodistoUrl, int version) {
        return Lists.transform(getKoodiTypes(koodistoUrl, version), new KoodiTypeToCodeFunction());
    }

    @Override
    public List<Option> getLukioKoulukoodit() {
        List<KoodiType> numerot = getKoodiTypes(CODE_KOULUNUMERO);
        LOGGER.debug("Getting lukiokoodit: {}", numerot.size());
        List<String> lukioNumerot = new ArrayList<String>();
        for (KoodiType koodi : numerot) {
            List<KoodiType> alakoodit = koodiService.getAlakoodis(koodi.getKoodiUri());

            for (KoodiType alakoodi : alakoodit) {
                String uri = alakoodi.getKoodiUri();
                String arvo = alakoodi.getKoodiArvo();
                if ((OPPILAITOSTYYPPI_LUKIO.equals(uri)
                        || OPPILAITOSTYYPPI_PK_JA_LUKIO.equals(uri)
                        || OPPILAITOSTYYPPI_KANSANOPISTO.equals(uri))
                        &&
                        (LUKIO.equals(arvo)
                        || LUKIO_JA_PERUSKOULU.equals(arvo))
                        || KANSANOPISTO.equals(arvo)) {
                    lukioNumerot.add(koodi.getKoodiArvo());
                }
            }
        }

        List<Option> opts = new ArrayList<Option>(lukioNumerot.size());
        List<Organization> orgs = organisaatioService.findByOppilaitosnumero(lukioNumerot);
        for (Organization org : orgs) {
            opts.add(new Option(org.getName(), org.getOid()));
        }
        return opts;
    }

    private List<Option> codesToOptions(final String codeName) {
        return ImmutableList.copyOf(
                Lists.reverse(
                        Lists.transform(
                                getKoodiTypes(codeName),
                                new KoodiTypeToOptionFunction())));
    }

    private List<Option> urisToOptions(final String codeName) {
        return ImmutableList.copyOf(
                Lists.reverse(
                        Lists.transform(
                                getKoodiTypes(codeName),
                                new Function<KoodiType, Option>() {
                                    @Override
                                    public Option apply(final KoodiType koodiType) {
                                        return new Option(
                                                new I18nText(TranslationsUtil.createTranslationsMap(koodiType)),
                                                koodiType.getKoodiUri() + "#" + koodiType.getVersio());
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
