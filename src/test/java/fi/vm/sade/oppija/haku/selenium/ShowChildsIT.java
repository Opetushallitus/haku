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
import fi.vm.sade.oppija.haku.domain.elements.Teema;
import fi.vm.sade.oppija.haku.domain.elements.questions.CheckBox;
import fi.vm.sade.oppija.haku.domain.elements.questions.Option;
import fi.vm.sade.oppija.haku.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.haku.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;


/**
 * @author jukka
 * @version 9/20/123:26 PM}
 * @since 1.1
 */
public class ShowChildsIT extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final CheckBox checkBox = new CheckBox("checkbox", "Valitse jotain");
        checkBox.addOption("value", "value", "title");
        checkBox.addOption("value2", "value2", "title2");

        final Option option = checkBox.getOptions().get(0);
        final Option option2 = checkBox.getOptions().get(1);

        final Teema teema = new Teema("ekaryhma", "ekaryhma", null);
        teema.addChild(new TextQuestion("alikysymys1", "alikysymys1"));
        teema.addChild(new TextQuestion("alikysymys2", "alikysymys2"));

        final RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule1", option.getId(), ".*");
        relatedQuestionRule.addChild(teema);
        option.addChild(relatedQuestionRule);

        final RelatedQuestionRule relatedQuestionRule2 = new RelatedQuestionRule("rule2", option2.getId(), ".*");
        final TextQuestion textQuestion = new TextQuestion("laitakolmenollaa", "Laita kolme nollaa tähän");
        relatedQuestionRule2.addChild(textQuestion);
        option2.addChild(relatedQuestionRule2);

        final RelatedQuestionRule relatedQuestionRule3 = new RelatedQuestionRule("rule3", textQuestion.getId(), "[0]{3}");
        relatedQuestionRule3.addChild(new TextQuestion("tamanakyykolmellanollalla", "tamanakyykolmellanollalla"));
        textQuestion.addChild(relatedQuestionRule3);

        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(checkBox);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExistsWithNoJavaScript() throws IOException, InterruptedException {
        final String startUrl = formModelHelper.getStartUrl();
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + startUrl);
        driver.findElement(By.name("checkbox_value"));
        driver.findElement(By.id("checkbox_value2"));
        driver.findElement(By.name("checkbox_value")).click();
        seleniumHelper.getSelenium().click("nav-save");
        final WebElement alikysymys1 = driver.findElement(By.id("alikysymys1"));
        assertNotNull(alikysymys1);
    }
}
