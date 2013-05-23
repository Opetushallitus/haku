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
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import org.junit.Before;
import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author Hannu Lyytikainen
 */
public class FormIT extends AbstractFormTest {

    @Before
    public void setUp() throws Exception {
        FormServiceMockImpl dummyMem = new FormServiceMockImpl(ASID, AOID);
        updateModelAndCreateFormModelHelper(dummyMem.getModel());
    }

    @Test
    public void testApplicationPeriod() {
        beginAt("/lomake");
        assertLinkPresent(ASID);
    }

    @Test
    public void testForm() throws Exception {
        beginAt("/lomake/" + ASID);
        assertLinkPresent("yhteishaku");
    }

    @Test
    public void testCategory() throws Exception {
        beginAt("/lomake/" + ASID + "/yhteishaku/henkilotiedot");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkNotPresent("nav-koulutustausta");
        gotoPage("/lomake/" + ASID + "/yhteishaku/koulutustausta");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkPresent("nav-koulutustausta");
    }
}
