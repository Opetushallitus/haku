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

/**
 * @author jukka
 * @version 10/12/121:20 PM}
 * @since 1.1
 */

import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class LoginController {

    public static final String TOP_LOGIN_VIEW = "top/login";
    public static final String LOGIN_VIEW = "login";
    public static final String ERROR_MODEL_KEY = "error";
    public static final String ERROR_MODEL_VALUE = "true";

    private final UserHolder userHolder;

    @Autowired
    public LoginController(UserHolder userHolder) {
        this.userHolder = userHolder;
    }

    @RequestMapping(value = "/postLogin")
    public String postLogin(HttpSession session, Principal principal) {

        userHolder.login(new User(principal.getName()));
        session.setAttribute("username", principal.getName());
        return principal.getName().equals("admin") ? "redirect:admin" : "redirect:oma";

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return TOP_LOGIN_VIEW;

    }

    @RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
    public ModelAndView loginerror() {
        ModelAndView modelAndView = new ModelAndView(LOGIN_VIEW);
        modelAndView.addObject(ERROR_MODEL_KEY, ERROR_MODEL_VALUE);
        return modelAndView;

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        return LOGIN_VIEW;
    }

}
