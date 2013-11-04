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

import fi.vm.sade.oppija.common.selenium.DummyModelBaseItTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertTrue;


public class HAK153IT extends DummyModelBaseItTest {

    @Test
    public void testSoraAndUrheilijanLisakysymys() {
        navigateToFirstPhase();
        fillOut(defaultValues.henkilotiedot);
        nextPhase();
        fillOut(defaultValues.koulutustausta_pk);
        nextPhase();
        selenium.typeKeys("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@data-sora='true' and @data-id='1.2.246.562.14.673437691210']")).click();
        findByXPath("//a[@href='#' and @data-po-show='sora-popup' and @class='popup-link']");
        driver.findElement(By.xpath("//option[@data-sora='false' and @data-id='1.2.246.562.14.71344129359']")).click();
        boolean soraNotFound = driver.findElements(By.xpath("//a[@href='#' and @data-po-show='sora-popup' and @class='popup-link']")).isEmpty();
        findByXPath("//option[@data-athlete='true' and @data-id='1.2.246.562.14.71344129359']");
        clickByNameAndValue("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "false");
        clickByNameAndValue("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "true");
        assertTrue(soraNotFound);
    }
}
