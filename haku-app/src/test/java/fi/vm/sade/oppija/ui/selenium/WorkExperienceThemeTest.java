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

package fi.vm.sade.oppija.ui.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.fail;

/**
 * @author Hannu Lyytikainen
 */
@Ignore
public class WorkExperienceThemeTest extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Before
    public void init() {
        FormServiceMockImpl dummyMem = new FormServiceMockImpl();
        this.formModelHelper = updateIndexAndFormModel(dummyMem.getModel());
    }

    @Test
    public void testWorkExperienceShown() {
        WebDriver driver = gotoHakutoiveet();
        driver.findElement(By.xpath("//option[@data-id='873']")).click();

        driver.findElement(new By.ByClassName("right")).click();
        driver.findElement(new By.ByClassName("right")).click();

        driver.findElement(new By.ById("tyokokemuskuukaudet"));
    }

    private WebDriver gotoHakutoiveet() {
        final String startUrl = formModelHelper.getFormUrl(formModelHelper.getFirstForm().getPhase("hakutoiveet"));

        Selenium selenium = seleniumHelper.getSelenium();

        WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + startUrl);

        inputOpetuspiste(selenium, driver);
        return driver;
    }

    @Test
    public void testWorkExperienceNotShown() {
        WebDriver driver = gotoHakutoiveet();
        driver.findElement(By.xpath("//option[@data-id='776']")).click();

        driver.findElement(new By.ByClassName("right")).click();
        driver.findElement(new By.ByClassName("right")).click();

        try {
            driver.findElement(new By.ById("tyokokemuskuukaudet"));
            fail();
        } catch (NoSuchElementException e) {
            // test passed
        }
    }

    private void inputOpetuspiste(Selenium selenium, WebDriver driver) {
        driver.findElement(By.id("preference1-Opetuspiste"));
        selenium.typeKeys("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("Espoon yhteislyseon lukio op")).click();
    }
}
