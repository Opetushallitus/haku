package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.koulutustausta;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.common.koodisto.domain.Code;
import fi.vm.sade.oppija.lomake.domain.elements.Notification;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import java.util.List;
import java.util.Map;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants.*;

public class KoulutustaustaPhase {
    public static final String TUTKINTO_ULKOMAILLA_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO_KESKEYTNYT_NOTIFICATION_ID = "tutkinto5-notification";
    public static final String TUTKINTO_OSITTAIN_YKSILOLLISTETTY = "tutkinto2";
    public static final String TUTKINTO_ERITYISOPETUKSEN_YKSILOLLISTETTY = "tutkinto3";
    public static final String TUTKINTO_YKSILOLLISTETTY = "tutkinto6";
    public static final String TUTKINTO_KESKEYTYNYT = "tutkinto7";
    public static final String TUTKINTO_YLIOPPILAS = "tutkinto9";
    public static final String TUTKINTO_ULKOMAINEN_TUTKINTO = "tutkinto0";
    public static final String TUTKINTO_KESKEYTYNYT_RULE = "tutkinto_7_rule";
    public static final String TUTKINTO_ULKOMAILLA_RULE = "tutkinto_0_rule";
    public static final String TUTKINTO_PERUSKOULU = "tutkinto1";

    public static Phase create(final KoodistoService koodistoService) {
        Phase koulutustausta = new Phase("koulutustausta", createI18NForm("form.koulutustausta.otsikko"), false);
        Theme koulutustaustaRyhma = new Theme("KoulutustaustaGrp", createI18NForm("form.koulutustausta.otsikko"), null, true);
        koulutustausta.addChild(koulutustaustaRyhma);
        koulutustaustaRyhma.setHelp(createI18NForm("form.koulutustausta.help"));
        koulutustaustaRyhma.addChild(createKoulutustaustaRadio(koodistoService));

        Radio osallistunut = new Radio("osallistunut", createI18NForm("form.koulutustausta.osallistunutPaasykokeisiin"));
        addDefaultTrueFalseOptions(osallistunut);
        setRequired(osallistunut);
        setVerboseHelp(osallistunut);

        koulutustaustaRyhma.addChild(osallistunut);
        return koulutustausta;
    }

