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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder.Dropdown;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;

public class OsaaminenPhase {

    public static Element create(final FormParameters formParameters) {
        Element osaaminen = Phase("osaaminen").setEditAllowedByRoles("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO").formParams(formParameters).build();
        if (!formParameters.isHigherEd()) {
            if (formParameters.isKevaanYhteishaku() || formParameters.isPervako() || formParameters.isKevaanLisahaku()) {
                osaaminen.addChild(ArvosanatTheme.createArvosanatThemeKevat(formParameters));
            } else {
                osaaminen.addChild(ArvosanatTheme.createArvosanatTheme(formParameters));
            }
            if (!formParameters.isPervako()) {
                osaaminen.addChild(KielitaitokysymyksetTheme.createKielitaitokysymyksetTheme(formParameters));
            }
        } else {
            KoodistoService koodistoService = formParameters.getKoodistoService();
            String[] amkkoulutuksetArr = HakutoiveetPhase.getAmkKoulutusIds(koodistoService);
            List<Expr> exprs = new ArrayList<Expr>();
            for (String preferenceId : HakutoiveetPhase.getPreferenceIds(formParameters)) {
                exprs.add(ExprUtil.atLeastOneValueEqualsToVariable(preferenceId + "-Koulutus-id-educationcode", amkkoulutuksetArr));
            }

            Expr haettuAMKHon = ExprUtil.reduceToOr(exprs);
            Expr pohjakoulutusAmmatillinen = new Or(ExprUtil.isAnswerTrue("pohjakoulutus_am"), ExprUtil.isAnswerTrue("pohjakoulutus_yo_ammatillinen"));

            ElementBuilder kysytaankoKeskiarvoJaAsteikko = Rule(new And(haettuAMKHon, pohjakoulutusAmmatillinen));
            List<Option> asteikkolista = koodistoService.getLaajuusYksikot();
            osaaminen.addChild(
                    Theme("osaaminenteema")
                            .formParams(formParameters)
                            .addChild(
                                    kysytaankoKeskiarvoJaAsteikko
                                            .addChild(TextQuestion("keskiarvo")
                                                    .inline()
                                                    .required()
                                                    .formParams(formParameters).build())
                                            .addChild(Dropdown("arvosanaasteikko")
                                                    .addOptions(asteikkolista)
                                                    .inline()
                                                    .required()
                                                    .formParams(formParameters).build()
                                            ))
                            .build());
        }
        return osaaminen;

    }
}
