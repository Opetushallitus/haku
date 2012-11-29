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

package fi.vm.sade.oppija.haku.dao.impl;

import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.PostOffice;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Phase;
import fi.vm.sade.oppija.haku.domain.elements.Theme;
import fi.vm.sade.oppija.haku.domain.elements.custom.*;
import fi.vm.sade.oppija.haku.domain.elements.questions.*;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import fi.vm.sade.oppija.haku.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("FormModelDummyMemoryDao")
public class FormModelDummyMemoryDaoImpl implements FormModelDAO, FormService {

    final ApplicationPeriod applicationPeriod;
    private FormModel formModel;

    public FormModelDummyMemoryDaoImpl() {
        this("yhteishaku", "henkilotiedot");
    }

    public FormModelDummyMemoryDaoImpl(final String formId, final String firstCategoryId) {
        this.applicationPeriod = new ApplicationPeriod("Yhteishaku");
        final Calendar instance = GregorianCalendar.getInstance();
        instance.roll(Calendar.YEAR, 1);
        applicationPeriod.setEnd(instance.getTime());
        formModel = new FormModel();
        formModel.addApplicationPeriod(applicationPeriod);
        Phase henkilötiedot = new Phase(firstCategoryId, "Henkilötiedot", false);
        Phase koulutustausta = new Phase("koulutustausta", "Koulutustausta", false);
        Phase hakutoiveet = new Phase("hakutoiveet", "Hakutoiveet", false);
        Phase arvosanat = new Phase("arvosanat", "Arvosanat", false);
        Phase lisätiedot = new Phase("lisatiedot", "Lisätiedot", false);
        Phase esikatselu = new Phase("esikatselu", "Esikatselu", true);

        Form form = new Form(formId, "Ammatillisen koulutuksen ja lukiokoulutuksen yhteishaku, syksy 2013");
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
        oppiaineList.add(new SubjectRow("tietotekniikka", "Tietotekniikka"));
        oppiaineList.add(new SubjectRow("kansantaloustiede", "Kansantaloustiede"));
        oppiaineMap.put("S1508", oppiaineList);


        final String id = "S1508";
        Radio radio = new Radio(id + "_additional_question_1", "Tällä alalla on terveydentilavaatimuksia, jotka voivat olla opiskelijan ottamisen esteenä. Onko sinulla terveydellisiä tekijöitä, jotka voivat olla opiskelijatksi ottamisen esteenä?");
        radio.addOption(id + "_q1_option_1", "q1_option_1", "Ei");
        radio.addOption(id + "_q1_option_2", "q1_option_2", "Kyllä. Ymmärrä, etten tästä johtuen ehkä tule valituksi");

        Radio radio2 = new Radio(id + "_additional_question_2", "Tässä koulutuksessa opiskelijaksi ottamisen esteenä voi olla eiempi päätös opiskeluoikeuden peruuttamisessa. Onko opiskeluoikeutesi aiemmin peruutettu terveydentilasi tai muiden henkilöiden turvallisuuden vaarantamisen takia?");
        radio2.addOption(id + "_q2_option_1", "q2_option_1", "Ei");
        radio2.addOption(id + "_q2_option_2", "q2_option_2", "Kyllä. Ymmärrä, etten tästä johtuen ehkä tule valituksi");

        Radio radio3 = new Radio(id + "_additional_question_3", "Jos olet osallistunut saman alan pääsykokeeseen, niin haluatko käyttää hyväksyttyjä koetuloksiasi?");
        radio3.addOption(id + "_q3_option_1", "q3_option_1", "En, en ole osallistunut pääsykokeeseen");
        radio3.addOption(id + "_q3_option_2", "q3_option_2", "Ei, en halua käyttää tuloksia");
        radio3.addOption(id + "_q3_option_3", "q3_option_3", "Kyllä, haluan käyttää pääsykoetuloksia");

        List<Question> lisakysymysList = new ArrayList<Question>();
        lisakysymysList.add(radio);
        lisakysymysList.add(radio2);
        lisakysymysList.add(radio3);
        lisakysymysMap.put(id, lisakysymysList);


        Theme henkilötiedotRyhmä = new Theme("HenkilotiedotGrp", "Henkilötiedot", null);
        Theme koulutustaustaRyhmä = new Theme("KoulutustaustaGrp", "Koulutustausta", null);
        Theme hakutoiveetRyhmä = new Theme("hakutoiveetGrp", "Hakutoiveet", lisakysymysMap);
        Theme arvosanatRyhmä = new Theme("arvosanatGrp", "Arvosanat", oppiaineMap);
        Theme tyokokemusRyhmä = new Theme("tyokokemusGrp", "Työkokemus", null);
        Theme lupatiedotRyhmä = new Theme("lupatiedotGrp", "Lupatiedot", null);
        Theme yhteenvetoRyhmä = new Theme("yhteenvetoGrp", "yhteenveto", null);

        henkilötiedot.addChild(henkilötiedotRyhmä);
        koulutustausta.addChild(koulutustaustaRyhmä);
        hakutoiveet.addChild(hakutoiveetRyhmä);
        arvosanat.addChild(arvosanatRyhmä);
        lisätiedot.addChild(tyokokemusRyhmä);
        lisätiedot.addChild(lupatiedotRyhmä);

        DropdownSelect aidinkieli = new DropdownSelect("äidinkieli", "Äidinkieli");
        aidinkieli.addOption("suomi", "Suomi", "Suomi");
        aidinkieli.addOption("ruotsi", "Ruotsi", "Ruotsi");
        aidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        aidinkieli.addAttribute("required", "required");
        aidinkieli.setVerboseHelp(getVerboseHelp());
        aidinkieli.setInline(true);


        DropdownSelect kansalaisuus = new DropdownSelect("kansalaisuus", "Kansalaisuus");
        kansalaisuus.addOption("fi", "fi", "Suomi");
        kansalaisuus.addOption("sv", "sv", "Ruotsi");
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.addAttribute("required", "required");
        kansalaisuus.setHelp("Jos sinulla on kaksoiskansalaisuus, valitse toinen niistä");
        kansalaisuus.setVerboseHelp(getVerboseHelp());
        kansalaisuus.setInline(true);

        DropdownSelect kotikunta = new DropdownSelect("kotikunta", "Kotikunta");
        kotikunta.addOption("jalasjarvi, ", "Jalasjärvi", "Jalasjärvi");
        kotikunta.addOption("janakkala", "Janakkala", "Janakkala");
        kotikunta.addOption("joensuu", "Joensuu", "Joensuu");
        kotikunta.addOption("jokioinen", "Jokioinen", "Jokioinen");
        kotikunta.addOption("jomala", "Jomala", "Jomala");
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        kotikunta.addAttribute("required", "required");
        kotikunta.setVerboseHelp(getVerboseHelp());
        kotikunta.setInline(true);

        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", "Kutsumanimi");
        kutsumanimi.setHelp("Valitse kutsumanimeksi jokin virallisista etunimistäsi");
        kutsumanimi.addAttribute("required", "required");
        kutsumanimi.addAttribute("size", "20");
        kutsumanimi.setVerboseHelp(getVerboseHelp());
        kutsumanimi.setInline(true);

        TextQuestion email = new TextQuestion("Sähköposti", "Sähköpostiosoite");
        email.addAttribute("size", "40");
        email.setHelp("Kirjoita tähän sähköpostiosoite, johon haluat vastaanottaa opiskelijavalintaan liittyviä tietoja ja jota käytät säännöllisesti. Saat vahvistuksen hakemuksen perille menosta tähän sähköpostiosoitteeseen.");
        email.setVerboseHelp(getVerboseHelp());
        email.setInline(true);

        TextQuestion henkilötunnus = new TextQuestion("Henkilotunnus", "Henkilötunnus");
        henkilötunnus.addAttribute("placeholder", "ppkkvv***** or dd.mm.yyyy");
        henkilötunnus.addAttribute("required", "required");
        henkilötunnus.addAttribute("pattern", "([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))|[0-9]{2}[.][0-9]{2}[.][0-9]{4}");
        henkilötunnus.addAttribute("size", "11");
        henkilötunnus.addAttribute("maxlength", "11");
        henkilötunnus.setHelp("Jos sinulla ei ole suomalaista henkilötunnusta, täytä tähän syntymäaikasi");
        henkilötunnus.setVerboseHelp(getVerboseHelp());
        henkilötunnus.setInline(true);

        Radio sukupuoli = new Radio("Sukupuoli", "Sukupuoli");
        sukupuoli.addOption("mies", "Mies", "Mies");
        sukupuoli.addOption("nainen", "Nainen", "Nainen");
        sukupuoli.addAttribute("required", "required");
        sukupuoli.setVerboseHelp(getVerboseHelp());
        sukupuoli.setInline(true);

        SocialSecurityNumber socialSecurityNumber = new SocialSecurityNumber("ssn_question", "Henkilötunnus");
        socialSecurityNumber.setSsn(henkilötunnus);
        socialSecurityNumber.setSex(sukupuoli);
        socialSecurityNumber.setMaleId(sukupuoli.getOptions().get(0).getId());
        socialSecurityNumber.setFemaleId(sukupuoli.getOptions().get(1).getId());
        socialSecurityNumber.setNationalityId(kansalaisuus.getId());

//        SelectingSubmitRule autofillhetu = new SelectingSubmitRule(henkilötunnus.getId(), sukupuoli.getId());
//        autofillhetu.addBinding(henkilötunnus, sukupuoli, "\\d{6}\\S\\d{2}[13579]\\w", sukupuoli.getOptions().get(0));
//        autofillhetu.addBinding(henkilötunnus, sukupuoli, "\\d{6}\\S\\d{2}[24680]\\w", sukupuoli.getOptions().get(1));

        Element postinumero = new PostalCode("Postinumero", "Postinumero", getPostOffices());
        postinumero.addAttribute("size", "5");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("placeholder", "#####");
        postinumero.addAttribute("maxlength", "5");
        postinumero.setHelp("Kirjoita tähän osoite, johon haluat vastaanottaan opiskelijavalintaan liittyvää postia, kuten kutsun valintakokeeseen tai valintapäätöksen.");

        DropdownSelect asuinmaa = new DropdownSelect("asuinmaa", "Asuinmaa");
        asuinmaa.addOption("valitse", null, "Valitse");
        asuinmaa.addOption("fi", "fi", "Suomi");
        asuinmaa.addOption("sv", "sv", "Ruotsi");
        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        asuinmaa.addAttribute("required", "required");
        asuinmaa.setVerboseHelp(getVerboseHelp());
        asuinmaa.setInline(true);

        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "Lähiosoite", "40");
        lahiosoite.setInline(true);

        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule1", asuinmaa.getId(), "fi");
        relatedQuestionRule.addChild(lahiosoite);
        relatedQuestionRule.addChild(postinumero);
        relatedQuestionRule.addChild(kotikunta);
        asuinmaa.addChild(relatedQuestionRule);

