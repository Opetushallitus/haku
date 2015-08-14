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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.*;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParametersBuilder;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.SyntheticApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.ui.service.OfficerUIService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
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
import java.net.URISyntaxException;
import java.util.*;

import static fi.vm.sade.haku.AuditHelper.AUDIT;
import static fi.vm.sade.haku.AuditHelper.builder;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * API for accessing applications.
 *
 * @author Hannu Lyytikainen
 */
@Component
@Path("/applications")
@Api(value = "/applications", description = "Hakemuspalvelun REST-rajapinta")
public class ApplicationResource {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    private ApplicationService applicationService;

    private ApplicationSystemService applicationSystemService;

    private ApplicationOptionService applicationOptionService;

    private SyntheticApplicationService syntheticApplicationService;

    private I18nBundleService i18nBundleService;

    @Autowired
    private OfficerUIService officerUIService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResource.class);
    private static final String OID = "oid";

    public ApplicationResource() {
    }

    @Autowired
    public ApplicationResource(final ApplicationService applicationService,
                               final ApplicationSystemService applicationSystemService,
                               final ApplicationOptionService applicationOptionService,
                               final SyntheticApplicationService syntheticApplicationService,
                               final I18nBundleService i18nBundleService) {
        this.applicationService = applicationService;
        this.applicationSystemService = applicationSystemService;
        this.applicationOptionService = applicationOptionService;
        this.syntheticApplicationService = syntheticApplicationService;
        this.i18nBundleService = i18nBundleService;
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    @ApiOperation(
            value = "Palauttaa hakemuksen tiedot",
            response = Application.class)
    public Application getApplicationByOid(@ApiParam(value="Hakemuksen oid-tunniste") @PathParam(OID) String oid) {
        LOGGER.debug("Getting application by oid : {}", oid);
        try {
            Application application = applicationService.getApplicationByOid(oid);
            LOGGER.debug("Got applicatoin by oid : {}", application.getOid());
            AUDIT.log(builder().hakemusOid(oid).message("Viewed application").build());
            return application;
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, "Could not find requested application", e);
        }
    }

    @GET
    @Path("/excel")
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
                .addAoOid(aoOid)
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
        I18nBundle i18nBundle = i18nBundleService.getBundle(activeApplicationSystem);
        return new XlsModel(ao, activeApplicationSystem, applications, userLocale.getLanguage(), i18nBundle);
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
	@ApiOperation(
            value = "Palauttaa useamman hakemuksen tiedot oid-listan perusteella. Oid:t annetaan query parametreina.",
            response = Application.class,
			responseContainer = "List")
    public List<Application> getApplicationsByOids(@ApiParam(value="Yksi tai useampi hakemuksen oid") @QueryParam("oid") List<String> oids) {
        return getApplications(oids);
    }

    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes("application/json")
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
	@ApiOperation(
            value = "Palauttaa useamman hakemuksen tiedot oid-listan perusteella. Oid:t json-listana POST-pyynnön bodyssa.",
            response = Application.class,
			responseContainer = "List")
    public List<Application> getApplicationsByOidsPost(final List<String> oids) {
        return getApplications(oids);
    }

    @GET
    @Path("/listfull")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
	@ApiOperation(
            value = "Palauttaa hakuehtoihin sopivien hakemusten tiedot.")
    public List<Map<String, Object>> findFullApplications(@ApiParam(value="Hakutermi, jokin seuraavista: nimi, henkilötunnus, oppijanumero, hakemusnumero.") @DefaultValue(value = "") @QueryParam("q") String searchTerms,
                                                          @ApiParam(value="Hakemuksen tila", allowableValues="[ACTIVE, PASSIVE, INCOMPLETE, NOT_IDENTIFIED]", allowMultiple=true) @QueryParam("appState") List<String> state,
                                                          @ApiParam(value="Onko liitetiedot merkitty tarkastetuksi") @QueryParam("preferenceChecked") Boolean preferenceChecked,
                                                          @ApiParam(value="Hakukohteen koodi") @QueryParam("aoid") String aoid,
                                                          @ApiParam(value="Hakukohderyhmän oid") @QueryParam("groupOid") String groupOid,
                                                          @ApiParam(value="Pohjakoulutus") @QueryParam("baseEducation") String baseEducation,
                                                          @ApiParam(value="Opetuspisteen organisaatiotunniste (oid)") @QueryParam("lopoid") String lopoid,
                                                          @ApiParam(value="Haun oid") @QueryParam("asId") String asId,
                                                          @ApiParam(value="Hakukausi") @QueryParam("asSemester") String asSemester,
                                                          @ApiParam(value="Hakuvuosi") @QueryParam("asYear") String asYear,
                                                          @ApiParam(value="Hakukohteen oid") @QueryParam("aoOid") String aoOid,
                                                          @ApiParam(value="Näytetäänkö vain harkinnanvaraisesti hakeneet") @QueryParam("discretionaryOnly") Boolean discretionaryOnly,
                                                          @ApiParam(value="Haetaanko vain sellaiset hakemukset, joissa hakuehtona oleva hakukohde on ensisijaisena toiveena") @QueryParam("primaryPreferenceOnly") Boolean primaryPreferenceOnly,
                                                          @ApiParam(value="Lähtökoulun oid") @QueryParam("sendingSchoolOid") String sendingSchoolOid,
                                                          @ApiParam(value="Lähtöluokka") @QueryParam("sendingClass") String sendingClass,
                                                          @ApiParam(value="Aikaleima, jonka jälkeen muuttuneet tai saapuneet hakemukset haetaan. Merkkijono muodossa yyyyMMddHHmm.") @QueryParam("updatedAfter") DateParam updatedAfter,
                                                          @ApiParam(value="Palautetaan tulosjoukosta rivit tästä rivinumerosta alkaen") @DefaultValue(value = "0") @QueryParam("start") int start,
                                                          @ApiParam(value="Palautetaan tulosjoukosta rivit tähän rivinumeroon saakka") @DefaultValue(value = "100") @QueryParam("rows") int rows) {

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
                .addAoOid(aoOid)
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
	@ApiOperation(
            value = "Palauttaa hakuehtoihin sopivien hakemusten tiedot.")
    public ApplicationSearchResultDTO findApplications(@ApiParam(value="Hakutermi, jokin seuraavista: nimi, henkilötunnus, oppijanumero, hakemusnumero.") @DefaultValue(value = "") @QueryParam("q") String query,
                                                       @ApiParam(value="Hakemuksen tila", allowableValues="[ACTIVE, PASSIVE, INCOMPLETE, NOT_IDENTIFIED]", allowMultiple=true) @QueryParam("appState") List<String> state,
                                                       @ApiParam(value="Onko liitetiedot merkitty tarkastetuksi") @QueryParam("preferenceChecked") Boolean preferenceChecked,
                                                       @ApiParam(value="Hakukohteen koodi") @QueryParam("aoid") String aoid,
                                                       @ApiParam(value="Hakukohderyhmän oid") @QueryParam("groupOid") String groupOid,
                                                       @ApiParam(value="Pohjakoulutus") @QueryParam("baseEducation") String baseEducation,
                                                       @ApiParam(value="Opetuspisteen organisaatiotunniste (oid)") @QueryParam("lopoid") String lopoid,
                                                       @ApiParam(value="Haun oid") @QueryParam("asId") String asId,
                                                       @ApiParam(value="Hakukausi") @QueryParam("asSemester") String asSemester,
                                                       @ApiParam(value="Hakuvuosi") @QueryParam("asYear") String asYear,
                                                       @ApiParam(value="Hakukohteen oid") @QueryParam("aoOid") String aoOid,
                                                       @ApiParam(value="Näytetäänkö vain harkinnanvaraisesti hakeneet") @QueryParam("discretionaryOnly") Boolean discretionaryOnly,
                                                       @ApiParam(value="Haetaanko vain sellaiset hakemukset, joissa hakuehtona oleva hakukohde on ensisijaisena toiveena") @QueryParam("primaryPreferenceOnly") Boolean primaryPreferenceOnly,
                                                       @ApiParam(value="Lähtökoulun oid") @QueryParam("sendingSchoolOid") String sendingSchoolOid,
                                                       @ApiParam(value="Lähtöluokka") @QueryParam("sendingClass") String sendingClass,
                                                       @ApiParam(value="Aikaleima, jonka jälkeen muuttuneet tai saapuneet hakemukset haetaan. Merkkijono muodossa yyyyMMddHHmm.") @QueryParam("updatedAfter") DateParam updatedAfter,
                                                       @ApiParam(value="Palautetaan tulosjoukosta rivit tästä rivinumerosta alkaen") @DefaultValue(value = "0") @QueryParam("start") int start,
                                                       @ApiParam(value="Palautetaan tulosjoukosta rivit tähän rivinumeroon saakka") @DefaultValue(value = "100") @QueryParam("rows") int rows) {

        return findApplicationsOrdered("fullName", "asc", query, state, preferenceChecked, aoid, groupOid, baseEducation, lopoid, asId,
                asSemester, asYear, aoOid, discretionaryOnly, primaryPreferenceOnly, sendingSchoolOid,
                sendingClass, updatedAfter, start, rows);
    }

    @GET
    @Path("/listshort")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
	@ApiOperation(
            value = "Palauttaa hakuehtoihin sopivien hakemusten perustiedot.")
    public ApplicationSearchResultDTO findApplicationsOrdered(@ApiParam(value="Kenttä, jonka perusteella tulosjoukko järjestetään") @DefaultValue(value = "fullName") @QueryParam("orderBy") String orderBy,
                                                              @ApiParam(value="Järjestys (asc = nouseva, desc = laskeva)") @DefaultValue(value = "asc") @QueryParam("orderDir") String orderDir,
                                                              @ApiParam(value="Hakutermi, jokin seuraavista: nimi, henkilötunnus, oppijanumero, hakemusnumero.") @DefaultValue(value = "") @QueryParam("q") String searchTerms,
                                                              @ApiParam(value="Hakemuksen tila", allowableValues="[ACTIVE, PASSIVE, INCOMPLETE, NOT_IDENTIFIED]", allowMultiple=true) @QueryParam("appState") List<String> state,
                                                              @ApiParam(value="Onko liitetiedot merkitty tarkastetuksi") @QueryParam("preferenceChecked") Boolean preferenceChecked,
                                                              @ApiParam(value="Hakukohteen koodi") @QueryParam("aoidCode") String aoid,
                                                              @ApiParam(value="Hakukohderyhmän oid") @QueryParam("groupOid") String groupOid,
                                                              @ApiParam(value="Pohjakoulutus") @QueryParam("baseEducation") String baseEducation,
                                                              @ApiParam(value="Opetuspisteen organisaatiotunniste (oid)") @QueryParam("lopoid") String lopoid,
                                                              @ApiParam(value="Haun oid") @QueryParam("asId") String asId,
                                                              @ApiParam(value="Hakukausi") @QueryParam("asSemester") String asSemester,
                                                              @ApiParam(value="Hakuvuosi") @QueryParam("asYear") String asYear,
                                                              @ApiParam(value="Hakukohteen oid") @QueryParam("aoOid") String aoOid,
                                                              @ApiParam(value="Näytetäänkö vain harkinnanvaraisesti hakeneet") @QueryParam("discretionaryOnly") Boolean discretionaryOnly,
                                                              @ApiParam(value="Haetaanko vain sellaiset hakemukset, joissa hakuehtona oleva hakukohde on ensisijaisena toiveena") @QueryParam("primaryPreferenceOnly") Boolean primaryPreferenceOnly,
                                                              @ApiParam(value="Lähtökoulun oid") @QueryParam("sendingSchoolOid") String sendingSchoolOid,
                                                              @ApiParam(value="Lähtöluokka") @QueryParam("sendingClass") String sendingClass,
                                                              @ApiParam(value="Aikaleima, jonka jälkeen muuttuneet tai saapuneet hakemukset haetaan. Merkkijono muodossa yyyyMMddHHmm.") @QueryParam("updatedAfter") DateParam updatedAfter,
                                                              @ApiParam(value="Palautetaan tulosjoukosta rivit tästä rivinumerosta alkaen") @DefaultValue(value = "0") @QueryParam("start") int start,
                                                              @ApiParam(value="Palautetaan tulosjoukosta rivit tähän rivinumeroon saakka") @DefaultValue(value = "100") @QueryParam("rows") int rows) {
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
                .addAoOid(aoOid)
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
    @Path("/{oid}/{key}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
	@ApiOperation(
            value = "Palauttaa hakemuksen {oid} avainta vastaavan {key} arvon.")
    public Map<String, String> getApplicationKeyValue(@ApiParam(value="Hakemuksen oid") @PathParam(OID) String oid, @ApiParam(value="Hakemuksen kentän avain") @PathParam("key") String key) {
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
    @Path("/{oid}/{key}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
	@ApiOperation(
            value = "Päivittää hakemuksen {oid} avaimen {key} arvon.")
    public void putApplicationAdditionalInfoKeyValue(@ApiParam(value="Hakemuksen oid") @PathParam(OID) String oid,
                                                     @ApiParam(value="Hakemuksen kentän avain") @PathParam("key") String key,
                                                     @ApiParam(value="Hakemuksen kentän uusi arvo") @QueryParam("value") String value) {
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
    @Path("/additionalData/{asId}/{aoId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
    public List<ApplicationAdditionalDataDTO> getApplicationAdditionalData(@PathParam("asId") String asId,
                                                                           @PathParam("aoId") String aoId) {
        return applicationService.findApplicationAdditionalData(asId, aoId);
    }

    @POST
    @Path("/additionalData")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
    public List<ApplicationAdditionalDataDTO> getApplicationAdditionalData(List<String> oids) {
        return applicationService.findApplicationAdditionalData(oids);
    }

    @PUT
    @Path("/additionalData/{asId}/{aoId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_LISATIETORU', 'ROLE_APP_HAKEMUS_LISATIETOCRUD')")
    public void putApplicationAdditionalData(@PathParam("asId") String asId,
                                             @PathParam("aoId") String aoId,
                                             List<ApplicationAdditionalDataDTO> additionalData) {
        boolean saveSucceeded = false;
        try {
            applicationService.saveApplicationAdditionalInfo(additionalData);
            saveSucceeded = true;
        } finally {
            if(saveSucceeded) {
                for (ApplicationAdditionalDataDTO applicationAdditionalDataDTO : additionalData) {
                    AUDIT.log(builder().hakuOid(asId).hakukohdeOid(aoId)
                            .addAll(applicationAdditionalDataDTO.getAdditionalData())
                            .hakemusOid(applicationAdditionalDataDTO.getOid()).message("Saved additional data").build());
                }

            }
        }
    }

    @PUT
    @Path("/syntheticApplication")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
	@ApiOperation(
            value = "Luo keinotekoisen hakemuksen (hyödynnetään ulkoisesti toteutettujen valintojen tulosten tuonnissa).")
    public Response putSyntheticApplication(@ApiParam(value="Hakemuksen tiedot") SyntheticApplication syntheticApplication) {
        try {
            if(new SyntheticApplicationValidator(syntheticApplication).validateSyntheticApplication()) {
                List<Application> applications = syntheticApplicationService.createApplications(syntheticApplication);
                return Response.ok(applications).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        } catch (Throwable e) {
            LOGGER.error("Could not import application: {} {}", syntheticApplication, e);
            throw new JSONException(Response.Status.INTERNAL_SERVER_ERROR, "Could not import application", e);
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

    @POST
    @Path("/state/passivate")
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_CRUD')")
    @ApiOperation(value = "Asettaa hakemusten tilan passiiviseksi")
    public Response passifyApplication(final ApplicationOidsAndReason applicationOidsAndReason) throws URISyntaxException {
        try {
            LOGGER.info("Setting state of applications to passive: {}", applicationOidsAndReason);
            for (String oid : applicationOidsAndReason.applicationOids) {
                officerUIService.changeState(oid, Application.State.PASSIVE, applicationOidsAndReason.reason);
            }
            return Response.ok().build();
        } catch (Throwable e) {
            LOGGER.error("Passivation failed {}", e);
            throw new JSONException(Response.Status.INTERNAL_SERVER_ERROR, "Passivation failed", e);
        }
    }
}
