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
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.util.ElementTree;
import fi.vm.sade.oppija.ui.common.MultivaluedMapUtil;
import fi.vm.sade.oppija.ui.common.UriUtil;
import fi.vm.sade.oppija.ui.service.OfficerUIService;
import fi.vm.sade.oppija.ui.service.UIService;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.seeOther;


@Path("virkailija")
@Controller
@PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
public class OfficerController {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerController.class);
    public static final String VIRKAILIJA_HAKEMUS_VIEW = "/virkailija/hakemus";
    public static final String DEFAULT_VIEW = "/virkailija/Phase";
    public static final String OID_PATH_PARAM = "oid";
    public static final String PHASE_ID_PATH_PARAM = "phaseId";
    public static final String ELEMENT_ID_PATH_PARAM = "elementId";
    public static final String APPLICATION_SYSTEM_ID_PATH_PARAM = "applicationSystemId";
    public static final String ADDITIONAL_INFO_VIEW = "/virkailija/additionalInfo";
    public static final String SEARCH_INDEX_VIEW = "/virkailija/searchIndex";
    public static final String MEDIA_TYPE_TEXT_HTML_UTF8 = MediaType.TEXT_HTML + ";charset=UTF-8";
    public static final String VIRKAILIJA_PHASE_VIEW = "/virkailija/Phase";
    public static final String APPLICATION_PRINT_VIEW = "/print/print";

    @Autowired
    OfficerUIService officerUIService;
    @Autowired
    UIService uiService;
    @Autowired
    FormService formService;

    @Autowired
    UserHolder userHolder;

    @GET
    @Path("/hakemus/")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable search() {
        UIServiceResponse uiServiceResponse = officerUIService.getOrganizationAndLearningInstitutions();
        return new Viewable(SEARCH_INDEX_VIEW, uiServiceResponse.getModel());
    }

    @POST
    @Path("/hakemus/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Response newApplication(final MultivaluedMap<String, String> multiValues) throws URISyntaxException, ResourceNotFoundException {
        LOGGER.debug("newApplication");
        final String asId = multiValues.getFirst("asId");
        Application application = officerUIService.createApplication(asId);
        return redirectToLastPhase(application.getOid());
    }

    @GET
    @Path("/hakemus/{oid}")
    public Response redirectToLastPhase(@PathParam(OID_PATH_PARAM) final String oid)
            throws ResourceNotFoundException, URISyntaxException {
        LOGGER.debug("redirectToLastPhase {}", new Object[]{oid});
        Application application = officerUIService.getApplicationWithLastPhase(oid);
        URI path = UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW,
                application.getApplicationSystemId(),
                application.getPhaseId(),
                application.getOid());
        return seeOther(path).build();
    }

    @GET
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}/{elementId}")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getPreviewElement(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                      @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                      @PathParam(OID_PATH_PARAM) final String oid,
                                      @PathParam("elementId") final String elementId)
            throws ResourceNotFoundException {
        LOGGER.debug("getPreviewElement {}, {}, {}", applicationSystemId, phaseId, oid);
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplicationElement(oid, phaseId, elementId);
        return new Viewable("/elements/Root", uiServiceResponse.getModel()); // TODO remove hardcoded Phase
    }

    @GET
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getPreview(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                               @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                               @PathParam(OID_PATH_PARAM) final String oid)
            throws ResourceNotFoundException, IOException {

        LOGGER.debug("getPreview {}, {}, {}", applicationSystemId, phaseId, oid);
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplication(oid, phaseId);
        return new Viewable(VIRKAILIJA_PHASE_VIEW, uiServiceResponse.getModel()); // TODO remove hardcoded Phase
    }

    @POST
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Response updatePhase(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                @PathParam(OID_PATH_PARAM) final String oid,
                                final MultivaluedMap<String, String> multiValues)
            throws URISyntaxException, ResourceNotFoundException {

        LOGGER.debug("updatePhase {}, {}, {}", applicationSystemId, phaseId, oid);

        UIServiceResponse uiServiceResponse = officerUIService.updateApplication(oid,
                new ApplicationPhase(applicationSystemId, phaseId, MultivaluedMapUtil.toSingleValueMap(multiValues)),
                userHolder.getUser());

        if (uiServiceResponse.hasErrors()) {
            return ok(new Viewable(DEFAULT_VIEW, uiServiceResponse.getModel())).build();
        } else {
            URI path = UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW, applicationSystemId, "esikatselu", oid);
            return seeOther(path).build();
        }
    }

    @POST
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}/{elementId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Viewable updateView(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                               @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                               @PathParam(OID_PATH_PARAM) final String oid,
                               @PathParam("elementId") final String elementId,
                               final MultivaluedMap<String, String> multiValues)
            throws ResourceNotFoundException {
        UIServiceResponse uiServiceResponse = officerUIService.getApplicationElement(oid, phaseId, elementId, false);
        uiServiceResponse.addAnswers(MultivaluedMapUtil.toSingleValueMap(multiValues));
        return new Viewable("/elements/Root", uiServiceResponse.getModel());
    }

    @POST
    @Path("/hakemus/{oid}/additionalInfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
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
    @Path("/hakemus/{oid}/addPersonOid")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Response addPersonAndAuthenticate(@PathParam(OID_PATH_PARAM) final String oid)
            throws URISyntaxException, ResourceNotFoundException {
        officerUIService.addPersonAndAuthenticate(oid);
        return seeOther(UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW, oid, "")).build();
    }

    @POST
    @Path("/hakemus/{oid}/addPersonOid")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Viewable addPersonAndAuthenticate(@PathParam(OID_PATH_PARAM) final String oid,
                                             final MultivaluedMap<String, String> multiValues) throws IOException, ResourceNotFoundException {
        StringBuilder reasonBuilder = new StringBuilder();
        for (String reasonPart : multiValues.get("activation-reason")) {
            reasonBuilder.append(reasonPart);
        }
        officerUIService.addPersonAndAuthenticate(oid);
        officerUIService.addNote(oid, reasonBuilder.toString(), userHolder.getUser());
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplication(oid, "esikatselu");
        return new Viewable(VIRKAILIJA_PHASE_VIEW, uiServiceResponse.getModel());
    }

    @POST
    @Path("/hakemus/{oid}/passivate")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_CRUD')")
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
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
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

    @GET
    @Path("/hakemus/{oid}/print")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable applicationPrintView(@PathParam(OID_PATH_PARAM) final String oid) throws ResourceNotFoundException {
        UIServiceResponse uiServiceResponse = uiService.getApplicationPrint(oid);
        return new Viewable(APPLICATION_PRINT_VIEW, uiServiceResponse.getModel());
    }

    @GET
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}/{elementId}/relatedData/{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Serializable getElementRelatedData(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                              @PathParam(OID_PATH_PARAM) final String oid,
                                              @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                              @PathParam("key") final String key) {
        LOGGER.debug("getElementRelatedData {}, {}, {}, {}", applicationSystemId, elementId, key);
        Form activeForm = formService.getForm(applicationSystemId);
        return new ElementTree(activeForm).getRelatedData(elementId, key);
    }

    @POST
    @Path("/hakemus/{oid}/addpersonoid")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    public Viewable addPersonOid(@PathParam(OID_PATH_PARAM) final String oid,
                            final MultivaluedMap<String, String> multiValues) throws IOException, ResourceNotFoundException {
        final String personOid = multiValues.getFirst("newPersonOid");
        LOGGER.debug("addPersonOid: oid {}, personOid {}", oid, personOid);
        officerUIService.addPersonOid(oid, personOid);
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplication(oid, "esikatselu");
        return new Viewable(VIRKAILIJA_PHASE_VIEW, uiServiceResponse.getModel());
    }
}