        TextArea osoite = new TextArea("osoite", "Osoite");
        osoite.addAttribute("required", "required");
        RelatedQuestionRule relatedQuestionRule2 = new RelatedQuestionRule("rule2", asuinmaa.getId(), "sv");
        relatedQuestionRule2.addChild(osoite);
        osoite.setVerboseHelp(getVerboseHelp());
        asuinmaa.addChild(relatedQuestionRule2);
        osoite.setInline(true);

        TextQuestion matkapuhelinnumero = new TextQuestion("matkapuhelinnumero", "Matkapuhelinnumero");
        matkapuhelinnumero.setHelp("Kirjoita tähän matkapuhelinnumerosi, jotta sinuun saadaan tarvittaessa yhteyden.");
        matkapuhelinnumero.addAttribute("size", "20");
        matkapuhelinnumero.setVerboseHelp(getVerboseHelp());
        matkapuhelinnumero.setInline(true);

        kotikunta.setHelp("Kotikunta on tyypillisesti se kunta, jossa asut.");
        aidinkieli.setHelp("Jos omaa äidinkieltäsi ei löydy valintalistasta, valitse äidinkieleksesi..");

        Question sukunimi = createRequiredTextQuestion("Sukunimi", "Sukunimi", "30");
        sukunimi.setInline(true);
        Question etunimet = createRequiredTextQuestion("Etunimet", "Etunimet", "30");
        etunimet.setInline(true);

