/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.oppija.ui.selenium;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.SocialSecurityNumberBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;


public class SocialSecurityNumberIT extends AbstractSeleniumBase {

    private ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void init() throws IOException {

        Radio sukupuoli = (Radio) RadioBuilder.Radio("Sukupuoli")
                .addOptions(ImmutableList.of(new Option(createI18NAsIs("Mies"), "1"), new Option(createI18NAsIs("Nainen"), "2")))
                .requiredInline()
                .build();

        Element socialSecurityNumber = new SocialSecurityNumberBuilder("Henkilotunnus").setSexI18nText(sukupuoli.getI18nText()).setMaleOption(sukupuoli.getOptions().get(0)).setFemaleOption(sukupuoli.getOptions().get(1)).setSexId(sukupuoli.getId()).build();

        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(socialSecurityNumber);
        this.applicationSystemHelper = updateApplicationSystem(applicationSystem);
        seleniumContainer.getDriver().get(getBaseUrl() + applicationSystemHelper.getStartUrl());
    }

    @Test
    public void testInputMale() {
        type("Henkilotunnus", "010101A1119", true);
        elementsPresent("//*[text()='Mies']");
    }

    @Test
    public void testInputFemale() {
        type("Henkilotunnus", "010101A112A", true);
        elementsPresent("//*[text()='Nainen']");
    }
}
