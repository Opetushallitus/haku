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

import fi.vm.sade.oppija.common.selenium.DummyModelBaseItTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Hannu Lyytikainen
 */
public class WorkExperienceThemeTest extends DummyModelBaseItTest {

    @Test
    public void testWorkExperienceShown() {
        gotoHakutoiveet("010113-668B");
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();
        findByIdAndClick("preference1-discretionary_false",
                "preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys_true",
                "preference1_sora_terveys_false",
                "preference1_sora_oikeudenMenetys_false");
        nextPhase();

        select();
        selectByValue("PK_A1_OPPIAINE", "EN");
        selectByValue("PK_B1_OPPIAINE", "SE");

        nextPhase();
        findById("TYOKOKEMUSKUUKAUDET");
    }

    @Test
    public void testWorkExperienceNotShown() {
        gotoHakutoiveet("010113A668B");
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();
        findByIdAndClick("preference1-discretionary_false");
        nextPhase();
        select();
        nextPhase();

        List<WebElement> tyokokemuskuukaudet = driver.findElements(new By.ById("TYOKOKEMUSKUUKAUDET"));
        assertTrue("tyokokemuskuukaudet should not be present", tyokokemuskuukaudet.isEmpty());
    }

    private void gotoHakutoiveet(final String hetu) {
        navigateToFirstPhase();
        LinkedHashMap<String, String> henkilotiedot = defaultValues.henkilotiedot;
        henkilotiedot.put("Henkilotunnus", hetu);
        fillOut(henkilotiedot);

        nextPhase();
        fillOut(defaultValues.koulutustausta_pk);

        nextPhase();

        setValue("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
    }

}
