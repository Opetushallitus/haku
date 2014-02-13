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
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.HiddenValue;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Popup;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceTable;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RegexFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class HakutoiveetPhaseYhteishakuKevat {
    public static final String DISCRETIONARY_EDUCATION_DEGREE = "32";
    public static final String HAKUTOIVEET_PHASE_ID = "hakutoiveet";

    private static final String FORM_MESSAGES = "form_messages_yhteishaku_kevat";
    private static final String FORM_ERRORS = "form_errors_yhteishaku_kevat";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_yhteishaku_kevat";
    public static final String TODISTUSTENPUUTTUMINEN = "todistustenpuuttuminen";


    public static Phase create() {

        // Hakutoiveet
        Phase hakutoiveet = new Phase(HAKUTOIVEET_PHASE_ID, createI18NText("form.hakutoiveet.otsikko", FORM_MESSAGES), false,
                Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD"));

        hakutoiveet.addChild(createHakutoiveetTheme());
        return hakutoiveet;
    }

    private static Theme createHakutoiveetTheme() {

        Theme hakutoiveetTheme = new Theme("hakutoiveetGrp", createI18NText("form.hakutoiveet.otsikko",
                FORM_MESSAGES), true);
        hakutoiveetTheme.setHelp(createI18NText("form.hakutoiveet.help", FORM_MESSAGES));
        PreferenceTable preferenceTable =
                new PreferenceTable("preferencelist", createI18NText("form.hakutoiveet.otsikko", FORM_MESSAGES));

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
        ElementUtil.setVerboseHelp(preferenceTable, "form.hakutoiveet.otsikko.verboseHelp", FORM_VERBOSE_HELP);
        hakutoiveetTheme.addChild(preferenceTable);
        return hakutoiveetTheme;
    }

    public static PreferenceRow createI18NPreferenceRow(final String id, final String title) {
        PreferenceRow pr = new PreferenceRow(id,
                createI18NText("form.hakutoiveet.hakutoive", FORM_MESSAGES, title),
                createI18NText("form.yleinen.tyhjenna", FORM_MESSAGES),
                createI18NText("form.hakutoiveet.koulutus", FORM_MESSAGES),
                createI18NText("form.hakutoiveet.opetuspiste", FORM_MESSAGES),
                createI18NText("form.hakutoiveet.sisaltyvatKoulutusohjelmat", FORM_MESSAGES));

        pr.addChild(createDiscretionaryQuestionsAndRules(id));
        pr.addChild(createSoraQuestions(id),
                createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(id),
                createUrheilijalinjaRule(id),
                createKaksoistutkintoQuestions(id));
        ElementUtil.addPreferenceValidator(pr);
        return pr;
    }

    private static Element[] createDiscretionaryQuestionsAndRules(final String index) {
        Radio discretionary = new Radio(index + "-discretionary", createI18NText("form.hakutoiveet.harkinnanvarainen",
                FORM_MESSAGES));
        addDefaultTrueFalseOptions(discretionary, FORM_MESSAGES);
        addRequiredValidator(discretionary, FORM_ERRORS);
        discretionary.setHelp(createI18NText("form.hakutoiveet.harkinnanvarainen.ohje", FORM_MESSAGES));

        DropdownSelect discretionaryFollowUp = new DropdownSelect(discretionary.getId() + "-follow-up",
                createI18NText("form.hakutoiveet.harkinnanvarainen.perustelu", FORM_MESSAGES), null);
        discretionaryFollowUp.addOption(ElementUtil.createI18NAsIs(""), "");
        discretionaryFollowUp.addOption(createI18NText("form.hakutoiveet.harkinnanvarainen.perustelu.oppimisvaikeudet",
                FORM_MESSAGES), "oppimisvaikudet");
        discretionaryFollowUp.addOption(createI18NText("form.hakutoiveet.harkinnanvarainen.perustelu.sosiaaliset",
                FORM_MESSAGES), "sosiaalisetsyyt");
        discretionaryFollowUp.addOption(createI18NText("form.hakutoiveet.harkinnanvarainen.perustelu.todistustenvertailuvaikeudet",
                FORM_MESSAGES), "todistustenvertailuvaikeudet");
        discretionaryFollowUp.addOption(createI18NText("form.hakutoiveet.harkinnanvarainen.perustelu.todistustenpuuttuminen",
                FORM_MESSAGES), TODISTUSTENPUUTTUMINEN);
        addRequiredValidator(discretionaryFollowUp, FORM_ERRORS);


        RelatedQuestionComplexRule discretionaryFollowUpRule = createVarEqualsToValueRule(discretionary.getId(), KYLLA);
        discretionaryFollowUpRule.addChild(discretionaryFollowUp);

        discretionary.addChild(discretionaryFollowUpRule);

        RelatedQuestionComplexRule discretionaryRule =
                createVarEqualsToValueRule(index + "-Koulutus-educationDegree", DISCRETIONARY_EDUCATION_DEGREE);

        RelatedQuestionComplexRule discretionaryRule2 = createVarEqualsToValueRule("POHJAKOULUTUS",
                PERUSKOULU, YLIOPPILAS, OSITTAIN_YKSILOLLISTETTY, ERITYISOPETUKSEN_YKSILOLLISTETTY, YKSILOLLISTETTY);


        discretionaryRule.addChild(discretionary);
        discretionaryRule2.addChild(discretionaryRule);

        RelatedQuestionComplexRule KoulutusValittu = new RelatedQuestionComplexRule(
                ElementUtil.randomId(), new Not(new Equals(new Variable(index + "-Koulutus-id"), new Value(""))));

        RelatedQuestionComplexRule keskeytynytTaiUlkomainenRule =
                createVarEqualsToValueRule("POHJAKOULUTUS", KESKEYTYNYT, ULKOMAINEN_TUTKINTO);

        HiddenValue hiddenDiscretionary = new HiddenValue(discretionary.getId(), ElementUtil.KYLLA);
        ElementUtil.addRequiredValidator(hiddenDiscretionary, FORM_MESSAGES);
        hiddenDiscretionary.setValidator(
                new RegexFieldValidator(hiddenDiscretionary.getId(),
                        ElementUtil.createI18NText("yleinen.virheellinenArvo", FORM_ERRORS),
                        ElementUtil.KYLLA));

        HiddenValue hiddenDiscretionaryFollowUp = new HiddenValue(discretionaryFollowUp.getId(), TODISTUSTENPUUTTUMINEN);
        ElementUtil.addRequiredValidator(hiddenDiscretionaryFollowUp, FORM_MESSAGES);
        hiddenDiscretionaryFollowUp.setValidator(
                new RegexFieldValidator(hiddenDiscretionaryFollowUp.getId(),
                        ElementUtil.createI18NText("yleinen.virheellinenArvo", FORM_ERRORS),
                        TODISTUSTENPUUTTUMINEN));


        keskeytynytTaiUlkomainenRule.addChild(hiddenDiscretionary, hiddenDiscretionaryFollowUp);
        KoulutusValittu.addChild(keskeytynytTaiUlkomainenRule);

        return new Element[]{discretionaryRule2, KoulutusValittu};

    }

    public static Element createSoraQuestions(final String index) {
        // sora-kysymykset

        RelatedQuestionRule hasSora = new RelatedQuestionRule(index + "_sora_rule",
                ImmutableList.of(index + "-Koulutus-id-sora"), ElementUtil.KYLLA, false);

        Radio sora1 = new Radio(index + "_sora_terveys", createI18NText("form.sora.terveys", FORM_MESSAGES));
        sora1.addOption(createI18NText("form.yleinen.ei", FORM_MESSAGES), ElementUtil.EI);
        sora1.addOption(createI18NText("form.sora.kylla", FORM_MESSAGES), ElementUtil.KYLLA);
        addRequiredValidator(sora1, FORM_ERRORS);
        sora1.setPopup(new Popup("sora-popup", createI18NText("form.hakutoiveet.terveydentilavaatimukset.otsikko", FORM_MESSAGES)));

        Radio sora2 = new Radio(index + "_sora_oikeudenMenetys", createI18NText("form.sora.oikeudenMenetys", FORM_MESSAGES));
        sora2.addOption(createI18NText("form.yleinen.ei", FORM_MESSAGES), ElementUtil.EI);
        sora2.addOption(createI18NText("form.sora.kylla", FORM_MESSAGES), ElementUtil.KYLLA);
        addRequiredValidator(sora2, FORM_ERRORS);

        hasSora.addChild(sora1, sora2);
        return hasSora;
    }

    public static Element createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(final String index) {
        Radio radio = new Radio(index + "_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                createI18NText("form.hakutoiveet.urheilijan.ammatillisen.koulutuksen.lisakysymys", FORM_MESSAGES));
        addDefaultTrueFalseOptions(radio, FORM_MESSAGES);
        addRequiredValidator(radio, FORM_ERRORS);
        Expr expr = new And(new Equals(new Variable(index + "-Koulutus-id-athlete"), new Value(ElementUtil.KYLLA)),
                new Equals(new Variable(index + "-Koulutus-id-vocational"), new Value(ElementUtil.KYLLA)));
        RelatedQuestionComplexRule rule = new RelatedQuestionComplexRule(ElementUtil.randomId(), expr);
        rule.addChild(radio);
        return rule;
    }

    public static Element createUrheilijalinjaRule(final String index) {
        HiddenValue hiddenValue = new HiddenValue(index + "_urheilijalinjan_lisakysymys", ElementUtil.KYLLA);
        Expr expr = new And(new Equals(new Variable(index + "-Koulutus-id-athlete"), new Value(ElementUtil.KYLLA)),
                new Equals(new Variable(index + "-Koulutus-id-vocational"), new Value(ElementUtil.EI)));
        RelatedQuestionComplexRule rule = new RelatedQuestionComplexRule(ElementUtil.randomId(), expr);
        rule.addChild(hiddenValue);
        return rule;
    }

    public static Element createKaksoistutkintoQuestions(final String index) {
        Radio radio = new Radio(index + "_kaksoistutkinnon_lisakysymys",
                createI18NText("form.hakutoiveet.kaksoistutkinnon.lisakysymys", FORM_MESSAGES));
        addDefaultTrueFalseOptions(radio, FORM_MESSAGES);
        addRequiredValidator(radio, FORM_ERRORS);
        RelatedQuestionRule hasQuestion = new RelatedQuestionRule(radio.getId() + "_related_question_rule",
                ImmutableList.of(index + "-Koulutus-id-kaksoistutkinto"), ElementUtil.KYLLA, false);

        hasQuestion.addChild(radio);
        return hasQuestion;
    }

}
