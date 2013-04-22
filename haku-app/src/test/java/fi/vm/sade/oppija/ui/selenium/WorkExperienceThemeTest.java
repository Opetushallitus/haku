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
import org.openqa.selenium.support.ui.Select;

import static org.junit.Assert.fail;

/**
 * @author Hannu Lyytikainen
 */
public class WorkExperienceThemeTest extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Before
    public void init() {
        FormServiceMockImpl dummyMem = new FormServiceMockImpl();
        this.formModelHelper = updateIndexAndFormModel(dummyMem.getModel());
    }

    @Test
    public void testWorkExperienceShown() {
        WebDriver driver = gotoHakutoiveet("010113-668B");
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();

        driver.findElement(new By.ByClassName("right")).click();
        driver.findElement(new By.ByClassName("right")).click();

        driver.findElement(new By.ById("tyokokemuskuukaudet"));
    }

    @Test
    public void testWorkExperienceNotShown() {
        WebDriver driver = gotoHakutoiveet("010113A668B");
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();

        driver.findElement(new By.ByClassName("right")).click();
        driver.findElement(new By.ByClassName("right")).click();

        try {
            driver.findElement(new By.ById("tyokokemuskuukaudet"));
            fail();
        } catch (NoSuchElementException e) {
            // test passed
        }
    }

    private WebDriver gotoHakutoiveet(String hetu) {
        final String henkilotiedot = formModelHelper.getFormUrl(formModelHelper.getFirstForm().getPhase("henkilotiedot"));
        final String hakutoiveet = formModelHelper.getFormUrl(formModelHelper.getFirstForm().getPhase("hakutoiveet"));

        Selenium selenium = seleniumHelper.getSelenium();
        WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + henkilotiedot);

        driver.findElement(By.id("Sukunimi")).sendKeys("sukunimi");
        driver.findElement(By.id("Etunimet")).sendKeys("etunimi");
        driver.findElement(By.id("Kutsumanimi")).sendKeys("etunimi");
        driver.findElement(By.id("Henkilotunnus")).sendKeys(hetu);
        new Select(driver.findElement(By.id("asuinmaa"))).selectByValue("FI");
        driver.findElement(By.id("lahiosoite")).sendKeys("Testikatu 1");
        driver.findElement(By.id("Postinumero")).sendKeys("00100");
        new Select(driver.findElement(By.id("kotikunta"))).selectByIndex(1);
        new Select(driver.findElement(By.id("aidinkieli"))).selectByIndex(1);

        driver.findElement(new By.ByClassName("right")).click();
        driver.get(getBaseUrl() + "/" + hakutoiveet);

        inputOpetuspiste(selenium, driver);
        return driver;
    }

    private void inputOpetuspiste(Selenium selenium, WebDriver driver) {
        driver.findElement(By.id("preference1-Opetuspiste"));
        selenium.typeKeys("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
    }
}
