
package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.*;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.OptionQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.StringUtils;

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
            if (formParameters.isAmmattillinenEritysopettajaTaiOppilaanohjaajaKoulutus()
                    || formParameters.isAmmattillinenOpettajaKoulutus()) {
                Element koulutustaustaRyhma = new ThemeBuilder("koulutustausta.teema_kk").previewable().formParams(formParameters).build();
                if (!formParameters.isOnlyThemeGenerationForFormEditor()) {
                    koulutustaustaRyhma.addChild(createOpetErkatJaOpotKoulutustausta(formParameters));
                    koulutustausta.addChild(koulutustaustaRyhma);
                }
            } else {
                Element koulutustaustaRyhma = new ThemeBuilder("koulutustausta.teema_kk").previewable().formParams(formParameters).build();
                if (!formParameters.isOnlyThemeGenerationForFormEditor()) {
                    koulutustaustaRyhma.addChild(createKorkeakouluKoulutustausta(formParameters));
                    koulutustausta.addChild(koulutustaustaRyhma);
                }
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

    private static Element[] createOpetErkatJaOpotKoulutustausta(FormParameters formParameters) {
        ArrayList<Element> elements = new ArrayList<Element>();
        KoodistoService koodistoService = formParameters.getKoodistoService();

        // Tutkinto ja suoritusvuosi
        Element tutkinto = TextQuestion("amk_ope_tutkinto")
                .size(50).formParams(formParameters).requiredInline().build();
        Element vuosi = TextQuestion("amk_ope_tutkinto_vuosi")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        elements.add(tutkinto);
        elements.add(vuosi);

        // Tutkinnon taso
        Element tutkinnonTaso = Dropdown("amk_ope_tutkinnontaso")
                .addOption(ElementUtil.createI18NText("amk_ope_tutkinnontaso.tohtori"), "tohtori")
                .addOption(ElementUtil.createI18NText("amk_ope_tutkinnontaso.ylempi"), "ylempi")
                .addOption(ElementUtil.createI18NText("amk_ope_tutkinnontaso.amk"), "amk")
                .addOption(ElementUtil.createI18NText("amk_ope_tutkinnontaso.alempi"), "alempi")
                .addOption(ElementUtil.createI18NText("amk_ope_tutkinnontaso.opisto"), "opisto")
                .addOption(ElementUtil.createI18NText("amk_ope_tutkinnontaso.ammatillinen"), "ammatillinen")
                .addOption(ElementUtil.createI18NText("amk_ope_tutkinnontaso.ammatti"), "ammatti")
                .addOption(ElementUtil.createI18NText("amk_ope_tutkinnontaso.muu"), "muu")
                .formParams(formParameters)
                .requiredInline()
                .build();
        elements.add(tutkinnonTaso);

        Element ulkomainen = Checkbox("amk_ope_ulkomainen_tutkinto")
                .labelKey("amk_ope_ulkomainen_tutkinto")
                .inline()
                .formParams(formParameters).build();
        elements.add(ulkomainen);

        // Ei korkeakoulututkintoa...
        Element tutkinnontasoRule = createVarEqualsToValueRule("amk_ope_tutkinnontaso",
                "opisto", "ammatillinen", "ammatti", "muu");
        elements.add(tutkinnontasoRule);
        OptionQuestionBuilder eiKorkeakoulututkintoaBuilder = Radio("ei_korkeakoulututkintoa");

        // ...opettajana/kouluttajana tutkintoon johtavassa ammatillisessa koulutuksessa
        eiKorkeakoulututkintoaBuilder
                .addOption(ElementUtil.createI18NText("ei_korkeakoulututkintoa.opettajana_ammatillisessa_tutkinto"),
                "opettajana_ammatillisessa_tutkinto");

        Element opettajaAmmatillisessaTutkintoRule = createVarEqualsToValueRule("ei_korkeakoulututkintoa",
                "opettajana_ammatillisessa_tutkinto");

        opettajaAmmatillisessaTutkintoRule.addChild(
                TextQuestion("opettajana_ammatillisessa_tutkinto_tyopaikka")
                        .size(50)
                        .formParams(formParameters)
                        .requiredInline()
                        .labelKey("opettajana_ammatillisessa_tutkinto_tyopaikka")
                        .build(),
                TextQuestion("opettajana_ammatillisessa_tutkinto_tehtavanimike")
                        .size(50)
                        .formParams(formParameters)
                        .requiredInline()
                        .labelKey("opettajana_ammatillisessa_tutkinto_tehtavanimike")
                        .build(),
                Checkbox("opettajana_ammatillisessa_tutkinto_ammattillisten_opettajana")
                        .formParams(formParameters)
                        .inline()
                        .build(),
                Checkbox("opettajana_ammatillisessa_tutkinto_yhteisten_opettajana")
                        .formParams(formParameters)
                        .inline()
                        .build()
        );
        eiKorkeakoulututkintoaBuilder.addChild(opettajaAmmatillisessaTutkintoRule);

        // ...toimin kouluttajana ammatillisessa lisäkoulutuksessa
        if (formParameters.isAmmattillinenOpettajaKoulutus()) {
            Element opettajaAmmatillisessaRule = createVarEqualsToValueRule("ei_korkeakoulututkintoa",
                    "opettajana_ammatillisessa");
            opettajaAmmatillisessaRule.addChild(
                    TextQuestion("opettajana_ammatillisessa_tyopaikka")
                            .size(50)
                            .formParams(formParameters)
                            .requiredInline()
                            .build(),
                    TextArea("opettajana_ammatillisessa_kuvaus")
                            .size(400)
                            .formParams(formParameters)
                            .requiredInline()
                            .build());

            eiKorkeakoulututkintoaBuilder
                    .addOption(ElementUtil.createI18NText("ei_korkeakoulututkintoa.opettajana_ammatillisessa"),
                            "opettajana_ammatillisessa")
                    .addChild(opettajaAmmatillisessaRule);
        }

        // ...en toimi ammatillisen koulutuksen opetustehtävissä
        eiKorkeakoulututkintoaBuilder.addOption(
                ElementUtil.createI18NText("ei_korkeakoulututkintoa.ei_opettajana_ammatillisessa"),
                "ei_opettajana_ammatillisessa");

        tutkinnontasoRule.addChild(eiKorkeakoulututkintoaBuilder.formParams(formParameters).requiredInline().build());

        List<Option> koulutusalatRaw = koodistoService.getKoulutusalat();
        List<Option> koulutusalat = new ArrayList<Option>(koulutusalatRaw.size() - 1);
        for (Option o : koulutusalatRaw) {
            if (!o.getValue().equals("0")) {
                koulutusalat.add(o);
            }
        }
        // Koulutus- ja opintoala
        OptionQuestion koulutusala = (OptionQuestion) Dropdown("amk_ope_koulutusala")
                .emptyOption()
                .addOptions(koulutusalat)
                .excelColumnLabel("amk_ope_koulutusala.excel")
                .formParams(formParameters)
                .required()
                .inline()
                .build();
        elements.add(koulutusala);
        for (Option koulutusalaOption : koulutusala.getOptions()) {
            String koulutusalaKoodi = koulutusalaOption.getValue();
            Element opintoalaRule = createVarEqualsToValueRule("amk_ope_koulutusala", koulutusalaKoodi);
            OptionQuestionBuilder opintoalaBuilder = Dropdown("amk_ope_opintoala")
                    .emptyOption();
            if (StringUtils.isNotBlank(koulutusalaKoodi)) {
                opintoalaBuilder.addOptions(koodistoService.getOpintoalat(koulutusalaKoodi))
                        .excelColumnLabel("amk_ope_opintoala.excel");
            }
            opintoalaRule.addChild(
                    opintoalaBuilder.formParams(formParameters).requiredInline().build());
            elements.add(opintoalaRule);
        }

        // Pedagogiset opinnot
        if (formParameters.isAmmattillinenEritysopettajaTaiOppilaanohjaajaKoulutus()) {
            Element pedagogisetOpinnot = Radio("pedagogiset_opinnot")
                    .addOption(ElementUtil.createI18NText("pedagogiset_opinnot.kylla"), "true")
                    .addOption(ElementUtil.createI18NText("pedagogiset_opinnot.ei"), "false")
                    .requiredInline()
                    .formParams(formParameters)
                    .build();
            elements.add(pedagogisetOpinnot);

            Element pedagogisetOpinnotEiSuoritettuRule = createVarEqualsToValueRule("pedagogiset_opinnot", "false");
            pedagogisetOpinnotEiSuoritettuRule.addChild(Info()
                    .i18nText(ElementUtil.createI18NText("pedagogiset_opinnot.ei.info"))
                    .inline().build());
            elements.add(pedagogisetOpinnotEiSuoritettuRule);

            Element pedagogisetOpinnotOnSuoritettuRule = createVarEqualsToValueRule("pedagogiset_opinnot", "true");
            pedagogisetOpinnotOnSuoritettuRule.addChild(
                    TextQuestion("pedagogiset_opinnot_oppilaitos")
                            .size(50).formParams(formParameters).requiredInline().build(),
                    TextQuestion("pedagogiset_opinnot_suoritusvuosi")
                            .requiredInline()
                            .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                            .formParams(formParameters).build());
            elements.add(pedagogisetOpinnotOnSuoritettuRule);
        }
        
        // Muut tutkinnot
        if (formParameters.isAmmattillinenOpettajaKoulutus()) {
            Element muutTutkinnotGrp = TitledGroup("muut_tutkinnot").formParams(formParameters).inline().build();
            elements.add(muutTutkinnotGrp);
            for (String t : new String[] {"amt", "am", "kk", "tri"} ) {
                String muuTutkintoBaseId = "muu_tutkinto_"+t;
                Element muuTutkinto = Checkbox(muuTutkintoBaseId).formParams(formParameters).inline().build();
                Element muuTutkintoSuoritettu = createVarEqualsToValueRule(muuTutkintoBaseId, "true");
                muuTutkintoSuoritettu.addChild(TextQuestion(muuTutkintoBaseId + "_nimi")
                        .i18nText(ElementUtil.createI18NAsIs("&nbsp;"))
                        .size(50).formParams(formParameters).inline().build());
                muutTutkinnotGrp.addChild(muuTutkinto, muuTutkintoSuoritettu);
            }
        }

        return elements.toArray(new Element[elements.size()]);
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

        Element prevElement = buildMuuElement(formParameters, 1, muuMore, true);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraMuuTutkintoRule = new AddElementRule("addMuuTutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraMuuTutkintoRule);
            prevElement = buildMuuElement(formParameters, i, extraMuuTutkintoRule, false);
        }

        return muu;
    }

    private static Element buildMuuElement(FormParameters formParameters, int index, Element parent, boolean required) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder vuosiBuilder = TextQuestion("pohjakoulutus_muu_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_muu_vuosi.excel")
                .labelKey("pohjakoulutus_muu_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters);
        ElementBuilder kuvausBuilder = TextArea("pohjakoulutus_muu_kuvaus" + postfix)
                .labelKey("pohjakoulutus_muu_kuvaus").formParams(formParameters);

        if (required) {
            vuosiBuilder = vuosiBuilder.requiredInline();
            kuvausBuilder = kuvausBuilder.requiredInline();
        } else {
            vuosiBuilder = vuosiBuilder.inline();
            kuvausBuilder = kuvausBuilder.inline();
        }
        Element vuosi = vuosiBuilder.build();
        Element kuvaus = kuvausBuilder.build();
        parent.addChild(vuosi, kuvaus);
        return kuvaus;
    }

    private static Element buildAvoin(FormParameters formParameters, int count) {
        Element avoin = Checkbox("pohjakoulutus_avoin").formParams(formParameters).build();
        Element avoinMore = createVarEqualsToValueRule(avoin.getId(), "true");
        avoin.addChild(avoinMore);

        Element prevElement = buildAvoinElement(formParameters, 1, avoinMore, true);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraAvoinTutkintoRule = new AddElementRule("addAvoinTutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraAvoinTutkintoRule);
            prevElement = buildAvoinElement(formParameters, i, extraAvoinTutkintoRule, false);
        }

        return avoin;
    }

    private static Element buildAvoinElement(FormParameters formParameters, int index, Element parent, boolean required) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder alaBuilder = TextQuestion("pohjakoulutus_avoin_ala" + postfix)
                .labelKey("pohjakoulutus_avoin_ala").formParams(formParameters);
        ElementBuilder kokonaisuusBuilder = TextQuestion("pohjakoulutus_avoin_kokonaisuus" + postfix)
                .labelKey("pohjakoulutus_avoin_kokonaisuus").formParams(formParameters);
        ElementBuilder laajuusBuilder = TextQuestion("pohjakoulutus_avoin_laajuus" + postfix)
                .labelKey("pohjakoulutus_avoin_laajuus").formParams(formParameters);
        ElementBuilder korkeakouluBuilder = TextQuestion("pohjakoulutus_avoin_korkeakoulu" + postfix).labelKey("pohjakoulutus.korkeakoulu")
                .formParams(formParameters);

        if (required) {
            alaBuilder = alaBuilder.requiredInline();
            kokonaisuusBuilder = kokonaisuusBuilder.requiredInline();
            laajuusBuilder = laajuusBuilder.requiredInline();
            korkeakouluBuilder = korkeakouluBuilder.requiredInline();
        } else {
            alaBuilder = alaBuilder.inline();
            kokonaisuusBuilder = kokonaisuusBuilder.inline();
            laajuusBuilder = laajuusBuilder.inline();
            korkeakouluBuilder = korkeakouluBuilder.inline();
        }
        Element ala = alaBuilder.build();
        Element kokonaisuus = kokonaisuusBuilder.build();
        Element laajuus = laajuusBuilder.build();
        Element korkeakoulu = korkeakouluBuilder.build();
        parent.addChild(ala, kokonaisuus, laajuus, korkeakoulu);
        return korkeakoulu;
    }

    private static Element buildUlkomainenTutkinto(FormParameters formParameters, int count) {
        Element ulk = Checkbox("pohjakoulutus_ulk").formParams(formParameters).build();
        Element ulkMore = createVarEqualsToValueRule(ulk.getId(), "true");
        ulk.addChild(ulkMore);

        Element prevElement = buildUlkomainenTutkintoElement(formParameters, 1, ulkMore, true);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraUlkTutkintoRule = new AddElementRule("addUlkTutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraUlkTutkintoRule);
            prevElement = buildUlkomainenTutkintoElement(formParameters, i, extraUlkTutkintoRule, false);
        }

        return ulk;
    }

    private static Element buildUlkomainenTutkintoElement(FormParameters formParameters, int index, Element parent, boolean required) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder vuosiBuilder = TextQuestion("pohjakoulutus_ulk_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_ulk_vuosi.excel")
                .labelKey("pohjakoulutus_ulk_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters);
        ElementBuilder nimikeBuilder = TextQuestion("pohjakoulutus_ulk_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters);
        ElementBuilder oppilaitosBuilder = TextQuestion("pohjakoulutus_ulk_oppilaitos" + postfix).labelKey("pohjakoulutus.oppilaitos")
                .formParams(formParameters);
        ElementBuilder maaBuilder = TextQuestion("pohjakoulutus_ulk_suoritusmaa" + postfix).labelKey("pohjakoulutus.suoritusmaa")
                .formParams(formParameters);

        if (required) {
            vuosiBuilder = vuosiBuilder.requiredInline();
            nimikeBuilder = nimikeBuilder.requiredInline();
            oppilaitosBuilder = oppilaitosBuilder.requiredInline();
            maaBuilder = maaBuilder.requiredInline();
        } else {
            vuosiBuilder = vuosiBuilder.inline();
            nimikeBuilder = nimikeBuilder.inline();
            oppilaitosBuilder = oppilaitosBuilder.inline();
            maaBuilder = maaBuilder.inline();
        }
        Element vuosi = vuosiBuilder.build();
        Element nimike = nimikeBuilder.build();
        Element oppilaitos = oppilaitosBuilder.build();
        Element maa = maaBuilder.build();
        parent.addChild(vuosi, nimike, oppilaitos, maa);

        return oppilaitos;
    }

    private static Element buildKorkeakoulututkinto(FormParameters formParameters, List<Option> tutkintotasot, int count) {
        Element kk = Checkbox("pohjakoulutus_kk").formParams(formParameters).build();
        Element kkMore = createVarEqualsToValueRule(kk.getId(), "true");
        kk.addChild(kkMore);

        Element prevElement = buildKorkeakoulututkintoElement(formParameters, tutkintotasot, 1, kkMore, true);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraKKTutkintoRule = new AddElementRule("addKKtutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraKKTutkintoRule);
            prevElement = buildKorkeakoulututkintoElement(formParameters, tutkintotasot, i, extraKKTutkintoRule, false);
        }

        return kk;
    }

    private static Element buildKorkeakoulututkintoElement(FormParameters formParameters, List<Option> tutkintotasot,
                                                           int index, Element parent, boolean required) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder tasoBuilder = Dropdown("pohjakoulutus_kk_taso" + postfix)
                .addOptions(tutkintotasot).labelKey("pohjakoulutus.tutkintotaso").formParams(formParameters);
        ElementBuilder pvmBuilder = Date("pohjakoulutus_kk_pvm" + postfix)
                .labelKey("pohjakoulutus_kk_pvm")
                .formParams(formParameters);
        ElementBuilder nimikeBuilder = TextQuestion("pohjakoulutus_kk_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters);
        ElementBuilder oppilaitosBuilder = TextQuestion("pohjakoulutus_kk_oppilaitos" + postfix)
                .labelKey("pohjakoulutus_kk_oppilaitos")
                .formParams(formParameters);


        if (required) {
            tasoBuilder = tasoBuilder.requiredInline();
            pvmBuilder = pvmBuilder.requiredInline();
            nimikeBuilder = nimikeBuilder.requiredInline();
            oppilaitosBuilder = oppilaitosBuilder.requiredInline();
        } else {
            tasoBuilder = tasoBuilder.inline();
            pvmBuilder = pvmBuilder.inline();
            nimikeBuilder = nimikeBuilder.inline();
            oppilaitosBuilder = oppilaitosBuilder.inline();
        }

        Element taso = tasoBuilder.build();
        Element pvm = pvmBuilder.build();
        Element nimike = nimikeBuilder.build();
        Element oppilaitos = oppilaitosBuilder.build();

        parent.addChild(taso, pvm, nimike, oppilaitos);

        return oppilaitos;
    }

    private static Element buildKorkeakoulututkintoUlkomaa(FormParameters formParameters, List<Option> tutkintotasot,
                                                           List<Option> maat, int count) {
        Element kk_ulkomainen = Checkbox("pohjakoulutus_kk_ulk").formParams(formParameters).build();
        Element kkUlkomainenMore = createVarEqualsToValueRule(kk_ulkomainen.getId(), "true");

        kk_ulkomainen.addChild(kkUlkomainenMore);

        Element prevElement = buildKorkeakoulututkintoUlkomaaElement(formParameters, tutkintotasot, maat, 1, kkUlkomainenMore, true);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraKKUlkomaaRule = new AddElementRule("addKKUlkomaaRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraKKUlkomaaRule);
            prevElement = buildKorkeakoulututkintoUlkomaaElement(formParameters, tutkintotasot, maat, i, extraKKUlkomaaRule, false);
        }

        return kk_ulkomainen;
    }

    private static Element buildKorkeakoulututkintoUlkomaaElement(FormParameters formParameters, List<Option> tutkintotasot,
                                                           List<Option> maat, int index, Element parent, boolean required) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder tasoBuilder = Dropdown("pohjakoulutus_kk_ulk_taso" + postfix)
                .addOptions(tutkintotasot).labelKey("pohjakoulutus.tutkintotaso").formParams(formParameters);
        ElementBuilder pvmBuilder = Date("pohjakoulutus_kk_ulk_pvm" + postfix)
                .labelKey("pohjakoulutus_kk_pvm")
                .formParams(formParameters);
        ElementBuilder nimikeBuilder = TextQuestion("pohjakoulutus_kk_ulk_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters);
        ElementBuilder oppilaitosBuilder = TextQuestion("pohjakoulutus_kk_ulk_oppilaitos" + postfix)
                .labelKey("pohjakoulutus_kk_oppilaitos")
                .formParams(formParameters);
        ElementBuilder kk_ulkomainen_missaBuilder = Dropdown("pohjakoulutus_kk_ulk_maa" + postfix)
                .addOptions(maat)
                .labelKey("pohjakoulutus_kk_ulk_maa")
                .formParams(formParameters);

        if (required) {
            tasoBuilder = tasoBuilder.requiredInline();
            pvmBuilder = pvmBuilder.requiredInline();
            nimikeBuilder = nimikeBuilder.requiredInline();
            oppilaitosBuilder = oppilaitosBuilder.requiredInline();
            kk_ulkomainen_missaBuilder = kk_ulkomainen_missaBuilder.requiredInline();
        } else {
            tasoBuilder = tasoBuilder.inline();
            pvmBuilder = pvmBuilder.inline();
            nimikeBuilder = nimikeBuilder.inline();
            oppilaitosBuilder = oppilaitosBuilder.inline();
            kk_ulkomainen_missaBuilder = kk_ulkomainen_missaBuilder.inline();
        }
        Element taso = tasoBuilder.build();
        Element pvm = pvmBuilder.build();
        Element nimike = nimikeBuilder.build();
        Element oppilaitos = oppilaitosBuilder.build();
        Element kk_ulkomainen_missa = kk_ulkomainen_missaBuilder.build();

        parent.addChild(taso, pvm, nimike, oppilaitos, kk_ulkomainen_missa);
        return kk_ulkomainen_missa;
    }

    private static Element buildAmmattitutkinto(FormParameters formParameters, int count) {
        Element amt = Checkbox("pohjakoulutus_amt").formParams(formParameters).build();
        Element amtMore = createVarEqualsToValueRule(amt.getId(), "true");
        amt.addChild(amtMore);

        Element prevElement = buildAmmattitutkintoElement(formParameters, 1, amtMore, true);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraAmmattitutkintoRule = new AddElementRule("addAmmattitutkintoRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraAmmattitutkintoRule);
            prevElement = buildAmmattitutkintoElement(formParameters, i, extraAmmattitutkintoRule, false);
        }

        return amt;
    }

    private static Element buildAmmattitutkintoElement(FormParameters formParameters, int index, Element parent, boolean required) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder vuosiBuilder = TextQuestion("pohjakoulutus_amt_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_amt_vuosi.excel")
                .labelKey("pohjakoulutus_amt_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters);

        ElementBuilder nimikeBuilder = TextQuestion("pohjakoulutus_amt_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters);

        ElementBuilder oppilaitosBuilder = TextQuestion("pohjakoulutus_amt_oppilaitos" + postfix).labelKey("pohjakoulutus.oppilaitos")
                .formParams(formParameters);

        if (required) {
            vuosiBuilder = vuosiBuilder.requiredInline();
            nimikeBuilder = nimikeBuilder.requiredInline();
            oppilaitosBuilder = oppilaitosBuilder.requiredInline();
        } else {
            vuosiBuilder = vuosiBuilder.inline();
            nimikeBuilder = nimikeBuilder.inline();
            oppilaitosBuilder = oppilaitosBuilder.inline();
        }

        Element vuosi = vuosiBuilder.build();
        Element nimike = nimikeBuilder.build();
        Element oppilaitos = oppilaitosBuilder.build();
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

        Element prevElement = buildAmmatillinenElement(formParameters, laajuusYksikot, 1, amMore, true);

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraAmmatillinenRule = new AddElementRule("addAmmatillinenRule" + i, prevElement.getId(), i18nText);
            prevElement.addChild(extraAmmatillinenRule);
            prevElement = buildAmmatillinenElement(formParameters, laajuusYksikot, i, extraAmmatillinenRule, false);
        }

        return am;
    }

    private static Element buildAmmatillinenElement(FormParameters formParameters, List<Option> laajuusYksikot, int index, Element parent, boolean required) {
        String postfix = index == 1 ? "" : String.valueOf(index);
        ElementBuilder vuosiBuilder = TextQuestion("pohjakoulutus_am_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_am_vuosi.excel")
                .labelKey("pohjakoulutus_am_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters);

        ElementBuilder nimikeBuilder = TextQuestion("pohjakoulutus_am_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters);

        ElementBuilder laajuusBuilder = TextQuestion("pohjakoulutus_am_laajuus" + postfix).labelKey("pohjakoulutus.tutkinnonLaajuus")
                .formParams(formParameters);

        ElementBuilder oppilaitosBuilder = TextQuestion("pohjakoulutus_am_oppilaitos" + postfix).labelKey("pohjakoulutus.oppilaitos")
                .formParams(formParameters);

        if (required) {
            vuosiBuilder = vuosiBuilder.requiredInline();
            nimikeBuilder = nimikeBuilder.requiredInline();
            laajuusBuilder = laajuusBuilder.requiredInline();
            oppilaitosBuilder = oppilaitosBuilder.requiredInline();
        } else {
            vuosiBuilder = vuosiBuilder.inline();
            nimikeBuilder = nimikeBuilder.inline();
            laajuusBuilder = laajuusBuilder.inline();
            oppilaitosBuilder = oppilaitosBuilder.inline();
        }

        Element vuosi = vuosiBuilder.build();
        Element nimike = nimikeBuilder.build();
        Element laajuus = laajuusBuilder.build();
        Element laajuusYksikko = Dropdown("pohjakoulutus_am_laajuus_yksikko" + postfix)
                .addOptions(laajuusYksikot)
                .excelColumnLabel("laajuusyksikko.excel")
                .inline().formParams(formParameters).labelKey("form.yleinen.nbsp").build();
        Element oppilaitos = oppilaitosBuilder.build();

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
