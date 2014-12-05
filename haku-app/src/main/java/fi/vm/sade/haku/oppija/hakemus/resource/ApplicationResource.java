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

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParametersBuilder;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

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

    @Autowired
    private ApplicationOptionService applicationOptionService;

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
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public Application getApplicationByOid(@PathParam(OID) String oid) {
        LOGGER.debug("Getting application by oid : {}", oid);
        try {
            Application application = applicationService.getApplicationByOid(oid);
            LOGGER.debug("Got applicatoin by oid : {}", application.getOid());
            return application;
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, "Could not find requested application", e);
        }
    }

    @GET
    @Path("excel")
    @Produces("application/vnd.ms-excel")
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public XlsModel getApplicationsByOids(@Context HttpServletRequest request,
                                          @QueryParam("asId") String asid,
                                          @QueryParam("aoid") String aoid,
                                          @QueryParam("aoidCode") String aoidCode,
                                          @QueryParam("q") @DefaultValue(value = "") String searchTerms,
                                          @QueryParam("appState") List<String> state,
                                          @QueryParam("preferenceChecked") Boolean preferenceChecked,
                                          @QueryParam("lopoid") String lopoid,
                                          @QueryParam("aoOid") String aoOid,
                                          @QueryParam("groupOid") String groupOid,
                                          @QueryParam("baseEducation") String baseEducation,
                                          @QueryParam("discretionaryOnly") Boolean discretionaryOnly,
                                          @QueryParam("primaryPreferenceOnly") Boolean primaryPreferenceOnly,
                                          @QueryParam("sendingSchoolOid") String sendingSchoolOid,
                                          @QueryParam("sendingClass") String sendingClass,
                                          @QueryParam("updatedAfter") DateParam updatedAfter,
                                          @QueryParam("start") @DefaultValue(value = "0") int start,
                                          @QueryParam("rows") @DefaultValue(value = "10000") int rows) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getApplicationSystem(asid);

        ApplicationQueryParameters queryParams = new ApplicationQueryParametersBuilder()
                .setSearchTerms(searchTerms)
                .setStates(state)
                .setPreferenceChecked(preferenceChecked)
                .setAsId(asid)
                .setAoId(StringUtils.trimToNull(aoidCode))
                .setGroupOid(groupOid)
                .setBaseEducation(baseEducation)
                .setLopOid(lopoid)
                .setAoOid(aoOid)
                .setDiscretionaryOnly(discretionaryOnly)
                .setPrimaryPreferenceOnly(primaryPreferenceOnly)
                .setSendingSchool(sendingSchoolOid)
                .setSendingClass(sendingClass)
                .setUpdatedAfter(updatedAfter != null ? updatedAfter.getDate() : null)
                .setStart(start)
                .setRows(rows)
                .setOrderBy("oid")
                .setOrderDir(1)
                .build();

        List<Map<String, Object>> applications = applicationService.findFullApplications(queryParams);
        Locale userLocale = (Locale) Config.get(request.getSession(), Config.FMT_LOCALE);
        ApplicationOption ao = applicationOptionService.get(aoOid);
        return new XlsModel(ao, activeApplicationSystem, applications, userLocale.getLanguage());
    }

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public List<Application> getApplicationsByOids(@QueryParam("oid") List<String> oids) {
        return getApplications(oids);
    }

    @POST
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes("application/json")
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public List<Application> getApplicationsByOidsPost(final List<String> oids) {
        return getApplications(oids);
    }

    @GET
    @Path("listfull")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public List<Map<String, Object>> findFullApplications(@DefaultValue(value = "") @QueryParam("q") String searchTerms,
                                                          @QueryParam("appState") List<String> state,
                                                          @QueryParam("preferenceChecked") Boolean preferenceChecked,
                                                          @QueryParam("aoid") String aoid,
                                                          @QueryParam("groupOid") String groupOid,
                                                          @QueryParam("baseEducation") String baseEducation,
                                                          @QueryParam("lopoid") String lopoid,
                                                          @QueryParam("asId") String asId,
                                                          @QueryParam("asSemester") String asSemester,
                                                          @QueryParam("asYear") String asYear,
                                                          @QueryParam("aoOid") String aoOid,
                                                          @QueryParam("discretionaryOnly") Boolean discretionaryOnly,
                                                          @QueryParam("primaryPreferenceOnly") Boolean primaryPreferenceOnly,
                                                          @QueryParam("sendingSchoolOid") String sendingSchoolOid,
                                                          @QueryParam("sendingClass") String sendingClass,
                                                          @QueryParam("updatedAfter") DateParam updatedAfter,
                                                          @DefaultValue(value = "0") @QueryParam("start") int start,
                                                          @DefaultValue(value = "100") @QueryParam("rows") int rows) {

        LOGGER.debug("findFullApplications start: {}", System.currentTimeMillis());
        List<String> asIds = new ArrayList<String>();
        if (isNotEmpty(asId)) {
            asIds.add(asId);
        } else if (isNotEmpty(asSemester) || isNotEmpty(asYear)) {
            List<String> applicationSystemIds = applicationSystemService.findByYearAndSemester(asSemester, asYear);
            if (applicationSystemIds.size() > 0)
                asIds.addAll(applicationSystemIds);
            else
                return new ArrayList<Map<String, Object>>(0);
        }

        ApplicationQueryParameters queryParams = new ApplicationQueryParametersBuilder()
                .setSearchTerms(searchTerms)
                .setStates(state)
                .setPreferenceChecked(preferenceChecked)
                .setAsIds(asIds)
                .setAoId(aoid)
                .setGroupOid(groupOid)
                .setBaseEducation(baseEducation)
                .setLopOid(lopoid)
                .setAoOid(aoOid)
                .setDiscretionaryOnly(discretionaryOnly)
                .setPrimaryPreferenceOnly(primaryPreferenceOnly)
                .setSendingSchool(sendingSchoolOid)
                .setSendingClass(sendingClass)
                .setUpdatedAfter(updatedAfter != null ? updatedAfter.getDate() : null)
                .setStart(start)
                .setRows(rows)
                .setOrderBy("oid")
                .setOrderDir(1)
                .build();

        List<Map<String, Object>> apps = applicationService.findFullApplications(queryParams);
        LOGGER.debug("findFullApplications done: {}", System.currentTimeMillis());
        return apps;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public ApplicationSearchResultDTO findApplications(@DefaultValue(value = "") @QueryParam("q") String query,
                                                       @QueryParam("appState") List<String> state,
                                                       @QueryParam("preferenceChecked") Boolean preferenceChecked,
                                                       @QueryParam("aoid") String aoid,
                                                       @QueryParam("groupOid") String groupOid,
                                                       @QueryParam("baseEducation") String baseEducation,
                                                       @QueryParam("lopoid") String lopoid,
                                                       @QueryParam("asId") String asId,
                                                       @QueryParam("asSemester") String asSemester,
                                                       @QueryParam("asYear") String asYear,
                                                       @QueryParam("aoOid") String aoOid,
                                                       @QueryParam("discretionaryOnly") Boolean discretionaryOnly,
                                                       @QueryParam("primaryPreferenceOnly") Boolean primaryPreferenceOnly,
                                                       @QueryParam("sendingSchoolOid") String sendingSchoolOid,
                                                       @QueryParam("sendingClass") String sendingClass,
                                                       @QueryParam("updatedAfter") DateParam updatedAfter,
                                                       @DefaultValue(value = "0") @QueryParam("start") int start,
                                                       @DefaultValue(value = "100") @QueryParam("rows") int rows) {

        return findApplicationsOrdered("fullName", "asc", query, state, preferenceChecked, aoid, groupOid, baseEducation, lopoid, asId,
                asSemester, asYear, aoOid, discretionaryOnly, primaryPreferenceOnly, sendingSchoolOid,
                sendingClass, updatedAfter, start, rows);
    }

    @GET
    @Path("listshort")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public ApplicationSearchResultDTO findApplicationsOrdered(@DefaultValue(value = "fullName") @QueryParam("orderBy") String orderBy,
                                                              @DefaultValue(value = "asc") @QueryParam("orderDir") String orderDir,
                                                              @DefaultValue(value = "") @QueryParam("q") String searchTerms,
                                                              @QueryParam("appState") List<String> state,
                                                              @QueryParam("preferenceChecked") Boolean preferenceChecked,
                                                              @QueryParam("aoidCode") String aoid,
                                                              @QueryParam("groupOid") String groupOid,
                                                              @QueryParam("baseEducation") String baseEducation,
                                                              @QueryParam("lopoid") String lopoid,
                                                              @QueryParam("asId") String asId,
                                                              @QueryParam("asSemester") String asSemester,
                                                              @QueryParam("asYear") String asYear,
                                                              @QueryParam("aoOid") String aoOid,
                                                              @QueryParam("discretionaryOnly") Boolean discretionaryOnly,
                                                              @QueryParam("primaryPreferenceOnly") Boolean primaryPreferenceOnly,
                                                              @QueryParam("sendingSchoolOid") String sendingSchoolOid,
                                                              @QueryParam("sendingClass") String sendingClass,
                                                              @QueryParam("updatedAfter") DateParam updatedAfter,
                                                              @DefaultValue(value = "0") @QueryParam("start") int start,
                                                              @DefaultValue(value = "100") @QueryParam("rows") int rows) {
//        LOGGER.debug("Finding applications q:{}, state:{}, aoid:{}, lopoid:{}, asId:{}, aoOid:{}, start:{}, rows: {}, " +
//                "asSemester: {}, asYear: {}, discretionaryOnly: {}, sendingSchoolOid: {}, sendingClass: {}",
//                q, state, aoid, lopoid, asId, aoOid, start, rows, asSemester, asYear, discretionaryOnly, sendingSchoolOid, sendingClass);

        List<String> asIds = new ArrayList<String>();
        if (isNotEmpty(asId)) {
            asIds.add(asId);
        } else if (isNotEmpty(asSemester) || isNotEmpty(asYear)) {
            List<String> applicationSystemIds = applicationSystemService.findByYearAndSemester(asSemester, asYear);
            if (applicationSystemIds.size() > 0)
                asIds.addAll(applicationSystemIds);
            else
                return new ApplicationSearchResultDTO(0, new ArrayList<ApplicationSearchResultItemDTO>(0));
        }
        for (String s : asIds) {
            LOGGER.debug("asId: {}", s);
        }
        ApplicationQueryParameters queryParams = new ApplicationQueryParametersBuilder()
                .setSearchTerms(searchTerms)
                .setStates(state)
                .setPreferenceChecked(preferenceChecked)
                .setAsIds(asIds)
                .setAoId(aoid)
                .setGroupOid(groupOid)
                .setBaseEducation(baseEducation)
                .setLopOid(lopoid)
                .setAoOid(aoOid)
                .setDiscretionaryOnly(discretionaryOnly)
                .setPrimaryPreferenceOnly(primaryPreferenceOnly)
                .setSendingSchool(sendingSchoolOid)
                .setSendingClass(sendingClass)
                .setUpdatedAfter(updatedAfter != null ? updatedAfter.getDate() : null)
                .setStart(start)
                .setRows(rows)
                .setOrderBy(orderBy)
                .setOrderDir("desc".equals(orderDir) ? -1 : 1)
                .build();
        return applicationService.findApplications(queryParams);
    }

    @GET
    @Path("{oid}/{key}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
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
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
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

    @GET
    @Path("additionalData/{asId}/{aoId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
    public List<ApplicationAdditionalDataDTO> getApplicationAdditionalData(@PathParam("asId") String asId,
                                                                           @PathParam("aoId") String aoId) {
        return applicationService.findApplicationAdditionalData(asId, aoId);
    }

    @PUT
    @Path("additionalData/{asId}/{aoId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
    public void putApplicationAdditionalData(@PathParam("asId") String asId,
                                             @PathParam("aoId") String aoId,
                                             List<ApplicationAdditionalDataDTO> additionalData) {
        applicationService.saveApplicationAdditionalInfo(additionalData);
    }

    @PUT
    @Path("syntheticApplication")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
    public Response putSyntheticApplication(List<SyntheticApplication> syntheticApplications) {
        if(new SyntheticApplicationValidator(syntheticApplications).validateSyntheticApplications()) {
            LOGGER.info("TODO insert applicationService function");
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private List<Application> getApplications(List<String> oids) {
        List<Application> result = new ArrayList<Application>();
        for (String oid : oids) {
            LOGGER.debug("Getting application by oid : {}", oid);
            try {
                Application app = applicationService.getApplicationByOid(oid);
                result.add(app);
            } catch (ResourceNotFoundException e) {
                throw new JSONException(Response.Status.NOT_FOUND, "Could not find requested application with oid: " + oid, e);
            }
        }
        return result;
    }
}
