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

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ElementBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PostalCode;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DateQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.List;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;
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

    public static Element create(final FormParameters formParameters) {

        // Henkilötiedot
        Element henkilotiedot = Phase("henkilotiedot").build(formParameters);

        Element henkilotiedotTeema = Theme("henkilotiedot.teema").previewable().build(formParameters);

        henkilotiedotTeema.addChild(
                createNameQuestionBuilder("Sukunimi", 30).build(formParameters),
                createNameQuestionBuilder("Etunimet", 30).build(formParameters),
                createNameQuestionBuilder("Kutsumanimi", 20)
                        .containsInField("Etunimet")
                        .build(formParameters));

        Element kansalaisuus = new DropdownSelectBuilder("kansalaisuus")
                .addOptions(formParameters.getKoodistoService().getNationalities())
                .defaultOption("FIN")
                .required()
                .inline()
                .build(formParameters);
        henkilotiedotTeema.addChild(kansalaisuus);

        Element henkilotunnus = TextQuestion("Henkilotunnus")
                .inline()
                .placeholder("ppkkvv*****")
                .size(11)
                .maxLength(11)
                .pattern(HETU_PATTERN)
                .required()
                .build(formParameters);

        List<Option> genders = formParameters.getKoodistoService().getGenders();
        Radio sukupuoli = (Radio) Radio("sukupuoli")
                .addOptions(genders)
                .build(formParameters);

        Option male = genders.get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Mies") ?
                genders.get(0) : sukupuoli.getOptions().get(1);
        Option female = genders.get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Nainen") ?
                genders.get(0) : genders.get(1);
        SocialSecurityNumber socialSecurityNumber =
                new SocialSecurityNumber("ssn_question", createI18NText("form.henkilotiedot.hetu",
                        formParameters),
                        sukupuoli.getI18nText(), male, female, sukupuoli.getId(), (TextQuestion) henkilotunnus);
        addUniqueApplicantValidator(henkilotunnus, formParameters.getApplicationSystem().getApplicationSystemType());

        RelatedQuestionComplexRule hetuRule = createRegexpRule(kansalaisuus, EMPTY_OR_FIN_PATTERN);

        hetuRule.addChild(socialSecurityNumber);
        henkilotiedotTeema.addChild(hetuRule);

        // Ulkomaalaisten tunnisteet
        Element onkoSinullaSuomalainenHetu = Radio("onkoSinullaSuomalainenHetu")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .requiredInline()
                .build(formParameters);

        RelatedQuestionComplexRule suomalainenHetuRule = createRuleIfVariableIsTrue("onSuomalainenHetu", onkoSinullaSuomalainenHetu.getId());
        suomalainenHetuRule.addChild(socialSecurityNumber);
        onkoSinullaSuomalainenHetu.addChild(suomalainenHetuRule);

        RelatedQuestionComplexRule eiSuomalaistaHetuaRule = createRuleIfVariableIsFalse("eiOleSuomalaistaHetua", onkoSinullaSuomalainenHetu.getId());
        eiSuomalaistaHetuaRule.addChild(sukupuoli);

        DateQuestion syntymaaika = new DateQuestion("syntymaaika", createI18NText("syntymaaika",
                formParameters));
        syntymaaika.setValidator(ElementUtil.createRegexValidator(syntymaaika.getId(), DATE_PATTERN, formParameters));
        syntymaaika.setValidator(ElementUtil.createDateOfBirthValidator(syntymaaika.getId(), formParameters.getFormMessagesBundle()));
        addRequiredValidator(syntymaaika, formParameters);
        syntymaaika.setInline(true);

        eiSuomalaistaHetuaRule.addChild(syntymaaika,
                TextQuestion("syntymapaikka").inline().size(30).required().build(formParameters),
                TextQuestion("kansallinenIdTunnus").inline().size(30).build(formParameters),
                TextQuestion("passinnumero").inline().size(30).build(formParameters));

        onkoSinullaSuomalainenHetu.addChild(eiSuomalaistaHetuaRule);

        RelatedQuestionComplexRule ulkomaalaisenTunnisteetRule = createRegexpRule(kansalaisuus, NOT_FI);
        ulkomaalaisenTunnisteetRule.addChild(onkoSinullaSuomalainenHetu);
        henkilotiedotTeema.addChild(ulkomaalaisenTunnisteetRule);

        henkilotiedotTeema.addChild(
                TextQuestion("Sähköposti").inline().size(50).pattern(EMAIL_REGEX).build(formParameters));

        // Matkapuhelinnumerot
        Element puhelinnumero1 = TextQuestion("matkapuhelinnumero1").labelKey("matkapuhelinnumero")
                .pattern(PHONE_PATTERN)
                .size(30)
                .inline()
                .build(formParameters);
        henkilotiedotTeema.addChild(puhelinnumero1);

        Element prevNum = puhelinnumero1;
        AddElementRule prevRule = null;
        for (int i = 2; i <= 5; i++) {
            Element extranumero = TextQuestion("matkapuhelinnumero" + i).labelKey("puhelinnumero")
                    .size(30)
                    .pattern(PHONE_PATTERN)
                    .inline()
                    .build(formParameters);

            String id = "addPuhelinnumero" + i + "Rule";
            I18nText i18nText = formParameters.getI18nText("puhelinnumero.lisaa");
            AddElementRule extranumeroRule = new AddElementRule(id, prevNum.getId(), i18nText);
            extranumeroRule.addChild(extranumero);
            if (i == 2) {
                henkilotiedotTeema.addChild(extranumeroRule);
            } else {
                prevRule.addChild(extranumeroRule);
            }
            prevNum = extranumero;
            prevRule = extranumeroRule;
        }


        // Asuinmaa, osoite
        Element asuinmaa = new DropdownSelectBuilder("asuinmaa")
                .defaultOption("FIN")
                .addOptions(formParameters.getKoodistoService().getCountries())
                .required()
                .inline()
                .build(formParameters);

        RelatedQuestionComplexRule asuinmaaFI = ElementUtil.createRegexpRule(asuinmaa, EMPTY_OR_FIN_PATTERN);
        Element lahiosoite = TextQuestion("lahiosoite").inline().size(40).required().build(formParameters);
        asuinmaaFI.addChild(lahiosoite);

        Element postinumero = new PostalCode("Postinumero",
                formParameters.getI18nText("Postinumero"),
                formParameters.getKoodistoService().getPostOffices());
        addSizeAttribute(postinumero, 5);
        postinumero.addAttribute("placeholder", "00000");
        postinumero.addAttribute("maxlength", "5");
        postinumero.setValidator(createRegexValidator(postinumero.getId(), POSTINUMERO_PATTERN, formParameters));
        addRequiredValidator(postinumero, formParameters);
        postinumero.setHelp(createI18NText("postinumero.help", formParameters));
        asuinmaaFI.addChild(postinumero);

        Element kotikunta =
                new DropdownSelectBuilder("kotikunta")
                        .emptyOption()
                        .addOptions(formParameters.getKoodistoService().getMunicipalities())
                        .inline()
                        .required()
                        .build(formParameters);

        asuinmaaFI.addChild(kotikunta);

        RelatedQuestionComplexRule asuinmaaEiOleSuomiRule = ElementUtil.createRegexpRule(asuinmaa, NOT_FI);
        asuinmaaEiOleSuomiRule.addChild(
                TextQuestion("osoiteUlkomaa").labelKey("osoite").inline().size(40).required().build(formParameters),
                TextQuestion("postinumeroUlkomaa").inline().size(12).required().build(formParameters),
                TextQuestion("kaupunkiUlkomaa").labelKey("kaupunki").inline().size(25).required().build(formParameters));

        asuinmaa.addChild(asuinmaaEiOleSuomiRule);
        asuinmaa.addChild(asuinmaaFI);

        henkilotiedotTeema.addChild(asuinmaa);

        henkilotiedotTeema.addChild(new DropdownSelectBuilder(AIDINKIELI_ID)
                .defaultValueAttribute("fi_vm_sade_oppija_language")
                .emptyOption()
                .addOptions(formParameters.getKoodistoService().getLanguages())
                .required()
                .inline()
                .build(formParameters));

        henkilotiedot.addChild(henkilotiedotTeema);
        if (formParameters.isPervako()) {
            henkilotiedotTeema.addChild(
                    TextQuestion("huoltajannimi")
                            .size(30)
                            .pattern(ElementUtil.ISO88591_NAME_REGEX)
                            .inline()
                            .build(formParameters),
                    TextQuestion("huoltajanpuhelinnumero")
                            .size(30)
                            .pattern(PHONE_PATTERN)
                            .inline()
                            .build(formParameters),
                    TextQuestion("huoltajansahkoposti")
                            .inline()
                            .size(50)
                            .pattern(EMAIL_REGEX).build(formParameters));
        }
        return henkilotiedot;
    }

    private static ElementBuilder createNameQuestionBuilder(final String id, final int size) {
        return TextQuestion(id)
                .inline()
                .pattern(ElementUtil.ISO88591_NAME_REGEX)
                .size(size)
                .required();
    }

}