        henkilötiedotRyhmä.addChild(sukunimi)
                .addChild(etunimet)
                .addChild(kutsumanimi)
                .addChild(socialSecurityNumber)
                .addChild(email)
                .addChild(matkapuhelinnumero)
                .addChild(asuinmaa)
                .addChild(kansalaisuus)
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
        gradeRange.add(new Option("grade_nograde", "-1", "Ei arvosanaa"));
        gradeRange.add(new Option("grade_10", "10", "10"));
        gradeRange.add(new Option("grade_9", "9", "9"));
        gradeRange.add(new Option("grade_8", "8", "8"));
        gradeRange.add(new Option("grade_7", "7", "7"));
        gradeRange.add(new Option("grade_6", "6", "6"));
        gradeRange.add(new Option("grade_5", "5", "5"));
        gradeRange.add(new Option("grade_4", "4", "4"));

        SubjectRow finnish = new SubjectRow("subject_finnish", "Äidinkieli ja kirjallisuus");
        List<SubjectRow> subjectRowsBefore = new ArrayList<SubjectRow>();
        subjectRowsBefore.add(finnish);

        LanguageRow a1 = new LanguageRow("lang_a1", "A1-kieli");
        LanguageRow b1 = new LanguageRow("lang_b1", "B1-kieli");

