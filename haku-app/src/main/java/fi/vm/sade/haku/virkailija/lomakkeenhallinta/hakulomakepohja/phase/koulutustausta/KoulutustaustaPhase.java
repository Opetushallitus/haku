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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Notification;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.oppija.lomake.validation.validators.AlwaysFailsValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class KoulutustaustaPhase {
    public static final String TUTKINTO_ULKOMAILLA_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO_KESKEYTNYT_NOTIFICATION_ID = "tutkinto5-notification";

    public static final String PAATTOTODISTUSVUOSI_PATTERN = "^(19[0-9][0-9]|200[0-9]|201[0-4])$";

    private KoulutustaustaPhase() {
    }

    public static Phase create(final KoodistoService koodistoService, ApplicationSystem as,
                               final String formMessagesBundle, final String formErrorsBundle, final String formVerboseHelpBundle) {
        Phase koulutustausta = new Phase("koulutustausta", createI18NText("form.koulutustausta.otsikko",
                formMessagesBundle), false, Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));
        Theme koulutustaustaRyhma = new Theme("KoulutustaustaGrp", createI18NText("form.koulutustausta.otsikko",
                formMessagesBundle), true);
        koulutustausta.addChild(koulutustaustaRyhma);
        koulutustaustaRyhma.setHelp(createI18NText("form.koulutustausta.help", formMessagesBundle));
        koulutustaustaRyhma.addChild(createKoulutustaustaRadio(koodistoService, as.getHakukausiVuosi(), formMessagesBundle, formErrorsBundle, formVerboseHelpBundle));

        //Tätä ei kysytä syksyn yhteishaussa, tarvitaan myöhemmin.
        /*Radio osallistunut = new Radio("osallistunut", createI18NForm("form.koulutustausta.osallistunutPaasykokeisiin"));
        addYesAndIDontOptions(osallistunut);
        addRequiredValidator(osallistunut);
        setVerboseHelp(osallistunut);

        koulutustaustaRyhma.addChild(osallistunut);*/
        return koulutustausta;
    }

    public static Radio createKoulutustaustaRadio(final KoodistoService koodistoService, final Integer hakuvuosi,
                                                  final String formMessagesBundle, final String formErrorsBundle, final String formVerboseHelpBundle) {
        List<Code> baseEducationCodes = koodistoService.getCodes("pohjakoulutustoinenaste", 1);

        Map<String, Code> educationMap = Maps.uniqueIndex(baseEducationCodes, new Function<Code, String>() {
            @Override
            public String apply(Code code) {
                return code.getValue(); //NOSONAR
            }
        });

        Radio millatutkinnolla = new Radio(ELEMENT_ID_BASE_EDUCATION,
                createI18NText("form.koulutustausta.millaTutkinnolla", formMessagesBundle));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.peruskoulu", formMessagesBundle),
                educationMap.get(PERUSKOULU).getValue(),
                createI18NText("form.koulutustausta.peruskoulu.help", formMessagesBundle));
        millatutkinnolla
                .addOption(createI18NText("form.koulutustausta.osittainYksilollistetty", formMessagesBundle),
                        educationMap.get(OSITTAIN_YKSILOLLISTETTY).getValue(),
                        createI18NText("form.koulutustausta.osittainYksilollistetty.help", formMessagesBundle));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.erityisopetuksenYksilollistetty", formMessagesBundle),
                        ALUEITTAIN_YKSILOLLISTETTY,
                        createI18NText("form.koulutustausta.erityisopetuksenYksilollistetty.help", formMessagesBundle));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.yksilollistetty", formMessagesBundle),
                        educationMap.get(YKSILOLLISTETTY).getValue(),
                        createI18NText("form.koulutustausta.yksilollistetty.help", formMessagesBundle));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.keskeytynyt", formMessagesBundle),
                educationMap.get(KESKEYTYNYT).getValue(),
                createI18NText("form.koulutustausta.keskeytynyt", formMessagesBundle));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.lukio", formMessagesBundle),
                        educationMap.get(YLIOPPILAS).getValue(),
                        createI18NText("form.koulutustausta.lukio.help", formMessagesBundle));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.ulkomailla", formMessagesBundle),
                educationMap.get(ULKOMAINEN_TUTKINTO).getValue(),
                createI18NText("form.koulutustausta.ulkomailla.help", formMessagesBundle));
        ElementUtil.setVerboseHelp(millatutkinnolla, "form.koulutustausta.millaTutkinnolla.verboseHelp", formVerboseHelpBundle);
        addRequiredValidator(millatutkinnolla, formErrorsBundle);

        Notification tutkintoUlkomaillaNotification = new Notification(TUTKINTO_ULKOMAILLA_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.ulkomailla.huom", formMessagesBundle),
                Notification.NotificationType.INFO);

        Notification tutkintoKeskeytynytNotification = new Notification(TUTKINTO_KESKEYTNYT_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.keskeytynyt.huom", formMessagesBundle),
                Notification.NotificationType.INFO);

        RelatedQuestionComplexRule keskeytynytRule = createVarEqualsToValueRule(millatutkinnolla.getId(), KESKEYTYNYT);

        RelatedQuestionComplexRule ulkomaillaSuoritettuTutkintoRule = createVarEqualsToValueRule(millatutkinnolla.getId(), ULKOMAINEN_TUTKINTO);

        ulkomaillaSuoritettuTutkintoRule.addChild(tutkintoUlkomaillaNotification);
        keskeytynytRule.addChild(tutkintoKeskeytynytNotification);
        millatutkinnolla.addChild(ulkomaillaSuoritettuTutkintoRule);
        millatutkinnolla.addChild(keskeytynytRule);

        TextQuestion paattotodistusvuosiPeruskoulu = new TextQuestion(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI,
                createI18NText("form.koulutustausta.paattotodistusvuosi", formMessagesBundle));
        paattotodistusvuosiPeruskoulu.addAttribute("placeholder", "vvvv");
        addRequiredValidator(paattotodistusvuosiPeruskoulu, formErrorsBundle);
        List<String> validYears = new ArrayList<String>(hakuvuosi - 1900 + 1);
        for (int year = 1900; year <= hakuvuosi; year++) {
            validYears.add(String.valueOf(year));
        }
        paattotodistusvuosiPeruskoulu.setValidator(
                createValueSetValidator(paattotodistusvuosiPeruskoulu.getId(), validYears, formErrorsBundle));
        paattotodistusvuosiPeruskoulu.addAttribute("size", "4");
        paattotodistusvuosiPeruskoulu.addAttribute("maxlength", "4");

        TitledGroup suorittanutGroup = new TitledGroup("suorittanutgroup",
                createI18NText("form.koulutustausta.suorittanut", formMessagesBundle));
        suorittanutGroup.addChild(
                new CheckBox("LISAKOULUTUS_KYMPPI", createI18NText("form.koulutustausta.kymppiluokka", formMessagesBundle)),
                new CheckBox("LISAKOULUTUS_VAMMAISTEN", createI18NText("form.koulutustausta.vammaistenValmentava", formMessagesBundle)),
                new CheckBox("LISAKOULUTUS_TALOUS", createI18NText("form.koulutustausta.talouskoulu", formMessagesBundle)),
                new CheckBox("LISAKOULUTUS_AMMATTISTARTTI", createI18NText("form.koulutustausta.ammattistartti", formMessagesBundle)),
                new CheckBox("LISAKOULUTUS_KANSANOPISTO", createI18NText("form.koulutustausta.kansanopisto", formMessagesBundle)),
                new CheckBox("LISAKOULUTUS_MAAHANMUUTTO", createI18NText("form.koulutustausta.maahanmuuttajienValmistava",
                  formMessagesBundle))
        );

        RelatedQuestionComplexRule pkKysymyksetRule = createVarEqualsToValueRule(millatutkinnolla.getId(),
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

        RelatedQuestionComplexRule paattotodistusvuosiPeruskouluRule = createRegexpRule(paattotodistusvuosiPeruskoulu.getId(), "^(19[0-9][0-9]|200[0-9]|201[0-1])$");

        Radio koulutuspaikkaAmmatillisenTutkintoon = new Radio("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON",
                createI18NText("form.koulutustausta.ammatillinenKoulutuspaikka", formMessagesBundle));
        addDefaultTrueFalseOptions(koulutuspaikkaAmmatillisenTutkintoon, formMessagesBundle);
        addRequiredValidator(koulutuspaikkaAmmatillisenTutkintoon, formErrorsBundle);

        Expr vuosiSyotetty = new Regexp(paattotodistusvuosiPeruskoulu.getId(), PAATTOTODISTUSVUOSI_PATTERN);
        Expr kysytaankoKoulutuspaikka = new And(new Not(new Equals(new Variable(paattotodistusvuosiPeruskoulu.getId()), new Value(String.valueOf(hakuvuosi)))), vuosiSyotetty);

        RelatedQuestionComplexRule onkoTodistusSaatuKuluneenaVuonna = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoKoulutuspaikka);
        onkoTodistusSaatuKuluneenaVuonna.addChild(koulutuspaikkaAmmatillisenTutkintoon);

        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskoulu, suorittanutGroup,
                onkoTodistusSaatuKuluneenaVuonna, paattotodistusvuosiPeruskouluRule);

        TextQuestion lukioPaattotodistusVuosi = new TextQuestion(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI,
                createI18NText("form.koulutustausta.lukio.paattotodistusvuosi", formMessagesBundle));
        lukioPaattotodistusVuosi.addAttribute("placeholder", "vvvv");
        addRequiredValidator(lukioPaattotodistusVuosi, formErrorsBundle);
        lukioPaattotodistusVuosi.setValidator(createRegexValidator(lukioPaattotodistusVuosi.getId(), PAATTOTODISTUSVUOSI_PATTERN,
          formErrorsBundle));
        lukioPaattotodistusVuosi.addAttribute("size", "4");
        lukioPaattotodistusVuosi.addAttribute("maxlength", "4");
        lukioPaattotodistusVuosi.setInline(true);

        RelatedQuestionComplexRule tuoreYoTodistus = createVarEqualsToValueRule(lukioPaattotodistusVuosi.getId(), String.valueOf(hakuvuosi));
        DropdownSelect lahtokoulu = new DropdownSelect("lahtokoulu", ElementUtil.createI18NText("form.koulutustausta.lukio.oppilaitos", formMessagesBundle), "");
        lahtokoulu.addOption(ElementUtil.createI18NText("form.koulutustausta.lukio.valitseOppilaitos", formMessagesBundle, true), "");
        lahtokoulu.addOptions(koodistoService.getLukioKoulukoodit());
        addRequiredValidator(lahtokoulu, formErrorsBundle);
        tuoreYoTodistus.addChild(lahtokoulu);

        DropdownSelect ylioppilastutkinto = new DropdownSelect(OppijaConstants.YLIOPPILASTUTKINTO,
                createI18NText("form.koulutustausta.lukio.yotutkinto", formMessagesBundle), null);
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.fi", formMessagesBundle),
                OppijaConstants.YLIOPPILASTUTKINTO_FI);
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.ib", formMessagesBundle), "ib");
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.eb", formMessagesBundle), "eb");
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.rp", formMessagesBundle), "rp");
        addRequiredValidator(ylioppilastutkinto, formErrorsBundle);
        ylioppilastutkinto.setInline(true);
        setDefaultOption("fi", ylioppilastutkinto.getOptions());

        TitledGroup lukioGroup = new TitledGroup("lukioGroup", createI18NText("form.koulutustausta.lukio.suoritus",
          formMessagesBundle));
        lukioGroup.addChild(lukioPaattotodistusVuosi);
        lukioGroup.addChild(ylioppilastutkinto);

        RelatedQuestionComplexRule lukioRule = createVarEqualsToValueRule(millatutkinnolla.getId(), YLIOPPILAS);
        lukioRule.addChild(lukioGroup);

        lukioRule.addChild(tuoreYoTodistus);

        millatutkinnolla.addChild(lukioRule);
        millatutkinnolla.addChild(pkKysymyksetRule);

        Radio suorittanutAmmatillisenTutkinnon = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NText("form.koulutustausta.ammatillinenSuoritettu", formMessagesBundle));
        addYesAndIDontOptions(suorittanutAmmatillisenTutkinnon, formMessagesBundle);
        addRequiredValidator(suorittanutAmmatillisenTutkinnon, formErrorsBundle);


        paattotodistusvuosiPeruskouluRule.addChild(suorittanutAmmatillisenTutkinnon);

        RelatedQuestionComplexRule suorittanutTutkinnonRule = createRuleIfVariableIsTrue(ElementUtil.randomId(), suorittanutAmmatillisenTutkinnon.getId());
        Notification warning = new Notification(
                ElementUtil.randomId(),
                createI18NText("form.koulutustausta.ammatillinenSuoritettu.huom", formMessagesBundle),
                Notification.NotificationType.INFO);
        suorittanutTutkinnonRule.addChild(warning);

        suorittanutAmmatillisenTutkinnon.addChild(suorittanutTutkinnonRule);

        Radio suorittanutAmmatillisenTutkinnonLukio = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NText("form.koulutustausta.ammatillinenSuoritettu", formMessagesBundle));
        addYesAndIDontOptions(suorittanutAmmatillisenTutkinnonLukio, formMessagesBundle);
        addRequiredValidator(suorittanutAmmatillisenTutkinnonLukio, formErrorsBundle);

        lukioRule.addChild(suorittanutAmmatillisenTutkinnonLukio);

        RelatedQuestionComplexRule suorittanutTutkinnonLukioRule = createRuleIfVariableIsTrue(ElementUtil.randomId(),
                suorittanutAmmatillisenTutkinnonLukio.getId());
        Notification warningLukio = new Notification(
                ElementUtil.randomId(),
                createI18NText("form.koulutustausta.ammatillinenSuoritettu.lukio.huom", formMessagesBundle),
                Notification.NotificationType.WARNING);
        warningLukio.setValidator(new AlwaysFailsValidator(warningLukio.getId(), createI18NText("form.koulutustausta.ammatillinenSuoritettu.lukio.huom",
                formErrorsBundle)));
        suorittanutTutkinnonLukioRule.addChild(warningLukio);

        suorittanutAmmatillisenTutkinnonLukio.addChild(suorittanutTutkinnonLukioRule);


        DropdownSelect perusopetuksenKieli = new DropdownSelect(OppijaConstants.PERUSOPETUS_KIELI,
                createI18NText("form.koulutustausta.perusopetuksenKieli", formMessagesBundle), null);
        perusopetuksenKieli.addOption(ElementUtil.createI18NAsIs(""), "");
        perusopetuksenKieli.addOptions(koodistoService.getTeachingLanguages());
        addRequiredValidator(perusopetuksenKieli, formErrorsBundle);
        setVerboseHelp(perusopetuksenKieli, "form.koulutustausta.perusopetuksenKieli.verboseHelp", formVerboseHelpBundle);
        pkKysymyksetRule.addChild(perusopetuksenKieli);

        DropdownSelect lukionKieli = new DropdownSelect(OppijaConstants.LUKIO_KIELI,
                createI18NText("form.koulutustausta.lukionKieli", formMessagesBundle), null);
        lukionKieli.addOption(ElementUtil.createI18NAsIs(""), "");
        lukionKieli.addOptions(koodistoService.getTeachingLanguages());
        addRequiredValidator(lukionKieli, formErrorsBundle);
        setVerboseHelp(lukionKieli, "form.koulutustausta.lukionKieli.verboseHelp", formVerboseHelpBundle);
        lukioRule.addChild(lukionKieli);

        return millatutkinnolla;
    }

}
