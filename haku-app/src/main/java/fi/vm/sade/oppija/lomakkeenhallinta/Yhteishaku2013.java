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

import com.google.common.collect.ImmutableList;
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
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.FormConstants;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.ArvosanatTheme;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.KielitaitokysymyksetTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.*;
import static fi.vm.sade.oppija.util.OppijaConstants.*;

@Service
public class Yhteishaku2013 {

    public static final String TUTKINTO_ULKOMAILLA_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO_KESKEYTNYT_NOTIFICATION_ID = "tutkinto5-notification";
    public static final String AIDINKIELI_ID = "aidinkieli";
    public static final String FORM_ID = "yhteishaku";
    public static final String DISCRETIONARY_EDUCATION_DEGREE = "32";
    public static final String HAKUTOIVEET_PHASE_ID = "hakutoiveet";
    public static final String TUTKINTO_OSITTAIN_YKSILOLLISTETTY = "tutkinto2";
    public static final String TUTKINTO_ERITYISOPETUKSEN_YKSILOLLISTETTY = "tutkinto3";
    public static final String TUTKINTO_YKSILOLLISTETTY = "tutkinto6";
    public static final String TUTKINTO_KESKEYTYNYT = "tutkinto7";
    public static final String TUTKINTO_YLIOPPILAS = "tutkinto9";
    public static final String TUTKINTO_ULKOMAINEN_TUTKINTO = "tutkinto0";
    public static final String TUTKINTO_KESKEYTYNYT_RULE = "tutkinto_7_rule";
    public static final String TUTKINTO_ULKOMAILLA_RULE = "tutkinto_0_rule";
    public static final String TUTKINTO_PERUSKOULU = "tutkinto1";
    public static final int AGE_WORK_EXPERIENCE = 16;
    private final ApplicationPeriod applicationPeriod;
    private final KielitaitokysymyksetTheme kielitaitokysymyksetTheme = new KielitaitokysymyksetTheme(this);


    public String aoidAdditionalQuestion = "1.2.246.562.14.71344129359";
    public static final String MOBILE_PHONE_PATTERN =
            "^$|^(?!\\+358|0)[\\+]?[0-9\\-\\s]+$|^(\\+358|0)[\\-\\s]*((4[\\-\\s]*[0-6])|50)[0-9\\-\\s]*$";
    public static final String PHONE_PATTERN =
            "^$|^\\+?[0-9\\-\\s]+$";

    private static final String NOT_FI = "^((?!FIN)[A-Z]{3})$";

    private final KoodistoService koodistoService;

    @Autowired // NOSONAR
    public Yhteishaku2013(
            final KoodistoService koodistoService,
            @Value("${asid}") String asid,
            @Value("${aoid}") String aoid) { // NOSONAR
        this.koodistoService = koodistoService;
        this.applicationPeriod = new ApplicationPeriod(asid);
        this.aoidAdditionalQuestion = aoid;
        createFrom();
    }


    public void createFrom() { // NOSONAR
        try {

            Form form = new Form(FORM_ID, createI18NForm("form.title"));

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
            Phase hakutoiveet = new Phase(HAKUTOIVEET_PHASE_ID, createI18NForm("form.hakutoiveet.otsikko"), false);
            form.addChild(hakutoiveet);
            Theme hakutoiveetRyhma = createHakutoiveetRyhma();
            hakutoiveet.addChild(hakutoiveetRyhma);
            createHakutoiveet(hakutoiveetRyhma);

            // Entinen ArvosanatTheme nykyinen Osaaminen
            Phase osaaminen = new Phase("osaaminen", createI18NForm("form.osaaminen.otsikko"), false);
            Theme arvosanatTheme = ArvosanatTheme.createArvosanatTheme(koodistoService);
            Element kielitaitokysymyksetTheme = KielitaitokysymyksetTheme.createKielitaitokysymyksetTheme();

            osaaminen.addChild(arvosanatTheme);
            osaaminen.addChild(kielitaitokysymyksetTheme);
            form.addChild(osaaminen);

            // Lisätiedot
            Phase lisatiedot = new Phase("lisatiedot", createI18NForm("form.lisatiedot.otsikko"), false);
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(applicationPeriod.getStarts());
            cal.roll(Calendar.YEAR, -AGE_WORK_EXPERIENCE);
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
                    .addChild(arvosanatTheme).addChild(kielitaitokysymyksetTheme)
                    .addChild(tyokokemusRyhma).addChild(lupatiedotRyhma);
            yhteenvetoRyhma.setHelp(createI18NForm("form.esikatselu.help"));
        } catch (Exception e) {
            throw new RuntimeException(Yhteishaku2013.class.getCanonicalName() + " init failed", e);
        }

    }


