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
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
        findByIdAndClick("preference1-discretionary_false");
        findByIdAndClick("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys_true");
        findByIdAndClick("preference1_sora_terveys_false");
        findByIdAndClick("preference1_sora_oikeudenMenetys_false");
        nextPhase();
        select();
        nextPhase();
        findById("tyokokemuskuukaudet");
    }

    @Test
    public void testWorkExperienceNotShown() {
        gotoHakutoiveet("010113A668B");
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();
        findByIdAndClick("preference1-discretionary_false");
        nextPhase();
        select();
        nextPhase();

        List<WebElement> tyokokemuskuukaudet = driver.findElements(new By.ById("tyokokemuskuukaudet"));
        assertTrue("tyokokemuskuukaudet should not be present", tyokokemuskuukaudet.isEmpty());
    }

    private void gotoHakutoiveet(final String hetu) {
        navigateToFirstPhase();
        setValue("Sukunimi", "Ankka");
        setValue("Etunimet", "Aku Kalle");
        setValue("Kutsumanimi", "A");
        setValue("Henkilotunnus", hetu);
        setValue("Sähköposti", "aku.ankka@ankkalinna.al");
        setValue("matkapuhelinnumero1", "0501000100");
        setValue("aidinkieli", "FI");
        setValue("asuinmaa", "FIN");
        setValue("kotikunta", "jalasjarvi");
        setValue("lahiosoite", "Katu 1");
        setValue("Postinumero", "00100");

        nextPhase();

        findByIdAndClick("POHJAKOULUTUS_tutkinto1", "LISAKOULUTUS_KYMPPI", "osallistunut_false", "koulutuspaikkaAmmatillisenTutkintoon_false");
        findById("paattotodistusvuosi_peruskoulu");
        setValue("perusopetuksen_kieli", "FI");
        setValue("paattotodistusvuosi_peruskoulu", "2012");

        nextPhase();

        setValue("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
    }

}
