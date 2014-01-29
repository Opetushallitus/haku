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

package fi.vm.sade.haku.oppija.ui.controller;

import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.ui.common.MultivaluedMapUtil;
import fi.vm.sade.haku.oppija.ui.common.UriUtil;
import fi.vm.sade.haku.oppija.ui.service.ModelResponse;
import fi.vm.sade.haku.oppija.ui.service.OfficerUIService;
import fi.vm.sade.haku.oppija.ui.service.UIService;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.seeOther;


@Path("virkailija")
@Controller
@PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
public class OfficerController {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerController.class);
    public static final String VIRKAILIJA_HAKEMUS_VIEW = "/virkailija/hakemus";
    public static final String DEFAULT_VIEW = "/virkailija/Form";
    public static final String OID_PATH_PARAM = "oid";
    public static final String VERBOSE_HELP_VIEW = "/help";
    public static final String PHASE_ID_PATH_PARAM = "phaseId";
    public static final String ELEMENT_ID_PATH_PARAM = "elementId";
    public static final String APPLICATION_SYSTEM_ID_PATH_PARAM = "applicationSystemId";
    public static final String ADDITIONAL_INFO_VIEW = "/virkailija/additionalInfo";
    public static final String SEARCH_INDEX_VIEW = "/virkailija/searchIndex";
    public static final String MEDIA_TYPE_TEXT_HTML_UTF8 = MediaType.TEXT_HTML + ";charset=UTF-8";
    public static final String APPLICATION_PRINT_VIEW = "/print/print";
    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    public static final String PHASE_ID_PREVIEW = "esikatselu";

    @Autowired
    OfficerUIService officerUIService;
    @Autowired
    UIService uiService;
    @Autowired
    FormService formService;

    @Autowired
    UserSession userSession;

    @GET
    @Path("/hakemus/")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable search() {
        ModelResponse modelResponse = officerUIService.getOrganizationAndLearningInstitutions();
        return new Viewable(SEARCH_INDEX_VIEW, modelResponse.getModel());
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
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{elementId}/help")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getFormHelp(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId) throws ResourceNotFoundException {
        return new Viewable(VERBOSE_HELP_VIEW, uiService.getElementHelp(applicationSystemId, elementId));
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

    @POST
    @Path("/hakemus/multiple")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable openApplications(final MultivaluedMap<String, String> multiValues) throws ResourceNotFoundException {
        LOGGER.debug("Opening multiple applications");
        Map<String, String> values = MultivaluedMapUtil.toSingleValueMap(multiValues);
        String applicationList = values.get("applicationList");
        String selectedApplication = values.get("selectedApplication");
        for (Map.Entry<String, String> entry : values.entrySet()) {
            LOGGER.debug("Entry: {} -> {}", entry.getKey(), entry.getValue());
        }
        ModelResponse modelResponse = officerUIService.getMultipleApplicationResponse(applicationList, selectedApplication);

        return new Viewable(DEFAULT_VIEW, modelResponse.getModel());
    }

    @GET
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}/{elementId}")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getPreviewElement(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                      @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                      @PathParam(OID_PATH_PARAM) final String oid,
                                      @PathParam("elementId") final String elementId)
            throws ResourceNotFoundException {
        LOGGER.debug("getPreviewElement {}, {}, {}", new String[]{applicationSystemId, phaseId, oid});
        ModelResponse modelResponse = officerUIService.getApplicationElement(oid, phaseId, elementId, true);
        return new Viewable("/elements/Root", modelResponse.getModel()); // TODO remove hardcoded Phase
    }

    @GET
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getPreview(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                               @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                               @PathParam(OID_PATH_PARAM) final String oid)
            throws ResourceNotFoundException, IOException {

        LOGGER.debug("getPreview {}, {}, {}", new String[]{applicationSystemId, phaseId, oid});
        ModelResponse modelResponse = officerUIService.getValidatedApplication(oid, phaseId);
        return new Viewable(DEFAULT_VIEW, modelResponse.getModel()); // TODO remove hardcoded Phase
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

        LOGGER.debug("updatePhase {}, {}, {}", new String[]{applicationSystemId, phaseId, oid});

        ModelResponse modelResponse = officerUIService.updateApplication(oid,
                new ApplicationPhase(applicationSystemId, phaseId, MultivaluedMapUtil.toSingleValueMap(multiValues)),
                userSession.getUser());

        if (modelResponse.hasErrors()) {
            return ok(new Viewable(DEFAULT_VIEW, modelResponse.getModel())).build();
        } else {
            URI path = UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW, applicationSystemId, PHASE_ID_PREVIEW, oid);
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
        ModelResponse modelResponse = officerUIService.getApplicationElement(oid, phaseId, elementId, false);
        modelResponse.addAnswers(MultivaluedMapUtil.toSingleValueMap(multiValues));
        return new Viewable("/elements/Root", modelResponse.getModel());
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
        ModelResponse modelResponse = officerUIService.getAdditionalInfo(oid);
        return new Viewable(ADDITIONAL_INFO_VIEW, modelResponse.getModel());
    }

    @GET
    @Path("/hakemus/{oid}/activate")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Response activate(@PathParam(OID_PATH_PARAM) final String oid)
            throws URISyntaxException, ResourceNotFoundException {
        LOGGER.debug("Entering GET activate");
        officerUIService.addPersonAndAuthenticate(oid);
        return seeOther(UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW, oid, "")).build();
    }

    @POST
    @Path("/hakemus/{oid}/activate")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_CRUD')")
    public Viewable activate(@PathParam(OID_PATH_PARAM) final String oid,
                             final MultivaluedMap<String, String> multiValues) throws IOException, ResourceNotFoundException {
        for (Map.Entry<String, List<String>> entry : multiValues.entrySet()) {
            LOGGER.debug("activation " + entry.getKey() + " -> " + entry.getValue());
        }
        StringBuilder reasonBuilder = new StringBuilder();
        for (String reasonPart : multiValues.get("activation-reason")) {
            reasonBuilder.append(reasonPart);
        }

        officerUIService.activateApplication(oid, reasonBuilder.toString());
        ModelResponse modelResponse = officerUIService.getValidatedApplication(oid, PHASE_ID_PREVIEW);
        return new Viewable(DEFAULT_VIEW, modelResponse.getModel());
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

        officerUIService.passivateApplication(oid, reasonBuilder.toString());
        ModelResponse modelResponse = officerUIService.getValidatedApplication(oid, PHASE_ID_PREVIEW);
        return new Viewable(DEFAULT_VIEW, modelResponse.getModel());
    }

    @POST
    @Path("/hakemus/{oid}/postProcess")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Viewable postProcess(@PathParam(OID_PATH_PARAM) final String oid) throws IOException, ResourceNotFoundException {
        officerUIService.postProcess(oid);
        ModelResponse modelResponse = officerUIService.getValidatedApplication(oid, PHASE_ID_PREVIEW);
        return new Viewable(DEFAULT_VIEW, modelResponse.getModel());
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

        officerUIService.addNote(oid, noteBuilder.toString());
        ModelResponse modelResponse = officerUIService.getValidatedApplication(oid, PHASE_ID_PREVIEW);
        return new Viewable(DEFAULT_VIEW, modelResponse.getModel());
    }

    @GET
    @Path("/hakemus/{oid}/print")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable applicationPrintView(@PathParam(OID_PATH_PARAM) final String oid) throws ResourceNotFoundException {
        ModelResponse modelResponse = uiService.getApplicationPrint(oid);
        return new Viewable(APPLICATION_PRINT_VIEW, modelResponse.getModel());
    }

    @POST
    @Path("/hakemus/{oid}/addStudentOid")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Viewable addStudentOid(@PathParam(OID_PATH_PARAM) final String oid) throws IOException, ResourceNotFoundException {
        officerUIService.addStudentOid(oid);
        ModelResponse modelResponse = officerUIService.getValidatedApplication(oid, PHASE_ID_PREVIEW);
        return new Viewable(DEFAULT_VIEW, modelResponse.getModel());
    }

    @GET
    @Path("/hakemus/applicationSystems")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> getApplicationSystems() {
        return getApplicationSystems("", "");
    }
    @GET
    @Path("/hakemus/applicationSystems/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> getApplicationSystems(@PathParam("year") String year) {
        return getApplicationSystems(year, "");

    }
    @GET
    @Path("/hakemus/applicationSystems/{year}/{semester}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> getApplicationSystems(@PathParam("year") String year,
                                                                  @PathParam("semester") String semester) {

        List<ApplicationSystem> applicationSystemList = officerUIService.getApplicationSystems();
        List<Map<String, String>> applicationSystems = new ArrayList<Map<String, String>>(applicationSystemList.size());
        for (ApplicationSystem as : applicationSystemList) {
            Map<String, String> applicationSystem = new HashMap<String, String>();
            applicationSystem.put("id", as.getId());
            applicationSystem.put("hakukausiUri", as.getHakukausiUri());
            applicationSystem.put("hakukausiVuosi", as.getHakukausiVuosi().toString());
            I18nText name = as.getName();
            Map<String, String> translations = name.getTranslations();
            for (Map.Entry<String, String> translation : translations.entrySet()) {
                String key = translation.getKey();
                String val = translation.getValue();
                applicationSystem.put("name_"+key, val);
            }
            applicationSystems.add(applicationSystem);
        }
        return applicationSystems;

    }

    @GET
    @Path("/autocomplete/{list}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> getAutocomplete(@PathParam("list") String list,
                                                     @QueryParam("term") String term) {
        if ("school".equals(list)) {
            return officerUIService.getSchools(term);
        } else if ("preference".equals(list)) {
            return officerUIService.getPreferences(term);
        }
        return new ArrayList<Map<String, Object>>(0);
    }
}
