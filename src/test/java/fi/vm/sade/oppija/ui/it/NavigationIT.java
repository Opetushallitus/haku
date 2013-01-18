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

import fi.vm.sade.oppija.common.it.AbstractRemoteTest;
import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class NavigationIT extends AbstractRemoteTest {

    @Before
    public void setUp() throws Exception {
        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();
        initModel(dummyMem.getModel());
    }

    @Test
    public void testNavigationExists() throws IOException {
        beginAt("/lomake/Yhteishaku/yhteishaku/henkilotiedot");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkPresent("nav-koulutustausta");
    }

    @Test
    public void testFirstGategoryNavButtons() throws IOException {
        beginAt("/lomake/Yhteishaku/yhteishaku/henkilotiedot");
        assertElementPresentByXPath("//button[@class='right']");
        assertElementNotPresentByXPath("//button[@class='left']");
    }

    @Test
    public void testMiddleGategoryNavButtons() throws IOException {
        beginAt("/lomake/Yhteishaku/yhteishaku/koulutustausta");
        assertElementPresentByXPath("//button[@class='right']");
        assertElementPresentByXPath("//button[@class='left']");
    }

    @Test
    public void testLastGategoryNavButtons() throws IOException {
        beginAt("/lomake/Yhteishaku/yhteishaku/esikatselu");
        assertElementNotPresentByXPath("//button[@name='nav-next']");
        assertElementPresentByXPath("//button[@class='left']");
        assertElementPresentByXPath("//button[@class='right']");
    }
}
