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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jukka
 * @version 10/11/128:55 AM}
 * @since 1.1
 */

@Controller
@RequestMapping(value = "/", method = RequestMethod.GET)
public class RootController {

    public static final String INDEX_VIEW = "index";
    public static final String LOCALE_VIEW = "locale";
    public static final String LOCALE_MODEL_NAME = "locale";
    public static final String LOCALE_MODEL_VALUE_FI = "fi";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getFrontPage() {
        return new ModelAndView(INDEX_VIEW);
    }

    @RequestMapping(value = "/fi", method = RequestMethod.GET)
    public ModelAndView selectLocale() {
        final ModelAndView modelAndView = new ModelAndView(LOCALE_VIEW);
        modelAndView.addObject(LOCALE_MODEL_NAME, LOCALE_MODEL_VALUE_FI);
        return modelAndView;
    }

    @RequestMapping(value = "/en", method = RequestMethod.GET)
    public ModelAndView selectEnLocale() {
        return new ModelAndView(LOCALE_VIEW);
    }

    @RequestMapping(value = "/sv", method = RequestMethod.GET)
    public ModelAndView selectSVLocale() {
        return new ModelAndView(LOCALE_VIEW);
    }

}
