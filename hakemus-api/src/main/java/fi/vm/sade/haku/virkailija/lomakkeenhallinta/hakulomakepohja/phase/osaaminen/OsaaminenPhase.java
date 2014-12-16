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
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.And;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Or;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Regexp;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ExprValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RegexFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder.Dropdown;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextAreaBuilder.TextArea;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;

public class OsaaminenPhase {

    public static Element create(final FormParameters formParameters) {
        Element osaaminen = Phase("osaaminen").setEditAllowedByRoles("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO").formParams(formParameters).build();
        if (!formParameters.isHigherEd()) {
            if (formParameters.isKevaanYhteishaku() || formParameters.isPerusopetuksenJalkeinenValmentava() || formParameters.isKevaanLisahaku()) {
                osaaminen.addChild(ArvosanatTheme.createArvosanatThemeKevat(formParameters));
            } else {
                osaaminen.addChild(ArvosanatTheme.createArvosanatTheme(formParameters));
            }
            if (!formParameters.isPerusopetuksenJalkeinenValmentava()) {
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

            Expr haettuAMKHon = ExprUtil.reduceToOr(exprs);

            ElementBuilder kysytaankoLukionKeskiarvo = Rule(new And(haettuAMKHon, pohjakoulutusLukio));
            List<Option> asteikkolista = koodistoService.getAmmatillisenTutkinnonArvosteluasteikko();

            RegexFieldValidator validator = new RegexFieldValidator(ElementUtil.createI18NText("validator.keskiarvo.desimaaliluku", formParameters), "^$|\\d+\\,?\\d{1,2}");
            osaaminenTheme.addChild(
                    kysytaankoLukionKeskiarvo.addChild(
                            TextQuestion("lukion-paattotodistuksen-keskiarvo")
                                    .inline()
                                    .required()
                                    .maxLength(5)
                                    .size(5)
                                    .validator(validator)
                                    .formParams(formParameters)
                                    .build())
                            .build()
            );
            buildKeskiarvotAmmatillinen(formParameters, haettuAMKHon, asteikkolista, osaaminenTheme);
            ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionConfigurator();
            osaaminenTheme.addChild(configurator.findAndConfigure(osaaminenTheme.getId()));
        }
        return osaaminen;
    }


    private static void buildKeskiarvotAmmatillinen(FormParameters formParameters, Expr haettuAMKHon, List<Option> asteikkolista, Element parent) {
        Expr pohjakoulutusLukioAmmatillinen = ExprUtil.isAnswerTrue("pohjakoulutus_yo_ammatillinen");

        Element kysytaankoLukioAmmatillinen = Rule(new And(haettuAMKHon, pohjakoulutusLukioAmmatillinen)).build();
        buildKeskiarvoJaAsteikko(asteikkolista, kysytaankoLukioAmmatillinen, formParameters, "");
        parent.addChild(kysytaankoLukioAmmatillinen);

        for (int i = 1; i <= 5; i++) {
            String postfix = i == 1 ? "" : String.valueOf(i);
            Expr pohjakoulutusAmmatillinen = new Regexp("pohjakoulutus_am_vuosi" + postfix, "^\\d+$");
            Element kysytaankoAmmatillinen = Rule(new And(haettuAMKHon, pohjakoulutusAmmatillinen)).build();
            buildKeskiarvoJaAsteikko(asteikkolista, kysytaankoAmmatillinen, formParameters, postfix);
            parent.addChild(kysytaankoAmmatillinen);
        }
    }

    private static void buildKeskiarvoJaAsteikko(List<Option> asteikkolista, Element parent, FormParameters formParameters, String postfix) {
        Validator validator = new ExprValidator(new And(new Regexp("keskiarvo" + postfix, "^$|\\d+\\,?\\d{1,2}"),
                new Or(new Or(new And(new Regexp("arvosanaasteikko" + postfix, "^1-3$"), new Regexp("keskiarvo" + postfix, "^([1-2]\\,[0-9][0-9])|(3\\,00)$")),
                new And(new Regexp("arvosanaasteikko" + postfix, "^1-5$"), new Regexp("keskiarvo" + postfix, "^([1-4]\\,[0-9][0-9])|(5\\,00)$"))),
                        new And(new Regexp("arvosanaasteikko" + postfix, "^4-10$"), new Regexp("keskiarvo" + postfix, "^([4-9]\\,[0-9][0-9])|(10\\,00)$")))),
                createI18NText("validator.keskiarvo", formParameters));

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
                        .addOptions(asteikkolista)
                        .inline()
                        .required()
                        .formParams(formParameters)
                        .labelKey("arvosanaasteikko")
                        .build())
                .addChild(TextArea("keskiarvo-tutkinto" + postfix)
                        .inline()
                        .formParams(formParameters)
                        .labelKey("keskiarvo-tutkinto")
                        .build());
    }
}
