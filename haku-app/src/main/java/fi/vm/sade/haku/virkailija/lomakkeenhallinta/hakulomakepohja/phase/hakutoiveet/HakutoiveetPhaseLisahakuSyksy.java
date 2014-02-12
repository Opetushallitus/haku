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

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.HiddenValue;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Popup;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SinglePreference;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class HakutoiveetPhaseLisahakuSyksy {
    public static final String DISCRETIONARY_EDUCATION_DEGREE = "32";
    public static final String HAKUTOIVEET_PHASE_ID = "hakutoiveet";

    private static final String FORM_MESSAGES = "form_messages_lisahaku_syksy";
    private static final String FORM_ERRORS = "form_errors_lisahaku_syksy";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_lisahaku_syksy";

    public static Phase create() {

        // Hakutoiveet
        Phase hakutoiveet = new Phase(HAKUTOIVEET_PHASE_ID, createI18NText("form.hakutoiveet.otsikko", FORM_MESSAGES), false);
        hakutoiveet.addChild(createHakutoiveetThemeLisahaku());

        return hakutoiveet;
    }

    private static Theme createHakutoiveetThemeLisahaku() {
        Theme hakutoiveetTheme = new Theme("hakutoiveetGrp", createI18NText("form.hakutoiveet.otsikko", FORM_MESSAGES), true);
        hakutoiveetTheme.setHelp(createI18NText("form.hakutoiveet.lisahaku.help", FORM_MESSAGES));
        final String id = "preference1";
        SinglePreference singlePreference = new SinglePreference(id,
                createI18NText("form.hakutoiveet.koulutus", FORM_MESSAGES),
                createI18NText("form.hakutoiveet.opetuspiste", FORM_MESSAGES),
                createI18NText("form.hakutoiveet.otsikko", FORM_MESSAGES),
                createI18NText("form.hakutoiveet.sisaltyvatKoulutusohjelmat", FORM_MESSAGES));
        singlePreference.addChild(createDiscretionaryQuestionsAndRules(id));
        singlePreference.addChild(createSoraQuestions(id),
                createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(id));
        hakutoiveetTheme.addChild(singlePreference);
        ElementUtil.setVerboseHelp(singlePreference, "form.hakutoiveet.otsikko.lisahaku.verboseHelp", FORM_VERBOSE_HELP);
        ElementUtil.addPreferenceValidator(singlePreference);
        addApplicationUniqueValidator(singlePreference, OppijaConstants.LISA_HAKU);
        return hakutoiveetTheme;
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
        addRequiredValidator(discretionaryFollowUp, FORM_ERRORS);

        RelatedQuestionComplexRule discretionaryFollowUpRule =
                ElementUtil.createRuleIfVariableIsTrue(index + "-discretionary-follow-up-rule", discretionary.getId());
        discretionaryFollowUpRule.addChild(discretionaryFollowUp);

        discretionary.addChild(discretionaryFollowUpRule);

        RelatedQuestionComplexRule discretionaryRule = createVarEqualsToValueRule(index + "-Koulutus-educationDegree", DISCRETIONARY_EDUCATION_DEGREE);

        RelatedQuestionComplexRule discretionaryRule2 = createVarEqualsToValueRule("POHJAKOULUTUS",
                PERUSKOULU, YLIOPPILAS, OSITTAIN_YKSILOLLISTETTY, ERITYISOPETUKSEN_YKSILOLLISTETTY, YKSILOLLISTETTY);
        discretionaryRule.addChild(discretionary);
        discretionaryRule2.addChild(discretionaryRule);

        RelatedQuestionComplexRule pohjakoulutusKeskeytynytTaiUlkomainen = createVarEqualsToValueRule("POHJAKOULUTUS", KESKEYTYNYT, ULKOMAINEN_TUTKINTO);
        pohjakoulutusKeskeytynytTaiUlkomainen.addChild(new HiddenValue(discretionary.getId(), ElementUtil.KYLLA));

        return new Element[]{discretionaryRule2, pohjakoulutusKeskeytynytTaiUlkomainen};

    }

    public static Element createSoraQuestions(final String index) {
        // sora-kysymykset

        RelatedQuestionComplexRule hasSora = ElementUtil.createRuleIfVariableIsTrue(index + "_sora_rule", index + "-Koulutus-id-sora");

        Radio sora1 = new Radio(index + "_sora_terveys", createI18NText("form.sora.terveys", FORM_MESSAGES));
        sora1.addOption(createI18NText("form.yleinen.ei", FORM_MESSAGES), ElementUtil.EI);
        sora1.addOption(createI18NText("form.sora.kylla", FORM_MESSAGES), ElementUtil.KYLLA);
        addRequiredValidator(sora1, FORM_ERRORS);
        sora1.setPopup(new Popup("sora-popup", createI18NText("form.hakutoiveet.terveydentilavaatimukset.otsikko",
                FORM_MESSAGES)));

        Radio sora2 = new Radio(index + "_sora_oikeudenMenetys", createI18NText("form.sora.oikeudenMenetys",
                FORM_MESSAGES));
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

        RelatedQuestionComplexRule hasQuestion =
                ElementUtil.createRuleIfVariableIsTrue(radio.getId() + "_related_question_rule", index + "-Koulutus-id-athlete");

        hasQuestion.addChild(radio);
        return hasQuestion;
    }

}
