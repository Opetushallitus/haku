
package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.DateQuestionBuilder.Date;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder.Dropdown;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.NotificationBuilder.Info;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.NotificationBuilder.Warning;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextAreaBuilder.TextArea;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class KoulutustaustaPhase {
    public static final String TUTKINTO_ULKOMAILLA_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO_KESKEYTNYT_NOTIFICATION_ID = "tutkinto5-notification";

    public static final int TEXT_AREA_COLS = 60;
    public static final String PAATTOTODISTUSVUOSI_PATTERN = "^(19[0-9][0-9]|200[0-9]|201[0-5])$";

    private KoulutustaustaPhase() {
    }

    public static Element create(final FormParameters formParameters) {
        Element koulutustausta = Phase("koulutustausta").setEditAllowedByRoles("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO").formParams(formParameters).build();
        ApplicationSystem as = formParameters.getApplicationSystem();
        if (as.getKohdejoukkoUri().equals(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU)){
            Element koulutustaustaRyhma = new ThemeBuilder("koulutustausta.teema_kk").previewable().formParams(formParameters).build();
            if (!formParameters.isOnlyThemeGenerationForFormEditor()) {
                koulutustaustaRyhma.addChild(createKorkeakouluKoulutustausta(formParameters));
                koulutustausta.addChild(koulutustaustaRyhma);
            }
        } else {
            Element koulutustaustaRyhma = new ThemeBuilder("koulutustausta.teema").previewable().formParams(formParameters).build();
            if (!formParameters.isOnlyThemeGenerationForFormEditor()) {
                koulutustaustaRyhma.addChild(createKoulutustaustaRadio(formParameters));
                koulutustausta.addChild(koulutustaustaRyhma);
            }
        }
        return koulutustausta;
    }

    private static Element[] createKorkeakouluKoulutustausta(FormParameters formParameters) {
        ArrayList<Element> elements = new ArrayList<Element>();
        KoodistoService koodistoService = formParameters.getKoodistoService();
        List<Option> korkeakoulut = koodistoService.getKorkeakouluKoulukoodit();
        List<Option> laajuusYksikot = koodistoService.getLaajuusYksikot();
        List<Option> tutkintotasot = koodistoService.getKorkeakouluTutkintotasot();

        elements.add(buildSuoritusoikeus(formParameters));
        elements.add(buildAiempiTutkinto(formParameters, tutkintotasot));

        Element pohjakoulutusGrp = TitledGroup("pohjakoulutus.korkeakoulut")
                .required().formParams(formParameters).build();

        pohjakoulutusGrp.addChild(
                buildYo(formParameters, laajuusYksikot),
                buildAmmatillinen(formParameters, laajuusYksikot),
                buildAmmattitutkinto(formParameters),
                buildKorkeakoulututkinto(formParameters, korkeakoulut, tutkintotasot),
                buildUlkomainenTutkinto(formParameters),
                buildAvoin(formParameters),
                buildMuu(formParameters));
        elements.add(pohjakoulutusGrp);

        return elements.toArray(new Element[elements.size()]);
    }

    private static Element buildMuu(FormParameters formParameters) {
        Element muu = Checkbox("pohjakoulutus_muu").formParams(formParameters).build();
        Element muuMore = createVarEqualsToValueRule(muu.getId(), "true");
        Element vuosi = TextQuestion("pohjakoulutus_muu_vuosi")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element kuvaus = TextArea("pohjakoulutus_muu_kuvaus").formParams(formParameters).requiredInline().build();
        muuMore.addChild(vuosi, kuvaus);
        muu.addChild(muuMore);
        return muu;
    }

    private static Element buildAvoin(FormParameters formParameters) {
        Element avoin = Checkbox("pohjakoulutus_avoin").formParams(formParameters).build();
        Element avoinMore = createVarEqualsToValueRule(avoin.getId(), "true");
        Element ala = TextQuestion("pohjakoulutus_avoin_ala").requiredInline().formParams(formParameters).build();
        Element kokonaisuus = TextQuestion("pohjakoulutus_avoin_kokonaisuus").requiredInline().formParams(formParameters).build();
        Element laajuus = TextQuestion("pohjakoulutus_avoin_laajuus").requiredInline().formParams(formParameters).build();
        // TODO: korkeakouluvalinnan dropdowniin 'muu', laukaisee tekstikysymyksen, johon koulu
        Element korkeakoulu = TextQuestion("pohjakoulutus_avoin_korkeakoulu").labelKey("pohjakoulutus.korkeakoulu")
                .requiredInline().formParams(formParameters).build();
        avoinMore.addChild(ala, kokonaisuus, laajuus, korkeakoulu);
        avoin.addChild(avoinMore);
        return avoin;
    }

    private static Element buildUlkomainenTutkinto(FormParameters formParameters) {
        Element ulk = Checkbox("pohjakoulutus_ulk").formParams(formParameters).build();
        Element ulkMore = createVarEqualsToValueRule(ulk.getId(), "true");
        Element vuosi = TextQuestion("pohjakoulutus_ulk_vuosi")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element nimike = TextQuestion("pohjakoulutus_ulk_nimike").labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters).requiredInline().build();
        Element oppilaitos = TextQuestion("pohjakoulutus_ulk_oppilaitos").labelKey("pohjakoulutus.oppilaitos")
                .formParams(formParameters).requiredInline().build();
        ulkMore.addChild(vuosi, nimike, oppilaitos);
        ulk.addChild(ulkMore);
        return ulk;
    }

    private static Element buildKorkeakoulututkinto(FormParameters formParameters, List<Option> korkeakoulut, List<Option> tutkintotasot) {
        Element kk = Checkbox("pohjakoulutus_kk").formParams(formParameters).build();
        Element kkMore = createVarEqualsToValueRule(kk.getId(), "true");
        Element taso = Dropdown("pohjakoulutus_kk_taso")
                .addOptions(tutkintotasot).requiredInline().labelKey("pohjakoulutus.tutkintotaso").formParams(formParameters).build();

        Element pvm = Date("pohjakoulutus_kk_pvm")
                .requiredInline()
                .formParams(formParameters).build();

        Element nimike = TextQuestion("pohjakoulutus_kk_nimike").labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters).requiredInline().build();
        Element oppilaitos = TextQuestion("pohjakoulutus_kk_oppilaitos")
                .requiredInline().formParams(formParameters).build();
        Element ulkomailla = Checkbox("pohjakoulutus_kk_ulkomainen")
                .inline().formParams(formParameters).build();

        kkMore.addChild(taso, pvm, nimike, oppilaitos, ulkomailla);
        kk.addChild(kkMore);

        return kk;
    }

    private static Element buildAmmattitutkinto(FormParameters formParameters) {
        Element amt = Checkbox("pohjakoulutus_amt").formParams(formParameters).build();
        Element amtMore = createVarEqualsToValueRule(amt.getId(), "true");
        Element vuosi = TextQuestion("pohjakoulutus_amt_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .requiredInline().formParams(formParameters).build();
        Element nimike = TextQuestion("pohjakoulutus_amt_nimike").labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters).requiredInline().build();
        Element oppilaitos = TextQuestion("pohjakoulutus_amt_oppilaitos").labelKey("pohjakoulutus.oppilaitos")
                .requiredInline().formParams(formParameters).build();
        amtMore.addChild(vuosi, nimike, oppilaitos);
        amt.addChild(amtMore);
        return amt;
    }

    private static Element buildAmmatillinen(FormParameters formParameters, List<Option> laajuusYksikot) {
        Element am = Checkbox("pohjakoulutus_am").formParams(formParameters).build();
        Element amMore = createVarEqualsToValueRule(am.getId(), "true");

        Element vuosi = TextQuestion("pohjakoulutus_am_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .requiredInline().formParams(formParameters).build();

        Element nimike = TextQuestion("pohjakoulutus_am_nimike").labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters).requiredInline().build();
        Element laajuus = TextQuestion("pohjakoulutus_am_laajuus").labelKey("pohjakoulutus.tutkinnonLaajuus")
                .requiredInline().formParams(formParameters).build();
        Element laajuusYksikko = Dropdown("pohjakoulutus_am_laajuus_yksikko")
                .addOptions(laajuusYksikot).inline().formParams(formParameters).labelKey("form.yleinen.nbsp").build();
        Element nayttotutkinto = Checkbox("pohjakoulutus_am_nayttotutkintona").inline()
                .formParams(formParameters).build();
        Element oppilaitos = TextQuestion("pohjakoulutus_am_oppilaitos").labelKey("pohjakoulutus.oppilaitos")
                .requiredInline().formParams(formParameters).build();
        amMore.addChild(vuosi,
                nimike,
                laajuus,
                laajuusYksikko,
                nayttotutkinto,
                oppilaitos);
        am.addChild(amMore);
        return am;
    }

    private static Element buildYo(FormParameters formParameters, List<Option> laajuusYksikot) {
        Element yo = Checkbox("pohjakoulutus_yo").formParams(formParameters).build();
        Element yoMore = createVarEqualsToValueRule(yo.getId(), "true");
        Element vuosi = TextQuestion("pohjakoulutus_yo_vuosi")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element yoTutkinto = Dropdown("pohjakoulutus_yo_tutkinto")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.fi"), "fi")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.ib"), "ib")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.eb"), "eb")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.rp"), "rp")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.lk"), "lk")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.lkOnly"), "lkOnly")
                .requiredInline()
                .formParams(formParameters).build();

        Element ammatillinen = Checkbox("pohjakoulutus_yo_ammatillinen").inline().formParams(formParameters).build();
        Element ammatillinenMore = createVarEqualsToValueRule(ammatillinen.getId(), "true");
        Element ammatillinenVuosi = TextQuestion("pohjakoulutus_yo_ammatillinen_vuosi")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element ammatillinenNimike = TextQuestion("pohjakoulutus_yo_ammatillinen_nimike")
                .labelKey("pohjakoulutus.tutkintonimike")
                .requiredInline()
                .formParams(formParameters).build();
        Element ammatillinenLaajuus = TextQuestion("pohjakoulutus_yo_ammatillinen_laajuus")
                .labelKey("pohjakoulutus.tutkinnonlaajuus")
                .requiredInline()
                .formParams(formParameters).build();
        Element ammatillinenLaajuusYksikot = Dropdown("pohjakoulutus_yo_ammatillinen_laajuusYksikko")
                .addOptions(laajuusYksikot).inline().formParams(formParameters).labelKey("form.yleinen.nbsp").build();

        ammatillinenMore.addChild(ammatillinenVuosi,
                ammatillinenNimike,
                ammatillinenLaajuus, ammatillinenLaajuusYksikot);
        ammatillinen.addChild(ammatillinenMore);

        Element ulkomainenYo = Checkbox("pohjakoulutus_yo_ulkomainen").inline().formParams(formParameters).build();
        Element ulkomainenYoMore = createVarEqualsToValueRule(ulkomainenYo.getId(), "true");
        Element ulkomainenYoMissa = TextQuestion("pohjakoulutus_yo_ulkomainen_maa")
                .requiredInline().formParams(formParameters).build();
        ulkomainenYoMore.addChild(ulkomainenYoMissa);
        ulkomainenYo.addChild(ulkomainenYoMore);

        yoMore.addChild(vuosi, yoTutkinto, ulkomainenYo, ammatillinen);

        yo.addChild(yoMore);
        return yo;
    }

    private static Element buildAiempiTutkinto(FormParameters formParameters,
                                               List<Option> tutkintotasot) {
        RadioBuilder aiempitutkintoBuilder = Radio("aiempitutkinto")
                .addOption("false", formParameters)
                .addOption("true", formParameters);
        Element aiempitutkinto = aiempitutkintoBuilder.required().formParams(formParameters).build();
        Element aiempitutkintoMore = createVarEqualsToValueRule(aiempitutkinto.getId(), "true");
        Element aiempitutkintoOppilaitos = TextQuestion("aiempitutkinto_korkeakoulu")
                .requiredInline()
                .formParams(formParameters).build();

        Element aiempitutkintoTutkinto = Dropdown("aiempitutkinto_tutkintotaso")
                .addOptions(tutkintotasot).labelKey("pohjakoulutus.tutkintotaso")
                .requiredInline().formParams(formParameters).build();

        Element tutkinto = TextQuestion("aiempitutkinto_tutkinto").labelKey("pohjakoulutus.tutkinto")
                .requiredInline().formParams(formParameters).build();
        Element vuosi = TextQuestion("aiempitutkinto_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .requiredInline().formParams(formParameters).build();

        aiempitutkintoMore.addChild(
                Info().formParams(formParameters)
                        .i18nText(ElementUtil.createI18NText("aiempitutkinto_info", formParameters)).build(),
                aiempitutkintoOppilaitos, aiempitutkintoTutkinto,tutkinto, vuosi
        );
        aiempitutkinto.addChild(aiempitutkintoMore);

        return aiempitutkinto;
    }

    private static Element buildSuoritusoikeus(FormParameters formParameters) {
        RadioBuilder suoritusoikeusBuilder = Radio("suoritusoikeus")
                .addOption("false", formParameters)
                .addOption("true", formParameters);
        Element suoritusoikeus = suoritusoikeusBuilder.required().formParams(formParameters).build();
        Element suoritusoikeusMore = createVarEqualsToValueRule(suoritusoikeus.getId(), "true");
        Element suoritusoikeusOppilaitos = TextQuestion("suoritusoikeus_korkeakoulu")
                .requiredInline()
                .formParams(formParameters).build();

        Element tutkinto = TextQuestion("suoritusoikeus_tutkinto").labelKey("pohjakoulutus.tutkinto")
                .requiredInline().formParams(formParameters).build();
        Element vuosi = TextQuestion("suoritusoikeus_vuosi").requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();

        suoritusoikeusMore.addChild(
                Info().formParams(formParameters)
                        .i18nText(ElementUtil.createI18NText("suoritusoikeus_info", formParameters)).build(),
                suoritusoikeusOppilaitos,
                tutkinto, vuosi
        );
        suoritusoikeus.addChild(suoritusoikeusMore);

        return suoritusoikeus;
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

        Element keskeytynytRule = createVarEqualsToValueRule(baseEducation.getId(), KESKEYTYNYT);
        if (!formParameters.isPervako()) {
            keskeytynytRule.addChild(
                    Info(TUTKINTO_KESKEYTNYT_NOTIFICATION_ID).labelKey("form.koulutustausta.keskeytynyt.huom").formParams(formParameters).build());
        }

        Element ulkomaillaSuoritettuTutkintoRule = createVarEqualsToValueRule(baseEducation.getId(), ULKOMAINEN_TUTKINTO);
        if (formParameters.isPervako()) {
            ulkomaillaSuoritettuTutkintoRule.addChild(
                    TextArea("mika-ulkomainen-koulutus")
                            .cols(TEXT_AREA_COLS)
                            .maxLength(250)
                            .formParams(formParameters).build());
        }

        if (!formParameters.isPervako()) {
            Element tutkintoUlkomaillaNotification =
                    Info(TUTKINTO_ULKOMAILLA_NOTIFICATION_ID).labelKey("form.koulutustausta.ulkomailla.huom").formParams(formParameters).build();
            ulkomaillaSuoritettuTutkintoRule.addChild(tutkintoUlkomaillaNotification);
        }

        baseEducation.addChild(ulkomaillaSuoritettuTutkintoRule);
        baseEducation.addChild(keskeytynytRule);

        Element paattotodistusvuosiPeruskoulu = new TextQuestionBuilder(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI)
                .labelKey("form.koulutustausta.paattotodistusvuosi")
                .required()
                .size(4)
                .maxLength(4)
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi(), 1900))
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

        Element pkKysymyksetRule = createVarEqualsToValueRule(baseEducation.getId(),
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

        Element paattotodistusvuosiPeruskouluRule = createRegexpRule(paattotodistusvuosiPeruskoulu.getId(), "^(19[0-9][0-9]|200[0-9]|201[0-1])$");

        Element koulutuspaikkaAmmatillisenTutkintoon = Radio("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .required()
                .formParams(formParameters).build();

        Expr vuosiSyotetty = new Regexp(paattotodistusvuosiPeruskoulu.getId(), PAATTOTODISTUSVUOSI_PATTERN);
        Expr kysytaankoKoulutuspaikka = new And(new Not(new Equals(new Variable(paattotodistusvuosiPeruskoulu.getId()), new Value("2014"))), vuosiSyotetty);

        Element onkoTodistusSaatuKuluneenaVuonna = Rule(kysytaankoKoulutuspaikka).build();
        onkoTodistusSaatuKuluneenaVuonna.addChild(koulutuspaikkaAmmatillisenTutkintoon);

        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskoulu, suorittanutGroup,
                onkoTodistusSaatuKuluneenaVuonna, paattotodistusvuosiPeruskouluRule);


        if (!formParameters.isPervako()) {

            Element lukioPaattotodistusVuosi = TextQuestion(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI)
                    .maxLength(4)
                    .size(4)
                    .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                    .requiredInline()
                    .formParams(formParameters).build();

            Element tuoreYoTodistus = createVarEqualsToValueRule(lukioPaattotodistusVuosi.getId(), hakukausiVuosiStr);
            tuoreYoTodistus.addChild(new DropdownSelectBuilder(ELEMENT_ID_SENDING_SCHOOL)
                    .defaultValueAttribute("")
                    .addOption(addSpaceAtTheBeginning(ElementUtil.createI18NText("form.koulutustausta.lukio.valitse.oppilaitos", formParameters)), "")
                    .addOptions(koodistoService.getLukioKoulukoodit())
                    .requiredInline()
                    .formParams(formParameters).build());

            Element lukioRule = createVarEqualsToValueRule(baseEducation.getId(), YLIOPPILAS);
            Element ylioppilastutkinto = new DropdownSelectBuilder(OppijaConstants.YLIOPPILASTUTKINTO)
                    .defaultOption(OppijaConstants.YLIOPPILASTUTKINTO_FI)
                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.fi"), OppijaConstants.YLIOPPILASTUTKINTO_FI)
                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.ib"), "ib")
                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.eb"), "eb")
                    .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.rp"), "rp")
                    .requiredInline()
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

            Element suorittanutTutkinnonLukioRule =
                    createRuleIfVariableIsTrue(suorittanutAmmatillisenTutkinnonLukio.getId());
            Element warningLukio =
                    Warning(ElementUtil.randomId()).failValidation().labelKey("form.koulutustausta.ammatillinenSuoritettu.lukio.huom").formParams(formParameters).build();

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

        Element suorittanutTutkinnonRule = createRuleIfVariableIsTrue(suorittanutAmmatillisenTutkinnon.getId());
        Element warning = Info().labelKey("form.koulutustausta.ammatillinensuoritettu.huom").formParams(formParameters).build();

        suorittanutTutkinnonRule.addChild(warning);

        suorittanutAmmatillisenTutkinnon.addChild(suorittanutTutkinnonRule);

        pkKysymyksetRule.addChild(Dropdown(OppijaConstants.PERUSOPETUS_KIELI)
                .emptyOption()
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
