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

package fi.vm.sade.haku.oppija.ui.it;

import fi.vm.sade.haku.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.KoodistoServiceMockImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class DropdownSelectIT extends AbstractSeleniumBase {

    public static final String SELECT_ID = "select_id";
    private ApplicationSystemHelper applicationSystemHelper;
    private WebDriver driver;
    private Element dropdownSelect;

    @Before
    public void init() throws IOException {
        dropdownSelect = new DropdownSelectBuilder(SELECT_ID)
                .addOption(createI18NAsIs("option1"), "option1")
                .addOption(createI18NAsIs("option2"), "option2")
                .i18nText(createI18NAsIs(SELECT_ID))
                .build();
        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(dropdownSelect);
        this.applicationSystemHelper = updateApplicationSystem(applicationSystem);
        driver = seleniumContainer.getDriver();
        driver.get(getBaseUrl() + this.applicationSystemHelper.getFormUrl(this.applicationSystemHelper.getFirstPhase().getId()));
    }

    @Test
    public void testSelect() throws IOException {
        WebElement select = driver.findElement(new By.ById(dropdownSelect.getId()));
        assertEquals("Invalid dropdown select tag", "select", select.getTagName());
        assertEquals("Invalid dropdown name attribute", dropdownSelect.getId(), select.getAttribute("name"));
        List<WebElement> options = driver.findElements(new By.ByTagName("option"));
        assertEquals("", 2, options.size());
    }

    @Test
    public void testDivsExists() throws IOException {
        driver.findElement(new By.ByXPath("//div[@class='form-item']"));
        driver.findElement(new By.ByXPath("//div[@class='form-item-content']"));
        driver.findElement(new By.ByXPath("//div[@class='clear']"));
    }

    @Test
    public void testLabel() throws IOException {
        WebElement label = driver.findElement(new By.ByTagName("label"));
        assertEquals("Invalid attribute on label tag", dropdownSelect.getId(), label.getAttribute("for"));
        assertEquals("Invalid label id", "label-" + dropdownSelect.getId(), label.getAttribute("id"));
    }

    @Test
    public void testLabelLangSv() throws IOException {
        driver.get(getBaseUrl() + this.applicationSystemHelper.getFormUrl(this.applicationSystemHelper.getFirstPhase().getId()) + "?lang=sv");
        WebElement label = driver.findElement(new By.ByTagName("label"));
        assertEquals("Invalid label id", ((DropdownSelect) dropdownSelect).getI18nText().getTranslations().get("sv"), label.getText());
    }

    @Ignore
    @Test
    public void testLabelLangXx() throws IOException {
        driver.get(getBaseUrl() + this.applicationSystemHelper.getFormUrl(this.applicationSystemHelper.getFirstPhase().getId()) + "?lang=xx");
        WebElement label = driver.findElement(new By.ByTagName("label"));
        assertEquals("Invalid label id", "???", label.getText());
    }
}
