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

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static junit.framework.Assert.assertNotNull;

public class HelpTextTest extends AbstractSeleniumBase {

    @Before
    public void init() {
        FormServiceMockImpl dummyMem = new FormServiceMockImpl();
        updateModel(dummyMem.getModel());
    }

    @Test
    public void testQuestionHelp() {
        final String url = getBaseUrl() + "/" + "lomake/" + Yhteishaku2013.ASID + "/yhteishaku/henkilotiedot";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);
        assertNotNull("Could not find question specific help", driver.findElement(By.id("help-Kutsumanimi")));
    }

    @Test
    public void testVerboseHelp() {
        final String url = getBaseUrl() + "/" + "lomake/" + Yhteishaku2013.ASID + "/yhteishaku/henkilotiedot/HenkilotiedotGrp/help";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);
        assertNotNull("Could not find verbose help page", driver.findElement(By.id("help-page")));
    }
}