        List<LanguageRow> languageRows = new ArrayList<LanguageRow>();
        languageRows.add(a1);
        languageRows.add(b1);

        SubjectRow matematiikka = new SubjectRow("subject_matematiikka", "Matematiikka");
        SubjectRow biologia = new SubjectRow("subject_biologia", "Biologia");
        SubjectRow maantieto = new SubjectRow("subject_maantieto", "Maantieto");
        SubjectRow fysiikka = new SubjectRow("subject_fysiikka", "Fysiikka");
        SubjectRow kemia = new SubjectRow("subject_kemia", "Kemia");
        SubjectRow terveystieto = new SubjectRow("subject_terveystieto", "Terveystieto");
        SubjectRow uskonto = new SubjectRow("subject_uskonto", "Uskonto tai elämänkatsomustieto");
        SubjectRow historia = new SubjectRow("subject_historia", "Historia");
        SubjectRow yhteiskuntaoppi = new SubjectRow("subject_yhteiskuntaoppi", "Yhteiskuntaoppi");
        SubjectRow musiikki = new SubjectRow("subject_musiikki", "Musiikki");
        SubjectRow kuvataide = new SubjectRow("subject_kuvataide", "Kuvataide");
        SubjectRow kasityo = new SubjectRow("subject_kasityo", "Käsityö");
        SubjectRow liikunta = new SubjectRow("subject_liikunta", "Liikunta");
        SubjectRow kotitalous = new SubjectRow("subject_kotitalous", "Kotitalous");
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
        languageOptions.add(new Option("langoption_" + "eng", "eng", "englanti"));
        languageOptions.add(new Option("langoption_" + "swe", "swe", "ruotsi"));
        languageOptions.add(new Option("langoption_" + "fra", "fra", "ranska"));
        languageOptions.add(new Option("langoption_" + "ger", "ger", "saksa"));
        languageOptions.add(new Option("langoption_" + "rus", "rus", "venäjä"));
        languageOptions.add(new Option("langoption_" + "fin", "fin", "suomi"));

        List<Option> scopeOptions = new ArrayList<Option>();
        scopeOptions.add(new Option("scopeoption_" + "a1", "a1", "A1"));
        scopeOptions.add(new Option("scopeoption_" + "a2", "a2", "A2"));
        scopeOptions.add(new Option("scopeoption_" + "b1", "b1", "B1"));
        scopeOptions.add(new Option("scopeoption_" + "b2", "b2", "B2"));
        scopeOptions.add(new Option("scopeoption_" + "b3", "b3", "B3"));

        GradeGrid gradeGrid = new GradeGrid("gradegrid", "Arvosanat",
                "Kieli", subjectRowsBefore, languageRows,
                subjectRowsAfter, scopeOptions, languageOptions, gradeRange);
        gradeGrid.setVerboseHelp(getVerboseHelp());

