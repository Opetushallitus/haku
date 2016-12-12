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

import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;
import org.openqa.selenium.By;

public class HAK153IT extends DummyModelBaseItTest {

    @Test
    public void testSoraAndUrheilijanLisakysymys() {
        navigateToFirstPhase();
        fillOut(defaultValues.henkilotiedot);

        nextPhase(OppijaConstants.PHASE_EDUCATION);
        fillOut(defaultValues.koulutustausta_pk);
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        typeWithoutTab("preference1-Opetuspiste", "Esp");
        clickLinkByText("FAKTIA, Espoo op");
        click(By.cssSelector("#preference1-Koulutus option[value='Kaivosalan perustutkinto, pk']"));
        findBy(By.cssSelector("a.popup-link[href='#'][data-po-show='sora-popup']"));
        click(By.cssSelector("#preference1-Koulutus option[value='Musiikkiteknologian koulutusohjelma, pk (Musiikkialan perustutkinto)']"));
        elementsNotPresentBy(By.cssSelector("a.popup-link[href='#'][data-po-show='sora-popup']"));
        click(By.cssSelector("#preference1-Koulutus option[value='Kaivosalan perustutkinto, pk']"));
        clickByNameAndValue("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "false");
        clickByNameAndValue("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "true");
    }


}
