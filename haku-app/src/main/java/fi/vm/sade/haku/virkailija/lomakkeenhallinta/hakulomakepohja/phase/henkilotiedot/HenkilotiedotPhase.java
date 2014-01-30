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

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PostalCode;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.*;
import fi.vm.sade.haku.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ContainedInOtherFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public final class
  HenkilotiedotPhase {

    public static final String MOBILE_PHONE_PATTERN =
            "^$|^(?!\\+358|0)[\\+]?[0-9\\-\\s]+$|^(\\+358|0)[\\-\\s]*((4[\\-\\s]*[0-6])|50)[0-9\\-\\s]*$";
    public static final String PHONE_PATTERN = "^$|^\\+?[0-9\\-\\s]+$";
    private static final String NOT_FI = "^((?!FIN)[A-Z]{3})$";
    public static final String AIDINKIELI_ID = "aidinkieli";
    private static final String HETU_PATTERN = "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$";
    private static final String POSTINUMERO_PATTERN = "[0-9]{5}";
    private static final String DATE_PATTERN = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.(19|20)\\d\\d$";
    public static final String EMPTY_OR_FIN_PATTERN = "^$|^FIN$";

    private HenkilotiedotPhase() {
    }

    public static Phase create(final String applicationType,final KoodistoService koodistoService, final String formMessagesBundle, final String formErrorsBundle, final String formVerboseHelpBundle) {

        // Henkilötiedot
        Phase henkilotiedot = new Phase("henkilotiedot", createI18NText("form.henkilotiedot.otsikko",
          formMessagesBundle), false, Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));

        Theme henkilotiedotRyhma = new Theme("HenkilotiedotGrp", createI18NText("form.henkilotiedot.otsikko",
          formMessagesBundle), true);

        // Nimet
        Question sukunimi = createNameQuestion("Sukunimi", "form.henkilotiedot.sukunimi", formMessagesBundle, formErrorsBundle, 30);
        henkilotiedotRyhma.addChild(sukunimi);

        Question etunimet = createNameQuestion("Etunimet", "form.henkilotiedot.etunimet", formMessagesBundle, formErrorsBundle, 30);
        henkilotiedotRyhma.addChild(etunimet);

        Question kutsumanimi = createCallingNameQuestion(formMessagesBundle, formErrorsBundle, formVerboseHelpBundle, 20);
        kutsumanimi.setValidator(
                new ContainedInOtherFieldValidator(kutsumanimi.getId(),
                        etunimet.getId(),
                        ElementUtil.createI18NText("yleinen.virheellinenArvo", formErrorsBundle)));

        henkilotiedotRyhma.addChild(kutsumanimi);


        // Kansalaisuus, hetu ja sukupuoli suomalaisille
        DropdownSelect kansalaisuus =
                new DropdownSelect("kansalaisuus", createI18NText("form.henkilotiedot.kansalaisuus",
                  formMessagesBundle), null);
        kansalaisuus.addOptions(koodistoService.getNationalities());
        setDefaultOption("FIN", kansalaisuus.getOptions());
        kansalaisuus.setHelp(createI18NText("form.henkilotiedot.kansalaisuus.help", formMessagesBundle));
        setRequiredInlineAndVerboseHelp(kansalaisuus, "form.henkilotiedot.kansalaisuus.verboseHelp", formVerboseHelpBundle,
          formErrorsBundle);
        henkilotiedotRyhma.addChild(kansalaisuus);

        TextQuestion henkilotunnus =
                new TextQuestion("Henkilotunnus", createI18NText("form.henkilotiedot.henkilotunnus",
                  formMessagesBundle));
        henkilotunnus.addAttribute("placeholder", "ppkkvv*****");
        addSizeAttribute(henkilotunnus, 11);
        henkilotunnus.addAttribute("maxlength", "11");
        henkilotunnus.setHelp(createI18NText("form.henkilotiedot.henkilotunnus.help", formMessagesBundle));
        henkilotunnus.setValidator(createRegexValidator(henkilotunnus.getId(), HETU_PATTERN, formErrorsBundle));
        setRequiredInlineAndVerboseHelp(henkilotunnus, "form.henkilotiedot.henkilotunnus.verboseHelp", formVerboseHelpBundle,
          formErrorsBundle);

        Radio sukupuoli = new Radio("sukupuoli", createI18NText("form.henkilotiedot.sukupuoli",
          formMessagesBundle));
        sukupuoli.addOptions(koodistoService.getGenders());
        setRequiredInlineAndVerboseHelp(sukupuoli, "form.henkilotiedot.sukupuoli.verboseHelp", formVerboseHelpBundle,
          formErrorsBundle);

        Option male = sukupuoli.getOptions().get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Mies") ?
                sukupuoli.getOptions().get(0) : sukupuoli.getOptions().get(1);
        Option female = sukupuoli.getOptions().get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Nainen") ?
                sukupuoli.getOptions().get(0) : sukupuoli.getOptions().get(1);
        SocialSecurityNumber socialSecurityNumber =
                new SocialSecurityNumber("ssn_question", createI18NText("form.henkilotiedot.hetu",
                  formMessagesBundle),
                        sukupuoli.getI18nText(), male, female, sukupuoli.getId(), henkilotunnus);
        addUniqueApplicantValidator(henkilotunnus, applicationType);

        RelatedQuestionComplexRule hetuRule = createRegexpRule(kansalaisuus, EMPTY_OR_FIN_PATTERN);
        hetuRule.addChild(socialSecurityNumber);
        henkilotiedotRyhma.addChild(hetuRule);

        // Ulkomaalaisten tunnisteet
        Radio onkoSinullaSuomalainenHetu = new Radio("onkoSinullaSuomalainenHetu",
                createI18NText("form.henkilotiedot.hetu.onkoSuomalainen", formMessagesBundle));
        addDefaultTrueFalseOptions(onkoSinullaSuomalainenHetu, formMessagesBundle);
        setRequiredInlineAndVerboseHelp(onkoSinullaSuomalainenHetu, "form.henkilotiedot.hetu.onkoSuomalainen.verboseHelp",
          formVerboseHelpBundle, formErrorsBundle);
        RelatedQuestionComplexRule suomalainenHetuRule = createRuleIfVariableIsTrue("onSuomalainenHetu", onkoSinullaSuomalainenHetu.getId());
        suomalainenHetuRule.addChild(socialSecurityNumber);
        onkoSinullaSuomalainenHetu.addChild(suomalainenHetuRule);

        RelatedQuestionComplexRule eiSuomalaistaHetuaRule = createRuleIfVariableIsFalse("eiOleSuomalaistaHetua", onkoSinullaSuomalainenHetu.getId());
        eiSuomalaistaHetuaRule.addChild(sukupuoli);

        DateQuestion syntymaaika = new DateQuestion("syntymaaika", createI18NText("form.henkilotiedot.syntymaaika",
                formMessagesBundle));
        syntymaaika.setValidator(ElementUtil.createRegexValidator(syntymaaika.getId(), DATE_PATTERN, formErrorsBundle));
        syntymaaika.setValidator(ElementUtil.createDateOfBirthValidator(syntymaaika.getId(), formErrorsBundle));
        addRequiredValidator(syntymaaika, formErrorsBundle);
        syntymaaika.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymaaika);

        TextQuestion syntymapaikka =
                new TextQuestion("syntymapaikka", createI18NText("form.henkilotiedot.syntymapaikka",
                  formMessagesBundle));
        addSizeAttribute(syntymapaikka, 30);
        addRequiredValidator(syntymapaikka, formErrorsBundle);
        syntymapaikka.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymapaikka);

        TextQuestion kansallinenIdTunnus =
                new TextQuestion("kansallinenIdTunnus", createI18NText("form.henkilotiedot.kansallinenId",
                  formMessagesBundle));
        addSizeAttribute(kansallinenIdTunnus, 30);
        kansallinenIdTunnus.setInline(true);
        eiSuomalaistaHetuaRule.addChild(kansallinenIdTunnus);

        TextQuestion passinnumero = new TextQuestion("passinnumero", createI18NText("form.henkilotiedot.passinnumero",
          formMessagesBundle));
        addSizeAttribute(passinnumero, 30);
        passinnumero.setInline(true);
        eiSuomalaistaHetuaRule.addChild(passinnumero);

        onkoSinullaSuomalainenHetu.addChild(eiSuomalaistaHetuaRule);

        RelatedQuestionComplexRule ulkomaalaisenTunnisteetRule = createRegexpRule(kansalaisuus, NOT_FI);
        ulkomaalaisenTunnisteetRule.addChild(onkoSinullaSuomalainenHetu);
        henkilotiedotRyhma.addChild(ulkomaalaisenTunnisteetRule);

        // Email
        TextQuestion email = new TextQuestion("Sähköposti", createI18NText("form.henkilotiedot.email",
          formMessagesBundle));
        addSizeAttribute(email, 50);
        email.setValidator(createRegexValidator(email.getId(), EMAIL_REGEX, formErrorsBundle));
        email.setHelp(createI18NText("form.henkilotiedot.email.help", formMessagesBundle));
        ElementUtil.setVerboseHelp(email, "form.henkilotiedot.email.verboseHelp", formVerboseHelpBundle);
        email.setInline(true);
        henkilotiedotRyhma.addChild(email);

        // Matkapuhelinnumerot

        TextQuestion puhelinnumero1 = new TextQuestion("matkapuhelinnumero1",
                createI18NText("form.henkilotiedot.matkapuhelinnumero", formMessagesBundle));
        puhelinnumero1.setHelp(createI18NText("form.henkilotiedot.matkapuhelinnumero.help",
          formMessagesBundle));
        addSizeAttribute(puhelinnumero1, 30);
        puhelinnumero1.setValidator(createRegexValidator(puhelinnumero1.getId(), MOBILE_PHONE_PATTERN, formErrorsBundle));
        ElementUtil.setVerboseHelp(puhelinnumero1, "form.henkilotiedot.matkapuhelinnumero.verboseHelp", formVerboseHelpBundle);
        puhelinnumero1.setInline(true);
        henkilotiedotRyhma.addChild(puhelinnumero1);

        TextQuestion prevNum = puhelinnumero1;
        AddElementRule prevRule = null;
        for (int i = 2; i <= 5; i++) {
            TextQuestion extranumero = new TextQuestion("matkapuhelinnumero" + i,
                    createI18NText("form.henkilotiedot.puhelinnumero", formMessagesBundle));
            addSizeAttribute(extranumero, 30);
            extranumero.setValidator(createRegexValidator(extranumero.getId(), PHONE_PATTERN, formErrorsBundle));
            extranumero.setInline(true);

            AddElementRule extranumeroRule = new AddElementRule("addPuhelinnumero" + i + "Rule", prevNum.getId(),
                    createI18NText("form.henkilotiedot.puhelinnumero.lisaa", formMessagesBundle));
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
          formMessagesBundle), null);
        asuinmaa.addOptions(koodistoService.getCountries());
        setDefaultOption("FIN", asuinmaa.getOptions());
        setRequiredInlineAndVerboseHelp(asuinmaa, "form.henkilotiedot.asuinmaa.verboseHelp", formVerboseHelpBundle,
          formErrorsBundle);

        RelatedQuestionComplexRule asuinmaaFI = ElementUtil.createRegexpRule(asuinmaa, EMPTY_OR_FIN_PATTERN);
        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "form.henkilotiedot.lahiosoite", formMessagesBundle,
          formErrorsBundle, 40);
        lahiosoite.setInline(true);
        asuinmaaFI.addChild(lahiosoite);

        Element postinumero = new PostalCode("Postinumero", createI18NText("form.henkilotiedot.postinumero",
          formMessagesBundle), koodistoService.getPostOffices());
        addSizeAttribute(postinumero, 5);
        postinumero.addAttribute("maxlength", "5");
        postinumero.setValidator(createRegexValidator(postinumero.getId(), POSTINUMERO_PATTERN, formErrorsBundle));
        addRequiredValidator(postinumero, formErrorsBundle);
        postinumero.setHelp(createI18NText("form.henkilotiedot.postinumero.help", formMessagesBundle));
        asuinmaaFI.addChild(postinumero);

        DropdownSelect kotikunta =
                new DropdownSelect("kotikunta", createI18NText("form.henkilotiedot.kotikunta",
                  formMessagesBundle), null);
        kotikunta.addOption(ElementUtil.createI18NAsIs(""), "");
        kotikunta.addOptions(koodistoService.getMunicipalities());
        setRequiredInlineAndVerboseHelp(kotikunta, "form.henkilotiedot.kotikunta.verboseHelp", formVerboseHelpBundle,
          formErrorsBundle);
        kotikunta.setHelp(createI18NText("form.henkilotiedot.kotikunta.help", formMessagesBundle));
        asuinmaaFI.addChild(kotikunta);

        /*CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1",
                createI18NForm("form.henkilotiedot.ensisijainenOsoite"));
        ensisijainenOsoite.setInline(true);
        asuinmaaFI.addChild(ensisijainenOsoite);*/

        RelatedQuestionComplexRule relatedQuestionRule2 = ElementUtil.createRegexpRule(asuinmaa, NOT_FI);
        Question osoiteUlkomaa = createRequiredTextQuestion("osoiteUlkomaa", "form.henkilotiedot.osoite", formMessagesBundle,
          formErrorsBundle, 40);
        osoiteUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(osoiteUlkomaa);
        Question postinumeroUlkomaa = createRequiredTextQuestion("postinumeroUlkomaa", "form.henkilotiedot.postinumero",
          formMessagesBundle, formErrorsBundle, 12);
        postinumeroUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(postinumeroUlkomaa);
        Question kaupunkiUlkomaa = createRequiredTextQuestion("kaupunkiUlkomaa", "form.henkilotiedot.kaupunki",
          formMessagesBundle, formErrorsBundle, 25);
        kaupunkiUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(kaupunkiUlkomaa);

        asuinmaa.addChild(relatedQuestionRule2);
        asuinmaa.addChild(asuinmaaFI);

        henkilotiedotRyhma.addChild(asuinmaa);

        // Äidinkieli
        DropdownSelect aidinkieli =
                new DropdownSelect(AIDINKIELI_ID, createI18NText("form.henkilotiedot.aidinkieli",
                  formMessagesBundle),
                        "fi_vm_sade_oppija_language");
        aidinkieli.addOption(ElementUtil.createI18NAsIs(""), "");
        aidinkieli.addOptions(koodistoService.getLanguages());
        setRequiredInlineAndVerboseHelp(aidinkieli, "form.henkilotiedot.aidinkieli.verboseHelp", formVerboseHelpBundle,
          formErrorsBundle);
        aidinkieli.setHelp(createI18NText("form.henkilotiedot.aidinkieli.help", formMessagesBundle));
        henkilotiedotRyhma.addChild(aidinkieli);

        henkilotiedot.addChild(henkilotiedotRyhma);
        return henkilotiedot;
    }

    private static TextQuestion createCallingNameQuestion(String formMessagesBundle, String formErrorsBundle, String formVerboseHelpBundle, final int size) {
        TextQuestion kutsumanimi = createNameQuestion("Kutsumanimi", "form.henkilotiedot.kutsumanimi", formMessagesBundle, formErrorsBundle, 20 );
        kutsumanimi.setHelp(createI18NText("form.henkilotiedot.kutsumanimi.help", formMessagesBundle));
        setVerboseHelp(kutsumanimi, "form.henkilotiedot.kutsumanimi.verboseHelp", formVerboseHelpBundle);
        return kutsumanimi;
    }

    private static TextQuestion createNameQuestion(String id, String translation, String formMessagesBundle, String formErrorsBundle, final int size ) {
        TextQuestion name = createRequiredTextQuestion(id, translation, formMessagesBundle, formErrorsBundle, size);
        name.setInline(true);
        name.setValidator(createRegexValidator(name.getId(), ElementUtil.ISO88591_NAME_REGEX, formErrorsBundle));
        return name;
    }
}
