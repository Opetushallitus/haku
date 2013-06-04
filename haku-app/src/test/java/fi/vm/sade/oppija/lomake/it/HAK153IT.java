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
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.hakutoiveet.HakutoiveetPhase;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.mongodb.util.MyAsserts.assertTrue;

public class HAK153IT extends DummyModelBaseItTest {

    @Test
    public void testSoraAndUrheilijanLisakysymys() throws Exception {
        navigateToPhase(HakutoiveetPhase.HAKUTOIVEET_PHASE_ID);
        selenium.typeKeys("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@data-sora='true' and @data-id='1.2.246.562.14.673437691210']")).click();
        findByXPath("//a[@href='#' and @data-po-show='sora-popup' and @class='popup-link']");
        driver.findElement(By.xpath("//option[@data-sora='false' and @data-id='1.2.246.562.14.71344129359']")).click();
        boolean soraNotFound = driver.findElements(By.xpath("//a[@href='#' and @data-po-show='sora-popup' and @class='popup-link']")).isEmpty();
        findByXPath("//option[@data-athlete='true' and @data-id='1.2.246.562.14.71344129359']");
        findById("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys_true");
        findById("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys_false");
        assertTrue(soraNotFound);
    }
}