    public static Element createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(final String index) {

        Radio radio = new Radio(index + "_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                createI18NForm("form.hakutoiveet.urheilijan.ammatillisen.koulutuksen.lisakysymys"));
        addDefaultTrueFalseOptions(radio);
        setRequired(radio);

        RelatedQuestionRule hasQuestion = new RelatedQuestionRule(radio.getId() + "_related_question_rule",
                ImmutableList.of(index + "-Koulutus-id-athlete"), ElementUtil.KYLLA, false);

        hasQuestion.addChild(radio);

        return hasQuestion;
    }

    public static Element createSoraQuestions(final String index) {
        // sora-kysymykset

        RelatedQuestionRule hasSora = new RelatedQuestionRule(index + "_sora_rule",
                ImmutableList.of(index + "-Koulutus-id-sora"), ElementUtil.KYLLA, false);

        Radio sora1 = new Radio(index + "_sora_terveys", createI18NForm("form.sora.terveys"));
        sora1.addOption(ElementUtil.EI, createI18NForm("form.yleinen.ei"), ElementUtil.EI);
        sora1.addOption(ElementUtil.KYLLA, createI18NForm("form.sora.kylla"), ElementUtil.KYLLA);
        ElementUtil.setRequired(sora1);
        sora1.setPopup(new Popup("sora-popup", createI18NForm("form.hakutoiveet.terveydentilavaatimukset.otsikko")));

        Radio sora2 = new Radio(index + "_sora_oikeudenMenetys", createI18NForm("form.sora.oikeudenMenetys"));
        sora2.addOption(ElementUtil.EI, createI18NForm("form.yleinen.ei"), ElementUtil.EI);
        sora2.addOption(ElementUtil.KYLLA, createI18NForm("form.sora.kylla"), ElementUtil.KYLLA);
        ElementUtil.setRequired(sora2);

        // popup ensimmäistä sora-kysymystä varten


        hasSora.addChild(sora1, sora2);
        return hasSora;
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
        kutsumanimi.addAttribute("size", "20");
        kutsumanimi.addAttribute("containedInOther", "Etunimet");
        setRequiredInlineAndVerboseHelp(kutsumanimi);

        henkilotiedotRyhma.addChild(kutsumanimi);

        // Kansalaisuus, hetu ja sukupuoli suomalaisille
        DropdownSelect kansalaisuus =
                new DropdownSelect("kansalaisuus", createI18NForm("form.henkilotiedot.kansalaisuus"), null);
        kansalaisuus.addOptions(koodistoService.getNationalities());
        setDefaultOption("FIN", kansalaisuus.getOptions());
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.setHelp(createI18NForm("form.henkilotiedot.kansalaisuus.help"));
        setRequiredInlineAndVerboseHelp(kansalaisuus);
        henkilotiedotRyhma.addChild(kansalaisuus);

        TextQuestion henkilotunnus =
                new TextQuestion("Henkilotunnus", createI18NForm("form.henkilotiedot.henkilotunnus"));
        henkilotunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilotunnus.addAttribute("pattern", "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$");
        henkilotunnus.addAttribute("size", "11");
        henkilotunnus.addAttribute("maxlength", "11");
        setRequiredInlineAndVerboseHelp(henkilotunnus);

        Radio sukupuoli = new Radio("SUKUPUOLI", createI18NForm("form.henkilotiedot.sukupuoli"));
        sukupuoli.addOption("mies", createI18NForm("form.henkilotiedot.sukupuoli.mies"), "m");
        sukupuoli.addOption("nainen", createI18NForm("form.henkilotiedot.sukupuoli.nainen"), "n");
        setRequiredInlineAndVerboseHelp(sukupuoli);

        SocialSecurityNumber socialSecurityNumber =
                new SocialSecurityNumber("ssn_question", createI18NForm("form.henkilotiedot.hetu"),
                        sukupuoli.getI18nText(), sukupuoli.getOptions().get(0),
                        sukupuoli.getOptions().get(1), sukupuoli.getId(), henkilotunnus);

        RelatedQuestionRule hetuRule = new RelatedQuestionRule("hetuRule", kansalaisuus.getId(), "^$|^FIN$", true);
        hetuRule.addChild(socialSecurityNumber);
        henkilotiedotRyhma.addChild(hetuRule);

        // Ulkomaalaisten tunnisteet
        Radio onkoSinullaSuomalainenHetu = new Radio("onkoSinullaSuomalainenHetu",
                createI18NForm("form.henkilotiedot.hetu.onkoSuomalainen"));
        addDefaultTrueFalseOptions(onkoSinullaSuomalainenHetu);
        setRequiredInlineAndVerboseHelp(onkoSinullaSuomalainenHetu);
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

        TextQuestion syntymapaikka =
                new TextQuestion("syntymapaikka", createI18NForm("form.henkilotiedot.syntymapaikka"));
        syntymapaikka.addAttribute("size", "30");
        syntymapaikka.addAttribute("required", "required");
        syntymapaikka.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymapaikka);

        TextQuestion kansallinenIdTunnus =
                new TextQuestion("kansallinenIdTunnus", createI18NForm("form.henkilotiedot.kansallinenId"));
        kansallinenIdTunnus.addAttribute("size", "30");
        kansallinenIdTunnus.setInline(true);
        eiSuomalaistaHetuaRule.addChild(kansallinenIdTunnus);

        TextQuestion passinnumero = new TextQuestion("passinnumero", createI18NForm("form.henkilotiedot.passinnumero"));
        passinnumero.addAttribute("size", "30");
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
        email.setVerboseHelp(FormConstants.VERBOSE_HELP);
        email.setInline(true);
        henkilotiedotRyhma.addChild(email);

        // Matkapuhelinnumerot

        TextQuestion puhelinnumero1 = new TextQuestion("matkapuhelinnumero1",
                createI18NForm("form.henkilotiedot.matkapuhelinnumero"));
        puhelinnumero1.setHelp(createI18NForm("form.henkilotiedot.matkapuhelinnumero.help"));
        puhelinnumero1.addAttribute("size", "30");
        puhelinnumero1.addAttribute("pattern", MOBILE_PHONE_PATTERN);
        puhelinnumero1.setVerboseHelp(FormConstants.VERBOSE_HELP);
        puhelinnumero1.setInline(true);
        henkilotiedotRyhma.addChild(puhelinnumero1);

        TextQuestion prevNum = puhelinnumero1;
        AddElementRule prevRule = null;
        for (int i = 2; i <= 5; i++) {
            TextQuestion extranumero = new TextQuestion("matkapuhelinnumero" + i,
                    createI18NForm("form.henkilotiedot.puhelinnumero"));
            extranumero.addAttribute("size", "30");
            extranumero.addAttribute("pattern", PHONE_PATTERN);
            extranumero.setInline(true);

            AddElementRule extranumeroRule = new AddElementRule("addPuhelinnumero" + i + "Rule", prevNum.getId(),
                    createI18NForm("form.henkilotiedot.puhelinnumero.lisaa"));
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
        DropdownSelect asuinmaa = new DropdownSelect("asuinmaa", createI18NForm("form.henkilotiedot.asuinmaa"), null);
        asuinmaa.addOptions(koodistoService.getCountries());
        setDefaultOption("FIN", asuinmaa.getOptions());
        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        setRequiredInlineAndVerboseHelp(asuinmaa);

        RelatedQuestionRule asuinmaaFI = new RelatedQuestionRule("rule1", asuinmaa.getId(), "FIN", true);
        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "form.henkilotiedot.lahiosoite", "40");
        lahiosoite.setInline(true);
        asuinmaaFI.addChild(lahiosoite);

        Element postinumero = new PostalCode("Postinumero", createI18NForm("form.henkilotiedot.postinumero"),
                createPostOffices());
        postinumero.addAttribute("size", "5");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("placeholder", "#####");
        postinumero.addAttribute("maxlength", "5");
        postinumero.setHelp(createI18NForm("form.henkilotiedot.postinumero.help"));
        asuinmaaFI.addChild(postinumero);

        DropdownSelect kotikunta =
                new DropdownSelect("kotikunta", createI18NForm("form.henkilotiedot.kotikunta"), null);
        kotikunta.addOption("eiValittu", ElementUtil.createI18NForm(null), "");
        kotikunta.addOptions(koodistoService.getMunicipalities());
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        setRequiredInlineAndVerboseHelp(kotikunta);
        kotikunta.setHelp(createI18NForm("form.henkilotiedot.kotikunta.help"));
        asuinmaaFI.addChild(kotikunta);

        CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1",
                createI18NForm("form.henkilotiedot.ensisijainenOsoite"));
        ensisijainenOsoite.setInline(true);
        asuinmaaFI.addChild(ensisijainenOsoite);

