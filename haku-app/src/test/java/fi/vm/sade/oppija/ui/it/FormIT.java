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


import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;

import org.junit.Before;
import org.junit.Test;

import fi.vm.sade.oppija.common.it.AbstractFormTest;
import fi.vm.sade.oppija.lomake.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;

/**
 * @author Hannu Lyytikainen
 */
public class FormIT extends AbstractFormTest {

    @Before
    public void setUp() throws Exception {
        FormServiceMockImpl dummyMem = new FormServiceMockImpl();
        updateModelAndCreateFormModelHelper(dummyMem.getModel());
    }

    @Test
    public void testApplicationPeriod() {
        beginAt("/lomake");
        assertLinkPresent(Yhteishaku2013.ASID);
    }

    @Test
    public void testForm() throws Exception {
        beginAt("/lomake/" + Yhteishaku2013.ASID);
        assertLinkPresent("yhteishaku");
    }

    @Test
    public void testCategory() throws Exception {
        beginAt("/lomake/" + Yhteishaku2013.ASID + "/yhteishaku/henkilotiedot");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkNotPresent("nav-koulutustausta");
        gotoPage("/lomake/" + Yhteishaku2013.ASID + "/yhteishaku/koulutustausta");
        assertLinkPresent("nav-henkilotiedot");
        assertLinkPresent("nav-koulutustausta");
    }
}
