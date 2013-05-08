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
import org.openqa.selenium.support.ui.Select;

import static org.junit.Assert.assertTrue;

public class HAK305IT extends DummyModelBaseItTest {

    public static final String NATIVE_LANGUAGE_FI = "FI";
    public static final String NATIVE_LANGUAGE_SV = "SV";

    @Test
    public void submitApplication() throws Exception {
        navigateToFirstPhase();

        fillInTheHenkilotiedotPhase(NATIVE_LANGUAGE_FI);

        nextPhase();

        fillInTheKoulutustaustaPhase(NATIVE_LANGUAGE_FI);

        nextPhase();

        fillInTheHakutoiveetPhase();

        nextPhase();

        fillInArvosanatTheme();

        elementsNotPresentByName("yleinen_kielitutkinto_sv", "valtionhallinnon_kielitutkinto_sv",
                "yleinen_kielitutkinto_fi", "valtionhallinnon_kielitutkinto_fi");

        driver.findElement(new By.ById("nav-henkilotiedot")).click();
        setNativeLanguage(NATIVE_LANGUAGE_SV);
        nextPhase(); // Koulutustausta
        setPerusopetuksenKieli(NATIVE_LANGUAGE_SV);
        nextPhase();
        nextPhase(); // Osaaminen
        elementsPresentByName("yleinen_kielitutkinto_fi", "valtionhallinnon_kielitutkinto_fi");
        findByIdAndClick("yleinen_kielitutkinto_fi_true", "valtionhallinnon_kielitutkinto_fi_true");
        nextPhase();
        fillInRestOfThePhasesAndCheckTheOID();
    }


    private void fillInArvosanatTheme() {
        driver.findElement(new By.ById("arvosanatTheme"));
        driver.findElement(new By.ById("KielitaitokysymyksetTheme"));
        select();
    }

    private void fillInRestOfThePhasesAndCheckTheOID() {
        // Lisätiedot
        clickAllElementsByXPath("//input[@type='checkbox']");

        nextPhase();
        setValue("tyokokemuskuukaudet", "2");
        WebElement asiointikieli_suomi = driver.findElement(new By.ById("asiointikieli_suomi"));
        asiointikieli_suomi.click();

        nextPhase();

        // Esikatselu
        nextPhase();
        driver.findElement(By.id("submit_confirm")).click();

        String oid = driver.findElement(new By.ByClassName("number")).getText();
        assertTrue(oid.startsWith("1.2.3.4.5"));
    }


    private void fillInTheHenkilotiedotPhase(final String aidinkieli) {
        setValue("Sukunimi", "Ankka");
        setValue("Etunimet", "Aku Kalle");
        setValue("Kutsumanimi", "A");
        setValue("Henkilotunnus", "150520-111E");
        setValue("Sähköposti", "aku.ankka@ankkalinna.al");
        setValue("matkapuhelinnumero1", "0501000100");
        setNativeLanguage(aidinkieli);
        setValue("asuinmaa", "FI");
        setValue("kotikunta", "jalasjarvi");
        setValue("lahiosoite", "Katu 1");
        setValue("Postinumero", "00100");
    }

    private void fillInTheKoulutustaustaPhase(final String opetuskieli) {
        findByIdAndClick("millatutkinnolla_tutkinto1", "suorittanut1", "osallistunut_ei");
        findById("paattotodistusvuosi_peruskoulu");
        setPerusopetuksenKieli(opetuskieli);
        setValue("paattotodistusvuosi_peruskoulu", "2012");
    }

    private void fillInTheHakutoiveetPhase() {
        driver.findElement(By.id("preference1-Opetuspiste"));
        selenium.typeKeys("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@value='Kaivosalan perustutkinto, pk']")).click();
        driver.findElements(By.name("preference1-Harkinnanvarainen")).get(1).click();
        Select followUpSelect = new Select(driver.findElement(new By.ById("preference1 - harkinnanvarainen_jatko")));
        followUpSelect.selectByIndex(0);
    }


    private void setPerusopetuksenKieli(final String opetuskieli) {
        setValue("perusopetuksen_kieli", opetuskieli);
    }

    private void setNativeLanguage(final String aidinkieli) {
        setValue("aidinkieli", aidinkieli);
    }
}
