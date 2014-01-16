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

package fi.vm.sade.haku.oppija.hakemus.resource;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * API for accessing applications.
 *
 * @author Hannu Lyytikainen
 */
@Component
@Path("/applications")
public class ApplicationResource {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSystemService applicationSystemService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResource.class);
    private static final String OID = "oid";

    public ApplicationResource() {
    }

    @Autowired
    public ApplicationResource(ApplicationService applicationService, ApplicationSystemService applicationSystemService) {
        this.applicationService = applicationService;
        this.applicationSystemService = applicationSystemService;
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD')")
    public Application getApplicationByOid(@PathParam(OID) String oid) {
        LOGGER.debug("Getting application by oid : {}", oid);
        try {
            return applicationService.getApplicationByOid(oid);
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, "Could not find requested application", e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD')")
    public ApplicationSearchResultDTO findApplications(@DefaultValue(value = "") @QueryParam("q") String query,
                                                       @QueryParam("appState") List<String> state,
                                                       @QueryParam("aoid") String aoid,
                                                       @QueryParam("lopoid") String lopoid,
                                                       @QueryParam("asId") String asId,
                                                       @QueryParam("asSemester") String asSemester,
                                                       @QueryParam("asYear") String asYear,
                                                       @QueryParam("aoOid") String aoOid,
                                                       @QueryParam("discretionaryOnly") Boolean discretionaryOnly,
                                                       @QueryParam("sendingSchoolOid") String sendingSchoolOid,
                                                       @QueryParam("sendingClass") String sendingClass,
                                                       @DefaultValue(value = "0") @QueryParam("start") int start,
                                                       @DefaultValue(value = "100") @QueryParam("rows") int rows) {
//        LOGGER.debug("Finding applications q:{}, state:{}, aoid:{}, lopoid:{}, asId:{}, aoOid:{}, start:{}, rows: {}, " +
//                "asSemester: {}, asYear: {}, discretionaryOnly: {}, sendingSchoolOid: {}, sendingClass: {}",
//                new String[] {query, state, aoid, lopoid, asId, aoOid, start, rows, asSemester, asYear,
//                        discretionaryOnly, sendingSchoolOid, sendingClass});

        List<String> asIds = new ArrayList<String>();
        if (isNotEmpty(asId)) {
            asIds.add(asId);
        }
        if (isNotEmpty(asSemester) || isNotEmpty(asYear)) {
            asIds.addAll(applicationSystemService.findByYearAndSemester(asSemester, asYear));
        }
        for (String s : asIds) {
            LOGGER.debug("asId: {}", s);
        }
        return applicationService.findApplications(
                query, new ApplicationQueryParameters(state, asIds, aoid, lopoid, aoOid, discretionaryOnly,
                sendingSchoolOid, sendingClass, start, rows, "fullName", 1));
    }

    @GET
    @Path("list/{orderBy}/{orderDir}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD')")
    public ApplicationSearchResultDTO findApplicationsOrdered(@PathParam("orderBy") String orderBy,
                                                              @PathParam("orderDir") String orderDir,
                                                              @DefaultValue(value = "") @QueryParam("q") String query,
                                                              @QueryParam("appState") List<String> state,
                                                              @QueryParam("aoid") String aoid,
                                                              @QueryParam("lopoid") String lopoid,
                                                              @QueryParam("asId") String asId,
                                                              @QueryParam("asSemester") String asSemester,
                                                              @QueryParam("asYear") String asYear,
                                                              @QueryParam("aoOid") String aoOid,
                                                              @QueryParam("discretionaryOnly") Boolean discretionaryOnly,
                                                              @QueryParam("sendingSchoolOid") String sendingSchoolOid,
                                                              @QueryParam("sendingClass") String sendingClass,
                                                              @DefaultValue(value = "0") @QueryParam("start") int start,
                                                              @DefaultValue(value = "100") @QueryParam("rows") int rows) {
//        LOGGER.debug("Finding applications q:{}, state:{}, aoid:{}, lopoid:{}, asId:{}, aoOid:{}, start:{}, rows: {}, " +
//                "asSemester: {}, asYear: {}, discretionaryOnly: {}, sendingSchoolOid: {}, sendingClass: {}",
//                query, state, aoid, lopoid, asId, aoOid, start, rows, asSemester, asYear, discretionaryOnly, sendingSchoolOid, sendingClass);

        int realOrderDir = "desc".equals(orderDir) ? -1 : 1;

        List<String> asIds = new ArrayList<String>();
        if (isNotEmpty(asId)) {
            asIds.add(asId);
        }
        if (isNotEmpty(asSemester) || isNotEmpty(asYear)) {
            asIds.addAll(applicationSystemService.findByYearAndSemester(asSemester, asYear));
        }
        for (String s : asIds) {
            LOGGER.debug("asId: {}", s);
        }
        return applicationService.findApplications(
                query, new ApplicationQueryParameters(state, asIds, aoid, lopoid, aoOid, discretionaryOnly,
                sendingSchoolOid, sendingClass, start, rows, orderBy, realOrderDir));
    }

    @GET
    @Path("{oid}/{key}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD')")
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
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public void putApplicationAdditionalInfoKeyValue(@PathParam(OID) String oid,
                                                     @PathParam("key") String key,
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


}
