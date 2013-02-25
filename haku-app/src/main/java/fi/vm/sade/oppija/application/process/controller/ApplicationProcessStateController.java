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
package fi.vm.sade.oppija.application.process.controller;

import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.application.process.service.ApplicationProcessStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("applicationProcessStates")
@Controller
@Secured("ROLE_OFFICER")
public class ApplicationProcessStateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationProcessStateController.class);

    @Autowired
    ApplicationProcessStateService applicationProcessStateService;

    @GET
    @Path("{oid:.+}")
    public ApplicationProcessState getApplicationProcessState(@PathParam("oid") final String oid) { //NOSONAR
        LOGGER.debug("getApplicationProcessState oid {}", oid);
        return applicationProcessStateService.get(oid);
    }

    @PUT
    @Path("/active/{oid:.+}")
    public void putToActiveProcessStates(@PathParam("oid") final String oid) { //NOSONAR
        LOGGER.debug("put to active process state oid {}", oid);
        applicationProcessStateService.setApplicationProcessStateStatus(oid, ApplicationProcessStateStatus.ACTIVE);
    }

    @PUT
    @Path("/cancelled/{oid:.+}")
    public void putToCancelledProcessStates(@PathParam("oid") final String oid) { //NOSONAR
        LOGGER.debug("put to cancelled process state oid {}", oid);
        applicationProcessStateService.setApplicationProcessStateStatus(oid, ApplicationProcessStateStatus.CANCELLED);
    }
}
