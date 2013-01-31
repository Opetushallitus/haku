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

package fi.vm.sade.oppija.lomake.dao.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.lomake.dao.FormModelDAO;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.PostOffice;
import fi.vm.sade.oppija.lomake.domain.elements.*;
import fi.vm.sade.oppija.lomake.domain.elements.custom.*;
import fi.vm.sade.oppija.lomake.domain.elements.questions.*;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.lomake.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.*;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;

@Service("FormModelDummyMemoryDao")
public class FormModelDummyMemoryDaoImpl implements FormModelDAO, FormService {

    final ApplicationPeriod applicationPeriod;
    private FormModel formModel;

    public FormModelDummyMemoryDaoImpl() {
        this("yhteishaku", "henkilotiedot");
    }

    public FormModelDummyMemoryDaoImpl(final String formId, final String firstCategoryId) {
        this.applicationPeriod = new ApplicationPeriod("Yhteishaku");
        formModel = new FormModel();
        formModel.addApplicationPeriod(applicationPeriod);
        Phase henkilötiedot = new Phase(firstCategoryId, createI18NText("Henkilötiedot"), false);
        Phase koulutustausta = new Phase("koulutustausta", createI18NText("Koulutustausta"), false);
        Phase hakutoiveet = new Phase("hakutoiveet", createI18NText("Hakutoiveet"), false);
        Phase arvosanat = new Phase("arvosanat", createI18NText("Arvosanat"), false);
        Phase lisätiedot = new Phase("lisatiedot", createI18NText("Lisätiedot"), false);
        Phase esikatselu = new Phase("esikatselu", createI18NText("Esikatselu"), true);

        Form form = new Form(formId, createI18NText("Ammatillisen koulutuksen ja lukiokoulutuksen yhteishaku, syksy 2013"));
        form.addChild(henkilötiedot);
        form.addChild(koulutustausta);
        form.addChild(hakutoiveet);
        form.addChild(arvosanat);
        form.addChild(lisätiedot);
        form.addChild(esikatselu);
        form.init();

        applicationPeriod.addForm(form);

        Map<String, List<Question>> lisakysymysMap = new HashMap<String, List<Question>>();
        Map<String, List<Question>> oppiaineMap = new HashMap<String, List<Question>>();

        List<Question> oppiaineList = new ArrayList<Question>();
        oppiaineList.add(new SubjectRow("tietotekniikka", createI18NText("Tietotekniikka")));
        oppiaineList.add(new SubjectRow("kansantaloustiede", createI18NText("Kansantaloustiede")));
        oppiaineMap.put("776", oppiaineList);


        final String id = "776";
        Radio radio = new Radio(id + "_additional_question_1",
                createI18NText("Tällä alalla on terveydentilavaatimuksia, jotka voivat olla opiskelijan ottamisen esteenä. Onko sinulla terveydellisiä tekijöitä, jotka voivat olla opiskelijatksi ottamisen esteenä?"));
        radio.addOption(id + "_q1_option_1", createI18NText("Ei"), "q1_option_1");
        radio.addOption(id + "_q1_option_2", createI18NText("Kyllä. Ymmärrä, etten tästä johtuen ehkä tule valituksi"), "q1_option_2");

        Radio radio2 = new Radio(id + "_additional_question_2", createI18NText("Tässä koulutuksessa opiskelijaksi ottamisen esteenä voi olla eiempi päätös opiskeluoikeuden peruuttamisessa. Onko opiskeluoikeutesi aiemmin peruutettu terveydentilasi tai muiden henkilöiden turvallisuuden vaarantamisen takia?"));
        radio2.addOption(id + "_q2_option_1", createI18NText("Ei"), "q2_option_1");
        radio2.addOption(id + "_q2_option_2", createI18NText("Kyllä. Ymmärrä, etten tästä johtuen ehkä tule valituksi"), "q2_option_2");

        Radio radio3 = new Radio(id + "_additional_question_3", createI18NText("Jos olet osallistunut saman alan pääsykokeeseen, niin haluatko käyttää hyväksyttyjä koetuloksiasi?"));
        radio3.addOption(id + "_q3_option_1", createI18NText("En, en ole osallistunut pääsykokeeseen"), "q3_option_1");
        radio3.addOption(id + "_q3_option_2", createI18NText("Ei, en halua käyttää tuloksia"), "q3_option_2");
        radio3.addOption(id + "_q3_option_3", createI18NText("Kyllä, haluan käyttää pääsykoetuloksia"), "q3_option_3");

        List<Question> lisakysymysList = new ArrayList<Question>();
        lisakysymysList.add(radio);
        lisakysymysList.add(radio2);
        lisakysymysList.add(radio3);
        lisakysymysMap.put(id, lisakysymysList);


        Theme henkilötiedotRyhmä = new Theme("HenkilotiedotGrp", createI18NText("Henkilötiedot"), null);
        Theme koulutustaustaRyhmä = new Theme("KoulutustaustaGrp", createI18NText("Koulutustausta"), null);
        Theme hakutoiveetRyhmä = new Theme("hakutoiveetGrp", createI18NText("Hakutoiveet"), lisakysymysMap);
        Theme arvosanatRyhmä = new Theme("arvosanatGrp", createI18NText("Arvosanat"), oppiaineMap);
        Theme tyokokemusRyhmä = new Theme("tyokokemusGrp", createI18NText("Työkokemus"), null);
        Theme lupatiedotRyhmä = new Theme("lupatiedotGrp", createI18NText("Lupatiedot"), null);
        Theme yhteenvetoRyhmä = new Theme("yhteenvetoGrp", createI18NText("yhteenveto"), null);

        henkilötiedot.addChild(henkilötiedotRyhmä);
        koulutustausta.addChild(koulutustaustaRyhmä);
        hakutoiveet.addChild(hakutoiveetRyhmä);
        arvosanat.addChild(arvosanatRyhmä);
        lisätiedot.addChild(tyokokemusRyhmä);
        lisätiedot.addChild(lupatiedotRyhmä);

        DropdownSelect aidinkieli = new DropdownSelect("äidinkieli", createI18NText("Äidinkieli"));
        aidinkieli.addOption("suomi", createI18NText("Suomi"), "Suomi");
        aidinkieli.addOption("ruotsi", createI18NText("Ruotsi"), "Ruotsi");
        aidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        aidinkieli.addAttribute("required", "required");
        aidinkieli.setVerboseHelp(getVerboseHelp());
        aidinkieli.setInline(true);


        DropdownSelect kansalaisuus = new DropdownSelect("kansalaisuus", createI18NText("Kansalaisuus"));
        kansalaisuus.addOption("fi", createI18NText("Suomi"), "fi");
        kansalaisuus.addOption("sv", createI18NText("Ruotsi"), "sv");
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.addAttribute("required", "required");
        kansalaisuus.setHelp("Jos sinulla on kaksoiskansalaisuus, valitse toinen niistä");
        kansalaisuus.setVerboseHelp(getVerboseHelp());
        kansalaisuus.setInline(true);

        DropdownSelect kotikunta = new DropdownSelect("kotikunta", createI18NText("Kotikunta"));
        kotikunta.addOption("jalasjarvi, ", createI18NText("Jalasjärvi"), "Jalasjärvi");
        kotikunta.addOption("janakkala", createI18NText("Janakkala"), "Janakkala");
        kotikunta.addOption("joensuu", createI18NText("Joensuu"), "Joensuu");
        kotikunta.addOption("jokioinen", createI18NText("Jokioinen"), "Jokioinen");
        kotikunta.addOption("jomala", createI18NText("Jomala"), "Jomala");
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        kotikunta.addAttribute("required", "required");
        kotikunta.setVerboseHelp(getVerboseHelp());
        kotikunta.setInline(true);

        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", createI18NText("Kutsumanimi"));
        kutsumanimi.setHelp("Valitse kutsumanimeksi jokin virallisista etunimistäsi");
        kutsumanimi.addAttribute("required", "required");
        kutsumanimi.addAttribute("size", "20");
        kutsumanimi.addAttribute("containedInOther", "Etunimet");
        kutsumanimi.setVerboseHelp(getVerboseHelp());
        kutsumanimi.setInline(true);

        TextQuestion email = new TextQuestion("Sähköposti", createI18NText("Sähköpostiosoite"));
        email.addAttribute("size", "50");
        email.addAttribute("pattern", "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");
        email.setHelp("Kirjoita tähän sähköpostiosoite, johon haluat vastaanottaa opiskelijavalintaan liittyviä tietoja ja jota käytät säännöllisesti. Saat vahvistuksen hakemuksen perille menosta tähän sähköpostiosoitteeseen.");
        email.setVerboseHelp(getVerboseHelp());
        email.setInline(true);

        TextQuestion henkilötunnus = new TextQuestion("Henkilotunnus", createI18NText("Henkilötunnus"));
        henkilötunnus.addAttribute("placeholder", "ppkkvv***** or dd.mm.yyyy");
        henkilötunnus.addAttribute("required", "required");
        henkilötunnus.addAttribute("pattern", "([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))|[0-9]{2}[.][0-9]{2}[.][0-9]{4}");
        henkilötunnus.addAttribute("size", "11");
        henkilötunnus.addAttribute("maxlength", "11");
        henkilötunnus.setHelp("Jos sinulla ei ole suomalaista henkilötunnusta, täytä tähän syntymäaikasi");
        henkilötunnus.setVerboseHelp(getVerboseHelp());
        henkilötunnus.setInline(true);

        Radio sukupuoli = new Radio("Sukupuoli", createI18NText("Sukupuoli"));
        sukupuoli.addOption("mies", createI18NText("Mies"), "Mies");
        sukupuoli.addOption("nainen", createI18NText("Nainen"), "Nainen");
        sukupuoli.addAttribute("required", "required");
        sukupuoli.setVerboseHelp(getVerboseHelp());
        sukupuoli.setInline(true);

        SocialSecurityNumber socialSecurityNumber = new SocialSecurityNumber("ssn_question", createI18NText("Henkilötunnus"));
        socialSecurityNumber.setSsn(henkilötunnus);
        socialSecurityNumber.setSex(sukupuoli);
        socialSecurityNumber.setMaleId(sukupuoli.getOptions().get(0).getId());
        socialSecurityNumber.setFemaleId(sukupuoli.getOptions().get(1).getId());
        socialSecurityNumber.setNationalityId(kansalaisuus.getId());

//        SelectingSubmitRule autofillhetu = new SelectingSubmitRule(henkilötunnus.getId(), sukupuoli.getId());
//        autofillhetu.addBinding(henkilötunnus, sukupuoli, "\\d{6}\\S\\d{2}[13579]\\w", sukupuoli.getOptions().get(0));
//        autofillhetu.addBinding(henkilötunnus, sukupuoli, "\\d{6}\\S\\d{2}[24680]\\w", sukupuoli.getOptions().get(1));

        Element postinumero = new PostalCode("Postinumero", createI18NText("Postinumero"), getPostOffices());
        postinumero.addAttribute("size", "5");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("placeholder", "#####");
        postinumero.addAttribute("maxlength", "5");
        postinumero.setHelp("Kirjoita tähän osoite, johon haluat vastaanottaan opiskelijavalintaan liittyvää postia, kuten kutsun valintakokeeseen tai valintapäätöksen.");

        DropdownSelect asuinmaa = new DropdownSelect("asuinmaa", createI18NText("Asuinmaa"));
        asuinmaa.addOption("valitse", null, "");
        asuinmaa.addOption("fi", createI18NText("Suomi"), "fi");
        asuinmaa.addOption("sv", createI18NText("Ruotsi"), "sv");
        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        asuinmaa.addAttribute("required", "required");
        asuinmaa.setVerboseHelp(getVerboseHelp());
        asuinmaa.setInline(true);

        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "Lähiosoite", "40");
        lahiosoite.setInline(true);

        CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1", createI18NText("Tämä on ensisijainen osoitteeni"));
        ensisijainenOsoite.setInline(true);

        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule1", asuinmaa.getId(), "fi");
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
        RelatedQuestionRule relatedQuestionRule2 = new RelatedQuestionRule("rule2", asuinmaa.getId(), "sv");
        relatedQuestionRule2.addChild(osoite);
        osoite.setVerboseHelp(getVerboseHelp());
        asuinmaa.addChild(relatedQuestionRule2);
        osoite.setInline(true);

        TextQuestion matkapuhelinnumero = new TextQuestion("matkapuhelinnumero", createI18NText("Matkapuhelinnumero"));
        matkapuhelinnumero.setHelp("Kirjoita tähän matkapuhelinnumerosi, jotta sinuun saadaan tarvittaessa yhteyden.");
        matkapuhelinnumero.addAttribute("size", "20");
        matkapuhelinnumero.setVerboseHelp(getVerboseHelp());
        matkapuhelinnumero.setInline(true);

        TextQuestion huoltajanPuhelinnumero = new TextQuestion("huoltajanPuhelinnumero", createI18NText("Huoltajan puhelinnumero"));
        huoltajanPuhelinnumero.setHelp("Kirjoita tähän huoltajan puhelinnumero.");
        huoltajanPuhelinnumero.addAttribute("size", "20");
        huoltajanPuhelinnumero.setVerboseHelp(getVerboseHelp());
        huoltajanPuhelinnumero.setInline(true);

