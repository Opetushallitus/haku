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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.haku.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertEquals;


/**
 * @author Hannu Lyytikainen
 */
public class AdditionalGradeQuestionTest extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Before
    public void init() {

        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();

        this.formModelHelper = initModel(dummyMem.getModel());
    }

    @Test
    public void testAdditionalSubjects() {
        final String url = getBaseUrl() + "/" + "lomake/test/yhteishaku/hakutoiveet";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);

        // select a LOI
        driver.findElement(By.id("preference1-Opetuspiste"));
        Selenium s = seleniumHelper.getSelenium();
        s.typeKeys("preference1-Opetuspiste", "koulu");
        WebDriverWait wait = new WebDriverWait(driver, 5, 1000);
        wait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver d) {
                return d.findElement(By.linkText("Koulu0"));
            }
        });
        driver.findElement(By.linkText("Koulu0")).click();
        wait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver d) {
                return d.findElement(By.xpath("//option[@value='Hakukohde_0_0']"));
            }
        });
        driver.findElement(By.xpath("//option[@value='Hakukohde_0_0']")).click();

        // navigate to grade phase
        s.click("nav-next");

        wait.until(new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver d) {
                return d.findElement(By.xpath("//table[@id='gradegrid-table']"));
            }
        });


        assertEquals(11, driver.findElements(By.xpath("//table[@id='gradegrid-table']/tbody/tr")).size());


    }

}
