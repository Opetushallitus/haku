
package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.*;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Notification;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.OptionQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.haku.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.oppija.lomake.validation.validators.YearValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.KoodiTypeToOptionFunction;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.OrganizationToOptionFunction;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;

import static fi.vm.sade.haku.oppija.hakemus.service.Role.*;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.DateQuestionBuilder.Date;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder.Dropdown;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.NotificationBuilder.Info;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.NotificationBuilder.Warning;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.SecondaryEducationCountryRadioBuilder.SecondaryEducationCountryRadio;
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
    public static final String UNKNOWN_OID = "0.0.0.0.0.0";

    private KoulutustaustaPhase() {
    }

    public static Element create(final FormParameters formParameters) {
        Element koulutustausta = Phase(PHASE_EDUCATION).setEditAllowedByRoles(ROLE_RU, ROLE_CRUD).formParams(formParameters).build();
        ApplicationSystem as = formParameters.getApplicationSystem();
        if (as.getKohdejoukkoUri().equals(KOHDEJOUKKO_KORKEAKOULU)){
            if (formParameters.isAmmattillinenEritysopettajaTaiOppilaanohjaajaKoulutus()
                    || formParameters.isAmmattillinenOpettajaKoulutus()) {
                Element koulutustaustaRyhma = new ThemeBuilder("koulutustausta_teema_kk").previewable().formParams(formParameters).build();
                if (!formParameters.isOnlyThemeGenerationForFormEditor()) {
                    koulutustaustaRyhma.addChild(createOpetErkatJaOpotKoulutustausta(formParameters));
                    koulutustausta.addChild(koulutustaustaRyhma);
                }
            } else {
                Element koulutustaustaRyhma = new ThemeBuilder("koulutustausta_teema_kk").previewable().formParams(formParameters).build();
                if (!formParameters.isOnlyThemeGenerationForFormEditor()) {
                    koulutustaustaRyhma.addChild(createKorkeakouluKoulutustausta(formParameters));
                    koulutustausta.addChild(koulutustaustaRyhma);
                }
            }
        } else {
            Element koulutustaustaRyhma = new ThemeBuilder("koulutustausta_teema").previewable().formParams(formParameters).build();
            if (!formParameters.isOnlyThemeGenerationForFormEditor()) {
                koulutustaustaRyhma.addChild(createKoulutustaustaRadio(formParameters));
                koulutustausta.addChild(koulutustaustaRyhma);
            }
        }
        return koulutustausta;
    }

    private static Element[] createOpetErkatJaOpotKoulutustausta(FormParameters formParameters) {
        ArrayList<Element> elements = new ArrayList<>();
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
                .emptyOptionDefault()
                .addOption(formParameters.getI18nText("amk_ope_tutkinnontaso.tohtori"), "tohtori")
                .addOption(formParameters.getI18nText("amk_ope_tutkinnontaso.ylempi"), "ylempi")
                .addOption(formParameters.getI18nText("amk_ope_tutkinnontaso.amk"), "amk")
                .addOption(formParameters.getI18nText("amk_ope_tutkinnontaso.alempi"), "alempi")
                .addOption(formParameters.getI18nText("amk_ope_tutkinnontaso.opisto"), "opisto")
                .addOption(formParameters.getI18nText("amk_ope_tutkinnontaso.ammatillinen"), "ammatillinen")
                .addOption(formParameters.getI18nText("amk_ope_tutkinnontaso.ammatti"), "ammatti")
                .addOption(formParameters.getI18nText("amk_ope_tutkinnontaso.muu"), "muu")
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
                .addOption(formParameters.getI18nText("ei_korkeakoulututkintoa.opettajana_ammatillisessa_tutkinto"),
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
                    .addOption(formParameters.getI18nText("ei_korkeakoulututkintoa.opettajana_ammatillisessa"),
                            "opettajana_ammatillisessa")
                    .addChild(opettajaAmmatillisessaRule);
        }

        // ...en toimi ammatillisen koulutuksen opetustehtävissä
        eiKorkeakoulututkintoaBuilder.addOption(
          formParameters.getI18nText("ei_korkeakoulututkintoa.ei_opettajana_ammatillisessa"),
          "ei_opettajana_ammatillisessa");

        tutkinnontasoRule.addChild(eiKorkeakoulututkintoaBuilder.formParams(formParameters).requiredInline().build());

        List<Option> koulutusalatRaw = koodistoService.getKoulutusalat();
        List<Option> koulutusalat = new ArrayList<>(koulutusalatRaw.size() - 1);
        for (Option o : koulutusalatRaw) {
            if (!o.getValue().equals("0")) {
                koulutusalat.add(o);
            }
        }
        // Koulutus- ja opintoala
        OptionQuestion koulutusala = (OptionQuestion) Dropdown("amk_ope_koulutusala")
                .emptyOptionDefault()
                .addOptions(koulutusalat)
                .excelColumnLabel("amk_ope_koulutusala.excel")
                .formParams(formParameters)
                .required()
                .inline()
                .build();
        elements.add(koulutusala);
        for (Option koulutusalaOption : koulutusalat) {
            String koulutusalaKoodi = koulutusalaOption.getValue();
            Element opintoalaRule = createVarEqualsToValueRule("amk_ope_koulutusala", koulutusalaKoodi);
            OptionQuestionBuilder opintoalaBuilder = Dropdown("amk_ope_opintoala")
                    .emptyOptionDefault();
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
                    .addOption(formParameters.getI18nText("pedagogiset_opinnot.kylla"), "true")
                    .addOption(formParameters.getI18nText("pedagogiset_opinnot.ei"), "false")
                    .requiredInline()
                    .formParams(formParameters)
                    .build();
            elements.add(pedagogisetOpinnot);

            Element pedagogisetOpinnotEiSuoritettuRule = createVarEqualsToValueRule("pedagogiset_opinnot", "false");
            pedagogisetOpinnotEiSuoritettuRule.addChild(Info()
                    .i18nText(formParameters.getI18nText("pedagogiset_opinnot.ei.info"))
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
        if (formParameters.isAmmattillinenOpettajaKoulutus() && formParameters.isAMKOpeMuutTutkinnotKysymys()) {
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

    private static Expr anyQuestionNotEmpty(final List<Element> elements) {
        final List<Element> questions = ImmutableList.copyOf(Iterables.filter(elements, new Predicate<Element>() {
            @Override
            public boolean apply(@Nullable Element input) {
                return input instanceof Question;
            }
        }));

        if (questions.size() == 0) {
            throw new IllegalStateException("no questions");
        }

        return new Any(
                ImmutableList.copyOf(Iterables.transform(questions, new Function<Element, Expr>() {
                    @Override
                    public Expr apply(Element child) {
                        return new Not(new Equals(new Variable(child.getId()), new Value("")));
                    }
                }))
        );
    }

    private static Element[] createKorkeakouluKoulutustausta(FormParameters formParameters) {
        ArrayList<Element> elements = new ArrayList<>();
        KoodistoService koodistoService = formParameters.getKoodistoService();
        List<Option> laajuusYksikot = koodistoService.getLaajuusYksikot();
        List<Option> tutkintotasot = koodistoService.getKorkeakouluTutkintotasot();
        List<Option> maat = koodistoService.getCountries();
        List<Option> ammattitutkintonimikkeet = koodistoService.getAmmattitutkinnot();
        List<Option> ammattioppilaitokset = koodistoService.getAmmattioppilaitosKoulukoodit();
        List<Option> korkeakoulut = Lists.newArrayList(Iterables.transform(
                Iterables.filter(koodistoService.getKorkeakoulutMyosRinnasteiset(), new Predicate<Organization>() {
                    public boolean apply(Organization organization) {
                        // Högskolan på Åland ei kuulu Suomen KK järjestelmään vaikka löytyykin koodistosta
                        return !("1.2.246.562.10.444626308710".equals(organization.getOid())
                                || "1.2.246.562.10.50686854907".equals(organization.getOid()));
                    }
                }),
                new OrganizationToOptionFunction()));
        List<Option> korkeakoulukoulutukset = Lists.transform(koodistoService.getKorkeakoulukoulutukset(), new KoodiTypeToOptionFunction());

        Element pohjakoulutusGrp = TitledGroup("pohjakoulutus.korkeakoulut")
                .required().formParams(formParameters).build();

        final int maxTutkintoCount = formParameters.getTutkintoCountMax();
        final int maxAvoinTutkintoCount = 6;

        pohjakoulutusGrp.addChild(
                buildYoSuomalainen(formParameters, laajuusYksikot, ammattitutkintonimikkeet, ammattioppilaitokset),
                buildYoKansainvalinenSuomessa(formParameters),
                buildAmmatillinen(formParameters, laajuusYksikot, ammattitutkintonimikkeet, ammattioppilaitokset, maxTutkintoCount),
                buildAmmattitutkinto(formParameters, maxTutkintoCount),
                buildKorkeakoulututkinto(formParameters, tutkintotasot, maxTutkintoCount),
                buildYoUlkomainen(formParameters, maat),
                buildKorkeakoulututkintoUlkomaa(formParameters, tutkintotasot, maat, maxTutkintoCount),
                buildUlkomainenTutkinto(formParameters, maat, maxTutkintoCount),
                buildAvoin(formParameters, maxAvoinTutkintoCount),
                buildMuu(formParameters, maxTutkintoCount));

        if (formParameters.getApplicationSystem().isMaksumuuriKaytossa()) {
            final Element rule = Rule(
                    new And(
                        anyQuestionNotEmpty(pohjakoulutusGrp.getAllChildren()),
                        ExprUtil.isAnswerTrue(PHASE_EDUCATION + PAYMENT_NOTIFICATION_POSTFIX)
                    )
            ).addChild(
                    new Notification(
                        PHASE_EDUCATION + "_payment_notification",
                        formParameters.getI18nText("form.koulutustausta.vaatiihakumaksun"),
                        Notification.NotificationType.INFO
                    )
            ).build();
            pohjakoulutusGrp.addChild(rule);
        }

        elements.add(pohjakoulutusGrp);

        // Toisen asteen pohjakoulutuksen maa
        elements.add(buildToisenAsteenSuoritusMaa(formParameters));

        if(formParameters.askOldEducationInfo()) {
            elements.addAll(buildSuoritusoikeusTaiAiempiTutkinto(formParameters, korkeakoulut, korkeakoulukoulutukset));
        }

        return elements.toArray(new Element[elements.size()]);
    }

    private static Element buildToisenAsteenSuoritusMaa(FormParameters formParameters) {
        KoodistoService koodistoService = formParameters.getKoodistoService();
        List<Option> maat = koodistoService.getCountries();

        return buildToisenAsteenSuoritusMaaRadio(formParameters, maat);
    }

    private static Element buildToisenAsteenSuoritusMaaRadio(FormParameters formParameters, List<Option> maat) {
        Option kyllaOption = new Option(formParameters.getI18nText("form.koulutustausta.valitse_maa"), KYLLA);
        Option eiOption = new Option(formParameters.getI18nText("form.koulutustausta.ei_toisen_asteen_tutkintoa"), EI);
        String radioId = TOISEN_ASTEEN_SUORITUS;

        Element taSuoritusmaaRadio = SecondaryEducationCountryRadio(radioId)
                .addOptions(ImmutableList.of(kyllaOption, eiOption))
                .required()
                .formParams(formParameters).build();

        Element taSuoritusmaaDropdown = Dropdown(TOISEN_ASTEEN_SUORITUSMAA)
                .emptyOptionDefault()
                .addOptions(maat)
                .labelKey(TOISEN_ASTEEN_SUORITUSMAA)
                .requiredInline()
                .formParams(formParameters).build();

        Element kyllaOptionMore = buildTrueOptionMore(radioId, taSuoritusmaaDropdown);

        kyllaOption.addChild(kyllaOptionMore);

        return taSuoritusmaaRadio;
    }

    private static Element buildTrueOptionMore(String id, Element child) {
        return Rule(new Equals(new Variable(id), new Value(KYLLA))).build().addChild(child);
    }

    private static List<Element> buildSuoritusoikeusTaiAiempiTutkinto(FormParameters formParameters,
                                                                      List<Option> korkeakoulut,
                                                                      List<Option> korkeakoulukoulutukset) {
        Element suoritusoikeusTaiAiempiTutkinto = Radio("suoritusoikeus_tai_aiempi_tutkinto")
                .addOptions(ImmutableList.of(
                        new Option(formParameters.getI18nText("form.yleinen.kylla"), KYLLA),
                        new Option(formParameters.getI18nText("form.yleinen.ei"), EI)))
                .required()
                .formParams(formParameters).build();
        if (formParameters.additionalInfoForPreviousDegree()) {
            Element kysytaanAiemmanTutkinnonLisatiedot = Rule(new Equals(new Variable("suoritusoikeus_tai_aiempi_tutkinto"), new Value(KYLLA))).build();
            kysytaanAiemmanTutkinnonLisatiedot.addChild(buildAiemmanTutkinnonLisatiedot(formParameters, korkeakoulut, korkeakoulukoulutukset));
            return Lists.newArrayList(suoritusoikeusTaiAiempiTutkinto, kysytaanAiemmanTutkinnonLisatiedot);
        } else {
            return Lists.newArrayList(suoritusoikeusTaiAiempiTutkinto);
        }
    }

    private static Element[] buildAiemmanTutkinnonLisatiedot(FormParameters formParameters,
                                                             List<Option> korkeakoulut,
                                                             List<Option> korkeakoulukoulutukset) {
        YearValidator yearValidator = (YearValidator)ElementUtil.createYearValidator(1994, 1900);
        yearValidator.setTooLateErrorKey("suoritusoikeus_tai_aiempi_tutkinto_vuosi_liian_uusi");
        Element vuosi = TextQuestion("suoritusoikeus_tai_aiempi_tutkinto_vuosi")
                .labelKey("suoritusoikeus_tai_aiempi_tutkinto_vuosi")
                .validator(yearValidator)
                .requiredInline()
                .formParams(formParameters)
                .build();
        Element nimi = Dropdown("suoritusoikeus_tai_aiempi_tutkinto_nimi")
                .emptyOptionDefault()
                .addOptions(korkeakoulukoulutukset)
                .labelKey("suoritusoikeus_tai_aiempi_tutkinto_nimi")
                .requiredInline()
                .formParams(formParameters)
                .build();
        Element oppilaitos = Dropdown("suoritusoikeus_tai_aiempi_tutkinto_oppilaitos")
                .emptyOptionDefault()
                .addOption(formParameters.getI18nText("suoritusoikeus_tai_aiempi_tutkinto_oppilaitos_muu"), UNKNOWN_OID)
                .addOptions(korkeakoulut)
                .keepFirst("", UNKNOWN_OID)
                .labelKey("suoritusoikeus_tai_aiempi_tutkinto_oppilaitos")
                .requiredInline()
                .formParams(formParameters)
                .build();
        Element muuOppilaitosRule = createVarEqualsToValueRule(oppilaitos.getId(), UNKNOWN_OID);
        muuOppilaitosRule.addChild(TextQuestion("suoritusoikeus_tai_aiempi_tutkinto_oppilaitos_muu")
                .labelKey("suoritusoikeus_tai_aiempi_tutkinto_oppilaitos_muu")
                .requiredInline()
                .formParams(formParameters)
                .build());
        return new Element[]{vuosi, nimi, oppilaitos, muuOppilaitosRule};
    }

    private static Element buildMuu(FormParameters formParameters, int count) {
        Element muu = Checkbox("pohjakoulutus_muu").formParams(formParameters).build();
        Element muuMore = createVarEqualsToValueRule(muu.getId(), "true");
        muu.addChild(muuMore);

        Element prevElement = buildMuuElement(formParameters, 1, muuMore);
        List<String> prevLinks = new ArrayList<>();

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraMuuTutkintoRule = new AddElementRule("addMuuTutkintoRule" + i, prevElement.getId(),
                    prevLinks, i18nText);
            prevLinks.add(extraMuuTutkintoRule.getId());
            prevElement.addChild(extraMuuTutkintoRule);
            prevElement = buildMuuElement(formParameters, i, extraMuuTutkintoRule);
        }

        return muu;
    }

    private static Element buildMuuElement(FormParameters formParameters, int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder vuosiBuilder = TextQuestion("pohjakoulutus_muu_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_muu_vuosi.excel")
                .labelKey("pohjakoulutus_muu_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters);
        ElementBuilder kuvausBuilder = TextArea("pohjakoulutus_muu_kuvaus" + postfix)
                .labelKey("pohjakoulutus_muu_kuvaus").formParams(formParameters);

        vuosiBuilder = vuosiBuilder.requiredInline();
        kuvausBuilder = kuvausBuilder.requiredInline();

        Element vuosi = vuosiBuilder.build();
        Element kuvaus = kuvausBuilder.build();
        parent.addChild(vuosi, kuvaus);
        return kuvaus;
    }

    private static Element buildAvoin(FormParameters formParameters, int count) {
        Element avoin = Checkbox("pohjakoulutus_avoin").formParams(formParameters).build();
        Element avoinMore = createVarEqualsToValueRule(avoin.getId(), "true");
        avoin.addChild(avoinMore);

        Element prevElement = buildAvoinElement(formParameters, 1, avoinMore);
        List<String> prevLinks = new ArrayList<>();

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraAvoinTutkintoRule = new AddElementRule("addAvoinTutkintoRule" + i, prevElement.getId(),
                    prevLinks, i18nText);
            prevLinks.add(extraAvoinTutkintoRule.getId());
            prevElement.addChild(extraAvoinTutkintoRule);
            prevElement = buildAvoinElement(formParameters, i, extraAvoinTutkintoRule);
        }

        return avoin;
    }

    private static Element buildAvoinElement(FormParameters formParameters, int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder alaBuilder = TextQuestion("pohjakoulutus_avoin_ala" + postfix)
                .labelKey("pohjakoulutus_avoin_ala").formParams(formParameters);
        ElementBuilder kokonaisuusBuilder = TextQuestion("pohjakoulutus_avoin_kokonaisuus" + postfix)
                .labelKey("pohjakoulutus_avoin_kokonaisuus").formParams(formParameters);
        ElementBuilder laajuusBuilder = TextQuestion("pohjakoulutus_avoin_laajuus" + postfix)
                .labelKey("pohjakoulutus_avoin_laajuus").formParams(formParameters);
        ElementBuilder korkeakouluBuilder = TextQuestion("pohjakoulutus_avoin_korkeakoulu" + postfix).labelKey("pohjakoulutus.korkeakoulu")
                .formParams(formParameters);

        alaBuilder = alaBuilder.requiredInline();
        kokonaisuusBuilder = kokonaisuusBuilder.requiredInline();
        laajuusBuilder = laajuusBuilder.requiredInline();
        korkeakouluBuilder = korkeakouluBuilder.requiredInline();

        Element ala = alaBuilder.build();
        Element kokonaisuus = kokonaisuusBuilder.build();
        Element laajuus = laajuusBuilder.build();
        Element korkeakoulu = korkeakouluBuilder.build();
        parent.addChild(ala, kokonaisuus, laajuus, korkeakoulu);
        return korkeakoulu;
    }

    private static Element buildUlkomainenTutkinto(FormParameters formParameters, List<Option> maat, int count) {
        Element ulk = Checkbox("pohjakoulutus_ulk").formParams(formParameters).build();
        Element ulkMore = createVarEqualsToValueRule(ulk.getId(), "true");
        ulk.addChild(ulkMore);

        Element parent = ulkMore;
        buildUlkomainenTutkintoElement(formParameters, maat, 1, parent);
        List<String> prevLinks = new ArrayList<>();
        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraUlkTutkintoRule = new AddElementRule("addUlkTutkintoRule" + i,
                    "pohjakoulutus_ulk_nimike" + (i - 1 == 1 ? "" : String.valueOf(i - 1)),
                    prevLinks, i18nText);
            prevLinks.add(extraUlkTutkintoRule.getId());
            parent.addChild(extraUlkTutkintoRule);
            parent = extraUlkTutkintoRule;
            buildUlkomainenTutkintoElement(formParameters, maat, i, parent);
        }

        return ulk;
    }

    private static void buildUlkomainenTutkintoElement(FormParameters formParameters, List<Option> maat, int index, Element parent) {
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

        vuosiBuilder = vuosiBuilder.requiredInline();
        nimikeBuilder = nimikeBuilder.requiredInline();
        oppilaitosBuilder = oppilaitosBuilder.requiredInline();

        Element vuosi = vuosiBuilder.build();
        Element nimike = nimikeBuilder.build();
        Element oppilaitos = oppilaitosBuilder.build();
        Element maa = buildSuoritusmaa(formParameters, maat, "pohjakoulutus_ulk_suoritusmaa", postfix);
        Element muuMaaRule = buildMuuSuoritusmaa(formParameters, "pohjakoulutus_ulk_suoritusmaa", postfix);
        parent.addChild(vuosi, nimike, oppilaitos, maa, muuMaaRule);
    }

    private static Element buildKorkeakoulututkinto(FormParameters formParameters, List<Option> tutkintotasot, int count) {
        Element kk = Checkbox("pohjakoulutus_kk").formParams(formParameters).build();
        Element kkMore = createVarEqualsToValueRule(kk.getId(), "true");
        kk.addChild(kkMore);

        Element prevElement = buildKorkeakoulututkintoElement(formParameters, tutkintotasot, 1, kkMore);
        List<String> prevLinks = new ArrayList<>();

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraKKTutkintoRule = new AddElementRule("addKKtutkintoRule" + i, prevElement.getId(),
                    prevLinks, i18nText);
            prevLinks.add(extraKKTutkintoRule.getId());
            prevElement.addChild(extraKKTutkintoRule);
            prevElement = buildKorkeakoulututkintoElement(formParameters, tutkintotasot, i, extraKKTutkintoRule);
        }

        return kk;
    }

    private static Element buildKorkeakoulututkintoElement(FormParameters formParameters, List<Option> tutkintotasot,
                                                           int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder tasoBuilder = Dropdown("pohjakoulutus_kk_taso" + postfix)
                .emptyOptionDefault()
                .addOptions(tutkintotasot).labelKey("pohjakoulutus.tutkintotaso").formParams(formParameters);
        ElementBuilder pvmBuilder = Date("pohjakoulutus_kk_pvm" + postfix)
                .allowFutureDates()
                .labelKey("pohjakoulutus_kk_pvm")
                .formParams(formParameters);
        ElementBuilder nimikeBuilder = TextQuestion("pohjakoulutus_kk_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters);
        ElementBuilder oppilaitosBuilder = TextQuestion("pohjakoulutus_kk_oppilaitos" + postfix)
                .labelKey("pohjakoulutus_kk_oppilaitos")
                .formParams(formParameters);

        tasoBuilder = tasoBuilder.requiredInline();
        pvmBuilder = pvmBuilder.requiredInline();
        nimikeBuilder = nimikeBuilder.requiredInline();
        oppilaitosBuilder = oppilaitosBuilder.requiredInline();

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

        Element parent = kkUlkomainenMore;
        buildKorkeakoulututkintoUlkomaaElement(formParameters, tutkintotasot, maat, 1, parent);
        List<String> prevLinks = new ArrayList<>();
        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraKKUlkomaaRule = new AddElementRule("addKKUlkomaaRule" + i,
                    "pohjakoulutus_kk_ulk_nimike" + (i - 1 == 1 ? "" : String.valueOf(i - 1)),
                    prevLinks, i18nText);
            prevLinks.add(extraKKUlkomaaRule.getId());
            parent.addChild(extraKKUlkomaaRule);
            parent = extraKKUlkomaaRule;
            buildKorkeakoulututkintoUlkomaaElement(formParameters, tutkintotasot, maat, i, parent);
        }

        return kk_ulkomainen;
    }

    private static void buildKorkeakoulututkintoUlkomaaElement(FormParameters formParameters, List<Option> tutkintotasot,
                                                           List<Option> maat, int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);

        ElementBuilder tasoBuilder = Dropdown("pohjakoulutus_kk_ulk_taso" + postfix)
                .emptyOptionDefault()
                .addOptions(tutkintotasot).labelKey("pohjakoulutus.tutkintotaso").formParams(formParameters);
        ElementBuilder pvmBuilder = Date("pohjakoulutus_kk_ulk_pvm" + postfix)
                .allowFutureDates()
                .labelKey("pohjakoulutus_kk_pvm")
                .formParams(formParameters);
        ElementBuilder nimikeBuilder = TextQuestion("pohjakoulutus_kk_ulk_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                .formParams(formParameters);
        ElementBuilder oppilaitosBuilder = TextQuestion("pohjakoulutus_kk_ulk_oppilaitos" + postfix)
                .labelKey("pohjakoulutus_kk_oppilaitos")
                .formParams(formParameters);

        tasoBuilder = tasoBuilder.requiredInline();
        pvmBuilder = pvmBuilder.requiredInline();
        nimikeBuilder = nimikeBuilder.requiredInline();
        oppilaitosBuilder = oppilaitosBuilder.requiredInline();

        Element taso = tasoBuilder.build();
        Element pvm = pvmBuilder.build();
        Element nimike = nimikeBuilder.build();
        Element oppilaitos = oppilaitosBuilder.build();
        Element kk_ulkomainen_missa = buildSuoritusmaa(formParameters, maat, "pohjakoulutus_kk_ulk_maa", postfix);
        Element kk_ulkomainen_missa_muuRule = buildMuuSuoritusmaa(formParameters, "pohjakoulutus_kk_ulk_maa", postfix);
        parent.addChild(taso, pvm, nimike, oppilaitos, kk_ulkomainen_missa, kk_ulkomainen_missa_muuRule);
    }

    private static Element buildAmmattitutkinto(FormParameters formParameters, int count) {
        Element amt = Checkbox("pohjakoulutus_amt").formParams(formParameters).build();
        Element amtMore = createVarEqualsToValueRule(amt.getId(), "true");
        amt.addChild(amtMore);

        Element prevElement = buildAmmattitutkintoElement(formParameters, 1, amtMore);
        List<String> prevLinks = new ArrayList<>();

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraAmmattitutkintoRule = new AddElementRule("addAmmattitutkintoRule" + i, prevElement.getId(),
                    prevLinks, i18nText);
            prevLinks.add(extraAmmattitutkintoRule.getId());
            prevElement.addChild(extraAmmattitutkintoRule);
            prevElement = buildAmmattitutkintoElement(formParameters, i, extraAmmattitutkintoRule);
        }

        return amt;
    }

    private static Element buildAmmattitutkintoElement(FormParameters formParameters, int index, Element parent) {
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

        vuosiBuilder = vuosiBuilder.requiredInline();
        nimikeBuilder = nimikeBuilder.requiredInline();
        oppilaitosBuilder = oppilaitosBuilder.requiredInline();

        Element vuosi = vuosiBuilder.build();
        Element nimike = nimikeBuilder.build();
        Element oppilaitos = oppilaitosBuilder.build();
        parent.addChild(vuosi, nimike, oppilaitos);
        return oppilaitos;
    }

    private static Element buildAmmatillinen(FormParameters formParameters, List<Option> laajuusYksikot,
                                             List<Option> tutkintonimikkeet, List<Option> ammattioppilaitokset,
                                             int count) {
        Element am = Checkbox("pohjakoulutus_am").formParams(formParameters).build();
        Element amMore = createVarEqualsToValueRule(am.getId(), "true");
        am.addChild(amMore);

        Element prevElement = buildAmmatillinenElement(formParameters, laajuusYksikot, tutkintonimikkeet,
                ammattioppilaitokset, 1, amMore);
        List<String> prevLinks = new ArrayList<>();

        for (int i = 2; i <= count; i++) {
            I18nText i18nText = formParameters.getI18nText("pohjakoulutus.lisaa");
            AddElementRule extraAmmatillinenRule = new AddElementRule("addAmmatillinenRule" + i, prevElement.getId(),
                    prevLinks, i18nText);
            prevLinks.add(extraAmmatillinenRule.getId());
            prevElement.addChild(extraAmmatillinenRule);
            prevElement = buildAmmatillinenElement(formParameters, laajuusYksikot, tutkintonimikkeet,
                    ammattioppilaitokset, i, extraAmmatillinenRule);
        }

        return am;
    }

    private static Element buildAmmatillinenElement(FormParameters formParameters, List<Option> laajuusYksikot,
                                                    List<Option> tutkintonimikkeet, List<Option> ammattioppilaitokset,
                                                    int index, Element parent) {
        String postfix = index == 1 ? "" : String.valueOf(index);
        ElementBuilder vuosiBuilder = TextQuestion("pohjakoulutus_am_vuosi" + postfix)
                .excelColumnLabel("pohjakoulutus_am_vuosi.excel")
                .labelKey("pohjakoulutus_am_vuosi")
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).requiredInline();

        ElementBuilder laajuusBuilder = TextQuestion("pohjakoulutus_am_laajuus" + postfix).labelKey("pohjakoulutus.tutkinnonLaajuus")
                .formParams(formParameters).requiredInline();

        Element vuosi = vuosiBuilder.build();
        Element laajuus = laajuusBuilder.build();
        Element laajuusYksikko = Dropdown("pohjakoulutus_am_laajuus_yksikko" + postfix)
                .emptyOptionDefault()
                .addOptions(laajuusYksikot)
                .excelColumnLabel("laajuusyksikko.excel")
                .inline().formParams(formParameters).labelKey("form.yleinen.nbsp").build();
        Element nayttotutkinto = Radio("pohjakoulutus_am_nayttotutkintona" + postfix)
                .addOptions(ImmutableList.of(
                        new Option(formParameters.getI18nText("form.yleinen.kylla"), KYLLA),
                        new Option(formParameters.getI18nText("form.yleinen.ei"), EI)))
                .defaultOption(EI)
                .requiredInline()
                .labelKey("pohjakoulutus_am_nayttotutkintona")
                .formParams(formParameters).build();

        Element nimike;
        Element oppilaitos;
        if (formParameters.isAmmatillinenDropdown()) {

            Element nimikekoodi = Dropdown("pohjakoulutus_am_nimike" + postfix)
                    .emptyOptionDefault()
                    .addOptions(tutkintonimikkeet)
                    .keepFirst("", TUTKINTO_MUU)
                    .labelKey("pohjakoulutus.tutkintonimike")
                    .formParams(formParameters)
                    .requiredInline()
                    .build();
            nimike = TextQuestion("pohjakoulutus_am_nimike_muu" + postfix).labelKey("muun.tutkinnon.nimi").requiredInline()
                    .formParams(formParameters).build();
            Element muuNimikeRule = createVarEqualsToValueRule(nimikekoodi.getId(), TUTKINTO_MUU);
            muuNimikeRule.addChild(nimike);

            Element oppilaitoskoodi = Dropdown("pohjakoulutus_am_oppilaitos" + postfix)
                    .emptyOptionDefault()
                    .keepFirst("", OPPILAITOS_TUNTEMATON)
                    .addOptions(ammattioppilaitokset)
                    .requiredInline()
                    .labelKey("pohjakoulutus.oppilaitos")
                    .formParams(formParameters)
                    .build();

            Element muuOppilaitosRule = createVarEqualsToValueRule(oppilaitoskoodi.getId(), OPPILAITOS_TUNTEMATON);
            muuOppilaitosRule.addChild(TextQuestion("pohjakoulutus_am_oppilaitos_muu" + postfix).labelKey("muun.oppilaitokseksen.nimi")
                    .requiredInline()
                    .formParams(formParameters).build());

            parent.addChild(vuosi,
                    nimikekoodi,
                    muuNimikeRule,
                    laajuus,
                    laajuusYksikko,
                    oppilaitoskoodi,
                    muuOppilaitosRule,
                    nayttotutkinto);

        } else {
            nimike = TextQuestion("pohjakoulutus_am_nimike" + postfix).labelKey("pohjakoulutus.tutkintonimike")
                    .requiredInline().formParams(formParameters).build();

            oppilaitos = TextQuestion("pohjakoulutus_am_oppilaitos" + postfix).labelKey("pohjakoulutus.oppilaitos")
                    .requiredInline().formParams(formParameters).build();

            parent.addChild(vuosi,
                    nimike,
                    laajuus,
                    laajuusYksikko,
                    oppilaitos,
                    nayttotutkinto);
        }

        return nayttotutkinto;
    }

    private static Element buildYoSuomalainen(FormParameters formParameters, List<Option> laajuusYksikot,
                                              List<Option> tutkintonimikkeet, List<Option> ammattioppilaitokset) {
        Element yo = Checkbox("pohjakoulutus_yo").formParams(formParameters).build();
        Element yoMore = createVarEqualsToValueRule(yo.getId(), "true");
        Element vuosi = TextQuestion("pohjakoulutus_yo_vuosi")
                .excelColumnLabel("pohjakoulutus_yo_vuosi.excel")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();
        Element yoTutkinto = Dropdown("pohjakoulutus_yo_tutkinto")
                .emptyOptionDefault()
                .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.fi"), "fi")
                .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.lk"), "lk")
                .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.lkonly"), "lkOnly")
                .requiredInline()
                .formParams(formParameters).build();
        yoMore.addChild(vuosi, yoTutkinto, buildYoAmmatillinen(formParameters, laajuusYksikot, tutkintonimikkeet, ammattioppilaitokset));
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
                .emptyOptionDefault()
                .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.ib"), "ib")
                .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.eb"), "eb")
                .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.rp"), "rp")
                .requiredInline()
                .formParams(formParameters).build();
        Element oppilaitos = TextQuestion("pohjakoulutus_yo_oppilaitos").excelColumnLabel("pohjakoulutus.oppilaitos")
                .requiredInline().formParams(formParameters).build();
        kansainvalinenSuomessaYoMore.addChild(vuosi, yoTutkintoKansainvalinenSuomessa, oppilaitos);
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
                .emptyOptionDefault()
                .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.ib"), "ib")
                .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.eb"), "eb")
                .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.rp"), "rp")
                .requiredInline()
                .formParams(formParameters).build();
        Element oppilaitos = TextQuestion("pohjakoulutus_yo_ulkomainen_oppilaitos").excelColumnLabel("pohjakoulutus.oppilaitos")
                .requiredInline().formParams(formParameters).build();
        Element ulkomainenYoMissa = buildSuoritusmaa(formParameters, maat, "pohjakoulutus_yo_ulkomainen_maa", "");
        Element ulkomainenYoMuuMissaRule = buildMuuSuoritusmaa(formParameters, "pohjakoulutus_yo_ulkomainen_maa", "");

        ulkomainenYoMore.addChild(vuosi, yoTutkintoUlkomainen, oppilaitos, ulkomainenYoMissa, ulkomainenYoMuuMissaRule);
        ulkomainenYo.addChild(ulkomainenYoMore);
        return ulkomainenYo;
    }

    private static Element buildYoAmmatillinen(FormParameters formParameters, List<Option> laajuusYksikot,
                                               List<Option> tutkintonimikkeet, List<Option> ammattioppilaitokset) {
        Element ammatillinen = Checkbox("pohjakoulutus_yo_ammatillinen").inline().formParams(formParameters).build();
        Element ammatillinenMore = createVarEqualsToValueRule(ammatillinen.getId(), "true");
        Element ammatillinenVuosi = TextQuestion("pohjakoulutus_yo_ammatillinen_vuosi")
                .requiredInline()
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                .formParams(formParameters).build();


        Element ammatillinenLaajuus = TextQuestion("pohjakoulutus_yo_ammatillinen_laajuus")
                .labelKey("pohjakoulutus.tutkinnonlaajuus")
                .requiredInline()
                .formParams(formParameters).build();
        Element ammatillinenLaajuusYksikot = Dropdown("pohjakoulutus_yo_ammatillinen_laajuusYksikko")
                .emptyOptionDefault()
                .addOptions(laajuusYksikot)
                .excelColumnLabel("laajuusyksikko.excel")
                .inline()
                .formParams(formParameters)
                .labelKey("form.yleinen.nbsp").build();

        if (formParameters.isAmmatillinenDropdown()) {
            Element ammatillinenKoodi = Dropdown("pohjakoulutus_yo_ammatillinen_nimike")
                    .emptyOptionDefault()
                    .addOptions(tutkintonimikkeet)
                    .keepFirst("", TUTKINTO_MUU)
                    .formParams(formParameters)
                    .requiredInline()
                    .labelKey("pohjakoulutus.tutkintonimike")
                    .build();
            Element muuAmmatillinenRule = createVarEqualsToValueRule(ammatillinenKoodi.getId(), TUTKINTO_MUU);

            muuAmmatillinenRule.addChild(TextQuestion("pohjakoulutus_yo_ammatillinen_nimike_muu")
                    .labelKey("muun.tutkinnon.nimi")
                    .requiredInline()
                    .formParams(formParameters).build());

            Element oppilaitos = Dropdown("pohjakoulutus_yo_ammatillinen_oppilaitos")
                    .emptyOptionDefault()
                    .keepFirst("", OPPILAITOS_TUNTEMATON)
                    .addOptions(ammattioppilaitokset)
                    .requiredInline()
                    .formParams(formParameters)
                    .labelKey("pohjakoulutus.oppilaitos")
                    .build();
            Element muuOppilaitosRule = createVarEqualsToValueRule(oppilaitos.getId(), OPPILAITOS_TUNTEMATON);
            muuOppilaitosRule.addChild(
                    TextQuestion("pohjakoulutus_yo_ammatillinen_oppilaitos_muu").requiredInline()
                            .labelKey("muun.oppilaitokseksen.nimi")
                            .formParams(formParameters).build()
            );

            ammatillinenMore.addChild(ammatillinenVuosi,
                    ammatillinenKoodi, muuAmmatillinenRule,
                    ammatillinenLaajuus, ammatillinenLaajuusYksikot, oppilaitos, muuOppilaitosRule);
            ammatillinen.addChild(ammatillinenMore);

        } else {
            Element ammatillinenNimike = TextQuestion("pohjakoulutus_yo_ammatillinen_nimike")
                    .labelKey("pohjakoulutus.tutkintonimike")
                    .requiredInline()
                    .formParams(formParameters).build();

            ammatillinenMore.addChild(ammatillinenVuosi,
                    ammatillinenNimike,
                    ammatillinenLaajuus, ammatillinenLaajuusYksikot);
            ammatillinen.addChild(ammatillinenMore);
        }
        return ammatillinen;
    }

    private static Date getFirstApplicationPeriodEndDate(ApplicationSystem applicationSystem) {
        List<ApplicationPeriod> applicationPeriods = applicationSystem.getApplicationPeriods();
        if (applicationPeriods.isEmpty()) {
            return null;
        }
        return applicationPeriods.get(0).getEnd();
    }

    private static Element buildPKTodistusSaatuViimeVuonna(final FormParameters formParameters,
                                                           final Element paattotodistusvuosiPeruskoulu) {
        // Check if the applicant has received the elementary school diploma during the previous year and
        // if the current application system type is "HAKUTYYPPI_VARSINAINEN_HAKU".
        // If so, generate an extra question. If the applicant has received diploma within a period of six months
        // from the end of application period they are eligible to receive an extra point. Generate the date ranges
        // according to these.
        Calendar cal = Calendar.getInstance();
        Date applicationPeriodEnds = getFirstApplicationPeriodEndDate(formParameters.getApplicationSystem());
        if (applicationPeriodEnds == null) {
            return Rule(Value.FALSE).build();
        }

        cal.setTime(applicationPeriodEnds);
        cal.add(Calendar.MONTH, -6);
        Variable pkPaattotodistusVuosi = new Variable(paattotodistusvuosiPeruskoulu.getId());
        Expr kysytaankoPaattotodistusAjanjakso = new All(Arrays.asList(
                new Equals(
                        pkPaattotodistusVuosi,
                        new Value(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1))
                ),
                new Equals(
                        new Value(formParameters.getApplicationSystem().getApplicationSystemType()),
                        new Value(HAKUTYYPPI_VARSINAINEN_HAKU)
                ),
                new Not(
                        new Equals(
                                new Value(formParameters.getApplicationSystem().getKohdejoukkoUri()),
                                new Value(KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA)
                        )
                )
        ));
        SimpleDateFormat fmt = new SimpleDateFormat("d.M.");
        String excludedPeriod = "1.1. - " + fmt.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String includedPeriod = fmt.format(cal.getTime()) + " - 31.12.";
        Element valitseTodistuksenSaantiAjankohta = Radio("peruskoulutodistus_saatu_puolivuotta_haun_lopusta")
                .addOptions(ImmutableList.of(
                        new Option(createI18NAsIs(excludedPeriod), "false"),
                        new Option(createI18NAsIs(includedPeriod), "true")))
                .formParams(formParameters).build();
        Element todistusSaatuViimeVuonna = Rule(kysytaankoPaattotodistusAjanjakso).build();
        todistusSaatuViimeVuonna.addChild(valitseTodistuksenSaantiAjankohta);
        return todistusSaatuViimeVuonna;
    }

    private static Element createPaattovuosiKysymys(String parentElement, String fieldName, String label, FormParameters formParameters) {
        Element vuosiRule = createRuleIfVariableIsTrue(parentElement);
        Element vuosiQuestion = new TextQuestionBuilder(fieldName)
                .labelKey(label)
                .required()
                .inline()
                .size(4)
                .maxLength(4)
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi(), 1900))
                .formParams(formParameters).build();
        vuosiRule.addChild(vuosiQuestion);
        return vuosiRule;
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


        if (formParameters.kysytaankoYlioppilastutkinto()) {
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
        if (formParameters.kysytaankoUlkomaisenTutkinnonTarkennus()) {
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

        Element paattotodistusvuosiPeruskoulu = new TextQuestionBuilder(PERUSOPETUS_PAATTOTODISTUSVUOSI)
                .labelKey("form.koulutustausta.paattotodistusvuosi")
                .required()
                .size(4)
                .maxLength(4)
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi(), 1900))
                .formParams(formParameters).build();

        Element kymppiPaatosRule = createRuleIfVariableIsTrue(ELEMENT_ID_LISAKOULUTUS_KYMPPI);
        Element kymppiPaatosQuestion = new TextQuestionBuilder(KYMPPI_PAATTOTODISTUSVUOSI)
                .labelKey("form.koulutustausta.kymppipaattotodistusvuosi")
                .required()
                .inline()
                .size(4)
                .maxLength(4)
                .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi(), 1900))
                .formParams(formParameters).build();
        kymppiPaatosRule.addChild(kymppiPaatosQuestion);

        Element kansanopistoRule = createPaattovuosiKysymys(ELEMENT_ID_LISAKOULUTUS_KANSANOPISTO, KANSANOPISTO_PAATTOTODISTUSVUOSI, "form.koulutustausta.suorituspaattotodistusvuosi", formParameters);
        Element luvaRule = createPaattovuosiKysymys(ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO_LUKIO, LUVA_PAATTOTODISTUSVUOSI, "form.koulutustausta.suorituspaattotodistusvuosi", formParameters);
        Element valmaRule = createPaattovuosiKysymys(ELEMENT_ID_LISAKOULUTUS_VALMA, VALMA_PAATTOTODISTUSVUOSI, "form.koulutustausta.suorituspaattotodistusvuosi", formParameters);
        Element telmaRule = createPaattovuosiKysymys(ELEMENT_ID_LISAKOULUTUS_TELMA, TELMA_PAATTOTODISTUSVUOSI, "form.koulutustausta.suorituspaattotodistusvuosi", formParameters);

        Element suorittanutGroup =
                TitledGroup("suorittanut.ryhma").formParams(formParameters).build()
                        .addChild(
                                Checkbox(ELEMENT_ID_LISAKOULUTUS_KYMPPI).formParams(formParameters).build(),
                                kymppiPaatosRule,
                                Checkbox(ELEMENT_ID_LISAKOULUTUS_KANSANOPISTO).formParams(formParameters).build(),
                                kansanopistoRule,
                                Checkbox(ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO_LUKIO).formParams(formParameters).build(),
                                luvaRule,
                                Checkbox(ELEMENT_ID_LISAKOULUTUS_VALMA).formParams(formParameters).build(),
                                valmaRule,
                                Checkbox(ELEMENT_ID_LISAKOULUTUS_TELMA).formParams(formParameters).build(),
                                telmaRule
                        );

        Element pkKysymyksetRule = createVarEqualsToValueRule(baseEducation.getId(),
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

        Element paattotodistusvuosiPeruskouluRule = Rule(
                        "paattotodistuvuosiPkRule",
                        new Or(
                                ExprUtil.lessThanRule(paattotodistusvuosiPeruskoulu.getId(), String.valueOf(hakukausiVuosi - 2)),
                                ExprUtil.lessThanRule(kymppiPaatosQuestion.getId(), String.valueOf(hakukausiVuosi - 2))
                        )
                ).build();

        pkKysymyksetRule.addChild(paattotodistusvuosiPeruskoulu,
                buildPKTodistusSaatuViimeVuonna(
                        formParameters,
                        paattotodistusvuosiPeruskoulu),
                suorittanutGroup,
                paattotodistusvuosiPeruskouluRule);


        if (formParameters.kysytaankoYlioppilastutkinto()) {

            Element lukioPaattotodistusVuosi = TextQuestion(LUKIO_PAATTOTODISTUS_VUOSI)
                    .maxLength(4)
                    .size(4)
                    .validator(ElementUtil.createYearValidator(formParameters.getApplicationSystem().getHakukausiVuosi() + 1, 1900))
                    .requiredInline()
                    .formParams(formParameters).build();

            Element tuoreYoTodistus = createVarEqualsToValueRule(lukioPaattotodistusVuosi.getId(), hakukausiVuosiStr);
            tuoreYoTodistus.addChild(new DropdownSelectBuilder(ELEMENT_ID_SENDING_SCHOOL)
                    .emptyOptionDefault()
                    .addOption(
                            addSpaceAtTheBeginning(formParameters.getI18nText("form.koulutustausta.lukio.valitse.oppilaitos")), "")
                    .addOptions(koodistoService.getLukioKoulukoodit())
                    .requiredInline()
                    .formParams(formParameters).build());

            Element lukioRule = createVarEqualsToValueRule(baseEducation.getId(), YLIOPPILAS);
            Element ylioppilastutkinto = new DropdownSelectBuilder(YLIOPPILASTUTKINTO)
                    .defaultOption(YLIOPPILASTUTKINTO_FI)
                    .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.fi"), YLIOPPILASTUTKINTO_FI)
                    .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.ib"), "ib")
                    .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.eb"), "eb")
                    .addOption(formParameters.getI18nText("form.koulutustausta.lukio.yotutkinto.rp"), "rp")
                    .requiredInline()
                    .formParams(formParameters).build();
            lukioRule.addChild(TitledGroup("lukio.suoritus").formParams(formParameters).build()
                    .addChild(lukioPaattotodistusVuosi,
                            ylioppilastutkinto));
            lukioRule.addChild(tuoreYoTodistus);

            Element suorittanutAmmatillisenTutkinnonLukio = Radio("ammatillinenTutkintoSuoritettu")
                    .addOptions(ImmutableList.of(
                            new Option(formParameters.getI18nText("form.yleinen.kylla"), KYLLA),
                            new Option(formParameters.getI18nText("form.yleinen.ei"), EI)))
                    .required()
                    .formParams(formParameters).build();
            lukioRule.addChild(suorittanutAmmatillisenTutkinnonLukio);

            lukioRule.addChild(
                    Dropdown(LUKIO_KIELI)
                            .emptyOptionDefault()
                            .addOptions(koodistoService.getTeachingLanguages())
                            .required()
                            .formParams(formParameters).build());
            baseEducation.addChild(lukioRule);

            if (formParameters.isAmmatillinenTutkintoEstaaHakemisen()) {
                Element suorittanutTutkinnonLukioRule =
                        createRuleIfVariableIsTrue(suorittanutAmmatillisenTutkinnonLukio.getId());
                final String failKey = "form.koulutustausta.ammatillinenSuoritettu.lukio.huom";
                Element warningLukio =
                        Warning(ElementUtil.randomId()).failValidation(failKey).labelKey(failKey).formParams(formParameters).build();
                suorittanutTutkinnonLukioRule.addChild(warningLukio);
                suorittanutAmmatillisenTutkinnonLukio.addChild(suorittanutTutkinnonLukioRule);
            }
        }

        baseEducation.addChild(pkKysymyksetRule);

        pkKysymyksetRule.addChild(Dropdown(PERUSOPETUS_KIELI)
                .emptyOptionDefault()
                .addOptions(koodistoService.getTeachingLanguages())
                .required()
                .formParams(formParameters).build());

        if (formParameters.isKoulutustaustaMuuKoulutus()) {
            baseEducation.addChild(TextArea("muukoulutus").cols(TEXT_AREA_COLS).maxLength(500).formParams(formParameters).build());
        }

        return baseEducation;
    }

    private static Element buildSuoritusmaa(FormParameters formParameters, List<Option> maat, String id, String postfix) {
        return Dropdown(id + postfix)
                .emptyOptionDefault()
                .addOptions(maat)
                .labelKey(id)
                .requiredInline()
                .formParams(formParameters).build();
    }

    private static Element buildMuuSuoritusmaa(FormParameters formParameters, String inputId, String postfix) {
        Element educationCountryOther = TextQuestion(inputId + "_muu" + postfix)
                .labelKey(inputId)
                .requiredInline()
                .formParams(formParameters).build();
        Element educationCountryOtherRule = createVarEqualsToValueRule(inputId + postfix, EDUCATION_COUNTRY_OTHER);
        educationCountryOtherRule.addChild(educationCountryOther);
        return educationCountryOtherRule;
    }
}
