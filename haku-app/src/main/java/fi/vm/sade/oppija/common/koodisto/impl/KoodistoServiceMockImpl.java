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

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;

@Service
@Profile("dev")
public class KoodistoServiceMockImpl implements KoodistoService {

    public static final String LEARNING_INSTITUTION_TYPE = "Yliopistot";
    public static final String ORGANIZATION_TYPE = "Toimipiste";
	private static final String RUOTSI = "SV";
	private static final String SUOMI = "FI";
    public final List<Option> listOfGradeGrades;
    public final List<PostOffice> listOfPostOffices;
    public final List<SubjectRow> listOfSubjects;
    public final List<SubjectRow> listOfSubjectsRowsSecondary;
    public final List<Option> listOfLearningInstitutionTypes;
    public final List<Option> listOfOrganizationTypes;
    public final List<Option> listOfCountries;
    public final List<Option> listOfLanguages;
    public final List<Option> listOfNationalities;
    public final List<Option> listOfMunicipalities;

    public KoodistoServiceMockImpl() {
        List<Option> listOfGradeGrades = new ArrayList<Option>();
        listOfGradeGrades.add(new Option("grade_0", createI18NForm("Ei arvosanaa"), "0"));
        listOfGradeGrades.add(new Option("grade_10", createI18NForm("10"), "10"));
        listOfGradeGrades.add(new Option("grade_9", createI18NForm("9"), "9"));
        listOfGradeGrades.add(new Option("grade_8", createI18NForm("8"), "8"));
        listOfGradeGrades.add(new Option("grade_7", createI18NForm("7"), "7"));
        listOfGradeGrades.add(new Option("grade_6", createI18NForm("6"), "6"));
        listOfGradeGrades.add(new Option("grade_5", createI18NForm("5"), "5"));
        listOfGradeGrades.add(new Option("grade_4", createI18NForm("4"), "4"));
        this.listOfGradeGrades = ImmutableList.copyOf(listOfGradeGrades);

        this.listOfPostOffices = ImmutableList.of(
                createPostOffice("00100", "Helsinki"),
                createPostOffice("02100", "Espoo"),
                createPostOffice("33100", "Tampere"));

        this.listOfSubjects = ImmutableList.of(
                new SubjectRow("A1", createI18NForm("A1-kieli")),
                new SubjectRow("A12", createI18NForm("A12-kieli")),
                new SubjectRow("A2", createI18NForm("A2-kieli")),
                new SubjectRow("A22", createI18NForm("A22-kieli")),
                new SubjectRow("AI", createI18NForm("Äidinkieli ja kirjallisuus")),
                new SubjectRow("B1", createI18NForm("B1-kieli")),
                new SubjectRow("B2", createI18NForm("B2-kieli")),
                new SubjectRow("B22", createI18NForm("B22-kieli")),
                new SubjectRow("B23", createI18NForm("B23-kieli")),
                new SubjectRow("B3", createI18NForm("B3-kieli")),
                new SubjectRow("MA", createI18NForm("Matematiikka")),
                new SubjectRow("BI", createI18NForm("Biologia")),
                new SubjectRow("GE", createI18NForm("Maantieto")),
                new SubjectRow("FY", createI18NForm("Fysiikka")),
                new SubjectRow("KE", createI18NForm("Kemia")),
                new SubjectRow("TE", createI18NForm("Terveystieto")),
                new SubjectRow("KT", createI18NForm("Uskonto tai elämänkatsomustieto")),
                new SubjectRow("HI", createI18NForm("Historia")),
                new SubjectRow("YH", createI18NForm("Yhteiskuntaoppi")),
                new SubjectRow("MU", createI18NForm("Musiikki")),
                new SubjectRow("KU", createI18NForm("Kuvataide")),
                new SubjectRow("KS", createI18NForm("Käsityö")),
                new SubjectRow("LI", createI18NForm("Liikunta"))
        );
        this.listOfSubjectsRowsSecondary = ImmutableList.of(
                new SubjectRow("LUK_A1", createI18NForm("A1-kieli")),
                new SubjectRow("LUK_A12", createI18NForm("A12-kieli")),
                new SubjectRow("LUK_A2", createI18NForm("A2-kieli")),
                new SubjectRow("LUK_A22", createI18NForm("A22-kieli")),
                new SubjectRow("LUK_AI", createI18NForm("Äidinkieli ja kirjallisuus")),
                new SubjectRow("LUK_B1", createI18NForm("B1-kieli")),
                new SubjectRow("LUK_B2", createI18NForm("B2-kieli")),
                new SubjectRow("LUK_B22", createI18NForm("B22-kieli")),
                new SubjectRow("LUK_B23", createI18NForm("B23-kieli")),
                new SubjectRow("LUK_B3", createI18NForm("B3-kieli")),
                new SubjectRow("LUK_HI", createI18NForm("Historia")),
                new SubjectRow("LUK_YH", createI18NForm("Yhteiskuntaoppi")),
                new SubjectRow("LUK_MA", createI18NForm("Matematiikka")),
                new SubjectRow("LUK_FY", createI18NForm("Fysiikka")),
                new SubjectRow("LUK_KE", createI18NForm("Kemia")),
                new SubjectRow("LUK_BI", createI18NForm("Biologia"))
        );

        this.listOfLearningInstitutionTypes = ImmutableList.of(
                new Option(LEARNING_INSTITUTION_TYPE,
                        createI18NForm(LEARNING_INSTITUTION_TYPE), LEARNING_INSTITUTION_TYPE));
        this.listOfOrganizationTypes =
                ImmutableList.of(
                        new Option(ORGANIZATION_TYPE,
                                createI18NForm(ORGANIZATION_TYPE), ORGANIZATION_TYPE));

        this.listOfCountries =
                ImmutableList.of(
                        new Option(SUOMI,
                                createI18NForm("Suomi"), SUOMI),
                        new Option(RUOTSI,
                                createI18NForm("Ruotsi"), RUOTSI));

        this.listOfLanguages = listOfCountries;

        this.listOfNationalities =
                ImmutableList.of(
                        new Option(SUOMI,
                                createI18NForm("Suomi"), SUOMI),
                        new Option(RUOTSI,
                                createI18NForm("Ruotsi"), RUOTSI));


        this.listOfMunicipalities = ImmutableList.of(
                new Option("jalasjarvi",
                        createI18NForm("Jalasjärvi"), "jalasjarvi"),
                new Option("janakkala",
                        createI18NForm("Janakkala"), "janakkala"),
                new Option("joensuu",
                        createI18NForm("Joensuu"), "joensuu"),
                new Option("jokioinen",
                        createI18NForm("jokioinen"), "jokioinen"),
                new Option("jomala",
                        createI18NForm("Jomala"), "jomala")
        );

    }

    @Override
    public List<PostOffice> getPostOffices() {
        return this.listOfPostOffices;
    }

    @Override
    public List<SubjectRow> getSubjects() {
        return this.listOfSubjects;
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
        return this.listOfOrganizationTypes;
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
        return new PostOffice(postCode, ElementUtil.createI18NForm(text));
    }
}