    public static final Radio createKoulutustaustaRadio(final KoodistoService koodistoService) {
        List<Code> baseEducationCodes = koodistoService.getCodes("pohjakoulutustoinenaste", 1);

        Map<String, Code> educationMap = Maps.uniqueIndex(baseEducationCodes, new Function<Code, String>() {
            @Override
            public String apply(fi.vm.sade.oppija.common.koodisto.domain.Code input) {
                return input.getValue();
            }
        });

        Radio millatutkinnolla = new Radio("POHJAKOULUTUS",
                createI18NForm("form.koulutustausta.millaTutkinnolla"));
        millatutkinnolla.addOption(TUTKINTO_PERUSKOULU, createI18NForm("form.koulutustausta.peruskoulu"),
                educationMap.get(PERUSKOULU).getValue(),
                createI18NForm("form.koulutustausta.peruskoulu.help"));
        millatutkinnolla
                .addOption(TUTKINTO_OSITTAIN_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.osittainYksilollistetty"),
                        educationMap.get(OSITTAIN_YKSILOLLISTETTY).getValue(),
                        createI18NForm("form.koulutustausta.osittainYksilollistetty.help"));
        millatutkinnolla
                .addOption(
                        TUTKINTO_ERITYISOPETUKSEN_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.erityisopetuksenYksilollistetty"),
                        ERITYISOPETUKSEN_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.erityisopetuksenYksilollistetty.help"));
        millatutkinnolla
                .addOption(
                        TUTKINTO_YKSILOLLISTETTY,
                        createI18NForm("form.koulutustausta.yksilollistetty"),
                        educationMap.get(YKSILOLLISTETTY).getValue(),
                        createI18NForm("form.koulutustausta.yksilollistetty.help"));
        millatutkinnolla.addOption(TUTKINTO_KESKEYTYNYT,
                createI18NForm("form.koulutustausta.keskeytynyt"),
                educationMap.get(KESKEYTYNYT).getValue(),
                createI18NForm("form.koulutustausta.keskeytynyt"));
        millatutkinnolla
                .addOption(
                        TUTKINTO_YLIOPPILAS,
                        createI18NForm("form.koulutustausta.lukio"),
                        educationMap.get(YLIOPPILAS).getValue(),
                        createI18NForm("form.koulutustausta.lukio.help"));
        millatutkinnolla.addOption(TUTKINTO_ULKOMAINEN_TUTKINTO, createI18NForm("form.koulutustausta.ulkomailla"),
                educationMap.get(ULKOMAINEN_TUTKINTO).getValue(),
                createI18NForm("form.koulutustausta.ulkomailla.help"));
        ElementUtil.setVerboseHelp(millatutkinnolla);
        millatutkinnolla.addAttribute("required", "required");

        Notification tutkintoUlkomaillaNotification = new Notification(TUTKINTO_ULKOMAILLA_NOTIFICATION_ID,
                createI18NForm("form.koulutustausta.ulkomailla.huom"),
                Notification.NotificationType.INFO);

        Notification tutkintoKeskeytynytNotification = new Notification(TUTKINTO_KESKEYTNYT_NOTIFICATION_ID,
                createI18NForm("form.koulutustausta.keskeytynyt.huom"),
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
                createI18NForm("form.koulutustausta.paattotodistusvuosi"));
        paattotodistusvuosiPeruskoulu.addAttribute("placeholder", "vvvv");
        paattotodistusvuosiPeruskoulu.addAttribute("required", "required");
        paattotodistusvuosiPeruskoulu.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        paattotodistusvuosiPeruskoulu.addAttribute("size", "4");
        paattotodistusvuosiPeruskoulu.addAttribute("maxlength", "4");

        TitledGroup suorittanutGroup = new TitledGroup("suorittanutgroup",
                createI18NForm("form.koulutustausta.suorittanut"));
        suorittanutGroup.addChild(
                new CheckBox("LISAKOULUTUS_KYMPPI", createI18NForm("form.koulutustausta.kymppiluokka")),
                new CheckBox("LISAKOULUTUS_VAMMAISTEN", createI18NForm("form.koulutustausta.vammaistenValmentava")),
                new CheckBox("LISAKOULUTUS_TALOUS", createI18NForm("form.koulutustausta.talouskoulu")),
                new CheckBox("LISAKOULUTUS_AMMATTISTARTTI", createI18NForm("form.koulutustausta.ammattistartti")),
                new CheckBox("LISAKOULUTUS_KANSANOPISTO", createI18NForm("form.koulutustausta.kansanopisto")),
                new CheckBox("LISAKOULUTUS_MAAHANMUUTTO", createI18NForm("form.koulutustausta.maahanmuuttajienValmistava"))
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
                createI18NForm("form.koulutustausta.ammatillinenKoulutuspaikka"));
        addDefaultTrueFalseOptions(koulutuspaikkaAmmatillisenTutkintoon);
        setRequired(koulutuspaikkaAmmatillisenTutkintoon);

        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskoulu);
        pkKysymyksetRule.addChild(suorittanutGroup);
        pkKysymyksetRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);
        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskouluRule);

        TextQuestion lukioPaattotodistusVuosi = new TextQuestion("lukioPaattotodistusVuosi",
                createI18NForm("form.koulutustausta.lukio.paattotodistusvuosi"));
        lukioPaattotodistusVuosi.addAttribute("placeholder", "vvvv");
        lukioPaattotodistusVuosi.addAttribute("required", "required");
        lukioPaattotodistusVuosi.addAttribute("pattern", "^(19[0-9][0-9]|200[0-9]|201[0-3])$");
        lukioPaattotodistusVuosi.addAttribute("size", "4");
        lukioPaattotodistusVuosi.addAttribute("maxlength", "4");
        lukioPaattotodistusVuosi.setInline(true);

        DropdownSelect ylioppilastutkinto = new DropdownSelect("ylioppilastutkinto",
                createI18NForm("form.koulutustausta.lukio.yotutkinto"), null);
        ylioppilastutkinto.addOption("fi", createI18NForm("form.koulutustausta.lukio.yotutkinto.fi"), "fi");
        ylioppilastutkinto.addOption("ib", createI18NForm("form.koulutustausta.lukio.yotutkinto.ib"), "ib");
        ylioppilastutkinto.addOption("eb", createI18NForm("form.koulutustausta.lukio.yotutkinto.eb"), "eb");
        ylioppilastutkinto.addOption("rp", createI18NForm("form.koulutustausta.lukio.yotutkinto.rp"), "rp");
        ylioppilastutkinto.addAttribute("required", "required");
        ylioppilastutkinto.setInline(true);
        setDefaultOption("fi", ylioppilastutkinto.getOptions());

        TitledGroup lukioGroup = new TitledGroup("lukioGroup", createI18NForm("form.koulutustausta.lukio.suoritus"));
        lukioGroup.addChild(lukioPaattotodistusVuosi);
        lukioGroup.addChild(ylioppilastutkinto);

        RelatedQuestionRule lukioRule = new RelatedQuestionRule("rule7", millatutkinnolla.getId(), YLIOPPILAS, false);
        lukioRule.addChild(lukioGroup);

        millatutkinnolla.addChild(lukioRule);
        millatutkinnolla.addChild(pkKysymyksetRule);

        Radio suorittanutAmmatillisenTutkinnon = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NForm("form.koulutustausta.ammatillinenSuoritettu"));
        addDefaultTrueFalseOptions(suorittanutAmmatillisenTutkinnon);
        setRequired(suorittanutAmmatillisenTutkinnon);


        lukioRule.addChild(suorittanutAmmatillisenTutkinnon);
        lukioRule.addChild(koulutuspaikkaAmmatillisenTutkintoon);
        paattotodistusvuosiPeruskouluRule.addChild(suorittanutAmmatillisenTutkinnon);


        suorittanutAmmatillisenTutkinnon.addChild(
                notificationWhenTrue(
                        suorittanutAmmatillisenTutkinnon.getId(),
                        "form.koulutustausta.ammatillinenSuoritettu.huom"));

        DropdownSelect perusopetuksenKieli = new DropdownSelect("perusopetuksen_kieli",
                createI18NForm("form.koulutustausta.perusopetuksenKieli"), null);
        perusopetuksenKieli.addOption(ElementUtil.randomId(), ElementUtil.createI18NForm(null), "");
        perusopetuksenKieli.addOptions(koodistoService.getLanguages());
        setRequired(perusopetuksenKieli);
        setVerboseHelp(perusopetuksenKieli);
        pkKysymyksetRule.addChild(perusopetuksenKieli);
        return millatutkinnolla;
    }
}
