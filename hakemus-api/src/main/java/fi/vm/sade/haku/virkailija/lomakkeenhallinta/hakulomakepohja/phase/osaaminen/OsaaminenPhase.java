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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ElementBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.GradeAverage;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ExprValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RegexFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.List;

import static fi.vm.sade.haku.oppija.hakemus.service.Role.*;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder.Dropdown;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextAreaBuilder.TextArea;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class OsaaminenPhase {

    public static Element create(final FormParameters formParameters) {
        Element osaaminen = Phase("osaaminen").setEditAllowedByRoles(ROLE_RU, ROLE_CRUD, ROLE_HETUTTOMIENKASITTELY, ROLE_KKVIRKAILIJA).formParams(formParameters).build();
        if (!formParameters.isHigherEd()) {
            if (formParameters.isKevaanYhteishaku() || formParameters.isPerusopetuksenJalkeinenValmentava() || formParameters.isKevaanLisahaku()) {
                osaaminen.addChild(ArvosanatTheme.createArvosanatThemeKevat(formParameters));
            } else {
                osaaminen.addChild(ArvosanatTheme.createArvosanatTheme(formParameters));
            }
            if (formParameters.kysytaankoKielitaitokysymykset()) {
                osaaminen.addChild(KielitaitokysymyksetTheme.createKielitaitokysymyksetTheme(formParameters));
            }
        } else {
            Element osaaminenTheme = Theme("osaaminenteema").configurable().formParams(formParameters).build();
            osaaminen.addChild(osaaminenTheme);
            if (formParameters.isOnlyThemeGenerationForFormEditor())
                return osaaminen;

            List<Option> ammattitutkinnot = formParameters.getKoodistoService().getAmmattitutkinnot();
            List<Option> ammattioppilaitokset = formParameters.getKoodistoService().getAmmattioppilaitosKoulukoodit();
            List<Option> ammatillisenTutkinnonArvosteluasteikko = formParameters.getKoodistoService().getAmmatillisenTutkinnonArvosteluasteikko();
            Expr haettuAMKHon = haettuAMKHon(
                    HakutoiveetPhase.getAmkKoulutusIds(formParameters.getKoodistoService()),
                    HakutoiveetPhase.getPreferenceIds(formParameters));

            if (formParameters.gradeAverageLukio()) {
                osaaminenTheme.addChild(kysytaanLukionKeskiarvo(haettuAMKHon).addChild(buildLukionKeskiarvo(formParameters)));
            }
            if (formParameters.isKansainvalinenYoAmkKysymys()) {
                osaaminenTheme.addChild(kysytaanYoArvosanat(haettuAMKHon).addChild(buildYoArvosanat(formParameters)));
            }
            if (formParameters.gradeAverageYoAmmatillinen()) {
                osaaminenTheme.addChild(kysytaanYoAmmatillinenKeskiarvo(haettuAMKHon)
                        .addChild(buildKeskiarvoYoAmmatillinen(formParameters, ammattitutkinnot, ammattioppilaitokset, ammatillisenTutkinnonArvosteluasteikko)));
            }
            if (formParameters.gradeAverageAmmatillinen()) {
                for (int i = 1; i <= 5; i++) {
                    String postfix = i == 1 ? "" : String.valueOf(i);
                    osaaminenTheme.addChild(kysytaanAmmatillinenKeskiarvo(haettuAMKHon, postfix)
                            .addChild(buildKeskiarvoAmmatillinen(formParameters, postfix, ammattitutkinnot, ammattioppilaitokset, ammatillisenTutkinnonArvosteluasteikko)));
                }
            }
            osaaminenTheme.addChild(formParameters.getThemeQuestionConfigurator().findAndConfigure(osaaminenTheme.getId()));
        }
        return osaaminen;
    }

    private static Element kysytaanLukionKeskiarvo(Expr haettuAMKHon) {
        return Rule(new And(haettuAMKHon, ExprUtil.isAnswerTrue("pohjakoulutus_yo"))).build();
    }

    private static Element kysytaanYoAmmatillinenKeskiarvo(Expr haettuAMKHon) {
        return Rule(new And(haettuAMKHon, ExprUtil.isAnswerTrue("pohjakoulutus_yo_ammatillinen"))).build();
    }

    private static Element kysytaanAmmatillinenKeskiarvo(Expr haettuAMKHon, String postfix) {
        return Rule(new And(haettuAMKHon, new Regexp("pohjakoulutus_am_vuosi" + postfix, "^\\d+$"))).build();
    }

    private static Element kysytaanYoArvosanat(Expr haettuAMKHon) {
        Expr pohjakoulutusYoKansainvalinen = new Or(ExprUtil.isAnswerTrue("pohjakoulutus_yo_kansainvalinen_suomessa"), ExprUtil.isAnswerTrue("pohjakoulutus_yo_ulkomainen"));
        return Rule(new And(haettuAMKHon, pohjakoulutusYoKansainvalinen)).build();
    }

    private static Expr haettuAMKHon(final String[] amkKoulutukset, List<String> preferenceIds) {
        return ExprUtil.any(Lists.transform(preferenceIds, new Function<String, Expr>() {
            public Expr apply(String preferenceId) {
                return ExprUtil.atLeastOneValueEqualsToVariable(preferenceId + "-Koulutus-id-educationcode", amkKoulutukset);
            }
        }));
    }

    private static Element buildLukionKeskiarvo(FormParameters formParameters) {
        ElementBuilder lukioKeskiarvo = TextQuestion("lukion-paattotodistuksen-keskiarvo")
                .inline()
                .required()
                .maxLength(5)
                .size(5)
                .validator(new RegexFieldValidator("validator.keskiarvo.desimaaliluku", "^$|\\d+\\,?\\d{1,2}"))
                .formParams(formParameters);
        if (formParameters.useGradeAverage()) {
            GradeAverage gradeAverage = new GradeAverage("keskiarvo_lukio", "", null, null, formParameters.getI18nText("lukio"),
                    null, null, null);
            if (formParameters.useOptionalGradeAverageLukio()) {
                Element eiOleKeskiarvoa = Checkbox("ei_ole_keskiarvoa_lukio")
                        .labelKey("eiolekeskiarvoa")
                        .formParams(formParameters).build();
                Element eiKysyta = Rule(new Not(new Equals(new Variable(eiOleKeskiarvoa.getId()), Value.TRUE)))
                        .formParams(formParameters).build();
                eiKysyta.addChild(lukioKeskiarvo.build());
                gradeAverage.addChild(eiOleKeskiarvoa, eiKysyta);
            } else {
                gradeAverage.addChild(lukioKeskiarvo.build());
            }
            return gradeAverage;
        } else {
            return lukioKeskiarvo.build();
        }
    }

    private static Element buildYoAsteikkoQuestion(String elementId, FormParameters formParameters) {
        return Dropdown(elementId)
                .useGivenOrder()
                .emptyOption()
                .addOptions(formParameters.getKoodistoService().getYoArvosanaasteikko())
                .requiredInline()
                .formParams(formParameters)
                .build();
    }

    private static Element buildYoArvosanat(FormParameters formParameters) {
        return TitledGroup("osaaminen-kansainvalinenyo-arvosanat").formParams(formParameters).build()
                .addChild(buildYoAsteikkoQuestion(ELEMENT_ID_OSAAMINEN_YOARVOSANAT_PARAS_KIELI, formParameters),
                        buildYoAsteikkoQuestion(ELEMENT_ID_OSAAMINEN_YOARVOSANAT_AIDINKIELI, formParameters),
                        buildYoAsteikkoQuestion(ELEMENT_ID_OSAAMINEN_YOARVOSANAT_MATEMATIIKKA, formParameters),
                        buildYoAsteikkoQuestion(ELEMENT_ID_OSAAMINEN_YOARVOSANAT_REAALI, formParameters));
    }

    private static Element[] buildKeskiarvoYoAmmatillinen(FormParameters formParameters,
                                                          List<Option> ammattitutkintonimikkeet,
                                                          List<Option> oppilaitokset,
                                                          List<Option> asteikkolista) {
        String yoAmmatillinenPostfix = formParameters.getFormConfiguration()
                .getFeatureFlag(FormConfiguration.FeatureFlag.erotteleAmmatillinenJaYoAmmatillinenKeskiarvo) ? "_yo_ammatillinen" : "";
        if (formParameters.useGradeAverage()) {
            return buildCustomGradeAverage("pohjakoulutus_yo_ammatillinen_nimike", ammattitutkintonimikkeet,
                    "pohjakoulutus_yo_ammatillinen_nimike_muu", "pohjakoulutus_yo_ammatillinen_oppilaitos", "pohjakoulutus_yo_ammatillinen_oppilaitos_muu",
                    oppilaitokset, asteikkolista, yoAmmatillinenPostfix, formParameters);
        } else {
            return buildKeskiarvoTutkinnolla(formParameters, asteikkolista, yoAmmatillinenPostfix);
        }
    }

    private static Element[] buildKeskiarvoAmmatillinen(FormParameters formParameters,
                                                        String postfix,
                                                        List<Option> ammattitutkintonimikkeet,
                                                        List<Option> oppilaitokset,
                                                        List<Option> asteikkolista) {
        if (formParameters.useGradeAverage()) {
            return buildCustomGradeAverage("pohjakoulutus_am_nimike" + postfix, ammattitutkintonimikkeet,
                    "pohjakoulutus_am_nimike_muu" + postfix, "pohjakoulutus_am_oppilaitos" + postfix, "pohjakoulutus_am_oppilaitos_muu" + postfix,
                    oppilaitokset, asteikkolista, postfix, formParameters);
        } else {
            return buildKeskiarvoTutkinnolla(formParameters, asteikkolista, postfix);
        }
    }

    private static Element[] buildCustomGradeAverage(String relatedNimikeId, List<Option> ammattitutkintonimikkeet,
                                                   String relatedMuuNimike, String relatedOppilaitosId,
                                                   String relatedMuuOppilaitos, List<Option> oppilaitokset,
                                                   List<Option> asteikkolista, String postfix,
                                                   FormParameters formParameters) {
        GradeAverage gradeAverage = new GradeAverage("keskiarvo_" + relatedNimikeId, relatedNimikeId, ammattitutkintonimikkeet, relatedMuuNimike,
                ElementUtil.createI18NAsIs(""), relatedOppilaitosId, relatedMuuOppilaitos, oppilaitokset);
        if (formParameters.useOptionalGradeAverageAmmatillinen()) {
            Element eiOleKeskiarvoa = Checkbox("ei_ole_keskiarvoa_" + relatedNimikeId)
                    .labelKey("eiolekeskiarvoa")
                    .formParams(formParameters).build();
            Element eiKysyta = Rule(new Not(new Equals(new Variable(eiOleKeskiarvoa.getId()), Value.TRUE)))
                    .formParams(formParameters)
                    .build();
            eiKysyta.addChild(buildKeskiarvo(formParameters, postfix));
            eiKysyta.addChild(buildArvosanaasteikko(formParameters, asteikkolista, postfix));
            gradeAverage.addChild(eiOleKeskiarvoa, eiKysyta);
        } else {
            gradeAverage.addChild(buildKeskiarvo(formParameters, postfix));
            gradeAverage.addChild(buildArvosanaasteikko(formParameters, asteikkolista, postfix));
        }
        return new Element[] {gradeAverage};
    }

    private static Element[] buildKeskiarvoTutkinnolla(FormParameters formParameters, List<Option> asteikkolista, String yoAmmatillinenPostfix) {
        return new Element[]{
                buildKeskiarvo(formParameters, yoAmmatillinenPostfix),
                buildArvosanaasteikko(formParameters, asteikkolista, yoAmmatillinenPostfix),
                buildKeskiarvonTutkinto(formParameters, yoAmmatillinenPostfix)};
    }

    private static Element buildKeskiarvo(FormParameters formParameters, String postfix) {
        Validator validator = new ExprValidator(
                new And(
                        new Regexp("keskiarvo" + postfix, "^$|\\d+\\,?\\d{1,2}"),
                        new Or(
                                new Or(
                                        new And(
                                                new Regexp("arvosanaasteikko" + postfix, "^1-3$"),
                                                new Regexp("keskiarvo" + postfix, "^([1-2]\\,[0-9][0-9])|(3\\,00)$")
                                        ),
                                        new And(
                                                new Regexp("arvosanaasteikko" + postfix, "^1-5$"),
                                                new Regexp("keskiarvo" + postfix, "^([1-4]\\,[0-9][0-9])|(5\\,00)$")
                                        )
                                ),
                                new And(
                                        new Regexp("arvosanaasteikko" + postfix, "^4-10$"),
                                        new Regexp("keskiarvo" + postfix, "^([4-9]\\,[0-9][0-9])|(10\\,00)$")
                                )
                        )
                ),
                formParameters.getI18nText("validator.keskiarvo"));
        return TextQuestion("keskiarvo" + postfix)
                .inline()
                .required()
                .maxLength(5)
                .size(5)
                .validator(validator)
                .formParams(formParameters)
                .labelKey("keskiarvo")
                .build();
    }

    private static Element buildArvosanaasteikko(FormParameters formParameters, List<Option> asteikkolista, String postfix) {
        return Dropdown("arvosanaasteikko" + postfix)
                .emptyOptionDefault()
                .addOptions(asteikkolista)
                .inline()
                .required()
                .formParams(formParameters)
                .labelKey("arvosanaasteikko")
                .build();
    }

    private static Element buildKeskiarvonTutkinto(FormParameters formParameters, String postfix) {
        return TextArea("keskiarvo-tutkinto" + postfix)
                .inline()
                .formParams(formParameters)
                .labelKey("keskiarvo-tutkinto")
                .build();
    }

}
