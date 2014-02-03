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

package fi.vm.sade.haku.oppija.lomake.it;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhaseYhteishakuSyksy;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;

import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.*;
import static org.junit.Assert.*;

public class LomakeIT extends DummyModelBaseItTest {

    @Test
    public void submitApplication() throws Exception {

        navigateToFirstPhase();
        setValue("Sukunimi", "Ankka ");
        setValue("Etunimet", " Aku Kalle");
        setValue("Kutsumanimi", " AKu");
        setValue("Henkilotunnus", "010113-668B");
        setValue("Sähköposti", " aku.ankka@ankkalinna.al    "); // OVT-5952 spaces
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

        testHAK123AandHAK124();
        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_YLIOPPILAS);
        findById("lukioPaattotodistusVuosi");
        selenium.typeKeys("lukioPaattotodistusVuosi", "2012");
        clickByNameAndValue("ammatillinenTutkintoSuoritettu", "false");
        setValue("lukion_kieli", "FI");
        nextPhase();

        setValue("preference1-Opetuspiste", "sturen");
        driver.findElement(By.linkText("Stadin ammattiopisto, Sturenkadun toimipaikka")).click();
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.5.20176855623']")).click();
        driver.findElement(new By.ByClassName("left")).click();

        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_PERUSKOULU);

        findById("PK_PAATTOTODISTUSVUOSI");
        selenium.typeKeys("PK_PAATTOTODISTUSVUOSI", "2013");

        findByIdAndClick("LISAKOULUTUS_KYMPPI", "LISAKOULUTUS_VAMMAISTEN", "LISAKOULUTUS_TALOUS", "LISAKOULUTUS_AMMATTISTARTTI");
        clickByNameAndValue("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON", "false");
        setValue("perusopetuksen_kieli", "FI");
        nextPhase();

        assertTrue(selenium.isTextPresent("ristiriita"));

        //Skip toimipiste
        setValue("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();

        fillOut(defaultValues.getPreference1(ImmutableMap.of("preference1-discretionary", "true")));

        Select followUpSelect = new Select(driver.findElement(new By.ByName("preference1-discretionary-follow-up")));
        followUpSelect.selectByIndex(1);

        nextPhase();

        select();
        selectByValue("PK_AI_OPPIAINE", "FI");
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

        setValue("TYOKOKEMUSKUUKAUDET", StringUtils.repeat("\b", "1001".length()) + "2"); //\b is backspace

        // Ei mene läpi, asiointikieli valitsematta
        nextPhase();
        clickByNameAndValue("asiointikieli", "suomi");

        // Menee läpi
        nextPhase();

        // Esikatselu
        nextPhase();

        findByIdAndClick("submit_confirm");

        String oid = driver.findElement(new By.ByClassName("number")).getText();
        findByXPath("//h3[contains(text(), \"form.valmis.musiikkitanssiliikunta.header\")]");

        assertFalse(oid.contains("."));

        //tulostus
        WebElement printLink = findByClassName("print").get(0);

        final int windowsBefore = driver.getWindowHandles().size();

        printLink.click();
        ExpectedCondition<Boolean> windowCondition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getWindowHandles().size() == windowsBefore + 1;
            }
        };
        WebDriverWait waitForWindow = new WebDriverWait(driver, 5);
        waitForWindow.until(windowCondition);

        ArrayList<String> newTab = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(newTab.get(1));
        assertTrue(driver.getCurrentUrl().contains("tulostus"));
        assertTrue(selenium.isTextPresent("Ankka"));
        driver.close();
        driver.switchTo().window(newTab.get(0));

        navigateToFirstPhase();

        String value = driver.findElement(new By.ById("Sukunimi")).getAttribute("value");
        assertTrue(StringUtils.isEmpty(value));
        nextPhase();
    }

    private void testHAK123AandHAK124() {
        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_KESKEYTYNYT);
        findById(KoulutustaustaPhaseYhteishakuSyksy.TUTKINTO_KESKEYTNYT_NOTIFICATION_ID);
        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_ULKOMAINEN_TUTKINTO);
        findById(KoulutustaustaPhaseYhteishakuSyksy.TUTKINTO_ULKOMAILLA_NOTIFICATION_ID);
    }
}
