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

package fi.vm.sade.oppija.lomake.it;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.thoughtworks.selenium.Selenium;

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;

public class LomakeIT extends AbstractSeleniumBase {

    @Before
    public void setUp() throws Exception {
        FormServiceMockImpl formModelDummyMemoryDao = new FormServiceMockImpl();
        updateIndexAndFormModel(formModelDummyMemoryDao.getModel());
    }

    @Test
    public void submitApplication() throws Exception {
        WebDriver driver = seleniumHelper.getDriver();
        Selenium selenium = seleniumHelper.getSelenium();
        driver.get(getBaseUrl() + "/lomake/");
        selenium.setSpeed("3000");
        driver.findElement(new By.ById(Yhteishaku2013.ASID)).click();
        driver.findElement(new By.ById("yhteishaku")).click();
        selenium.typeKeys("Sukunimi", "Ankka");
        selenium.typeKeys("Etunimet", "Aku Kalle");
        selenium.typeKeys("Kutsumanimi", "A");
        selenium.typeKeys("Henkilotunnus", "150520-111E");
        selenium.typeKeys("Sähköposti", "aku.ankka@ankkalinna.al");
        selenium.typeKeys("matkapuhelinnumero", "0501000100");

        clickNextPhase(driver);

        driver.findElement(new By.ByClassName("notification"));

        Select select = new Select(driver.findElement(new By.ById("asuinmaa")));
        select.selectByIndex(1);

        //Wait
        driver.findElement(new By.ById("Postinumero"));
        selenium.typeKeys("lahiosoite", "Katu 1");
        selenium.typeKeys("Postinumero", "00100");

        clickNextPhase(driver);

        clickNextPhase(driver);

        driver.findElement(new By.ById("millatutkinnolla_tutkinto1")).click();
        driver.findElement(new By.ById("paattotodistusvuosi_peruskoulu"));
        selenium.typeKeys("paattotodistusvuosi_peruskoulu", "2013");

        driver.findElement(new By.ById("suorittanut1")).click();
        driver.findElement(new By.ById("suorittanut2")).click();
        driver.findElement(new By.ById("suorittanut3")).click();
        driver.findElement(new By.ById("suorittanut4")).click();

        driver.findElement(new By.ById("osallistunut_ei")).click();

        clickNextPhase(driver);
        //Skip toimipiste
        driver.findElement(By.id("preference1-Opetuspiste"));
        selenium.typeKeys("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@value='Kaivosalan perustutkinto, pk']")).click();

        clickNextPhase(driver);
        select(driver);

        clickNextPhase(driver);

        selenium.typeKeys("tyokokemuskuukaudet", "1001");
        clickAllElements(driver, "//input[@type='checkbox']");

        selenium.typeKeys("lupa1_email", "aiti@koti.fi");
        driver.findElement(new By.ById("asiointikieli_suomi")).click();

        clickNextPhase(driver);

        // HAK-20.
        driver.findElement(new By.ById("tyokokemuskuukaudet"));
        selenium.typeKeys("tyokokemuskuukaudet", "\b\b\b\b2"); // \b is backspace
        clickNextPhase(driver);

        clickNextPhase(driver);
        driver.findElement(By.id("submit_confirm")).click();

        String oid = driver.findElement(new By.ByClassName("number")).getText();
        assertTrue(oid.startsWith("1.2.3.4.5"));

        driver.get(getBaseUrl() + "/lomake/");
        driver.findElement(new By.ById(Yhteishaku2013.ASID)).click();
        driver.findElement(new By.ById("yhteishaku")).click();
        String value = driver.findElement(new By.ById("Sukunimi")).getAttribute("value");
        assertTrue(StringUtils.isEmpty(value));
        clickNextPhase(driver);
    }

    private void clickNextPhase(WebDriver driver) {
        driver.findElement(new By.ByClassName("right")).click();
    }

    private void select(final WebDriver driver) {
        List<WebElement> elements = driver.findElements(new By.ByXPath("//select"));
        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                Select select = new Select(element);
                select.selectByIndex(2);
            }
        }
    }

    private void clickAllElements(final WebDriver driver, final String xpath) {
        List<WebElement> elements = driver.findElements(new By.ByXPath(xpath));
        for (WebElement element : elements) {
            element.click();
        }
    }

}
