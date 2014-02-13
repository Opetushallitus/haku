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

package fi.vm.sade.haku.oppija.ui.it;

import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.oppija.ui.selenium.DefaultValues;
import org.junit.Test;

import java.io.IOException;

public class NavigationIT extends DummyModelBaseItTest {

    @Test
    public void testNavigationExists() throws IOException {

        navigateToFirstPhase();
        elementsPresent("//a[@id='nav-henkilotiedot']");
        elementsPresent("//li/span/span[contains(text(),'2')]");
        elementsPresent("//li/span/span[contains(text(),'3')]");
        elementsPresent("//li/span/span[contains(text(),'4')]");
        elementsPresent("//li/span/span[contains(text(),'5')]");
        elementsPresent("//li/span/span[contains(text(),'6')]");
        elementsPresent("//li/span/span[contains(text(),'7')]");

        elementsPresent("//button[@class='right']");
        elementsNotPresent("//button[@class='left']");
        fillOut(defaultValues.henkilotiedot);
        nextPhase();

        elementsPresent("//a[@id='nav-henkilotiedot']");
        elementsPresent("//a[@id='nav-koulutustausta']");
        elementsPresent("//li/span/span[contains(text(),'3')]");
        elementsPresent("//li/span/span[contains(text(),'4')]");
        elementsPresent("//li/span/span[contains(text(),'5')]");
        elementsPresent("//li/span/span[contains(text(),'6')]");
        elementsPresent("//li/span/span[contains(text(),'7')]");

        elementsPresent("//button[@class='right']");
        elementsPresent("//button[@class='left']");
        fillOut(defaultValues.koulutustausta_pk);
        nextPhase();

        elementsPresent("//a[@id='nav-henkilotiedot']");
        elementsPresent("//a[@id='nav-koulutustausta']");
        elementsPresent("//a[@id='nav-hakutoiveet']");
        elementsPresent("//li/span/span[contains(text(),'4')]");
        elementsPresent("//li/span/span[contains(text(),'5')]");
        elementsPresent("//li/span/span[contains(text(),'6')]");
        elementsPresent("//li/span/span[contains(text(),'7')]");

        elementsPresent("//button[@class='right']");
        elementsPresent("//button[@class='left']");

        typeWithoutTab("preference1-Opetuspiste", "Esp");
        clickLinkByText(DefaultValues.OPETUSPISTE);
        findByXPath("//option[@data-id='1.2.246.562.14.79893512065']").click();

        fillOut(defaultValues.preference1);

        nextPhase();

        select();
        selectByValue("PK_A1_OPPIAINE", "EN");
        selectByValue("PK_B1_OPPIAINE", "SV");
        elementsPresent("//a[@id='nav-henkilotiedot']");
        elementsPresent("//a[@id='nav-koulutustausta']");
        elementsPresent("//a[@id='nav-hakutoiveet']");
        elementsPresent("//a[@id='nav-osaaminen']");
        elementsPresent("//li/span/span[contains(text(),'5')]");
        elementsPresent("//li/span/span[contains(text(),'6')]");
        elementsPresent("//li/span/span[contains(text(),'7')]");
        elementsPresent("//button[@class='right']");
        elementsPresent("//button[@class='left']");
        nextPhase();

        elementsPresent("//a[@id='nav-henkilotiedot']");
        elementsPresent("//a[@id='nav-koulutustausta']");
        elementsPresent("//a[@id='nav-hakutoiveet']");
        elementsPresent("//a[@id='nav-osaaminen']");
        elementsPresent("//a[@id='nav-lisatiedot']");
        elementsPresent("//li/span/span[contains(text(),'6')]");
        elementsPresent("//li/span/span[contains(text(),'7')]");
        elementsPresent("//button[@class='right']");
        elementsPresent("//button[@class='left']");

        fillOut(defaultValues.lisatiedot);
        nextPhase();
        elementsPresent("//a[@id='nav-henkilotiedot']");
        elementsPresent("//a[@id='nav-koulutustausta']");
        elementsPresent("//a[@id='nav-hakutoiveet']");
        elementsPresent("//a[@id='nav-osaaminen']");
        elementsPresent("//a[@id='nav-lisatiedot']");
        elementsPresent("//li/a[@class='current']");
        elementsPresent("//li//*[contains(text(),'7')]");

        elementsPresent("//button[@class='right']");
        elementsPresent("//button[@class='left']");

        nextPhase();
        back();
        back();
        back();
        back();
        back();
        nextPhase();
        nextPhase();
        nextPhase();
        nextPhase();
        nextPhase();
        nextPhase();
        findByIdAndClick("submit_confirm");

        elementsPresent("//li/span/span[contains(text(),'1')]");
        elementsPresent("//li/span/span[contains(text(),'2')]");
        elementsPresent("//li/span/span[contains(text(),'3')]");
        elementsPresent("//li/span/span[contains(text(),'4')]");
        elementsPresent("//li/span/span[contains(text(),'5')]");
        elementsPresent("//li/span/span[contains(text(),'6')]");
        elementsPresent("//li/a[@class='current']/span[contains(text(),'7')]");

    }

    @Test
    public void testApplicationSystemNotFound() throws Exception {
        navigateToPath("lomake/nonexistingapplicationsysytemid");
        findByXPath("//*[contains(.,'Tapahtui odottamaton virhe.')]");
    }
}
