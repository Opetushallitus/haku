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
import fi.vm.sade.haku.oppija.lomake.domain.elements.Notification;
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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.service.Role.*;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.NotificationBuilder.Warning;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator.ConfiguratorFilter;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class HakutoiveetPhase {
    private static final String HAKUTOIVEET_PHASE_ID = "hakutoiveet";
    private static final String HAKUTOIVEET_THEME_ID = "hakutoiveet_teema";
    public static final String TODISTUSTENPUUTTUMINEN = "todistustenpuuttuminen";
    private static final String[] POHJAKOULUTUS_KESKEYTYNYT_TAI_ULKOMAINEN_TUTKINTO = {KESKEYTYNYT, ULKOMAINEN_TUTKINTO};

    public static Element create(final FormParameters formParameters) {
        return Phase(HAKUTOIVEET_PHASE_ID).setEditAllowedByRoles(ROLE_RU, ROLE_CRUD, ROLE_HETUTTOMIENKASITTELY, ROLE_KKVIRKAILIJA).formParams(formParameters)
                .addChild(createHakutoiveetTheme(formParameters)).build();
    }
    public static List<String> getPreferenceIds(final FormParameters formParameters) {
        int maxApplicationOptions = formParameters.getApplicationSystem().getMaxApplicationOptions();
        ArrayList<String> ids = new ArrayList<String>(maxApplicationOptions);
        for (int i = 1; i <= maxApplicationOptions; i++) {
            ids.add("preference" + i);
        }
        return ids;
    }
    private static Element createHakutoiveetTheme(final FormParameters formParameters) {

        Element hakutoiveetTheme = Theme(HAKUTOIVEET_THEME_ID).previewable().configurable().formParams(formParameters).build();
        hakutoiveetTheme.setHelp(formParameters.getI18nText("form.hakutoiveet.help"));

        if (formParameters.isOnlyThemeGenerationForFormEditor())
            return hakutoiveetTheme;

        PreferenceTable preferenceTable = new PreferenceTable(
                "preferencelist",
                formParameters.getI18nText("form.hakutoiveet.otsikko"),
                formParameters.getApplicationSystem().isUsePriorities(),
                Math.min(6, formParameters.getApplicationSystem().getMaxApplicationOptions()),
                formParameters.isOpetuspisteetVetovalikossa());

        List<String> preferenceIds = getPreferenceIds(formParameters);
        PreferenceRow pr1 = createI18NPreferenceRow(preferenceIds.remove(0), formParameters);
        pr1.setValidator(new RequiredFieldValidator(pr1.getLearningInstitutionInputId(), "yleinen.pakollinen"));
        pr1.setValidator(new RequiredFieldValidator(pr1.getEducationInputId(), "yleinen.pakollinen"));
        if ((formParameters.isLisahaku() || formParameters.isToisenAsteenErillishaku()) && !formParameters.isDemoMode()) {
            pr1.setValidator(new SsnAndPreferenceUniqueValidator());
        }
        preferenceTable.addChild(pr1);
        for (String preferenceId : preferenceIds) {
            preferenceTable.addChild(createI18NPreferenceRow(preferenceId, formParameters));
        }
        ElementUtil.setVerboseHelp(preferenceTable, "form.hakutoiveet.otsikko.verboseHelp", formParameters);
        hakutoiveetTheme.addChild(preferenceTable);

        preferenceTable.setGroupRestrictionValidators(formParameters.getGroupRestrictionConfigurator().findAndConfigureGroupRestrictions());

        ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionConfigurator();
        hakutoiveetTheme.addChild(configurator.findAndConfigure(hakutoiveetTheme.getId(), ConfiguratorFilter.ONLY_GROUP_QUESTIONS));

        return hakutoiveetTheme;
    }

    public static PreferenceRow createI18NPreferenceRow(final String id, final FormParameters formParameters) {
        final String LISAOPETUS_EDUCATION_DEGREE = formParameters.useEducationDegreeURI() ? "koulutusasteoph2002_22" : "22";
        final String DISCRETIONARY_EDUCATION_DEGREE = formParameters.useEducationDegreeURI() ? "koulutusasteoph2002_32" : "32";
        PreferenceRow pr = new PreferenceRow(id,
                formParameters.getI18nText("form.yleinen.tyhjenna"),
                formParameters.getI18nText("form.hakutoiveet.koulutus"),
                formParameters.getI18nText("form.hakutoiveet.opetuspiste"),
                formParameters.getI18nText("form.hakutoiveet.sisaltyvatKoulutusohjelmat"),
                formParameters.getI18nText("form.hakutoiveet.liitteet"),
                formParameters.getI18nText("form.hakutoiveet.hakumaksu"));

        if (formParameters.getApplicationSystem().isMaksumuuriKaytossa()) {
            pr.addChild(Rule(
                    new And(
                            new Not(new Equals(new Variable(id + "-Koulutus-id"), new Value(""))),
                            ExprUtil.isAnswerTrue(id + PAYMENT_NOTIFICATION_POSTFIX))
                    )
                    .addChild(createPaymentNotification(id, formParameters))
                    .build());
        }

        if (formParameters.kysytaankoHarkinnanvaraisuus()) {
            pr.addChild(createDiscretionaryQuestionsAndRules(id, formParameters));
        }

        if (formParameters.kysytaankoSora()) {
            pr.addChild(createSoraQuestions(id, formParameters));
        }
        if (formParameters.kysytaankoUrheilijanLisakysymykset()) {
            pr.addChild(createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(id, formParameters),
                    createUrheilijalinjaRule(id));
        }
        if (formParameters.kysytaankoKaksoistutkinto()) {
            pr.addChild(createKaksoistutkintoQuestions(id, formParameters));
        }

        if (formParameters.isPerusopetuksenJalkeinenValmentava()) {
            Element koulutusasteRistiriidassaSuoritettuunTutkintoon = Rule(
                    new And(
                            ExprUtil.isAnswerTrue("ammatillinenTutkintoSuoritettu"),
                            ExprUtil.atLeastOneValueEqualsToVariable(id + "-Koulutus-educationDegree", LISAOPETUS_EDUCATION_DEGREE, DISCRETIONARY_EDUCATION_DEGREE)))
                    .build();
            Element ristiriita = new Notification("koulutusasteristiriita",formParameters.getI18nText("form.koulutustausta.ammatillinensuoritettu.huom"), Notification.NotificationType.INFO);
            koulutusasteRistiriidassaSuoritettuunTutkintoon.addChild(ristiriita);
            pr.addChild(koulutusasteRistiriidassaSuoritettuunTutkintoon);
        }

        if (formParameters.isTarkistaPohjakoulutusRiittavyys()) {
            pr.addChild(createTarkistaPohjakoulutusRiittavyys(id, formParameters));

            KoodistoService koodistoService = formParameters.getKoodistoService();

            // Yliopisto
            List<String> yliopistokoulutukset = new ArrayList<>();
            List<String> ylemmatAMKKoulutukset = new ArrayList<>();
            for (Code c : koodistoService.getYliopistokoulutukset()) {
                yliopistokoulutukset.add("koulutus_" + c.getValue());
            }
            for (Code c : koodistoService.getYlemmatAMKkoulutukset()) {
                ylemmatAMKKoulutukset.add("koulutus_" + c.getValue());
            }
            pr.addChild(Rule(ExprUtil
                    .atLeastOneValueEqualsToVariable(id + "-Koulutus-id-educationcode", yliopistokoulutukset.toArray(new String[yliopistokoulutukset.size()])))
                    .addChild(new HiddenValue(id + "-yoLiite", "true"))
                    .formParams(formParameters)
                    .build());
            pr.addChild(Rule(ExprUtil
                    .atLeastOneValueEqualsToVariable(id + "-Koulutus-id-educationcode", ylemmatAMKKoulutukset.toArray(new String[ylemmatAMKKoulutukset.size()])))
                    .addChild(new HiddenValue(id + "-ylempiAMKLiite", "true"))
                    .formParams(formParameters)
                    .build());
        }

        pr.setValidator(new PreferenceValidator());

        ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionConfigurator();
        pr.addChild(configurator.findAndConfigure(HAKUTOIVEET_THEME_ID, pr.getId(), ConfiguratorFilter.NO_GROUP_QUESTIONS));

        return pr;
    }

    public static String[] getAmkKoulutusIds(KoodistoService koodistoService) {
        List<Code> amkkoulutukset = koodistoService.getAMKkoulutukset();
        String[] amkkoulutuksetArr =  new String[amkkoulutukset.size()];
        for (int i = 0; i < amkkoulutukset.size(); i++) {
            amkkoulutuksetArr[i] = "koulutus_" + amkkoulutukset.get(i).getValue();
        }
        return amkkoulutuksetArr;
    }

    private static Element[] createDiscretionaryQuestionsAndRules(final String index,
                                                                  final FormParameters formParameters) {
        Element discretionary = RadioBuilder.Radio(index + "-discretionary")
                .addOptions(ImmutableList.of(
                        new Option(formParameters.getI18nText("form.yleinen.kylla"), KYLLA),
                        new Option(formParameters.getI18nText("form.yleinen.ei"), EI)))
                .i18nText(formParameters.getI18nText("form.hakutoiveet.harkinnanvarainen"))
                .required()
                .formParams(formParameters).build();

        Element discretionaryFollowUp = new DropdownSelectBuilder(discretionary.getId() + "-follow-up")
                .emptyOptionDefault()
                .addOption((Option) new OptionBuilder().setValue("oppimisvaikudet").labelKey("perustelu.oppimisvaikeudet").formParams(formParameters).build())
                .addOption((Option) new OptionBuilder().setValue("sosiaalisetsyyt").labelKey("perustelu.sosiaaliset").formParams(formParameters).build())
                .addOption((Option) new OptionBuilder().setValue("todistustenvertailuvaikeudet").labelKey("perustelu.todistustenvertailuvaikeudet").formParams(formParameters).build())
                .addOption((Option) new OptionBuilder().setValue(TODISTUSTENPUUTTUMINEN).labelKey("perustelu.todistustenpuuttuminen").formParams(formParameters).build())
                .i18nText(formParameters.getI18nText("form.hakutoiveet.harkinnanvarainen.perustelu"))
                .required()
                .formParams(formParameters).build();

        Or baseEducationNotReadyOrForeignExpr = new Or(
                ExprUtil.atLeastOneValueEqualsToVariable("POHJAKOULUTUS", POHJAKOULUTUS_KESKEYTYNYT_TAI_ULKOMAINEN_TUTKINTO),
                ExprUtil.atLeastOneValueEqualsToVariable("POHJAKOULUTUS-POSTPROCESS", POHJAKOULUTUS_KESKEYTYNYT_TAI_ULKOMAINEN_TUTKINTO)
        );

        Element discretionaryFollowUpRule = createVarEqualsToValueRule(discretionary.getId(), KYLLA);
        discretionaryFollowUpRule.addChild(discretionaryFollowUp);

        discretionary.addChild(discretionaryFollowUpRule);

        Element discretionaryRule =
                createVarEqualsToValueRule(index + "-Koulutus-id-discretionary", KYLLA);

        Element discretionaryRule2 = Rule(new Not(baseEducationNotReadyOrForeignExpr)).build();

        discretionaryRule.addChild(discretionary);
        discretionaryRule2.addChild(discretionaryRule);

        Element KoulutusValittu = Rule(new Not(new Equals(new Variable(index + "-Koulutus-id"), new Value("")))).build();

        Element keskeytynytTaiUlkomainenRule = Rule(baseEducationNotReadyOrForeignExpr).build();

        HiddenValue hiddenDiscretionary = new HiddenValue(discretionary.getId(), ElementUtil.KYLLA);
        ElementUtil.addRequiredValidator(hiddenDiscretionary, formParameters);
        hiddenDiscretionary.setValidator(
                new RegexFieldValidator("yleinen.virheellinenarvo", ElementUtil.KYLLA));

        HiddenValue hiddenDiscretionaryFollowUp = new HiddenValue(discretionaryFollowUp.getId(), TODISTUSTENPUUTTUMINEN);
        ElementUtil.addRequiredValidator(hiddenDiscretionaryFollowUp, formParameters);
        hiddenDiscretionaryFollowUp.setValidator(
                new RegexFieldValidator("yleinen.virheellinenarvo", TODISTUSTENPUUTTUMINEN));

        keskeytynytTaiUlkomainenRule.addChild(hiddenDiscretionary, hiddenDiscretionaryFollowUp);
        KoulutusValittu.addChild(keskeytynytTaiUlkomainenRule);

        return new Element[]{discretionaryRule2, KoulutusValittu};

    }

    private static Element createPaymentNotification(final String index, final FormParameters formParameters) {
        return new Notification(index + "_payment_notification", formParameters.getI18nText("form.hakutoiveet.vaatiihakumaksun"), Notification.NotificationType.INFO);
    }

    private static Element createSoraQuestions(final String index, final FormParameters formParameters) {
        // sora-kysymykset

        Element hasSora = ElementUtil.createRuleIfVariableIsTrue(index + "-Koulutus-id-sora");

        Element sora1 = RadioBuilder.Radio(index + "_sora_terveys")
                .addOptions(ImmutableList.of(
                        new Option(formParameters.getI18nText("form.sora.kylla"), KYLLA),
                        new Option(formParameters.getI18nText("form.yleinen.ei"), EI)
                ))
                .labelKey("form.sora.terveys")
                .required()
                .formParams(formParameters).build();
        sora1.setPopup(new Popup("sora-popup", formParameters.getI18nText(
          "form.hakutoiveet.terveydentilavaatimukset.otsikko")));

        Element sora2 = RadioBuilder.Radio(index + "_sora_oikeudenMenetys")
                .addOptions(ImmutableList.of(
                        new Option(formParameters.getI18nText("form.sora.kylla"), KYLLA),
                        new Option(formParameters.getI18nText("form.yleinen.ei"), EI)
                ))
                .labelKey("form.sora.oikeudenmenetys")
                .required()
                .formParams(formParameters).build();

        hasSora.addChild(sora1, sora2);
        return hasSora;
    }

    private static Element createTarkistaPohjakoulutusRiittavyys(final String id, final FormParameters formParameters) {
        Element pohjakoulutusValidation = Rule(new Equals(new ValidateEducationExpr(id), Value.TRUE))
                .addChild(new Notification(id + "_pohjakoulutus_ei_riita", formParameters.getI18nText("form.hakutoiveet.pohjakoulutuseiriita"), Notification.NotificationType.INFO))
                .build();

        Element koulutusValittu = Rule(new Not(new Equals(new Variable(id + "-Koulutus-id"), new Value("")))).build();
        koulutusValittu.addChild(pohjakoulutusValidation);
        return koulutusValittu;
    }

    private static Element createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(final String index, final FormParameters formParameters) {
        Element radio = RadioBuilder.Radio(index + "_urheilijan_ammatillisen_koulutuksen_lisakysymys")
                .addOptions(ImmutableList.of(
                        new Option(formParameters.getI18nText("form.yleinen.kylla"), KYLLA),
                        new Option(formParameters.getI18nText("form.yleinen.ei"), EI)))
                .i18nText(formParameters.getI18nText("form.hakutoiveet.urheilijan.ammatillisen.koulutuksen.lisakysymys"))
                .required()
                .formParams(formParameters).build();
        Expr expr = new And(new Equals(new Variable(index + "-Koulutus-id-athlete"), new Value(ElementUtil.KYLLA)),
                new Equals(new Variable(index + "-Koulutus-id-vocational"), new Value(ElementUtil.KYLLA)));
        Element rule = Rule(expr).build();
        rule.addChild(radio);
        return rule;
    }

    private static Element createUrheilijalinjaRule(final String index) {
        HiddenValue hiddenValue = new HiddenValue(index + "_urheilijalinjan_lisakysymys", ElementUtil.KYLLA);
        Expr expr = new And(new Equals(new Variable(index + "-Koulutus-id-athlete"), new Value(ElementUtil.KYLLA)),
                new Equals(new Variable(index + "-Koulutus-id-vocational"), new Value(ElementUtil.EI)));
        Element rule = Rule(expr).build();
        rule.addChild(hiddenValue);
        return rule;
    }

    private static Element createKaksoistutkintoQuestions(final String index, final FormParameters formParameters) {
        Element radio = RadioBuilder.Radio(index + "_kaksoistutkinnon_lisakysymys")
                .addOptions(ImmutableList.of(
                        new Option(formParameters.getI18nText("form.yleinen.kylla"), KYLLA),
                        new Option(formParameters.getI18nText("form.yleinen.ei"), EI)))
                .i18nText(formParameters.getI18nText("form.hakutoiveet.kaksoistutkinnon.lisakysymys"))
                .required()
                .formParams(formParameters).build();
        Element hasQuestion =
                ElementUtil.createRuleIfVariableIsTrue(index + "-Koulutus-id-kaksoistutkinto");
        hasQuestion.addChild(radio);
        return hasQuestion;
    }

}
