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

package fi.vm.sade.haku.oppija.ui.selenium;

import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Test for education institute preferences
 *
 * @author Mikko Majapuro
 */
public class HakutoiveetIT extends DummyModelBaseItTest {

    @Test
    public void testEducationPreferenceAdditionalQuestion() throws InterruptedException {
        toApplicationOptionPhase();
        findById("preference1-Opetuspiste");
        typeWithoutTab("preference1-Opetuspiste", "Esp");
        findByAndAjaxClick(By.linkText("FAKTIA, Espoo op"));
        findByAndAjaxClick(By.xpath("//option[@value='Kaivosalan perustutkinto, pk']"));
        isTextPresent("Kaivosalan perustutkinto, Kaivosalan koulutusohjelma");
        clickByNameAndValue("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "true");
        clickByNameAndValue("preference1_sora_terveys", "false");
        clickByNameAndValue("preference1_sora_oikeudenMenetys", "false");
        findByAndAjaxClick(By.xpath("//button[@class='right']"));
    }

    @Test
    public void testNonExistingEducationPreferenceNotAutocompleted() throws InterruptedException {
        toApplicationOptionPhase();
        typeWithoutTab("preference1-Opetuspiste", "Eso");
        elementsNotPresentBy(By.linkText("FAKTIA, Espoo op"));
    }

    private void toApplicationOptionPhase() {
        navigateToFirstPhase();
        fillOut(defaultValues.henkilotiedot);
        nextPhase(OppijaConstants.PHASE_EDUCATION);
        fillOut(defaultValues.koulutustausta_pk);
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);
    }

}
