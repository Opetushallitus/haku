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

package fi.vm.sade.oppija.hakemus.controller;

import fi.vm.sade.oppija.ExceptionController;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jukka
 * @version 10/26/122:14 PM}
 * @since 1.1
 */
@Controller
@RequestMapping(value = "/hakemukset", method = RequestMethod.GET)
@Secured("ROLE_OFFICER")
public class ApplicationController extends ExceptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    protected ApplicationService applicationService;

    @RequestMapping(method = {RequestMethod.GET})
    @ResponseBody
    public List<Application> searchApplications(@RequestParam(value = "term", required = true) String term) throws ResourceNotFoundException {
        //TODO implement this
        List<Application> result = new ArrayList<Application>();
        Application app = applicationService.getApplication(term);
        result.add(app);
        return result;
    }

    @RequestMapping(value = "hakemus/{oid:.+}", method = {RequestMethod.GET})
    @ResponseBody
    public Application getApplication(@PathVariable String oid) throws ResourceNotFoundException {
        LOGGER.debug("oid {}", oid);
        return applicationService.getApplication(oid);
    }
}
