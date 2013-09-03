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
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.koulutustausta.KoulutustaustaPhase;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

import static org.junit.Assert.assertFalse;
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

        findById("Postinumero");
        selenium.typeKeys("lahiosoite", "Katu 1");
        selenium.typeKeys("Postinumero", "00100");

        nextPhase();
        nextPhase();

        testHAK123AandHAK124();

        findByIdAndClick("POHJAKOULUTUS_" + KoulutustaustaPhase.TUTKINTO_PERUSKOULU);

        findById("PK_PAATTOTODISTUSVUOSI");
        selenium.typeKeys("PK_PAATTOTODISTUSVUOSI", "2013");

        findByIdAndClick("LISAKOULUTUS_KYMPPI", "LISAKOULUTUS_VAMMAISTEN", "LISAKOULUTUS_TALOUS", "LISAKOULUTUS_AMMATTISTARTTI", "KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON_false");
        setValue("perusopetuksen_kieli", "FI");
        nextPhase();
        //Skip toimipiste
        setValue("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();
        findByIdAndClick("preference1-discretionary_true");
        findByIdAndClick("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys_true");
        findByIdAndClick("preference1_sora_terveys_false");
        findByIdAndClick("preference1_sora_oikeudenMenetys_false");
        Select followUpSelect = new Select(driver.findElement(new By.ById("preference1-discretionary-follow-up")));
        followUpSelect.selectByIndex(1);

        nextPhase();

        select();
        selectByValue("PK_A1_OPPIAINE", "EN");
        selectByValue("PK_B1_OPPIAINE", "SE");

        nextPhase();

        nextPhase();
        // Lisätiedot
        clickAllElementsByXPath("//input[@type='checkbox']");

        // Ei mene läpi, työkokemus syöttämättä

        selenium.typeKeys("TYOKOKEMUSKUUKAUDET", "1001");
        nextPhase();
        // Ei mene läpi, työkokemus > 1000 kuukautta
        nextPhase();
        findById("TYOKOKEMUSKUUKAUDET");
        selenium.typeKeys("TYOKOKEMUSKUUKAUDET", "\b\b\b\b2"); // \b is backspace

        // Ei mene läpi, asiointikieli valitsematta
        nextPhase();
        findByIdAndClick("asiointikieli_suomi");

        // Menee läpi
        nextPhase();

        // Esikatselu
        nextPhase();
        findByIdAndClick("submit_confirm");

        String oid = driver.findElement(new By.ByClassName("number")).getText();
        assertFalse(oid.contains("."));

        navigateToFirstPhase();

        String value = driver.findElement(new By.ById("Sukunimi")).getAttribute("value");
        assertTrue(StringUtils.isEmpty(value));
        nextPhase();
    }

    private void testHAK123AandHAK124() {
        findByIdAndClick("POHJAKOULUTUS_" + KoulutustaustaPhase.TUTKINTO_KESKEYTYNYT);
        findById(KoulutustaustaPhase.TUTKINTO_KESKEYTNYT_NOTIFICATION_ID);
        findByIdAndClick("POHJAKOULUTUS_" + KoulutustaustaPhase.TUTKINTO_ULKOMAINEN_TUTKINTO);
        findById(KoulutustaustaPhase.TUTKINTO_ULKOMAILLA_NOTIFICATION_ID);
    }
}
