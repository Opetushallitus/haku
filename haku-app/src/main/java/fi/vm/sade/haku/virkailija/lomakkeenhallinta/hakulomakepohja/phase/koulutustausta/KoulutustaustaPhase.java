
package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Notification;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
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
import static fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder.Dropdown;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextAreaBuilder.TextArea;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class KoulutustaustaPhase {
    public static final String TUTKINTO_ULKOMAILLA_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO_KESKEYTNYT_NOTIFICATION_ID = "tutkinto5-notification";

    public static final String PAATTOTODISTUSVUOSI_PATTERN = "^(19[0-9][0-9]|200[0-9]|201[0-4])$";
    public static final int TEXT_AREA_COLS = 60;

    private KoulutustaustaPhase() {
    }

    public static Element create(final FormParameters formParameters) {
        Element koulutustausta = Phase("koulutustausta").formParams(formParameters).build();
        Element koulutustaustaRyhma = new ThemeBuilder("koulutustausta").previewable().formParams(formParameters).build();
        koulutustaustaRyhma.addChild(createKoulutustaustaRadio(formParameters));
        koulutustausta.addChild(koulutustaustaRyhma);
        return koulutustausta;
    }


    public static Element createKoulutustaustaRadio(final FormParameters formParameters) {
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

        RadioBuilder baseEducationBuilder = Radio(ELEMENT_ID_BASE_EDUCATION)
                .addOption(educationMap.get(PERUSKOULU).getValue(), formParameters)
                .addOption(educationMap.get(OSITTAIN_YKSILOLLISTETTY).getValue(), formParameters)
                .addOption(ALUEITTAIN_YKSILOLLISTETTY, formParameters)
                .addOption(educationMap.get(YKSILOLLISTETTY).getValue(), formParameters)
                .addOption(educationMap.get(KESKEYTYNYT).getValue(), formParameters);


        if (!formParameters.isPervako()) {
            baseEducationBuilder.addOption(educationMap.get(YLIOPPILAS).getValue(), formParameters);
        }
        baseEducationBuilder.addOption(educationMap.get(ULKOMAINEN_TUTKINTO).getValue(), formParameters);


        Element baseEducation = baseEducationBuilder.required().formParams(formParameters).build();

        RelatedQuestionComplexRule keskeytynytRule = createVarEqualsToValueRule(baseEducation.getId(), KESKEYTYNYT);
        if (!formParameters.isPervako()) {
            keskeytynytRule.addChild(new Notification(TUTKINTO_KESKEYTNYT_NOTIFICATION_ID,
                    createI18NText("form.koulutustausta.keskeytynyt.huom", formParameters),
                    Notification.NotificationType.INFO));
        }


        RelatedQuestionComplexRule ulkomaillaSuoritettuTutkintoRule = createVarEqualsToValueRule(baseEducation.getId(), ULKOMAINEN_TUTKINTO);
        if (formParameters.isPervako()) {
            ulkomaillaSuoritettuTutkintoRule.addChild(
                    TextArea("mika-ulkomainen-koulutus")
                            .cols(TEXT_AREA_COLS)
                            .maxLength(250)
                            .formParams(formParameters).build());

        }

        if (!formParameters.isPervako()) {
            Notification tutkintoUlkomaillaNotification = new Notification(TUTKINTO_ULKOMAILLA_NOTIFICATION_ID,
                    createI18NText("form.koulutustausta.ulkomailla.huom", formParameters),
                    Notification.NotificationType.INFO);
            ulkomaillaSuoritettuTutkintoRule.addChild(tutkintoUlkomaillaNotification);
        }

        baseEducation.addChild(ulkomaillaSuoritettuTutkintoRule);
        baseEducation.addChild(keskeytynytRule);

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
                .formParams(formParameters).build();

        Element suorittanutGroup =
                TitledGroup("suorittanut.ryhma").formParams(formParameters).build()
                        .addChild(
                                Checkbox(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_KYMPPI).formParams(formParameters).build(),
                                Checkbox("LISAKOULUTUS_VAMMAISTEN").formParams(formParameters).build(),
                                Checkbox("LISAKOULUTUS_TALOUS").formParams(formParameters).build(),
                                Checkbox("LISAKOULUTUS_AMMATTISTARTTI").formParams(formParameters).build(),
                                Checkbox("LISAKOULUTUS_KANSANOPISTO").formParams(formParameters).build(),
                                Checkbox("LISAKOULUTUS_MAAHANMUUTTO").formParams(formParameters).build()
                        );

        RelatedQuestionComplexRule pkKysymyksetRule = createVarEqualsToValueRule(baseEducation.getId(),
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

        RelatedQuestionComplexRule paattotodistusvuosiPeruskouluRule = createRegexpRule(paattotodistusvuosiPeruskoulu.getId(), "^(19[0-9][0-9]|200[0-9]|201[0-1])$");

        Element koulutuspaikkaAmmatillisenTutkintoon = Radio("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .required()
                .formParams(formParameters).build();

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
                    .formParams(formParameters).build();

            RelatedQuestionComplexRule tuoreYoTodistus = createVarEqualsToValueRule(lukioPaattotodistusVuosi.getId(), hakukausiVuosiStr);
            tuoreYoTodistus.addChild(new DropdownSelectBuilder("lahtokoulu")
                    .defaultValueAttribute("")
                    .addOption(addSpaceAtTheBeginning(ElementUtil.createI18NText("form.koulutustausta.lukio.valitse.oppilaitos", formParameters)), "")
                    .addOptions(koodistoService.getLukioKoulukoodit())
                    .requiredInline()
                    .formParams(formParameters).build());

            RelatedQuestionComplexRule lukioRule = createVarEqualsToValueRule(baseEducation.getId(), YLIOPPILAS);
            Element ylioppilastutkinto = new DropdownSelectBuilder(OppijaConstants.YLIOPPILASTUTKINTO)
                    .defaultOption(OppijaConstants.YLIOPPILASTUTKINTO_FI)
                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.fi"), OppijaConstants.YLIOPPILASTUTKINTO_FI)
                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.ib"), "ib")
                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.eb"), "eb")
                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.rp"), "rp")
                    .required()
                    .inline()
                    .formParams(formParameters).build();
            lukioRule.addChild(TitledGroup("lukio.suoritus").formParams(formParameters).build()
                    .addChild(lukioPaattotodistusVuosi,
                            ylioppilastutkinto));

            lukioRule.addChild(tuoreYoTodistus);

            Element suorittanutAmmatillisenTutkinnonLukio = Radio("ammatillinenTutkintoSuoritettu")
                    .addOptions(ImmutableList.of(
                            new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                            new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                    .required()
                    .formParams(formParameters).build();
            lukioRule.addChild(suorittanutAmmatillisenTutkinnonLukio);


            lukioRule.addChild(
                    Dropdown(OppijaConstants.LUKIO_KIELI)
                            .emptyOption()
                            .addOptions(koodistoService.getTeachingLanguages())
                            .required()
                            .formParams(formParameters).build());

            baseEducation.addChild(lukioRule);

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

        baseEducation.addChild(pkKysymyksetRule);

        Element suorittanutAmmatillisenTutkinnon = Radio("ammatillinenTutkintoSuoritettu")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .required()
                .formParams(formParameters).build();

        paattotodistusvuosiPeruskouluRule.addChild(suorittanutAmmatillisenTutkinnon);

        RelatedQuestionComplexRule suorittanutTutkinnonRule = createRuleIfVariableIsTrue(ElementUtil.randomId(), suorittanutAmmatillisenTutkinnon.getId());
        Notification warning = new Notification(
                ElementUtil.randomId(),
                createI18NText("form.koulutustausta.ammatillinensuoritettu.huom", formParameters),
                Notification.NotificationType.INFO);
        suorittanutTutkinnonRule.addChild(warning);

        suorittanutAmmatillisenTutkinnon.addChild(suorittanutTutkinnonRule);


        pkKysymyksetRule.addChild(Dropdown(OppijaConstants.PERUSOPETUS_KIELI)
                .addOption(ElementUtil.createI18NAsIs(""), "")
                .addOptions(koodistoService.getTeachingLanguages())
                .required()
                .formParams(formParameters).build());

        if (formParameters.isPervako()) {
            baseEducation.addChild(TextArea("muukoulutus").cols(TEXT_AREA_COLS).maxLength(500).formParams(formParameters).build());
        }
        return baseEducation;
    }

}
