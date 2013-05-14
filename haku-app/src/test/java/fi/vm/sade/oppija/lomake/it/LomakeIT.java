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
import fi.vm.sade.oppija.lomakkeenhallinta.Yhteishaku2013;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LomakeIT extends DummyModelBaseItTest {

    @Test
    public void submitApplication() throws Exception {
        navigateToFirstPhase();
        setValue("Sukunimi", "Ankka");
        setValue("Etunimet", "Aku Kalle");
        setValue("Kutsumanimi", "A");
        setValue("Henkilotunnus", "010113-668B");
        setValue("Sähköposti", "aku.ankka@ankkalinna.al");
        setValue("matkapuhelinnumero1", "0501000100");
        setValue("aidinkieli", "FI");

        try {
            driver.findElement(By.id("puhelinnumero2"));
            fail();
        } catch (NoSuchElementException nsee) {
            // As expected
        }

        driver.findElement(By.id("addPuhelinnumero2Rule-link")).click();
        driver.findElement(By.id("matkapuhelinnumero2"));
        selenium.typeKeys("matkapuhelinnumero2", "09-123 456");

        nextPhase();

        findByClassName("notification");

        setValue("asuinmaa", "FIN");
        setValue("kotikunta", "jalasjarvi");

        screenshot("postinumero_it");
        findById("Postinumero");
        selenium.typeKeys("lahiosoite", "Katu 1");
        selenium.typeKeys("Postinumero", "00100");

        nextPhase();
        nextPhase();

        screenshot("hak123");
        testHAK123AandHAK124();

        findByIdAndClick("millatutkinnolla_" + Yhteishaku2013.TUTKINTO_PERUSKOULU);

        findById("paattotodistusvuosi_peruskoulu");
        selenium.typeKeys("paattotodistusvuosi_peruskoulu", "2013");

        findByIdAndClick("suorittanut1", "suorittanut2", "suorittanut3", "suorittanut4", "osallistunut_ei");
        setValue("perusopetuksen_kieli", "FI");
        nextPhase();
        driver.findElement(new By.ById("suorittanut1")).click();
        driver.findElement(new By.ById("suorittanut2")).click();
        driver.findElement(new By.ById("suorittanut3")).click();
        driver.findElement(new By.ById("suorittanut4")).click();

        driver.findElement(new By.ById("osallistunut_false")).click();

        clickNextPhase(driver);
        //Skip toimipiste
        setValue("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();
        driver.findElement(By.xpath("//input[@name='preference1-Harkinnanvarainen' and @value='false']")).click();

        nextPhase();
        select();

        nextPhase();
        findByIdAndClick("preference1-discretionary_true");
        Select followUpSelect = new Select(driver.findElement(new By.ById("preference1-discretionary-follow-up")));
        followUpSelect.selectByIndex(1);

        // Lisätiedot
        clickAllElementsByXPath("//input[@type='checkbox']");

        // Ei mene läpi, työkokemus syöttämättä

        selenium.typeKeys("tyokokemuskuukaudet", "1001");
        nextPhase();
        // Ei mene läpi, työkokemus > 1000 kuukautta
        nextPhase();
        findById("tyokokemuskuukaudet");
        selenium.typeKeys("tyokokemuskuukaudet", "\b\b\b\b2"); // \b is backspace

        // Ei mene läpi, asiointikieli valitsematta
        nextPhase();
        findByIdAndClick("asiointikieli_suomi");

        screenshot("kokemus");

        // Menee läpi
        nextPhase();
        screenshot("kokemus4");

        // Esikatselu
        nextPhase();
        findByIdAndClick("submit_confirm");

        String oid = driver.findElement(new By.ByClassName("number")).getText();
        assertTrue(oid.startsWith("1.2.3.4.5"));

        navigateToFirstPhase();

        String value = driver.findElement(new By.ById("Sukunimi")).getAttribute("value");
        assertTrue(StringUtils.isEmpty(value));
        nextPhase();
    }

    private void testHAK123AandHAK124() {
        findByIdAndClick("millatutkinnolla_" + Yhteishaku2013.TUTKINTO_KESKEYTYNYT);
        findById(Yhteishaku2013.TUTKINTO_KESKEYTNYT_NOTIFICATION_ID);
        findByIdAndClick("millatutkinnolla_" + Yhteishaku2013.TUTKINTO_ULKOMAINEN_TUTKINTO);
        findById(Yhteishaku2013.TUTKINTO_ULKOMAILLA_NOTIFICATION_ID);
    }
}
