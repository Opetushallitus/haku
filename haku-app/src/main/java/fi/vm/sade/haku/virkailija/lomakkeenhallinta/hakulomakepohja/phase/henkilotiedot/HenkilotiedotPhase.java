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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public final class HenkilotiedotPhase {

    public static final String PHONE_PATTERN = "^$|^([0-9\\(\\)\\/\\+ \\-]*)$";
    private static final String NOT_FI = "^((?!FIN)[A-Z]{3})$";
    public static final String AIDINKIELI_ID = "aidinkieli";
    private static final String HETU_PATTERN = "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$";
    private static final String POSTINUMERO_PATTERN = "[0-9]{5}";
    private static final String DATE_PATTERN = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.(19|20)\\d\\d$";
    public static final String EMPTY_OR_FIN_PATTERN = "^$|^FIN$";

    private HenkilotiedotPhase() {
    }

    public static Phase create(final FormParameters formParameters) {

        // Henkilötiedot
        Phase henkilotiedot = new Phase("henkilotiedot", createI18NText("form.henkilotiedot.otsikko",
                formParameters), false, Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));

        Theme henkilotiedotRyhma = new Theme("HenkilotiedotGrp", createI18NText("form.henkilotiedot.otsikko",
                formParameters), true);

        // Nimet
        Question sukunimi = createNameQuestion("Sukunimi", "form.henkilotiedot.sukunimi", formParameters, 30);
        henkilotiedotRyhma.addChild(sukunimi);

        Question etunimet = createNameQuestion("Etunimet", "form.henkilotiedot.etunimet", formParameters, 30);
        henkilotiedotRyhma.addChild(etunimet);

        Question kutsumanimi = createCallingNameQuestion(formParameters);
        kutsumanimi.setValidator(
                new ContainedInOtherFieldValidator(kutsumanimi.getId(),
                        etunimet.getId(),
                        ElementUtil.createI18NText("yleinen.virheellinenArvo", formParameters)));

        henkilotiedotRyhma.addChild(kutsumanimi);


        // Kansalaisuus, hetu ja sukupuoli suomalaisille
        DropdownSelect kansalaisuus =
                new DropdownSelect("kansalaisuus", createI18NText("form.henkilotiedot.kansalaisuus",
                        formParameters), null);
        kansalaisuus.addOptions(formParameters.getKoodistoService().getNationalities());
        setDefaultOption("FIN", kansalaisuus.getOptions());
        kansalaisuus.setHelp(createI18NText("form.henkilotiedot.kansalaisuus.help", formParameters));
        setRequiredInlineAndVerboseHelp(kansalaisuus, "form.henkilotiedot.kansalaisuus.verboseHelp", formParameters);
        henkilotiedotRyhma.addChild(kansalaisuus);

        TextQuestion henkilotunnus =
                new TextQuestion("Henkilotunnus", createI18NText("form.henkilotiedot.henkilotunnus",
                        formParameters));
        henkilotunnus.addAttribute("placeholder", "ppkkvv*****");
        addSizeAttribute(henkilotunnus, 11);
        henkilotunnus.addAttribute("maxlength", "11");
        henkilotunnus.setHelp(createI18NText("form.henkilotiedot.henkilotunnus.help", formParameters));
        henkilotunnus.setValidator(createRegexValidator(henkilotunnus.getId(), HETU_PATTERN, formParameters));
        setRequiredInlineAndVerboseHelp(henkilotunnus, "form.henkilotiedot.henkilotunnus.verboseHelp", formParameters);

        Radio sukupuoli = new Radio("sukupuoli", createI18NText("form.henkilotiedot.sukupuoli",
                formParameters));
        sukupuoli.addOptions(formParameters.getKoodistoService().getGenders());
        setRequiredInlineAndVerboseHelp(sukupuoli, "form.henkilotiedot.sukupuoli.verboseHelp", formParameters);

        Option male = sukupuoli.getOptions().get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Mies") ?
                sukupuoli.getOptions().get(0) : sukupuoli.getOptions().get(1);
        Option female = sukupuoli.getOptions().get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Nainen") ?
                sukupuoli.getOptions().get(0) : sukupuoli.getOptions().get(1);
        SocialSecurityNumber socialSecurityNumber =
                new SocialSecurityNumber("ssn_question", createI18NText("form.henkilotiedot.hetu",
                        formParameters),
                        sukupuoli.getI18nText(), male, female, sukupuoli.getId(), henkilotunnus);
        addUniqueApplicantValidator(henkilotunnus, formParameters.getApplicationSystem().getApplicationSystemType());

        RelatedQuestionComplexRule hetuRule = createRegexpRule(kansalaisuus, EMPTY_OR_FIN_PATTERN);
        hetuRule.addChild(socialSecurityNumber);
        henkilotiedotRyhma.addChild(hetuRule);

        // Ulkomaalaisten tunnisteet
        Radio onkoSinullaSuomalainenHetu = new Radio("onkoSinullaSuomalainenHetu",
                createI18NText("form.henkilotiedot.hetu.onkoSuomalainen", formParameters));
        addDefaultTrueFalseOptions(onkoSinullaSuomalainenHetu, formParameters);
        setRequiredInlineAndVerboseHelp(onkoSinullaSuomalainenHetu, "form.henkilotiedot.hetu.onkoSuomalainen.verboseHelp", formParameters);
        RelatedQuestionComplexRule suomalainenHetuRule = createRuleIfVariableIsTrue("onSuomalainenHetu", onkoSinullaSuomalainenHetu.getId());
        suomalainenHetuRule.addChild(socialSecurityNumber);
        onkoSinullaSuomalainenHetu.addChild(suomalainenHetuRule);

        RelatedQuestionComplexRule eiSuomalaistaHetuaRule = createRuleIfVariableIsFalse("eiOleSuomalaistaHetua", onkoSinullaSuomalainenHetu.getId());
        eiSuomalaistaHetuaRule.addChild(sukupuoli);

        DateQuestion syntymaaika = new DateQuestion("syntymaaika", createI18NText("form.henkilotiedot.syntymaaika",
                formParameters));
        syntymaaika.setValidator(ElementUtil.createRegexValidator(syntymaaika.getId(), DATE_PATTERN, formParameters));
        syntymaaika.setValidator(ElementUtil.createDateOfBirthValidator(syntymaaika.getId(), formParameters.getFormMessagesBundle()));
        addRequiredValidator(syntymaaika, formParameters);
        syntymaaika.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymaaika);

        TextQuestion syntymapaikka =
                new TextQuestion("syntymapaikka", createI18NText("form.henkilotiedot.syntymapaikka",
                        formParameters));
        addSizeAttribute(syntymapaikka, 30);
        addRequiredValidator(syntymapaikka, formParameters);
        syntymapaikka.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymapaikka);

        TextQuestion kansallinenIdTunnus =
                new TextQuestion("kansallinenIdTunnus", createI18NText("form.henkilotiedot.kansallinenId",
                        formParameters));
        addSizeAttribute(kansallinenIdTunnus, 30);
        kansallinenIdTunnus.setInline(true);
        eiSuomalaistaHetuaRule.addChild(kansallinenIdTunnus);

        TextQuestion passinnumero = new TextQuestion("passinnumero", createI18NText("form.henkilotiedot.passinnumero",
                formParameters));
        addSizeAttribute(passinnumero, 30);
        passinnumero.setInline(true);
        eiSuomalaistaHetuaRule.addChild(passinnumero);

        onkoSinullaSuomalainenHetu.addChild(eiSuomalaistaHetuaRule);

        RelatedQuestionComplexRule ulkomaalaisenTunnisteetRule = createRegexpRule(kansalaisuus, NOT_FI);
        ulkomaalaisenTunnisteetRule.addChild(onkoSinullaSuomalainenHetu);
        henkilotiedotRyhma.addChild(ulkomaalaisenTunnisteetRule);

        // Email
        TextQuestion email = new TextQuestion("Sähköposti", createI18NText("form.henkilotiedot.email",
                formParameters));
        addSizeAttribute(email, 50);
        email.setValidator(createRegexValidator(email.getId(), EMAIL_REGEX, formParameters));
        email.setHelp(createI18NText("form.henkilotiedot.email.help", formParameters));
        ElementUtil.setVerboseHelp(email, "form.henkilotiedot.email.verboseHelp", formParameters);
        email.setInline(true);
        henkilotiedotRyhma.addChild(email);

        // Matkapuhelinnumerot

        TextQuestion puhelinnumero1 = new TextQuestion("matkapuhelinnumero1",
                createI18NText("form.henkilotiedot.matkapuhelinnumero", formParameters));
        puhelinnumero1.setHelp(createI18NText("form.henkilotiedot.matkapuhelinnumero.help",
                formParameters));
        addSizeAttribute(puhelinnumero1, 30);
        puhelinnumero1.setValidator(createRegexValidator(puhelinnumero1.getId(), PHONE_PATTERN, formParameters));
        ElementUtil.setVerboseHelp(puhelinnumero1, "form.henkilotiedot.matkapuhelinnumero.verboseHelp", formParameters);
        puhelinnumero1.setInline(true);
        henkilotiedotRyhma.addChild(puhelinnumero1);

        TextQuestion prevNum = puhelinnumero1;
        AddElementRule prevRule = null;
        for (int i = 2; i <= 5; i++) {
            TextQuestion extranumero = new TextQuestion("matkapuhelinnumero" + i,
                    createI18NText("form.henkilotiedot.puhelinnumero", formParameters));
            addSizeAttribute(extranumero, 30);
            extranumero.setValidator(createRegexValidator(extranumero.getId(), PHONE_PATTERN, formParameters));
            extranumero.setInline(true);

            AddElementRule extranumeroRule = new AddElementRule("addPuhelinnumero" + i + "Rule", prevNum.getId(),
                    createI18NText("form.henkilotiedot.puhelinnumero.lisaa", formParameters));
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
                formParameters), null);
        asuinmaa.addOptions(formParameters.getKoodistoService().getCountries());
        setDefaultOption("FIN", asuinmaa.getOptions());
        setRequiredInlineAndVerboseHelp(asuinmaa, "form.henkilotiedot.asuinmaa.verboseHelp", formParameters);

        RelatedQuestionComplexRule asuinmaaFI = ElementUtil.createRegexpRule(asuinmaa, EMPTY_OR_FIN_PATTERN);
        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "form.henkilotiedot.lahiosoite", 40, formParameters);
        lahiosoite.setInline(true);
        asuinmaaFI.addChild(lahiosoite);

        Element postinumero = new PostalCode("Postinumero", createI18NText("form.henkilotiedot.postinumero",
                formParameters), formParameters.getKoodistoService().getPostOffices());
        addSizeAttribute(postinumero, 5);
        postinumero.addAttribute("placeholder", "00000");
        postinumero.addAttribute("maxlength", "5");
        postinumero.setValidator(createRegexValidator(postinumero.getId(), POSTINUMERO_PATTERN, formParameters, "f"));
        addRequiredValidator(postinumero, formParameters);
        postinumero.setHelp(createI18NText("form.henkilotiedot.postinumero.help", formParameters));
        asuinmaaFI.addChild(postinumero);

        DropdownSelect kotikunta =
                new DropdownSelect("kotikunta", createI18NText("form.henkilotiedot.kotikunta",
                        formParameters), null);
        kotikunta.addOption(ElementUtil.createI18NAsIs(""), "");
        kotikunta.addOptions(formParameters.getKoodistoService().getMunicipalities());
        setRequiredInlineAndVerboseHelp(kotikunta, "form.henkilotiedot.kotikunta.verboseHelp", formParameters);
        kotikunta.setHelp(createI18NText("form.henkilotiedot.kotikunta.help", formParameters));
        asuinmaaFI.addChild(kotikunta);

        /*CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1",
                createI18NForm("form.henkilotiedot.ensisijainenOsoite"));
        ensisijainenOsoite.setInline(true);
        asuinmaaFI.addChild(ensisijainenOsoite);*/

        RelatedQuestionComplexRule relatedQuestionRule2 = ElementUtil.createRegexpRule(asuinmaa, NOT_FI);
        Question osoiteUlkomaa = createRequiredTextQuestion("osoiteUlkomaa", "form.henkilotiedot.osoite", 40, formParameters);
        osoiteUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(osoiteUlkomaa);
        Question postinumeroUlkomaa = createRequiredTextQuestion("postinumeroUlkomaa", "form.henkilotiedot.postinumero", 12, formParameters);
        postinumeroUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(postinumeroUlkomaa);
        Question kaupunkiUlkomaa = createRequiredTextQuestion("kaupunkiUlkomaa", "form.henkilotiedot.kaupunki", 25, formParameters);
        kaupunkiUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(kaupunkiUlkomaa);

        asuinmaa.addChild(relatedQuestionRule2);
        asuinmaa.addChild(asuinmaaFI);

        henkilotiedotRyhma.addChild(asuinmaa);

        // Äidinkieli
        DropdownSelect aidinkieli =
                new DropdownSelect(AIDINKIELI_ID, createI18NText("form.henkilotiedot.aidinkieli", formParameters),
                        "fi_vm_sade_oppija_language");
        aidinkieli.addOption(ElementUtil.createI18NAsIs(""), "");
        aidinkieli.addOptions(formParameters.getKoodistoService().getLanguages());
        setRequiredInlineAndVerboseHelp(aidinkieli, "form.henkilotiedot.aidinkieli.verboseHelp", formParameters);
        aidinkieli.setHelp(createI18NText("form.henkilotiedot.aidinkieli.help", formParameters));
        henkilotiedotRyhma.addChild(aidinkieli);

        henkilotiedot.addChild(henkilotiedotRyhma);
        return henkilotiedot;
    }

    private static TextQuestion createCallingNameQuestion(final FormParameters formParameters) {
        TextQuestion kutsumanimi = createNameQuestion("Kutsumanimi", "form.henkilotiedot.kutsumanimi", formParameters, 20);
        kutsumanimi.setHelp(createI18NText("form.henkilotiedot.kutsumanimi.help", formParameters));
        setVerboseHelp(kutsumanimi, "form.henkilotiedot.kutsumanimi.verboseHelp", formParameters);
        return kutsumanimi;
    }

    private static TextQuestion createNameQuestion(String id, String translation, final FormParameters formParameters, final int size) {
        TextQuestion name = createRequiredTextQuestion(id, translation, size, formParameters);
        name.setInline(true);
        name.setValidator(createRegexValidator(name.getId(), ElementUtil.ISO88591_NAME_REGEX, formParameters));
        return name;
    }
}
