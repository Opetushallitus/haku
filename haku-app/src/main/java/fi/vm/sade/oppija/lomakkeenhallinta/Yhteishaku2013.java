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

package fi.vm.sade.oppija.lomakkeenhallinta;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;
import static fi.vm.sade.oppija.util.OppijaConstants.ERITYISOPETUKSEN_YKSILOLLISTETTY;
import static fi.vm.sade.oppija.util.OppijaConstants.KESKEYTYNYT;
import static fi.vm.sade.oppija.util.OppijaConstants.OSITTAIN_YKSILOLLISTETTY;
import static fi.vm.sade.oppija.util.OppijaConstants.PERUSKOULU;
import static fi.vm.sade.oppija.util.OppijaConstants.ULKOMAINEN_TUTKINTO;
import static fi.vm.sade.oppija.util.OppijaConstants.YKSILOLLISTETTY;
import static fi.vm.sade.oppija.util.OppijaConstants.YLIOPPILAS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.lomake.domain.elements.custom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.PostOffice;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Group;
import fi.vm.sade.oppija.lomake.domain.elements.Notification;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Text;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DateQuestion;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextArea;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.*;
import fi.vm.sade.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.predicate.ComprehensiveSchools;
import fi.vm.sade.oppija.lomakkeenhallinta.predicate.HighSchools;
import fi.vm.sade.oppija.lomakkeenhallinta.predicate.Ids;
import fi.vm.sade.oppija.lomakkeenhallinta.predicate.Languages;

@Service
public class Yhteishaku2013 {

    public static final String ASID = "1.2.246.562.5.50476818906";
    public static final String AOID_ADDITIONAL_QUESTION = "1.2.246.562.14.71344129359";
    public static final String TUTKINTO7_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO5_NOTIFICATION_ID = "tutkinto5-notification";

    private final ApplicationPeriod applicationPeriod;
    public static String mobilePhonePattern =
            "^$|^(?!\\+358|0)[\\+]?[0-9\\-\\s]+$|^(\\+358|0)[\\-\\s]*((4[\\-\\s]*[0-6])|50)[0-9\\-\\s]*$";

    private static final String NOT_FI = "^((?!FI)[A-Z]{2})$";
    
    private final KoodistoService koodistoService;
    private List<SubjectRow> subjects;
    private List<Option> gradeRanges;

    @Autowired // NOSONAR
    public Yhteishaku2013(final KoodistoService koodistoService) { // NOSONAR
        this.koodistoService = koodistoService;
        this.applicationPeriod = new ApplicationPeriod(ASID);
    }


    public void init() { // NOSONAR
        try {

            this.subjects = koodistoService.getSubjects();

            Form form = new Form("yhteishaku", createI18NForm("form.title"));

            applicationPeriod.addForm(form);

            // Henkilötiedot
            Phase henkilotiedot = new Phase("henkilotiedot", createI18NForm("form.henkilotiedot.otsikko"), false);
            form.addChild(henkilotiedot);
            Theme henkilotiedotRyhma = createHenkilotiedotRyhma();
            henkilotiedot.addChild(henkilotiedotRyhma);

            // Koulutustausta
            Phase koulutustausta = new Phase("koulutustausta", createI18NForm("form.koulutustausta.otsikko"), false);
            form.addChild(koulutustausta);
            Theme koulutustaustaRyhma = new Theme("KoulutustaustaGrp", createI18NForm("form.koulutustausta.otsikko"), null);
            koulutustausta.addChild(koulutustaustaRyhma);
            createKoulutustausta(koulutustaustaRyhma);

            // Hakutoiveet
            Phase hakutoiveet = new Phase("hakutoiveet", createI18NForm("form.hakutoiveet.otsikko"), false);
            form.addChild(hakutoiveet);
            Theme hakutoiveetRyhma = createHakutoiveetRyhma();
            hakutoiveet.addChild(hakutoiveetRyhma);
            createHakutoiveet(hakutoiveetRyhma);

            // Arvosanat
            Phase arvosanat = new Phase("arvosanat", createI18NForm("form.arvosanat.otsikko"), false);
            form.addChild(arvosanat);
            Theme arvosanatRyhma = createArvosanatRyhma();
            arvosanat.addChild(arvosanatRyhma);
            createArvosanat(arvosanatRyhma);

            // Lisätiedot
            Phase lisatiedot = new Phase("lisatiedot", createI18NForm("form.lisatiedot.otsikko"), false);
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(applicationPeriod.getStarts());
            cal.roll(Calendar.YEAR, -16);
            WorkExperienceTheme tyokokemusRyhma = new WorkExperienceTheme("tyokokemusGrp",
                    createI18NForm("form.lisatiedot.tyokokemus"), null, "32", cal.getTime());
            Theme lupatiedotRyhma = new Theme("lupatiedotGrp", createI18NForm("form.lisatiedot.lupatiedot"), null);
            form.addChild(lisatiedot);
            lisatiedot.addChild(tyokokemusRyhma);
            lisatiedot.addChild(lupatiedotRyhma);
            createTyokokemus(tyokokemusRyhma);
            createLupatiedot(lupatiedotRyhma);

            // Esikatselu
            Phase esikatselu = new Phase("esikatselu", createI18NForm("form.esikatselu.otsikko"), true);
            form.addChild(esikatselu);
            Theme yhteenvetoRyhma = new Theme("yhteenvetoGrp", createI18NForm("form.esikatselu.yhteenveto"), null);
            esikatselu.addChild(henkilotiedotRyhma).addChild(koulutustaustaRyhma).addChild(hakutoiveetRyhma)
                    .addChild(arvosanatRyhma).addChild(tyokokemusRyhma).addChild(lupatiedotRyhma);
            yhteenvetoRyhma.setHelp(createI18NForm("form.esikatselu.help"));
        } catch (Throwable t) {
            throw new RuntimeException(Yhteishaku2013.class.getCanonicalName() + " init failed", t);
        }

    }

