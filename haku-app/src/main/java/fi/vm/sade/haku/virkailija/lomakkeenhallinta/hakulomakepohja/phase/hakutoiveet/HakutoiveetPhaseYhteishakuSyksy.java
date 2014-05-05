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

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
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
import fi.vm.sade.haku.oppija.lomake.validation.validators.RequiredFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class HakutoiveetPhaseYhteishakuSyksy {
    public static final String DISCRETIONARY_EDUCATION_DEGREE = "32";
    public static final String HAKUTOIVEET_PHASE_ID = "hakutoiveet";

    private static final String FORM_MESSAGES = "form_messages_yhteishaku_syksy";
    private static final String FORM_ERRORS = "form_errors_yhteishaku_syksy";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_yhteishaku_syksy";


    public static Phase create(ApplicationSystem as) {

        // Hakutoiveet
        Phase hakutoiveet = new Phase(HAKUTOIVEET_PHASE_ID, createI18NText("form.hakutoiveet.otsikko", FORM_MESSAGES), false,
                Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD"));

        hakutoiveet.addChild(createHakutoiveetTheme(as.getMaxApplicationOptions()));
        return hakutoiveet;
    }

    private static Theme createHakutoiveetTheme(int maxApplicationOptions) {

        Theme hakutoiveetTheme = new Theme("hakutoiveetGrp", createI18NText("form.hakutoiveet.otsikko",
                FORM_MESSAGES), true);
        hakutoiveetTheme.setHelp(createI18NText("form.hakutoiveet.help", FORM_MESSAGES));
        PreferenceTable preferenceTable =
                new PreferenceTable("preferencelist", createI18NText("form.hakutoiveet.otsikko", FORM_MESSAGES));

        PreferenceRow pr1 = createI18NPreferenceRow("preference1", "1");
        pr1.setValidator(new RequiredFieldValidator(pr1.getLearningInstitutionInputId(), ElementUtil.createI18NText("yleinen.pakollinen", "form_errors_yhteishaku_kevat")));
        pr1.setValidator(new RequiredFieldValidator(pr1.getEducationInputId(), ElementUtil.createI18NText("yleinen.pakollinen", "form_errors_yhteishaku_kevat")));
        preferenceTable.addChild(pr1);
        for (int index = 2; index <= maxApplicationOptions; index++) {
            PreferenceRow pref = createI18NPreferenceRow("preference" + index, String.valueOf(index));
            preferenceTable.addChild(pref);

        }
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
                createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(id));
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
        addRequiredValidator(discretionaryFollowUp, FORM_ERRORS);

        RelatedQuestionComplexRule discretionaryFollowUpRule = createRuleIfVariableIsTrue(discretionary.getId(), KYLLA);
        discretionaryFollowUpRule.addChild(discretionaryFollowUp);

        discretionary.addChild(discretionaryFollowUpRule);

        RelatedQuestionComplexRule discretionaryRule =
                createVarEqualsToValueRule(index + "-Koulutus-educationDegree", DISCRETIONARY_EDUCATION_DEGREE);

        RelatedQuestionComplexRule discretionaryRule2 = createVarEqualsToValueRule("POHJAKOULUTUS",
                PERUSKOULU, YLIOPPILAS, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

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
        RelatedQuestionComplexRule hasQuestion =
                ElementUtil.createRuleIfVariableIsTrue(radio.getId() + "_related_question_rule", index + "-Koulutus-id-athlete");

        hasQuestion.addChild(radio);
        return hasQuestion;
    }

}
