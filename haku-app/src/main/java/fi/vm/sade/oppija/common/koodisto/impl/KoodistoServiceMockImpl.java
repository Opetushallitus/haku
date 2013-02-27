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
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.PostOffice;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;

@Service
@Profile("default")
public class KoodistoServiceMockImpl implements KoodistoService {

    public static final String LEARNING_INSTITUTION_TYPE = "Yliopistot";
    public static final String ORGANIZATION_TYPE = "Toimipiste";
    public final List<Option> listOfGradeGrades;
    public final List<PostOffice> listOfPostOffices;
    public final List<SubjectRow> listOfSubjectsRows;
    public final List<Option> listOfLearningInstitutionTypes;
    public final List<Option> listOfOrganizationtypes;
    public final List<Option> listOfCountries;
    public final List<Option> listOfLanguages;
    public final List<Option> listOfNationalities;
    public final List<Option> listOfMunicipalities;

    public KoodistoServiceMockImpl() {
        List<Option> listOfGradeGrades = new ArrayList<Option>();
        listOfGradeGrades.add(new Option("grade_0", createI18NText("Ei arvosanaa"), "0"));
        listOfGradeGrades.add(new Option("grade_10", createI18NText("10"), "10"));
        listOfGradeGrades.add(new Option("grade_9", createI18NText("9"), "9"));
        listOfGradeGrades.add(new Option("grade_8", createI18NText("8"), "8"));
        listOfGradeGrades.add(new Option("grade_7", createI18NText("7"), "7"));
        listOfGradeGrades.add(new Option("grade_6", createI18NText("6"), "6"));
        listOfGradeGrades.add(new Option("grade_5", createI18NText("5"), "5"));
        listOfGradeGrades.add(new Option("grade_4", createI18NText("4"), "4"));
        this.listOfGradeGrades = ImmutableList.copyOf(listOfGradeGrades);

        this.listOfPostOffices = ImmutableList.of(
                createPostOffice("00100", "Helsinki"),
                createPostOffice("02100", "Espoo"),
                createPostOffice("33100", "Tampere"));

        this.listOfSubjectsRows = ImmutableList.of(
                new SubjectRow("A1", createI18NText("A1-kieli")),
                new SubjectRow("A12", createI18NText("A12-kieli")),
                new SubjectRow("A2", createI18NText("A2-kieli")),
                new SubjectRow("A22", createI18NText("A22-kieli")),
                new SubjectRow("AI", createI18NText("Äidinkieli ja kirjallisuus")),
                new SubjectRow("B1", createI18NText("B1-kieli")),
                new SubjectRow("B2", createI18NText("B2-kieli")),
                new SubjectRow("B22", createI18NText("B22-kieli")),
                new SubjectRow("B23", createI18NText("B23-kieli")),
                new SubjectRow("B3", createI18NText("B3-kieli")),
                new SubjectRow("MA", createI18NText("Matematiikka")),
                new SubjectRow("BI", createI18NText("Biologia")),
                new SubjectRow("GE", createI18NText("Maantieto")),
                new SubjectRow("FY", createI18NText("Fysiikka")),
                new SubjectRow("KE", createI18NText("Kemia")),
                new SubjectRow("TE", createI18NText("Terveystieto")),
                new SubjectRow("KT", createI18NText("Uskonto tai elämänkatsomustieto")),
                new SubjectRow("HI", createI18NText("Historia")),
                new SubjectRow("YH", createI18NText("Yhteiskuntaoppi")),
                new SubjectRow("MU", createI18NText("Musiikki")),
                new SubjectRow("KU", createI18NText("Kuvataide")),
                new SubjectRow("KS", createI18NText("Käsityö")),
                new SubjectRow("LI", createI18NText("Liikunta"))
        );
        this.listOfLearningInstitutionTypes = ImmutableList.of(
                new Option(LEARNING_INSTITUTION_TYPE,
                        createI18NText(LEARNING_INSTITUTION_TYPE), LEARNING_INSTITUTION_TYPE));
        this.listOfOrganizationtypes =
                ImmutableList.of(
                        new Option(ORGANIZATION_TYPE,
                                createI18NText(ORGANIZATION_TYPE), ORGANIZATION_TYPE));

        this.listOfCountries =
                ImmutableList.of(
                        new Option("FI",
                                createI18NText("Suomi"), "FI"),
                        new Option("SV",
                                createI18NText("Ruotsi"), "SV"));

        this.listOfLanguages = listOfCountries;

        this.listOfNationalities = listOfCountries;


        this.listOfMunicipalities = ImmutableList.of(
                new Option("jalasjarvi",
                        createI18NText("Jalasjärvi"), "jalasjarvi"),
                new Option("janakkala",
                        createI18NText("Janakkala"), "janakkala"),
                new Option("joensuu",
                        createI18NText("Joensuu"), "joensuu"),
                new Option("jokioinen",
                        createI18NText("jokioinen"), "jokioinen"),
                new Option("jomala",
                        createI18NText("Jomala"), "jomala")
        );

    }

    @Override
    public List<PostOffice> getPostOffices() {
        return this.listOfPostOffices;
    }

    @Override
    public List<SubjectRow> getSubjects() {
        return this.listOfSubjectsRows;
    }

    @Override
    public List<Option> getGradeRanges() {
        return this.listOfGradeGrades;
    }

    @Override
    public List<Option> getLearningInstitutionTypes() {
        return this.listOfLearningInstitutionTypes;
    }

    @Override
    public List<Option> getOrganizationtypes() {
        return this.listOfOrganizationtypes;
    }

    @Override
    public List<Option> getCountries() {
        return this.listOfCountries;
    }

    @Override
    public List<Option> getNationalities() {
        return this.listOfNationalities;
    }

    @Override
    public List<Option> getLanguages() {
        return this.listOfLanguages;
    }

    @Override
    public List<Option> getMunicipalities() {
        return this.listOfMunicipalities;
    }

    private static PostOffice createPostOffice(final String postCode, final String text) {
        return new PostOffice(postCode, ElementUtil.createI18NText(text));
    }
}
