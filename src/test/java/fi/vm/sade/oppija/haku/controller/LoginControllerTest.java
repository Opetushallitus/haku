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

import fi.vm.sade.oppija.haku.service.UserHolder;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;

public class LoginControllerTest {

    private LoginController loginController = new LoginController(new UserHolder());

    @Test
    public void testLogin() throws Exception {
        assertEquals(LoginController.TOP_LOGIN_VIEW, loginController.login());
    }

    @Test
    public void testLoginerrorView() throws Exception {
        ModelAndView modelAndView = loginController.loginerror();
        assertEquals(LoginController.LOGIN_VIEW, modelAndView.getViewName());
    }

    @Test
    public void testLoginerrorModel() throws Exception {
        ModelAndView modelAndView = loginController.loginerror();
        assertEquals(LoginController.ERROR_MODEL_VALUE, modelAndView.getModel().get(LoginController.ERROR_MODEL_KEY));
    }

    @Test
    public void testLogout() throws Exception {
        assertEquals(LoginController.LOGIN_VIEW, loginController.logout());
    }
}
