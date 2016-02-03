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

/**
 * @author Hannu Lyytikainen
 */
public class GradeGridIT extends DummyModelBaseItTest {


    @Test
    public void testAddLanguage() {
        navigateToFirstPhase();
        fillOut(defaultValues.henkilotiedot);

        nextPhase(OppijaConstants.PHASE_EDUCATION);
        fillOut(defaultValues.koulutustausta_lk);
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        typeWithoutTab("preference1-Opetuspiste", "Esp");

        clickLinkByText(DefaultValues.OPETUSPISTE);
        findByAndAjaxClick(By.xpath("//option[@data-id='1.2.246.562.14.79893512065']"));

        fillOut(defaultValues.preference1);

        nextPhase(OppijaConstants.PHASE_GRADES);

        findByIdAndClick("nativeLanguage");
        findByIdAndClick("additionalLanguages");
    }
}
