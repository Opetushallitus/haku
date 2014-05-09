
package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
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

    public static Phase create(final FormParameters formParameters) {
        Phase koulutustausta = new Phase("koulutustausta", createI18NText("form.koulutustausta.otsikko",
                formParameters.getFormMessagesBundle()), false, Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));
        Theme koulutustaustaRyhma = new Theme("KoulutustaustaGrp", createI18NText("form.koulutustausta.otsikko",
                formParameters.getFormMessagesBundle()), true);
        koulutustausta.addChild(koulutustaustaRyhma);
        koulutustaustaRyhma.setHelp(createI18NText("form.koulutustausta.help", formParameters.getFormMessagesBundle()));
        koulutustaustaRyhma.addChild(createKoulutustaustaRadio(formParameters));
        return koulutustausta;
    }


    public static Radio createKoulutustaustaRadio(final FormParameters formParameters) {
        Integer hakukausiVuosi = formParameters.getApplicationSystem().getHakukausiVuosi();
        String hakukausiVuosiStr = String.valueOf(hakukausiVuosi);
        KoodistoService koodistoService = formParameters.getKoodistoService();

        List<Code> baseEducationCodes = koodistoService.getCodes("pohjakoulutustoinenaste", 1);

        Map<String, Code> educationMap = Maps.uniqueIndex(baseEducationCodes, new Function<Code, String>() {
            @Override
            public String apply(Code code) {
                return code.getValue(); //NOSONAR
            }
        });

        Radio millatutkinnolla = new Radio(ELEMENT_ID_BASE_EDUCATION,
                createI18NText("form.koulutustausta.millaTutkinnolla", formParameters.getFormMessagesBundle()));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.peruskoulu", formParameters.getFormMessagesBundle()),
                educationMap.get(PERUSKOULU).getValue(),
                createI18NText("form.koulutustausta.peruskoulu.help", formParameters.getFormMessagesBundle()));
        millatutkinnolla
                .addOption(createI18NText("form.koulutustausta.osittainYksilollistetty", formParameters.getFormMessagesBundle()),
                        educationMap.get(OSITTAIN_YKSILOLLISTETTY).getValue(),
                        createI18NText("form.koulutustausta.osittainYksilollistetty.help", formParameters.getFormMessagesBundle()));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.erityisopetuksenYksilollistetty", formParameters.getFormMessagesBundle()),
                        ALUEITTAIN_YKSILOLLISTETTY,
                        createI18NText("form.koulutustausta.erityisopetuksenYksilollistetty.help", formParameters.getFormMessagesBundle()));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.yksilollistetty", formParameters.getFormMessagesBundle()),
                        educationMap.get(YKSILOLLISTETTY).getValue(),
                        createI18NText("form.koulutustausta.yksilollistetty.help", formParameters.getFormMessagesBundle()));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.keskeytynyt", formParameters.getFormMessagesBundle()),
                educationMap.get(KESKEYTYNYT).getValue(),
                createI18NText("form.koulutustausta.keskeytynyt", formParameters.getFormMessagesBundle()));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.lukio", formParameters.getFormMessagesBundle()),
                        educationMap.get(YLIOPPILAS).getValue(),
                        createI18NText("form.koulutustausta.lukio.help", formParameters.getFormMessagesBundle()));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.ulkomailla", formParameters),
                educationMap.get(ULKOMAINEN_TUTKINTO).getValue(),
                createI18NText("form.koulutustausta.ulkomailla.help", formParameters));
        ElementUtil.setVerboseHelp(millatutkinnolla, "form.koulutustausta.millaTutkinnolla.verboseHelp", formParameters);
        addRequiredValidator(millatutkinnolla, formParameters);

        Notification tutkintoUlkomaillaNotification = new Notification(TUTKINTO_ULKOMAILLA_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.ulkomailla.huom", formParameters),
                Notification.NotificationType.INFO);


        Notification tutkintoKeskeytynytNotification = new Notification(TUTKINTO_KESKEYTNYT_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.keskeytynyt.huom", formParameters),
                Notification.NotificationType.INFO);

        RelatedQuestionComplexRule keskeytynytRule = createVarEqualsToValueRule(millatutkinnolla.getId(), KESKEYTYNYT);

        RelatedQuestionComplexRule ulkomaillaSuoritettuTutkintoRule = createVarEqualsToValueRule(millatutkinnolla.getId(), ULKOMAINEN_TUTKINTO);

        ulkomaillaSuoritettuTutkintoRule.addChild(tutkintoUlkomaillaNotification);
        keskeytynytRule.addChild(tutkintoKeskeytynytNotification);
        millatutkinnolla.addChild(ulkomaillaSuoritettuTutkintoRule);
        millatutkinnolla.addChild(keskeytynytRule);

        TextQuestion paattotodistusvuosiPeruskoulu = new TextQuestion(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI,
                createI18NText("form.koulutustausta.paattotodistusvuosi", formParameters));
        paattotodistusvuosiPeruskoulu.addAttribute("placeholder", "vvvv");
        addRequiredValidator(paattotodistusvuosiPeruskoulu, formParameters);
        List<String> validYears = new ArrayList<String>(hakukausiVuosi - 1900 + 1);
        for (int year = 1900; year <= hakukausiVuosi; year++) {
            validYears.add(String.valueOf(year));
        }
        paattotodistusvuosiPeruskoulu.setValidator(
                createValueSetValidator(paattotodistusvuosiPeruskoulu.getId(), validYears, formParameters));
        paattotodistusvuosiPeruskoulu.addAttribute("size", "4");
        paattotodistusvuosiPeruskoulu.addAttribute("maxlength", "4");

        TitledGroup suorittanutGroup = new TitledGroup("suorittanutgroup",
                createI18NText("form.koulutustausta.suorittanut", formParameters));
        suorittanutGroup.addChild(
                new CheckBox(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_KYMPPI, createI18NText("form.koulutustausta.kymppiluokka", formParameters)),
                new CheckBox("LISAKOULUTUS_VAMMAISTEN", createI18NText("form.koulutustausta.vammaistenValmentava", formParameters)),
                new CheckBox("LISAKOULUTUS_TALOUS", createI18NText("form.koulutustausta.talouskoulu", formParameters)),
                new CheckBox("LISAKOULUTUS_AMMATTISTARTTI", createI18NText("form.koulutustausta.ammattistartti", formParameters)),
                new CheckBox("LISAKOULUTUS_KANSANOPISTO", createI18NText("form.koulutustausta.kansanopisto", formParameters)),
                new CheckBox("LISAKOULUTUS_MAAHANMUUTTO", createI18NText("form.koulutustausta.maahanmuuttajienValmistava",
                        formParameters))
        );

        RelatedQuestionComplexRule pkKysymyksetRule = createVarEqualsToValueRule(millatutkinnolla.getId(),
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

        RelatedQuestionComplexRule paattotodistusvuosiPeruskouluRule = createRegexpRule(paattotodistusvuosiPeruskoulu.getId(), "^(19[0-9][0-9]|200[0-9]|201[0-1])$");

        Radio koulutuspaikkaAmmatillisenTutkintoon = new Radio("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON",
                createI18NText("form.koulutustausta.ammatillinenKoulutuspaikka", formParameters));
        addDefaultTrueFalseOptions(koulutuspaikkaAmmatillisenTutkintoon, formParameters);
        addRequiredValidator(koulutuspaikkaAmmatillisenTutkintoon, formParameters);

        Expr vuosiSyotetty = new Regexp(paattotodistusvuosiPeruskoulu.getId(), PAATTOTODISTUSVUOSI_PATTERN);
        Expr kysytaankoKoulutuspaikka = new And(new Not(new Equals(new Variable(paattotodistusvuosiPeruskoulu.getId()), new Value(hakukausiVuosiStr))), vuosiSyotetty);

        RelatedQuestionComplexRule onkoTodistusSaatuKuluneenaVuonna = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoKoulutuspaikka);
        onkoTodistusSaatuKuluneenaVuonna.addChild(koulutuspaikkaAmmatillisenTutkintoon);

        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskoulu, suorittanutGroup,
                onkoTodistusSaatuKuluneenaVuonna, paattotodistusvuosiPeruskouluRule);

        TextQuestion lukioPaattotodistusVuosi = new TextQuestion(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI,
                createI18NText("form.koulutustausta.lukio.paattotodistusvuosi", formParameters));
        lukioPaattotodistusVuosi.addAttribute("placeholder", "vvvv");
        addRequiredValidator(lukioPaattotodistusVuosi, formParameters);
        lukioPaattotodistusVuosi.setValidator(createRegexValidator(lukioPaattotodistusVuosi.getId(), PAATTOTODISTUSVUOSI_PATTERN,
                formParameters));
        lukioPaattotodistusVuosi.addAttribute("size", "4");
        lukioPaattotodistusVuosi.addAttribute("maxlength", "4");
        lukioPaattotodistusVuosi.setInline(true);

        RelatedQuestionComplexRule tuoreYoTodistus = createVarEqualsToValueRule(lukioPaattotodistusVuosi.getId(), hakukausiVuosiStr);
        DropdownSelect lahtokoulu = new DropdownSelect("lahtokoulu", ElementUtil.createI18NText("form.koulutustausta.lukio.oppilaitos", formParameters), "");
        lahtokoulu.addOption(ElementUtil.createI18NText("form.koulutustausta.lukio.valitseOppilaitos", formParameters.getFormMessagesBundle(), true), "");
        lahtokoulu.addOptions(koodistoService.getLukioKoulukoodit());
        addRequiredValidator(lahtokoulu, formParameters);
        tuoreYoTodistus.addChild(lahtokoulu);

        DropdownSelect ylioppilastutkinto = new DropdownSelect(OppijaConstants.YLIOPPILASTUTKINTO,
                createI18NText("form.koulutustausta.lukio.yotutkinto", formParameters), null);
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.fi", formParameters),
                OppijaConstants.YLIOPPILASTUTKINTO_FI);
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.ib", formParameters), "ib");
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.eb", formParameters), "eb");
        ylioppilastutkinto.addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.rp", formParameters), "rp");
        addRequiredValidator(ylioppilastutkinto, formParameters);
        ylioppilastutkinto.setInline(true);
        setDefaultOption("fi", ylioppilastutkinto.getOptions());

        TitledGroup lukioGroup = new TitledGroup("lukioGroup", createI18NText("form.koulutustausta.lukio.suoritus",
                formParameters));
        lukioGroup.addChild(lukioPaattotodistusVuosi);
        lukioGroup.addChild(ylioppilastutkinto);

        RelatedQuestionComplexRule lukioRule = createVarEqualsToValueRule(millatutkinnolla.getId(), YLIOPPILAS);
        lukioRule.addChild(lukioGroup);

        lukioRule.addChild(tuoreYoTodistus);

        millatutkinnolla.addChild(lukioRule);
        millatutkinnolla.addChild(pkKysymyksetRule);

        Radio suorittanutAmmatillisenTutkinnon = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NText("form.koulutustausta.ammatillinenSuoritettu", formParameters));
        addYesAndIDontOptions(suorittanutAmmatillisenTutkinnon, formParameters);
        addRequiredValidator(suorittanutAmmatillisenTutkinnon, formParameters);


        paattotodistusvuosiPeruskouluRule.addChild(suorittanutAmmatillisenTutkinnon);

        RelatedQuestionComplexRule suorittanutTutkinnonRule = createRuleIfVariableIsTrue(ElementUtil.randomId(), suorittanutAmmatillisenTutkinnon.getId());
        Notification warning = new Notification(
                ElementUtil.randomId(),
                createI18NText("form.koulutustausta.ammatillinenSuoritettu.huom", formParameters),
                Notification.NotificationType.INFO);
        suorittanutTutkinnonRule.addChild(warning);

        suorittanutAmmatillisenTutkinnon.addChild(suorittanutTutkinnonRule);

        Radio suorittanutAmmatillisenTutkinnonLukio = new Radio(
                "ammatillinenTutkintoSuoritettu",
                createI18NText("form.koulutustausta.ammatillinenSuoritettu", formParameters));
        addYesAndIDontOptions(suorittanutAmmatillisenTutkinnonLukio, formParameters);
        addRequiredValidator(suorittanutAmmatillisenTutkinnonLukio, formParameters);

        lukioRule.addChild(suorittanutAmmatillisenTutkinnonLukio);

        RelatedQuestionComplexRule suorittanutTutkinnonLukioRule = createRuleIfVariableIsTrue(ElementUtil.randomId(),
                suorittanutAmmatillisenTutkinnonLukio.getId());
        Notification warningLukio = new Notification(
                ElementUtil.randomId(),
                createI18NText("form.koulutustausta.ammatillinenSuoritettu.lukio.huom", formParameters),
                Notification.NotificationType.WARNING);
        warningLukio.setValidator(new AlwaysFailsValidator(warningLukio.getId(), createI18NText("form.koulutustausta.ammatillinenSuoritettu.lukio.huom",
                formParameters)));
        suorittanutTutkinnonLukioRule.addChild(warningLukio);

        suorittanutAmmatillisenTutkinnonLukio.addChild(suorittanutTutkinnonLukioRule);


        DropdownSelect perusopetuksenKieli = new DropdownSelect(OppijaConstants.PERUSOPETUS_KIELI,
                createI18NText("form.koulutustausta.perusopetuksenKieli", formParameters), null);
        perusopetuksenKieli.addOption(ElementUtil.createI18NAsIs(""), "");
        perusopetuksenKieli.addOptions(koodistoService.getTeachingLanguages());
        addRequiredValidator(perusopetuksenKieli, formParameters);
        setVerboseHelp(perusopetuksenKieli, "form.koulutustausta.perusopetuksenKieli.verboseHelp", formParameters);
        pkKysymyksetRule.addChild(perusopetuksenKieli);

        DropdownSelect lukionKieli = new DropdownSelect(OppijaConstants.LUKIO_KIELI,
                createI18NText("form.koulutustausta.lukionKieli", formParameters), null);
        lukionKieli.addOption(ElementUtil.createI18NAsIs(""), "");
        lukionKieli.addOptions(koodistoService.getTeachingLanguages());
        addRequiredValidator(lukionKieli, formParameters);
        setVerboseHelp(lukionKieli, "form.koulutustausta.lukionKieli.verboseHelp", formParameters);
        lukioRule.addChild(lukionKieli);

        return millatutkinnolla;
    }

}
