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
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
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

    private static final String RUOTSI = "SWE";
    private static final String SUOMI = "FIN";
    public final List<Option> listOfGradeGrades;
    public final List<Option> listOfPostOffices;
    public final List<Option> listOfLearningInstitutionTypes;
    public final List<Option> listOfOrganizationTypes;
    public final List<Option> listOfCountries;
    public final List<Option> listOfLanguages;
    public final List<Option> listOfTeachingLanguages;
    public final List<Option> listOfLanguageAndLiterature;
    public final List<Option> listOfNationalities;
    public final List<Option> listOfMunicipalities;
    public final List<Option> listOfGenders;
    public final List<Option> listOfKausi;
    public final List<Option> listOfLukios;
    public final List<Option> listOfHakukohdekoodit;
    public final List<Code> listOfBaseEducationCodes;
    // koodisto uri -> codes
    public Map<String, List<Code>> codes = Maps.newHashMap();
    public static final String BASE_EDUCATION_KOODISTO_URI = "pohjakoulutustoinenaste";

    public KoodistoServiceMockImpl() {
        List<Option> listOfGradeGrades = new ArrayList<Option>();
        listOfGradeGrades.add(new OptionBuilder().setI18nText(createI18NAsIs("Ei arvosanaa")).setValue("0").createOption());
        listOfGradeGrades.add(new OptionBuilder().setI18nText(createI18NAsIs("10")).setValue("10").createOption());
        listOfGradeGrades.add(new OptionBuilder().setI18nText(createI18NAsIs("9")).setValue("9").createOption());
        listOfGradeGrades.add(new OptionBuilder().setI18nText(createI18NAsIs("8")).setValue("8").createOption());
        listOfGradeGrades.add(new OptionBuilder().setI18nText(createI18NAsIs("7")).setValue("7").createOption());
        listOfGradeGrades.add(new OptionBuilder().setI18nText(createI18NAsIs("6")).setValue("6").createOption());
        listOfGradeGrades.add(new OptionBuilder().setI18nText(createI18NAsIs("5")).setValue("5").createOption());
        listOfGradeGrades.add(new OptionBuilder().setI18nText(createI18NAsIs("4")).setValue("4").createOption());
        this.listOfGradeGrades = ImmutableList.copyOf(listOfGradeGrades);

        this.listOfPostOffices = ImmutableList.of(
                new OptionBuilder().setI18nText(createI18NAsIs("Helsinki")).setValue("00100").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Espoo")).setValue("02100").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Tampere")).setValue("33100").createOption());

        this.listOfLearningInstitutionTypes = ImmutableList.of(
                new OptionBuilder().setI18nText(createI18NAsIs("Oppisopimustoimipiste")).setValue("04").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Muu organisaatio")).setValue("05").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Oppilaitos")).setValue("02").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Koulutustoimija")).setValue("01").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Toimipiste")).setValue("03").createOption()

        );

        this.listOfOrganizationTypes =
                ImmutableList.of(
                        new OptionBuilder().setI18nText(createI18NAsIs("Oppisopimustoimipiste")).setValue("04").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Muu organisaatio")).setValue("05").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Oppilaitos")).setValue("02").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Koulutustoimija")).setValue("01").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Toimipiste")).setValue("03").createOption());

        this.listOfCountries =
                ImmutableList.of(
                        new OptionBuilder().setI18nText(createI18NAsIs("Suomi")).setValue(SUOMI).createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Ruotsi")).setValue(RUOTSI).createOption());

        this.listOfLanguages =
                ImmutableList.of(
                        new OptionBuilder().setI18nText(createI18NAsIs("Suomi")).setValue("FI").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Ruotsi")).setValue("SV").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Saame")).setValue("SE").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Englanti")).setValue("EN").createOption());

        this.listOfTeachingLanguages =
                ImmutableList.of(
                        new OptionBuilder().setI18nText(createI18NAsIs("Suomi")).setValue("FI").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Ruotsi")).setValue("SV").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Saame")).setValue("SE").createOption());

        this.listOfNationalities =
                ImmutableList.of(
                        new OptionBuilder().setI18nText(createI18NAsIs("Suomi")).setValue(SUOMI).createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Ruotsi")).setValue(RUOTSI).createOption());

        this.listOfMunicipalities = ImmutableList.of(
                new OptionBuilder().setI18nText(createI18NAsIs("Jalasjärvi")).setValue("jalasjarvi").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Janakkala")).setValue("janakkala").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Joensuu")).setValue("joensuu").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("jokioinen")).setValue("jokioinen").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Jomala")).setValue("jomala").createOption()
        );

        this.listOfLanguageAndLiterature = ImmutableList.of(
                new OptionBuilder().setI18nText(createI18NAsIs("Suomi äidinkielenä")).setValue("FI").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Ruotsi äidinkielenä")).setValue("SV").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Saame äidinkielenä")).setValue("SE").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Romani äidinkielenä")).setValue("RI").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Viittomakieli äidinkielenä")).setValue("VK").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Muu oppilaan äidinkieli")).setValue("XX").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Suomi toisena kielenä")).setValue("FI_2").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Ruotsi toisena kielenä")).setValue("SV_2").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Suomi saamenkielisille")).setValue("FI_SE").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Suomi viittomakielisille")).setValue("FI_VK").createOption(),
                new OptionBuilder().setI18nText(createI18NAsIs("Ruotsi viittomakielisille")).setValue("SV_VK").createOption());

        this.listOfGenders =
                ImmutableList.of(
                        new OptionBuilder().setI18nText(createI18NAsIs("Mies")).setValue("1").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Nainen")).setValue("2").createOption());

        this.listOfKausi =
                ImmutableList.of(
                        new OptionBuilder().setI18nText(createI18NAsIs("Kevät")).setValue("kausi_k").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Syksy")).setValue("kausi_s").createOption());

        this.listOfLukios =
                ImmutableList.of(
                        new OptionBuilder().setI18nText(createI18NAsIs("Aktiivi-instituutti")).setValue("1.2.246.562.10.56695937518").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Alajärven lukio")).setValue("1.2.246.562.10.54943480589").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Alavuden lukio")).setValue("1.2.246.562.10.328060821310").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Alkio-opisto")).setValue("1.2.246.562.10.77255241653").createOption()
                );

        this.listOfHakukohdekoodit =
                ImmutableList.of(
                        new OptionBuilder().setI18nText(createI18NAsIs("Kaivosalan perustutkinto, pk")).setValue("123").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Kone- ja metallialan perustutkinto, pk")).setValue("857").createOption(),
                        new OptionBuilder().setI18nText(createI18NAsIs("Notfound")).setValue("xxx").createOption()
                );

        this.listOfBaseEducationCodes = ImmutableList.of(
                new Code(ULKOMAINEN_TUTKINTO, createI18NAsIs("Ulkomailla suoritettu koulutus")),
                new Code(PERUSKOULU, createI18NAsIs("Perusopetuksen oppimäärä")),
                new Code(OSITTAIN_YKSILOLLISTETTY, createI18NAsIs("Perusopetuksen osittain yksilöllistetty oppimäärä")),
                new Code(ALUEITTAIN_YKSILOLLISTETTY, createI18NAsIs("Perusopetuksen yksilöllistetty oppimäärä, opetus järjestetty toiminta-alueittain")),
                new Code(YKSILOLLISTETTY, createI18NAsIs("Perusopetuksen pääosin tai kokonaan yksilöllistetty oppimäärä")),
                new Code(KESKEYTYNYT, createI18NAsIs("Oppivelvollisuuden suorittaminen keskeytynyt (ei päättötodistusta)")),
                new Code("8", createI18NAsIs("Ammatillinen tutkinto")),
                new Code(YLIOPPILAS, createI18NAsIs("Lukion päättötodistus, ylioppilastutkinto tai abiturientti"))
        );

        this.codes.put(BASE_EDUCATION_KOODISTO_URI, this.listOfBaseEducationCodes);
    }

    @Override
    public List<Option> getPostOffices() {
        return this.listOfPostOffices;
    }

    @Override
    public List<SubjectRow> getSubjects() {
        return ImmutableList.of(
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
    public List<Option> getLukioKoulukoodit() {
        return this.listOfLukios;
    }

    @Override
    public List<Option> getHakukohdekoodit() {
        return this.listOfHakukohdekoodit;
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

    @Override
    public List<Option> getTeachingLanguages() {
        return listOfTeachingLanguages;
    }
}
