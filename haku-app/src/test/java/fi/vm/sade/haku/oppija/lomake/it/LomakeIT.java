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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;

import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LomakeIT extends DummyModelBaseItTest {

    @Test
    public void submitApplication() throws Exception {

        final WebDriver driver = seleniumContainer.getDriver();

        navigateToFirstPhase();
        WebElement form = findBy(By.id("form-henkilotiedot"));
        assertTrue(form.isDisplayed());
        setValue("Sukunimi", "Ankka ");
        setValue("Etunimet", " Aku Kalle");
        setValue("Kutsumanimi", " AKu");
        setValue("onkosinullakaksoiskansallisuus", "false");
        setValue("Henkilotunnus", "010113-668B");
        setValue("Sähköposti", " aku.ankka@ankkalinna.al    "); // OVT-5952 spaces
        setValue("matkapuhelinnumero1", "0501000100");
        setValue("aidinkieli", "FI");

        findById("help-Kutsumanimi");

        elementsNotPresentByName("puhelinnumero2");

        findByIdAndClick("addPuhelinnumero2Rule-link");
        findById("matkapuhelinnumero2");
        setValue("matkapuhelinnumero2", "09-123 456");

        screenshot("lomake");

        setValue("huoltajannimi", "Äiti Ankka");
        setValue("huoltajanpuhelinnumero", "0500111011");
        setValue("huoltajansahkoposti", "aiti.ankka@ankkalinna.al");

        findByAndAjaxClick(new By.ByClassName("right"));
        findById("phase-contains-errors");

        setValue("asuinmaa", "FIN");
        setValue("kotikunta", "jalasjarvi");

        findById("Postinumero");
        setValue("lahiosoite", "Katu 1");
        setValue("Postinumero", "00100");

        nextPhase(OppijaConstants.PHASE_EDUCATION);

        testHAK123AandHAK124();
        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_YLIOPPILAS);
        findById(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI);
        setValue(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI, "2012");
        clickByNameAndValue("ammatillinenTutkintoSuoritettu", "false");
        setValue(OppijaConstants.LUKIO_KIELI, "FI");
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        typeWithoutTab("preference1-Opetuspiste", "sturen");
        clickLinkByText("Stadin ammattiopisto, Sturenkadun toimipaikka");
        waitForMillis(100);

        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.5.20176855623']")).click();
        waitForMillis(500);

        prevPhase(OppijaConstants.PHASE_EDUCATION);

        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_PERUSKOULU);

        findById(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI);
        setValue(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI, "2012");

        findByIdAndClick(
                OppijaConstants.ELEMENT_ID_LISAKOULUTUS_KYMPPI,
                OppijaConstants.ELEMENT_ID_LISAKOULUTUS_VAMMAISTEN,
                OppijaConstants.ELEMENT_ID_LISAKOULUTUS_TALOUS,
                OppijaConstants.ELEMENT_ID_LISAKOULUTUS_AMMATTISTARTTI,
                OppijaConstants.ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO_LUKIO
                );
        setValue(OppijaConstants.KYMPPI_PAATTOTODISTUSVUOSI, "2012");
        setValue(OppijaConstants.PERUSOPETUS_KIELI, "FI");
        setValue("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON", "false", true);
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        assertTrue("Warning text 'ristiriita' not found", !findByClassName("warning").isEmpty());

        //Skip toimipiste
        findByIdAndClick("preference1-reset");
        waitForAjax();

        typeWithoutTab("preference1-Opetuspiste", "Esp");

        findByAndAjaxClick(By.linkText("FAKTIA, Espoo op"));
        findByAndAjaxClick(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']"));

        fillOut(defaultValues.getPreference1(ImmutableMap.of("preference1-discretionary", "true")));

        Select followUpSelect = new Select(driver.findElement(new By.ByName("preference1-discretionary-follow-up")));
        followUpSelect.selectByIndex(1);

        nextPhase(OppijaConstants.PHASE_GRADES);

        select();
        selectByValue("PK_AI_OPPIAINE", "FI");
        selectByValue("PK_A1_OPPIAINE", "EN");
        selectByValue("PK_B1_OPPIAINE", "SE");

        nextPhase(OppijaConstants.PHASE_MISC);
        prevPhase(OppijaConstants.PHASE_GRADES);
        nextPhase(OppijaConstants.PHASE_MISC);

        // Lisätiedot

        // Ei mene läpi, työkokemus syöttämättä
        findByAndAjaxClick(new By.ByClassName("right"));
        findById("phase-contains-errors");

        clickAllElementsByXPath("//input[@type='checkbox']");
        setValue("TYOKOKEMUSKUUKAUDET", "1001");

        // Ei mene läpi, työkokemus > 1000 kuukautta
        findByAndAjaxClick(new By.ByClassName("right"));
        findById("phase-contains-errors");

        findById("TYOKOKEMUSKUUKAUDET");
        setValue("TYOKOKEMUSKUUKAUDET", StringUtils.repeat("\b", "1001".length()) + "2"); //\b is backspace

        // Ei mene läpi, asiointikieli valitsematta
        findByAndAjaxClick(new By.ByClassName("right"));
        findById("phase-contains-errors");
        clickByNameAndValue("asiointikieli", "suomi");

        // Menee läpi
        nextPhase(OppijaConstants.PHASE_PREVIEW);

        // Esikatselu
        nextPhase(OppijaConstants.PHASE_PREVIEW);

        findByIdAndClick("submit_confirm");

        String oid = driver.findElement(new By.ByClassName("number")).getText();
        findByXPath("//h3[contains(text(), \"Musiikki- tanssi- ja liikunta-alan\")]");

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
        driver.close();
        driver.switchTo().window(newTab.get(0));

        navigateToFirstPhase();

        String value = driver.findElement(new By.ById("Sukunimi")).getAttribute("value");
        assertTrue(StringUtils.isEmpty(value));
        // Ei mene läpi, tyhjä lomake
        findByAndAjaxClick(new By.ByClassName("right"));
        findById("phase-contains-errors");
    }

    private void testHAK123AandHAK124() {
        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_KESKEYTYNYT);
        findById(KoulutustaustaPhase.TUTKINTO_KESKEYTNYT_NOTIFICATION_ID);
        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_ULKOMAINEN_TUTKINTO);
        waitForAjax();
        findById(KoulutustaustaPhase.TUTKINTO_ULKOMAILLA_NOTIFICATION_ID);
    }
}
