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

package fi.vm.sade.oppija.lomake.controller;

import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;

public class RootControllerTest {

    private RootController rootController = new RootController();

    @Test
    public void testGetFrontPageView() throws Exception {
        ModelAndView frontPage = rootController.getFrontPage();
        assertEquals(frontPage.getViewName(), RootController.INDEX_VIEW);
    }

    @Test
    public void testSelectLocale() throws Exception {
        ModelAndView modelAndView = rootController.selectLocale();
        assertEquals(modelAndView.getModel().get(RootController.LOCALE_MODEL_NAME), RootController.LOCALE_MODEL_VALUE_FI);
    }

    @Test
    public void testSelectLocaleView() throws Exception {
        ModelAndView modelAndView = rootController.selectLocale();
        assertEquals(modelAndView.getViewName(), RootController.LOCALE_VIEW);
    }

    @Test
    public void testSelectEnLocaleView() throws Exception {
        ModelAndView modelAndView = rootController.selectEnLocale();
        assertEquals(modelAndView.getViewName(), RootController.LOCALE_VIEW);
    }

    @Test
    public void testSelectSVLocaleView() throws Exception {
        ModelAndView modelAndView = rootController.selectSVLocale();
        assertEquals(modelAndView.getViewName(), RootController.LOCALE_VIEW);

    }
}
