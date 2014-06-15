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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.HiddenValue;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Popup;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceTable;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.oppija.lomake.validation.validators.PreferenceValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RegexFieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RequiredFieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.SsnAndPreferenceUniqueValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import java.util.List;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.NotificationBuilder.Warning;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class HakutoiveetPhase {
    public static final String LISAOPETUS_EDUCATION_DEGREE = "22";
    private static final String DISCRETIONARY_EDUCATION_DEGREE = "32";
    private static final String HAKUTOIVEET_PHASE_ID = "hakutoiveet";
    private static final String HAKUTOIVEET_THEME_ID = "hakutoiveet.teema";

    private static final String TODISTUSTENPUUTTUMINEN = "todistustenpuuttuminen";


    public static Element create(final FormParameters formParameters) {
        return Phase(HAKUTOIVEET_PHASE_ID).formParams(formParameters)
                .addChild(createHakutoiveetTheme(formParameters)).build();
    }

    private static Element createHakutoiveetTheme(final FormParameters formParameters) {

        Element hakutoiveetTheme = Theme(HAKUTOIVEET_THEME_ID).previewable().formParams(formParameters).build();
        hakutoiveetTheme.setHelp(createI18NText("form.hakutoiveet.help", formParameters));
        PreferenceTable preferenceTable =
                new PreferenceTable("preferencelist", createI18NText("form.hakutoiveet.otsikko", formParameters));

        PreferenceRow pr1 = createI18NPreferenceRow("preference1", "1", formParameters);
        pr1.setValidator(new RequiredFieldValidator(pr1.getLearningInstitutionInputId(), ElementUtil.createI18NText("yleinen.pakollinen", formParameters)));
        pr1.setValidator(new RequiredFieldValidator(pr1.getEducationInputId(), ElementUtil.createI18NText("yleinen.pakollinen", formParameters)));
        if (formParameters.isLisahaku()) {
            pr1.setValidator(new SsnAndPreferenceUniqueValidator());
        }
        preferenceTable.addChild(pr1);
        for (int index = 2; index <= formParameters.getApplicationSystem().getMaxApplicationOptions(); index++) {
            PreferenceRow pref = createI18NPreferenceRow("preference" + index, String.valueOf(index), formParameters);
            preferenceTable.addChild(pref);

        }
        ElementUtil.setVerboseHelp(preferenceTable, "form.hakutoiveet.otsikko.verboseHelp", formParameters);
        hakutoiveetTheme.addChild(preferenceTable);
        return hakutoiveetTheme;
    }

    public static PreferenceRow createI18NPreferenceRow(final String id, final String title, final FormParameters formParameters) {
        PreferenceRow pr = new PreferenceRow(id,
                createI18NAsIs(title + "."),
                createI18NText("form.yleinen.tyhjenna", formParameters),
                createI18NText("form.hakutoiveet.koulutus", formParameters),
                createI18NText("form.hakutoiveet.opetuspiste", formParameters),
                createI18NText("form.hakutoiveet.sisaltyvatKoulutusohjelmat", formParameters));
        if (!formParameters.isPervako()) {
            pr.addChild(createDiscretionaryQuestionsAndRules(id, formParameters));

            pr.addChild(createSoraQuestions(id, formParameters),
                    createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(id, formParameters),
                    createUrheilijalinjaRule(id),
                    createKaksoistutkintoQuestions(id, formParameters));
        } else {
            Element koulutusasteRistiriidassaSuoritettuunTutkintoon = Rule(ElementUtil.randomId()).setExpr(
                    new And(
                            ExprUtil.isAnswerTrue("ammatillinenTutkintoSuoritettu"),
                            ExprUtil.atLeastOneValueEqualsToVariable(id + "-Koulutus-educationDegree", LISAOPETUS_EDUCATION_DEGREE, DISCRETIONARY_EDUCATION_DEGREE)))
                    .build();
            Element ristiriita = Warning("koulutusasteristiriita").failValidation()
                    .formParams(formParameters).build();
            koulutusasteRistiriidassaSuoritettuunTutkintoon.addChild(ristiriita);
            pr.addChild(koulutusasteRistiriidassaSuoritettuunTutkintoon);
        }
        pr.setValidator(new PreferenceValidator());
        ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionGenerator();
        List<Element> themeQuestions = configurator.findAndConfigure(formParameters.getApplicationSystem(), HAKUTOIVEET_THEME_ID, pr.getId());
        if (themeQuestions.size() > 0){
           pr.addChild(themeQuestions.toArray(new Element[themeQuestions.size()]));
        }
        return pr;
    }

    private static Element[] createDiscretionaryQuestionsAndRules(final String index, final FormParameters formParameters) {
        Element discretionary = RadioBuilder.Radio(index + "-discretionary")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .required()
                .i18nText(createI18NText("form.hakutoiveet.harkinnanvarainen", formParameters))
                .formParams(formParameters).build();

        Element discretionaryFollowUp = new DropdownSelectBuilder(discretionary.getId() + "-follow-up")
                .emptyOption()
                .addOption((Option) new OptionBuilder().setValue("oppimisvaikudet").labelKey("perustelu.oppimisvaikeudet").formParams(formParameters).build())
                .addOption((Option) new OptionBuilder().setValue("sosiaalisetsyyt").labelKey("perustelu.sosiaaliset").formParams(formParameters).build())
                .addOption((Option) new OptionBuilder().setValue("todistustenvertailuvaikeudet").labelKey("perustelu.todistustenvertailuvaikeudet").formParams(formParameters).build())
                .addOption((Option) new OptionBuilder().setValue(TODISTUSTENPUUTTUMINEN).labelKey("perustelu.todistustenpuuttuminen").formParams(formParameters).build())
                .required()
                .i18nText(createI18NText("form.hakutoiveet.harkinnanvarainen.perustelu", formParameters))
                .formParams(formParameters).build();

        Element discretionaryFollowUpRule = createVarEqualsToValueRule(discretionary.getId(), KYLLA);
        discretionaryFollowUpRule.addChild(discretionaryFollowUp);

        discretionary.addChild(discretionaryFollowUpRule);

        Element discretionaryRule =
                createVarEqualsToValueRule(index + "-Koulutus-educationDegree", DISCRETIONARY_EDUCATION_DEGREE);

        Element discretionaryRule2 = createVarEqualsToValueRule("POHJAKOULUTUS",
                PERUSKOULU, YLIOPPILAS, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);


        discretionaryRule.addChild(discretionary);
        discretionaryRule2.addChild(discretionaryRule);

        Element KoulutusValittu = Rule(ElementUtil.randomId()).setExpr(new Not(new Equals(new Variable(index + "-Koulutus-id"), new Value("")))).build();

        Element keskeytynytTaiUlkomainenRule =
                createVarEqualsToValueRule("POHJAKOULUTUS", KESKEYTYNYT, ULKOMAINEN_TUTKINTO);

        HiddenValue hiddenDiscretionary = new HiddenValue(discretionary.getId(), ElementUtil.KYLLA);
        ElementUtil.addRequiredValidator(hiddenDiscretionary, formParameters);
        hiddenDiscretionary.setValidator(
                new RegexFieldValidator(ElementUtil.createI18NText("yleinen.virheellinenArvo", formParameters),
                        ElementUtil.KYLLA));

        HiddenValue hiddenDiscretionaryFollowUp = new HiddenValue(discretionaryFollowUp.getId(), TODISTUSTENPUUTTUMINEN);
        ElementUtil.addRequiredValidator(hiddenDiscretionaryFollowUp, formParameters);
        hiddenDiscretionaryFollowUp.setValidator(
                new RegexFieldValidator(ElementUtil.createI18NText("yleinen.virheellinenArvo", formParameters),
                        TODISTUSTENPUUTTUMINEN));


        keskeytynytTaiUlkomainenRule.addChild(hiddenDiscretionary, hiddenDiscretionaryFollowUp);
        KoulutusValittu.addChild(keskeytynytTaiUlkomainenRule);

        return new Element[]{discretionaryRule2, KoulutusValittu};

    }

    private static Element createSoraQuestions(final String index, final FormParameters formParameters) {
        // sora-kysymykset

        Element hasSora = ElementUtil.createRuleIfVariableIsTrue(index + "_sora_rule", index + "-Koulutus-id-sora");

        Element sora1 = RadioBuilder.Radio(index + "_sora_terveys")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI),
                        new Option(createI18NText("form.sora.kylla", formParameters), KYLLA)))
                .labelKey("form.sora.terveys")
                .required()
                .formParams(formParameters).build();
        sora1.setPopup(new Popup("sora-popup", createI18NText("form.hakutoiveet.terveydentilavaatimukset.otsikko", formParameters)));

        Element sora2 = RadioBuilder.Radio(index + "_sora_oikeudenMenetys")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI),
                        new Option(createI18NText("form.sora.kylla", formParameters), KYLLA)))
                .labelKey("form.sora.oikeudenmenetys")
                .required()
                .formParams(formParameters).build();

        hasSora.addChild(sora1, sora2);
        return hasSora;
    }

    private static Element createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(final String index, final FormParameters formParameters) {
        Element radio = RadioBuilder.Radio(index + "_urheilijan_ammatillisen_koulutuksen_lisakysymys")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .required()
                .i18nText(createI18NText("form.hakutoiveet.urheilijan.ammatillisen.koulutuksen.lisakysymys", formParameters))
                .formParams(formParameters).build();
        Expr expr = new And(new Equals(new Variable(index + "-Koulutus-id-athlete"), new Value(ElementUtil.KYLLA)),
                new Equals(new Variable(index + "-Koulutus-id-vocational"), new Value(ElementUtil.KYLLA)));
        Element rule = Rule(ElementUtil.randomId()).setExpr(expr).build();
        rule.addChild(radio);
        return rule;
    }

    private static Element createUrheilijalinjaRule(final String index) {
        HiddenValue hiddenValue = new HiddenValue(index + "_urheilijalinjan_lisakysymys", ElementUtil.KYLLA);
        Expr expr = new And(new Equals(new Variable(index + "-Koulutus-id-athlete"), new Value(ElementUtil.KYLLA)),
                new Equals(new Variable(index + "-Koulutus-id-vocational"), new Value(ElementUtil.EI)));
        Element rule = Rule(ElementUtil.randomId()).setExpr(expr).build();
        rule.addChild(hiddenValue);
        return rule;
    }

    private static Element createKaksoistutkintoQuestions(final String index, final FormParameters formParameters) {
        Element radio = RadioBuilder.Radio(index + "_kaksoistutkinnon_lisakysymys")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .required()
                .i18nText(createI18NText("form.hakutoiveet.kaksoistutkinnon.lisakysymys", formParameters))
                .formParams(formParameters).build();
        Element hasQuestion =
                ElementUtil.createRuleIfVariableIsTrue(radio.getId() + "_related_question_rule", index + "-Koulutus-id-kaksoistutkinto");
        hasQuestion.addChild(radio);
        return hasQuestion;
    }

}
