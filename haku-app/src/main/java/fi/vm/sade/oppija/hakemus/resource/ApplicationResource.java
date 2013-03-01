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

import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationDTO;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResource.class);

    @Autowired
    public ApplicationResource(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GET
    @Path("/process/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationDTO getApplication(@PathParam("oid") String oid) {

        try {
            return new ApplicationDTO(applicationService.getApplication(oid));
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, "Could not find requested application", e);

        }

    }

    @GET
    @Path("/process")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApplicationDTO> getApplicationsByAOId(@QueryParam("aoid") String aoid) {
        if (aoid != null) {
            // retrieve applications related to a single application system
            List<String> aoids = new ArrayList<String>();
            aoids.add(aoid);
            List<Application> applications = applicationService.getApplicationsByApplicationOption(aoids);
            List<ApplicationDTO> applicationDTOs = new ArrayList<ApplicationDTO>();
            for (Application application : applications) {
                applicationDTOs.add(new ApplicationDTO(application));
            }
            return applicationDTOs;
        } else {
            throw new JSONException(Response.Status.BAD_REQUEST, "Invalid application option id argument");
        }
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Application getApplicationByOid(@PathParam("oid") String oid) {
        LOGGER.debug("Getting application by oid : {}", oid);
        try {
            return applicationService.getApplication(oid);
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, "Could not find requested application", e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Application> findApplications(@DefaultValue("") @QueryParam("q") String query,
                                              @DefaultValue("") @QueryParam("appState") String appState,
                                              @DefaultValue("false") @QueryParam("fetchPassive") boolean fetchPassive,
                                              @DefaultValue("") @QueryParam("appPreference") String appPreference,
                                              @DefaultValue("") @QueryParam("lopOid") String lopOid) {
        LOGGER.debug("Finding applications q:{}, appState:{}, fetchPassive:{}, appPreference:{}", new Object[]{query, appState, fetchPassive, appPreference});
        List<Application> result = new ArrayList<Application>();
        result.addAll(applicationService.findApplications(query, new ApplicationQueryParameters(appState, fetchPassive, appPreference, lopOid)));
        return result;
    }
}
