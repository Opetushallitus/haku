
package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.*;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.rules.AddElementRule;
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
        List<Option> laajuusYksikot = koodistoService.getLaajuusYksikot();
        List<Option> tutkintotasot = koodistoService.getKorkeakouluTutkintotasot();
        List<Option> maat = koodistoService.getCountries();

        //elements.add(buildSuoritusoikeus(formParameters));
        //elements.add(buildAiempiTutkinto(formParameters, tutkintotasot));

        Element pohjakoulutusGrp = TitledGroup("pohjakoulutus.korkeakoulut")
                .required().formParams(formParameters).build();

        pohjakoulutusGrp.addChild(
                buildYoSuomalainen(formParameters, laajuusYksikot),
                buildYoKansainvalinenSuomessa(formParameters),
                buildAmmatillinen(formParameters, laajuusYksikot, 5),
                buildAmmattitutkinto(formParameters, 5),
                buildKorkeakoulututkinto(formParameters, tutkintotasot, 5),
                buildYoUlkomainen(formParameters, maat),
                buildKorkeakoulututkintoUlkomaa(formParameters, tutkintotasot, maat, 5),
                buildUlkomainenTutkinto(formParameters, 5),
                buildAvoin(formParameters, 5),
                buildMuu(formParameters, 5));
        elements.add(pohjakoulutusGrp);

        Element suoritusoikeusTaiAiempitutkinto = Radio("suoritusoikeus_tai_aiempi_tutkinto")
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .required()
                .formParams(formParameters).build();

        elements.add(suoritusoikeusTaiAiempitutkinto);

        return elements.toArray(new Element[elements.size()]);
    }

    private static Element buildMuu(FormParameters formParameters, int count) {
        Element muu = Checkbox("pohjakoulutus_muu").formParams(formParameters).build();
        Element muuMore = createVarEqualsToValueRule(muu.getId(), "true");
        muu.addChild(muuMore);

        Element prevElement = buildMuuElement(formParameters, 1, muuMore);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraMuuTutkintoRule = new AddElementRule("addMuuTutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraMuuTutkintoRule);
            prevElement = buildMuuElement(formParameters, i, extraMuuTutkintoRule);
        }

        return muu;
    }

    private static Element buildMuuElement(FormParameters formParameters, int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        Element vuosi = TextQuestion("pohjakoulutus_muu_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_muu_vuosi.excel")
                .labelKey("pohjakoulutus_muu_vuosi")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element kuvaus = TextArea("pohjakoulutus_muu_kuvaus" + postfix)
                .labelKey("pohjakoulutus_muu_kuvaus").formParams(formParameters).requiredInline().build();
        parent.addChild(vuosi, kuvaus);
        return kuvaus;
    }

    private static Element buildAvoin(FormParameters formParameters, int count) {
        Element avoin = Checkbox("pohjakoulutus_avoin").formParams(formParameters).build();
        Element avoinMore = createVarEqualsToValueRule(avoin.getId(), "true");
        avoin.addChild(avoinMore);

        Element prevElement = buildAvoinElement(formParameters, 1, avoinMore);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraAvoinTutkintoRule = new AddElementRule("addAvoinTutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraAvoinTutkintoRule);
            prevElement = buildAvoinElement(formParameters, i, extraAvoinTutkintoRule);
        }

        return avoin;
    }

    private static Element buildAvoinElement(FormParameters formParameters, int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        Element ala = TextQuestion("pohjakoulutus_avoin_ala" + postfix)
                .labelKey("pohjakoulutus_avoin_ala").requiredInline().formParams(formParameters).build();
        Element kokonaisuus = TextQuestion("pohjakoulutus_avoin_kokonaisuus" + postfix)
                .labelKey("pohjakoulutus_avoin_kokonaisuus").requiredInline().formParams(formParameters).build();
        Element laajuus = TextQuestion("pohjakoulutus_avoin_laajuus" + postfix)
                .labelKey("pohjakoulutus_avoin_laajuus").requiredInline().formParams(formParameters).build();
        Element korkeakoulu = TextQuestion("pohjakoulutus_avoin_korkeakoulu" + postfix).labelKey("pohjakoulutus.korkeakoulu")
                .requiredInline().formParams(formParameters).build();
        parent.addChild(ala, kokonaisuus, laajuus, korkeakoulu);
        return korkeakoulu;
    }

    private static Element buildUlkomainenTutkinto(FormParameters formParameters, int count) {
        Element ulk = Checkbox("pohjakoulutus_ulk").formParams(formParameters).build();
        Element ulkMore = createVarEqualsToValueRule(ulk.getId(), "true");
        ulk.addChild(ulkMore);

        Element prevElement = buildUlkomainenTutkintoElement(formParameters, 1, ulkMore);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraUlkTutkintoRule = new AddElementRule("addUlkTutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraUlkTutkintoRule);
            prevElement = buildUlkomainenTutkintoElement(formParameters, i, extraUlkTutkintoRule);
        }

        return ulk;
    }

    private static Element buildUlkomainenTutkintoElement(FormParameters formParameters, int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        Element vuosi = TextQuestion("pohjakoulutus_ulk_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_ulk_vuosi.excel")
                .requiredInline()
                .labelKey("pohjakoulutus_ulk_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element nimike = TextQuestion("pohjakoulutus_ulk_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters).requiredInline().build();
        Element oppilaitos = TextQuestion("pohjakoulutus_ulk_oppilaitos" + postfix).labelKey("pohjakoulutus.oppilaitos")
                .formParams(formParameters).requiredInline().build();
        parent.addChild(vuosi, nimike, oppilaitos);

        return oppilaitos;
    }

    private static Element buildKorkeakoulututkinto(FormParameters formParameters, List<Option> tutkintotasot, int count) {
        Element kk = Checkbox("pohjakoulutus_kk").formParams(formParameters).build();
        Element kkMore = createVarEqualsToValueRule(kk.getId(), "true");
        kk.addChild(kkMore);

        Element prevElement = buildKorkeakoulututkintoElement(formParameters, tutkintotasot, 1, kkMore);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraKKTutkintoRule = new AddElementRule("addKKtutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraKKTutkintoRule);
            prevElement = buildKorkeakoulututkintoElement(formParameters, tutkintotasot, i, extraKKTutkintoRule);
        }

        return kk;
    }

    private static Element buildKorkeakoulututkintoElement(FormParameters formParameters, List<Option> tutkintotasot,
                                                           int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);
        Element taso = Dropdown("pohjakoulutus_kk_taso" + postfix)
                .addOptions(tutkintotasot).requiredInline().labelKey("pohjakoulutus.tutkintotaso").formParams(formParameters).build();

        Element pvm = Date("pohjakoulutus_kk_pvm" + postfix)
                .requiredInline()
                .labelKey("pohjakoulutus_kk_pvm")
                .formParams(formParameters).build();

        Element nimike = TextQuestion("pohjakoulutus_kk_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters).requiredInline().build();
        Element oppilaitos = TextQuestion("pohjakoulutus_kk_oppilaitos" + postfix)
                .labelKey("pohjakoulutus_kk_oppilaitos")
                .requiredInline().formParams(formParameters).build();

        parent.addChild(taso, pvm, nimike, oppilaitos);

        return oppilaitos;
    }

    private static Element buildKorkeakoulututkintoUlkomaa(FormParameters formParameters, List<Option> tutkintotasot,
                                                           List<Option> maat, int count) {
        Element kk_ulkomainen = Checkbox("pohjakoulutus_kk_ulk").formParams(formParameters).build();
        Element kkUlkomainenMore = createVarEqualsToValueRule(kk_ulkomainen.getId(), "true");

        kk_ulkomainen.addChild(kkUlkomainenMore);

        Element prevElement = buildKorkeakoulututkintoUlkomaaElement(formParameters, tutkintotasot, maat, 1, kkUlkomainenMore);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraKKUlkomaaRule = new AddElementRule("addKKUlkomaaRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraKKUlkomaaRule);
            prevElement = buildKorkeakoulututkintoUlkomaaElement(formParameters, tutkintotasot, maat, i, extraKKUlkomaaRule);
        }

        return kk_ulkomainen;
    }

    private static Element buildKorkeakoulututkintoUlkomaaElement(FormParameters formParameters, List<Option> tutkintotasot,
                                                           List<Option> maat, int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);
        Element taso = Dropdown("pohjakoulutus_kk_ulk_taso" + postfix)
                .addOptions(tutkintotasot).requiredInline().labelKey("pohjakoulutus.tutkintotaso").formParams(formParameters).build();

        Element pvm = Date("pohjakoulutus_kk_ulk_pvm" + postfix)
                .requiredInline()
                .labelKey("pohjakoulutus_kk_pvm")
                .formParams(formParameters).build();

        Element nimike = TextQuestion("pohjakoulutus_kk_ulk_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters).requiredInline().build();
        Element oppilaitos = TextQuestion("pohjakoulutus_kk_ulk_oppilaitos" + postfix)
                .labelKey("pohjakoulutus_kk_oppilaitos")
                .requiredInline().formParams(formParameters).build();

        Element kk_ulkomainen_missa = Dropdown("pohjakoulutus_kk_ulk_maa" + postfix)
                .addOptions(maat)
                .requiredInline()
                .labelKey("pohjakoulutus_kk_ulk_maa")
                .formParams(formParameters).build();

        parent.addChild(taso, pvm, nimike, oppilaitos, kk_ulkomainen_missa);
        return kk_ulkomainen_missa;
    }

    private static Element buildAmmattitutkinto(FormParameters formParameters, int count) {
        Element amt = Checkbox("pohjakoulutus_amt").formParams(formParameters).build();
        Element amtMore = createVarEqualsToValueRule(amt.getId(), "true");
        amt.addChild(amtMore);

        Element prevElement = buildAmmattitutkintoElement(formParameters, 1, amtMore);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraAmmattitutkintoRule = new AddElementRule("addAmmattitutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraAmmattitutkintoRule);
            prevElement = buildAmmattitutkintoElement(formParameters, i, extraAmmattitutkintoRule);
        }

        return amt;
    }

    private static Element buildAmmattitutkintoElement(FormParameters formParameters, int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);
        Element vuosi = TextQuestion("pohjakoulutus_amt_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_amt_vuosi.excel")
                .labelKey("pohjakoulutus_amt_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .requiredInline().formParams(formParameters).build();
        Element nimike = TextQuestion("pohjakoulutus_amt_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters).requiredInline().build();
        Element oppilaitos = TextQuestion("pohjakoulutus_amt_oppilaitos" + postfix).labelKey("pohjakoulutus.oppilaitos")
                .requiredInline().formParams(formParameters).build();
        Element nayttotutkinto = Checkbox("pohjakoulutus_amt_nayttotutkintona" + postfix).inline()
                .labelKey("pohjakoulutus_amt_nayttotutkintona")
                .formParams(formParameters).build();
        parent.addChild(vuosi, nimike, oppilaitos, nayttotutkinto);
        return nayttotutkinto;
    }

    private static Element buildAmmatillinen(FormParameters formParameters, List<Option> laajuusYksikot, int count) {
        Element am = Checkbox("pohjakoulutus_am").formParams(formParameters).build();
        Element amMore = createVarEqualsToValueRule(am.getId(), "true");
        am.addChild(amMore);

        Element prevElement = buildAmmatillinenElement(formParameters, laajuusYksikot, 1, amMore);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraAmmatillinenRule = new AddElementRule("addAmmatillinenRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraAmmatillinenRule);
            prevElement = buildAmmatillinenElement(formParameters, laajuusYksikot, i, extraAmmatillinenRule);
        }

        return am;
    }

    private static Element buildAmmatillinenElement(FormParameters formParameters, List<Option> laajuusYksikot, int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);
        Element vuosi = TextQuestion("pohjakoulutus_am_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_am_vuosi.excel")
                .labelKey("pohjakoulutus_am_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .requiredInline().formParams(formParameters).build();

        Element nimike = TextQuestion("pohjakoulutus_am_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters).requiredInline().build();
        Element laajuus = TextQuestion("pohjakoulutus_am_laajuus" + postfix).labelKey("pohjakoulutus.tutkinnonLaajuus")
                .requiredInline().formParams(formParameters).build();
        Element laajuusYksikko = Dropdown("pohjakoulutus_am_laajuus_yksikko" + postfix)
                .addOptions(laajuusYksikot)
                .excelColumnLabel("laajuusyksikko.excel")
                .inline().formParams(formParameters).labelKey("form.yleinen.nbsp").build();
        Element oppilaitos = TextQuestion("pohjakoulutus_am_oppilaitos" + postfix).labelKey("pohjakoulutus.oppilaitos")
                .requiredInline().formParams(formParameters).build();
        parent.addChild(vuosi,
                nimike,
                laajuus,
                laajuusYksikko,
                oppilaitos);
        return oppilaitos;
    }

    private static Element buildYoSuomalainen(FormParameters formParameters, List<Option> laajuusYksikot) {
        Element yo = Checkbox("pohjakoulutus_yo").formParams(formParameters).build();
        Element yoMore = createVarEqualsToValueRule(yo.getId(), "true");
        Element vuosi = TextQuestion("pohjakoulutus_yo_vuosi")
                .excelColumnLabel("pohjakoulutus_yo_vuosi.excel")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element yoTutkinto = Dropdown("pohjakoulutus_yo_tutkinto")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.fi"), "fi")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.lk"), "lk")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.lkOnly"), "lkOnly")
                .requiredInline()
                .formParams(formParameters).build();
        yoMore.addChild(vuosi, yoTutkinto, buildYoAmmatillinen(formParameters, laajuusYksikot));
        yo.addChild(yoMore);
        return yo;
    }

    private static Element buildYoKansainvalinenSuomessa(FormParameters formParameters) {
        Element kansainvalinenSuomessaYo = Checkbox("pohjakoulutus_yo_kansainvalinen_suomessa").formParams(formParameters).build();
        Element kansainvalinenSuomessaYoMore = createVarEqualsToValueRule(kansainvalinenSuomessaYo.getId(), "true");

        Element vuosi = TextQuestion("pohjakoulutus_yo_kansainvalinen_suomessa_vuosi")
                .excelColumnLabel("pohjakoulutus_yo_vuosi.excel")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element yoTutkintoKansainvalinenSuomessa = Dropdown("pohjakoulutus_yo_kansainvalinen_suomessa_tutkinto")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.ib"), "ib")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.eb"), "eb")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.rp"), "rp")
                .requiredInline()
                .formParams(formParameters).build();

        kansainvalinenSuomessaYoMore.addChild(vuosi, yoTutkintoKansainvalinenSuomessa);
        kansainvalinenSuomessaYo.addChild(kansainvalinenSuomessaYoMore);
        return kansainvalinenSuomessaYo;
    }

    private static Element buildYoUlkomainen(FormParameters formParameters, List<Option> maat) {
        Element ulkomainenYo = Checkbox("pohjakoulutus_yo_ulkomainen").formParams(formParameters).build();
        Element ulkomainenYoMore = createVarEqualsToValueRule(ulkomainenYo.getId(), "true");

        Element vuosi = TextQuestion("pohjakoulutus_yo_ulkomainen_vuosi")
                .excelColumnLabel("pohjakoulutus_yo_vuosi.excel")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element yoTutkintoUlkomainen = Dropdown("pohjakoulutus_yo_ulkomainen_tutkinto")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.ib"), "ib")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.eb"), "eb")
                .addOption(createI18NText("form.koulutustausta.lukio.yotutkinto.rp"), "rp")
                .requiredInline()
                .formParams(formParameters).build();

        Element ulkomainenYoMissa = Dropdown("pohjakoulutus_yo_ulkomainen_maa")
                .addOptions(maat)
                .requiredInline()
                .formParams(formParameters).build();

        ulkomainenYoMore.addChild(vuosi, yoTutkintoUlkomainen, ulkomainenYoMissa);
        ulkomainenYo.addChild(ulkomainenYoMore);
        return ulkomainenYo;
    }

    private static Element buildYoAmmatillinen(FormParameters formParameters, List<Option> laajuusYksikot) {
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
                .addOptions(laajuusYksikot)
                .excelColumnLabel("laajuusyksikko.excel")
                .inline()
                .formParams(formParameters)
                .labelKey("form.yleinen.nbsp").build();

        ammatillinenMore.addChild(ammatillinenVuosi,
                ammatillinenNimike,
                ammatillinenLaajuus, ammatillinenLaajuusYksikot);
        ammatillinen.addChild(ammatillinenMore);
        return ammatillinen;
    }

    private static Element buildAiempiTutkinto(FormParameters formParameters,
                                               List<Option> tutkintotasot) {
        OptionQuestionBuilder aiempitutkintoBuilder = Radio("aiempitutkinto")
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

        aiempitutkintoMore.addChild(Info()
                        .i18nText(ElementUtil.createI18NText("aiempitutkinto_info", formParameters))
                        .formParams(formParameters).build(),
                aiempitutkintoOppilaitos, aiempitutkintoTutkinto,tutkinto, vuosi
        );
        aiempitutkinto.addChild(aiempitutkintoMore);

        return aiempitutkinto;
    }

    private static Element buildSuoritusoikeus(FormParameters formParameters) {
        OptionQuestionBuilder suoritusoikeusBuilder = Radio("suoritusoikeus")
                .addOption("false", formParameters)
                .addOption("true", formParameters);
        Element suoritusoikeus = suoritusoikeusBuilder.required().formParams(formParameters).build();
        Element suoritusoikeusMore = createVarEqualsToValueRule(suoritusoikeus.getId(), "true");
        Element suoritusoikeusOppilaitos = TextQuestion("suoritusoikeus_korkeakoulu")
                .requiredInline()
                .formParams(formParameters).build();

        Element tutkinto = TextQuestion("suoritusoikeus_tutkinto").labelKey("pohjakoulutus.tutkinto")
                .requiredInline().formParams(formParameters).build();
        Element vuosi = TextQuestion("suoritusoikeus_vuosi")
                .excelColumnLabel("suoritusoikeus_vuosi.excel")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();

        suoritusoikeusMore.addChild(Info()
                        .i18nText(ElementUtil.createI18NText("suoritusoikeus_info", formParameters))
                        .formParams(formParameters)
                        .build(),
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

        OptionQuestionBuilder baseEducationBuilder = Radio(ELEMENT_ID_BASE_EDUCATION)
                .addOption(educationMap.get(PERUSKOULU).getValue(), formParameters)
                .addOption(educationMap.get(OSITTAIN_YKSILOLLISTETTY).getValue(), formParameters)
                .addOption(ALUEITTAIN_YKSILOLLISTETTY, formParameters)
                .addOption(educationMap.get(YKSILOLLISTETTY).getValue(), formParameters)
                .addOption(educationMap.get(KESKEYTYNYT).getValue(), formParameters);


        if (!formParameters.isPerusopetuksenJalkeinenValmentava()) {
            baseEducationBuilder.addOption(educationMap.get(YLIOPPILAS).getValue(), formParameters);
        }
        baseEducationBuilder.addOption(educationMap.get(ULKOMAINEN_TUTKINTO).getValue(), formParameters);

        Element baseEducation = baseEducationBuilder.required().formParams(formParameters).build();

        Element keskeytynytRule = createVarEqualsToValueRule(baseEducation.getId(), KESKEYTYNYT);
        if (!formParameters.isPerusopetuksenJalkeinenValmentava()) {
            keskeytynytRule.addChild(
                    Info(TUTKINTO_KESKEYTNYT_NOTIFICATION_ID).labelKey("form.koulutustausta.keskeytynyt.huom").formParams(formParameters).build());
        }

        Element ulkomaillaSuoritettuTutkintoRule = createVarEqualsToValueRule(baseEducation.getId(), ULKOMAINEN_TUTKINTO);
        if (formParameters.isPerusopetuksenJalkeinenValmentava()) {
            ulkomaillaSuoritettuTutkintoRule.addChild(
                    TextArea("mika-ulkomainen-koulutus")
                            .cols(TEXT_AREA_COLS)
                            .maxLength(250)
                            .formParams(formParameters).build());
        }

        if (!formParameters.isPerusopetuksenJalkeinenValmentava()) {
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

        Expr kysytaankoKoulutuspaikka;
        String hakukausi = formParameters.getApplicationSystem().getHakukausiUri();
        if (OppijaConstants.HAKUKAUSI_SYKSY.equals(hakukausi)) {
            kysytaankoKoulutuspaikka = new Equals(new Value("true"), new Value("true"));
        } else {
            kysytaankoKoulutuspaikka = new And(
                    new Not(
                            new Equals(
                                    new Variable(paattotodistusvuosiPeruskoulu.getId()),
                                    new Value(hakukausiVuosiStr))),
                    vuosiSyotetty);
        }

        Element onkoTodistusSaatuKuluneenaVuonna = Rule(kysytaankoKoulutuspaikka).build();
        onkoTodistusSaatuKuluneenaVuonna.addChild(koulutuspaikkaAmmatillisenTutkintoon);

        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskoulu, suorittanutGroup,
                onkoTodistusSaatuKuluneenaVuonna, paattotodistusvuosiPeruskouluRule);


        if (!formParameters.isPerusopetuksenJalkeinenValmentava()) {

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

        if (formParameters.isPerusopetuksenJalkeinenValmentava()) {
            baseEducation.addChild(TextArea("muukoulutus").cols(TEXT_AREA_COLS).maxLength(500).formParams(formParameters).build());
        }
        return baseEducation;
    }

}
