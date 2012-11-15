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

package fi.vm.sade.oppija.haku.selenium;

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.oppija.haku.domain.elements.questions.Radio;
import fi.vm.sade.oppija.haku.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.haku.domain.rules.SelectingSubmitRule;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * @author jukka
 * @version 10/3/123:25 PM}
 * @since 1.1
 */
public class SocialSecurityNumberTest extends AbstractSeleniumBase {
    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {

        TextQuestion henkilötunnus = new TextQuestion("Henkilotunnus", "Henkilötunnus");
        henkilötunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilötunnus.addAttribute("title", "ppkkvv*****");
        henkilötunnus.addAttribute("required", "required");
        henkilötunnus.addAttribute("pattern", "[0-9]{6}.[0-9]{4}");
        henkilötunnus.addAttribute("size", "11");
        henkilötunnus.addAttribute("maxlength", "11");
        henkilötunnus.setHelp("Jos sinulla ei ole suomalaista henkilötunnusta, täytä tähän syntymäaikasi");
        henkilötunnus.setInline(true);

        Radio sukupuoli = new Radio("Sukupuoli", "Sukupuoli");
        sukupuoli.addOption("mies", "Mies", "Mies");
        sukupuoli.addOption("nainen", "Nainen", "Nainen");
        sukupuoli.addAttribute("required", "required");
        sukupuoli.setInline(true);

        SocialSecurityNumber socialSecurityNumber = new SocialSecurityNumber("ssn_question", "Henkilötunnus");
        socialSecurityNumber.setSsn(henkilötunnus);
        socialSecurityNumber.setSex(sukupuoli);
        socialSecurityNumber.setMaleId(sukupuoli.getOptions().get(0).getId());
        socialSecurityNumber.setFemaleId(sukupuoli.getOptions().get(1).getId());

        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(socialSecurityNumber);
        this.formModelHelper = initModel(formModel);
    }


    @Test
    public void testInputMale() {
        final String startUrl = formModelHelper.getStartUrl();
        seleniumHelper.getDriver().get(getBaseUrl() + "/" + startUrl);
        seleniumHelper.getSelenium().type("Henkilotunnus", "010101-111X");

        String maleChecked = seleniumHelper.getDriver().findElement(By.id("Sukupuoli_mies")).getAttribute("checked");
        String femaleChecked = seleniumHelper.getDriver().findElement(By.id("Sukupuoli_nainen")).getAttribute("checked");

        assertEquals("true", maleChecked);
        assertNull(femaleChecked);
    }

    @Test
    public void testInputFemale() {
        final String startUrl = formModelHelper.getStartUrl();
        seleniumHelper.getDriver().get(getBaseUrl() + "/" + startUrl);
        seleniumHelper.getSelenium().type("Henkilotunnus", "010101-112X");

        String maleChecked = seleniumHelper.getDriver().findElement(By.id("Sukupuoli_mies")).getAttribute("checked");
        String femaleChecked = seleniumHelper.getDriver().findElement(By.id("Sukupuoli_nainen")).getAttribute("checked");

        assertEquals("true", femaleChecked);
        assertNull(maleChecked);


    }
}
