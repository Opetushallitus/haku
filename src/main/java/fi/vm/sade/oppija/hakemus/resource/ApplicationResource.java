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

package fi.vm.sade.oppija.hakemus.resource;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * API for accessing applications.
 *
 * @author Hannu Lyytikainen
 */
@Component
@Path("/applications")
public class ApplicationResource {

    private ApplicationService applicationService;

    @Autowired
    public ApplicationResource(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Application> getApplications(@QueryParam("asid") String asId) {

        List<Application> applications;
        if (asId != null) {
            // retrieve applications related to a single application system
            return applications = applicationService.getApplicationsByApplicationSystem(asId);
        } else {
            throw new JSONException(Response.Status.BAD_REQUEST, "Invalid application system id argument");
        }
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Application getApplication(@PathParam("oid") String oid) {

        try {
            return applicationService.getApplication(oid);
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, "Could not find requested application");

        }

    }

}
