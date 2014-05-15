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

import fi.vm.sade.haku.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;


public class SocialSecurityNumberIT extends AbstractSeleniumBase {

    private ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void init() throws IOException {

        TextQuestion henkilötunnus = (TextQuestion) new TextQuestionBuilder("Henkilotunnus").i18nText(createI18NAsIs("Henkilotunnus")).build();
        henkilötunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilötunnus.addAttribute("title", "ppkkvv*****");
        FormParameters formParameters = new FormParameters(new ApplicationSystemBuilder().addHakukausiUri(OppijaConstants.HAKUKAUSI_SYKSY).addApplicationSystemType(OppijaConstants.VARSINAINEN_HAKU).get(), null);
        addRequiredValidator(henkilötunnus, formParameters);
        henkilötunnus.setValidator(createRegexValidator(henkilötunnus.getId(), "[0-9]{6}.[0-9]{4}", formParameters));
        henkilötunnus.addAttribute("size", "11");
        henkilötunnus.addAttribute("maxlength", "11");
        henkilötunnus.setHelp(createI18NAsIs("Jos sinulla ei ole suomalaista henkilötunnusta, täytä tähän syntymäaikasi"));
        henkilötunnus.setInline(true);

        Radio sukupuoli = new Radio("Sukupuoli", createI18NAsIs("Sukupuoli"));
        sukupuoli.addOption(createI18NText("form.henkilotiedot.sukupuoli.mies", formParameters), "1");
        sukupuoli.addOption(createI18NText("form.henkilotiedot.sukupuoli.nainen", formParameters), "2");
        addRequiredValidator(sukupuoli, formParameters);
        sukupuoli.setInline(true);

        SocialSecurityNumber socialSecurityNumber = new SocialSecurityNumber("ssn_question", createI18NAsIs("Henkilötunnus"),
                sukupuoli.getI18nText(), sukupuoli.getOptions().get(0), sukupuoli.getOptions().get(1), sukupuoli.getId(), henkilötunnus);

        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(socialSecurityNumber);
        this.applicationSystemHelper = updateApplicationSystem(applicationSystem);
        seleniumContainer.getDriver().get(getBaseUrl() + applicationSystemHelper.getStartUrl());
    }


    @Test
    public void testInputMale() {
        seleniumContainer.getSelenium().type("Henkilotunnus", "010101-111X");
        seleniumContainer.getDriver().findElement(By.xpath("//*[text()='Mies']"));
    }

    @Test
    public void testInputFemale() {
        seleniumContainer.getSelenium().type("Henkilotunnus", "010101-112X");
        seleniumContainer.getDriver().findElement(By.xpath("//*[text()='Nainen']"));


    }
}
