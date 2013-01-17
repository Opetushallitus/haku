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

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class LomakeIT extends AbstractSeleniumBase {

    private FormModel model;

    @Before
    public void setUp() throws Exception {
        FormModelDummyMemoryDaoImpl formModelDummyMemoryDao = new FormModelDummyMemoryDaoImpl();
        this.model = formModelDummyMemoryDao.getModel();
        FormModel model = this.model;
        initModel(model);
    }

    @Test
    public void submitApplication() throws Exception {
        WebDriver driver = seleniumHelper.getDriver();
        Selenium selenium = seleniumHelper.getSelenium();
        driver.get(getBaseUrl() + "/lomake/");
        driver.findElement(new By.ById("Yhteishaku")).click();
        driver.findElement(new By.ById("yhteishaku")).click();
        selenium.typeKeys("Sukunimi", "Ankka");
        selenium.typeKeys("Etunimet", "Aku Kalle");
        selenium.typeKeys("Kutsumanimi", "A");
        selenium.typeKeys("Henkilotunnus", "150520-1111");
        selenium.typeKeys("Sähköposti", "aku.ankka@ankkalinna.al");
        selenium.typeKeys("matkapuhelinnumero", "0501000100");

        driver.findElement(new By.ByClassName("right")).click();

        driver.findElement(new By.ByClassName("notification"));

        Select select = new Select(driver.findElement(new By.ById("asuinmaa")));
        select.selectByIndex(1);

        //Wait
        driver.findElement(new By.ById("Postinumero"));
        selenium.typeKeys("lahiosoite", "Katu 1");
        selenium.typeKeys("Postinumero", "00100");

        driver.findElement(new By.ByClassName("right")).click();
        printElementsIdByXpath(driver, "//input");

        driver.findElement(new By.ByClassName("right")).click();

        driver.findElement(new By.ById("millatutkinnolla_tutkinto1")).click();
        driver.findElement(new By.ById("peruskoulu2012_kylla")).click();

        driver.findElement(new By.ById("suorittanut_suorittanut1")).click();
        driver.findElement(new By.ById("suorittanut_suorittanut2")).click();
        driver.findElement(new By.ById("suorittanut_suorittanut3")).click();
        driver.findElement(new By.ById("suorittanut_suorittanut4")).click();

        driver.findElement(new By.ById("osallistunut_ei")).click();

        driver.findElement(new By.ByClassName("right")).click();
        //Skip toimipiste
        driver.findElement(By.id("preference1-Opetuspiste"));
        selenium.typeKeys("preference1-Opetuspiste", "Hel");
        driver.findElement(By.linkText("Helsingin sosiaali- ja terveysalan oppilaitos, Laakson koulutusyksikkö")).click();
        driver.findElement(By.xpath("//option[@value='Sosiaali- ja terveysalan perustutkinto, pk']")).click();

        driver.findElement(new By.ByClassName("right")).click();
        printElementsIdByXpath(driver, "//select");
        select(driver);

        driver.findElement(new By.ByClassName("right")).click();

        selenium.typeKeys("tyokokemuskuukaudet", "10");
        clickAllCheckboxies(driver);
        driver.findElement(new By.ById("asiointikieli_suomi")).click();

        driver.findElement(new By.ByClassName("right")).click();
        driver.findElement(new By.ByClassName("right")).click();
        String oid = driver.findElement(new By.ByClassName("number")).getText();
        assertTrue(oid.startsWith("1.2.3.4.5"));

        driver.get(getBaseUrl() + "/lomake/");
        driver.findElement(new By.ById("Yhteishaku")).click();
        driver.findElement(new By.ById("yhteishaku")).click();
        String value = driver.findElement(new By.ById("Sukunimi")).getAttribute("value");
        assertTrue(StringUtils.isEmpty(value));


    }

    private void printElementsIdByXpath(final WebDriver driver, final String xpath) {
        List<WebElement> elements = driver.findElements(new By.ByXPath(xpath));
        for (WebElement element : elements) {
            System.out.println("---->" + element.getAttribute("id"));
        }
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

    private void clickAllCheckboxies(final WebDriver driver) {
        List<WebElement> elements = driver.findElements(new By.ByXPath("//checkbox"));
        for (WebElement element : elements) {
            element.click();
        }
    }

}
