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

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.thoughtworks.selenium.Selenium;

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;


/**
 * @author Hannu Lyytikainen
 */
public class AdditionalGradeQuestionTest extends AbstractSeleniumBase {

    public static final String OPETUSPISTE = "FAKTIA, Espoo op";

    @Before
    public void init() {
        super.before();
        FormServiceMockImpl dummyMem = new FormServiceMockImpl();
        updateIndexAndFormModel(dummyMem.getModel());
    }

    @Test
    @Ignore
    public void testAdditionalSubjects() {
        final String url = getBaseUrl() + "/lomake/"+ Yhteishaku2013.ASID + "/yhteishaku/koulutustausta";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);
        Selenium s = seleniumHelper.getSelenium();
        driver.findElement(new By.ById("millatutkinnolla_tutkinto1")).click();
        driver.findElement(new By.ById("paattotodistusvuosi_peruskoulu"));
        s.typeKeys("paattotodistusvuosi_peruskoulu", "2013");
        driver.findElement(new By.ById("suorittanut1")).click();
        driver.findElement(new By.ById("suorittanut2")).click();
        driver.findElement(new By.ById("suorittanut3")).click();
        driver.findElement(new By.ById("suorittanut4")).click();

        driver.findElement(new By.ById("osallistunut_ei")).click();

        driver.findElement(new By.ByClassName("right")).click();

        // select a LOI
        driver.findElement(By.id("preference1-Opetuspiste"));


        s.typeKeys("preference1-Opetuspiste", "Esp");

        WebElement element = driver.findElement(By.linkText(OPETUSPISTE));
        element.click();
        WebElement option = driver.findElement(By.xpath("//option[@value='Kaivosalan perustutkinto, pk']"));
        option.click();
        // navigate to grade phase
        s.click("class=right");
        driver.findElement(By.xpath("//table[@id='gradegrid-table']"));
        assertTrue(driver.findElements(By.xpath("//table[@id='gradegrid-table']/tbody/tr")).size() > 10);


    }

}
