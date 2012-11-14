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

import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author Hannu Lyytikainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class GradeGridIT extends AbstractSeleniumBase {

    @Before
    public void init() {
        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();
        initModel(dummyMem.getModel());
    }

    @Test
    public void testTableExists() {
        final String url = getBaseUrl() + "/" + "lomake/Yhteishaku/yhteishaku/arvosanat";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);

        assertNotNull(driver.findElement(By.id("gradegrid-table")));

    }

    @Test
    public void testAddLanguage() {
        final String url = getBaseUrl() + "/" + "lomake/Yhteishaku/yhteishaku/arvosanat";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);

        driver.findElement(By.id("add_language_button")).click();

        assertNotNull(driver.findElement(By.className("gradegrid-custom-language-row")));
    }
}
