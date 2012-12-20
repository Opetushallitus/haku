/*
 *
 *  * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *  *
 *  * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 *  * soon as they will be approved by the European Commission - subsequent versions
 *  * of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * European Union Public Licence for more details.
 *
 */

package fi.vm.sade.oppija.application.process.controller;

import fi.vm.sade.oppija.ExceptionController;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.application.process.service.ApplicationProcessStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Mikko Majapuro
 */
@Controller
@RequestMapping(value = "/applicationProcessStates", method = RequestMethod.GET)
@Secured("ROLE_OFFICER")
public class ApplicationProcessStateController extends ExceptionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationProcessStateController.class);

    @Autowired
    ApplicationProcessStateService applicationProcessStateService;

    @RequestMapping(value = "{oid:.+}", method = {RequestMethod.GET})
    @ResponseBody
    public ApplicationProcessState getApplicationProcessState(@PathVariable String oid) {
        LOGGER.debug("getApplicationProcessState oid {}", oid);
        return applicationProcessStateService.get(oid);
    }

    @RequestMapping(value = "/active/{oid:.+}", method = {RequestMethod.PUT})
    @ResponseBody
    public void putToActiveProcessStates(@PathVariable String oid) {
        LOGGER.debug("put to active process state oid {}", oid);
        applicationProcessStateService.setApplicationProcessStateStatus(oid, ApplicationProcessStateStatus.ACTIVE);
    }

    @RequestMapping(value = "/cancelled/{oid:.+}", method = {RequestMethod.PUT})
    @ResponseBody
    public void putToCancelledProcessStates(@PathVariable String oid) {
        LOGGER.debug("put to cancelled process state oid {}", oid);
        applicationProcessStateService.setApplicationProcessStateStatus(oid, ApplicationProcessStateStatus.CANCELLED);
    }
}