        TextArea osoite = new TextArea("osoite", createI18NForm("form.henkilotiedot.osoite"));
        osoite.addAttribute("rows", "6");
        osoite.addAttribute("cols", "40");
        osoite.addAttribute("style", "height: 8em");
        RelatedQuestionRule relatedQuestionRule2 =
                new RelatedQuestionRule("rule2", asuinmaa.getId(), NOT_FI, false);
        relatedQuestionRule2.addChild(osoite);
        asuinmaa.addChild(relatedQuestionRule2);
        setRequiredInlineAndVerboseHelp(osoite);

        asuinmaa.addChild(asuinmaaFI);

        henkilotiedotRyhma.addChild(asuinmaa);

        // Äidinkieli
        DropdownSelect aidinkieli =
                new DropdownSelect(AIDINKIELI_ID, createI18NForm("form.henkilotiedot.aidinkieli"),
                        "fi_vm_sade_oppija_language");
        aidinkieli.addOption("eiValittu", ElementUtil.createI18NForm(null), "");
        aidinkieli.addOptions(koodistoService.getLanguages());
        aidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        setRequiredInlineAndVerboseHelp(aidinkieli);
        aidinkieli.setHelp(createI18NForm("form.henkilotiedot.aidinkieli.help"));
        henkilotiedotRyhma.addChild(aidinkieli);

