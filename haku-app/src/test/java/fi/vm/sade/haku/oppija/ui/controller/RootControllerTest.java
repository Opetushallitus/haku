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

package fi.vm.sade.haku.oppija.ui.controller;

import org.glassfish.jersey.server.mvc.Viewable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RootControllerTest {


    private RootController rootController;

    @Before
    public void setUp() throws Exception {
        rootController = new RootController();
    }

    @Test
    public void testGetIndexView() throws Exception {
        Viewable frontPage = rootController.getFrontPage();
        assertEquals(frontPage.getTemplateName(), RootController.INDEX_VIEW);
    }
}
