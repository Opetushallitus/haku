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

import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.ui.common.MultivaluedMapUtil;
import fi.vm.sade.oppija.ui.common.UriUtil;
import fi.vm.sade.oppija.ui.service.OfficerUIService;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.seeOther;


@Path("virkailija")
@Controller
@Secured("ROLE_APP_HAKEMUS_READ_UPDATE")
public class OfficerController {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerController.class);
    public static final String VIRKAILIJA_HAKEMUS_VIEW = "/virkailija/hakemus";
    public static final String DEFAULT_VIEW = "/virkailija/Phase";
    public static final String OID_PATH_PARAM = "oid";
    public static final String PHASE_ID_PATH_PARAM = "phaseId";
    public static final String FORM_ID_PATH_PARAM = "formId";
    public static final String APPLICATION_PERIOD_ID_PATH_PARAM = "applicationPeriodId";
    public static final String ADDITIONAL_INFO_VIEW = "/virkailija/additionalInfo";
    public static final String SEARCH_INDEX_VIEW = "/virkailija/searchIndex";
    public static final String MEDIA_TYPE_TEXT_HTML_UTF8 = MediaType.TEXT_HTML + ";charset=UTF-8";
    public static final String VIRKAILIJA_PHASE_VIEW = "/virkailija/Phase";

    @Autowired
    OfficerUIService officerUIService;

    @Autowired
    UserHolder userHolder;

    @GET
    @Path("/hakemus/")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable search() {
        UIServiceResponse uiServiceResponse = officerUIService.getOrganizationAndLearningInstitutions();
        return new Viewable(SEARCH_INDEX_VIEW, uiServiceResponse.getModel());
    }

    @GET
    @Path("/hakemus/{oid}")
    public Response redirectToLastPhase(@PathParam(OID_PATH_PARAM) final String oid)
            throws ResourceNotFoundException, URISyntaxException {
        LOGGER.debug("redirectToLastPhase {}", new Object[]{oid});
        Application application = officerUIService.getApplicationWithLastPhase(oid);
        FormId formId = application.getFormId();
        URI path = UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW,
                formId.getApplicationPeriodId(),
                formId.getFormId(),
                application.getPhaseId(),
                application.getOid());
        return seeOther(path).build();
    }

    @GET
    @Path("/hakemus/{applicationPeriodId}/{formId}/{phaseId}/{oid}/{elementId}")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getPreviewElement(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                      @PathParam(FORM_ID_PATH_PARAM) final String formId,
                                      @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                      @PathParam(OID_PATH_PARAM) final String oid,
                                      @PathParam("elementId") final String elementId)
            throws ResourceNotFoundException {
        LOGGER.debug("getPreviewElement {}, {}, {}, {}", applicationPeriodId, formId, phaseId, oid);
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplicationElement(oid, phaseId, elementId);
        return new Viewable("/elements/Root", uiServiceResponse.getModel()); // TODO remove hardcoded Phase
    }

    @GET
    @Path("/hakemus/{applicationPeriodId}/{formId}/{phaseId}/{oid}")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getPreview(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                               @PathParam(FORM_ID_PATH_PARAM) final String formId,
                               @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                               @PathParam(OID_PATH_PARAM) final String oid)
            throws ResourceNotFoundException, IOException {

        LOGGER.debug("getPreview {}, {}, {}, {}", applicationPeriodId, formId, phaseId, oid);
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplication(oid, phaseId);
        return new Viewable(VIRKAILIJA_PHASE_VIEW, uiServiceResponse.getModel()); // TODO remove hardcoded Phase
    }

    @POST
    @Path("/hakemus/{applicationPeriodId}/{formId}/{phaseId}/{oid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Response updatePhase(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                @PathParam("formId") final String formId,
                                @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                @PathParam(OID_PATH_PARAM) final String oid,
                                final MultivaluedMap<String, String> multiValues)
            throws URISyntaxException, ResourceNotFoundException {

        LOGGER.debug("updatePhase {}, {}, {}, {}, {}", applicationPeriodId, formId, phaseId, oid, multiValues);
        final FormId hakuLomakeId = new FormId(applicationPeriodId, formId);

        UIServiceResponse uiServiceResponse = officerUIService.updateApplication(oid,
                new ApplicationPhase(hakuLomakeId, phaseId, MultivaluedMapUtil.toSingleValueMap(multiValues)),
                userHolder.getUser());

        if (uiServiceResponse.hasErrors()) {
            return ok(new Viewable(DEFAULT_VIEW, uiServiceResponse.getModel())).build();
        } else {
            URI path = UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW, applicationPeriodId, formId, "esikatselu", oid);
            return seeOther(path).build();
        }
    }

    @POST
    @Path("/hakemus/{oid}/additionalInfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Response saveAdditionalInfo(@PathParam(OID_PATH_PARAM) final String oid,
                                       final MultivaluedMap<String, String> multiValues)
            throws URISyntaxException, ResourceNotFoundException {
        LOGGER.debug("saveAdditionalInfo {}, {}", new Object[]{oid, multiValues});
        officerUIService.saveApplicationAdditionalInfo(oid, MultivaluedMapUtil.toSingleValueMap(multiValues));
        return seeOther(UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW, oid, "")).build();
    }

    @GET
    @Path("/hakemus/{oid}/additionalInfo")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getAdditionalInfo(@PathParam(OID_PATH_PARAM) final String oid)
            throws ResourceNotFoundException, IOException {
        LOGGER.debug("getAdditionalInfo  {}, {}", new Object[]{oid});
        UIServiceResponse uiServiceResponse = officerUIService.getAdditionalInfo(oid);
        return new Viewable(ADDITIONAL_INFO_VIEW, uiServiceResponse.getModel());
    }

    @GET
    @Path("/hakemus/{oid}/addPersonAndAuthenticate")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Response addPersonAndAuthenticate(@PathParam(OID_PATH_PARAM) final String oid)
            throws URISyntaxException, ResourceNotFoundException {
//        LOGGER.debug("Activate application {}, {}", new Object[]{oid, multiValues});
        officerUIService.addPersonAndAuthenticate(oid);
        return seeOther(UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW, oid, "")).build();
    }

    @POST
    @Path("/hakemus/{oid}/passivate")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Viewable passivate(@PathParam(OID_PATH_PARAM) final String oid,
                              final MultivaluedMap<String, String> multiValues) throws IOException, ResourceNotFoundException {
        for (Map.Entry<String, List<String>> entry : multiValues.entrySet()) {
            LOGGER.debug("passivation " + entry.getKey() + " -> " + entry.getValue());
        }
        StringBuilder reasonBuilder = new StringBuilder();
        for (String reasonPart : multiValues.get("passivation-reason")) {
            reasonBuilder.append(reasonPart);
        }

        officerUIService.passivateApplication(oid, reasonBuilder.toString(), userHolder.getUser());
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplication(oid, "esikatselu");
        return new Viewable(VIRKAILIJA_PHASE_VIEW, uiServiceResponse.getModel());
    }

    @POST
    @Path("/hakemus/{oid}/addNote")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Viewable addNote(@PathParam(OID_PATH_PARAM) final String oid,
                            final MultivaluedMap<String, String> multiValues) throws IOException, ResourceNotFoundException {
        for (Map.Entry<String, List<String>> entry : multiValues.entrySet()) {
            LOGGER.debug("passivation " + entry.getKey() + " -> " + entry.getValue());
        }
        StringBuilder noteBuilder = new StringBuilder();
        for (String notePart : multiValues.get("note-text")) {
            noteBuilder.append(notePart);
        }

        officerUIService.addNote(oid, noteBuilder.toString(), userHolder.getUser());
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplication(oid, "esikatselu");
        return new Viewable(VIRKAILIJA_PHASE_VIEW, uiServiceResponse.getModel());
    }
}
