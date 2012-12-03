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

package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.lomake.controller.ListsController;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;

public class ListsControllerTest {

    private ListsController listsController = new ListsController();

    @Test
    public void testGetUserNoteList() throws Exception {
        ModelAndView modelAndView = listsController.getUserNoteList();
        assertEquals(modelAndView.getViewName(), ListsController.NOTELIST_VIEW);
    }

    @Test
    public void testGetUserComparison() throws Exception {
        ModelAndView modelAndView = listsController.getUserComparison();
        assertEquals(modelAndView.getViewName(), ListsController.COMPARISON_VIEW);
    }

    @Test
    public void testAddApplicationOptionToNoteList() throws Exception {
        listsController.addApplicationOptionToNoteList("1");
    }

    @Test
    public void testAddApplicationOptionToComparison() throws Exception {
        listsController.addApplicationOptionToNoteList("1");
    }
}
