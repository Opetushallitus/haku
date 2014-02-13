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
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FormIT extends DummyModelBaseItTest {

    @Test
    public void testApplicationSystem() {

        navigateTo(getBaseUrl() + "lomake");

        findById(ASID);
    }


    @Test
    public void testPhase() throws Exception {
        navigateToFirstPhase();
        findById("nav-henkilotiedot");
    }

    protected void elementsPresent(String... locations) {
        for (String location : locations) {
            assertTrue("Could not find element " + location, seleniumContainer.getSelenium().isElementPresent(location));
        }
    }
}