    private Theme createHenkilotiedotRyhma() {
        Theme henkilotiedotRyhma = new Theme("HenkilotiedotGrp", createI18NForm("form.henkilotiedot.otsikko"), null);

        // Nimet
        Question sukunimi = createRequiredTextQuestion("Sukunimi", "form.henkilotiedot.sukunimi", "30");
        sukunimi.setInline(true);
        sukunimi.addAttribute("iso8859name", "iso8859name");
        henkilotiedotRyhma.addChild(sukunimi);

        Question etunimet = createRequiredTextQuestion("Etunimet", "form.henkilotiedot.etunimet", "30");
        etunimet.setInline(true);
        etunimet.addAttribute("iso8859name", "iso8859name");
        henkilotiedotRyhma.addChild(etunimet);

        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", createI18NForm("form.henkilotiedot.kutsumanimi"));
        kutsumanimi.setHelp(createI18NForm("form.henkilotiedot.kutsumanimi.help"));
        kutsumanimi.addAttribute("required", "required");
        kutsumanimi.addAttribute("size", "20");
        kutsumanimi.addAttribute("containedInOther", "Etunimet");
        kutsumanimi.setVerboseHelp(getVerboseHelp());
        kutsumanimi.setInline(true);
        henkilotiedotRyhma.addChild(kutsumanimi);

        // Kansalaisuus, hetu ja sukupuoli suomalaisille
        DropdownSelect kansalaisuus = new DropdownSelect("kansalaisuus", createI18NForm("form.henkilotiedot.kansalaisuus"));
        kansalaisuus.addOptions(koodistoService.getNationalities());
        setDefaultOption("FI", kansalaisuus.getOptions());
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.addAttribute("required", "required");
        kansalaisuus.setHelp(createI18NForm("form.henkilotiedot.kansalaisuus.help"));
        kansalaisuus.setVerboseHelp(getVerboseHelp());
        kansalaisuus.setInline(true);
        henkilotiedotRyhma.addChild(kansalaisuus);

        TextQuestion henkilotunnus = new TextQuestion("Henkilotunnus", createI18NForm("form.henkilotiedot.henkilotunnus"));
        henkilotunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilotunnus.addAttribute("required", "required");
        henkilotunnus.addAttribute("pattern", "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$");
        henkilotunnus.addAttribute("size", "11");
        henkilotunnus.addAttribute("maxlength", "11");
        henkilotunnus.setVerboseHelp(getVerboseHelp());
        henkilotunnus.setInline(true);

        Radio sukupuoli = new Radio("Sukupuoli", createI18NForm("form.henkilotiedot.sukupuoli"));
        sukupuoli.addOption("mies", createI18NForm("form.henkilotiedot.sukupuoli.mies"), "m");
        sukupuoli.addOption("nainen", createI18NForm("form.henkilotiedot.sukupuoli.nainen"), "n");
        sukupuoli.addAttribute("required", "required");
        sukupuoli.setVerboseHelp(getVerboseHelp());
        sukupuoli.setInline(true);

        SocialSecurityNumber socialSecurityNumber = new SocialSecurityNumber("ssn_question", createI18NForm("form.henkilotiedot.hetu"),
                sukupuoli.getI18nText(), sukupuoli.getOptions().get(0),
                sukupuoli.getOptions().get(1), sukupuoli.getId(), henkilotunnus);

        RelatedQuestionRule hetuRule = new RelatedQuestionRule("hetuRule", kansalaisuus.getId(), "^$|^FI$", false);
        hetuRule.addChild(socialSecurityNumber);
        henkilotiedotRyhma.addChild(hetuRule);

        // Ulkomaalaisten tunnisteet
        Radio onkoSinullaSuomalainenHetu = new Radio("onkoSinullaSuomalainenHetu",
                createI18NForm("form.henkilotiedot.hetu.onkoSuomalainen"));
        onkoSinullaSuomalainenHetu.addOption("true", createI18NForm("form.yleinen.kylla"), "true");
        onkoSinullaSuomalainenHetu.addOption("false", createI18NForm("form.yleinen.ei"), "false");
        onkoSinullaSuomalainenHetu.addAttribute("required", "required");
        onkoSinullaSuomalainenHetu.setVerboseHelp(getVerboseHelp());
        onkoSinullaSuomalainenHetu.setInline(true);
        RelatedQuestionRule suomalainenHetuRule = new RelatedQuestionRule("suomalainenHetuRule",
                onkoSinullaSuomalainenHetu.getId(), "^true", false);
        suomalainenHetuRule.addChild(socialSecurityNumber);
        onkoSinullaSuomalainenHetu.addChild(suomalainenHetuRule);

        RelatedQuestionRule eiSuomalaistaHetuaRule = new RelatedQuestionRule("eiSuomalaistaHetuaRule",
                onkoSinullaSuomalainenHetu.getId(), "^false", false);
        eiSuomalaistaHetuaRule.addChild(sukupuoli);

        DateQuestion syntymaaika = new DateQuestion("syntymaaika", createI18NForm("form.henkilotiedot.syntymaaika"));
        syntymaaika.addAttribute("required", "required");
        syntymaaika.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymaaika);

