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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.oppija.hakemus.service.Role.*;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder.Dropdown;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextAreaBuilder.TextArea;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;

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

            Expr pohjakoulutusLukio = ExprUtil.isAnswerTrue("pohjakoulutus_yo");

            KoodistoService koodistoService = formParameters.getKoodistoService();
            String[] amkkoulutuksetArr = HakutoiveetPhase.getAmkKoulutusIds(koodistoService);
            List<Expr> exprs = new ArrayList<Expr>();
            for (String preferenceId : HakutoiveetPhase.getPreferenceIds(formParameters)) {
                exprs.add(ExprUtil.atLeastOneValueEqualsToVariable(preferenceId + "-Koulutus-id-educationcode", amkkoulutuksetArr));
            }

            Expr haettuAMKHon = ExprUtil.any(exprs);

            ElementBuilder kysytaankoLukionKeskiarvo = Rule(new And(haettuAMKHon, pohjakoulutusLukio));
            List<Option> asteikkolista = koodistoService.getAmmatillisenTutkinnonArvosteluasteikko();

            ElementBuilder lukioKeskiarvo = TextQuestion("lukion-paattotodistuksen-keskiarvo")
                    .inline()
                    .required()
                    .maxLength(5)
                    .size(5)
                    .validator(new RegexFieldValidator("validator.keskiarvo.desimaaliluku", "^$|\\d+\\,?\\d{1,2}"))
                    .formParams(formParameters);
            if (formParameters.useCustomGradeAverageComponent()) {
                GradeAverage gradeAverage = new GradeAverage("keskiarvo_lukio", "", null, null, formParameters.getI18nText("lukio"),
                        null, null, null);
                Element eiOleKeskiarvoa = Checkbox("ei_ole_keskiarvoa_lukio")
                        .labelKey("eiolekeskiarvoa")
                        .formParams(formParameters).build();
                Element eiKysyta = Rule(new Not(new Equals(new Variable(eiOleKeskiarvoa.getId()), Value.TRUE)))
                        .formParams(formParameters).build();
                eiKysyta.addChild(lukioKeskiarvo.build());
                gradeAverage.addChild(eiOleKeskiarvoa, eiKysyta);
                kysytaankoLukionKeskiarvo.addChild(gradeAverage);
            } else {
                kysytaankoLukionKeskiarvo.addChild(lukioKeskiarvo.build());
            }

            List<Option> ammattitutkintonimikkeet = koodistoService.getAmmattitutkinnot();
            List<Option> oppilaitokset = koodistoService.getAmmattioppilaitosKoulukoodit();

            osaaminenTheme.addChild(kysytaankoLukionKeskiarvo.build());
            buildKeskiarvotAmmatillinen(formParameters, ammattitutkintonimikkeet, oppilaitokset,
                    haettuAMKHon, asteikkolista, osaaminenTheme);
            ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionConfigurator();
            osaaminenTheme.addChild(configurator.findAndConfigure(osaaminenTheme.getId()));
        }
        return osaaminen;
    }

    private static void buildKeskiarvotAmmatillinen(FormParameters formParameters, List<Option> ammattitutkintonimikkeet,
                                                    List<Option> oppilaitokset, Expr haettuAMKHon, List<Option> asteikkolista, Element parent) {
        Expr pohjakoulutusLukioAmmatillinen = ExprUtil.isAnswerTrue("pohjakoulutus_yo_ammatillinen");

        String yoAmmatillinenPostfix = formParameters.getFormConfiguration()
                .getFeatureFlag(FormConfiguration.FeatureFlag.erotteleAmmatillinenJaYoAmmatillinenKeskiarvo) ? "_yo_ammatillinen" : "";

        Element kysytaankoLukioAmmatillinen = Rule(new And(haettuAMKHon, pohjakoulutusLukioAmmatillinen)).build();
        if (formParameters.useCustomGradeAverageComponent()) {
            buildCustomGradeAverage(kysytaankoLukioAmmatillinen, "pohjakoulutus_yo_ammatillinen_nimike", ammattitutkintonimikkeet,
                    "pohjakoulutus_yo_ammatillinen_muu", "pohjakoulutus_yo_ammatillinen_oppilaitos", "pohjakoulutus_yo_ammatillinen_oppilaitos_muu", oppilaitokset,
                    asteikkolista, yoAmmatillinenPostfix, formParameters);
        } else {
            buildKeskiarvoJaAsteikko(asteikkolista, kysytaankoLukioAmmatillinen, formParameters, yoAmmatillinenPostfix, true);
        }
        parent.addChild(kysytaankoLukioAmmatillinen);

        for (int i = 1; i <= 5; i++) {
            String postfix = i == 1 ? "" : String.valueOf(i);
            Expr pohjakoulutusAmmatillinen = new Regexp("pohjakoulutus_am_vuosi" + postfix, "^\\d+$");
            Element kysytaankoAmmatillinen = Rule(new And(haettuAMKHon, pohjakoulutusAmmatillinen)).build();
            if (formParameters.useCustomGradeAverageComponent()) {
                buildCustomGradeAverage(kysytaankoAmmatillinen, "pohjakoulutus_am_nimike" + postfix, ammattitutkintonimikkeet,
                        "pohjakoulutus_am_nimike_muu" + postfix, "pohjakoulutus_am_oppilaitos" + postfix, "pohjakoulutus_am_oppilaitos_muu" + postfix, oppilaitokset,
                        asteikkolista, postfix, formParameters);
            } else {
                buildKeskiarvoJaAsteikko(asteikkolista, kysytaankoAmmatillinen, formParameters, postfix, true);
            }
            parent.addChild(kysytaankoAmmatillinen);
        }
    }

    private static void buildCustomGradeAverage(Element kysytaankoKeskiarvo, String relatedNimikeId, List<Option> ammattitutkintonimikkeet,
                                                String relatedMuuNimike, String relatedOppilaitosId, String relatedMuuOppilaitos,
                                                List<Option> oppilaitokset, List<Option> asteikkolista, String postfix, FormParameters formParameters) {
        GradeAverage gradeAverage = new GradeAverage("keskiarvo_"+relatedNimikeId, relatedNimikeId, ammattitutkintonimikkeet, relatedMuuNimike,
                ElementUtil.createI18NAsIs(""), relatedOppilaitosId, relatedMuuOppilaitos, oppilaitokset);
        Element eiOleKeskiarvoa = Checkbox("ei_ole_keskiarvoa_"+relatedNimikeId)
                .labelKey("eiolekeskiarvoa")
                .formParams(formParameters).build();
        Element eiKysyta = Rule(new Not(new Equals(new Variable(eiOleKeskiarvoa.getId()), Value.TRUE)))
                .formParams(formParameters)
                .build();
        buildKeskiarvoJaAsteikko(asteikkolista, eiKysyta, formParameters, postfix, false);
        gradeAverage.addChild(eiOleKeskiarvoa, eiKysyta);
        kysytaankoKeskiarvo.addChild(gradeAverage);
    }

    private static void buildKeskiarvoJaAsteikko(List<Option> asteikkolista, Element parent,
                                                 FormParameters formParameters, String postfix, boolean kysyTutkinto) {
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

        parent.addChild(TextQuestion("keskiarvo" + postfix)
                    .inline()
                    .required()
                    .maxLength(5)
                    .size(5)
                    .validator(validator)
                    .formParams(formParameters)
                    .labelKey("keskiarvo")
                    .build())
                .addChild(Dropdown("arvosanaasteikko" + postfix)
                    .emptyOptionDefault()
                    .addOptions(asteikkolista)
                    .inline()
                    .required()
                    .formParams(formParameters)
                    .labelKey("arvosanaasteikko")
                    .build());
        if (kysyTutkinto) {
            parent.addChild(TextArea("keskiarvo-tutkinto" + postfix)
                    .inline()
                    .formParams(formParameters)
                    .labelKey("keskiarvo-tutkinto")
                    .build());
        }

    }

}