        return henkilotiedotRyhma;
    }

    private Theme createHakutoiveetRyhma() {
        final String elementIdPrefix = aoidAdditionalQuestion.replace('.', '_');

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
        lisakysymysMap.put(aoidAdditionalQuestion, lisakysymysList);

        return new Theme("hakutoiveetGrp", createI18NForm("form.hakutoiveet.otsikko"), lisakysymysMap);
    }


    private void createArsdvosanat(Theme arvosanatRyhma) {


    }

    private void createHakutoiveet(Theme hakutoiveetRyhma) {
        hakutoiveetRyhma
                .setHelp(createI18NForm("form.hakutoiveet.help"));

        PreferenceTable preferenceTable =
                new PreferenceTable("preferencelist", createI18NForm("form.hakutoiveet.otsikko"), "Ylös", "Alas");

        PreferenceRow pr1 = createI18NPreferenceRow("preference1", "1");
        pr1.addAttribute("required", "required");
        PreferenceRow pr2 = createI18NPreferenceRow("preference2", "2");
        PreferenceRow pr3 = createI18NPreferenceRow("preference3", "3");
        PreferenceRow pr4 = createI18NPreferenceRow("preference4", "4");
        PreferenceRow pr5 = createI18NPreferenceRow("preference5", "5");
        preferenceTable.addChild(pr1);
        preferenceTable.addChild(pr2);
        preferenceTable.addChild(pr3);
        preferenceTable.addChild(pr4);
        preferenceTable.addChild(pr5);
        preferenceTable.setVerboseHelp(FormConstants.VERBOSE_HELP);

        hakutoiveetRyhma.addChild(preferenceTable);
    }

    public static PreferenceRow createI18NPreferenceRow(final String id, final String title) {


        PreferenceRow pr = new PreferenceRow(id,
                createI18NForm("form.hakutoiveet.hakutoive", title),
                createI18NForm("form.yleinen.tyhjenna"),
                createI18NForm("form.hakutoiveet.koulutus"),
                createI18NForm("form.hakutoiveet.opetuspiste"),
                createI18NForm("form.hakutoiveet.sisaltyvatKoulutusohjelmat"),
                "Valitse koulutus");


        pr.addChild(
                Yhteishaku2013.createDiscretionaryQuestionsAndRules(id),
                Yhteishaku2013.createSoraQuestions(id),
                Yhteishaku2013.createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(id));
        return pr;
    }

    private static Element createDiscretionaryQuestionsAndRules(final String index) {
        Radio discretionary = new Radio(index + "-discretionary", createI18NForm("form.hakutoiveet.harkinnanvarainen"));
        addDefaultTrueFalseOptions(discretionary);
        setRequired(discretionary);

        DropdownSelect discretionaryFollowUp = new DropdownSelect(discretionary.getId() + "-follow-up",
                createI18NForm("form.hakutoiveet.harkinnanvarainen.perustelu"), null);
        discretionaryFollowUp.addOption(discretionaryFollowUp.getId() + "oppimisvaikudet",
                createI18NForm("form.hakutoiveet.harkinnanvarainen.perustelu.oppimisvaikeudet"), "oppimisvaikudet");
        discretionaryFollowUp.addOption(discretionaryFollowUp.getId() + "sosiaalisetsyyt",
                createI18NForm("form.hakutoiveet.harkinnanvarainen.perustelu.sosiaaliset"), "sosiaalisetsyyt");

        RelatedQuestionRule discretionaryFollowUpRule = new RelatedQuestionRule(index + "-discretionary-follow-up-rule",
                ImmutableList.of(discretionary.getId()), Boolean.TRUE.toString().toLowerCase(), false);
        discretionaryFollowUpRule.addChild(discretionaryFollowUp);


        discretionary.addChild(discretionaryFollowUpRule);

        RelatedQuestionRule discretionaryRule = new RelatedQuestionRule(index + "-discretionary-rule",
                ImmutableList.of(index + "-Koulutus-educationDegree"), DISCRETIONARY_EDUCATION_DEGREE, false);
        discretionaryRule.addChild(discretionary);

        return discretionaryRule;

    }

    private void createTyokokemus(Theme tyokokemus) {
        tyokokemus.setHelp(createI18NForm("form.tyokokemus.help"));
        TextQuestion tyokokemuskuukaudet = new TextQuestion("TYOKOKEMUSKUUKAUDET",
                createI18NForm("form.tyokokemus.kuukausina"));
        tyokokemuskuukaudet
                .setHelp(createI18NForm("form.tyokokemus.kuukausina.help"));
        tyokokemuskuukaudet.addAttribute("placeholder", "kuukautta");
        tyokokemuskuukaudet.addAttribute("pattern", "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$");
        tyokokemuskuukaudet.addAttribute("size", "8");
        tyokokemuskuukaudet.setVerboseHelp(FormConstants.VERBOSE_HELP);
        tyokokemus.addChild(tyokokemuskuukaudet);
    }

    private void createLupatiedot(final Theme lupatiedot) {

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
        lupatiedot.setVerboseHelp(FormConstants.VERBOSE_HELP);

        Radio asiointikieli = new Radio("asiointikieli", createI18NForm("form.asiointikieli.otsikko"));
        asiointikieli.setHelp(createI18NForm("form.asiointikieli.help"));
        asiointikieli.addOption("suomi", createI18NForm("form.asiointikieli.suomi"), "suomi");
        asiointikieli.addOption("ruotsi", createI18NForm("form.asiointikieli.ruotsi"), "ruotsi");
        asiointikieli.addAttribute("required", "required");
        asiointikieli.setVerboseHelp(FormConstants.VERBOSE_HELP);
        lupatiedot.addChild(asiointikieli);
    }

    private void createKoulutustausta(Theme koulutustaustaRyhma) {
        koulutustaustaRyhma
                .setHelp(createI18NForm("form.koulutustausta.help"));

        Radio osallistunut = new Radio("osallistunut",
                createI18NForm("form.koulutustausta.osallistunutPaasykokeisiin"));
        addDefaultTrueFalseOptions(osallistunut);
        setRequired(osallistunut);
        osallistunut.setVerboseHelp(FormConstants.VERBOSE_HELP);

        koulutustaustaRyhma.addChild(createKoulutustaustaRadio());

        koulutustaustaRyhma.addChild(osallistunut);
    }

    public Radio createKoulutustaustaRadio() { //NOSONAR
        Radio millatutkinnolla = new Radio("POHJAKOULUTUS",
                createI18NForm("form.koulutustausta.millaTutkinnolla"));
        millatutkinnolla.addOption(TUTKINTO_PERUSKOULU, createI18NForm("form.koulutustausta.peruskoulu"), PERUSKOULU,
                createI18NForm("form.koulutustausta.peruskoulu.help"));
        millatutkinnolla
                .addOption(TUTKINTO_OSITTAIN_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.osittainYksilollistetty"),
                        OSITTAIN_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.osittainYksilollistetty.help"));
        millatutkinnolla
                .addOption(
                        TUTKINTO_ERITYISOPETUKSEN_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.erityisopetuksenYksilollistetty"),
                        ERITYISOPETUKSEN_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.erityisopetuksenYksilollistetty.help"));
        millatutkinnolla
                .addOption(
                        TUTKINTO_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.yksilollistetty"),
                        YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.yksilollistetty.help"));
        millatutkinnolla.addOption(TUTKINTO_KESKEYTYNYT,
                createI18NForm("form.koulutustausta.keskeytynyt"),
                KESKEYTYNYT,
                createI18NForm("form.koulutustausta.keskeytynyt"));
        millatutkinnolla
                .addOption(
                        TUTKINTO_YLIOPPILAS,
                        createI18NForm("form.koulutustausta.lukio"),
                        YLIOPPILAS,
                        createI18NForm("form.koulutustausta.lukio.help"));
        millatutkinnolla.addOption(TUTKINTO_ULKOMAINEN_TUTKINTO, createI18NForm("form.koulutustausta.ulkomailla"),
                ULKOMAINEN_TUTKINTO,
                createI18NForm("form.koulutustausta.ulkomailla.help"));
        millatutkinnolla.setVerboseHelp(FormConstants.VERBOSE_HELP);
        millatutkinnolla.addAttribute("required", "required");

        Notification tutkintoUlkomaillaNotification = new Notification(TUTKINTO_ULKOMAILLA_NOTIFICATION_ID,
                createI18NForm("form.koulutustausta.ulkomailla.huom"),
                Notification.NotificationType.INFO);

        Notification tutkintoKeskeytynytNotification = new Notification(TUTKINTO_KESKEYTNYT_NOTIFICATION_ID,
                createI18NForm("form.koulutustausta.keskeytynyt.huom"),
                Notification.NotificationType.INFO);

        RelatedQuestionRule keskeytynytRule = new RelatedQuestionRule(TUTKINTO_KESKEYTYNYT_RULE,
                millatutkinnolla.getId(), KESKEYTYNYT, false);

        RelatedQuestionRule ulkomaillaSuoritettuTutkintoRule = new RelatedQuestionRule(TUTKINTO_ULKOMAILLA_RULE,
                millatutkinnolla.getId(), ULKOMAINEN_TUTKINTO, false);

        ulkomaillaSuoritettuTutkintoRule.addChild(tutkintoUlkomaillaNotification);
        keskeytynytRule.addChild(tutkintoKeskeytynytNotification);
        millatutkinnolla.addChild(ulkomaillaSuoritettuTutkintoRule);
        millatutkinnolla.addChild(keskeytynytRule);

        TextQuestion paattotodistusvuosiPeruskoulu = new TextQuestion("PK_PAATTOTODISTUSVUOSI",
                createI18NForm("form.koulutustausta.paattotodistusvuosi"));
        paattotodistusvuosiPeruskoulu.addAttribute("placeholder", "vvvv");
        paattotodistusvuosiPeruskoulu.addAttribute("required", "required");
        paattotodistusvuosiPeruskoulu.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        paattotodistusvuosiPeruskoulu.addAttribute("size", "4");
        paattotodistusvuosiPeruskoulu.addAttribute("maxlength", "4");

        Group suorittanutGroup = new Group("suorittanutgroup",
                createI18NForm("form.koulutustausta.suorittanut"));
        suorittanutGroup.addChild(
                new CheckBox("LISAKOULUTUS_KYMPPI", createI18NForm("form.koulutustausta.kymppiluokka")),
                new CheckBox("LISAKOULUTUS_VAMMAISTEN", createI18NForm("form.koulutustausta.vammaistenValmentava")),
                new CheckBox("LISAKOULUTUS_TALOUS", createI18NForm("form.koulutustausta.talouskoulu")),
                new CheckBox("LISAKOULUTUS_AMMATTISTARTTI", createI18NForm("form.koulutustausta.ammattistartti")),
                new CheckBox("LISAKOULUTUS_KANSANOPISTO", createI18NForm("form.koulutustausta.kansanopisto")),
                new CheckBox("LISAKOULUTUS_MAAHANMUUTTO", createI18NForm("form.koulutustausta.maahanmuuttajienValmistava"))
        );

        RelatedQuestionRule pkKysymyksetRule = new RelatedQuestionRule("rule3", millatutkinnolla.getId(), "("
                + PERUSKOULU + "|"
                + OSITTAIN_YKSILOLLISTETTY + "|"
                + ERITYISOPETUKSEN_YKSILOLLISTETTY + "|"
                + YKSILOLLISTETTY + ")", false);

        RelatedQuestionRule paattotodistusvuosiPeruskouluRule = new RelatedQuestionRule("rule8",
                paattotodistusvuosiPeruskoulu.getId(), "^(19[0-9][0-9]|200[0-9]|201[0-1])$", false);

        Radio koulutuspaikkaAmmatillisenTutkintoon = new Radio(
                "KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON",
                createI18NForm("form.koulutustausta.ammatillinenKoulutuspaikka"));
        addDefaultTrueFalseOptions(koulutuspaikkaAmmatillisenTutkintoon);
        setRequired(koulutuspaikkaAmmatillisenTutkintoon);

        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskoulu);
        pkKysymyksetRule.addChild(suorittanutGroup);
        pkKysymyksetRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);
        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskouluRule);

        TextQuestion lukioPaattotodistusVuosi = new TextQuestion("lukioPaattotodistusVuosi",
                createI18NForm("form.koulutustausta.lukio.paattotodistusvuosi"));
        lukioPaattotodistusVuosi.addAttribute("placeholder", "vvvv");
        lukioPaattotodistusVuosi.addAttribute("required", "required");
        lukioPaattotodistusVuosi.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        lukioPaattotodistusVuosi.addAttribute("size", "4");
        lukioPaattotodistusVuosi.addAttribute("maxlength", "4");
        lukioPaattotodistusVuosi.setInline(true);

        DropdownSelect ylioppilastutkinto = new DropdownSelect("ylioppilastutkinto",
                createI18NForm("form.koulutustausta.lukio.yotutkinto"), null);
        ylioppilastutkinto.addOption("fi", createI18NForm("form.koulutustausta.lukio.yotutkinto.fi"), "fi");
        ylioppilastutkinto.addOption("ib", createI18NForm("form.koulutustausta.lukio.yotutkinto.ib"), "ib");
        ylioppilastutkinto.addOption("eb", createI18NForm("form.koulutustausta.lukio.yotutkinto.eb"), "eb");
        ylioppilastutkinto.addOption("rp", createI18NForm("form.koulutustausta.lukio.yotutkinto.rp"), "rp");
        ylioppilastutkinto.addAttribute("required", "required");
        ylioppilastutkinto.setInline(true);
        setDefaultOption("fi", ylioppilastutkinto.getOptions());

        Group lukioGroup = new Group("lukioGroup", createI18NForm("form.koulutustausta.lukio.suoritus"));
        lukioGroup.addChild(lukioPaattotodistusVuosi);
        lukioGroup.addChild(ylioppilastutkinto);

        RelatedQuestionRule lukioRule = new RelatedQuestionRule("rule7", millatutkinnolla.getId(), YLIOPPILAS, false);
        lukioRule.addChild(lukioGroup);

        millatutkinnolla.addChild(lukioRule);
        millatutkinnolla.addChild(pkKysymyksetRule);

        Radio suorittanutAmmatillisenTutkinnon = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NForm("form.koulutustausta.ammatillinenSuoritettu"));
        addDefaultTrueFalseOptions(suorittanutAmmatillisenTutkinnon);
        setRequired(suorittanutAmmatillisenTutkinnon);


        lukioRule.addChild(suorittanutAmmatillisenTutkinnon);
        lukioRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);
        paattotodistusvuosiPeruskouluRule.addChild(suorittanutAmmatillisenTutkinnon);

        RelatedQuestionRule suorittanutAmmatillisenTutkinnonRule = new RelatedQuestionRule("rule9",
                suorittanutAmmatillisenTutkinnon.getId(), "^true", false);
        Notification notification1 = new Notification(
                "notification1",
                createI18NForm("form.koulutustausta.ammatillinenKoulutuspaikka.huom"),
                Notification.NotificationType.INFO);

        suorittanutAmmatillisenTutkinnonRule.addChild(notification1);
        suorittanutAmmatillisenTutkinnon.addChild(suorittanutAmmatillisenTutkinnonRule);

        DropdownSelect perusopetuksenKieli = new DropdownSelect("perusopetuksen_kieli",
                createI18NForm("Millä opetuskielellä olet suorittanut perusopetuksen?"), null);
        perusopetuksenKieli.addOption("eiValittu", ElementUtil.createI18NForm(null), "");
        perusopetuksenKieli.addOptions(koodistoService.getLanguages());
        perusopetuksenKieli.addAttribute("required", "required");
        perusopetuksenKieli.setVerboseHelp(FormConstants.VERBOSE_HELP);
        perusopetuksenKieli.setHelp(createI18NForm("form.henkilotiedot.aidinkieli.help"));
        pkKysymyksetRule.addChild(perusopetuksenKieli);
        return millatutkinnolla;
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

    private void setRequiredInlineAndVerboseHelp(Question question) {
        question.addAttribute("required", "required");
        question.setVerboseHelp(FormConstants.VERBOSE_HELP);
        question.setInline(true);
    }

    public KoodistoService getKoodistoService() {
        return koodistoService;
    }
}
