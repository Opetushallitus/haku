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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.*;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.*;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class KoulutustaustaPhaseYhteishakuKevat {
    public static final String TUTKINTO_ULKOMAILLA_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO_KESKEYTNYT_NOTIFICATION_ID = "tutkinto5-notification";

    public static final String TUTKINTO_KESKEYTYNYT_RULE = "tutkinto_7_rule";
    public static final String TUTKINTO_ULKOMAILLA_RULE = "tutkinto_0_rule";
    public static final String PAATTOTODISTUSVUOSI_PATTERN = "^(19[0-9][0-9]|200[0-9]|201[0-4])$";

    private static final String FORM_MESSAGES = "form_messages_yhteishaku_kevat";
    private static final String FORM_ERRORS = "form_errors_yhteishaku_kevat";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_yhteishaku_kevat";

    private KoulutustaustaPhaseYhteishakuKevat() {
    }

    public static Phase create(final KoodistoService koodistoService, ApplicationSystem as,
                               OrganizationService organisaatioService) {
        Phase koulutustausta = new Phase("koulutustausta", createI18NText("form.koulutustausta.otsikko",
                FORM_MESSAGES), false);
        Theme koulutustaustaRyhma = new Theme("KoulutustaustaGrp", createI18NText("form.koulutustausta.otsikko",
                FORM_MESSAGES), true);
        koulutustausta.addChild(koulutustaustaRyhma);
        koulutustaustaRyhma.setHelp(createI18NText("form.koulutustausta.help", FORM_MESSAGES));
        koulutustaustaRyhma.addChild(createKoulutustaustaRadio(koodistoService, as.getHakukausiVuosi(), organisaatioService));

        //Tätä ei kysytä syksyn yhteishaussa, tarvitaan myöhemmin.
        /*Radio osallistunut = new Radio("osallistunut", createI18NForm("form.koulutustausta.osallistunutPaasykokeisiin"));
        addYesAndIDontOptions(osallistunut);
        addRequiredValidator(osallistunut);
        setVerboseHelp(osallistunut);

        koulutustaustaRyhma.addChild(osallistunut);*/
        return koulutustausta;
    }

    public static Radio createKoulutustaustaRadio(final KoodistoService koodistoService, final Integer hakuvuosi,
                                                  final OrganizationService organisaatioService) {
        List<Code> baseEducationCodes = koodistoService.getCodes("pohjakoulutustoinenaste", 1);

        Map<String, Code> educationMap = Maps.uniqueIndex(baseEducationCodes, new Function<Code, String>() {
            @Override
            public String apply(Code code) {
                return code.getValue(); //NOSONAR
            }
        });

        Radio millatutkinnolla = new Radio(ELEMENT_ID_BASE_EDUCATION,
                createI18NText("form.koulutustausta.millaTutkinnolla", FORM_MESSAGES));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.peruskoulu", FORM_MESSAGES),
                educationMap.get(PERUSKOULU).getValue(),
                createI18NText("form.koulutustausta.peruskoulu.help", FORM_MESSAGES));
        millatutkinnolla
                .addOption(createI18NText("form.koulutustausta.osittainYksilollistetty", FORM_MESSAGES),
                        educationMap.get(OSITTAIN_YKSILOLLISTETTY).getValue(),
                        createI18NText("form.koulutustausta.osittainYksilollistetty.help", FORM_MESSAGES));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.erityisopetuksenYksilollistetty", FORM_MESSAGES),
                        ERITYISOPETUKSEN_YKSILOLLISTETTY,
                        createI18NText("form.koulutustausta.erityisopetuksenYksilollistetty.help", FORM_MESSAGES));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.yksilollistetty", FORM_MESSAGES),
                        educationMap.get(YKSILOLLISTETTY).getValue(),
                        createI18NText("form.koulutustausta.yksilollistetty.help", FORM_MESSAGES));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.keskeytynyt", FORM_MESSAGES),
                educationMap.get(KESKEYTYNYT).getValue(),
                createI18NText("form.koulutustausta.keskeytynyt", FORM_MESSAGES));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.lukio", FORM_MESSAGES),
                        educationMap.get(YLIOPPILAS).getValue(),
                        createI18NText("form.koulutustausta.lukio.help", FORM_MESSAGES));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.ulkomailla", FORM_MESSAGES),
                educationMap.get(ULKOMAINEN_TUTKINTO).getValue(),
                createI18NText("form.koulutustausta.ulkomailla.help", FORM_MESSAGES));
        ElementUtil.setVerboseHelp(millatutkinnolla, "form.koulutustausta.millaTutkinnolla.verboseHelp", FORM_VERBOSE_HELP);
        addRequiredValidator(millatutkinnolla, FORM_ERRORS);

        Notification tutkintoUlkomaillaNotification = new Notification(TUTKINTO_ULKOMAILLA_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.ulkomailla.huom", FORM_MESSAGES),
                Notification.NotificationType.INFO);

        Notification tutkintoKeskeytynytNotification = new Notification(TUTKINTO_KESKEYTNYT_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.keskeytynyt.huom", FORM_MESSAGES),
                Notification.NotificationType.INFO);

        RelatedQuestionRule keskeytynytRule = new RelatedQuestionRule(TUTKINTO_KESKEYTYNYT_RULE,
                millatutkinnolla.getId(), KESKEYTYNYT, false);

        RelatedQuestionRule ulkomaillaSuoritettuTutkintoRule = new RelatedQuestionRule(TUTKINTO_ULKOMAILLA_RULE,
                millatutkinnolla.getId(), ULKOMAINEN_TUTKINTO, false);

        ulkomaillaSuoritettuTutkintoRule.addChild(tutkintoUlkomaillaNotification);
        keskeytynytRule.addChild(tutkintoKeskeytynytNotification);
        millatutkinnolla.addChild(ulkomaillaSuoritettuTutkintoRule);
        millatutkinnolla.addChild(keskeytynytRule);

        TextQuestion paattotodistusvuosiPeruskoulu = new TextQuestion("PK_PAATTOTODISTUSVUOSI",
                createI18NText("form.koulutustausta.paattotodistusvuosi", FORM_MESSAGES));
        paattotodistusvuosiPeruskoulu.addAttribute("placeholder", "vvvv");
        addRequiredValidator(paattotodistusvuosiPeruskoulu, FORM_ERRORS);
        List<String> validYears = new ArrayList<String>(hakuvuosi - 1900 + 1);
        for (int year = 1900; year <= hakuvuosi; year++) {
            validYears.add(String.valueOf(year));
        }
        paattotodistusvuosiPeruskoulu.setValidator(
                createValueSetValidator(paattotodistusvuosiPeruskoulu.getId(), validYears, FORM_ERRORS));
        paattotodistusvuosiPeruskoulu.addAttribute("size", "4");
        paattotodistusvuosiPeruskoulu.addAttribute("maxlength", "4");

        TitledGroup suorittanutGroup = new TitledGroup("suorittanutgroup",
                createI18NText("form.koulutustausta.suorittanut", FORM_MESSAGES));
        suorittanutGroup.addChild(
                new CheckBox("LISAKOULUTUS_KYMPPI", createI18NText("form.koulutustausta.kymppiluokka", FORM_MESSAGES)),
                new CheckBox("LISAKOULUTUS_VAMMAISTEN", createI18NText("form.koulutustausta.vammaistenValmentava", FORM_MESSAGES)),
                new CheckBox("LISAKOULUTUS_TALOUS", createI18NText("form.koulutustausta.talouskoulu", FORM_MESSAGES)),
                new CheckBox("LISAKOULUTUS_AMMATTISTARTTI", createI18NText("form.koulutustausta.ammattistartti", FORM_MESSAGES)),
                new CheckBox("LISAKOULUTUS_KANSANOPISTO", createI18NText("form.koulutustausta.kansanopisto", FORM_MESSAGES)),
                new CheckBox("LISAKOULUTUS_MAAHANMUUTTO", createI18NText("form.koulutustausta.maahanmuuttajienValmistava",
                        FORM_MESSAGES))
        );

        RelatedQuestionRule pkKysymyksetRule = new RelatedQuestionRule("rule3", millatutkinnolla.getId(), "("
                + PERUSKOULU + "|"
                + OSITTAIN_YKSILOLLISTETTY + "|"
                + ERITYISOPETUKSEN_YKSILOLLISTETTY + "|"
                + YKSILOLLISTETTY + ")", false);

        RelatedQuestionRule paattotodistusvuosiPeruskouluRule = new RelatedQuestionRule("rule8",
                paattotodistusvuosiPeruskoulu.getId(), "^(19[0-9][0-9]|200[0-9]|201[0-1])$", false);

        Radio koulutuspaikkaAmmatillisenTutkintoon = new Radio(
                "KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON",
                createI18NText("form.koulutustausta.ammatillinenKoulutuspaikka", FORM_MESSAGES));
        addYesAndIDontOptions(koulutuspaikkaAmmatillisenTutkintoon, FORM_MESSAGES);
        addRequiredValidator(koulutuspaikkaAmmatillisenTutkintoon, FORM_ERRORS);

        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskoulu);
        pkKysymyksetRule.addChild(suorittanutGroup);
        pkKysymyksetRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);
        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskouluRule);

        TextQuestion lukioPaattotodistusVuosi = new TextQuestion("lukioPaattotodistusVuosi",
                createI18NText("form.koulutustausta.lukio.paattotodistusvuosi", FORM_MESSAGES));
        lukioPaattotodistusVuosi.addAttribute("placeholder", "vvvv");
        addRequiredValidator(lukioPaattotodistusVuosi, FORM_ERRORS);
        lukioPaattotodistusVuosi.setValidator(createRegexValidator(lukioPaattotodistusVuosi.getId(), PAATTOTODISTUSVUOSI_PATTERN,
                FORM_ERRORS));
        lukioPaattotodistusVuosi.addAttribute("size", "4");
        lukioPaattotodistusVuosi.addAttribute("maxlength", "4");
        lukioPaattotodistusVuosi.setInline(true);

        RelatedQuestionRule tuoreYoTodistus = new RelatedQuestionRule("tuoreYoTodistus", lukioPaattotodistusVuosi.getId(), String.valueOf(hakuvuosi), false);
        DropdownSelect lahtokoulu = new DropdownSelect("lahtokoulu", ElementUtil.createI18NAsIs("Valitse lähtökoulusi"), "");
        lahtokoulu.addOption(ElementUtil.createI18NAsIs(""), "");
        lahtokoulu.addOptions(koodistoService.getLukioKoulukoodit());
        addRequiredValidator(lahtokoulu, FORM_ERRORS);
        tuoreYoTodistus.addChild(lahtokoulu);

        DropdownSelect ylioppilastutkinto = new DropdownSelect("ylioppilastutkinto",
                createI18NText("form.koulutustausta.lukio.yotutkinto", FORM_MESSAGES), null);
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.fi", FORM_MESSAGES), "fi");
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.ib", FORM_MESSAGES), "ib");
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.eb", FORM_MESSAGES), "eb");
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.rp", FORM_MESSAGES), "rp");
        addRequiredValidator(ylioppilastutkinto, FORM_ERRORS);
        ylioppilastutkinto.setInline(true);
        setDefaultOption("fi", ylioppilastutkinto.getOptions());

        TitledGroup lukioGroup = new TitledGroup("lukioGroup", createI18NText("form.koulutustausta.lukio.suoritus",
                FORM_MESSAGES));
        lukioGroup.addChild(lukioPaattotodistusVuosi);
        lukioGroup.addChild(ylioppilastutkinto);

        RelatedQuestionRule lukioRule = new RelatedQuestionRule("rule7", millatutkinnolla.getId(), YLIOPPILAS, false);
        lukioRule.addChild(lukioGroup);

        lukioRule.addChild(tuoreYoTodistus);

        millatutkinnolla.addChild(lukioRule);
        millatutkinnolla.addChild(pkKysymyksetRule);

        Radio suorittanutAmmatillisenTutkinnon = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NText("form.koulutustausta.ammatillinenSuoritettu", FORM_MESSAGES));
        addYesAndIDontOptions(suorittanutAmmatillisenTutkinnon, FORM_MESSAGES);
        addRequiredValidator(suorittanutAmmatillisenTutkinnon, FORM_ERRORS);

        lukioRule.addChild(suorittanutAmmatillisenTutkinnon);
        paattotodistusvuosiPeruskouluRule.addChild(suorittanutAmmatillisenTutkinnon);

        RelatedQuestionRule suorittanutTutkinnonRule = new RelatedQuestionRule(ElementUtil.randomId(),
                suorittanutAmmatillisenTutkinnon.getId(), "^true", false);
        Notification warning = new Notification(
                ElementUtil.randomId(),
                createI18NText("form.koulutustausta.ammatillinenSuoritettu.huom", FORM_MESSAGES),
                Notification.NotificationType.INFO);
        suorittanutTutkinnonRule.addChild(warning);

        suorittanutAmmatillisenTutkinnon.addChild(suorittanutTutkinnonRule);

        DropdownSelect perusopetuksenKieli = new DropdownSelect("perusopetuksen_kieli",
                createI18NText("form.koulutustausta.perusopetuksenKieli", FORM_MESSAGES), null);
        perusopetuksenKieli.addOption(ElementUtil.createI18NAsIs(""), "");
        perusopetuksenKieli.addOptions(koodistoService.getTeachingLanguages());
        addRequiredValidator(perusopetuksenKieli, FORM_ERRORS);
        setVerboseHelp(perusopetuksenKieli, "form.koulutustausta.perusopetuksenKieli.verboseHelp", FORM_VERBOSE_HELP);
        pkKysymyksetRule.addChild(perusopetuksenKieli);

        DropdownSelect lukionKieli = new DropdownSelect("lukion_kieli",
                createI18NText("form.koulutustausta.lukionKieli", FORM_MESSAGES), null);
        lukionKieli.addOption(ElementUtil.createI18NAsIs(""), "");
        lukionKieli.addOptions(koodistoService.getTeachingLanguages());
        addRequiredValidator(lukionKieli, FORM_ERRORS);
        setVerboseHelp(lukionKieli, "form.koulutustausta.lukionKieli.verboseHelp", FORM_VERBOSE_HELP);
        lukioRule.addChild(lukionKieli);

        return millatutkinnolla;
    }

}
