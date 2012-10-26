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

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.elements.questions.Radio;
import fi.vm.sade.oppija.haku.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.haku.domain.rules.SelectingSubmitRule;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static org.junit.Assert.assertTrue;


/**
 * @author jukka
 * @version 10/3/123:25 PM}
 * @since 1.1
 */
public class SelectingSubmitRuleIT extends AbstractSeleniumBase {
    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final TextQuestion textQuestion = new TextQuestion("hetu", "Henkilötunnus");
        textQuestion.addAttribute("onchange", "submit()");

        final Radio child = new Radio("sukupuoli", "sukupuoli");
        child.addOption("mies", "mies", "Mies");
        child.addOption("nainen", "nainen", "Nainen");
        SelectingSubmitRule selectingSubmitRule = new SelectingSubmitRule(textQuestion.getId(), child.getId());
        selectingSubmitRule.addBinding(textQuestion, child, "\\d{6}\\S\\d{2}[13579]\\w", child.getOptions().get(0));
        selectingSubmitRule.addBinding(textQuestion, child, "\\d{6}\\S\\d{2}[24680]\\w", child.getOptions().get(1));


        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(selectingSubmitRule);
        this.formModelHelper = initModel(formModel);
    }


    @Test
    public void testInputMale() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        seleniumHelper.getDriver().get(getBaseUrl() + "/" + startUrl);
        seleniumHelper.getSelenium().type("hetu", "010101-111X");

        seleniumHelper.getSelenium().click("nav-save");
        final WebElement mies = seleniumHelper.getDriver().findElement(By.name("sukupuoli"));
        assertTrue(!mies.isEnabled());
        assertTrue(mies.isSelected());
    }
}
