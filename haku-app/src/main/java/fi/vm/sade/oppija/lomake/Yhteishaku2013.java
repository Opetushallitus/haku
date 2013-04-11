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
import static fi.vm.sade.oppija.util.OppijaConstants.*;

@Service
public class Yhteishaku2013 {

    public static final String ASID = "1.2.246.562.5.50476818906";
    public static final String TUTKINTO7_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO5_NOTIFICATION_ID = "tutkinto5-notification";

    private final ApplicationPeriod applicationPeriod;
    public static String mobilePhonePattern =
            "^$|^(?!\\+358|0)[\\+]?[0-9\\-\\s]+$|^(\\+358|0)[\\-\\s]*((4[\\-\\s]*[0-6])|50)[0-9\\-\\s]*$";

    private final KoodistoService koodistoService;

    @Autowired // NOSONAR
    public Yhteishaku2013(final KoodistoService koodistoService) { // NOSONAR
        this.koodistoService = koodistoService;
        this.applicationPeriod = new ApplicationPeriod(ASID);
    }


    public void init() { // NOSONAR
        try {
            Form form = new Form("yhteishaku", createI18NText("form.title"));

            applicationPeriod.addForm(form);

            // Henkilötiedot
            Phase henkilotiedot = new Phase("henkilotiedot", createI18NText("form.henkilotiedot.otsikko"), false);
            form.addChild(henkilotiedot);
            Theme henkilotiedotRyhma = createHenkilotiedotRyhma();
            henkilotiedot.addChild(henkilotiedotRyhma);

            // Koulutustausta
            Phase koulutustausta = new Phase("koulutustausta", createI18NText("form.koulutustausta.otsikko"), false);
            form.addChild(koulutustausta);
            Theme koulutustaustaRyhma = new Theme("KoulutustaustaGrp", createI18NText("form.koulutustausta.otsikko"), null);
            koulutustausta.addChild(koulutustaustaRyhma);
            createKoulutustausta(koulutustaustaRyhma);

            // Hakutoiveet
            Phase hakutoiveet = new Phase("hakutoiveet", createI18NText("form.hakutoiveet.otsikko"), false);
            form.addChild(hakutoiveet);
            Theme hakutoiveetRyhma = createHakutoiveetRyhma();
            hakutoiveet.addChild(hakutoiveetRyhma);
            createHakutoiveet(hakutoiveetRyhma);

            // Arvosanat
            Phase arvosanat = new Phase("arvosanat", createI18NText("form.arvosanat.otsikko"), false);
            form.addChild(arvosanat);
            Theme arvosanatRyhma = createArvosanatRyhma();
            arvosanat.addChild(arvosanatRyhma);
            createArvosanat(arvosanatRyhma);

            // Lisätiedot
            Phase lisatiedot = new Phase("lisatiedot", createI18NText("form.lisatiedot.otsikko"), false);
            WorkExperienceTheme tyokokemusRyhma = new WorkExperienceTheme("tyokokemusGrp",
                    createI18NText("form.lisatiedot.tyokokemus"), null, "32");
            Theme lupatiedotRyhma = new Theme("lupatiedotGrp", createI18NText("form.lisatiedot.lupatiedot"), null);
            form.addChild(lisatiedot);
            lisatiedot.addChild(tyokokemusRyhma);
            lisatiedot.addChild(lupatiedotRyhma);
            createTyokokemus(tyokokemusRyhma);
            createLupatiedot(lupatiedotRyhma);

            // Esikatselu
            Phase esikatselu = new Phase("esikatselu", createI18NText("form.esikatselu.otsikko"), true);
            form.addChild(esikatselu);
            Theme yhteenvetoRyhma = new Theme("yhteenvetoGrp", createI18NText("form.esikatselu.yhteenveto"), null);
            esikatselu.addChild(henkilotiedotRyhma).addChild(koulutustaustaRyhma).addChild(hakutoiveetRyhma)
                    .addChild(arvosanatRyhma).addChild(tyokokemusRyhma).addChild(lupatiedotRyhma);
            yhteenvetoRyhma.setHelp(createI18NText("form.esikatselu.help"));
        } catch (Throwable t) {
            throw new RuntimeException(Yhteishaku2013.class.getCanonicalName() + " init failed");
        }

    }

