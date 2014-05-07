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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class HakutoiveetPhaseYhteishakuSyksy {
    public static final String DISCRETIONARY_EDUCATION_DEGREE = "32";
    public static final String HAKUTOIVEET_PHASE_ID = "hakutoiveet";

    public static Phase create(final FormParameters formParameters) {

        // Hakutoiveet
        Phase hakutoiveet = new Phase(HAKUTOIVEET_PHASE_ID, createI18NText("form.hakutoiveet.otsikko", formParameters), false,
                Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD"));

        hakutoiveet.addChild(createHakutoiveetTheme(formParameters));
        return hakutoiveet;
    }

    private static Theme createHakutoiveetTheme(FormParameters formParameters) {

        Theme hakutoiveetTheme = new Theme("hakutoiveetGrp", createI18NText("form.hakutoiveet.otsikko",
                formParameters), true);
        hakutoiveetTheme.setHelp(createI18NText("form.hakutoiveet.help", formParameters));
        PreferenceTable preferenceTable =
                new PreferenceTable("preferencelist", createI18NText("form.hakutoiveet.otsikko", formParameters));

        PreferenceRow pr1 = createI18NPreferenceRow("preference1", "1", formParameters);
        pr1.setValidator(new RequiredFieldValidator(pr1.getLearningInstitutionInputId(), ElementUtil.createI18NText("yleinen.pakollinen", formParameters)));
        pr1.setValidator(new RequiredFieldValidator(pr1.getEducationInputId(), ElementUtil.createI18NText("yleinen.pakollinen", formParameters)));
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
                createI18NText("form.hakutoiveet.hakutoive", formParameters, title),
                createI18NText("form.yleinen.tyhjenna", formParameters),
                createI18NText("form.hakutoiveet.koulutus", formParameters),
                createI18NText("form.hakutoiveet.opetuspiste", formParameters),
                createI18NText("form.hakutoiveet.sisaltyvatKoulutusohjelmat", formParameters));

        pr.addChild(createDiscretionaryQuestionsAndRules(id, formParameters));
        pr.addChild(createSoraQuestions(id, formParameters),
                createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(id, formParameters));
        ElementUtil.addPreferenceValidator(pr);
        return pr;
    }

    private static Element[] createDiscretionaryQuestionsAndRules(final String index, final FormParameters formParameters) {
        Radio discretionary = new Radio(index + "-discretionary", createI18NText("form.hakutoiveet.harkinnanvarainen",
                formParameters));
        addDefaultTrueFalseOptions(discretionary, formParameters);
        addRequiredValidator(discretionary, formParameters);
        discretionary.setHelp(createI18NText("form.hakutoiveet.harkinnanvarainen.ohje", formParameters));

        DropdownSelect discretionaryFollowUp = new DropdownSelect(discretionary.getId() + "-follow-up",
                createI18NText("form.hakutoiveet.harkinnanvarainen.perustelu", formParameters), null);
        discretionaryFollowUp.addOption(ElementUtil.createI18NAsIs(""), "");
        discretionaryFollowUp.addOption(createI18NText("form.hakutoiveet.harkinnanvarainen.perustelu.oppimisvaikeudet",
                formParameters), "oppimisvaikudet");
        discretionaryFollowUp.addOption(createI18NText("form.hakutoiveet.harkinnanvarainen.perustelu.sosiaaliset",
                formParameters), "sosiaalisetsyyt");
        addRequiredValidator(discretionaryFollowUp, formParameters);

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

    public static Element createSoraQuestions(final String index, final FormParameters formParameters) {
        // sora-kysymykset
        RelatedQuestionComplexRule hasSora = ElementUtil.createRuleIfVariableIsTrue(index + "_sora_rule", index + "-Koulutus-id-sora");

        Radio sora1 = new Radio(index + "_sora_terveys", createI18NText("form.sora.terveys", formParameters));
        sora1.addOption(createI18NText("form.yleinen.ei", formParameters), ElementUtil.EI);
        sora1.addOption(createI18NText("form.sora.kylla", formParameters), ElementUtil.KYLLA);
        addRequiredValidator(sora1, formParameters);
        sora1.setPopup(new Popup("sora-popup", createI18NText("form.hakutoiveet.terveydentilavaatimukset.otsikko", formParameters)));

        Radio sora2 = new Radio(index + "_sora_oikeudenMenetys", createI18NText("form.sora.oikeudenMenetys", formParameters));
        sora2.addOption(createI18NText("form.yleinen.ei", formParameters), ElementUtil.EI);
        sora2.addOption(createI18NText("form.sora.kylla", formParameters), ElementUtil.KYLLA);
        addRequiredValidator(sora2, formParameters);

        hasSora.addChild(sora1, sora2);
        return hasSora;
    }

    public static Element createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(final String index, final FormParameters formParameters) {

        Radio radio = new Radio(index + "_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                createI18NText("form.hakutoiveet.urheilijan.ammatillisen.koulutuksen.lisakysymys", formParameters));
        addDefaultTrueFalseOptions(radio, formParameters);
        addRequiredValidator(radio, formParameters);
        RelatedQuestionComplexRule hasQuestion =
                ElementUtil.createRuleIfVariableIsTrue(radio.getId() + "_related_question_rule", index + "-Koulutus-id-athlete");

        hasQuestion.addChild(radio);
        return hasQuestion;
    }

}
