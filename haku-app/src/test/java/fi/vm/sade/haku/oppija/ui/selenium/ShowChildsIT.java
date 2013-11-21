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
import fi.vm.sade.haku.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertNotNull;

public class ShowChildsIT extends AbstractSeleniumBase {

    private ApplicationSystemHelper applicationSystemHelper;
    private CheckBox checkBox1;
    private WebDriver driver;

    @Before
    public void init() throws IOException {
        checkBox1 = new CheckBox("value", createI18NAsIs("title"));
        final CheckBox checkBox2 = new CheckBox("value2", createI18NAsIs("title2"));

        final Theme theme = new Theme("ekaryhma", createI18NAsIs("ekaryhma"), true);
        theme.addChild(new TextQuestion("alikysymys1", createI18NAsIs("alikysymys1")));
        theme.addChild(new TextQuestion("alikysymys2", createI18NAsIs("alikysymys2")));

        final RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule1", checkBox1.getId(), ".*", false);
        relatedQuestionRule.addChild(theme);
        checkBox1.addChild(relatedQuestionRule);

        final RelatedQuestionRule relatedQuestionRule2 = new RelatedQuestionRule("rule2", checkBox2.getId(), ".*", false);
        final TextQuestion textQuestion = new TextQuestion("laitakolmenollaa", createI18NAsIs("Laita kolme nollaa tähän"));
        relatedQuestionRule2.addChild(textQuestion);
        checkBox2.addChild(relatedQuestionRule2);

        final RelatedQuestionRule relatedQuestionRule3 = new RelatedQuestionRule("rule3", textQuestion.getId(), "[0]{3}", false);
        relatedQuestionRule3.addChild(new TextQuestion("tamanakyykolmellanollalla", createI18NAsIs("tamanakyykolmellanollalla")));
        textQuestion.addChild(relatedQuestionRule3);

        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(checkBox1, checkBox2);
        this.applicationSystemHelper = updateApplicationSystem(applicationSystem);
        driver = seleniumContainer.getDriver();
    }

    @Test
    public void testInputExistsWithNoJavaScript() throws IOException, InterruptedException {

        final String startUrl = applicationSystemHelper.getStartUrl();
        driver.get(getBaseUrl() + startUrl);
        WebElement checkbox = driver.findElement(By.id(checkBox1.getId()));
        checkbox.click();
        final WebElement alikysymys1 = driver.findElement(By.id("alikysymys1"));
        assertNotNull("Checkbox not found", alikysymys1);
    }

}
