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

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;

public class KoodistoServiceMockImpl implements KoodistoService {

    private final List<Option> listOfGradeGrades;
    private final List<PostOffice> postOffices;
    private final List<SubjectRow> listOfSubjectsRows;

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

        List<PostOffice> postOffices = new ArrayList<PostOffice>();
        postOffices.add(createPostOffice("00100", "Helsinki"));
        postOffices.add(createPostOffice("02100", "Espoo"));
        postOffices.add(createPostOffice("33100", "Tampere"));
        this.postOffices = ImmutableList.copyOf(postOffices);

        this.listOfSubjectsRows = ImmutableList.copyOf(new ArrayList<SubjectRow>());
    }

    @Override
    public List<PostOffice> getPostOffices() {
        return this.postOffices;
    }

    @Override
    public List<SubjectRow> getSubjects() {
        return this.listOfSubjectsRows;
    }

    @Override
    public List<Option> getGradeRanges() {
        return listOfGradeGrades;
    }

    private static PostOffice createPostOffice(final String postCode, final String text) {
        return new PostOffice(postCode, ElementUtil.createI18NText(text));
    }
}
