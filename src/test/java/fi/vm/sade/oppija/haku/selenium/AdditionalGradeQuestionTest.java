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

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;


/**
 * @author Hannu Lyytikainen
 */

public class AdditionalGradeQuestionTest extends AbstractSeleniumBase {

    public static final String OPETUSPISTE = "Helsingin sosiaali- ja terveysalan oppilaitos, Laakson koulutusyksikkö";

    @Before
    public void init() {
        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();
        initModel(dummyMem.getModel());
    }

    @Test
    public void testAdditionalSubjects() {
        final String url = getBaseUrl() + "/lomake/Yhteishaku/yhteishaku/hakutoiveet";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);

        // select a LOI
        driver.findElement(By.id("preference1-Opetuspiste"));
        Selenium s = seleniumHelper.getSelenium();

        s.typeKeys("preference1-Opetuspiste", "Hel");

        WebElement element = driver.findElement(By.linkText(OPETUSPISTE));
        element.click();
        WebElement option = driver.findElement(By.xpath("//option[@value='Sosiaali- ja terveysalan perustutkinto, pk']"));
        option.click();
        // navigate to grade phase
        s.click("class=right");
        driver.findElement(By.xpath("//table[@id='gradegrid-table']"));
        assertEquals(19, driver.findElements(By.xpath("//table[@id='gradegrid-table']/tbody/tr")).size());


    }

}
