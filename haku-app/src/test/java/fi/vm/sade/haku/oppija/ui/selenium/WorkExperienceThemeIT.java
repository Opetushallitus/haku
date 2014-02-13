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

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import org.junit.Test;

/**
 * @author Hannu Lyytikainen
 */
public class WorkExperienceThemeIT extends DummyModelBaseItTest {

    @Test
    public void testWorkExperienceShown() {
        gotoHakutoiveet("010113-668B");
        click("//option[@data-id='1.2.246.562.14.79893512065']");
        fillOut(defaultValues.preference1);

        nextPhase();

        select();
        selectByValue("PK_AI_OPPIAINE", "FI");
        selectByValue("PK_A1_OPPIAINE", "EN");
        selectByValue("PK_B1_OPPIAINE", "SE");

        nextPhase();
        findById("TYOKOKEMUSKUUKAUDET");
    }

    @Test
    public void testWorkExperienceNotShown() {
        gotoHakutoiveet("010113A668B");
        click("//option[@data-id='1.2.246.562.14.79893512065']");
        clickByNameAndValue("preference1-discretionary", "false");

        nextPhase();
        select();
        nextPhase();

        elementsNotPresent("TYOKOKEMUSKUUKAUDET");
    }

    private void gotoHakutoiveet(final String hetu) {
        navigateToFirstPhase();
        fillOut(defaultValues.getHenkilotiedot(ImmutableMap.of("Henkilotunnus", hetu)));

        nextPhase();
        fillOut(defaultValues.koulutustausta_pk);

        nextPhase();

        typeWithoutTab("preference1-Opetuspiste", "Esp");
        clickLinkByText("FAKTIA, Espoo op");
    }
}