        AddElementRule addHuoltajanPuhelinnumero = new AddElementRule("addHuoltajanPuhelinnumeroRule",
                huoltajanPuhelinnumero.getId(), "Lisää huoltajan puhelinnumero");
        addHuoltajanPuhelinnumero.addChild(huoltajanPuhelinnumero);

        kotikunta.setHelp("Kotikunta on tyypillisesti se kunta, jossa asut.");
        aidinkieli.setHelp("Jos omaa äidinkieltäsi ei löydy valintalistasta, valitse äidinkieleksesi.");

        Question sukunimi = createRequiredTextQuestion("Sukunimi", "Sukunimi", "30");
        sukunimi.setInline(true);
        Question etunimet = createRequiredTextQuestion("Etunimet", "Etunimet", "30");
        etunimet.setInline(true);

        henkilötiedotRyhmä.addChild(sukunimi)
                .addChild(etunimet)
                .addChild(kutsumanimi)
                .addChild(kansalaisuus)
                .addChild(socialSecurityNumber)
                .addChild(email)
                .addChild(matkapuhelinnumero)
                .addChild(addHuoltajanPuhelinnumero)
                .addChild(asuinmaa)
                .addChild(aidinkieli);

        createKoulutustausta(koulutustaustaRyhmä);
        createHakutoiveet(hakutoiveetRyhmä);
        createArvosanat(arvosanatRyhmä);
        createTyokokemus(tyokokemusRyhmä);
        createLupatiedot(lupatiedotRyhmä);

