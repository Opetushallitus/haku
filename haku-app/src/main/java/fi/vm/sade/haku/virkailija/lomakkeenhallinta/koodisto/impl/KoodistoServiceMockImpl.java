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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.PostOffice;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

@Service
@Profile(value = {"dev", "it"})
public class KoodistoServiceMockImpl implements KoodistoService {

    public static final String LEARNING_INSTITUTION_TYPE = "Yliopistot";
    public static final String ORGANIZATION_TYPE = "Toimipiste";
    private static final String RUOTSI = "SWE";
    private static final String SUOMI = "FIN";
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
    public final List<Option> listOfGenders;
    public final List<Option> listOfKausi;
    public final List<Code> listOfBaseEducationCodes;
    // koodisto uri -> codes
    public Map<String, List<Code>> codes = Maps.newHashMap();
    public static final String BASE_EDUCATION_KOODISTO_URI = "pohjakoulutustoinenaste";

    public KoodistoServiceMockImpl() {
        List<Option> listOfGradeGrades = new ArrayList<Option>();
        listOfGradeGrades.add(new Option(createI18NAsIs("Ei arvosanaa"), "0"));
        listOfGradeGrades.add(new Option(createI18NAsIs("10"), "10"));
        listOfGradeGrades.add(new Option(createI18NAsIs("9"), "9"));
        listOfGradeGrades.add(new Option(createI18NAsIs("8"), "8"));
        listOfGradeGrades.add(new Option(createI18NAsIs("7"), "7"));
        listOfGradeGrades.add(new Option(createI18NAsIs("6"), "6"));
        listOfGradeGrades.add(new Option(createI18NAsIs("5"), "5"));
        listOfGradeGrades.add(new Option(createI18NAsIs("4"), "4"));
        this.listOfGradeGrades = ImmutableList.copyOf(listOfGradeGrades);

        this.listOfPostOffices = ImmutableList.of(
                createPostOffice("00100", "Helsinki"),
                createPostOffice("02100", "Espoo"),
                createPostOffice("33100", "Tampere"));

        this.listOfSubjects = ImmutableList.of(
                new SubjectRow("A1", createI18NAsIs("A1-kieli"), true, true, true, true),
                new SubjectRow("A12", createI18NAsIs("A12-kieli"), true, true, true, true),
                new SubjectRow("A2", createI18NAsIs("A2-kieli"), true, true, true, true),
                new SubjectRow("A22", createI18NAsIs("A22-kieli"), true, true, true, true),
                new SubjectRow("AI", createI18NAsIs("Äidinkieli ja kirjallisuus"), true, true, true, false),
                new SubjectRow("AI2", createI18NAsIs("Valinnainen Äidinkieli ja kirjallisuus"), true, true, true, false),
                new SubjectRow("B1", createI18NAsIs("B1-kieli"), true, true, true, true),
                new SubjectRow("B2", createI18NAsIs("B2-kieli"), true, true, true, true),
                new SubjectRow("B22", createI18NAsIs("B22-kieli"), true, true, true, true),
                new SubjectRow("B23", createI18NAsIs("B23-kieli"), true, true, true, true),
                new SubjectRow("B3", createI18NAsIs("B3-kieli"), true, true, true, true),
                new SubjectRow("MA", createI18NAsIs("Matematiikka"), true, true, true, false),
                new SubjectRow("BI", createI18NAsIs("Biologia"), true, true, true, false),
                new SubjectRow("GE", createI18NAsIs("Maantieto"), true, true, true, false),
                new SubjectRow("FY", createI18NAsIs("Fysiikka"), true, true, true, false),
                new SubjectRow("KE", createI18NAsIs("Kemia"), true, true, true, false),
                new SubjectRow("TE", createI18NAsIs("Terveystieto"), true, true, true, false),
                new SubjectRow("KT", createI18NAsIs("Uskonto tai elämänkatsomustieto"), true, true, true, false),
                new SubjectRow("HI", createI18NAsIs("Historia"), true, true, true, false),
                new SubjectRow("YH", createI18NAsIs("Yhteiskuntaoppi"), true, true, true, false),
                new SubjectRow("MU", createI18NAsIs("Musiikki"), true, true, true, false),
                new SubjectRow("KU", createI18NAsIs("Kuvataide"), true, true, true, false),
                new SubjectRow("KS", createI18NAsIs("Käsityö"), true, true, true, false),
                new SubjectRow("LI", createI18NAsIs("Liikunta"), true, true, true, false)
        );

        this.listOfLearningInstitutionTypes = ImmutableList.of(
                new Option(createI18NAsIs(LEARNING_INSTITUTION_TYPE), LEARNING_INSTITUTION_TYPE));
        this.listOfOrganizationTypes =
                ImmutableList.of(
                        new Option(createI18NAsIs(ORGANIZATION_TYPE), ORGANIZATION_TYPE));

        this.listOfCountries =
                ImmutableList.of(
                        new Option(createI18NAsIs("Suomi"), SUOMI),
                        new Option(createI18NAsIs("Ruotsi"), RUOTSI));

        this.listOfLanguages =
                ImmutableList.of(
                        new Option(createI18NAsIs("Suomi"), "FI"),
                        new Option(createI18NAsIs("Ruotsi"), "SV"),
                        new Option(createI18NAsIs("Saame"), "SE"),
                        new Option(createI18NAsIs("Englanti"), "EN"));

        this.listOfNationalities =
                ImmutableList.of(
                        new Option(createI18NAsIs("Suomi"), SUOMI),
                        new Option(createI18NAsIs("Ruotsi"), RUOTSI));

        this.listOfMunicipalities = ImmutableList.of(
                new Option(createI18NAsIs("Jalasjärvi"), "jalasjarvi"),
                new Option(createI18NAsIs("Janakkala"), "janakkala"),
                new Option(createI18NAsIs("Joensuu"), "joensuu"),
                new Option(createI18NAsIs("jokioinen"), "jokioinen"),
                new Option(createI18NAsIs("Jomala"), "jomala")
        );

        this.listOfLanguageAndLiterature = ImmutableList.of(
                new Option(createI18NAsIs("Suomi äidinkielenä"), "FI"),
                new Option(createI18NAsIs("Ruotsi äidinkielenä"), "SV"),
                new Option(createI18NAsIs("Saame äidinkielenä"), "SE"),
                new Option(createI18NAsIs("Romani äidinkielenä"), "RI"),
                new Option(createI18NAsIs("Viittomakieli äidinkielenä"), "VK"),
                new Option(createI18NAsIs("Muu oppilaan äidinkieli"), "XX"),
                new Option(createI18NAsIs("Suomi toisena kielenä"), "FI_2"),
                new Option(createI18NAsIs("Ruotsi toisena kielenä"), "SV_2"),
                new Option(createI18NAsIs("Suomi saamenkielisille"), "FI_SE"),
                new Option(createI18NAsIs("Suomi viittomakielisille"), "FI_VK"),
                new Option(createI18NAsIs("Ruotsi viittomakielisille"), "SV_VK"));

        this.listOfGenders =
                ImmutableList.of(
                        new Option(createI18NAsIs("Mies"), "1"),
                        new Option(createI18NAsIs("Nainen"), "2"));

        this.listOfKausi =
                ImmutableList.of(
                        new Option(createI18NAsIs("Kevät"), "kausi_k"),
                        new Option(createI18NAsIs("Syksy"), "kausi_s"));

        this.listOfBaseEducationCodes = ImmutableList.of(
                new Code(ULKOMAINEN_TUTKINTO, createI18NAsIs("Ulkomailla suoritettu koulutus")),
                new Code(PERUSKOULU, createI18NAsIs("Perusopetuksen oppimäärä")),
                new Code(OSITTAIN_YKSILOLLISTETTY, createI18NAsIs("Perusopetuksen osittain yksilöllistetty oppimäärä")),
                new Code(ERITYISOPETUKSEN_YKSILOLLISTETTY, createI18NAsIs("Perusopetuksen yksilöllistetty oppimäärä, opetus järjestetty toiminta-alueittain")),
                new Code(YKSILOLLISTETTY, createI18NAsIs("Perusopetuksen pääosin tai kokonaan yksilöllistetty oppimäärä")),
                new Code(KESKEYTYNYT, createI18NAsIs("Oppivelvollisuuden suorittaminen keskeytynyt (ei päättötodistusta)")),
                new Code("8", createI18NAsIs("Ammatillinen tutkinto")),
                new Code(YLIOPPILAS, createI18NAsIs("Lukion päättötodistus, ylioppilastutkinto tai abiturientti"))
        );

        this.codes.put(BASE_EDUCATION_KOODISTO_URI, this.listOfBaseEducationCodes);
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
    public List<Code> getCodes(String koodistoUrl, int version) {
        return this.codes.get(koodistoUrl);
    }

    @Override
    public List<Option> getMunicipalities() {
        return this.listOfMunicipalities;
    }

    @Override
    public List<Option> getGenders() {
        return listOfGenders;
    }

    @Override
    public List<Option> getHakukausi() {
        return listOfKausi;
    }

    private static PostOffice createPostOffice(final String postCode, final String text) {
        return new PostOffice(postCode, ElementUtil.createI18NAsIs(text));
    }
}