        return gradeGrid;
    }

    private void createArvosanat(Theme arvosanatRyhmä) {

        arvosanatRyhmä.addChild(createGradeGrid());
        arvosanatRyhmä.setHelp("Merkitse arvosanat siitä todistuksesta, jolla haet koulutukseen (perusopetus,tai sitä vastaavat opinnot, lukiokoulutus). Korotetut arvosanat voit merkitä, mikäli olet saanut korotuksista virallisen todistuksen. Huomio. Jos olet suorittanut lukion oppimäärän tai ylioppilastutkinnon, et voi hakea perusopetuksen päättötodistuksella. Ammatillisella perustutkinnolla et voi hakea. Oppilaitokset tarkistavat todistukset hyväksytyiksi tulleilta hakijoilta. 1. Tarkista ja täydennä taulukkoon todistuksen oppiaineet ja arvosanat, jotka poikkeavat esitäytetyistä. Huom! Valinnaisaineiden arvosanat merkitään vain mikäli niiden laajuus on vähintään kaksi vuosiviikkotuntia perusopetuksen vuosiluokkien 7-9 aikana. Jos sinulla on yksilöllistettyjä arvosanoja, valitse listasta arvosana, jossa on tähti.");

    }

    private void createHakutoiveet(Theme hakutoiveetRyhmä) {
        hakutoiveetRyhmä.setHelp("Merkitse tälle sivulle koulutukset, joihin haluat hakea. Merkitse hakutoiveesi siinä järjestyksessä, kun toivot tulevasi niihin valituksi. Jos olet valinnut korissa koulutuksia, voit siirttää ne hakutoivelistalle. Voit halutessasi etsiä koulutuksia koulutuskorin kautta. harkitse hakutoivejärjestystä tarkoin, sillä se on sitova, etkä voi muuttaa sitä enää hakuajan jälkeen. Jos et pääse koulutukseen, jonka olet merkinnyt ensimmäiselle sijalle, tarkistetaan riittävätkö pisteesi toiselle sijalle merkitsemääsi hakutoiveeseen jne. Jos pääset esimerkiksi toisena toiveena olevaan koulutukseen, alemmat hakutoiveet peruuntuvat automaattisesti, etkä voi enää tulla valituksi niihin. Ylempiin hakutoiveisiin voit vielä päästä. HUOM! Lukion oppimäärän tai ylioppilastutkinnon suorittaneet voivat hakea vain heille varatuille aloituspaikoille (yo).");
        SortableTable sortableTable = new SortableTable("preferencelist", "Hakutoiveet", "Ylös", "Alas");
        PreferenceRow pr1 = new PreferenceRow("preference1", "Hakutoive 1", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr2 = new PreferenceRow("preference2", "Hakutoive 2", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr3 = new PreferenceRow("preference3", "Hakutoive 3", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr4 = new PreferenceRow("preference4", "Hakutoive 4", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr5 = new PreferenceRow("preference5", "Hakutoive 5", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        sortableTable.addChild(pr1);
        sortableTable.addChild(pr2);
        sortableTable.addChild(pr3);
        sortableTable.addChild(pr4);
        sortableTable.addChild(pr5);
        sortableTable.setVerboseHelp(getVerboseHelp());
        hakutoiveetRyhmä.addChild(sortableTable);
    }

    private void createTyokokemus(Theme tyokokemus) {
        tyokokemus.setHelp("Työkokemukseksi lasketaan työ, josta sinulla on työtodistus. Työhön rinnastettavaksi toiminnaksi lasketaan varusmiespalvelu, siviilipalvelus, vähintään kolmen kuukauden pituinen työpajatoimintaan osallistuminen tai työharjoitteluun osallistuminen, oppisopimuskoulutus. Oppilaitos tarkistaa työtodistukset ennen lopullista valintaa.");
        TextQuestion tyokokemuskuukaudet = new TextQuestion("tyokokemuskuukaudet", "Työkokemus");
        tyokokemuskuukaudet.setHelp("Merkitse kenttään hakuajan päättymiseen mennessä kertynyt työkokemuksesi. Voit käyttää laskemiseen apuna laskuria.");
        tyokokemuskuukaudet.addAttribute("placeholder", "kuukautta");
        tyokokemuskuukaudet.addAttribute("pattern", "[0-9]*");
        tyokokemuskuukaudet.addAttribute("size", "8");
        tyokokemuskuukaudet.setVerboseHelp(getVerboseHelp());
        tyokokemus.addChild(tyokokemuskuukaudet);
    }

    private void createLupatiedot(Theme lupatiedot) {
        CheckBox lupa = new CheckBox("lupa", "Ohjeteksti lorem ipsum.");
        lupa.addOption("lupa1", "lupa1", "Haluan, että huoltajalleni lähetetään tieto sähköpostilla hakulomakkeen täyttämisestä");
        lupa.addOption("lupa2", "lupa2", "Minulle saa lähettää postia vapaista opiskelupaikoista ja muuta koulutusmarkkinointia");
        lupa.addOption("lupa3", "lupa3", "Tietoni opiskeluvalinna tuloksista saa julkaista Internetissä");
        lupa.addOption("lupa4", "lupa4", "Valintaani koskevat tiedot saa lähettää minulle sähköisesti");
        lupa.addOption("lupa5", "lupa5", "Minulle saa lähettää tietoa opiskelijavalinnan etenemisestä ja tuloksista tekstiviestillä");
        lupa.setVerboseHelp(getVerboseHelp());

        Radio asiointikieli = new Radio("asiointikieli", "Asiointikieli, jolla haluat vastaanottaa opiskelijavalintaan liittyviä tietoja");
        asiointikieli.addOption("suomi", "suomi", "suomi");
        asiointikieli.addOption("ruotsi", "ruotsi", "ruotsi");
        asiointikieli.addAttribute("required", "required");
        asiointikieli.setVerboseHelp(getVerboseHelp());

        lupatiedot.addChild(lupa);
        lupatiedot.addChild(asiointikieli);
    }

    private void createKoulutustausta(Theme koulutustaustaRyhmä) {
        koulutustaustaRyhmä.setHelp("Merkitse tälle sivulle pohjakoulutuksesi. Valitse pohjakoulutus, jonka perusteella haet. Voit merkitä vain yhden kohdan. HUOM! Jos olet suorittanut lukion oppimäärän tai ylioppilastutkinnon, et voi valita kohtaa Perusopetuksen oppimäärä. Lukion oppimäärän tai ylioppilastutkinnon suorittaneet eivät voi hakea perusopetuksen päättötodistuksella. Ammatillisella perustutkintotodistuksella et voi hakea ammatillisen koulutuksen ja lukiokoulutuksen yhteishaussa. Oppilaitokset tarkistavat todistukset hyväksytyiksi tulleilta hakijoilta.");
        Radio millatutkinnolla = new Radio("millatutkinnolla", "Valitse tutkinto, jolla haet koulutukseen.");
        millatutkinnolla.addOption("tutkinto1", "tutkinto1", "Perusopetuksen oppimäärä", "Valitse tämä, jos olet käynyt peruskoulun.");
        millatutkinnolla.addOption("tutkinto2", "tutkinto2", "Perusopetuksen erityisopetuksen osittain yksilöllistetty oppimäärä", "Valitse tämä, jos olet opiskellut yksilöllistetyn oppimäärän puolessa tai alle puolessa oppiaineista.");
        millatutkinnolla.addOption("tutkinto3", "tutkinto3", "Perusopetuksen erityisopetuksen yksilöllistetty oppimäärä, opetus järjestetty toiminta-alueittain", "Valitse tämä, jos olet osallistunut harjaantumisopetukseen.");
        millatutkinnolla.addOption("tutkinto4", "tutkinto4", "Perusopetuksen pääosin tai kokonaan yksilöllistetty oppimäärä", "Valitse tämä, jos olet opiskellut peruskoulun kokonaan yksilöllistetyn oppimäärän mukaan tai olet opiskellut yli puolet opinnoistasi yksilöllistetyn opetuksen mukaan.");
        millatutkinnolla.addOption("tutkinto5", "tutkinto5", "Oppivelvollisuuden suorittaminen keskeytynyt (ei päättötodistusta)", "Valitse tämä vain, jos sinulla ei ole lainkaan päättötodistusta.");
        millatutkinnolla.addOption("tutkinto6", "tutkinto6", "Lukion päättötodistus, ylioppilastutkinto tai abiturientti", "Valitse tämä, jos olet suorittanut lukion ja sinulla on suomalainen tai kansainvälinen ylioppilastutkinto, tai olet suorittanut yhdistelmätutkinnon, johon sisältyy lukion vahimmäisoppimäärää vastaavat opinnot.");
        millatutkinnolla.addOption("tutkinto7", "tutkinto7", "Ulkomailla suoritettu koulutus", "Valitse tämä, jos olet suorittanut tutkintosi ulkomailla.");
        millatutkinnolla.setVerboseHelp(getVerboseHelp());
        millatutkinnolla.addAttribute("required", "required");

        Radio peruskoulu2012 = new Radio("peruskoulu2012", "Saatko peruskoulun päättötodistuksen hakukeväänä 2012?");
        peruskoulu2012.addOption("kylla", "Kyllä", "Kyllä");
        peruskoulu2012.addOption("ei", "Ei", "en, olen saanut päättötodistuksen jo aiemmin, vuonna");
        peruskoulu2012.addAttribute("required", "required");
        peruskoulu2012.setVerboseHelp(getVerboseHelp());

        DropdownSelect tutkinnonOpetuskieli = new DropdownSelect("opetuskieli", "Mikä oli tukintosi opetuskieli");
        tutkinnonOpetuskieli.addOption("suomi", "Suomi", "Suomi");
        tutkinnonOpetuskieli.addOption("ruotsi", "Ruotsi", "Ruotsi");
        tutkinnonOpetuskieli.addAttribute("placeholder", "Tutkintosi opetuskieli");
        tutkinnonOpetuskieli.setHelp("Merkitse tähän se kieli, jolla suoritit suurimman osan opinnoistasi. Jos suoritit opinnot kahdella kielellä tasapuolisesti, valitse toinen niistä");
        tutkinnonOpetuskieli.setVerboseHelp(getVerboseHelp());

        CheckBox suorittanut = new CheckBox("suorittanut", "Merkitse tähän, jos olet suorittanut jonkun seuraavista");
        suorittanut.addOption("suorittanut1", "suorittanut1", "Perusopetuksen lisäopetuksen oppimäärä (kymppiluokka)");
        suorittanut.addOption("suorittanut2", "suorittanut2", "Vammaisten valmentava ja kuntouttava opetus ja ohjaus");
        suorittanut.addOption("suorittanut3", "suorittanut3", "Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus");
        suorittanut.addOption("suorittanut4", "suorittanut4", "Muuna kuin ammatillisena peruskoulutuksena järjestettävä kotitalousopetus (talouskoulu)");
        suorittanut.addOption("suorittanut5", "suorittanut5", "Ammatilliseen peruskoulutukseen ohjaava ja valmistava koulutus (ammattistartti)");
        suorittanut.setVerboseHelp(getVerboseHelp());

        Radio osallistunut = new Radio("osallistunut", "Oletko osallistunut viimeisen vuoden aikana jonkun hakukohteen alan pääsykokeisiin?");
        osallistunut.addOption("ei", "Ei", "En");
        osallistunut.addOption("kylla", "Kyllä", "Kyllä");
        osallistunut.addAttribute("required", "required");
        osallistunut.setVerboseHelp(getVerboseHelp());

        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule3", millatutkinnolla.getId(), "(" +
                millatutkinnolla.getOptions().get(0).getValue() + "|" + millatutkinnolla.getOptions().get(1).getValue() +
                "|" + millatutkinnolla.getOptions().get(2).getValue() + "|" + millatutkinnolla.getOptions().get(3).getValue() + ")");
        relatedQuestionRule.addChild(peruskoulu2012);
        relatedQuestionRule.addChild(tutkinnonOpetuskieli);
        relatedQuestionRule.addChild(suorittanut);
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
    public FormModel find() {
        return formModel;
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
        TextQuestion textQuestion = new TextQuestion(id, name);
        textQuestion.addAttribute("required", "required");
        textQuestion.addAttribute("size", size);
        return textQuestion;
    }


    public FormModel getModel() {
        return formModel;
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
            throw new ResourceNotFoundException("Not found");
        }
    }

    @Override
    public Phase getFirstCategory(String applicationPeriodId, String formId) {
        try {
            return this.getActiveForm(applicationPeriodId, formId).getFirstCategory();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Not found");
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Validator> getVaiheValidators(HakemusState hakemusState) {
        return Collections.EMPTY_LIST;
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
