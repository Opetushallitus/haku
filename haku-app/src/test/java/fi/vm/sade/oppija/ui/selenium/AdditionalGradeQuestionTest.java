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

import static org.junit.Assert.assertTrue;


/**
 * @author Hannu Lyytikainen
 */
public class AdditionalGradeQuestionTest extends DummyModelBaseItTest {

    public static final String OPETUSPISTE = "FAKTIA, Espoo op";
    public static final String KOULUTUSTAUSTA_PHASE_ID = "koulutustausta";

    @Test
    public void testAdditionalSubjects() {
        navigateToPhase(KOULUTUSTAUSTA_PHASE_ID);

        findByIdAndClick("millatutkinnolla_tutkinto1", "paattotodistusvuosi_peruskoulu", "suorittanut1", "suorittanut2", "suorittanut3", "suorittanut4", "osallistunut_false");
        setValue("perusopetuksen_kieli", "SV");
        setValue("paattotodistusvuosi_peruskoulu", "2013");

        nextPhase();

        // select a LOI

        setValue("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText(OPETUSPISTE)).click();
        driver.findElement(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']")).click();
        findByIdAndClick("preference1-discretionary_false");
        findByIdAndClick("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys_true");
        findByIdAndClick("preference1_sora_terveys_false");
        findByIdAndClick("preference1_sora_oikeudenMenetys_false");

        nextPhase();
        driver.findElement(By.xpath("//table[@id='gradegrid-table']"));
        assertTrue(driver.findElements(By.xpath("//table[@id='gradegrid-table']/tbody/tr")).size() > 10);


    }

}