    private Theme createHenkilotiedotRyhma() {
        Theme henkilotiedotRyhma = new Theme("HenkilotiedotGrp", createI18NText("form.henkilotiedot.otsikko"), null);

        // Nimet
        Question sukunimi = createRequiredTextQuestion("Sukunimi", "form.henkilotiedot.sukunimi", "30");
        sukunimi.setInline(true);
        sukunimi.addAttribute("iso8859name", "iso8859name");
        henkilotiedotRyhma.addChild(sukunimi);

        Question etunimet = createRequiredTextQuestion("Etunimet", "form.henkilotiedot.etunimet", "30");
        etunimet.setInline(true);
        etunimet.addAttribute("iso8859name", "iso8859name");
        henkilotiedotRyhma.addChild(etunimet);

        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", createI18NText("form.henkilotiedot.kutsumanimi"));
        kutsumanimi.setHelp(createI18NText("form.henkilotiedot.kutsumanimi.help"));
        kutsumanimi.addAttribute("required", "required");
        kutsumanimi.addAttribute("size", "20");
        kutsumanimi.addAttribute("containedInOther", "Etunimet");
        kutsumanimi.setVerboseHelp(getVerboseHelp());
        kutsumanimi.setInline(true);
        henkilotiedotRyhma.addChild(kutsumanimi);

        // Kansalaisuus, hetu ja sukupuoli suomalaisille
        DropdownSelect kansalaisuus = new DropdownSelect("kansalaisuus", createI18NText("form.henkilotiedot.kansalaisuus"));
        kansalaisuus.addOptions(koodistoService.getNationalities());
        setDefaultOption("FI", kansalaisuus.getOptions());
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.addAttribute("required", "required");
        kansalaisuus.setHelp(createI18NText("form.henkilotiedot.kansalaisuus.help"));
        kansalaisuus.setVerboseHelp(getVerboseHelp());
        kansalaisuus.setInline(true);
        henkilotiedotRyhma.addChild(kansalaisuus);

        TextQuestion henkilotunnus = new TextQuestion("Henkilotunnus", createI18NText("form.henkilotiedot.henkilotunnus"));
        henkilotunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilotunnus.addAttribute("required", "required");
        henkilotunnus.addAttribute("pattern", "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$");
        henkilotunnus.addAttribute("size", "11");
        henkilotunnus.addAttribute("maxlength", "11");
        henkilotunnus.setVerboseHelp(getVerboseHelp());
        henkilotunnus.setInline(true);

        Radio sukupuoli = new Radio("Sukupuoli", createI18NText("form.henkilotiedot.sukupuoli"));
        sukupuoli.addOption("mies", createI18NText("form.henkilotiedot.sukupuoli.mies"), "m");
        sukupuoli.addOption("nainen", createI18NText("form.henkilotiedot.sukupuoli.nainen"), "n");
        sukupuoli.addAttribute("required", "required");
        sukupuoli.setVerboseHelp(getVerboseHelp());
        sukupuoli.setInline(true);

        SocialSecurityNumber socialSecurityNumber = new SocialSecurityNumber("ssn_question", createI18NText("form.henkilotiedot.hetu"),
                sukupuoli.getI18nText(), sukupuoli.getOptions().get(0),
                sukupuoli.getOptions().get(1), sukupuoli.getId(), henkilotunnus);

        RelatedQuestionRule hetuRule = new RelatedQuestionRule("hetuRule", kansalaisuus.getId(), "^$|^FI$");
        hetuRule.addChild(socialSecurityNumber);
        henkilotiedotRyhma.addChild(hetuRule);

        // Ulkomaalaisten tunnisteet
        Radio onkoSinullaSuomalainenHetu = new Radio("onkoSinullaSuomalainenHetu",
                createI18NText("form.henkilotiedot.hetu.onkoSuomalainen"));
        onkoSinullaSuomalainenHetu.addOption("true", createI18NText("form.yleinen.kylla"), "true");
        onkoSinullaSuomalainenHetu.addOption("false", createI18NText("form.yleinen.ei"), "false");
        onkoSinullaSuomalainenHetu.addAttribute("required", "required");
        onkoSinullaSuomalainenHetu.setVerboseHelp(getVerboseHelp());
        onkoSinullaSuomalainenHetu.setInline(true);
        RelatedQuestionRule suomalainenHetuRule = new RelatedQuestionRule("suomalainenHetuRule",
                onkoSinullaSuomalainenHetu.getId(), "^true");
        suomalainenHetuRule.addChild(socialSecurityNumber);
        onkoSinullaSuomalainenHetu.addChild(suomalainenHetuRule);

        RelatedQuestionRule eiSuomalaistaHetuaRule = new RelatedQuestionRule("eiSuomalaistaHetuaRule",
                onkoSinullaSuomalainenHetu.getId(), "^false");
        eiSuomalaistaHetuaRule.addChild(sukupuoli);

        DateQuestion syntymaaika = new DateQuestion("syntymaaika", createI18NText("form.henkilotiedot.syntymaaika"));
        syntymaaika.addAttribute("required", "required");
        syntymaaika.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymaaika);