        TextQuestion syntymapaikka = new TextQuestion("syntymapaikka", createI18NForm("form.henkilotiedot.syntymapaikka"));
        syntymapaikka.addAttribute("size", "30");
        syntymapaikka.addAttribute("required", "required");
        syntymapaikka.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymapaikka);

        TextQuestion kansallinenIdTunnus = new TextQuestion("kansallinenIdTunnus", createI18NForm("form.henkilotiedot.kansallinenId"));
        kansallinenIdTunnus.addAttribute("size", "30");
        kansallinenIdTunnus.addAttribute("required", "required");
        kansallinenIdTunnus.setInline(true);
        eiSuomalaistaHetuaRule.addChild(kansallinenIdTunnus);

        TextQuestion passinnumero = new TextQuestion("passinnumero", createI18NForm("form.henkilotiedot.passinnumero"));
        passinnumero.addAttribute("size", "30");
        passinnumero.addAttribute("required", "required");
        passinnumero.setInline(true);
        eiSuomalaistaHetuaRule.addChild(passinnumero);

        onkoSinullaSuomalainenHetu.addChild(eiSuomalaistaHetuaRule);

        RelatedQuestionRule ulkomaalaisenTunnisteetRule = new RelatedQuestionRule("ulkomaalaisenTunnisteetRule",
                kansalaisuus.getId(), NOT_FI, false);
        ulkomaalaisenTunnisteetRule.addChild(onkoSinullaSuomalainenHetu);
        henkilotiedotRyhma.addChild(ulkomaalaisenTunnisteetRule);

        // Email
        TextQuestion email = new TextQuestion("Sähköposti", createI18NForm("form.henkilotiedot.email"));
        email.addAttribute("size", "50");
        email.addAttribute("pattern", "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^$");
        email.setHelp(createI18NForm("form.henkilotiedot.email.help"));
        email.setVerboseHelp(getVerboseHelp());
        email.setInline(true);
        henkilotiedotRyhma.addChild(email);

        // Matkapuhelinnumerot

        TextQuestion puhelinnumero1 = new TextQuestion("matkapuhelinnumero1",
                createI18NForm("form.henkilotiedot.matkapuhelinnumero"));
        puhelinnumero1.setHelp(createI18NForm("form.henkilotiedot.matkapuhelinnumero.help"));
        puhelinnumero1.addAttribute("size", "30");
        puhelinnumero1.addAttribute("pattern", mobilePhonePattern);
        puhelinnumero1.setVerboseHelp(getVerboseHelp());
        puhelinnumero1.setInline(true);
        henkilotiedotRyhma.addChild(puhelinnumero1);

        TextQuestion prevNum = puhelinnumero1;
        AddElementRule prevRule = null;
        for (int i = 2; i <= 5; i++) {
            TextQuestion extranumero = new TextQuestion("matkapuhelinnumero"+i,
                    createI18NForm("form.henkilotiedot.matkapuhelinnumero"));
            extranumero.addAttribute("size", "30");
            extranumero.addAttribute("pattern", mobilePhonePattern);
            extranumero.setInline(true);

            AddElementRule extranumeroRule = new AddElementRule("addPuhelinnumero"+i+"Rule", prevNum.getId(),
                    createI18NForm("form.henkilotiedot.matkapuhelinnumero.lisaa"));
            extranumeroRule.addChild(extranumero);
            if (i == 2) {
                henkilotiedotRyhma.addChild(extranumeroRule);
            } else {
                prevRule.addChild(extranumeroRule);
            }
            prevNum = extranumero;
            prevRule = extranumeroRule;
        }


        // Asuinmaa, osoite
        DropdownSelect asuinmaa = new DropdownSelect("asuinmaa", createI18NForm("form.henkilotiedot.asuinmaa"));
        asuinmaa.addOptions(koodistoService.getCountries());
        setDefaultOption("FI", asuinmaa.getOptions());
        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        asuinmaa.addAttribute("required", "required");
        asuinmaa.setVerboseHelp(getVerboseHelp());
        asuinmaa.setInline(true);

        RelatedQuestionRule asuinmaaFI = new RelatedQuestionRule("rule1", asuinmaa.getId(), "FI", true);
        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "form.henkilotiedot.lahiosoite", "40");
        lahiosoite.setInline(true);
        asuinmaaFI.addChild(lahiosoite);

        Element postinumero = new PostalCode("Postinumero", createI18NForm("form.henkilotiedot.postinumero"), createPostOffices());
        postinumero.addAttribute("size", "5");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("placeholder", "#####");
        postinumero.addAttribute("maxlength", "5");
        postinumero.setHelp(createI18NForm("form.henkilotiedot.postinumero.help"));
        asuinmaaFI.addChild(postinumero);

        DropdownSelect kotikunta = new DropdownSelect("kotikunta", createI18NForm("form.henkilotiedot.kotikunta"));
        kotikunta.addOption("eiValittu", ElementUtil.createI18NForm(null), "");
        kotikunta.addOptions(koodistoService.getMunicipalities());
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        kotikunta.addAttribute("required", "required");
        kotikunta.setVerboseHelp(getVerboseHelp());
        kotikunta.setInline(true);
        kotikunta.setHelp(createI18NForm("form.henkilotiedot.kotikunta.help"));
        asuinmaaFI.addChild(kotikunta);

        CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1",
                createI18NForm("form.henkilotiedot.ensisijainenOsoite"));
        ensisijainenOsoite.setInline(true);
        asuinmaaFI.addChild(ensisijainenOsoite);

        TextArea osoite = new TextArea("osoite", createI18NForm("form.henkilotiedot.osoite"));
        osoite.addAttribute("required", "required");
        osoite.addAttribute("rows", "6");
        osoite.addAttribute("cols", "40");
        osoite.addAttribute("style", "height: 8em");
        RelatedQuestionRule relatedQuestionRule2 =
                new RelatedQuestionRule("rule2", asuinmaa.getId(), NOT_FI, false);
        relatedQuestionRule2.addChild(osoite);
        osoite.setVerboseHelp(getVerboseHelp());
        asuinmaa.addChild(relatedQuestionRule2);
        osoite.setInline(true);

        asuinmaa.addChild(asuinmaaFI);

        henkilotiedotRyhma.addChild(asuinmaa);

        // Äidinkieli
        DropdownSelect aidinkieli = new DropdownSelect("aidinkieli", createI18NForm("form.henkilotiedot.aidinkieli"));
        aidinkieli.addOption("eiValittu", ElementUtil.createI18NForm(null), "");
        aidinkieli.addOptions(koodistoService.getLanguages());
        aidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        aidinkieli.addAttribute("required", "required");
        aidinkieli.setVerboseHelp(getVerboseHelp());
        aidinkieli.setInline(true);
        aidinkieli.setHelp(createI18NForm("form.henkilotiedot.aidinkieli.help"));
        henkilotiedotRyhma.addChild(aidinkieli);

        return henkilotiedotRyhma;
    }

    private Theme createArvosanatRyhma() {
        Map<String, List<Question>> oppiaineMap = new HashMap<String, List<Question>>();

        List<Question> oppiaineList = new ArrayList<Question>();

        oppiaineList.add(new SubjectRow("tietotekniikka", createI18NForm("form.oppiaineet.tietotekniikka"), true, false, false, false));
        oppiaineList.add(new SubjectRow("kansantaloustiede", createI18NForm("form.oppiaineet.kansantaloustiede"), true, false, false, false));
        oppiaineMap.put("1.2.246.562.14.79893512065", oppiaineList);

        return new Theme("arvosanatGrp", createI18NForm("form.arvosanat.otsikko"), oppiaineMap);
    }

    private Theme createHakutoiveetRyhma() {
        final String id = AOID_ADDITIONAL_QUESTION;
        final String elementIdPrefix = id.replace('.', '_');

        Radio radio3 = new Radio(
                elementIdPrefix + "_additional_question_1",
                createI18NForm("form.hakutoiveet.paasykoe.tuloksiaSaaKayttaa"));
        radio3.addOption(elementIdPrefix + "_q1_option_1",
                createI18NForm("form.hakutoiveet.paasykoe.eiOsallistunut"), "q1_option_1");
        radio3.addOption(elementIdPrefix + "_q1_option_2",
                createI18NForm("form.hakutoiveet.paasykoe.eiSaaKayttaa"), "q1_option_2");
        radio3.addOption(elementIdPrefix + "_q1_option_3",
                createI18NForm("form.hakutoiveet.paasykoe.saaKayttaa"), "q1_option_3");

        List<Question> lisakysymysList = new ArrayList<Question>();
        lisakysymysList.add(radio3);

        Map<String, List<Question>> lisakysymysMap = new HashMap<String, List<Question>>();
        lisakysymysMap.put(id, lisakysymysList);

        return new Theme("hakutoiveetGrp", createI18NForm("form.hakutoiveet.otsikko"), lisakysymysMap);
    }

    public GradeGrid createGradeGrid(final String id, final boolean comprehensiveSchool) {


        for (SubjectRow subject : subjects) {
            subject.addAttribute("required", "required");
                    }
        List<SubjectRow> filtered;
        if (comprehensiveSchool) {
            filtered = ImmutableList.copyOf(Iterables.filter(this.subjects,
                    new ComprehensiveSchools()));
        } else {
            filtered = ImmutableList.copyOf(Iterables.filter(this.subjects,
                    new HighSchools()));
                    }

        List<SubjectRow> nativeLanguages = ImmutableList.copyOf(Iterables.filter(filtered,
                new Ids<SubjectRow>("AI")));
        List<SubjectRow> listOfLanguages = ImmutableList.copyOf(Iterables.filter(filtered, new Languages()));

        ImmutableList<SubjectRow> defaultLanguages = ImmutableList.copyOf(
                Iterables.filter(listOfLanguages, new Ids<SubjectRow>("A1", "B1")));

        ImmutableList<SubjectRow> subjectsAfterLanguages = ImmutableList.copyOf(
                Iterables.filter(Iterables.filter(filtered, Predicates.not(new Languages())),
                        Predicates.not(new Ids<SubjectRow>("AI"))));


        GradeGrid gradeGrid = new GradeGrid(id, createI18NForm("form.arvosanat.otsikko"));
        gradeGrid.setVerboseHelp(getVerboseHelp());

        for (SubjectRow nativeLanguage : nativeLanguages) {
            gradeGrid.addChild(createGradeGridRow(nativeLanguage, true, true));
            GradeGridRow additionalNativeLanguageRow = createAdditionalNativeLanguageRow(nativeLanguage, 0);
            additionalNativeLanguageRow.addAttribute("hidden", "hidden");
            additionalNativeLanguageRow.addAttribute("group", "nativeLanguage");
            gradeGrid.addChild(additionalNativeLanguageRow);
        }
        gradeGrid.addChild(createAddLangRow("nativeLanguage", filtered));

        for (SubjectRow defaultLanguage : defaultLanguages) {
            gradeGrid.addChild(createGradeGridRow(defaultLanguage, true, false));
        }

        List<GradeGridRow> additionalLanguages = createAdditionalLanguages(5, filtered);
        for (GradeGridRow additionalLanguage : additionalLanguages) {
            additionalLanguage.addAttribute("group", "languages");
            gradeGrid.addChild(additionalLanguage);
        }
        gradeGrid.addChild(createAddLangRow("languages", filtered));


        for (SubjectRow subjectsAfterLanguage : subjectsAfterLanguages) {
            gradeGrid.addChild(createGradeGridRow(subjectsAfterLanguage, false, false));
        }
        return gradeGrid;
    }

    private List<GradeGridRow> createAdditionalLanguages(int maxAdditionalLanguages, final List<SubjectRow> subjects) {
        List<GradeGridRow> rows = new ArrayList<GradeGridRow>();
        for (int i = 0; i < maxAdditionalLanguages; i++) {
            rows.add(createAdditionalLanguageRow(i, subjects));
        }
        return rows;
    }

    private GradeGridRow createAdditionalNativeLanguageRow(final SubjectRow subjectRow, int index) {

        Element[] columnsArray = createColumnsArray(index);

        String postfix = subjectRow.getId() + "-" + index;
        GradeGridOptionQuestion addLangs = new GradeGridOptionQuestion("custom-language-" + postfix, koodistoService.getSubjectLanguages(), false);
        ElementUtil.setDisabled(addLangs);
        GradeGridOptionQuestion grades = new GradeGridOptionQuestion("custom-grades-" + postfix, getGradeRanges(false), false);
        ElementUtil.setDisabled(grades);
        GradeGridOptionQuestion gradesSelected = new GradeGridOptionQuestion("custom-optional-grades-" + postfix, getGradeRanges(true), true);
        ElementUtil.setDisabled(gradesSelected);
        GradeGridOptionQuestion gradesSelected2 = new GradeGridOptionQuestion("second-custom-optional-grades-" + postfix, getGradeRanges(true), true);
        ElementUtil.setDisabled(gradesSelected2);

        GradeGridTitle title = new GradeGridTitle(System.currentTimeMillis() + "", subjectRow.getI18nText(), true);

        columnsArray[0].addChild(title);
        columnsArray[1].addChild(addLangs);
        columnsArray[2].addChild(grades);
        columnsArray[3].addChild(gradesSelected);
        columnsArray[4].addChild(gradesSelected2);

        GradeGridRow gradeGridRow = ElementUtil.createHiddenGradeGridRowWithId("additionalLanguageNativeRow-" + index);
        gradeGridRow.addChild(columnsArray);
        return gradeGridRow;
    }

    private GradeGridRow createAdditionalLanguageRow(int index, final List<SubjectRow> subjects) {
        GradeGridRow gradeGridRow = new GradeGridRow("additionalLanguageRow-" + index);
        gradeGridRow.addAttribute("hidden", "hidden");
        Element[] columnsArray = createColumnsArray(index);

        List<Option> options = getLanguageSubjects(subjects);
        GradeGridOptionQuestion addSubs = new GradeGridOptionQuestion("custom-scope-" + index, options, false);
        ElementUtil.setDisabled(addSubs);
        GradeGridOptionQuestion addLangs = new GradeGridOptionQuestion("custom-language-" + index, koodistoService.getSubjectLanguages(), false);
        ElementUtil.setDisabled(addLangs);
        GradeGridOptionQuestion grades = new GradeGridOptionQuestion("custom-grades-" + index, getGradeRanges(false), false);
        ElementUtil.setDisabled(grades);
        GradeGridOptionQuestion gradesSelected = new GradeGridOptionQuestion("custom-optional-grades-" + index, getGradeRanges(true), true);
        ElementUtil.setDisabled(gradesSelected);
        GradeGridOptionQuestion gradesSelected2 = new GradeGridOptionQuestion("second-custom-optional-grades-" + index, getGradeRanges(true), true);
        ElementUtil.setDisabled(gradesSelected2);

        columnsArray[0].addChild(addSubs);
        columnsArray[1].addChild(addLangs);
        columnsArray[2].addChild(grades);
        columnsArray[3].addChild(gradesSelected);
        columnsArray[4].addChild(gradesSelected2);

        gradeGridRow.addChild(columnsArray);
        return gradeGridRow;
    }

    private Element[] createColumnsArray(final int index) {

        return new Element[]{
                new GradeGridColumn("column1-" + index, true),
                new GradeGridColumn("column2-" + index, false),
                new GradeGridColumn("column3-" + index, false),
                new GradeGridColumn("column4-" + index, false),
                new GradeGridColumn("column5-" + index, false),
        };
    }

    private List<Option> getGradeRanges(boolean setDefault) {
        this.gradeRanges = koodistoService.getGradeRanges();
        List<Option> gradeRanges = this.gradeRanges;
        if (setDefault) {
            setDefaultOption("Ei arvosanaa", gradeRanges);
        }
        return gradeRanges;
    }

    private List<Option> getLanguageSubjects(final List<SubjectRow> subjects) {
        List<SubjectRow> listOfLanguages = ImmutableList.copyOf(Iterables.filter(subjects, new Languages()));
        List<SubjectRow> additionalLanguages = ImmutableList.copyOf(
                Iterables.filter(listOfLanguages, Predicates.not(new Ids<SubjectRow>("A1", "B1"))));
        List<Option> options = new ArrayList<Option>();
        for (SubjectRow additionalLanguage : additionalLanguages) {
            options.add(new Option("subject-template", additionalLanguage.getI18nText(), additionalLanguage.getId()));
        }
        return options;
    }

    private GradeGridRow createAddLangRow(final String group, List<SubjectRow> subjects) {
        GradeGridRow gradeGridRow = new GradeGridRow(System.currentTimeMillis() + "");
        GradeGridColumn column1 = new GradeGridColumn(gradeGridRow.getId() + "-addlang", false);
        List<Option> subjectOptions = getLanguageSubjects(subjects);
        GradeGridAddLang child = new GradeGridAddLang(group, subjectOptions, koodistoService.getSubjectLanguages(),
                koodistoService.getGradeRanges());
        column1.addChild(child);
        column1.addAttribute("colspan", "5");
        gradeGridRow.addChild(column1);
        return gradeGridRow;
    }

    private GradeGridRow createGradeGridRow(final SubjectRow subjectRow, boolean language, boolean ai) {
        GradeGridRow gradeGridRow = new GradeGridRow(subjectRow.getId());
        GradeGridColumn column1 = new GradeGridColumn("column1", false);
        column1.addChild(new GradeGridTitle(System.currentTimeMillis() + "", subjectRow.getI18nText(), false));
        GradeGridColumn column2 = new GradeGridColumn("column2", false);
        GradeGridColumn column3 = new GradeGridColumn("column3", false);
        GradeGridColumn column4 = new GradeGridColumn("column4", false);
        GradeGridColumn column5 = new GradeGridColumn("column5", false);
        List<Option> gradeRanges = koodistoService.getGradeRanges();
        List<Option> gradeRangesSecond = koodistoService.getGradeRanges();
        setDefaultOption("Ei arvosanaa", gradeRangesSecond);
        if (subjectRow.isLanguage() || language) {
            List<Option> subjectLanguages = koodistoService.getSubjectLanguages();
            if (ai) {
                setDefaultOption("FI", subjectLanguages);
            }
            GradeGridOptionQuestion child = new GradeGridOptionQuestion(subjectRow.getId() + "", subjectLanguages, false);
            child.addAttribute("required", "required");
            column2.addChild(child);
        } else {
            column1.addAttribute("colspan", "2");
        }
        GradeGridOptionQuestion child1 = new GradeGridOptionQuestion("common-" + subjectRow.getId(), gradeRanges, false);
        child1.addAttribute("required", "required");
        column3.addChild(child1);
        GradeGridOptionQuestion gradeGridOptionQuestion = new GradeGridOptionQuestion("optional-common-" + subjectRow.getId(), gradeRangesSecond, true);
        gradeGridOptionQuestion.addAttribute("required", "required");
        GradeGridOptionQuestion child = gradeGridOptionQuestion;
        column4.addChild(child);
        GradeGridOptionQuestion child2 = new GradeGridOptionQuestion("second-optional-common-" + subjectRow.getId(), gradeRangesSecond, true);
        child2.addAttribute("required", "required");
        column5.addChild(child2);
        gradeGridRow.addChild(column1);
        if (subjectRow.isLanguage() || language) {
            gradeGridRow.addChild(column2);
        }
        gradeGridRow.addChild(column3);
        gradeGridRow.addChild(column4);
        gradeGridRow.addChild(column5);
        return gradeGridRow;

    }

    private void createArvosanat(Theme arvosanatRyhma) {
        RelatedQuestionRule relatedQuestionPK = new RelatedQuestionRule("rule_grade_pk", "millatutkinnolla",
                "(" + PERUSKOULU + "|tutkinto2|tutkinto3|tutkinto4)", false);
        relatedQuestionPK.addChild(createGradeGrid("grid_pk", true));
        arvosanatRyhma.addChild(relatedQuestionPK);

        RelatedQuestionRule relatedQuestionLukio = new RelatedQuestionRule("rule_grade_yo", "millatutkinnolla",
                "(" + YLIOPPILAS + ")", false);
        relatedQuestionLukio.addChild(createGradeGrid("grid_yo", false));
        arvosanatRyhma.addChild(relatedQuestionLukio);

        RelatedQuestionRule relatedQuestionEiTutkintoa = new RelatedQuestionRule("rule_grade_no", "millatutkinnolla",
                "(tutkinto5|tutkinto7)", false);
        relatedQuestionEiTutkintoa.addChild(new Text("nogradegrid", createI18NForm("form.arvosanat.eiKysyta")));
        arvosanatRyhma.addChild(relatedQuestionEiTutkintoa);

        arvosanatRyhma
                .setHelp(createI18NForm("form.arvosanat.help"));

    }

    private void createHakutoiveet(Theme hakutoiveetRyhma) {
        hakutoiveetRyhma
                .setHelp(createI18NForm("form.hakutoiveet.help"));

        Radio radio = new Radio(
                "_sora_question_1",
                createI18NForm("form.sora.terveys"));
        radio.addOption("_sora_q1_option_1", createI18NForm("form.yleinen.ei"), "q1_option_1");
        radio.addOption("_sora_q1_option_2", createI18NForm("form.sora.kylla"),
                "q1_option_2");
        Radio radio2 = new Radio(
                "_sora_additional_question_2",
                createI18NForm("form.sora.oikeudenMenetys"));
        radio2.addOption("_sora_q2_option_1", createI18NForm("form.yleinen.ei"), "q2_option_1");
        radio2.addOption("_sora_q2_option_2",
                createI18NForm("form.sora.kylla"), "q2_option_2");
        List<Radio> soraQuestions = Lists.newArrayList();
        soraQuestions.add(radio);
        soraQuestions.add(radio2);

        PreferenceTable preferenceTable = new PreferenceTable("preferencelist", createI18NForm("form.hakutoiveet.otsikko"), "Ylös",
                "Alas", 32);
        PreferenceRow pr1 = ElementUtil.createI18NPreferenceRow("preference1", "1", 32);
        pr1.addAttribute("required", "required");
        PreferenceRow pr2 = ElementUtil.createI18NPreferenceRow("preference2", "2", 32);
        PreferenceRow pr3 = ElementUtil.createI18NPreferenceRow("preference3", "3", 32);
        PreferenceRow pr4 = ElementUtil.createI18NPreferenceRow("preference4", "4", 32);
        PreferenceRow pr5 = ElementUtil.createI18NPreferenceRow("preference5", "5", 32);
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
                .setHelp(createI18NForm("form.tyokokemus.help"));
        TextQuestion tyokokemuskuukaudet = new TextQuestion("tyokokemuskuukaudet",
                createI18NForm("form.tyokokemus.kuukausina"));
        tyokokemuskuukaudet
                .setHelp(createI18NForm("form.tyokokemus.kuukausina.help"));
        tyokokemuskuukaudet.addAttribute("placeholder", "kuukautta");
        tyokokemuskuukaudet.addAttribute("pattern", "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$");
        tyokokemuskuukaudet.addAttribute("size", "8");
        tyokokemuskuukaudet.setVerboseHelp(getVerboseHelp());
        tyokokemus.addChild(tyokokemuskuukaudet);
    }

    private void createLupatiedot(Theme lupatiedot) {

        CheckBox lupaMarkkinointi = new CheckBox(
                "lupaMarkkinointi",
                createI18NForm("form.lupatiedot.saaMarkkinoida"));
        CheckBox lupaJulkaisu = new CheckBox("lupaJulkaisu",
                createI18NForm("form.lupatiedot.saaJulkaista"));
        CheckBox lupaSahkoisesti = new CheckBox("lupaSahkoisesti",
                createI18NForm("form.lupatiedot.saaLahettaaSahkoisesti"));
        CheckBox lupaSms = new CheckBox(
                "lupaSms",
                createI18NForm("form.lupatiedot.saaLahettaaTekstiviesteja"));

        Group lupaGroup = new Group("permissionCheckboxes", createI18NForm("form.lupatiedot.otsikko"));

        lupaGroup.addChild(lupaMarkkinointi);
        lupaGroup.addChild(lupaJulkaisu);
        lupaGroup.addChild(lupaSahkoisesti);
        lupaGroup.addChild(lupaSms);
        lupatiedot.addChild(lupaGroup);
        lupatiedot.setVerboseHelp(getVerboseHelp());

        Radio asiointikieli = new Radio("asiointikieli", createI18NForm("form.asiointikieli.otsikko"));
        asiointikieli.setHelp(createI18NForm("form.asiointikieli.help"));
        asiointikieli.addOption("suomi", createI18NForm("Suomi"), "suomi");
        asiointikieli.addOption("ruotsi", createI18NForm("Ruotsi"), "ruotsi");
        asiointikieli.addAttribute("required", "required");
        asiointikieli.setVerboseHelp(getVerboseHelp());
        lupatiedot.addChild(asiointikieli);
    }

    private void createKoulutustausta(Theme koulutustaustaRyhma) {
        koulutustaustaRyhma
                .setHelp(createI18NForm("form.koulutustausta.help"));

        Radio osallistunut = new Radio("osallistunut",
                createI18NForm("form.koulutustausta.osallistunutPaasykokeisiin"));
        osallistunut.addOption("ei", createI18NForm("form.yleinen.en"), "false");
        osallistunut.addOption("kylla", createI18NForm("form.yleinen.kylla"), "true");
        osallistunut.addAttribute("required", "required");
        osallistunut.setVerboseHelp(getVerboseHelp());

        koulutustaustaRyhma.addChild(createKoulutustaustaRadio());
        koulutustaustaRyhma.addChild(osallistunut);
    }

    public Radio createKoulutustaustaRadio() { //NOSONAR
        Radio millatutkinnolla = new Radio("millatutkinnolla",
                createI18NForm("form.koulutustausta.millaTutkinnolla"));
        millatutkinnolla.addOption("tutkinto1", createI18NForm("form.koulutustausta.peruskoulu"), PERUSKOULU,
                createI18NForm("form.koulutustausta.peruskoulu.help"));
        millatutkinnolla
                .addOption("tutkinto2",
                        createI18NForm("form.koulutustausta.osittainYksilollistetty"),
                        OSITTAIN_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.osittainYksilollistetty.help"));
        millatutkinnolla
                .addOption(
                        "tutkinto3",
                        createI18NForm("form.koulutustausta.erityisopetuksenYksilollistetty"),
                        ERITYISOPETUKSEN_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.erityisopetuksenYksilollistetty.help"));
        millatutkinnolla
                .addOption(
                        "tutkinto4",
                        createI18NForm("form.koulutustausta.yksilollistetty"),
                        YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.yksilollistetty.help"));
        millatutkinnolla.addOption("tutkinto5",
                createI18NForm("form.koulutustausta.keskeytynyt"),
                KESKEYTYNYT,
                createI18NForm("form.koulutustausta.keskeytynyt"));
        millatutkinnolla
                .addOption(
                        "tutkinto6",
                        createI18NForm("form.koulutustausta.lukio"),
                        YLIOPPILAS,
                        createI18NForm("form.koulutustausta.lukio.help"));
        millatutkinnolla.addOption("tutkinto7", createI18NForm("form.koulutustausta.ulkomailla"), ULKOMAINEN_TUTKINTO,
                createI18NForm("form.koulutustausta.ulkomailla.help"));
        millatutkinnolla.setVerboseHelp(getVerboseHelp());
        millatutkinnolla.addAttribute("required", "required");

        Notification tutkinto7Notification = new Notification(TUTKINTO7_NOTIFICATION_ID,
                createI18NForm("form.koulutustausta.ulkomailla.huom"),
                Notification.NotificationType.INFO);

        Notification tutkinto5Notification = new Notification(TUTKINTO5_NOTIFICATION_ID,
                createI18NForm("form.koulutustausta.keskeytynyt.huom"),
                Notification.NotificationType.INFO);


        RelatedQuestionRule keskeytynytRule = new RelatedQuestionRule("tutkinto5-rule",
                millatutkinnolla.getId(), KESKEYTYNYT, false);

        RelatedQuestionRule ulkomaillaSuoritettuTutkintoRule = new RelatedQuestionRule("tutkinto7-rule",
                millatutkinnolla.getId(), ULKOMAINEN_TUTKINTO, false);

        ulkomaillaSuoritettuTutkintoRule.addChild(tutkinto7Notification);
        keskeytynytRule.addChild(tutkinto5Notification);
        millatutkinnolla.addChild(ulkomaillaSuoritettuTutkintoRule);
        millatutkinnolla.addChild(keskeytynytRule);

        TextQuestion paattotodistusvuosiPeruskoulu = new TextQuestion("paattotodistusvuosi_peruskoulu",
                createI18NForm("form.koulutustausta.paattotodistusvuosi"));
        paattotodistusvuosiPeruskoulu.addAttribute("placeholder", "vvvv");
        paattotodistusvuosiPeruskoulu.addAttribute("required", "required");
        paattotodistusvuosiPeruskoulu.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        paattotodistusvuosiPeruskoulu.addAttribute("size", "4");
        paattotodistusvuosiPeruskoulu.addAttribute("maxlength", "4");

        CheckBox suorittanut1 = new CheckBox("suorittanut1",
                createI18NForm("form.koulutustausta.kymppiluokka"));
        CheckBox suorittanut2 = new CheckBox("suorittanut2",
                createI18NForm("form.koulutustausta.vammaistenValmentava"));
        CheckBox suorittanut3 = new CheckBox("suorittanut3",
                createI18NForm("form.koulutustausta.maahanmuuttajienValmistava"));
        CheckBox suorittanut4 = new CheckBox(
                "suorittanut4",
                createI18NForm("form.koulutustausta.talouskoulu"));
        CheckBox suorittanut5 = new CheckBox(
                "suorittanut5",
                createI18NForm("form.koulutustausta.ammattistartti"));
        CheckBox suorittanut6 = new CheckBox("suorittanut6",
                createI18NForm("form.koulutustausta.kansanopisto"));

        Group suorittanutGroup = new Group("suorittanutgroup",
                createI18NForm("form.koulutustausta.suorittanut"));
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
                + YKSILOLLISTETTY + ")", false);

        RelatedQuestionRule paattotodistusvuosiPeruskouluRule = new RelatedQuestionRule("rule8",
                paattotodistusvuosiPeruskoulu.getId(), "^(19[0-9][0-9]|200[0-9]|201[0-1])$", false);

        relatedQuestionRule.addChild(paattotodistusvuosiPeruskoulu);
        // relatedQuestionRule.addChild(tutkinnonOpetuskieli);
        relatedQuestionRule.addChild(suorittanutGroup);
        relatedQuestionRule.addChild(paattotodistusvuosiPeruskouluRule);

        TextQuestion lukioPaattotodistusVuosi = new TextQuestion("lukioPaattotodistusVuosi",
                createI18NForm("form.koulutustausta.paattotodistusvuosi"));
        lukioPaattotodistusVuosi.addAttribute("placeholder", "vvvv");
        lukioPaattotodistusVuosi.addAttribute("required", "required");
        lukioPaattotodistusVuosi.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        lukioPaattotodistusVuosi.addAttribute("size", "4");
        lukioPaattotodistusVuosi.addAttribute("maxlength", "4");
        lukioPaattotodistusVuosi.setInline(true);

        TextQuestion ylioppilastodistuksenVuosi = new TextQuestion("ylioppilastodistuksenVuosi",
                createI18NForm("form.koulutustausta.lukio.yotodistusvuosi"));
        ylioppilastodistuksenVuosi.addAttribute("placeholder", "vvvv");
        ylioppilastodistuksenVuosi.addAttribute("required", "required");
        ylioppilastodistuksenVuosi.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        ylioppilastodistuksenVuosi.addAttribute("size", "4");
        ylioppilastodistuksenVuosi.addAttribute("maxlength", "4");
        ylioppilastodistuksenVuosi.setInline(true);

        DropdownSelect ylioppilastutkinto = new DropdownSelect("ylioppilastutkinto",
                createI18NForm("form.koulutustausta.lukio.yotutkinto"));
        ylioppilastutkinto.addOption("fi", createI18NForm("form.koulutustausta.lukio.yotutkinto.fi"), "fi");
        ylioppilastutkinto.addOption("ib", createI18NForm("form.koulutustausta.lukio.yotutkinto.ib"), "ib");
        ylioppilastutkinto.addOption("eb", createI18NForm("form.koulutustausta.lukio.yotutkinto.eb"), "eb");
        ylioppilastutkinto.addOption("rp", createI18NForm("form.koulutustausta.lukio.yotutkinto.rp"), "rp");
        ylioppilastutkinto.addAttribute("required", "required");
        ylioppilastutkinto.setInline(true);

        Group lukioGroup = new Group("lukioGroup", createI18NForm("form.koulutustausta.lukio.suoritus"));
        lukioGroup.addChild(lukioPaattotodistusVuosi);
        lukioGroup.addChild(ylioppilastodistuksenVuosi);
        lukioGroup.addChild(ylioppilastutkinto);

        RelatedQuestionRule lukioRule = new RelatedQuestionRule("rule7", millatutkinnolla.getId(), YLIOPPILAS, false);
        lukioRule.addChild(lukioGroup);

        millatutkinnolla.addChild(lukioRule);
        millatutkinnolla.addChild(relatedQuestionRule);

        Radio suorittanutAmmatillisenTutkinnon = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NForm("form.koulutustausta.ammatillinenSuoritettu"));
        suorittanutAmmatillisenTutkinnon.addOption("kylla", createI18NForm("form.yleinen.kylla"), "true");
        suorittanutAmmatillisenTutkinnon.addOption("ei", createI18NForm("form.yleinen.en"), "false");
        suorittanutAmmatillisenTutkinnon.addAttribute("required", "required");

        Radio koulutuspaikkaAmmatillisenTutkintoon = new Radio(
                "koulutuspaikkaAmmatillisenTutkintoon",
                createI18NForm("form.koulutustausta.ammatillinenKoulutuspaikka"));
        koulutuspaikkaAmmatillisenTutkintoon.addOption("kylla", createI18NForm("form.yleinen.kylla"), "true");
        koulutuspaikkaAmmatillisenTutkintoon.addOption("ei", createI18NForm("form.yleinen.ei"), "false");
        koulutuspaikkaAmmatillisenTutkintoon.addAttribute("required", "required");

        lukioRule.addChild(suorittanutAmmatillisenTutkinnon);
        lukioRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);
        paattotodistusvuosiPeruskouluRule.addChild(suorittanutAmmatillisenTutkinnon);
        paattotodistusvuosiPeruskouluRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);

        RelatedQuestionRule suorittanutAmmatillisenTutkinnonRule = new RelatedQuestionRule("rule9",
                suorittanutAmmatillisenTutkinnon.getId(), "^true", false);
        Notification notification1 = new Notification(
                "notification1",
                createI18NForm("form.koulutustausta.ammatillinenKoulutuspaikka.huom"),
                Notification.NotificationType.INFO);

        suorittanutAmmatillisenTutkinnonRule.addChild(notification1);
        suorittanutAmmatillisenTutkinnon.addChild(suorittanutAmmatillisenTutkinnonRule);

        RelatedQuestionRule koulutuspaikkaAmmatillisenTutkintoonRule = new RelatedQuestionRule("rule10",
                koulutuspaikkaAmmatillisenTutkintoon.getId(), "^true$", false);
        Notification notification2 = new Notification(
                "notification2",
                createI18NForm("form.koulutustausta.ammatillinenKoulutuspaikka.huom"),
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
        TextQuestion textQuestion = new TextQuestion(id, createI18NForm(name));
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
