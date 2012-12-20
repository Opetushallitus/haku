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

package fi.vm.sade.oppija.ui.controller;

import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/oma")
@Secured("ROLE_USER")
public class PersonalServices {

    public static final Logger LOGGER = LoggerFactory.getLogger(PersonalServices.class);

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping
    public ModelAndView hautKoulutuksiin() {
        return getApplications();
    }

    @RequestMapping(value = "applications", method = RequestMethod.GET)
    public ModelAndView getApplications() {
        LOGGER.debug("getApplications");

        ModelAndView modelAndView = new ModelAndView("personal/template");
        List<ApplicationInfo> userApplicationInfo = applicationService.getUserApplicationInfo();
        modelAndView.addObject("section", "applications");
        modelAndView.addObject("presentPeriodApplications", userApplicationInfo);
        return modelAndView;
    }

}
