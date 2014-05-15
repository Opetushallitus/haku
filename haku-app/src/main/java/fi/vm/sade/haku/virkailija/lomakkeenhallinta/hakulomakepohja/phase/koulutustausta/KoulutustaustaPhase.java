
package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Notification;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
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

import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder.DropdownSelect;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextAreaBuilder.TextArea;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class KoulutustaustaPhase {
    public static final String TUTKINTO_ULKOMAILLA_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO_KESKEYTNYT_NOTIFICATION_ID = "tutkinto5-notification";

    public static final String PAATTOTODISTUSVUOSI_PATTERN = "^(19[0-9][0-9]|200[0-9]|201[0-4])$";

    private KoulutustaustaPhase() {
    }

    public static Element create(final FormParameters formParameters) {
        Element koulutustausta = Phase("koulutustausta").build(formParameters);
        Element koulutustaustaRyhma = new ThemeBuilder("koulutustausta").previewable().build(formParameters);
        koulutustaustaRyhma.addChild(createKoulutustaustaRadio(formParameters));
        koulutustausta.addChild(koulutustaustaRyhma);
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
                createI18NText("form.koulutustausta.milla.tutkinnolla", formParameters.getFormMessagesBundle()));


        millatutkinnolla.addOption(createI18NText("form.koulutustausta.peruskoulu", formParameters.getFormMessagesBundle()),
                educationMap.get(PERUSKOULU).getValue(),
                createI18NText("form.koulutustausta.peruskoulu.help", formParameters.getFormMessagesBundle()));
        millatutkinnolla
                .addOption(createI18NText("form.koulutustausta.osittain.yksilollistetty", formParameters.getFormMessagesBundle()),
                        educationMap.get(OSITTAIN_YKSILOLLISTETTY).getValue(),
                        createI18NText("form.koulutustausta.osittain.yksilollistetty.help", formParameters.getFormMessagesBundle()));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.erityisopetuksen.yksilollistetty", formParameters.getFormMessagesBundle()),
                        ALUEITTAIN_YKSILOLLISTETTY,
                        createI18NText("form.koulutustausta.erityisopetuksen.yksilollistetty.help", formParameters.getFormMessagesBundle()));
        millatutkinnolla
                .addOption(
                        createI18NText("form.koulutustausta.yksilollistetty", formParameters.getFormMessagesBundle()),
                        educationMap.get(YKSILOLLISTETTY).getValue(),
                        createI18NText("form.koulutustausta.yksilollistetty.help", formParameters.getFormMessagesBundle()));
        millatutkinnolla.addOption(createI18NText("form.koulutustausta.keskeytynyt", formParameters.getFormMessagesBundle()),
                educationMap.get(KESKEYTYNYT).getValue(),
                createI18NText("form.koulutustausta.keskeytynyt", formParameters.getFormMessagesBundle()));

        if (!formParameters.isPervako()) {
            millatutkinnolla
                    .addOption(
                            createI18NText("form.koulutustausta.lukio", formParameters.getFormMessagesBundle()),
                            educationMap.get(YLIOPPILAS).getValue(),
                            createI18NText("form.koulutustausta.lukio.help", formParameters.getFormMessagesBundle()));
        }

        millatutkinnolla.addOption(createI18NText("form.koulutustausta.ulkomailla", formParameters),
                educationMap.get(ULKOMAINEN_TUTKINTO).getValue(),
                createI18NText("form.koulutustausta.ulkomailla.help", formParameters));
        ElementUtil.setVerboseHelp(millatutkinnolla, "form.koulutustausta.milla.tutkinnolla.verboseHelp", formParameters);
        addRequiredValidator(millatutkinnolla, formParameters);

        Notification tutkintoUlkomaillaNotification = new Notification(TUTKINTO_ULKOMAILLA_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.ulkomailla.huom", formParameters),
                Notification.NotificationType.INFO);


        Notification tutkintoKeskeytynytNotification = new Notification(TUTKINTO_KESKEYTNYT_NOTIFICATION_ID,
                createI18NText("form.koulutustausta.keskeytynyt.huom", formParameters),
                Notification.NotificationType.INFO);


        RelatedQuestionComplexRule keskeytynytRule = createVarEqualsToValueRule(millatutkinnolla.getId(), KESKEYTYNYT);
        keskeytynytRule.addChild(tutkintoKeskeytynytNotification);
        // Minkä koulutuksen olet suorittanut ulkomailla? (vapaatekstikenttä, 250 merkkiä

        RelatedQuestionComplexRule ulkomaillaSuoritettuTutkintoRule = createVarEqualsToValueRule(millatutkinnolla.getId(), ULKOMAINEN_TUTKINTO);
        if (formParameters.isPervako()) {
            ulkomaillaSuoritettuTutkintoRule.addChild(
                    TextArea("mika-ulkomainen-koulutus")
                            .cols(50)
                            .maxLength(250)
                            .build(formParameters));

        }
        ulkomaillaSuoritettuTutkintoRule.addChild(tutkintoUlkomaillaNotification);

        millatutkinnolla.addChild(ulkomaillaSuoritettuTutkintoRule);
        millatutkinnolla.addChild(keskeytynytRule);

        List<String> validYears = new ArrayList<String>(hakukausiVuosi - 1900 + 1);
        for (int year = 1900; year <= hakukausiVuosi; year++) {
            validYears.add(String.valueOf(year));
        }

        Element paattotodistusvuosiPeruskoulu = new TextQuestionBuilder(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI)
                .placeholder("vvvv")
                .labelKey("form.koulutustausta.paattotodistusvuosi")
                .required()
                .size(4)
                .maxLength(4)
                .validator(createValueSetValidator(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI, validYears, formParameters))
                .build(formParameters);

        Element suorittanutGroup =
                TitledGroup("suorittanut.ryhma").build(formParameters)
                        .addChild(
                                Checkbox(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_KYMPPI).build(formParameters),
                                Checkbox("LISAKOULUTUS_VAMMAISTEN").build(formParameters),
                                Checkbox("LISAKOULUTUS_TALOUS").build(formParameters),
                                Checkbox("LISAKOULUTUS_AMMATTISTARTTI").build(formParameters),
                                Checkbox("LISAKOULUTUS_KANSANOPISTO").build(formParameters),
                                Checkbox("LISAKOULUTUS_MAAHANMUUTTO").build(formParameters)
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


        if (!formParameters.isPervako()) {

            Element lukioPaattotodistusVuosi = TextQuestion(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI)
                    .placeholder("vvvv")
                    .maxLength(4)
                    .size(4)
                    .pattern(PAATTOTODISTUSVUOSI_PATTERN)
                    .required()
                    .inline()
                    .build(formParameters);

            RelatedQuestionComplexRule tuoreYoTodistus = createVarEqualsToValueRule(lukioPaattotodistusVuosi.getId(), hakukausiVuosiStr);
            tuoreYoTodistus.addChild(new DropdownSelectBuilder("lahtokoulu")
                    .defaultValueAttribute("")
                    .addOption(ElementUtil.createI18NText("form.koulutustausta.lukio.valitseOppilaitos", formParameters.getFormMessagesBundle(), true), "")
                    .addOptions(koodistoService.getLukioKoulukoodit())
                    .required()
                    .build(formParameters));

            RelatedQuestionComplexRule lukioRule = createVarEqualsToValueRule(millatutkinnolla.getId(), YLIOPPILAS);
            lukioRule.addChild(TitledGroup("lukio.suoritus").build(formParameters)
                    .addChild(lukioPaattotodistusVuosi,
                            new DropdownSelectBuilder(OppijaConstants.YLIOPPILASTUTKINTO)
                                    .defaultOption("fi")
                                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.fi", formParameters), OppijaConstants.YLIOPPILASTUTKINTO_FI)
                                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.ib", formParameters), "ib")
                                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.eb", formParameters), "eb")
                                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.rp", formParameters), "rp")
                                    .required()
                                    .inline()
                                    .build(formParameters)));


            lukioRule.addChild(tuoreYoTodistus);

            Radio suorittanutAmmatillisenTutkinnonLukio = new Radio(
                    "ammatillinenTutkintoSuoritettu",
                    createI18NText("form.koulutustausta.ammatillinenSuoritettu", formParameters));
            addYesAndIDontOptions(suorittanutAmmatillisenTutkinnonLukio, formParameters);
            addRequiredValidator(suorittanutAmmatillisenTutkinnonLukio, formParameters);
            lukioRule.addChild(suorittanutAmmatillisenTutkinnonLukio);


            lukioRule.addChild(
                    DropdownSelect(OppijaConstants.LUKIO_KIELI)
                            .emptyOption()
                            .addOptions(koodistoService.getTeachingLanguages())
                            .required()
                            .build(formParameters));

            millatutkinnolla.addChild(lukioRule);

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


        }
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


        pkKysymyksetRule.addChild(DropdownSelect(OppijaConstants.PERUSOPETUS_KIELI)
                .addOption(ElementUtil.createI18NAsIs(""), "")
                .addOptions(koodistoService.getTeachingLanguages())
                .required()
                .build(formParameters));


        return millatutkinnolla;
    }

}