        esikatselu.addChild(henkilötiedotRyhmä).
                addChild(koulutustaustaRyhmä).addChild(hakutoiveetRyhmä).addChild(arvosanatRyhmä).addChild(tyokokemusRyhmä).addChild(lupatiedotRyhmä);

        yhteenvetoRyhmä.setHelp("Kiitos, hakemuksesi on vastaanotettu");

    }

    public GradeGrid createGradeGrid() {

        List<Option> gradeRange = new ArrayList<Option>();
        gradeRange.add(new Option("grade_nograde", createI18NText("Ei arvosanaa"), "-1"));
        gradeRange.add(new Option("grade_10", createI18NText("10"), "10"));
        gradeRange.add(new Option("grade_9", createI18NText("9"), "9"));
        gradeRange.add(new Option("grade_8", createI18NText("8"), "8"));
        gradeRange.add(new Option("grade_7", createI18NText("7"), "7"));
        gradeRange.add(new Option("grade_6", createI18NText("6"), "6"));
        gradeRange.add(new Option("grade_5", createI18NText("5"), "5"));
        gradeRange.add(new Option("grade_4", createI18NText("4"), "4"));

        SubjectRow finnish = new SubjectRow("subject_finnish", createI18NText("Äidinkieli ja kirjallisuus"));
        List<SubjectRow> subjectRowsBefore = new ArrayList<SubjectRow>();
        subjectRowsBefore.add(finnish);

        LanguageRow a1 = new LanguageRow("lang_a1", createI18NText("A1-kieli"));
        LanguageRow b1 = new LanguageRow("lang_b1", createI18NText("B1-kieli"));

        List<LanguageRow> languageRows = new ArrayList<LanguageRow>();
        languageRows.add(a1);
        languageRows.add(b1);

        SubjectRow matematiikka = new SubjectRow("subject_matematiikka", createI18NText("Matematiikka"));
        SubjectRow biologia = new SubjectRow("subject_biologia", createI18NText("Biologia"));
        SubjectRow maantieto = new SubjectRow("subject_maantieto", createI18NText("Maantieto"));
        SubjectRow fysiikka = new SubjectRow("subject_fysiikka", createI18NText("Fysiikka"));
        SubjectRow kemia = new SubjectRow("subject_kemia", createI18NText("Kemia"));
        SubjectRow terveystieto = new SubjectRow("subject_terveystieto", createI18NText("Terveystieto"));
        SubjectRow uskonto = new SubjectRow("subject_uskonto", createI18NText("Uskonto tai elämänkatsomustieto"));
        SubjectRow historia = new SubjectRow("subject_historia", createI18NText("Historia"));
        SubjectRow yhteiskuntaoppi = new SubjectRow("subject_yhteiskuntaoppi", createI18NText("Yhteiskuntaoppi"));
        SubjectRow musiikki = new SubjectRow("subject_musiikki", createI18NText("Musiikki"));
        SubjectRow kuvataide = new SubjectRow("subject_kuvataide", createI18NText("Kuvataide"));
        SubjectRow kasityo = new SubjectRow("subject_kasityo", createI18NText("Käsityö"));
        SubjectRow liikunta = new SubjectRow("subject_liikunta", createI18NText("Liikunta"));
        SubjectRow kotitalous = new SubjectRow("subject_kotitalous", createI18NText("Kotitalous"));
        List<SubjectRow> subjectRowsAfter = new ArrayList<SubjectRow>();
        subjectRowsAfter.add(matematiikka);
        subjectRowsAfter.add(biologia);
        subjectRowsAfter.add(maantieto);
        subjectRowsAfter.add(fysiikka);
        subjectRowsAfter.add(kemia);
        subjectRowsAfter.add(terveystieto);
        subjectRowsAfter.add(uskonto);
        subjectRowsAfter.add(historia);
        subjectRowsAfter.add(yhteiskuntaoppi);
        subjectRowsAfter.add(musiikki);
        subjectRowsAfter.add(kuvataide);
        subjectRowsAfter.add(kasityo);
        subjectRowsAfter.add(liikunta);
        subjectRowsAfter.add(kotitalous);

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

        GradeGrid gradeGrid = new GradeGrid("gradegrid", createI18NText("Arvosanat"),
                "Kieli", subjectRowsBefore, languageRows,
                subjectRowsAfter, scopeOptions, languageOptions, gradeRange);
        gradeGrid.setVerboseHelp(getVerboseHelp());

        return gradeGrid;
    }

    private void createArvosanat(Theme arvosanatRyhmä) {
        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule4", "millatutkinnolla", "(tutkinto1|tutkinto2|tutkinto3|tutkinto4|tutkinto6)");
        relatedQuestionRule.addChild(createGradeGrid());
        arvosanatRyhmä.addChild(relatedQuestionRule);
        RelatedQuestionRule relatedQuestionRule2 = new RelatedQuestionRule("rule5", "millatutkinnolla", "(tutkinto5|tutkinto7)");
        relatedQuestionRule2.addChild(new Text("nogradegrid", createI18NText("Sinulta ei kysytä arvosanoja.")));
        arvosanatRyhmä.addChild(relatedQuestionRule2);
        arvosanatRyhmä.setHelp("Merkitse arvosanat siitä todistuksesta, jolla haet koulutukseen (perusopetus,tai sitä vastaavat opinnot, lukiokoulutus). Korotetut arvosanat voit merkitä, mikäli olet saanut korotuksista virallisen todistuksen. Huomio. Jos olet suorittanut lukion oppimäärän tai ylioppilastutkinnon, et voi hakea perusopetuksen päättötodistuksella. Ammatillisella perustutkinnolla et voi hakea. Oppilaitokset tarkistavat todistukset hyväksytyiksi tulleilta hakijoilta. 1. Tarkista ja täydennä taulukkoon todistuksen oppiaineet ja arvosanat, jotka poikkeavat esitäytetyistä. Huom! Valinnaisaineiden arvosanat merkitään vain mikäli niiden laajuus on vähintään kaksi vuosiviikkotuntia perusopetuksen vuosiluokkien 7-9 aikana. Jos sinulla on yksilöllistettyjä arvosanoja, valitse listasta arvosana, jossa on tähti.");

    }

    private void createHakutoiveet(Theme hakutoiveetRyhmä) {
        hakutoiveetRyhmä.setHelp("Merkitse tälle sivulle koulutukset, joihin haluat hakea. Merkitse hakutoiveesi siinä järjestyksessä, kun toivot tulevasi niihin valituksi. Jos olet valinnut korissa koulutuksia, voit siirttää ne hakutoivelistalle. Voit halutessasi etsiä koulutuksia koulutuskorin kautta. harkitse hakutoivejärjestystä tarkoin, sillä se on sitova, etkä voi muuttaa sitä enää hakuajan jälkeen. Jos et pääse koulutukseen, jonka olet merkinnyt ensimmäiselle sijalle, tarkistetaan riittävätkö pisteesi toiselle sijalle merkitsemääsi hakutoiveeseen jne. Jos pääset esimerkiksi toisena toiveena olevaan koulutukseen, alemmat hakutoiveet peruuntuvat automaattisesti, etkä voi enää tulla valituksi niihin. Ylempiin hakutoiveisiin voit vielä päästä. HUOM! Lukion oppimäärän tai ylioppilastutkinnon suorittaneet voivat hakea vain heille varatuille aloituspaikoille (yo).");
        PreferenceTable preferenceTable = new PreferenceTable("preferencelist", createI18NText("Hakutoiveet"), "Ylös", "Alas");
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
        hakutoiveetRyhmä.addChild(preferenceTable);
    }

    private void createTyokokemus(Theme tyokokemus) {
        tyokokemus.setHelp("Työkokemukseksi lasketaan työ, josta sinulla on työtodistus. Työhön rinnastettavaksi toiminnaksi lasketaan varusmiespalvelu, siviilipalvelus, vähintään kolmen kuukauden pituinen työpajatoimintaan osallistuminen tai työharjoitteluun osallistuminen, oppisopimuskoulutus. Oppilaitos tarkistaa työtodistukset ennen lopullista valintaa.");
        TextQuestion tyokokemuskuukaudet = new TextQuestion("tyokokemuskuukaudet", createI18NText("Työkokemus kuukausina"));
        tyokokemuskuukaudet.setHelp("Merkitse kenttään hakuajan päättymiseen mennessä kertynyt työkokemuksesi. Voit käyttää laskemiseen apuna laskuria.");
        tyokokemuskuukaudet.addAttribute("placeholder", "kuukautta");
        tyokokemuskuukaudet.addAttribute("pattern", "[0-9]*");
        tyokokemuskuukaudet.addAttribute("size", "8");
        tyokokemuskuukaudet.setVerboseHelp(getVerboseHelp());
        tyokokemus.addChild(tyokokemuskuukaudet);
    }

    private void createLupatiedot(Theme lupatiedot) {
        TextQuestion email = new TextQuestion("lupa1_email", createI18NText("Sähköpostiosoite"));
        email.addAttribute("size", "40");
        email.addAttribute("required", "required");
        email.setHelp("Kirjoita tähän huoltajan sähköpostiosoite");
        email.setVerboseHelp(getVerboseHelp());
        email.setInline(true);

        CheckBox permission1 = new CheckBox("lupa1", createI18NText("Haluan, että huoltajalleni lähetetään tieto sähköpostilla hakulomakkeen täyttämisestä."));
        CheckBox permission2 = new CheckBox("lupa2", createI18NText("Minulle saa lähettää postia vapaista opiskelupaikoista ja muuta koulutusmarkkinointia."));
        CheckBox permission3 = new CheckBox("lupa3", createI18NText("Tietoni opiskeluvalinnan tuloksista saa julkaista Internetissä."));
        CheckBox permission4 = new CheckBox("lupa4", createI18NText("Valintaani koskevat tiedot saa lähettää minulle sähköisesti."));
        CheckBox permission5 = new CheckBox("lupa5", createI18NText("Minulle saa lähettää tietoa opiskelijavalinnan etenemisestä ja tuloksista tekstiviestillä."));
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
        asiointikieli.setHelp("Valitse kieli, jolla haluat vastaanottaa opiskelijavalintaan liittyviä tietoja");
        asiointikieli.addOption("suomi", createI18NText("Suomi"), "suomi");
        asiointikieli.addOption("ruotsi", createI18NText("Ruotsi"), "ruotsi");
        asiointikieli.addAttribute("required", "required");
        asiointikieli.setVerboseHelp(getVerboseHelp());
        lupatiedot.addChild(asiointikieli);
    }

    private void createKoulutustausta(Theme koulutustaustaRyhmä) {
        koulutustaustaRyhmä.setHelp("Merkitse tälle sivulle pohjakoulutuksesi. Valitse pohjakoulutus, jonka perusteella haet. Voit merkitä vain yhden kohdan. HUOM! Jos olet suorittanut lukion oppimäärän tai ylioppilastutkinnon, et voi valita kohtaa Perusopetuksen oppimäärä. Lukion oppimäärän tai ylioppilastutkinnon suorittaneet eivät voi hakea perusopetuksen päättötodistuksella. Ammatillisella perustutkintotodistuksella et voi hakea ammatillisen koulutuksen ja lukiokoulutuksen yhteishaussa. Oppilaitokset tarkistavat todistukset hyväksytyiksi tulleilta hakijoilta.");
        Radio millatutkinnolla = new Radio("millatutkinnolla", createI18NText("Valitse tutkinto, jolla haet koulutukseen"));
        millatutkinnolla.addOption("tutkinto1", createI18NText("Perusopetuksen oppimäärä"), "tutkinto1", "Valitse tämä, jos olet käynyt peruskoulun.");
        millatutkinnolla.addOption("tutkinto2", createI18NText("Perusopetuksen erityisopetuksen osittain yksilöllistetty oppimäärä"), "tutkinto2", "Valitse tämä, jos olet opiskellut yksilöllistetyn oppimäärän puolessa tai alle puolessa oppiaineista.");
        millatutkinnolla.addOption("tutkinto3", createI18NText("Perusopetuksen erityisopetuksen yksilöllistetty oppimäärä, opetus järjestetty toiminta-alueittain"), "tutkinto3", "Valitse tämä, jos olet osallistunut harjaantumisopetukseen.");
        millatutkinnolla.addOption("tutkinto4", createI18NText("Perusopetuksen pääosin tai kokonaan yksilöllistetty oppimäärä"), "tutkinto4", "Valitse tämä, jos olet opiskellut peruskoulun kokonaan yksilöllistetyn oppimäärän mukaan tai olet opiskellut yli puolet opinnoistasi yksilöllistetyn opetuksen mukaan.");
        millatutkinnolla.addOption("tutkinto5", createI18NText("Oppivelvollisuuden suorittaminen keskeytynyt (ei päättötodistusta)"), "tutkinto5", "Valitse tämä vain, jos sinulla ei ole lainkaan päättötodistusta.");
        millatutkinnolla.addOption("tutkinto6", createI18NText("Lukion päättötodistus, ylioppilastutkinto tai abiturientti"), "tutkinto6", "Valitse tämä, jos olet suorittanut lukion ja sinulla on suomalainen tai kansainvälinen ylioppilastutkinto, tai olet suorittanut yhdistelmätutkinnon, johon sisältyy lukion vahimmäisoppimäärää vastaavat opinnot.");
        millatutkinnolla.addOption("tutkinto7", createI18NText("Ulkomailla suoritettu koulutus"), "tutkinto7", "Valitse tämä, jos olet suorittanut tutkintosi ulkomailla.");
        millatutkinnolla.setVerboseHelp(getVerboseHelp());
        millatutkinnolla.addAttribute("required", "required");

        Radio peruskoulu2012 = new Radio("peruskoulu2012", createI18NText("Saatko peruskoulun päättötodistuksen hakukeväänä 2012?"));
        peruskoulu2012.addOption("kylla", createI18NText("Kyllä"), "kyllä");
        peruskoulu2012.addOption("ei", createI18NText("En"), "ei");
        peruskoulu2012.addAttribute("required", "required");
        peruskoulu2012.setVerboseHelp(getVerboseHelp());

        TextQuestion paattotodistusvuosi = new TextQuestion("päättötodistusvuosi", createI18NText("Olen saanut päättötodistuksen jo aiemmin, vuonna"));
        paattotodistusvuosi.addAttribute("placeholder", "vvvv");
        paattotodistusvuosi.addAttribute("required", "required");
        paattotodistusvuosi.addAttribute("pattern", "^([1][9]\\d\\d|200[0-9]|201[0-1])$");
        paattotodistusvuosi.addAttribute("size", "4");
        paattotodistusvuosi.addAttribute("maxlength", "4");

        RelatedQuestionRule rule = new RelatedQuestionRule("rule6", peruskoulu2012.getId(), peruskoulu2012.getOptions().get(1).getValue());
        rule.addChild(paattotodistusvuosi);
        peruskoulu2012.addChild(rule);

        DropdownSelect tutkinnonOpetuskieli = new DropdownSelect("opetuskieli", createI18NText("Mikä oli tukintosi opetuskieli"));
        tutkinnonOpetuskieli.addOption("suomi", createI18NText("Suomi"), "Suomi");
        tutkinnonOpetuskieli.addOption("ruotsi", createI18NText("Ruotsi"), "Ruotsi");
        tutkinnonOpetuskieli.addAttribute("placeholder", "Tutkintosi opetuskieli");
        tutkinnonOpetuskieli.setHelp("Merkitse tähän se kieli, jolla suoritit suurimman osan opinnoistasi. Jos suoritit opinnot kahdella kielellä tasapuolisesti, valitse toinen niistä");
        tutkinnonOpetuskieli.setVerboseHelp(getVerboseHelp());

        CheckBox suorittanut1 = new CheckBox("suorittanut1", createI18NText("Kymppiluokka (perusopetuksen lisäopetuksen oppimäärä, vähintään 1100 tuntia)"));
        CheckBox suorittanut2 = new CheckBox("suorittanut2", createI18NText("Vammaisten valmentava ja kuntouttava opetus ja ohjaus"));
        CheckBox suorittanut3 = new CheckBox("suorittanut3", createI18NText("Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus"));
        CheckBox suorittanut4 = new CheckBox("suorittanut4", createI18NText("Talouskoulu (muuna kuin ammatillisena peruskoulutuksena järjestettävä kotitalousopetus)"));
        CheckBox suorittanut5 = new CheckBox("suorittanut5", createI18NText("Ammattistartti (ammatilliseen peruskoulutukseen ohjaava ja valmistava koulutus, vähintään 20 tuntia)"));
        CheckBox suorittanut6 = new CheckBox("suorittanut6", createI18NText("Kansanopiston lukuvuoden mittainen linja ammatilliseen peruskoulutukseen"));

        Radio osallistunut = new Radio("osallistunut", createI18NText("Oletko osallistunut viimeisen vuoden aikana jonkun hakukohteen alan pääsykokeisiin?"));
        osallistunut.addOption("ei", createI18NText("En"), "Ei");
        osallistunut.addOption("kylla", createI18NText("Kyllä"), "Kyllä");
        osallistunut.addAttribute("required", "required");
        osallistunut.setVerboseHelp(getVerboseHelp());

        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule3", millatutkinnolla.getId(), "(" +
                millatutkinnolla.getOptions().get(0).getValue() + "|" + millatutkinnolla.getOptions().get(1).getValue() +
                "|" + millatutkinnolla.getOptions().get(2).getValue() + "|" + millatutkinnolla.getOptions().get(3).getValue() + ")");
        relatedQuestionRule.addChild(peruskoulu2012);
        relatedQuestionRule.addChild(tutkinnonOpetuskieli);
        relatedQuestionRule.addChild(suorittanut1);
        relatedQuestionRule.addChild(suorittanut2);
        relatedQuestionRule.addChild(suorittanut3);
        relatedQuestionRule.addChild(suorittanut4);
        relatedQuestionRule.addChild(suorittanut5);
        relatedQuestionRule.addChild(suorittanut6);
        millatutkinnolla.addChild(relatedQuestionRule);

        koulutustaustaRyhmä.addChild(millatutkinnolla);
        koulutustaustaRyhmä.addChild(osallistunut);
    }

    private String getVerboseHelp() {
        return " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur nec dolor quam. Duis sodales placerat scelerisque. Suspendisse porta mauris eu felis malesuada rutrum. Aliquam varius fringilla mi sed luctus. Nam in enim ipsum. Sed lobortis lorem sit amet justo blandit et tempus ante eleifend. Proin egestas, magna et condimentum egestas, arcu mauris tincidunt augue, eget varius diam massa nec nisi. Proin dolor risus, tincidunt non faucibus imperdiet, fringilla quis massa. Curabitur pharetra posuere est, sit amet pulvinar urna facilisis at. Praesent posuere feugiat elit vel porttitor. Integer venenatis, arcu ac suscipit ornare, augue nibh tempus libero, eget molestie turpis massa quis purus. Suspendisse id libero dolor. Ut eget velit augue, eget fringilla erat. Quisque sed neque non arcu elementum vehicula eget at est. Etiam dictum fringilla mi, sit amet sodales tortor facilisis in.\n" +
                "\n" +
                "Nunc nisl felis, placerat non pellentesque non, dapibus non sem. Nunc et consectetur tellus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nulla facilisi. Nulla facilisi. Etiam lobortis, justo non eleifend rhoncus, eros felis vestibulum metus, ut ullamcorper neque urna et velit. Duis congue tincidunt urna non consectetur. Phasellus quis ligula et libero convallis eleifend non quis velit. Morbi luctus, ligula sed mollis placerat, nunc justo tempor velit, eget dignissim ante ipsum eu elit. Sed interdum urna in justo eleifend id fringilla mi facilisis. Ut id sapien erat. Aenean urna quam, aliquet nec imperdiet quis, suscipit eu nunc. Vestibulum vitae dolor in sapien auctor hendrerit et et turpis. Ut at diam eu sapien blandit blandit at in lorem.\n" +
                "\n" +
                "Aenean ornare, mi non rutrum gravida, augue neque pretium leo, in porta justo mauris eget orci. Donec porttitor eleifend aliquam. Cras mattis tincidunt purus, et facilisis risus consequat vitae. Nunc consectetur, odio sit amet rhoncus iaculis, ipsum lectus pharetra lectus, sit amet vestibulum est mi commodo enim. Sed libero sem, iaculis a lobortis non, molestie id arcu. Donec gravida tincidunt ligula quis mattis. Nulla sit amet malesuada sem. Duis porta adipiscing purus iaculis consequat. Aliquam erat volutpat. ";
    }

    @Override
    public void insert(FormModel formModel) {
        throw new RuntimeException("Insert not implemented");
    }


    @Override
    public void insertModelAsJsonString(String builder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Question createRequiredTextQuestion(final String id, final String name, final String size) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NText(name));
        textQuestion.addAttribute("required", "required");
        textQuestion.addAttribute("size", size);
        return textQuestion;
    }

    public FormModel getModel() {
        return formModel;
    }


    @Override
    public List<FormModel> find(FormModel formModel) {
        return Lists.newArrayList(getModel());
    }

    @Override
    public void update(FormModel o, FormModel n) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(FormModel formModel) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Form getActiveForm(String applicationPeriodId, String formId) {
        try {
            return formModel.getApplicationPeriodById(applicationPeriodId).getFormById(formId);
        } catch (Exception e) {
            throw new ResourceNotFoundExceptionRuntime("Not found");
        }
    }

    @Override
    public Phase getFirstPhase(String applicationPeriodId, String formId) {
        try {
            return this.getActiveForm(applicationPeriodId, formId).getFirstPhase();
        } catch (Exception e) {
            throw new ResourceNotFoundExceptionRuntime("Not found");
        }
    }

    @Override
    public Phase getLastPhase(String applicationPeriodId, String formId) {
        try {
            return this.getActiveForm(applicationPeriodId, formId).getLastPhase();
        } catch (Exception e) {
            throw new ResourceNotFoundExceptionRuntime("Not found");
        }
    }

    @Override
    public Map<String, ApplicationPeriod> getApplicationPerioidMap() {
        return getModel().getApplicationPerioidMap();
    }

    @Override
    public ApplicationPeriod getApplicationPeriodById(final String applicationPeriodId) {
        return getModel().getApplicationPeriodById(applicationPeriodId);
    }

    @Override
    public Form getForm(String applicationPeriodId, String formId) {
        ApplicationPeriod applicationPeriod = getApplicationPeriodById(applicationPeriodId);
        return applicationPeriod.getFormById(formId);
    }

    @Override
    public List<Validator> getVaiheValidators(ApplicationState applicationState) {
        return Collections.<Validator>emptyList();
    }

    private Map<String, PostOffice> getPostOffices() {
        Map<String, PostOffice> postOffices = new HashMap<String, PostOffice>();
        PostOffice helsinki = new PostOffice("Helsinki");
        PostOffice espoo = new PostOffice("Espoo");
        PostOffice tampere = new PostOffice("Tampere");
        postOffices.put("00180", helsinki);
        postOffices.put("00002", helsinki);
        postOffices.put("00100", helsinki);
        postOffices.put("00102", helsinki);
        postOffices.put("00120", helsinki);
        postOffices.put("00130", helsinki);
        postOffices.put("00140", helsinki);
        postOffices.put("00150", helsinki);
        postOffices.put("00160", helsinki);
        postOffices.put("00170", helsinki);
        postOffices.put("00190", helsinki);
        postOffices.put("00200", helsinki);
        postOffices.put("02100", espoo);
        postOffices.put("02110", espoo);
        postOffices.put("02120", espoo);
        postOffices.put("02130", espoo);
        postOffices.put("02140", espoo);
        postOffices.put("02150", espoo);
        postOffices.put("02160", espoo);
        postOffices.put("02170", espoo);
        postOffices.put("02230", espoo);
        postOffices.put("33100", tampere);
        postOffices.put("33310", tampere);
        postOffices.put("33540", tampere);
        postOffices.put("33200", tampere);
        return postOffices;
    }

}
