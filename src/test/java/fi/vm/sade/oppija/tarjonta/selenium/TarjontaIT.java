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

package fi.vm.sade.oppija.tarjonta.selenium;

import fi.vm.sade.oppija.haku.selenium.AbstractSeleniumBase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TarjontaIT extends AbstractSeleniumBase {

    @Before
    public void setUp() throws Exception {
        seleniumHelper.getDriver().get(getBaseUrl() + "/index/update");
        assertTrue(seleniumHelper.getSelenium().isTextPresent("true"));
    }

    @Test
    public void testTarjonta() throws Exception {
        seleniumHelper.getDriver().get(getBaseUrl() + "/tarjontatiedot");
        seleniumHelper.getSelenium().type("text", "perustutkinto");
        seleniumHelper.getSelenium().click("btn-search");
        boolean elementPresent = seleniumHelper.getSelenium().isTextPresent("Koulutuksia (1 kpl)");
        assertTrue(elementPresent);
    }

    @Test
    public void testTarjontaKuvaus() throws Exception {
        seleniumHelper.getDriver().get(getBaseUrl() + "/tarjontatiedot/776");
        boolean elementPresent = seleniumHelper.getSelenium().isTextPresent("Ensihoidon koulutusohjelma");
        assertTrue(elementPresent);
    }

    @Test
    public void testPuuttuvaKuvaus() throws Exception {
        seleniumHelper.getDriver().get(getBaseUrl() + "/tarjontatiedot/0");
        boolean elementPresent = seleniumHelper.getSelenium().isTextPresent("Pyytämääsi resurssia ei löytynyt");
        assertTrue(elementPresent);
    }


}
