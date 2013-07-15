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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicantDTO;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API for accessing applications.
 *
 * @author Hannu Lyytikainen
 */
@Component
@Path("/applications")
public class ApplicationResource {

    private ApplicationService applicationService;
    private ConversionService conversionService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResource.class);
    private static final String OID = "oid";

    @Autowired
    public ApplicationResource(ApplicationService applicationService, ConversionService conversionService) {
        this.applicationService = applicationService;
        this.conversionService = conversionService;
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Application getApplicationByOid(@PathParam(OID) String oid) {
        LOGGER.debug("Getting application by oid : {}", oid);
        try {
            return applicationService.getApplication(oid);
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, "Could not find requested application", e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationSearchResultDTO findApplications(@DefaultValue("") @QueryParam("q") String query,
                                              @DefaultValue("") @QueryParam("appState") String appState,
                                              @DefaultValue("") @QueryParam("appPreference") String appPreference,
                                              @DefaultValue("") @QueryParam("lopoid") String lopoid,
                                              @DefaultValue(value = "0") @QueryParam("start") int start,
                                              @DefaultValue(value = "100") @QueryParam("rows") int rows) {
        LOGGER.debug("Finding applications q:{}, appState:{}, appPreference:{}, lopoid:{}",
                query, appState, appPreference, lopoid);
        return applicationService.findApplications(
                query, new ApplicationQueryParameters(appState, appPreference, lopoid, start, rows));
    }

    @GET
    @Path("{oid}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getApplicationKeyValue(@PathParam(OID) String oid, @PathParam("key") String key) {
        Map<String, String> keyValue = new HashMap<String, String>();

        try {
            String value = applicationService.getApplicationKeyValue(oid, key);
            keyValue.put(key, value);
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, e.getMessage(), e);
        }
        return keyValue;
    }

    @PUT
    @Path("{oid}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public void putApplicationAdditionalInfoKeyValue(@PathParam(OID) String oid, @PathParam("key") String key,
                                                     @QueryParam("value") String value) {
        try {
            applicationService.putApplicationAdditionalInfoKeyValue(oid, key, value);
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new JSONException(Response.Status.CONFLICT, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new JSONException(Response.Status.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GET
    @Path("applicant/{asId}/{aoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApplicantDTO> findApplicants(@PathParam("asId") String asId, @PathParam("aoId") String aoId) {
        LOGGER.debug("Finding applicants asId:{}, aoID:{}", asId, aoId);
        List<Application> applications = applicationService.getApplicationsByApplicationSystemAndApplicationOption(asId, aoId);
        return Lists.transform(applications, new Function<Application, ApplicantDTO>() {
            @Override
            public ApplicantDTO apply(Application application) {
                return conversionService.convert(application, ApplicantDTO.class);
            }
        });
    }

}
