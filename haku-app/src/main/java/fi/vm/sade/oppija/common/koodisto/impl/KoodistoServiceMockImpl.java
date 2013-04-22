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
    public final List<Option> listOfLearningInstitutionTypes;
    public final List<Option> listOfOrganizationTypes;
    public final List<Option> listOfCountries;
    public final List<Option> listOfLanguages;
    public final List<Option> listOfLanguageAndLiterature;
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
                new SubjectRow("A1", createI18NForm("A1-kieli"), true, true, true, true),
                new SubjectRow("A12", createI18NForm("A12-kieli"), true, true, true, true),
                new SubjectRow("A2", createI18NForm("A2-kieli"), true, true, true, true),
                new SubjectRow("A22", createI18NForm("A22-kieli"), true, true, true, true),
                new SubjectRow("AI", createI18NForm("Äidinkieli ja kirjallisuus"), true, true, true, false),
                new SubjectRow("B1", createI18NForm("B1-kieli"), true, true, true, true),
                new SubjectRow("B2", createI18NForm("B2-kieli"), true, true, true, true),
                new SubjectRow("B22", createI18NForm("B22-kieli"), true, true, true, true),
                new SubjectRow("B23", createI18NForm("B23-kieli"), true, true, true, true),
                new SubjectRow("B3", createI18NForm("B3-kieli"), true, true, true, true),
                new SubjectRow("MA", createI18NForm("Matematiikka"), true, true, true, false),
                new SubjectRow("BI", createI18NForm("Biologia"), true, true, true, false),
                new SubjectRow("GE", createI18NForm("Maantieto"), true, true, true, false),
                new SubjectRow("FY", createI18NForm("Fysiikka"), true, true, true, false),
                new SubjectRow("KE", createI18NForm("Kemia"), true, true, true, false),
                new SubjectRow("TE", createI18NForm("Terveystieto"), true, true, true, false),
                new SubjectRow("KT", createI18NForm("Uskonto tai elämänkatsomustieto"), true, true, true, false),
                new SubjectRow("HI", createI18NForm("Historia"), true, true, true, false),
                new SubjectRow("YH", createI18NForm("Yhteiskuntaoppi"), true, true, true, false),
                new SubjectRow("MU", createI18NForm("Musiikki"), true, true, true, false),
                new SubjectRow("KU", createI18NForm("Kuvataide"), true, true, true, false),
                new SubjectRow("KS", createI18NForm("Käsityö"), true, true, true, false),
                new SubjectRow("LI", createI18NForm("Liikunta"), true, true, true, false)
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

        this.listOfLanguageAndLiterature = ImmutableList.of(
                new Option("FI", createI18NForm("Suomi äidinkielenä"), "FI"),
                new Option("SV", createI18NForm("Ruotsi äidinkielenä"), "SV"),
                new Option("SE", createI18NForm("Saame äidinkielenä"), "SE"),
                new Option("RI", createI18NForm("Romani äidinkielenä"), "RI"),
                new Option("VK", createI18NForm("Viittomakieli äidinkielenä"), "VK"),
                new Option("XX", createI18NForm("Muu oppilaan äidinkieli"), "XX"),
                new Option("FI_2", createI18NForm("Suomi toisena kielenä"), "FI_2"),
                new Option("SV_2", createI18NForm("Ruotsi toisena kielenä"), "SV_2"),
                new Option("FI_SE", createI18NForm("Suomi saamenkielisille"), "FI_SE"),
                new Option("FI_VK", createI18NForm("Suomi viittomakielisille"), "FI_VK"),
                new Option("SV_VK", createI18NForm("Ruotsi viittomakielisille"), "SV_VK"));
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
    public List<Option> getSubjectLanguages() {
        return this.listOfLanguages;
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
    public List<Option> getLanguageAndLiterature() {
        return this.listOfLanguageAndLiterature;
    }

    @Override
    public List<Option> getMunicipalities() {
        return this.listOfMunicipalities;
    }

    private static PostOffice createPostOffice(final String postCode, final String text) {
        return new PostOffice(postCode, ElementUtil.createI18NForm(text));
    }
}
