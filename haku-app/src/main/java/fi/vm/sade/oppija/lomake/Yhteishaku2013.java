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

package fi.vm.sade.oppija.lomake;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.PostOffice;
import fi.vm.sade.oppija.lomake.domain.elements.*;
import fi.vm.sade.oppija.lomake.domain.elements.custom.*;
import fi.vm.sade.oppija.lomake.domain.elements.questions.*;
import fi.vm.sade.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;

@Service
@SuppressWarnings("all")
public class Yhteishaku2013 {

    public static final String ASID = "1.2.246.562.5.50476818906";

    private final ApplicationPeriod applicationPeriod;
    public static String mobilePhonePattern =
            "^$|^(?!\\+358|0)[\\+]?[0-9\\-\\s]+$|^(\\+358|0)[\\-\\s]*((4[\\-\\s]*[0-6])|50)[0-9\\-\\s]*$";

    private final KoodistoService koodistoService;

    @Autowired // NOSONAR
    public Yhteishaku2013(final KoodistoService koodistoService) { // NOSONAR
        this.koodistoService = koodistoService;
        this.applicationPeriod = new ApplicationPeriod(ASID);
        Phase henkilotiedot = new Phase("henkilotiedot", createI18NText("Henkilötiedot"), false);
        Phase koulutustausta = new Phase("koulutustausta", createI18NText("Koulutustausta"), false);
        Phase hakutoiveet = new Phase("hakutoiveet", createI18NText("Hakutoiveet"), false);
        Phase arvosanat = new Phase("arvosanat", createI18NText("Arvosanat"), false);
        Phase lisatiedot = new Phase("lisatiedot", createI18NText("Lisätiedot"), false);
        Phase esikatselu = new Phase("esikatselu", createI18NText("Esikatselu"), true);

        Form form = new Form("yhteishaku",
                createI18NText("Ammatillisen koulutuksen ja lukiokoulutuksen yhteishaku, syksy 2013"));
        form.addChild(henkilotiedot);
        form.addChild(koulutustausta);
        form.addChild(hakutoiveet);
        form.addChild(arvosanat);
        form.addChild(lisatiedot);
        form.addChild(esikatselu);
        form.init();

        applicationPeriod.addForm(form);

        Map<String, List<Question>> lisakysymysMap = new HashMap<String, List<Question>>();
        Map<String, List<Question>> oppiaineMap = new HashMap<String, List<Question>>();

        List<Question> oppiaineList = new ArrayList<Question>();
        oppiaineList.add(new SubjectRow("tietotekniikka", false, false, createI18NText("Tietotekniikka")));
        oppiaineList.add(new SubjectRow("kansantaloustiede", false, false, createI18NText("Kansantaloustiede")));
        oppiaineMap.put("1.2.246.562.14.79893512065", oppiaineList);

        final String id = "1.2.246.562.14.79893512065";
        final String elementIdPrefix = id.replace('.', '_');
        Radio radio = new Radio(
                elementIdPrefix + "_additional_question_1",
                createI18NText("Tällä alalla on terveydentilavaatimuksia, jotka voivat olla opiskelijan ottamisen esteenä. " +
                        "Onko sinulla terveydellisiä " +
                        "tekijöitä, jotka voivat olla opiskelijatksi ottamisen esteenä?"));
        radio.addOption(id + "_q1_option_1", createI18NText("Ei"), "q1_option_1");
        radio.addOption(id + "_q1_option_2", createI18NText("Kyllä. Ymmärrä, etten tästä johtuen ehkä tule valituksi"),
                "q1_option_2");

        Radio radio2 = new Radio(
                elementIdPrefix + "_additional_question_2",
                createI18NText("Tässä koulutuksessa opiskelijaksi ottamisen esteenä voi olla eiempi päätös " +
                        "opiskeluoikeuden peruuttamisessa. Onko " +
                        "opiskeluoikeutesi aiemmin peruutettu terveydentilasi tai muiden henkilöiden " +
                        "turvallisuuden vaarantamisen takia?"));
        radio2.addOption(id + "_q2_option_1", createI18NText("Ei"), "q2_option_1");
        radio2.addOption(id + "_q2_option_2",
                createI18NText("Kyllä. Ymmärrä, etten tästä johtuen ehkä tule valituksi"), "q2_option_2");

        Radio radio3 = new Radio(
                elementIdPrefix + "_additional_question_3",
                createI18NText("Jos olet osallistunut saman alan pääsykokeeseen, " +
                        "niin haluatko käyttää hyväksyttyjä koetuloksiasi?"));
        radio3.addOption(elementIdPrefix + "_q3_option_1",
                createI18NText("En, en ole osallistunut pääsykokeeseen"), "q3_option_1");
        radio3.addOption(elementIdPrefix + "_q3_option_2",
                createI18NText("Ei, en halua käyttää tuloksia"), "q3_option_2");
        radio3.addOption(elementIdPrefix + "_q3_option_3",
                createI18NText("Kyllä, haluan käyttää pääsykoetuloksia"), "q3_option_3");

        List<Question> lisakysymysList = new ArrayList<Question>();
        lisakysymysList.add(radio);
        lisakysymysList.add(radio2);
        lisakysymysList.add(radio3);
        lisakysymysMap.put(id, lisakysymysList);

        Theme henkilotiedotRyhma = new Theme("HenkilotiedotGrp", createI18NText("Henkilötiedot"), null);
        Theme koulutustaustaRyhma = new Theme("KoulutustaustaGrp", createI18NText("Koulutustausta"), null);
        Theme hakutoiveetRyhma = new Theme("hakutoiveetGrp", createI18NText("Hakutoiveet"), lisakysymysMap);
        Theme arvosanatRyhma = new Theme("arvosanatGrp", createI18NText("Arvosanat"), oppiaineMap);
        WorkExperienceTheme tyokokemusRyhma = new WorkExperienceTheme("tyokokemusGrp", createI18NText("Työkokemus"),
                null, "32");
        Theme lupatiedotRyhma = new Theme("lupatiedotGrp", createI18NText("Lupatiedot"), null);
        Theme yhteenvetoRyhma = new Theme("yhteenvetoGrp", createI18NText("yhteenveto"), null);

        henkilotiedot.addChild(henkilotiedotRyhma);
        koulutustausta.addChild(koulutustaustaRyhma);
        hakutoiveet.addChild(hakutoiveetRyhma);
        arvosanat.addChild(arvosanatRyhma);
        lisatiedot.addChild(tyokokemusRyhma);
        lisatiedot.addChild(lupatiedotRyhma);

        DropdownSelect aidinkieli = new DropdownSelect("äidinkieli", createI18NText("Äidinkieli"));
        aidinkieli.addOptions(koodistoService.getLanguages());
        // aidinkieli.addOption("suomi", createI18NText("Suomi"), "fi");
        // aidinkieli.addOption("ruotsi", createI18NText("Ruotsi"), "sv");
        aidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        aidinkieli.addAttribute("required", "required");
        aidinkieli.setVerboseHelp(getVerboseHelp());
        aidinkieli.setInline(true);

        DropdownSelect kansalaisuus = new DropdownSelect("kansalaisuus", createI18NText("Kansalaisuus"));
        kansalaisuus.addOptions(koodistoService.getNationalities());
        setDefaultOption("FI", kansalaisuus.getOptions());
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.addAttribute("required", "required");
        kansalaisuus.setHelp(createI18NText("Jos sinulla on kaksoiskansalaisuus, valitse toinen niistä"));
        kansalaisuus.setVerboseHelp(getVerboseHelp());
        kansalaisuus.setInline(true);

        DropdownSelect kotikunta = new DropdownSelect("kotikunta", createI18NText("Kotikunta"));
        kotikunta.addOptions(koodistoService.getMunicipalities());
//        kotikunta.addOption("jalasjarvi, ", createI18NText("Jalasjärvi"), "Jalasjärvi");
//        kotikunta.addOption("janakkala", createI18NText("Janakkala"), "Janakkala");
//        kotikunta.addOption("joensuu", createI18NText("Joensuu"), "Joensuu");
//        kotikunta.addOption("jokioinen", createI18NText("Jokioinen"), "Jokioinen");
//        kotikunta.addOption("jomala", createI18NText("Jomala"), "Jomala");
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        kotikunta.addAttribute("required", "required");
        kotikunta.setVerboseHelp(getVerboseHelp());
        kotikunta.setInline(true);

        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", createI18NText("Kutsumanimi"));
        kutsumanimi.setHelp(createI18NText("Valitse kutsumanimeksi jokin virallisista etunimistäsi"));
        kutsumanimi.addAttribute("required", "required");
        kutsumanimi.addAttribute("size", "20");
        kutsumanimi.addAttribute("containedInOther", "Etunimet");
        kutsumanimi.setVerboseHelp(getVerboseHelp());
        kutsumanimi.setInline(true);

        TextQuestion email = new TextQuestion("Sähköposti", createI18NText("Sähköpostiosoite"));
        email.addAttribute("size", "50");
        email.addAttribute("pattern", "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^$");
        email.setHelp(createI18NText("Kirjoita tähän sähköpostiosoite, " +
                "johon haluat vastaanottaa opiskelijavalintaan liittyviä tietoja ja jota käytät säännöllisesti. Saat " +
                "vahvistuksen hakemuksen perille menosta tähän sähköpostiosoitteeseen."));
        email.setVerboseHelp(getVerboseHelp());
        email.setInline(true);

        TextQuestion henkilotunnus = new TextQuestion("Henkilotunnus", createI18NText("Henkilötunnus"));
        henkilotunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilotunnus.addAttribute("required", "required");
        henkilotunnus.addAttribute("pattern", "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$");
        henkilotunnus.addAttribute("size", "11");
        henkilotunnus.addAttribute("maxlength", "11");
        henkilotunnus.setVerboseHelp(getVerboseHelp());
        henkilotunnus.setInline(true);

        Radio sukupuoli = new Radio("Sukupuoli", createI18NText("Sukupuoli"));
        sukupuoli.addOption("mies", createI18NText("Mies"), "m");
        sukupuoli.addOption("nainen", createI18NText("Nainen"), "n");
        sukupuoli.addAttribute("required", "required");
        sukupuoli.setVerboseHelp(getVerboseHelp());
        sukupuoli.setInline(true);

        SocialSecurityNumber socialSecurityNumber = new SocialSecurityNumber("ssn_question", createI18NText("Henkilötunnus"),
                sukupuoli.getI18nText(), sukupuoli.getOptions().get(0),
                sukupuoli.getOptions().get(1), sukupuoli.getId(), henkilotunnus);

        DateQuestion syntymaaika = new DateQuestion("syntymaaika", createI18NText("Syntymäaika"));
        syntymaaika.addAttribute("required", "required");
        syntymaaika.setInline(true);

        TextQuestion syntymapaikka = new TextQuestion("syntymapaikka", createI18NText("Syntymäpaikka"));
        syntymapaikka.addAttribute("size", "30");
        syntymapaikka.addAttribute("required", "required");
        syntymapaikka.setInline(true);

        TextQuestion kansallinenIdTunnus = new TextQuestion("kansallinenIdTunnus", createI18NText("Kansallinen ID-tunnus"));
        kansallinenIdTunnus.addAttribute("size", "30");
        kansallinenIdTunnus.addAttribute("required", "required");
        kansallinenIdTunnus.setInline(true);

        TextQuestion passinnumero = new TextQuestion("passinnumero", createI18NText("Passinnumero"));
        passinnumero.addAttribute("size", "30");
        passinnumero.addAttribute("required", "required");
        passinnumero.setInline(true);

        Radio onkoSinullaSuomalainenHetu = new Radio("onkoSinullaSuomalainenHetu",
                createI18NText("Onko sinulla suomalainen henkilötunnus?"));
        onkoSinullaSuomalainenHetu.addOption("true", createI18NText("Kyllä"), "true");
        onkoSinullaSuomalainenHetu.addOption("false", createI18NText("Ei"), "false");
        onkoSinullaSuomalainenHetu.addAttribute("required", "required");
        onkoSinullaSuomalainenHetu.setVerboseHelp(getVerboseHelp());
        onkoSinullaSuomalainenHetu.setInline(true);

        RelatedQuestionRule hetuRule = new RelatedQuestionRule("hetuRule", kansalaisuus.getId(), "^$|^FI$");
        hetuRule.addChild(socialSecurityNumber);
        RelatedQuestionRule ulkomaalaisenTunnisteetRule = new RelatedQuestionRule("ulkomaalaisenTunnisteetRule",
                kansalaisuus.getId(), "(?!FI)([A-Z]{2})");
        ulkomaalaisenTunnisteetRule.addChild(onkoSinullaSuomalainenHetu);

        RelatedQuestionRule suomalainenHetuRule = new RelatedQuestionRule("suomalainenHetuRule",
                onkoSinullaSuomalainenHetu.getId(), "^true");
        suomalainenHetuRule.addChild(socialSecurityNumber);
        onkoSinullaSuomalainenHetu.addChild(suomalainenHetuRule);

        RelatedQuestionRule eiSuomalaistaHetuaRule = new RelatedQuestionRule("eiSuomalaistaHetuaRule",
                onkoSinullaSuomalainenHetu.getId(), "^false");
        eiSuomalaistaHetuaRule.addChild(syntymaaika);
        eiSuomalaistaHetuaRule.addChild(syntymapaikka);
        eiSuomalaistaHetuaRule.addChild(sukupuoli);
        eiSuomalaistaHetuaRule.addChild(kansallinenIdTunnus);
        eiSuomalaistaHetuaRule.addChild(passinnumero);

        onkoSinullaSuomalainenHetu.addChild(eiSuomalaistaHetuaRule);

        Element postinumero = new PostalCode("Postinumero", createI18NText("Postinumero"), createPostOffices());
        postinumero.addAttribute("size", "5");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("placeholder", "#####");
        postinumero.addAttribute("maxlength", "5");
        postinumero.setHelp(createI18NText("Kirjoita tähän osoite, johon haluat vastaanottaan opiskelijavalintaan liittyvää postia, " +
                "kuten kutsun valintakokeeseen tai " +
                "valintapäätöksen."));

        DropdownSelect asuinmaa = new DropdownSelect("asuinmaa", createI18NText("Asuinmaa"));
        Option option = asuinmaa.addOption("valitse", null, "");
        asuinmaa.addOptions(koodistoService.getCountries());

        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        asuinmaa.addAttribute("required", "required");
        asuinmaa.setVerboseHelp(getVerboseHelp());
        asuinmaa.setInline(true);

        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "Lähiosoite", "40");
        lahiosoite.setInline(true);

        CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1",
                createI18NText("Tämä on ensisijainen osoitteeni"));
        ensisijainenOsoite.setInline(true);

        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule1", asuinmaa.getId(), "FI");
        relatedQuestionRule.addChild(lahiosoite);
        relatedQuestionRule.addChild(postinumero);
        relatedQuestionRule.addChild(kotikunta);
        relatedQuestionRule.addChild(ensisijainenOsoite);
        asuinmaa.addChild(relatedQuestionRule);

        TextArea osoite = new TextArea("osoite", createI18NText("Osoite"));
        osoite.addAttribute("required", "required");
        osoite.addAttribute("rows", "6");
        osoite.addAttribute("cols", "40");
        osoite.addAttribute("style", "height: 8em");
        RelatedQuestionRule relatedQuestionRule2 =
                new RelatedQuestionRule("rule2", asuinmaa.getId(), "(?!FI)([A-Z]{2})");
        relatedQuestionRule2.addChild(osoite);
        osoite.setVerboseHelp(getVerboseHelp());
        asuinmaa.addChild(relatedQuestionRule2);
        osoite.setInline(true);

        TextQuestion matkapuhelinnumero = new TextQuestion("matkapuhelinnumero", createI18NText("Matkapuhelinnumero"));
        matkapuhelinnumero.setHelp(createI18NText("Kirjoita tähän matkapuhelinnumerosi, jotta sinuun saadaan tarvittaessa yhteyden."));
        matkapuhelinnumero.addAttribute("size", "30");
        matkapuhelinnumero.addAttribute("pattern", mobilePhonePattern);
        matkapuhelinnumero.setVerboseHelp(getVerboseHelp());
        matkapuhelinnumero.setInline(true);

        TextQuestion huoltajanPuhelinnumero = new TextQuestion("huoltajanPuhelinnumero",
                createI18NText("Huoltajan puhelinnumero"));
        huoltajanPuhelinnumero.setHelp(createI18NText("Kirjoita tähän huoltajan puhelinnumero."));
        huoltajanPuhelinnumero.addAttribute("size", "20");
        huoltajanPuhelinnumero.setVerboseHelp(getVerboseHelp());
        huoltajanPuhelinnumero.setInline(true);

        AddElementRule addHuoltajanPuhelinnumero = new AddElementRule("addHuoltajanPuhelinnumeroRule",
                huoltajanPuhelinnumero.getId(), createI18NText("Lisää huoltajan puhelinnumero"));
        addHuoltajanPuhelinnumero.addChild(huoltajanPuhelinnumero);

        kotikunta.setHelp(createI18NText("Kotikunta on tyypillisesti se kunta, jossa asut."));
        aidinkieli.setHelp(createI18NText("Jos omaa äidinkieltäsi ei löydy valintalistasta, valitse äidinkieleksesi."));

        Question sukunimi = createRequiredTextQuestion("Sukunimi", "Sukunimi", "30");
        sukunimi.setInline(true);
        sukunimi.addAttribute("iso8859name", "iso8859name");
        Question etunimet = createRequiredTextQuestion("Etunimet", "Etunimet", "30");
        etunimet.setInline(true);
        etunimet.addAttribute("iso8859name", "iso8859name");

        henkilotiedotRyhma.addChild(sukunimi).addChild(etunimet).addChild(kutsumanimi).addChild(kansalaisuus)
                .addChild(hetuRule).addChild(ulkomaalaisenTunnisteetRule).addChild(email).addChild(matkapuhelinnumero)
                .addChild(addHuoltajanPuhelinnumero).addChild(asuinmaa).addChild(aidinkieli);

        createKoulutustausta(koulutustaustaRyhma);
        createHakutoiveet(hakutoiveetRyhma);
        createArvosanat(arvosanatRyhma);
        createTyokokemus(tyokokemusRyhma);
        createLupatiedot(lupatiedotRyhma);

        esikatselu.addChild(henkilotiedotRyhma).addChild(koulutustaustaRyhma).addChild(hakutoiveetRyhma)
                .addChild(arvosanatRyhma).addChild(tyokokemusRyhma).addChild(lupatiedotRyhma);

        yhteenvetoRyhma.setHelp(createI18NText("Kiitos, hakemuksesi on vastaanotettu"));

    }

    public GradeGrid createGradeGrid(boolean primary) {

        List<Option> gradeRange = primary ?
                koodistoService.getGradeRangesForPrimary() : koodistoService.getGradeRangesForSecondary();
        List<SubjectRow> subjects = primary ?
                koodistoService.getSubjectsForPrimary() : koodistoService.getSubjectsForSecondary();
        SubjectRow finnish =
                new SubjectRow("subject_finnish", false, false, createI18NText("Äidinkieli ja kirjallisuus"));
        List<SubjectRow> subjectRowsAfter = new ArrayList<SubjectRow>();
        for (SubjectRow subject : subjects) {
            String id = subject.getId();
            if (id.endsWith("AI")) {
                finnish = subject;
            } else if (!(id.endsWith("A1") || id.endsWith("A12") || id.endsWith("A13")
                    || id.endsWith("A2") || id.endsWith("A22") || id.endsWith("23")
                    || id.endsWith("B1") || id.endsWith("B12") || id.endsWith("B13")
                    || id.endsWith("B2") || id.endsWith("B22") || id.endsWith("B23")
                    || id.endsWith("B3") || id.endsWith("B32") || id.endsWith("B33"))) {
                subjectRowsAfter.add(subject);
            }
        }
        List<SubjectRow> subjectRowsBefore = new ArrayList<SubjectRow>();
        subjectRowsBefore.add(finnish);

        LanguageRow a1 = new LanguageRow("lang_a1", createI18NText("A1-kieli"));
        LanguageRow b1 = new LanguageRow("lang_b1", createI18NText("B1-kieli"));

        List<LanguageRow> languageRows = new ArrayList<LanguageRow>();
        languageRows.add(a1);
        languageRows.add(b1);

        List<Option> languageOptions = new ArrayList<Option>();
        languageOptions.add(new Option("langoption_" + "eng", createI18NText("englanti"), "eng"));
        languageOptions.add(new Option("langoption_" + "swe", createI18NText("ruotsi"), "swe"));
        languageOptions.add(new Option("langoption_" + "fra", createI18NText("ranska"), "fra"));
        languageOptions.add(new Option("langoption_" + "ger", createI18NText("saksa"), "ger"));
        languageOptions.add(new Option("langoption_" + "rus", createI18NText("venäjä"), "rus"));
        languageOptions.add(new Option("langoption_" + "fin", createI18NText("suomi"), "fin"));

        List<Option> scopeOptions = new ArrayList<Option>();
        scopeOptions.add(new Option("scopeoption_" + "a1", createI18NText("A1"), "a1"));
        scopeOptions.add(new Option("scopeoption_" + "a2", createI18NText("A2"), "a2"));
        scopeOptions.add(new Option("scopeoption_" + "b1", createI18NText("B1"), "b1"));
        scopeOptions.add(new Option("scopeoption_" + "b2", createI18NText("B2"), "b2"));
        scopeOptions.add(new Option("scopeoption_" + "b3", createI18NText("B3"), "b3"));

        GradeGrid gradeGrid = new GradeGrid("gradegrid", createI18NText("Arvosanat"), "Kieli", subjectRowsBefore,
                languageRows, subjectRowsAfter, scopeOptions, languageOptions, gradeRange);
        gradeGrid.setVerboseHelp(getVerboseHelp());

        return gradeGrid;
    }

    private void createArvosanat(Theme arvosanatRyhma) {
        RelatedQuestionRule relatedQuestionPK = new RelatedQuestionRule("rule_grade_pk", "millatutkinnolla",
                "(tutkinto1|tutkinto2|tutkinto3|tutkinto4)");
        relatedQuestionPK.addChild(createGradeGrid(true));
        arvosanatRyhma.addChild(relatedQuestionPK);

        RelatedQuestionRule relatedQuestionLukio = new RelatedQuestionRule("rule_grade_yo", "millatutkinnolla",
                "(tutkinto6)");
        relatedQuestionLukio.addChild(createGradeGrid(false));
        arvosanatRyhma.addChild(relatedQuestionLukio);

        RelatedQuestionRule relatedQuestionEiTutkintoa = new RelatedQuestionRule("rule_grade_no", "millatutkinnolla",
                "(tutkinto5|tutkinto7)");
        relatedQuestionEiTutkintoa.addChild(new Text("nogradegrid", createI18NText("Sinulta ei kysytä arvosanoja.")));
        arvosanatRyhma.addChild(relatedQuestionEiTutkintoa);

        arvosanatRyhma
                .setHelp(createI18NText("Merkitse arvosanat siitä todistuksesta, " +
                        "jolla haet koulutukseen (perusopetus, tai sitä vastaavat opinnot, lukiokoulutus). " +
                        "Korotetut arvosanat voit merkitä, " +
                        "mikäli olet saanut korotuksista virallisen todistuksen. Huomio. Jos olet suorittanut lukion " +
                        "oppimäärän tai ylioppilastutkinnon, et voi hakea perusopetuksen päättötodistuksella." +
                        " Ammatillisella perustutkinnolla et voi hakea. " +
                        "Oppilaitokset tarkistavat todistukset hyväksytyiksi tulleilta hakijoilta." +
                        " 1. Tarkista ja täydennä taulukkoon todistuksen " +
                        "oppiaineet ja arvosanat, jotka poikkeavat esitäytetyistä. " +
                        "Huom! Valinnaisaineiden arvosanat merkitään vain mikäli niiden " +
                        "laajuus on vähintään kaksi vuosiviikkotuntia perusopetuksen vuosiluokkien 7-9 aikana." +
                        " Jos sinulla on yksilöllistettyjä " +
                        "arvosanoja, valitse listasta arvosana, jossa on tähti."));

    }

    private void createHakutoiveet(Theme hakutoiveetRyhma) {
        hakutoiveetRyhma
                .setHelp(createI18NText("Merkitse tälle sivulle koulutukset, joihin haluat hakea." +
                        " Merkitse hakutoiveesi siinä järjestyksessä, kun toivot tulevasi niihin " +
                        "valituksi. Jos olet valinnut korissa koulutuksia, voit siirttää ne hakutoivelistalle. " +
                        "Voit halutessasi etsiä koulutuksia " +
                        "koulutuskorin kautta. harkitse hakutoivejärjestystä tarkoin, " +
                        "sillä se on sitova, etkä voi muuttaa sitä enää hakuajan jälkeen. " +
                        "Jos et pääse koulutukseen, jonka olet merkinnyt ensimmäiselle sijalle, " +
                        "tarkistetaan riittävätkö pisteesi toiselle sijalle " +
                        "merkitsemääsi hakutoiveeseen jne. Jos pääset esimerkiksi toisena " +
                        "toiveena olevaan koulutukseen, alemmat hakutoiveet peruuntuvat " +
                        "automaattisesti, etkä voi enää tulla valituksi niihin. " +
                        "Ylempiin hakutoiveisiin voit vielä päästä. HUOM! Lukion oppimäärän tai " +
                        "ylioppilastutkinnon suorittaneet voivat hakea vain heille varatuille aloituspaikoille (yo)."));
        PreferenceTable preferenceTable = new PreferenceTable("preferencelist", createI18NText("Hakutoiveet"), "Ylös",
                "Alas");
        PreferenceRow pr1 = ElementUtil.createI18NPreferenceRow("preference1", "Hakutoive 1");
        pr1.addAttribute("required", "required");
        PreferenceRow pr2 = ElementUtil.createI18NPreferenceRow("preference2", "Hakutoive 2");
        PreferenceRow pr3 = ElementUtil.createI18NPreferenceRow("preference3", "Hakutoive 3");
        PreferenceRow pr4 = ElementUtil.createI18NPreferenceRow("preference4", "Hakutoive 4");
        PreferenceRow pr5 = ElementUtil.createI18NPreferenceRow("preference5", "Hakutoive 5");
        preferenceTable.addChild(pr1);
        preferenceTable.addChild(pr2);
        preferenceTable.addChild(pr3);
        preferenceTable.addChild(pr4);
        preferenceTable.addChild(pr5);
        preferenceTable.setVerboseHelp(getVerboseHelp());
        hakutoiveetRyhma.addChild(preferenceTable);
    }

    private void createTyokokemus(Theme tyokokemus) {
        tyokokemus
                .setHelp(createI18NText("Työkokemukseksi lasketaan työ, josta sinulla on työtodistus. " +
                        "Työhön rinnastettavaksi toiminnaksi lasketaan varusmiespalvelu, " +
                        "siviilipalvelus, vähintään kolmen kuukauden pituinen työpajatoimintaan osallistuminen tai " +
                        "työharjoitteluun osallistuminen, " +
                        "oppisopimuskoulutus. Oppilaitos tarkistaa työtodistukset ennen lopullista valintaa."));
        TextQuestion tyokokemuskuukaudet = new TextQuestion("tyokokemuskuukaudet",
                createI18NText("Työkokemus kuukausina"));
        tyokokemuskuukaudet
                .setHelp(createI18NText("Merkitse kenttään hakuajan päättymiseen mennessä kertynyt työkokemuksesi. " +
                        "Voit käyttää laskemiseen apuna laskuria."));
        tyokokemuskuukaudet.addAttribute("placeholder", "kuukautta");
        tyokokemuskuukaudet.addAttribute("pattern", "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$");
        tyokokemuskuukaudet.addAttribute("size", "8");
        tyokokemuskuukaudet.setVerboseHelp(getVerboseHelp());
        tyokokemus.addChild(tyokokemuskuukaudet);
    }

    private void createLupatiedot(Theme lupatiedot) {
        TextQuestion email = new TextQuestion("lupa1_email", createI18NText("Sähköpostiosoite"));
        email.addAttribute("size", "40");
        email.addAttribute("required", "required");
        email.setHelp(createI18NText("Kirjoita tähän huoltajan sähköpostiosoite"));
        email.setVerboseHelp(getVerboseHelp());
        email.setInline(true);

        CheckBox permission1 = new CheckBox(
                "lupa1",
                createI18NText("Haluan, että huoltajalleni lähetetään tieto sähköpostilla hakulomakkeen täyttämisestä."));
        CheckBox permission2 = new CheckBox(
                "lupa2",
                createI18NText("Minulle saa lähettää postia vapaista opiskelupaikoista ja muuta koulutusmarkkinointia."));
        CheckBox permission3 = new CheckBox("lupa3",
                createI18NText("Tietoni opiskeluvalinnan tuloksista saa julkaista Internetissä."));
        CheckBox permission4 = new CheckBox("lupa4",
                createI18NText("Valintaani koskevat tiedot saa lähettää minulle sähköisesti."));
        CheckBox permission5 = new CheckBox(
                "lupa5",
                createI18NText("Minulle saa lähettää tietoa opiskelijavalinnan etenemisestä " +
                        "ja tuloksista tekstiviestillä."));
        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("lupa1_rule", permission1.getId(), "on");
        relatedQuestionRule.addChild(email);
        permission1.addChild(relatedQuestionRule);

        Group group = new Group("permissionCheckboxes", createI18NText("Ryhmän otsikkotai ohjeteksti"));

        group.addChild(permission1);
        group.addChild(permission2);
        group.addChild(permission3);
        group.addChild(permission4);
        group.addChild(permission5);
        lupatiedot.addChild(group);
        lupatiedot.setVerboseHelp(getVerboseHelp());

        Radio asiointikieli = new Radio("asiointikieli", createI18NText("Asiointikieli"));
        asiointikieli.setHelp(createI18NText("Valitse kieli, " +
                "jolla haluat vastaanottaa opiskelijavalintaan liittyviä tietoja"));
        asiointikieli.addOption("suomi", createI18NText("Suomi"), "suomi");
        asiointikieli.addOption("ruotsi", createI18NText("Ruotsi"), "ruotsi");
        asiointikieli.addAttribute("required", "required");
        asiointikieli.setVerboseHelp(getVerboseHelp());
        lupatiedot.addChild(asiointikieli);
    }

    private void createKoulutustausta(Theme koulutustaustaRyhma) {
        koulutustaustaRyhma
                .setHelp(createI18NText("Merkitse tälle sivulle tiedot koulutustaustastasi. " +
                        "Valitse pohjakoulutus, jonka perusteella haet. " +
                        "Voit merkitä vain yhden vaihtoehdon. " +
                        "Huom! Jos olet suorittanut lukion oppimäärän, et voi hakea " +
                        "perusopetuksen päättötodistuksella. " +
                        "Jos oppivelvollisuutesi on keskeytynyt tai olet suoritta nut " +
                        "tutkintosi ulkomailla, " +
                        "haet automaattisesti harkintaan perustuvassa valinnassa. Oppilaitokset " +
                        "tarkistavat todistukset hyväksytyiksi tulleilta hakijoilta."));

        Radio osallistunut = new Radio("osallistunut",
                createI18NText("Oletko osallistunut viimeisen vuoden aikana jonkun hakukohteen alan pääsykokeisiin?"));
        osallistunut.addOption("ei", createI18NText("En"), "false");
        osallistunut.addOption("kylla", createI18NText("Kyllä"), "true");
        osallistunut.addAttribute("required", "required");
        osallistunut.setVerboseHelp(getVerboseHelp());

        koulutustaustaRyhma.addChild(createKoulutustaustaRadio());
        koulutustaustaRyhma.addChild(osallistunut);
    }

    public Radio createKoulutustaustaRadio() { //NOSONAR
        Radio millatutkinnolla = new Radio("millatutkinnolla",
                createI18NText("Valitse tutkinto, jolla haet koulutukseen"));
        millatutkinnolla.addOption("tutkinto1", createI18NText("Perusopetuksen oppimäärä"), "tutkinto1",
                createI18NText("Valitse tämä, jos olet käynyt peruskoulun."));
        millatutkinnolla
                .addOption("tutkinto2",
                        createI18NText("Perusopetuksen erityisopetuksen osittain yksilöllistetty oppimäärä"),
                        "tutkinto2",
                        createI18NText("Valitse tämä, jos olet opiskellut yksilöllistetyn " +
                                "oppimäärän puolessa tai alle puolessa oppiaineista."));
        millatutkinnolla
                .addOption(
                        "tutkinto3",
                        createI18NText("Perusopetuksen erityisopetuksen yksilöllistetty oppimäärä, " +
                                "opetus järjestetty toiminta-alueittain"),
                        "tutkinto3",
                        createI18NText("Valitse tämä, jos olet osallistunut harjaantumisopetukseen."));
        millatutkinnolla
                .addOption(
                        "tutkinto4",
                        createI18NText("Perusopetuksen pääosin tai kokonaan yksilöllistetty oppimäärä"),
                        "tutkinto4",
                        createI18NText("Valitse tämä, jos olet opiskellut peruskoulun kokonaan yksilöllistetyn " +
                                "oppimäärän mukaan tai olet opiskellut yli puolet " +
                                "opinnoistasi yksilöllistetyn opetuksen mukaan."));
        millatutkinnolla.addOption("tutkinto5",
                createI18NText("Oppivelvollisuuden suorittaminen keskeytynyt (ei päättötodistusta)"),
                "tutkinto5",
                createI18NText("Valitse tämä vain, jos sinulla ei ole lainkaan päättötodistusta."));
        millatutkinnolla
                .addOption(
                        "tutkinto6",
                        createI18NText("Lukion päättötodistus, ylioppilastutkinto tai abiturientti"),
                        "tutkinto6",
                        createI18NText("Valitse tämä, jos olet suorittanut lukion ja sinulla on suomalainen tai " +
                                "kansainvälinen ylioppilastutkinto, tai olet suorittanut " +
                                "yhdistelmätutkinnon, johon sisältyy lukion vahimmäisoppimäärää vastaavat opinnot."));
        millatutkinnolla.addOption("tutkinto7", createI18NText("Ulkomailla suoritettu koulutus"), "tutkinto7",
                createI18NText("Valitse tämä, jos olet suorittanut tutkintosi ulkomailla."));
        millatutkinnolla.setVerboseHelp(getVerboseHelp());
        millatutkinnolla.addAttribute("required", "required");

        TextQuestion paattotodistusvuosiPeruskoulu = new TextQuestion("paattotodistusvuosi_peruskoulu",
                createI18NText("Minä vuonna sait/saat peruskoulun päättötodistuksen?"));
        paattotodistusvuosiPeruskoulu.addAttribute("placeholder", "vvvv");
        paattotodistusvuosiPeruskoulu.addAttribute("required", "required");
        paattotodistusvuosiPeruskoulu.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        paattotodistusvuosiPeruskoulu.addAttribute("size", "4");
        paattotodistusvuosiPeruskoulu.addAttribute("maxlength", "4");

        CheckBox suorittanut1 = new CheckBox("suorittanut1",
                createI18NText("Kymppiluokka (perusopetuksen lisäopetuksen oppimäärä, vähintään 1100 tuntia)"));
        CheckBox suorittanut2 = new CheckBox("suorittanut2",
                createI18NText("Vammaisten valmentava ja kuntouttava opetus ja ohjaus"));
        CheckBox suorittanut3 = new CheckBox("suorittanut3",
                createI18NText("Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus"));
        CheckBox suorittanut4 = new CheckBox(
                "suorittanut4",
                createI18NText("Talouskoulu (muuna kuin ammatillisena peruskoulutuksena " +
                        "järjestettävä kotitalousopetus)"));
        CheckBox suorittanut5 = new CheckBox(
                "suorittanut5",
                createI18NText("Ammattistartti (ammatilliseen peruskoulutukseen ohjaava ja " +
                        "valmistava koulutus, vähintään 20 opintoviikkoa)"));
        CheckBox suorittanut6 = new CheckBox("suorittanut6",
                createI18NText("Kansanopiston lukuvuoden mittainen linja ammatilliseen peruskoulutukseen"));

        Group suorittanutGroup = new Group("suorittanutgroup",
                createI18NText("Merkitse tähän, jos olet suorittanut jonkun seuraavista"));
        suorittanutGroup.addChild(suorittanut1);
        suorittanutGroup.addChild(suorittanut2);
        suorittanutGroup.addChild(suorittanut3);
        suorittanutGroup.addChild(suorittanut4);
        suorittanutGroup.addChild(suorittanut5);
        suorittanutGroup.addChild(suorittanut6);

        /*
         * DropdownSelect tutkinnonOpetuskieli = new
         * DropdownSelect("opetuskieli",
         * createI18NText("Mikä oli tukintosi opetuskieli"));
         * tutkinnonOpetuskieli.addOption("suomi", createI18NText("Suomi"),
         * "Suomi"); tutkinnonOpetuskieli.addOption("ruotsi",
         * createI18NText("Ruotsi"), "Ruotsi");
         * tutkinnonOpetuskieli.addAttribute("placeholder",
         * "Tutkintosi opetuskieli"); tutkinnonOpetuskieli.setHelp(
         * "Merkitse tähän se kieli, jolla suoritit suurimman osan opinnoistasi.
         * Jos suoritit opinnot kahdella kielellä tasapuolisesti, valitse toinen niistä"
         * ); tutkinnonOpetuskieli.setVerboseHelp(getVerboseHelp());
         */

        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule3", millatutkinnolla.getId(), "(" // NOSONAR
                + millatutkinnolla.getOptions().get(0).getValue() + "|"   // NOSONAR
                + millatutkinnolla.getOptions().get(1).getValue() + "|"   // NOSONAR
                + millatutkinnolla.getOptions().get(2).getValue() + "|"   // NOSONAR
                + millatutkinnolla.getOptions().get(3).getValue() + ")"); // NOSONAR

        RelatedQuestionRule paattotodistusvuosiPeruskouluRule = new RelatedQuestionRule("rule8",
                paattotodistusvuosiPeruskoulu.getId(), "^(19[0-9][0-9]|200[0-9]|201[0-1])$");

        relatedQuestionRule.addChild(paattotodistusvuosiPeruskoulu);
        // relatedQuestionRule.addChild(tutkinnonOpetuskieli);
        relatedQuestionRule.addChild(suorittanutGroup);
        relatedQuestionRule.addChild(paattotodistusvuosiPeruskouluRule);

        TextQuestion lukioPaattotodistusVuosi = new TextQuestion("lukioPaattotodistusVuosi",
                createI18NText("Päättötodistuksen vuosi"));
        lukioPaattotodistusVuosi.addAttribute("placeholder", "vvvv");
        lukioPaattotodistusVuosi.addAttribute("required", "required");
        lukioPaattotodistusVuosi.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        lukioPaattotodistusVuosi.addAttribute("size", "4");
        lukioPaattotodistusVuosi.addAttribute("maxlength", "4");
        lukioPaattotodistusVuosi.setInline(true);

        TextQuestion ylioppilastodistuksenVuosi = new TextQuestion("ylioppilastodistuksenVuosi",
                createI18NText("Ylioppilastodistuksen vuosi"));
        ylioppilastodistuksenVuosi.addAttribute("placeholder", "vvvv");
        ylioppilastodistuksenVuosi.addAttribute("required", "required");
        ylioppilastodistuksenVuosi.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        ylioppilastodistuksenVuosi.addAttribute("size", "4");
        ylioppilastodistuksenVuosi.addAttribute("maxlength", "4");
        ylioppilastodistuksenVuosi.setInline(true);

        DropdownSelect ylioppilastutkinto = new DropdownSelect("ylioppilastutkinto",
                createI18NText("Ylioppilastutkinto"));
        ylioppilastutkinto.addOption("fi", createI18NText("Suomalainen ylioppilastutkinto"), "fi");
        ylioppilastutkinto.addOption("ib", createI18NText("International Baccalaureate (IB) -tutkinto"), "ib");
        ylioppilastutkinto.addOption("eb", createI18NText("European Baccalaureate (EB) -tutkinto"), "eb");
        ylioppilastutkinto.addOption("rp", createI18NText("Reifeprũfung (RP) -tutkinto"), "rp");
        ylioppilastutkinto.addAttribute("required", "required");
        ylioppilastutkinto.setInline(true);

        Group lukioGroup = new Group("lukioGroup", createI18NText("Täytä lukion suorittamiseen liittyvät tiedot"));
        lukioGroup.addChild(lukioPaattotodistusVuosi);
        lukioGroup.addChild(ylioppilastodistuksenVuosi);
        lukioGroup.addChild(ylioppilastutkinto);

        RelatedQuestionRule lukioRule = new RelatedQuestionRule("rule7", millatutkinnolla.getId(), millatutkinnolla
                .getOptions().get(5).getValue()); // NOSONAR
        lukioRule.addChild(lukioGroup);

        millatutkinnolla.addChild(lukioRule);
        millatutkinnolla.addChild(relatedQuestionRule);

        Radio suorittanutAmmatillisenTutkinnon = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NText("Oletko suorittanut jonkun ammatillisen perustutkinnon, " +
                        "muun ammatillisen tutkinnon tai korkeakoulututkinnon?"));
        suorittanutAmmatillisenTutkinnon.addOption("kylla", createI18NText("Kyllä"), "true");
        suorittanutAmmatillisenTutkinnon.addOption("ei", createI18NText("En"), "false");
        suorittanutAmmatillisenTutkinnon.addAttribute("required", "required");

        Radio koulutuspaikkaAmmatillisenTutkintoon = new Radio(
                "koulutuspaikkaAmmatillisenTutkintoon",
                createI18NText("Onko sinulla koulutuspaikka ammatilliseen perustutkintoon " +
                        "johtavassa oppilaitoksessa tai lukiossa?"));
        koulutuspaikkaAmmatillisenTutkintoon.addOption("kylla", createI18NText("Kyllä"), "true");
        koulutuspaikkaAmmatillisenTutkintoon.addOption("ei", createI18NText("Ei"), "false");
        koulutuspaikkaAmmatillisenTutkintoon.addAttribute("required", "required");

        lukioRule.addChild(suorittanutAmmatillisenTutkinnon);
        lukioRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);
        paattotodistusvuosiPeruskouluRule.addChild(suorittanutAmmatillisenTutkinnon);
        paattotodistusvuosiPeruskouluRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);

        RelatedQuestionRule suorittanutAmmatillisenTutkinnonRule = new RelatedQuestionRule("rule9",
                suorittanutAmmatillisenTutkinnon.getId(), "^true");
        Notification notification1 = new Notification(
                "notification1",
                createI18NText("Yhteishaun ammatillisen koulutuksen koulutuspaikat on varattu hakijoille, " +
                        "jotka ovat ilman koulutuspaikkaa. Huomioi, " +
                        "että et voi hakea ammatillisen koulutuksen tällä hakulomakkeella, " +
                        "koska olet jo suorittanut ammatilliseen perustutkintoon " +
                        "johtavan koulutuksen tai lukiokoulutuksen."),
                Notification.NotificationType.INFO);
        suorittanutAmmatillisenTutkinnonRule.addChild(notification1);
        suorittanutAmmatillisenTutkinnon.addChild(suorittanutAmmatillisenTutkinnonRule);

        RelatedQuestionRule koulutuspaikkaAmmatillisenTutkintoonRule = new RelatedQuestionRule("rule10",
                koulutuspaikkaAmmatillisenTutkintoon.getId(), "^true$");
        Notification notification2 = new Notification(
                "notification2",
                createI18NText("Yhteishaun ammatillisen koulutuksen koulutuspaikat on varattu hakijoille, " +
                        "jotka ovat ilman koulutuspaikkaa. Huomioi, " +
                        "että et voi hakea ammatillisen koulutuksen tällä hakulomakkeella, koska olet jo " +
                        "suorittamassa ammatilliseen perustutkintoon " +
                        "johtavaa koulutusta tai lukiokoulutusta."),
                Notification.NotificationType.INFO);
        koulutuspaikkaAmmatillisenTutkintoonRule.addChild(notification2);
        koulutuspaikkaAmmatillisenTutkintoon.addChild(koulutuspaikkaAmmatillisenTutkintoonRule);

        return millatutkinnolla;
    }

    private String getVerboseHelp() {
        return " Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Curabitur nec dolor quam. Duis sodales placerat scelerisque. Suspendisse " +
                "porta mauris eu felis malesuada rutrum. Aliquam varius fringilla mi sed " +
                "luctus. Nam in enim ipsum. Sed lobortis lorem sit amet justo " +
                "blandit et tempus ante eleifend. Proin egestas, magna et condimentum egestas, " +
                "arcu mauris tincidunt augue, eget varius diam massa nec " +
                "nisi. Proin dolor risus, tincidunt non faucibus imperdiet, fringilla quis massa. " +
                "Curabitur pharetra posuere est, sit amet pulvinar urna " +
                "facilisis at. Praesent posuere feugiat elit vel porttitor. Integer venenatis, " +
                "arcu ac suscipit ornare, augue nibh tempus libero, eget " +
                "molestie turpis massa quis purus. Suspendisse id libero dolor. Ut eget velit augue, " +
                "eget fringilla erat. Quisque sed neque non arcu " +
                "elementum vehicula eget at est. Etiam dictum fringilla mi, sit amet sodales tortor facilisis in.\n"
                + "\n"
                + "Nunc nisl felis, placerat non pellentesque non, dapibus non sem. " +
                "Nunc et consectetur tellus. Class aptent taciti sociosqu ad litora " +
                "torquent per conubia nostra, per inceptos himenaeos. Nulla facilisi. " +
                "Nulla facilisi. Etiam lobortis, justo non eleifend rhoncus, eros " +
                "felis vestibulum metus, ut ullamcorper neque urna et velit. " +
                "Duis congue tincidunt urna non consectetur. Phasellus quis ligula et libero " +
                "convallis eleifend non quis velit. Morbi luctus, ligula sed mollis placerat, " +
                "nunc justo tempor velit, eget dignissim ante ipsum eu elit. " +
                "Sed interdum urna in justo eleifend id fringilla mi facilisis. " +
                "Ut id sapien erat. Aenean urna quam, aliquet nec imperdiet quis, suscipit " +
                "eu nunc. Vestibulum vitae dolor in sapien auctor hendrerit et et turpis. " +
                "Ut at diam eu sapien blandit blandit at in lorem.\n"
                + "\n"
                + "Aenean ornare, mi non rutrum gravida, augue neque pretium leo, " +
                "in porta justo mauris eget orci. Donec porttitor eleifend aliquam. " +
                "Cras mattis tincidunt purus, et facilisis risus consequat vitae. " +
                "Nunc consectetur, odio sit amet rhoncus iaculis, ipsum lectus pharetra " +
                "lectus, sit amet vestibulum est mi commodo enim. Sed libero sem, " +
                "iaculis a lobortis non, molestie id arcu. Donec gravida tincidunt ligula " +
                "quis mattis. Nulla sit amet malesuada sem. " +
                "Duis porta adipiscing purus iaculis consequat. Aliquam erat volutpat. ";
    }

    private Question createRequiredTextQuestion(final String id, final String name, final String size) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NText(name));
        textQuestion.addAttribute("required", "required");
        textQuestion.addAttribute("size", size);
        return textQuestion;
    }

    public ApplicationPeriod getApplicationPeriod() {
        return applicationPeriod;
    }

    private Map<String, PostOffice> createPostOffices() {
        List<PostOffice> listOfPostOffices = koodistoService.getPostOffices();
        Map<String, PostOffice> postOfficeMap = new HashMap<String, PostOffice>(listOfPostOffices.size());
        for (PostOffice postOffice : listOfPostOffices) {
            postOfficeMap.put(postOffice.getPostcode(), postOffice);
        }
        return ImmutableMap.copyOf(postOfficeMap);
    }

    private void setDefaultOption(final String value, final List<Option> options) {
        for (Option opt : options) {
            if (opt.getValue().equalsIgnoreCase(value)) {
                opt.setDefaultOption(true);
            }
        }
    }
}
