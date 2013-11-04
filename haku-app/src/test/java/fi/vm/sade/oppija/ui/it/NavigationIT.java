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

package fi.vm.sade.oppija.ui.it;

import fi.vm.sade.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.oppija.ui.selenium.DefaultValues;
import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;

public class NavigationIT extends DummyModelBaseItTest {

    @Test
    public void testNavigationExists() throws IOException {

        navigateToFirstPhase();
        elementsPresentBy(By.xpath("//a[@id='nav-henkilotiedot']"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'2')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'3')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'4')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'5')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'6')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'7')]"));

        elementsPresentBy(By.xpath("//button[@class='right']"));
        elementsNotPresentBy(By.xpath("//button[@class='left']"));
        fillOut(defaultValues.henkilotiedot);
        nextPhase();

        elementsPresentBy(By.xpath("//a[@id='nav-henkilotiedot']"));
        elementsPresentBy(By.xpath("//a[@id='nav-koulutustausta']"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'3')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'4')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'5')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'6')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'7')]"));

        elementsPresentBy(By.xpath("//button[@class='right']"));
        elementsPresentBy(By.xpath("//button[@class='left']"));
        fillOut(defaultValues.koulutustausta_pk);
        nextPhase();

        elementsPresentBy(By.xpath("//a[@id='nav-henkilotiedot']"));
        elementsPresentBy(By.xpath("//a[@id='nav-koulutustausta']"));
        elementsPresentBy(By.xpath("//a[@id='nav-hakutoiveet']"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'4')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'5')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'6')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'7')]"));

        elementsPresentBy(By.xpath("//button[@class='right']"));
        elementsPresentBy(By.xpath("//button[@class='left']"));

        setValue("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText(DefaultValues.OPETUSPISTE)).click();
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();

        fillOut(defaultValues.preference1);

        nextPhase();

        select();
        selectByValue("PK_A1_OPPIAINE", "EN");
        selectByValue("PK_B1_OPPIAINE", "SV");
        elementsPresentBy(By.xpath("//a[@id='nav-henkilotiedot']"));
        elementsPresentBy(By.xpath("//a[@id='nav-koulutustausta']"));
        elementsPresentBy(By.xpath("//a[@id='nav-hakutoiveet']"));
        elementsPresentBy(By.xpath("//a[@id='nav-osaaminen']"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'5')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'6')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'7')]"));
        elementsPresentBy(By.xpath("//button[@class='right']"));
        elementsPresentBy(By.xpath("//button[@class='left']"));
        nextPhase();

        elementsPresentBy(By.xpath("//a[@id='nav-henkilotiedot']"));
        elementsPresentBy(By.xpath("//a[@id='nav-koulutustausta']"));
        elementsPresentBy(By.xpath("//a[@id='nav-hakutoiveet']"));
        elementsPresentBy(By.xpath("//a[@id='nav-osaaminen']"));
        elementsPresentBy(By.xpath("//a[@id='nav-lisatiedot']"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'6')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'7')]"));
        elementsPresentBy(By.xpath("//button[@class='right']"));
        elementsPresentBy(By.xpath("//button[@class='left']"));

        fillOut(defaultValues.lisatiedot);
        nextPhase();
        elementsPresentBy(By.xpath("//a[@id='nav-henkilotiedot']"));
        elementsPresentBy(By.xpath("//a[@id='nav-koulutustausta']"));
        elementsPresentBy(By.xpath("//a[@id='nav-hakutoiveet']"));
        elementsPresentBy(By.xpath("//a[@id='nav-osaaminen']"));
        elementsPresentBy(By.xpath("//a[@id='nav-lisatiedot']"));
        elementsPresentBy(By.xpath("//li/a[@class='current']"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'7')]"));

        elementsPresentBy(By.xpath("//button[@class='right']"));
        elementsPresentBy(By.xpath("//button[@class='left']"));

        nextPhase();
        selenium.goBack();
        selenium.goBack();
        selenium.goBack();
        selenium.goBack();
        selenium.goBack();
        nextPhase();
        nextPhase();
        nextPhase();
        nextPhase();
        nextPhase();
        nextPhase();
        findByIdAndClick("submit_confirm");

        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'1')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'2')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'3')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'4')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'5')]"));
        elementsPresentBy(By.xpath("//li/span/span[contains(text(),'6')]"));
        elementsPresentBy(By.xpath("//li/a[@class='current']/span[contains(text(),'7')]"));

    }

    @Test
    public void testApplicationSystemNotFound() throws Exception {
        navigateToPath("lomake/nonexistingapplicationsysytemid");
        findByXPath("//*[contains(.,'Tapahtui odottamaton virhe.')]");
    }
}
