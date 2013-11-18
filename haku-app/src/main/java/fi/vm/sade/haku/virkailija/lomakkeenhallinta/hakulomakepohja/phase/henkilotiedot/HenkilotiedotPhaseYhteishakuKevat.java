/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.PostOffice;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PostalCode;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.*;
import fi.vm.sade.haku.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ContainedInOtherFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public final class
        HenkilotiedotPhaseYhteishakuKevat {

    public static final String MOBILE_PHONE_PATTERN =
            "^$|^(?!\\+358|0)[\\+]?[0-9\\-\\s]+$|^(\\+358|0)[\\-\\s]*((4[\\-\\s]*[0-6])|50)[0-9\\-\\s]*$";
    public static final String PHONE_PATTERN = "^$|^\\+?[0-9\\-\\s]+$";
    private static final String NOT_FI = "^((?!FIN)[A-Z]{3})$";
    public static final String AIDINKIELI_ID = "aidinkieli";
    private static final String HETU_PATTERN = "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$";
    private static final String POSTINUMERO_PATTERN = "[0-9]{5}";
    private static final String DATE_PATTERN = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.(19|20)\\d\\d$";

    private static final String FORM_MESSAGES = "form_messages_yhteishaku_kevat";
    private static final String FORM_ERRORS = "form_errors_yhteishaku_kevat";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_yhteishaku_kevat";

    private HenkilotiedotPhaseYhteishakuKevat() {
    }

    public static Phase create(final KoodistoService koodistoService) {

        // Henkilötiedot
        Phase henkilotiedot = new Phase("henkilotiedot", createI18NText("form.henkilotiedot.otsikko",
                FORM_MESSAGES), false);

        Theme henkilotiedotRyhma = new Theme("HenkilotiedotGrp", createI18NText("form.henkilotiedot.otsikko",
                FORM_MESSAGES), true);

        // Nimet
        Question sukunimi = createRequiredTextQuestion("Sukunimi", "form.henkilotiedot.sukunimi", FORM_MESSAGES, FORM_ERRORS,
                30);
        sukunimi.setInline(true);
        sukunimi.setValidator(createRegexValidator(sukunimi.getId(), ElementUtil.ISO88591_NAME_REGEX, FORM_ERRORS));
        henkilotiedotRyhma.addChild(sukunimi);

        Question etunimet = createRequiredTextQuestion("Etunimet", "form.henkilotiedot.etunimet", FORM_MESSAGES, FORM_ERRORS,
                30);
        etunimet.setInline(true);
        etunimet.setValidator(createRegexValidator(etunimet.getId(), ElementUtil.ISO88591_NAME_REGEX, FORM_ERRORS));
        henkilotiedotRyhma.addChild(etunimet);

        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", createI18NText("form.henkilotiedot.kutsumanimi",
                FORM_MESSAGES));
        kutsumanimi.setHelp(createI18NText("form.henkilotiedot.kutsumanimi.help", FORM_MESSAGES));
        addSizeAttribute(kutsumanimi, 20);
        kutsumanimi.setValidator(
                new ContainedInOtherFieldValidator(kutsumanimi.getId(),
                        etunimet.getId(),
                        ElementUtil.createI18NText("yleinen.virheellinenArvo", FORM_ERRORS)));
        kutsumanimi.setValidator(
                createRegexValidator(kutsumanimi.getId(), ISO88591_NAME_REGEX, FORM_ERRORS));
        setRequiredInlineAndVerboseHelp(kutsumanimi, "form.henkilotiedot.kutsumanimi.verboseHelp", FORM_VERBOSE_HELP,
                FORM_ERRORS);

        henkilotiedotRyhma.addChild(kutsumanimi);

        // Kansalaisuus, hetu ja sukupuoli suomalaisille
        DropdownSelect kansalaisuus =
                new DropdownSelect("kansalaisuus", createI18NText("form.henkilotiedot.kansalaisuus",
                        FORM_MESSAGES), null);
        kansalaisuus.addOptions(koodistoService.getNationalities());
        setDefaultOption("FIN", kansalaisuus.getOptions());
        kansalaisuus.setHelp(createI18NText("form.henkilotiedot.kansalaisuus.help", FORM_MESSAGES));
        setRequiredInlineAndVerboseHelp(kansalaisuus, "form.henkilotiedot.kansalaisuus.verboseHelp", FORM_VERBOSE_HELP,
                FORM_ERRORS);
        henkilotiedotRyhma.addChild(kansalaisuus);

        TextQuestion henkilotunnus =
                new TextQuestion("Henkilotunnus", createI18NText("form.henkilotiedot.henkilotunnus",
                        FORM_MESSAGES));
        henkilotunnus.addAttribute("placeholder", "ppkkvv*****");
        addSizeAttribute(henkilotunnus, 11);
        henkilotunnus.addAttribute("maxlength", "11");
        henkilotunnus.setHelp(createI18NText("form.henkilotiedot.henkilotunnus.help", FORM_MESSAGES));
        henkilotunnus.setValidator(createRegexValidator(henkilotunnus.getId(), HETU_PATTERN, FORM_ERRORS));
        setRequiredInlineAndVerboseHelp(henkilotunnus, "form.henkilotiedot.henkilotunnus.verboseHelp", FORM_VERBOSE_HELP,
                FORM_ERRORS);

        Radio sukupuoli = new Radio("sukupuoli", createI18NText("form.henkilotiedot.sukupuoli",
                FORM_MESSAGES));
        sukupuoli.addOptions(koodistoService.getGenders());
        setRequiredInlineAndVerboseHelp(sukupuoli, "form.henkilotiedot.sukupuoli.verboseHelp", FORM_VERBOSE_HELP,
                FORM_ERRORS);

        Option male = sukupuoli.getOptions().get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Mies") ?
                sukupuoli.getOptions().get(0) : sukupuoli.getOptions().get(1);
        Option female = sukupuoli.getOptions().get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Nainen") ?
                sukupuoli.getOptions().get(0) : sukupuoli.getOptions().get(1);
        SocialSecurityNumber socialSecurityNumber =
                new SocialSecurityNumber("ssn_question", createI18NText("form.henkilotiedot.hetu",
                        FORM_MESSAGES),
                        sukupuoli.getI18nText(), male, female, sukupuoli.getId(), henkilotunnus);
        addApplicationUniqueValidator(henkilotunnus, OppijaConstants.VARSINAINEN_HAKU);

        RelatedQuestionRule hetuRule = new RelatedQuestionRule("hetuRule", kansalaisuus.getId(), "^$|^FIN$", true);
        hetuRule.addChild(socialSecurityNumber);
        henkilotiedotRyhma.addChild(hetuRule);

        // Ulkomaalaisten tunnisteet
        Radio onkoSinullaSuomalainenHetu = new Radio("onkoSinullaSuomalainenHetu",
                createI18NText("form.henkilotiedot.hetu.onkoSuomalainen", FORM_MESSAGES));
        addDefaultTrueFalseOptions(onkoSinullaSuomalainenHetu, FORM_MESSAGES);
        setRequiredInlineAndVerboseHelp(onkoSinullaSuomalainenHetu, "form.henkilotiedot.hetu.onkoSuomalainen.verboseHelp",
                FORM_VERBOSE_HELP, FORM_ERRORS);
        RelatedQuestionRule suomalainenHetuRule = new RelatedQuestionRule("suomalainenHetuRule",
                onkoSinullaSuomalainenHetu.getId(), "^true$", false);
        suomalainenHetuRule.addChild(socialSecurityNumber);
        onkoSinullaSuomalainenHetu.addChild(suomalainenHetuRule);

        RelatedQuestionRule eiSuomalaistaHetuaRule = new RelatedQuestionRule("eiSuomalaistaHetuaRule",
                onkoSinullaSuomalainenHetu.getId(), "^false$", false);
        eiSuomalaistaHetuaRule.addChild(sukupuoli);

        DateQuestion syntymaaika = new DateQuestion("syntymaaika", createI18NText("form.henkilotiedot.syntymaaika",
                FORM_MESSAGES));
        syntymaaika.setValidator(ElementUtil.createRegexValidator(syntymaaika.getId(), DATE_PATTERN, FORM_ERRORS));
        addRequiredValidator(syntymaaika, FORM_ERRORS);
        syntymaaika.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymaaika);

        TextQuestion syntymapaikka =
                new TextQuestion("syntymapaikka", createI18NText("form.henkilotiedot.syntymapaikka",
                        FORM_MESSAGES));
        addSizeAttribute(syntymapaikka, 30);
        addRequiredValidator(syntymapaikka, FORM_ERRORS);
        syntymapaikka.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymapaikka);

        TextQuestion kansallinenIdTunnus =
                new TextQuestion("kansallinenIdTunnus", createI18NText("form.henkilotiedot.kansallinenId",
                        FORM_MESSAGES));
        addSizeAttribute(kansallinenIdTunnus, 30);
        kansallinenIdTunnus.setInline(true);
        eiSuomalaistaHetuaRule.addChild(kansallinenIdTunnus);

        TextQuestion passinnumero = new TextQuestion("passinnumero", createI18NText("form.henkilotiedot.passinnumero",
                FORM_MESSAGES));
        addSizeAttribute(passinnumero, 30);
        passinnumero.setInline(true);
        eiSuomalaistaHetuaRule.addChild(passinnumero);

        onkoSinullaSuomalainenHetu.addChild(eiSuomalaistaHetuaRule);

        RelatedQuestionRule ulkomaalaisenTunnisteetRule = new RelatedQuestionRule("ulkomaalaisenTunnisteetRule",
                kansalaisuus.getId(), NOT_FI, false);
        ulkomaalaisenTunnisteetRule.addChild(onkoSinullaSuomalainenHetu);
        henkilotiedotRyhma.addChild(ulkomaalaisenTunnisteetRule);

        // Email
        TextQuestion email = new TextQuestion("Sähköposti", createI18NText("form.henkilotiedot.email",
                FORM_MESSAGES));
        addSizeAttribute(email, 50);
        email.setValidator(createRegexValidator(email.getId(), EMAIL_REGEX, FORM_ERRORS));
        email.setHelp(createI18NText("form.henkilotiedot.email.help", FORM_MESSAGES));
        ElementUtil.setVerboseHelp(email, "form.henkilotiedot.email.verboseHelp", FORM_VERBOSE_HELP);
        email.setInline(true);
        henkilotiedotRyhma.addChild(email);

        // Matkapuhelinnumerot

        TextQuestion puhelinnumero1 = new TextQuestion("matkapuhelinnumero1",
                createI18NText("form.henkilotiedot.matkapuhelinnumero", FORM_MESSAGES));
        puhelinnumero1.setHelp(createI18NText("form.henkilotiedot.matkapuhelinnumero.help",
                FORM_MESSAGES));
        addSizeAttribute(puhelinnumero1, 30);
        puhelinnumero1.setValidator(createRegexValidator(puhelinnumero1.getId(), MOBILE_PHONE_PATTERN, FORM_ERRORS));
        ElementUtil.setVerboseHelp(puhelinnumero1, "form.henkilotiedot.matkapuhelinnumero.verboseHelp", FORM_VERBOSE_HELP);
        puhelinnumero1.setInline(true);
        henkilotiedotRyhma.addChild(puhelinnumero1);

        TextQuestion prevNum = puhelinnumero1;
        AddElementRule prevRule = null;
        for (int i = 2; i <= 5; i++) {
            TextQuestion extranumero = new TextQuestion("matkapuhelinnumero" + i,
                    createI18NText("form.henkilotiedot.puhelinnumero", FORM_MESSAGES));
            extranumero.addAttribute("size", "30");
            addSizeAttribute(extranumero, 30);
            extranumero.setValidator(createRegexValidator(extranumero.getId(), PHONE_PATTERN, FORM_ERRORS));
            extranumero.setInline(true);

            AddElementRule extranumeroRule = new AddElementRule("addPuhelinnumero" + i + "Rule", prevNum.getId(),
                    createI18NText("form.henkilotiedot.puhelinnumero.lisaa", FORM_MESSAGES));
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
        DropdownSelect asuinmaa = new DropdownSelect("asuinmaa", createI18NText("form.henkilotiedot.asuinmaa",
                FORM_MESSAGES), null);
        asuinmaa.addOptions(koodistoService.getCountries());
        setDefaultOption("FIN", asuinmaa.getOptions());
        setRequiredInlineAndVerboseHelp(asuinmaa, "form.henkilotiedot.asuinmaa.verboseHelp", FORM_VERBOSE_HELP,
                FORM_ERRORS);

        RelatedQuestionRule asuinmaaFI = new RelatedQuestionRule("rule1", asuinmaa.getId(), "FIN", true);
        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "form.henkilotiedot.lahiosoite", FORM_MESSAGES,
                FORM_ERRORS, 40);
        lahiosoite.setInline(true);
        asuinmaaFI.addChild(lahiosoite);

        Element postinumero = new PostalCode("Postinumero", createI18NText("form.henkilotiedot.postinumero",
                FORM_MESSAGES),
                createPostOffices(koodistoService));
        addSizeAttribute(postinumero, 5);
        postinumero.addAttribute("maxlength", "5");
        addRequiredValidator(postinumero, FORM_ERRORS);
        postinumero.addAttribute("placeholder", "#####");
        postinumero.setValidator(createRegexValidator(postinumero.getId(), POSTINUMERO_PATTERN, FORM_ERRORS));
        postinumero.setHelp(createI18NText("form.henkilotiedot.postinumero.help", FORM_MESSAGES));
        asuinmaaFI.addChild(postinumero);

        DropdownSelect kotikunta =
                new DropdownSelect("kotikunta", createI18NText("form.henkilotiedot.kotikunta",
                        FORM_MESSAGES), null);
        kotikunta.addOption(ElementUtil.createI18NAsIs(""), "");
        kotikunta.addOptions(koodistoService.getMunicipalities());
        setRequiredInlineAndVerboseHelp(kotikunta, "form.henkilotiedot.kotikunta.verboseHelp", FORM_VERBOSE_HELP,
                FORM_ERRORS);
        kotikunta.setHelp(createI18NText("form.henkilotiedot.kotikunta.help", FORM_MESSAGES));
        asuinmaaFI.addChild(kotikunta);

        /*CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1",
                createI18NForm("form.henkilotiedot.ensisijainenOsoite"));
        ensisijainenOsoite.setInline(true);
        asuinmaaFI.addChild(ensisijainenOsoite);*/

        RelatedQuestionRule relatedQuestionRule2 =
                new RelatedQuestionRule("rule2", asuinmaa.getId(), NOT_FI, false);
        Question osoiteUlkomaa = createRequiredTextQuestion("osoiteUlkomaa", "form.henkilotiedot.osoite", FORM_MESSAGES,
                FORM_ERRORS, 40);
        osoiteUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(osoiteUlkomaa);
        Question postinumeroUlkomaa = createRequiredTextQuestion("postinumeroUlkomaa", "form.henkilotiedot.postinumero",
                FORM_MESSAGES, FORM_ERRORS, 12);
        postinumeroUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(postinumeroUlkomaa);
        Question kaupunkiUlkomaa = createRequiredTextQuestion("kaupunkiUlkomaa", "form.henkilotiedot.kaupunki",
                FORM_MESSAGES, FORM_ERRORS, 25);
        kaupunkiUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(kaupunkiUlkomaa);

        asuinmaa.addChild(relatedQuestionRule2);
        asuinmaa.addChild(asuinmaaFI);

        henkilotiedotRyhma.addChild(asuinmaa);

        // Äidinkieli
        DropdownSelect aidinkieli =
                new DropdownSelect(AIDINKIELI_ID, createI18NText("form.henkilotiedot.aidinkieli",
                        FORM_MESSAGES),
                        "fi_vm_sade_oppija_language");
        aidinkieli.addOption(ElementUtil.createI18NAsIs(""), "");
        aidinkieli.addOptions(koodistoService.getLanguages());
        setRequiredInlineAndVerboseHelp(aidinkieli, "form.henkilotiedot.aidinkieli.verboseHelp", FORM_VERBOSE_HELP,
                FORM_ERRORS);
        aidinkieli.setHelp(createI18NText("form.henkilotiedot.aidinkieli.help", FORM_MESSAGES));
        henkilotiedotRyhma.addChild(aidinkieli);

        henkilotiedot.addChild(henkilotiedotRyhma);
        return henkilotiedot;
    }

    private static Map<String, PostOffice> createPostOffices(final KoodistoService koodistoService) {
        List<PostOffice> listOfPostOffices = koodistoService.getPostOffices();
        Map<String, PostOffice> postOfficeMap = new HashMap<String, PostOffice>(listOfPostOffices.size());
        for (PostOffice postOffice : listOfPostOffices) {
            postOfficeMap.put(postOffice.getPostcode(), postOffice);
        }
        return ImmutableMap.copyOf(postOfficeMap);
    }
}
