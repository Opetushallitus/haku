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
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.KoodiBaseSearchCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.common.koodisto.domain.Code;
import fi.vm.sade.oppija.lomake.domain.PostOffice;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

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
    public static final String CODE_MUNICIPALITY = "kunta";
    public static final String CODE_SUBJECT_LANGUAGES = "kielivalikoima";
    public static final String CODE_AIDINKIELI_JA_KIRJALLISUUS = "aidinkielijakirjallisuus";
    public static final String CODE_GENDER = "sukupuoli";


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
    public List<Option> getSubjectLanguages() {
        return codesToOptions(CODE_SUBJECT_LANGUAGES);
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
    public List<Code> getCodes(String koodistoUrl, int version) {
        return null;
    }

    private List<Option> codesToOptions(final String codeName) {
        return ImmutableList.copyOf(
                Lists.reverse(
                        Lists.transform(
                                getKoodiTypes(codeName),
                                new KoodiTypeToOptionFunction())));
    }

    private List<KoodiType> getKoodiTypes(final String koodistoUri) {
        SearchKoodisByKoodistoCriteriaType koodistoCriteria = new SearchKoodisByKoodistoCriteriaType();
        koodistoCriteria.setKoodistoUri(koodistoUri);

        KoodiBaseSearchCriteriaType koodiCriteria = new KoodiBaseSearchCriteriaType();
        koodiCriteria.setValidAt(new XMLGregorianCalendarImpl((GregorianCalendar) GregorianCalendar.getInstance()));
        koodistoCriteria.setKoodiSearchCriteria(koodiCriteria);

        XMLGregorianCalendar calendar = new XMLGregorianCalendarImpl(new GregorianCalendar());
        koodistoCriteria.setValidAt(calendar);

        List<KoodiType> koodiTypes = new ArrayList<KoodiType>();
        try {
            koodiTypes = koodiService.searchKoodisByKoodisto(koodistoCriteria);
        } catch (GenericFault t) {
            LOGGER.warn("Error calling koodisto", t);
        }
        return ImmutableList.copyOf(koodiTypes);
    }
}
