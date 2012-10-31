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
import fi.vm.sade.oppija.haku.domain.*;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Teema;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.elements.custom.*;
import fi.vm.sade.oppija.haku.domain.elements.questions.*;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.haku.domain.rules.SelectingSubmitRule;
import fi.vm.sade.oppija.haku.service.FormService;
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
        this.applicationPeriod = new ApplicationPeriod("test");
        final Calendar instance = GregorianCalendar.getInstance();
        instance.roll(Calendar.YEAR, 1);
        applicationPeriod.setEnd(instance.getTime());
        formModel = new FormModel();
        formModel.addApplicationPeriod(applicationPeriod);
        Vaihe henkilötiedot = new Vaihe(firstCategoryId, "Henkilötiedot", false);
        Vaihe koulutustausta = new Vaihe("koulutustausta", "Koulutustausta", false);
        Vaihe hakutoiveet = new Vaihe("hakutoiveet", "Hakutoiveet", false);
        Vaihe arvosanat = new Vaihe("arvosanat", "Arvosanat", false);
        Vaihe lisätiedot = new Vaihe("lisatiedot", "Lisätiedot", false);
        Vaihe esikatselu = new Vaihe("esikatselu", "Esikatselu", true);

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

        int AMOUNT_OF_TEST_OPETUSPISTE = 5;
        int AMOUNT_OF_TEST_HAKUKOHDE = 5;
        List<Organisaatio> institutes = new ArrayList<Organisaatio>();

        for (int i = 0; i < AMOUNT_OF_TEST_OPETUSPISTE; ++i) {
            Organisaatio op = new Organisaatio(String.valueOf(i), "Koulu" + i);
            institutes.add(op);
        }

        List<Question> oppianieList = new ArrayList<Question>();
        oppianieList.add(new SubjectRow("tietotekniikka", "Tietotekniikka"));
        oppianieList.add(new SubjectRow("kansantaloustiede", "Kansantaloustiede"));
        oppiaineMap.put("0_0", oppianieList);

        for (Organisaatio institute : institutes) {
            List<Hakukohde> hakukohdeList = new ArrayList<Hakukohde>();
            for (int i = 0; i < AMOUNT_OF_TEST_HAKUKOHDE; i++) {
                String id = String.valueOf(institute.getId()) + "_" + String.valueOf(i);
                Hakukohde h;
                if (i % 2 == 0) {
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
                }
            }
        }


        Teema henkilötiedotRyhmä = new Teema("HenkilotiedotGrp", "Henkilötiedot", null);
        Teema koulutustaustaRyhmä = new Teema("KoulutustaustaGrp", "Koulutustausta", null);
        Teema hakutoiveetRyhmä = new Teema("hakutoiveetGrp", "Hakutoiveet", lisakysymysMap);
        Teema arvosanatRyhmä = new Teema("arvosanatGrp", "Arvosanat", oppiaineMap);
        Teema tyokokemusRyhmä = new Teema("tyokokemusGrp", "Työkokemus", null);
        Teema lupatiedotRyhmä = new Teema("lupatiedotGrp", "Lupatiedot", null);
        Teema yhteenvetoRyhmä = new Teema("yhteenvetoGrp", "yhteenveto", null);

        henkilötiedot.addChild(henkilötiedotRyhmä);
        koulutustausta.addChild(koulutustaustaRyhmä);
        hakutoiveet.addChild(hakutoiveetRyhmä);
        arvosanat.addChild(arvosanatRyhmä);
        lisätiedot.addChild(tyokokemusRyhmä);
        lisätiedot.addChild(lupatiedotRyhmä);

        DropdownSelect äidinkieli = new DropdownSelect("äidinkieli", "Äidinkieli");
        äidinkieli.addOption("suomi", "Suomi", "Suomi");
        äidinkieli.addOption("ruotsi", "Ruotsi", "Ruotsi");
        äidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        äidinkieli.addAttribute("required", "required");

        DropdownSelect kansalaisuus = new DropdownSelect("kansalaisuus", "Kansalaisuus");
        kansalaisuus.addOption("suomi", "Suomi", "Suomi");
        kansalaisuus.addOption("ruotsi", "Ruotsi", "Ruotsi");
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.addAttribute("required", "required");
        kansalaisuus.setHelp("Jos sinulla on kaksoiskansalaisuus, valitse toinen niistä");

        DropdownSelect kotikunta = new DropdownSelect("kotikunta", "Kotikunta");
        kotikunta.addOption("jalasjarvi, ", "Jalasjärvi", "Jalasjärvi");
        kotikunta.addOption("janakkala", "Janakkala", "Janakkala");
        kotikunta.addOption("joensuu", "Joensuu", "Joensuu");
        kotikunta.addOption("jokioinen", "Jokioinen", "Jokioinen");
        kotikunta.addOption("jomala", "Jomala", "Jomala");
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        kotikunta.addAttribute("required", "required");

        TextQuestion henkilötunnus = new TextQuestion("Henkilotunnus", "Henkilötunnus");
        henkilötunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilötunnus.addAttribute("onChange", "submit()");
        henkilötunnus.addAttribute("title", "ppkkvv*****");
        henkilötunnus.addAttribute("required", "required");
        henkilötunnus.addAttribute("pattern", "[0-9]{6}.[0-9]{4}");
        henkilötunnus.setHelp("Jos sinulla ei ole suomalaista henkilötunnusta, täytä tähän syntymäaikasi");
        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", "Kutsumanimi");
        kutsumanimi.setHelp("Valitse kutsumanimeksi jokin virallisista etunimistäsi");
        kutsumanimi.addAttribute("required", "required");
        TextQuestion sähköposti = new TextQuestion("Sähköposti", "Sähköpostiosoite");
        sähköposti.setHelp("Kirjoita tähän sähköpostiosoite, johon haluat vastaanottaa opiskelijavalintaan liittyviä tietoja ja jota käytät säännöllisesti. Saat vahvistuksen hakemuksen perille menosta tähän sähköpostiosoitteeseen.");

        Radio sukupuoli = new Radio("Sukupuoli", "Sukupuoli");
        sukupuoli.addOption("mies", "Mies", "Mies");
        sukupuoli.addOption("nainen", "Nainen", "Nainen");
        sukupuoli.addAttribute("required", "required");

        SelectingSubmitRule autofillhetu = new SelectingSubmitRule(henkilötunnus.getId(), sukupuoli.getId());
        autofillhetu.addBinding(henkilötunnus, sukupuoli, "\\d{6}\\S\\d{2}[13579]\\w", sukupuoli.getOptions().get(0));
        autofillhetu.addBinding(henkilötunnus, sukupuoli, "\\d{6}\\S\\d{2}[24680]\\w", sukupuoli.getOptions().get(1));

        DropdownSelect asuinmaa = new DropdownSelect("Asuinmaa", "Asuinmaa");
        asuinmaa.addOption("suomi", "Suomi", "Suomi");
        asuinmaa.addOption("ruotsi", "Ruotsi", "Ruotsi");
        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        asuinmaa.addAttribute("required", "required");
        Element postinumero = createRequiredTextQuestion("Postinumero", "Postinumero");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("title", "#####");
        postinumero.setHelp("Kirjoita tähän osoite, johon haluat vastaanottaan opiskelijavalintaan liittyvää postia, kuten kutsun valintakokeeseen tai valintapäätöksen.");
        TextQuestion matkapuhelinnumero = new TextQuestion("matkapuhelinnumero", "Matkapuhelinnumero");
        matkapuhelinnumero.setHelp("Kirjoita tähän matkapuhelinnumerosi, jotta sinuun saadaan tarvittaessa yhteyden.");
        kotikunta.setHelp("Kotikunta on tyypillisesti se kunta, jossa asut.");
        äidinkieli.setHelp("Jos omaa äidinkieltäsi ei löydy valintalistasta, valitse äidinkieleksesi..");
        henkilötiedotRyhmä.addChild(createRequiredTextQuestion("Sukunimi", "Sukunimi"))
                .addChild(createRequiredTextQuestion("Etunimet", "Etunimet"))
                .addChild(kutsumanimi)
                .addChild(autofillhetu)
                .addChild(sähköposti)
                .addChild(matkapuhelinnumero)
                .addChild(asuinmaa)
                .addChild(createRequiredTextQuestion("Lähiosoite", "Lähiosoite"))
                .addChild(postinumero)
                .addChild(kotikunta)
                .addChild(kansalaisuus)
                .addChild(äidinkieli);

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

        return gradeGrid;
    }

    private void createArvosanat(Teema arvosanatRyhmä) {

        arvosanatRyhmä.addChild(createGradeGrid());
        arvosanatRyhmä.setHelp("Merkitse arvosanat siitä todistuksesta, jolla haet koulutukseen (perusopetus,tai sitä vastaavat opinnot, lukiokoulutus). Korotetut arvosanat voit merkitä, mikäli olet saanut korotuksista virallisen todistuksen. Huomio. Jos olet suorittanut lukion oppimäärän tai ylioppilastutkinnon, et voi hakea perusopetuksen päättötodistuksella. Ammatillisella perustutkinnolla et voi hakea. Oppilaitokset tarkistavat todistukset hyväksytyiksi tulleilta hakijoilta. 1. Tarkista ja täydennä taulukkoon todistuksen oppiaineet ja arvosanat, jotka poikkeavat esitäytetyistä. Huom! Valinnaisaineiden arvosanat merkitään vain mikäli niiden laajuus on vähintään kaksi vuosiviikkotuntia perusopetuksen vuosiluokkien 7-9 aikana. Jos sinulla on yksilöllistettyjä arvosanoja, valitse listasta arvosana, jossa on tähti.");

    }

    private void createHakutoiveet(Teema hakutoiveetRyhmä) {
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
        hakutoiveetRyhmä.addChild(sortableTable);
    }

    private void createTyokokemus(Teema tyokokemus) {
        tyokokemus.setHelp("Työkokemukseksi lasketaan työ, josta sinulla on työtodistus. Työhön rinnastettavaksi toiminnaksi lasketaan varusmiespalvelu, siviilipalvelus, vähintään kolmen kuukauden pituinen työpajatoimintaan osallistuminen tai työharjoitteluun osallistuminen, oppisopimuskoulutus. Oppilaitos tarkistaa työtodistukset ennen lopullista valintaa.");
        TextQuestion tyokokemuskuukaudet = new TextQuestion("tyokokemuskuukaudet", "Työkokemus");
        tyokokemuskuukaudet.setHelp("Merkitse kenttään hakuajan päättymiseen mennessä kertynyt työkokemuksesi. Voit käyttää laskemiseen apuna laskuria.");
        tyokokemuskuukaudet.addAttribute("placeholder", "kuukautta");
        tyokokemuskuukaudet.addAttribute("title", "kuukautta");
        tyokokemuskuukaudet.addAttribute("pattern", "[0-9]*");
        tyokokemus.addChild(tyokokemuskuukaudet);
    }

    private void createLupatiedot(Teema lupatiedot) {
        CheckBox lupa = new CheckBox("lupa", "Ohjeteksti lorem ipsum.");
        lupa.addOption("lupa1", "lupa1", "Haluan, että huoltajalleni lähetetään tieto sähköpostilla hakulomakkeen täyttämisestä");
        lupa.addOption("lupa2", "lupa2", "Minulle saa lähettää postia vapaista opiskelupaikoista ja muuta koulutusmarkkinointia");
        lupa.addOption("lupa3", "lupa3", "Tietoni opiskeluvalinna tuloksista saa julkaista Internetissä");
        lupa.addOption("lupa4", "lupa4", "Valintaani koskevat tiedot saa lähettää minulle sähköisesti");
        lupa.addOption("lupa5", "lupa5", "Minulle saa lähettää tietoa opiskelijavalinnan etenemisestä ja tuloksista tekstiviestillä");

        Radio asiointikieli = new Radio("asiointikieli", "Asiointikieli, jolla haluat vastaanottaa opiskelijavalintaan liittyviä tietoja");
        asiointikieli.addOption("suomi", "suomi", "suomi");
        asiointikieli.addOption("ruotsi", "ruotsi", "ruotsi");
        asiointikieli.addAttribute("required", "required");

        lupatiedot.addChild(lupa);
        lupatiedot.addChild(asiointikieli);
    }

    private void createKoulutustausta(Teema koulutustaustaRyhmä) {
        koulutustaustaRyhmä.setHelp("Merkitse tälle sivulle pohjakoulutuksesi. Valitse pohjakoulutus, jonka perusteella haet. Voit merkitä vain yhden kohdan. HUOM! Jos olet suorittanut lukion oppimäärän tai ylioppilastutkinnon, et voi valita kohtaa Perusopetuksen oppimäärä. Lukion oppimäärän tai ylioppilastutkinnon suorittaneet eivät voi hakea perusopetuksen päättötodistuksella. Ammatillisella perustutkintotodistuksella et voi hakea ammatillisen koulutuksen ja lukiokoulutuksen yhteishaussa. Oppilaitokset tarkistavat todistukset hyväksytyiksi tulleilta hakijoilta.");
        Radio millatutkinnolla = new Radio("millatutkinnolla", "valitse tutkinto, jolla haet koulutukseen.");
        millatutkinnolla.addOption("tutkinto1", "tutkinto1", "Perusopetuksen oppimäärä", "Valitse tämä, jos olet käynyt peruskoulun.");
        millatutkinnolla.addOption("tutkinto2", "tutkinto2", "Perusopetuksen erityisopetuksen osittain yksilöllistetty oppimäärä", "Valitse tämä, jos olet opiskellut yksilöllistetyn oppimäärän puolessa tai alle puolessa oppiaineista.");
        millatutkinnolla.addOption("tutkinto3", "tutkinto3", "Perusopetuksen erityisopetuksen yksilöllistetty oppimäärä, opetus järjestetty toiminta-alueittain", "Valitse tämä, jos olet osallistunut harjaantumisopetukseen.");
        millatutkinnolla.addOption("tutkinto4", "tutkinto4", "Perusopetuksen pääosin tai kokonaan yksilöllistetty oppimäärä", "Valitse tämä, jos olet opiskellut peruskoulun kokonaan yksilöllistetyn oppimäärän mukaan tai olet opiskellut yli puolet opinnoistasi yksilöllistetyn opetuksen mukaan.");
        millatutkinnolla.addOption("tutkinto5", "tutkinto5", "Oppivelvollisuuden suorittaminen keskeytynyt (ei päättötodistusta)", "Valitse tämä vain, jos sinulla ei ole lainkaan päättötodistusta.");
        millatutkinnolla.addOption("tutkinto6", "tutkinto6", "Lukion päättötodistus, ylioppilastutkinto tai abiturientti", "Valitse tämä, jos olet suorittanut lukion ja sinulla on suomalainen tai kansainvälinen ylioppilastutkinto, tai olet suorittanut yhdistelmätutkinnon, johon sisältyy lukion vahimmäisoppimäärää vastaavat opinnot.");
        millatutkinnolla.addOption("tutkinto7", "tutkinto7", "Ulkomailla suoritettu koulutus", "Valitse tämä, jos olet suorittanut tutkintosi ulkomailla.");
        millatutkinnolla.getOptions().get(0).addAttribute("onChange", "submit()");
        millatutkinnolla.getOptions().get(1).addAttribute("onChange", "submit()");
        millatutkinnolla.getOptions().get(2).addAttribute("onChange", "submit()");
        millatutkinnolla.getOptions().get(3).addAttribute("onChange", "submit()");
        millatutkinnolla.getOptions().get(4).addAttribute("onChange", "submit()");
        millatutkinnolla.getOptions().get(5).addAttribute("onChange", "submit()");
        millatutkinnolla.getOptions().get(6).addAttribute("onChange", "submit()");

        Radio peruskoulu2012 = new Radio("peruskoulu2012", "Saatko peruskoulun päättötodistuksen hakukeväänä 2012?");
        peruskoulu2012.addOption("kylla", "Kyllä", "Kyllä");
        peruskoulu2012.addOption("ei", "Ei", "en, olen saanut päättötodistuksen jo aiemmin, vuonna");
        peruskoulu2012.addAttribute("required", "required");

        DropdownSelect tutkinnonOpetuskieli = new DropdownSelect("opetuskieli", "Mikä oli tukintosi opetuskieli");
        tutkinnonOpetuskieli.addOption("suomi", "Suomi", "Suomi");
        tutkinnonOpetuskieli.addOption("ruotsi", "Ruotsi", "Ruotsi");
        tutkinnonOpetuskieli.addAttribute("placeholder", "Tutkintosi opetuskieli");
        tutkinnonOpetuskieli.setHelp("Merkitse tähän se kieli, jolla suoritit suurimman osan opinnoistasi. Jos suoritit opinnot kahdella kielellä tasapuolisesti, valitse toinen niistä");

        CheckBox suorittanut = new CheckBox("suorittanut", "Merkitse tähän, jos olet suorittanut jonkun seuraavista");
        suorittanut.addOption("suorittanut1", "suorittanut1", "Perusopetuksen lisäopetuksen oppimäärä (kymppiluokka)");
        suorittanut.addOption("suorittanut2", "suorittanut2", "Vammaisten valmentava ja kuntouttava opetus ja ohjaus");
        suorittanut.addOption("suorittanut3", "suorittanut3", "Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus");
        suorittanut.addOption("suorittanut4", "suorittanut4", "Muuna kuin ammatillisena peruskoulutuksena järjestettävä kotitalousopetus (talouskoulu)");
        suorittanut.addOption("suorittanut5", "suorittanut5", "Ammatilliseen peruskoulutukseen ohjaava ja valmistava koulutus (ammattistartti)");

        Radio osallistunut = new Radio("osallistunut", "Oletko osallistunut viimeisen vuoden aikana jonkun hakukohteen alan pääsykokeisiin?");
        osallistunut.addOption("ei", "Ei", "En");
        osallistunut.addOption("kylla", "Kyllä", "Kyllä");
        osallistunut.addAttribute("required", "required");

        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule(millatutkinnolla.getId(), "(" +
                millatutkinnolla.getOptions().get(0).getValue() + "|" + millatutkinnolla.getOptions().get(1).getValue() +
                "|" + millatutkinnolla.getOptions().get(2).getValue() + "|" + millatutkinnolla.getOptions().get(3).getValue() + ")");
        relatedQuestionRule.addChild(peruskoulu2012);
        relatedQuestionRule.addChild(tutkinnonOpetuskieli);
        relatedQuestionRule.addChild(suorittanut);
        millatutkinnolla.addChild(relatedQuestionRule);

        koulutustaustaRyhmä.addChild(millatutkinnolla);
        koulutustaustaRyhmä.addChild(osallistunut);
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

    private Element createRequiredTextQuestion(final String id, final String name) {
        TextQuestion textQuestion = new TextQuestion(id, name);
        textQuestion.addAttribute("required", "required");
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
    public Vaihe getFirstCategory(String applicationPeriodId, String formId) {
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
    public List<Validator> getCategoryValidators(HakemusId hakemusId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Form getForm(String applicationPeriodId, String formId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
