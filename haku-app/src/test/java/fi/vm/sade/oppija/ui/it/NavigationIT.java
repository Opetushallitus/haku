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

import fi.vm.sade.oppija.common.it.AbstractFormTest;
import fi.vm.sade.oppija.lomakkeenhallinta.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class NavigationIT extends AbstractFormTest {

    @Before
    public void setUp() throws Exception {
        FormServiceMockImpl dummyMem = new FormServiceMockImpl();
        updateModelAndCreateFormModelHelper(dummyMem.getModel());
    }

    @Test
    public void testNavigationExists() throws IOException {
        beginAt("/lomake/" + Yhteishaku2013.ASID + "/yhteishaku/henkilotiedot");
        assertLinkPresent("nav-henkilotiedot");
        assertElementPresentByXPath("//span[@class='index']");
    }

    @Test
    public void testFirstGategoryNavButtons() throws IOException {
        beginAt("/lomake/" + Yhteishaku2013.ASID + "/yhteishaku/henkilotiedot");
        assertElementPresentByXPath("//button[@class='right']");
        assertElementNotPresentByXPath("//button[@class='left']");
    }

    @Test
    public void testMiddleGategoryNavButtons() throws IOException {
        beginAt("/lomake/" + Yhteishaku2013.ASID + "/yhteishaku/koulutustausta");
        assertElementPresentByXPath("//button[@class='right']");
        assertElementPresentByXPath("//button[@class='left']");
    }

    @Test
    public void testLastGategoryNavButtons() throws IOException {
        beginAt("/lomake/" + Yhteishaku2013.ASID + "/yhteishaku/esikatselu");
        assertElementNotPresentByXPath("//button[@name='nav-next']");
        assertElementPresentByXPath("//button[@class='left']");
        assertElementPresentByXPath("//button[@class='right']");
    }
}
