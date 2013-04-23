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
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import fi.vm.sade.oppija.lomakkeenhallinta.Yhteishaku2013;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        selenium.typeKeys("matkapuhelinnumero1", "0501000100");
        Select selectAidinkieli = new Select(driver.findElement(new By.ById("aidinkieli")));
        selectAidinkieli.selectByIndex(1);

        try {
            driver.findElement(By.id("puhelinnumero2"));
            fail();
        } catch (NoSuchElementException nsee) {
            // As expected
        }

        driver.findElement(By.id("addPuhelinnumero2Rule-link")).click();
        driver.findElement(By.id("matkapuhelinnumero2"));
        selenium.typeKeys("matkapuhelinnumero2", "0501000100");

        clickNextPhase(driver);

        driver.findElement(new By.ByClassName("notification"));

        Select asuinmaaSelect = new Select(driver.findElement(new By.ById("asuinmaa")));
        asuinmaaSelect.selectByIndex(0);

        Select selectKotikunta = new Select(driver.findElement(new By.ById("kotikunta")));
        selectKotikunta.selectByIndex(1);

        screenshot("postinumero_it");
        driver.findElement(new By.ById("Postinumero"));
        selenium.typeKeys("lahiosoite", "Katu 1");
        selenium.typeKeys("Postinumero", "00100");

        clickNextPhase(driver);

        clickNextPhase(driver);

        screenshot("hak123");
        testHAK123AandHAK124(driver);


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

        driver.findElements(By.name("preference1-Harkinnanvarainen")).get(1).click();

        Select followUpSelect = new Select(driver.findElement(new By.ById("preference1 - harkinnanvarainen_jatko")));
        followUpSelect.selectByIndex(1);

        clickNextPhase(driver);
        select(driver);

        clickNextPhase(driver);

        // Lisätiedot
        clickAllElements(driver, "//input[@type='checkbox']");

        // Ei mene läpi, työkokemus syöttämättä
        clickNextPhase(driver);
        selenium.typeKeys("tyokokemuskuukaudet", "1001");

        // Ei mene läpi, työkokemus > 1000 kuukautta
        clickNextPhase(driver);
        driver.findElement(new By.ById("tyokokemuskuukaudet"));
        selenium.typeKeys("tyokokemuskuukaudet", "\b\b\b\b2"); // \b is backspace

        // Ei mene läpi, asiointikieli valitsematta
        clickNextPhase(driver);
        driver.findElement(new By.ById("asiointikieli_suomi")).click();

        screenshot("kokemus");

        // Menee läpi
        clickNextPhase(driver);
        screenshot("kokemus4");

        // Esikatselu
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

    private void testHAK123AandHAK124(final WebDriver driver) {
        driver.findElement(new By.ById("millatutkinnolla_tutkinto5")).click();
        driver.findElement(new By.ById(Yhteishaku2013.TUTKINTO5_NOTIFICATION_ID));
        driver.findElement(new By.ById("millatutkinnolla_tutkinto7")).click();
        driver.findElement(new By.ById(Yhteishaku2013.TUTKINTO7_NOTIFICATION_ID));
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