        TextQuestion syntymapaikka = new TextQuestion("syntymapaikka", createI18NText("form.henkilotiedot.syntymapaikka"));
        syntymapaikka.addAttribute("size", "30");
        syntymapaikka.addAttribute("required", "required");
        syntymapaikka.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymapaikka);

        TextQuestion kansallinenIdTunnus = new TextQuestion("kansallinenIdTunnus", createI18NText("form.henkilotiedot.kansallinenId"));
        kansallinenIdTunnus.addAttribute("size", "30");
        kansallinenIdTunnus.addAttribute("required", "required");
        kansallinenIdTunnus.setInline(true);
        eiSuomalaistaHetuaRule.addChild(kansallinenIdTunnus);

        TextQuestion passinnumero = new TextQuestion("passinnumero", createI18NText("form.henkilotiedot.passinnumero"));
        passinnumero.addAttribute("size", "30");
        passinnumero.addAttribute("required", "required");
        passinnumero.setInline(true);
        eiSuomalaistaHetuaRule.addChild(passinnumero);

        onkoSinullaSuomalainenHetu.addChild(eiSuomalaistaHetuaRule);

        RelatedQuestionRule ulkomaalaisenTunnisteetRule = new RelatedQuestionRule("ulkomaalaisenTunnisteetRule",
                kansalaisuus.getId(), "(?!FI)([A-Z]{2})");
        ulkomaalaisenTunnisteetRule.addChild(onkoSinullaSuomalainenHetu);
        henkilotiedotRyhma.addChild(ulkomaalaisenTunnisteetRule);

        // Email
        TextQuestion email = new TextQuestion("Sähköposti", createI18NText("form.henkilotiedot.email"));
        email.addAttribute("size", "50");
        email.addAttribute("pattern", "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^$");
        email.setHelp(createI18NText("form.henkilotiedot.email.help"));
        email.setVerboseHelp(getVerboseHelp());
        email.setInline(true);
        henkilotiedotRyhma.addChild(email);

        // Matkapuhelinnumerot
        TextQuestion matkapuhelinnumero = new TextQuestion("matkapuhelinnumero", createI18NText("form.henkilotiedot.matkapuhelinnumero"));
        matkapuhelinnumero.setHelp(createI18NText("form.henkilotiedot.matkapuhelinnumero.help"));
        matkapuhelinnumero.addAttribute("size", "30");
        matkapuhelinnumero.addAttribute("pattern", mobilePhonePattern);
        matkapuhelinnumero.setVerboseHelp(getVerboseHelp());
        matkapuhelinnumero.setInline(true);
        henkilotiedotRyhma.addChild(matkapuhelinnumero);

        TextQuestion huoltajanPuhelinnumero = new TextQuestion("huoltajanPuhelinnumero",
                createI18NText("form.henkilotiedot.matkapuhelinnumero.huoltaja"));
        huoltajanPuhelinnumero.setHelp(createI18NText("form.henkilotiedot.matkapuhelinnumero.huoltaja.help"));
        huoltajanPuhelinnumero.addAttribute("size", "20");
        huoltajanPuhelinnumero.setVerboseHelp(getVerboseHelp());
        huoltajanPuhelinnumero.setInline(true);

        AddElementRule addHuoltajanPuhelinnumero = new AddElementRule("addHuoltajanPuhelinnumeroRule",
                huoltajanPuhelinnumero.getId(), createI18NText("form.henkilotiedot.matkapuhelinnumero.huoltaja.lisaa"));
        addHuoltajanPuhelinnumero.addChild(huoltajanPuhelinnumero);
        henkilotiedotRyhma.addChild(addHuoltajanPuhelinnumero);

        // Asuinmaa, osoite
        DropdownSelect asuinmaa = new DropdownSelect("asuinmaa", createI18NText("form.henkilotiedot.asuinmaa"));
        asuinmaa.addOption("eiValittu", ElementUtil.createI18NText("form.yleinen.null"), "");
        asuinmaa.addOptions(koodistoService.getCountries());

        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        asuinmaa.addAttribute("required", "required");
        asuinmaa.setVerboseHelp(getVerboseHelp());
        asuinmaa.setInline(true);

        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule1", asuinmaa.getId(), "FI");
        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "form.henkilotiedot.lahiosoite", "40");
        lahiosoite.setInline(true);
        relatedQuestionRule.addChild(lahiosoite);

        Element postinumero = new PostalCode("Postinumero", createI18NText("form.henkilotiedot.postinumero"), createPostOffices());
        postinumero.addAttribute("size", "5");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("placeholder", "#####");
        postinumero.addAttribute("maxlength", "5");
        postinumero.setHelp(createI18NText("form.henkilotiedot.postinumero.help"));
        relatedQuestionRule.addChild(postinumero);

        DropdownSelect kotikunta = new DropdownSelect("kotikunta", createI18NText("form.henkilotiedot.kotikunta"));
        kotikunta.addOptions(koodistoService.getMunicipalities());
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        kotikunta.addAttribute("required", "required");
        kotikunta.setVerboseHelp(getVerboseHelp());
        kotikunta.setInline(true);
        kotikunta.setHelp(createI18NText("form.henkilotiedot.kotikunta.help"));
        relatedQuestionRule.addChild(kotikunta);

        CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1",
                createI18NText("form.henkilotiedot.ensisijainenOsoite"));
        ensisijainenOsoite.setInline(true);
        relatedQuestionRule.addChild(ensisijainenOsoite);

        TextArea osoite = new TextArea("osoite", createI18NText("form.henkilotiedot.osoite"));
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

        asuinmaa.addChild(relatedQuestionRule);

        henkilotiedotRyhma.addChild(asuinmaa);

        // Äidinkieli
        DropdownSelect aidinkieli = new DropdownSelect("äidinkieli", createI18NText("form.henkilotiedot.aidinkieli"));
        aidinkieli.addOptions(koodistoService.getLanguages());
        aidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        aidinkieli.addAttribute("required", "required");
        aidinkieli.setVerboseHelp(getVerboseHelp());
        aidinkieli.setInline(true);
        aidinkieli.setHelp(createI18NText("form.henkilotiedot.aidinkieli.help"));
        henkilotiedotRyhma.addChild(aidinkieli);

        return henkilotiedotRyhma;
    }

    private Theme createArvosanatRyhma() {
        Map<String, List<Question>> oppiaineMap = new HashMap<String, List<Question>>();

        List<Question> oppiaineList = new ArrayList<Question>();
        oppiaineList.add(new SubjectRow("tietotekniikka", createI18NText("form.oppiaineet.tietotekniikka")));
        oppiaineList.add(new SubjectRow("kansantaloustiede", createI18NText("form.oppiaineet.kansantaloustiede")));
        oppiaineMap.put("1.2.246.562.14.79893512065", oppiaineList);

        return new Theme("arvosanatGrp", createI18NText("form.arvosanat.otsikko"), oppiaineMap);
    }

    private Theme createHakutoiveetRyhma() {
        final String id = "1.2.246.562.14.79893512065";
        final String elementIdPrefix = id.replace('.', '_');
        Radio radio = new Radio(
                elementIdPrefix + "_additional_question_1",
                createI18NText("form.sora.terveys"));
        radio.addOption(id + "_q1_option_1", createI18NText("form.yleinen.ei"), "q1_option_1");
        radio.addOption(id + "_q1_option_2", createI18NText("form.sora.kylla"),
                "q1_option_2");

        Radio radio2 = new Radio(
                elementIdPrefix + "_additional_question_2",
                createI18NText("form.sora.oikeudenMenetys"));
        radio2.addOption(id + "_q2_option_1", createI18NText("form.yleinen.ei"), "q2_option_1");
        radio2.addOption(id + "_q2_option_2",
                createI18NText("form.sora.kylla"), "q2_option_2");

        Radio radio3 = new Radio(
                elementIdPrefix + "_additional_question_3",
                createI18NText("form.hakutoiveet.paasykoe.tuloksiaSaaKayttaa"));
        radio3.addOption(elementIdPrefix + "_q3_option_1",
                createI18NText("form.hakutoiveet.paasykoe.eiOsallistunut"), "q3_option_1");
        radio3.addOption(elementIdPrefix + "_q3_option_2",
                createI18NText("form.hakutoiveet.paasykoe.eiSaaKayttaa"), "q3_option_2");
        radio3.addOption(elementIdPrefix + "_q3_option_3",
                createI18NText("form.hakutoiveet.paasykoe.saaKayttaa"), "q3_option_3");

        List<Question> lisakysymysList = new ArrayList<Question>();
        lisakysymysList.add(radio);
        lisakysymysList.add(radio2);
        lisakysymysList.add(radio3);

        Map<String, List<Question>> lisakysymysMap = new HashMap<String, List<Question>>();
        lisakysymysMap.put(id, lisakysymysList);

        return new Theme("hakutoiveetGrp", createI18NText("form.hakutoiveet.otsikko"), lisakysymysMap);
    }

    public GradeGrid createGradeGrid(final String id, boolean primary) {

        List<Option> gradeRange = koodistoService.getGradeRanges();
        List<SubjectRow> subjects = koodistoService.getSubjects();
        SubjectRow finnish = new SubjectRow("subject_finnish", createI18NText("form.arvosanat.aidinkieli"));
        List<SubjectRow> subjectRowsAfter = new ArrayList<SubjectRow>();
        for (SubjectRow subject : subjects) {
            String subjectId = subject.getId();
            if (subjectId.endsWith("AI")) {
                finnish = subject;
            } else if (!(subjectId.endsWith("A1") || subjectId.endsWith("A12") || subjectId.endsWith("A13")
                    || subjectId.endsWith("A2") || subjectId.endsWith("A22") || subjectId.endsWith("23")
                    || subjectId.endsWith("B1") || subjectId.endsWith("B12") || subjectId.endsWith("B13")
                    || subjectId.endsWith("B2") || subjectId.endsWith("B22") || subjectId.endsWith("B23")
                    || subjectId.endsWith("B3") || subjectId.endsWith("B32") || subjectId.endsWith("B33"))) {
                subjectRowsAfter.add(subject);
            }
        }
        List<SubjectRow> subjectRowsBefore = new ArrayList<SubjectRow>();
        subjectRowsBefore.add(finnish);

        LanguageRow a1 = new LanguageRow("lang_a1", createI18NText("form.arvosanat.a1kieli"));
        LanguageRow b1 = new LanguageRow("lang_b1", createI18NText("form.arvosanat.b1kieli"));

        List<LanguageRow> languageRows = new ArrayList<LanguageRow>();
        languageRows.add(a1);
        languageRows.add(b1);

        List<Option> languageOptions = new ArrayList<Option>();
        languageOptions.add(new Option("langoption_" + "eng", createI18NText("form.arvosanat.englanti"), "eng"));
        languageOptions.add(new Option("langoption_" + "swe", createI18NText("form.arvosanat.ruotsi"), "swe"));
        languageOptions.add(new Option("langoption_" + "fra", createI18NText("form.arvosanat.ranska"), "fra"));
        languageOptions.add(new Option("langoption_" + "ger", createI18NText("form.arvosanat.saksa"), "ger"));
        languageOptions.add(new Option("langoption_" + "rus", createI18NText("form.arvosanat.venaja"), "rus"));
        languageOptions.add(new Option("langoption_" + "fin", createI18NText("form.arvosanat.suomi"), "fin"));

        List<Option> scopeOptions = new ArrayList<Option>();
        scopeOptions.add(new Option("scopeoption_" + "a1", createI18NText("form.arvosanat.a1"), "a1"));
        scopeOptions.add(new Option("scopeoption_" + "a2", createI18NText("form.arvosanat.a2"), "a2"));
        scopeOptions.add(new Option("scopeoption_" + "b1", createI18NText("form.arvosanat.b1"), "b1"));
        scopeOptions.add(new Option("scopeoption_" + "b2", createI18NText("form.arvosanat.b2"), "b2"));
        scopeOptions.add(new Option("scopeoption_" + "b3", createI18NText("form.arvosanat.b3"), "b3"));

        GradeGrid gradeGrid = new GradeGrid(id, createI18NText("form.arvosanat.otsikko"), "Kieli", subjectRowsBefore,
                languageRows, subjectRowsAfter, scopeOptions, languageOptions, gradeRange, primary);
        gradeGrid.setVerboseHelp(getVerboseHelp());

        return gradeGrid;
    }

    private void createArvosanat(Theme arvosanatRyhma) {
        RelatedQuestionRule relatedQuestionPK = new RelatedQuestionRule("rule_grade_pk", "millatutkinnolla",
                "(" + PERUSKOULU + "|tutkinto2|tutkinto3|tutkinto4)");
        relatedQuestionPK.addChild(createGradeGrid("grid_pk", true));
        arvosanatRyhma.addChild(relatedQuestionPK);

        RelatedQuestionRule relatedQuestionLukio = new RelatedQuestionRule("rule_grade_yo", "millatutkinnolla",
                "(" + YLIOPPILAS + ")");
        relatedQuestionLukio.addChild(createGradeGrid("grid_yo", false));
        arvosanatRyhma.addChild(relatedQuestionLukio);

        RelatedQuestionRule relatedQuestionEiTutkintoa = new RelatedQuestionRule("rule_grade_no", "millatutkinnolla",
                "(tutkinto5|tutkinto7)");
        relatedQuestionEiTutkintoa.addChild(new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta")));
        arvosanatRyhma.addChild(relatedQuestionEiTutkintoa);

        arvosanatRyhma
                .setHelp(createI18NText("form.arvosanat.help"));

    }

    private void createHakutoiveet(Theme hakutoiveetRyhma) {
        hakutoiveetRyhma
                .setHelp(createI18NText("form.hakutoiveet.help"));
        PreferenceTable preferenceTable = new PreferenceTable("preferencelist", createI18NText("form.hakutoiveet.otsikko"), "Ylös",
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
                .setHelp(createI18NText("form.tyokokemus.help"));
        TextQuestion tyokokemuskuukaudet = new TextQuestion("tyokokemuskuukaudet",
                createI18NText("form.tyokokemus.kuukausina"));
        tyokokemuskuukaudet
                .setHelp(createI18NText("form.tyokokemus.kuukausina.help"));
        tyokokemuskuukaudet.addAttribute("placeholder", "kuukautta");
        tyokokemuskuukaudet.addAttribute("pattern", "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$");
        tyokokemuskuukaudet.addAttribute("size", "8");
        tyokokemuskuukaudet.setVerboseHelp(getVerboseHelp());
        tyokokemus.addChild(tyokokemuskuukaudet);
    }

    private void createLupatiedot(Theme lupatiedot) {
        TextQuestion email = new TextQuestion("lupa1_email", createI18NText("form.lupatiedot.email"));
        email.addAttribute("size", "40");
        email.addAttribute("required", "required");
        email.setHelp(createI18NText("form.lupatiedot.email.help"));
        email.setVerboseHelp(getVerboseHelp());
        email.setInline(true);

        CheckBox permission1 = new CheckBox(
                "lupa1",
                createI18NText("form.lupatiedot.vanhemmille"));
        CheckBox permission2 = new CheckBox(
                "lupa2",
                createI18NText("form.lupatiedot.saaMarkkinoida"));
        CheckBox permission3 = new CheckBox("lupa3",
                createI18NText("form.lupatiedot.saaJulkaista"));
        CheckBox permission4 = new CheckBox("lupa4",
                createI18NText("form.lupatiedot.saaLahettaaSahkoisesti"));
        CheckBox permission5 = new CheckBox(
                "lupa5",
                createI18NText("form.lupatiedot.saaLahettaaTekstiviesteja"));
        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("lupa1_rule", permission1.getId(), "on");
        relatedQuestionRule.addChild(email);
        permission1.addChild(relatedQuestionRule);

        Group group = new Group("permissionCheckboxes", createI18NText("form.lupatiedot.otsikko"));

        group.addChild(permission1);
        group.addChild(permission2);
        group.addChild(permission3);
        group.addChild(permission4);
        group.addChild(permission5);
        lupatiedot.addChild(group);
        lupatiedot.setVerboseHelp(getVerboseHelp());

        Radio asiointikieli = new Radio("asiointikieli", createI18NText("form.asiointikieli.otsikko"));
        asiointikieli.setHelp(createI18NText("form.asiointikieli.help"));
        asiointikieli.addOption("suomi", createI18NText("Suomi"), "suomi");
        asiointikieli.addOption("ruotsi", createI18NText("Ruotsi"), "ruotsi");
        asiointikieli.addAttribute("required", "required");
        asiointikieli.setVerboseHelp(getVerboseHelp());
        lupatiedot.addChild(asiointikieli);
    }

    private void createKoulutustausta(Theme koulutustaustaRyhma) {
        koulutustaustaRyhma
                .setHelp(createI18NText("form.koulutustausta.help"));

        Radio osallistunut = new Radio("osallistunut",
                createI18NText("form.koulutustausta.osallistunutPaasykokeisiin"));
        osallistunut.addOption("ei", createI18NText("form.yleinen.en"), "false");
        osallistunut.addOption("kylla", createI18NText("form.yleinen.kylla"), "true");
        osallistunut.addAttribute("required", "required");
        osallistunut.setVerboseHelp(getVerboseHelp());

        koulutustaustaRyhma.addChild(createKoulutustaustaRadio());
        koulutustaustaRyhma.addChild(osallistunut);
    }

    public Radio createKoulutustaustaRadio() { //NOSONAR
        Radio millatutkinnolla = new Radio("millatutkinnolla",
                createI18NText("form.koulutustausta.millaTutkinnolla"));
        millatutkinnolla.addOption("tutkinto1", createI18NText("form.koulutustausta.peruskoulu"), PERUSKOULU,
                createI18NText("form.koulutustausta.peruskoulu.help"));
        millatutkinnolla
                .addOption("tutkinto2",
                        createI18NText("form.koulutustausta.osittainYksilollistetty"),
                        OSITTAIN_YKSILOLLISTETTY,
                        createI18NText("form.koulutustausta.osittainYksilollistetty.help"));
        millatutkinnolla
                .addOption(
                        "tutkinto3",
                        createI18NText("form.koulutustausta.erityisopetuksenYksilollistetty"),
                        ERITYISOPETUKSEN_YKSILOLLISTETTY,
                        createI18NText("form.koulutustausta.erityisopetuksenYksilollistetty.help"));
        millatutkinnolla
                .addOption(
                        "tutkinto4",
                        createI18NText("form.koulutustausta.yksilollistetty"),
                        YKSILOLLISTETTY,
                        createI18NText("form.koulutustausta.yksilollistetty.help"));
        millatutkinnolla.addOption("tutkinto5",
                createI18NText("form.koulutustausta.keskeytynyt"),
                KESKEYTYNYT,
                createI18NText("form.koulutustausta.keskeytynyt"));
        millatutkinnolla
                .addOption(
                        "tutkinto6",
                        createI18NText("form.koulutustausta.lukio"),
                        YLIOPPILAS,
                        createI18NText("form.koulutustausta.lukio.help"));
        millatutkinnolla.addOption("tutkinto7", createI18NText("form.koulutustausta.ulkomailla"), ULKOMAINEN_TUTKINTO,
                createI18NText("form.koulutustausta.ulkomailla.help"));
        millatutkinnolla.setVerboseHelp(getVerboseHelp());
        millatutkinnolla.addAttribute("required", "required");

        Notification tutkinto7Notification = new Notification(TUTKINTO7_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.ulkomailla.huom"),
                Notification.NotificationType.INFO);

        Notification tutkinto5Notification = new Notification(TUTKINTO5_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.keskeytynyt.huom"),
                Notification.NotificationType.INFO);


        RelatedQuestionRule keskeytynytRule = new RelatedQuestionRule("tutkinto5-rule",
                millatutkinnolla.getId(), KESKEYTYNYT);

        RelatedQuestionRule ulkomaillaSuoritettuTutkintoRule = new RelatedQuestionRule("tutkinto7-rule",
                millatutkinnolla.getId(), ULKOMAINEN_TUTKINTO);

        ulkomaillaSuoritettuTutkintoRule.addChild(tutkinto7Notification);
        keskeytynytRule.addChild(tutkinto5Notification);
        millatutkinnolla.addChild(ulkomaillaSuoritettuTutkintoRule);
        millatutkinnolla.addChild(keskeytynytRule);

        TextQuestion paattotodistusvuosiPeruskoulu = new TextQuestion("paattotodistusvuosi_peruskoulu",
                createI18NText("form.koulutustausta.paattotodistusvuosi"));
        paattotodistusvuosiPeruskoulu.addAttribute("placeholder", "vvvv");
        paattotodistusvuosiPeruskoulu.addAttribute("required", "required");
        paattotodistusvuosiPeruskoulu.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        paattotodistusvuosiPeruskoulu.addAttribute("size", "4");
        paattotodistusvuosiPeruskoulu.addAttribute("maxlength", "4");

        CheckBox suorittanut1 = new CheckBox("suorittanut1",
                createI18NText("form.koulutustausta.kymppiluokka"));
        CheckBox suorittanut2 = new CheckBox("suorittanut2",
                createI18NText("form.koulutustausta.vammaistenValmentava"));
        CheckBox suorittanut3 = new CheckBox("suorittanut3",
                createI18NText("form.koulutustausta.maahanmuuttajienValmistava"));
        CheckBox suorittanut4 = new CheckBox(
                "suorittanut4",
                createI18NText("form.koulutustausta.talouskoulu"));
        CheckBox suorittanut5 = new CheckBox(
                "suorittanut5",
                createI18NText("form.koulutustausta.ammattistartti"));
        CheckBox suorittanut6 = new CheckBox("suorittanut6",
                createI18NText("form.koulutustausta.kansanopisto"));

        Group suorittanutGroup = new Group("suorittanutgroup",
                createI18NText("form.koulutustausta.suorittanut"));
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

        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule3", millatutkinnolla.getId(), "("
                + PERUSKOULU + "|"
                + OSITTAIN_YKSILOLLISTETTY + "|"
                + ERITYISOPETUKSEN_YKSILOLLISTETTY + "|"
                + YKSILOLLISTETTY + ")");

        RelatedQuestionRule paattotodistusvuosiPeruskouluRule = new RelatedQuestionRule("rule8",
                paattotodistusvuosiPeruskoulu.getId(), "^(19[0-9][0-9]|200[0-9]|201[0-1])$");

        relatedQuestionRule.addChild(paattotodistusvuosiPeruskoulu);
        // relatedQuestionRule.addChild(tutkinnonOpetuskieli);
        relatedQuestionRule.addChild(suorittanutGroup);
        relatedQuestionRule.addChild(paattotodistusvuosiPeruskouluRule);

        TextQuestion lukioPaattotodistusVuosi = new TextQuestion("lukioPaattotodistusVuosi",
                createI18NText("form.koulutustausta.paattotodistusvuosi"));
        lukioPaattotodistusVuosi.addAttribute("placeholder", "vvvv");
        lukioPaattotodistusVuosi.addAttribute("required", "required");
        lukioPaattotodistusVuosi.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        lukioPaattotodistusVuosi.addAttribute("size", "4");
        lukioPaattotodistusVuosi.addAttribute("maxlength", "4");
        lukioPaattotodistusVuosi.setInline(true);

        TextQuestion ylioppilastodistuksenVuosi = new TextQuestion("ylioppilastodistuksenVuosi",
                createI18NText("form.koulutustausta.lukio.yotodistusvuosi"));
        ylioppilastodistuksenVuosi.addAttribute("placeholder", "vvvv");
        ylioppilastodistuksenVuosi.addAttribute("required", "required");
        ylioppilastodistuksenVuosi.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        ylioppilastodistuksenVuosi.addAttribute("size", "4");
        ylioppilastodistuksenVuosi.addAttribute("maxlength", "4");
        ylioppilastodistuksenVuosi.setInline(true);

        DropdownSelect ylioppilastutkinto = new DropdownSelect("ylioppilastutkinto",
                createI18NText("form.koulutustausta.lukio.yotutkinto"));
        ylioppilastutkinto.addOption("fi", createI18NText("form.koulutustausta.lukio.yotutkinto.fi"), "fi");
        ylioppilastutkinto.addOption("ib", createI18NText("form.koulutustausta.lukio.yotutkinto.ib"), "ib");
        ylioppilastutkinto.addOption("eb", createI18NText("form.koulutustausta.lukio.yotutkinto.eb"), "eb");
        ylioppilastutkinto.addOption("rp", createI18NText("form.koulutustausta.lukio.yotutkinto.rp"), "rp");
        ylioppilastutkinto.addAttribute("required", "required");
        ylioppilastutkinto.setInline(true);

        Group lukioGroup = new Group("lukioGroup", createI18NText("form.koulutustausta.lukio.suoritus"));
        lukioGroup.addChild(lukioPaattotodistusVuosi);
        lukioGroup.addChild(ylioppilastodistuksenVuosi);
        lukioGroup.addChild(ylioppilastutkinto);

        RelatedQuestionRule lukioRule = new RelatedQuestionRule("rule7", millatutkinnolla.getId(), YLIOPPILAS);
        lukioRule.addChild(lukioGroup);

        millatutkinnolla.addChild(lukioRule);
        millatutkinnolla.addChild(relatedQuestionRule);

        Radio suorittanutAmmatillisenTutkinnon = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NText("form.koulutustausta.ammatillinenSuoritettu"));
        suorittanutAmmatillisenTutkinnon.addOption("kylla", createI18NText("form.yleinen.kylla"), "true");
        suorittanutAmmatillisenTutkinnon.addOption("ei", createI18NText("form.yleinen.en"), "false");
        suorittanutAmmatillisenTutkinnon.addAttribute("required", "required");

        Radio koulutuspaikkaAmmatillisenTutkintoon = new Radio(
                "koulutuspaikkaAmmatillisenTutkintoon",
                createI18NText("form.koulutustausta.ammatillinenKoulutuspaikka"));
        koulutuspaikkaAmmatillisenTutkintoon.addOption("kylla", createI18NText("form.yleinen.kylla"), "true");
        koulutuspaikkaAmmatillisenTutkintoon.addOption("ei", createI18NText("form.yleinen.ei"), "false");
        koulutuspaikkaAmmatillisenTutkintoon.addAttribute("required", "required");

        lukioRule.addChild(suorittanutAmmatillisenTutkinnon);
        lukioRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);
        paattotodistusvuosiPeruskouluRule.addChild(suorittanutAmmatillisenTutkinnon);
        paattotodistusvuosiPeruskouluRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);

        RelatedQuestionRule suorittanutAmmatillisenTutkinnonRule = new RelatedQuestionRule("rule9",
                suorittanutAmmatillisenTutkinnon.getId(), "^true");
        Notification notification1 = new Notification(
                "notification1",
                createI18NText("form.koulutustausta.ammatillinenKoulutuspaikka.huom"),
                Notification.NotificationType.INFO);

        suorittanutAmmatillisenTutkinnonRule.addChild(notification1);
        suorittanutAmmatillisenTutkinnon.addChild(suorittanutAmmatillisenTutkinnonRule);

        RelatedQuestionRule koulutuspaikkaAmmatillisenTutkintoonRule = new RelatedQuestionRule("rule10",
                koulutuspaikkaAmmatillisenTutkintoon.getId(), "^true$");
        Notification notification2 = new Notification(
                "notification2",
                createI18NText("form.koulutustausta.ammatillinenKoulutuspaikka.huom"),
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
