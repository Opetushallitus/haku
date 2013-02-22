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

package fi.vm.sade.oppija.common.koodisto.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.PostOffice;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
@Profile("default")
public class KoodistoServiceImpl implements KoodistoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KoodistoServiceImpl.class);
    public static final String CODE_POST = "posti";
    public static final String CODE_SUBJECT = "oppiaineet";
    public static final String CODE_GRADE_RANGE = "ARVOSANA-ASTEIKKO";
    public static final String CODE_LEARNING_INSTITUTION_TYPES = "Oppilaitostyyppi";
    public static final String CODE_ORGANIZATION_TYPES = "Organisaatiotyyppi";
    public static final String CODE_COUNTRIES = "maat kaksimerkkisellä arvolla";
    public static final String CODE_NATIONALITIES = "";
    public static final String CODE_LANGUAGES = "";

    private final KoodiService koodiService;

    @Autowired
    public KoodistoServiceImpl(final KoodiService koodiService) {
        this.koodiService = koodiService;
    }

    @Override
    public List<PostOffice> getPostOffices() {
        return ImmutableList.copyOf(
                Lists.transform(
                        getKoodiTypes(CODE_POST),
                        new KoodiTypeToPostOfficeFunction()));
    }

    @Override
    public List<SubjectRow> getSubjects() {
        return ImmutableList.copyOf(
                Lists.transform(
                        getKoodiTypes(CODE_SUBJECT),
                        new KoodiTypeToSubjectRowFunction()));
    }

    @Override
    public List<Option> getGradeRanges() {
        return ImmutableList.copyOf(
                Lists.reverse(
                        Lists.transform(
                                getKoodiTypes(CODE_GRADE_RANGE),
                                new KoodiTypeToOptionFunction())));
    }

    @Override
    public List<Option> getLearningInstitutionTypes() {
        return codesToOptions(CODE_LEARNING_INSTITUTION_TYPES);
    }

    @Override
    public List<Option> getOrganizationtypes() {
        return codesToOptions(CODE_ORGANIZATION_TYPES);
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

    private List<Option> codesToOptions(final String codeName) {
        return ImmutableList.copyOf(
                Lists.reverse(
                        Lists.transform(
                                getKoodiTypes(codeName),
                                new KoodiTypeToOptionFunction())));
    }


    private List<KoodiType> getKoodiTypes(final String koodistoUri) {
        SearchKoodisByKoodistoCriteriaType searchKoodisByKoodistoCriteriaType = new SearchKoodisByKoodistoCriteriaType();
        searchKoodisByKoodistoCriteriaType.setKoodistoUri(koodistoUri);
        List<KoodiType> koodiTypes = newArrayList();
        try {
            koodiTypes = koodiService.searchKoodisByKoodisto(searchKoodisByKoodistoCriteriaType);
        } catch (Throwable t) {
            LOGGER.warn("Error calling koodisto", t);
        }
        return ImmutableList.copyOf(koodiTypes);
    }
}
