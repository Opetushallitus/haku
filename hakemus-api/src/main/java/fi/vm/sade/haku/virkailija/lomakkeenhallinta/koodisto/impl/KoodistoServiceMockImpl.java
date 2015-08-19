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
import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

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
    public final List<Option> listOfKorkeakoulus;
    public final List<Option> listOfHakukohdekoodit;
    public final List<Option> listOfLaajuusYksikot;
    public final List<Option> listOfKorkealuTutkintotasot;
    public final List<Option> listOfOpintoalat;
    public final List<Option> listOfKoulutusalat;
    public final List<Code> listOfBaseEducationCodes;
    public final List<Code> listOfYliopistokoulutukset;
    public final List<Code> listOfAMKkoulutukset;
    public final List<Code> listOfYlemmatAMKkoulutukset;
    private final List<Option> listOfAmmatillisentutkinnonArvosteluasteikko;
    public final List<Option> listOfAmmattioppilaitosKoulukoodit;
    public final List<Option> listOfAmmattitutkinnot;

    // koodisto uri -> codes
    public Map<String, List<Code>> codes = Maps.newHashMap();
    public static final String BASE_EDUCATION_KOODISTO_URI = "pohjakoulutustoinenaste";

    public KoodistoServiceMockImpl() {
        this.listOfGradeGrades = ImmutableList.of(
                getOption("Ei arvosanaa", "0"),
                getOption("10", "10"),
                getOption("9", "9"),
                getOption("8", "8"),
                getOption("7", "7"),
                getOption("6", "6"),
                getOption("5", "5"),
                getOption("4", "4"));

        this.listOfPostOffices = ImmutableList.of(
                getOption("Helsinki", "00100"),
                getOption("Espoo", "02100"),
                getOption("Tampere", "33100"));

        this.listOfLearningInstitutionTypes = ImmutableList.of(
                getOption("Oppisopimustoimipiste", "04"),
                getOption("Muu organisaatio", "05"),
                getOption("Oppilaitos", "02"),
                getOption("Koulutustoimija", "01"),
                getOption("Toimipiste", "03")

        );

        this.listOfOrganizationTypes =
                ImmutableList.of(
                        getOption("Oppisopimustoimipiste", "04"),
                        getOption("Muu organisaatio", "05"),
                        getOption("Oppilaitos", "02"),
                        getOption("Koulutustoimija", "01"),
                        getOption("Toimipiste", "03"));

        this.listOfCountries =
                ImmutableList.of(
                        getOption("Suomi", SUOMI),
                        getOption("Ruotsi", RUOTSI));

        this.listOfLanguages =
                ImmutableList.of(
                        getOption("Suomi", "FI"),
                        getOption("Ruotsi", "SV"),
                        getOption("Saame", "SE"),
                        getOption("Englanti", "EN"),
                        getOption("Muu", OppijaConstants.EDUCATION_LANGUAGE_OTHER) );

        this.listOfTeachingLanguages =
                ImmutableList.of(
                        getOption("Suomi", "FI"),
                        getOption("Ruotsi", "SV"),
                        getOption("Saame", "SE"));

        this.listOfNationalities =
                ImmutableList.of(
                        getOption("Suomi", SUOMI),
                        getOption("Ruotsi", RUOTSI));

        this.listOfMunicipalities = ImmutableList.of(
                getOption("Jalasjärvi", "jalasjarvi"),
                getOption("Janakkala", "janakkala"),
                getOption("Joensuu", "joensuu"),
                getOption("jokioinen", "jokioinen"),
                getOption("Jomala", "jomala")
        );

        this.listOfLanguageAndLiterature = ImmutableList.of(
                getOption("Suomi äidinkielenä", "FI"),
                getOption("Ruotsi äidinkielenä", "SV"),
                getOption("Saame äidinkielenä", "SE"),
                getOption("Romani äidinkielenä", "RI"),
                getOption("Viittomakieli äidinkielenä", "VK"),
                getOption("Muu oppilaan äidinkieli", "XX"),
                getOption("Suomi toisena kielenä", "FI_2"),
                getOption("Ruotsi toisena kielenä", "SV_2"),
                getOption("Suomi saamenkielisille", "FI_SE"),
                getOption("Suomi viittomakielisille", "FI_VK"),
                getOption("Ruotsi viittomakielisille", "SV_VK"));

        this.listOfGenders =
                ImmutableList.of(
                        getOption("Mies", "1"),
                        getOption("Nainen", "2"));

        this.listOfKausi =
                ImmutableList.of(
                        getOption("Kevät", "kausi_k"),
                        getOption("Syksy", "kausi_s"));

        this.listOfLukios =
                ImmutableList.of(
                        getOption("Aktiivi-instituutti", "1.2.246.562.10.56695937518"),
                        getOption("Alajärven lukio", "1.2.246.562.10.54943480589"),
                        getOption("Alavuden lukio", "1.2.246.562.10.328060821310"),
                        getOption("Alkio-opisto", "1.2.246.562.10.77255241653")
                );

        this.listOfKorkeakoulus =
                ImmutableList.of(
                        getOption("Aktiivi-instituutti", "1.2.246.562.10.56695937518"),
                        getOption("Alajärven lukio", "1.2.246.562.10.54943480589"),
                        getOption("Alavuden lukio", "1.2.246.562.10.328060821310"),
                        getOption("Alkio-opisto", "1.2.246.562.10.77255241653")
                );

        this.listOfHakukohdekoodit =
                ImmutableList.of(
                        getOption("Kaivosalan perustutkinto, pk", "123"),
                        getOption("Kone- ja metallialan perustutkinto, pk", "857"),
                        getOption("hakukohde 019", "019"),
                        getOption("Notfound", "xxx"));

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

        this.listOfLaajuusYksikot = ImmutableList.of(
                getOption("opintoviikkoa", "1"),
                getOption("opintopistettä", "2"),
                getOption("tuntia", "5")
        );

        this.listOfYliopistokoulutukset = ImmutableList.of(
                new Code("723111", createI18NAsIs("Tohtorin tutkinto")),
                new Code("726301", createI18NAsIs("Tohtorin tutkinto")),
                new Code("726402", createI18NAsIs("Tohtorin tutkinto")),
                new Code("726403", createI18NAsIs("Tohtorin tutkinto")),
                new Code("726701", createI18NAsIs("Tohtorin tutkinto"))
        );

        this.listOfAMKkoulutukset = ImmutableList.of(
                new Code("222222", createI18NAsIs("Amkkarin tutkinto")),
                new Code("222223", createI18NAsIs("Ylempi Amkkarin tutkinto"))
        );

        this.listOfYlemmatAMKkoulutukset = ImmutableList.of(
                new Code("222223", createI18NAsIs("Ylempi Amkkarin tutkinto"))
        );

        this.listOfKorkealuTutkintotasot = ImmutableList.of(
                getOption("Masteri", "1")
        );
        this.listOfAmmatillisentutkinnonArvosteluasteikko = ImmutableList.of(
                getOption( "1-3", "1-3"),
                getOption( "1-5", "1-5"),
                getOption( "4-10", "4-10")
        );
        this.listOfOpintoalat = ImmutableList.of(
                getOption("Esiopetus", "001"),
                getOption("Perusopetusopetus", "002")
        );
        this.listOfKoulutusalat = ImmutableList.of(
                getOption("Yleissivistävä koulutus", "1"),
                getOption("Humanistinen ja kasvatusala", "2")
        );
        this.codes.put(BASE_EDUCATION_KOODISTO_URI, this.listOfBaseEducationCodes);

        this.listOfAmmattioppilaitosKoulukoodit = ImmutableList.of(
                getOption("", ""),
                getOption("Ammattikoulu", "1.2.246.562.10.57118763500"),
                getOption("Tuntematon koulu", "1.2.246.562.10.57118763579")
        );

        this.listOfAmmattitutkinnot = ImmutableList.of(
                getOption("", ""),
                getOption("Muu tutkinto", "399999"),
                getOption("Ammattitutkinto", "400000")
        );
    }

    private Option getOption(final String labelKey, final String value) {
        return (Option) OptionBuilder.Option(labelKey).setValue(value).build();
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
    public List<Code> getYliopistokoulutukset() {
        return this.listOfYliopistokoulutukset;
    }

    @Override
    public List<Option> getLukioKoulukoodit() {
        return this.listOfLukios;
    }

    @Override
    public List<Option> getAmmattioppilaitosKoulukoodit() {
        return this.listOfAmmattioppilaitosKoulukoodit;
    }

    @Override
    public List<Option> getAmmattitutkinnot() {
        return this.listOfAmmattitutkinnot;
    }

    @Override
    public List<Option> getKorkeakouluKoulukoodit() {
        return this.listOfKorkeakoulus;
    }

    @Override
    public List<Option> getHakukohdekoodit() {
        return this.listOfHakukohdekoodit;
    }

    @Override
    public List<Option> getLaajuusYksikot() {
        return this.listOfLaajuusYksikot;
    }

    @Override
    public List<Option> getKorkeakouluTutkintotasot() {
        return this.listOfKorkealuTutkintotasot;
    }

    @Override
    public List<Code> getAMKkoulutukset() {
        return this.listOfAMKkoulutukset;
    }

    @Override
    public List<Code> getYlemmatAMKkoulutukset() {
        return this.listOfYlemmatAMKkoulutukset;
    }

    @Override
    public List<Option> getAmmatillisenTutkinnonArvosteluasteikko() {
        return this.listOfAmmatillisentutkinnonArvosteluasteikko;
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

    @Override
    public List<Option> getOpintoalat() {
        return listOfOpintoalat;
    }

    @Override
    public List<Option> getOpintoalat(String koulutusala) {
        return getOpintoalat();
    }

    @Override
    public List<Option> getKoulutusalat() {
        return listOfKoulutusalat;
    }
}
