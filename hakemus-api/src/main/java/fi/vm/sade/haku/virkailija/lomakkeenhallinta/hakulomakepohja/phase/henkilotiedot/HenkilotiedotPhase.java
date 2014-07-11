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
import fi.vm.sade.haku.oppija.lomake.domain.builder.SocialSecurityNumberBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.oppija.lomake.validation.validators.PastDateValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RegexFieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.SocialSecurityNumberFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.List;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.DateQuestionBuilder.Date;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder.Dropdown;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PostalCodeBuilder.PostalCode;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public final class HenkilotiedotPhase {

    public static final String PHONE_PATTERN = "^$|^([0-9\\(\\)\\/\\+ \\-]*)$";
    private static final String NOT_FI = "^((?!FIN)[A-Z]{3})$";
    public static final String AIDINKIELI_ID = "aidinkieli";
    private static final String HETU_PATTERN = "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$";
    private static final String POSTINUMERO_PATTERN = "[0-9]{5}";
    private static final String DATE_PATTERN = "^([0-9]|0[1-9]|[12][0-9]|3[01])\\.([0-9]|0[1-9]|1[012])\\.(19|20)\\d\\d$";
    public static final String EMPTY_OR_FIN_PATTERN = "^$|^FIN$";

    private HenkilotiedotPhase() {
    }

    public static Element create(final FormParameters formParameters) {

        // Henkilötiedot
        Element henkilotiedot = Phase("henkilotiedot").setEditAllowedByRoles("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO").formParams(formParameters).build();

        Element henkilotiedotTeema = Theme("henkilotiedot.teema").previewable().formParams(formParameters).build();

        henkilotiedotTeema.addChild(
                createNameQuestionBuilder("Sukunimi", 30).formParams(formParameters).build(),
                createNameQuestionBuilder("Etunimet", 30).formParams(formParameters).build(),
                createNameQuestionBuilder("Kutsumanimi", 20)
                        .containsInField("Etunimet")
                        .formParams(formParameters).build());


        Element kansalaisuus = new DropdownSelectBuilder("kansalaisuus")
                .addOptions(formParameters.getKoodistoService().getNationalities())
                .defaultOption("FIN")
                .requiredInline()
                .formParams(formParameters).build();
        henkilotiedotTeema.addChild(kansalaisuus);
        Expr suomalainen = new Regexp(kansalaisuus.getId(), EMPTY_OR_FIN_PATTERN);

        Element eiSuomalainen = Rule(ElementUtil.randomId()).setExpr(new Not(suomalainen)).build();
        // Ulkomaalaisten tunnisteet
        Element onkoSuomalainenKysymys = Radio("onkoSinullaSuomalainenHetu")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .requiredInline()
                .formParams(formParameters).build();
        eiSuomalainen.addChild(onkoSuomalainenKysymys);
        henkilotiedotTeema.addChild(eiSuomalainen);

        Expr onSuomalainenHetu = new Equals(new Variable("onkoSinullaSuomalainenHetu"), new Value(KYLLA));

        Or kysytaankoHetu = new Or(suomalainen, onSuomalainenHetu);
        Element kysytaankoHetuSaanto = Rule(ElementUtil.randomId()).setExpr(kysytaankoHetu).build();

        henkilotiedotTeema.addChild(kysytaankoHetuSaanto);

        List<Option> genders = formParameters.getKoodistoService().getGenders();
        Radio sukupuoli = (Radio) Radio(OppijaConstants.ELEMENT_ID_SEX)
                .addOptions(genders)
                .requiredInline()
                .formParams(formParameters).build();

        Option male = genders.get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Mies") ?
                genders.get(0) : sukupuoli.getOptions().get(1);
        Option female = genders.get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Nainen") ?
                genders.get(0) : genders.get(1);

        Element socialSecurityNumber =
                new SocialSecurityNumberBuilder(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER)
                        .setSexI18nText(sukupuoli.getI18nText())
                        .setMaleOption(male)
                        .setFemaleOption(female)
                        .setSexId(sukupuoli.getId())
                        .formParams(formParameters)
                        .placeholder("ppkkvv*****")
                        .requiredInline()
                        .size(11)
                        .maxLength(11)
                        .validator(new SocialSecurityNumberFieldValidator())
                        .pattern(HETU_PATTERN)
                        .build();
        addUniqueApplicantValidator(socialSecurityNumber, formParameters.getApplicationSystem().getApplicationSystemType());


        kysytaankoHetuSaanto.addChild(socialSecurityNumber);

        Element syntymaaika = Date("syntymaaika").formParams(formParameters).build();
        syntymaaika.setValidator(new PastDateValidator(createI18NText("henkilotiedot.syntymaaika.tulevaisuudessa", formParameters)));
        syntymaaika.setValidator(new RegexFieldValidator(createI18NText("henkilotiedot.syntymaaika.virhe"), DATE_PATTERN));
        addRequiredValidator(syntymaaika, formParameters);
        syntymaaika.setInline(true);

        Element eiHetuaSaanto = Rule(ElementUtil.randomId()).setExpr(new Equals(new Variable("onkoSinullaSuomalainenHetu"), new Value(EI))).build();
        eiHetuaSaanto.addChild(
                sukupuoli,
                syntymaaika,
                TextQuestion("syntymapaikka").size(30).requiredInline().formParams(formParameters).build(),
                TextQuestion("kansallinenIdTunnus").inline().size(30).formParams(formParameters).build(),
                TextQuestion("passinnumero").inline().size(30).formParams(formParameters).build());

        onkoSuomalainenKysymys.addChild(eiHetuaSaanto);


        henkilotiedotTeema.addChild(
                TextQuestion("Sähköposti").inline().size(50).pattern(EMAIL_REGEX).formParams(formParameters).build());

        // Matkapuhelinnumerot
        Element puhelinnumero1 = TextQuestion("matkapuhelinnumero1").labelKey("matkapuhelinnumero")
                .pattern(PHONE_PATTERN)
                .size(30)
                .inline()
                .formParams(formParameters).build();
        henkilotiedotTeema.addChild(puhelinnumero1);

        Element prevNum = puhelinnumero1;
        AddElementRule prevRule = null;
        for (int i = 2; i <= 5; i++) {
            Element extranumero = TextQuestion("matkapuhelinnumero" + i).labelKey("puhelinnumero")
                    .size(30)
                    .pattern(PHONE_PATTERN)
                    .inline()
                    .formParams(formParameters).build();

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
        Element asuinmaa = Dropdown("asuinmaa")
                .defaultOption("FIN")
                .addOptions(formParameters.getKoodistoService().getCountries())
                .requiredInline()
                .formParams(formParameters).build();

        Element asuinmaaFI = ElementUtil.createRegexpRule(asuinmaa, EMPTY_OR_FIN_PATTERN);
        Element lahiosoite = TextQuestion("lahiosoite").inline().size(40).required().formParams(formParameters).build();
        asuinmaaFI.addChild(lahiosoite);

        Element postinumero = PostalCode("Postinumero")
                .addOptions(formParameters.getKoodistoService().getPostOffices())
                .maxLength(5)
                .size(5)
                .required()
                .formParams(formParameters)
                .pattern(POSTINUMERO_PATTERN)
                .placeholder("00000")
                .build();

        asuinmaaFI.addChild(postinumero);

        Element kotikunta =
                new DropdownSelectBuilder("kotikunta")
                        .emptyOption()
                        .addOptions(formParameters.getKoodistoService().getMunicipalities())
                        .requiredInline()
                        .formParams(formParameters).build();

        asuinmaaFI.addChild(kotikunta);

        Element asuinmaaEiOleSuomiRule = ElementUtil.createRegexpRule(asuinmaa, NOT_FI);
        asuinmaaEiOleSuomiRule.addChild(
                TextQuestion("osoiteUlkomaa").labelKey("osoite").inline().size(40).required().formParams(formParameters).build(),
                TextQuestion("postinumeroUlkomaa").inline().size(12).required().formParams(formParameters).build(),
                TextQuestion("kaupunkiUlkomaa").labelKey("kaupunki").inline().size(25).required().formParams(formParameters).build());

        asuinmaa.addChild(asuinmaaEiOleSuomiRule);
        asuinmaa.addChild(asuinmaaFI);

        henkilotiedotTeema.addChild(asuinmaa);

        henkilotiedotTeema.addChild(Dropdown(AIDINKIELI_ID)
                .defaultValueAttribute("fi_vm_sade_oppija_language")
                .emptyOption()
                .addOptions(formParameters.getKoodistoService().getLanguages())
                .requiredInline()
                .formParams(formParameters).build());

        henkilotiedot.addChild(henkilotiedotTeema);
        if (formParameters.isPervako()) {
            henkilotiedotTeema.addChild(
                    ElementBuilder.buildAll(formParameters,
                            TextQuestion("huoltajannimi")
                                    .size(30)
                                    .pattern(ElementUtil.ISO88591_NAME_REGEX)
                                    .inline(),
                            TextQuestion("huoltajanpuhelinnumero")
                                    .size(30)
                                    .pattern(PHONE_PATTERN)
                                    .inline(),
                            TextQuestion("huoltajansahkoposti")
                                    .inline()
                                    .size(50)
                                    .pattern(EMAIL_REGEX)));
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
