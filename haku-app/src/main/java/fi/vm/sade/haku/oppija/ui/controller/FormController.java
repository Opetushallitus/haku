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

import fi.vm.sade.haku.oppija.ui.common.RedirectToFormViewPath;
import fi.vm.sade.haku.oppija.ui.common.RedirectToPendingViewPath;
import fi.vm.sade.haku.oppija.ui.common.RedirectToPhaseViewPath;
import fi.vm.sade.haku.oppija.ui.common.UriUtil;
import fi.vm.sade.haku.oppija.ui.service.ModelResponse;
import fi.vm.sade.haku.oppija.ui.service.UIService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

import static fi.vm.sade.haku.oppija.ui.common.MultivaluedMapUtil.toSingleValueMap;

@Component
@Path("lomake")
public class FormController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    public static final String ROOT_VIEW = "/elements/Root";
    public static final String VERBOSE_HELP_VIEW = "/help";
    public static final String APPLICATION_SYSTEM_LIST_VIEW = "/applicationSystemList";
    public static final String VALMIS_VIEW = "/valmis/valmis";
    public static final String PRINT_VIEW = "/print/print";
    public static final String APPLICATION_SYSTEM_ID_PATH_PARAM = "applicationSystemId";
    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final String PHASE_ID_PATH_PARAM = "phaseId";
    public static final String ELEMENT_ID_PATH_PARAM = "elementId";

    private final UIService uiService;
    private final PDFService pdfService;

    @Autowired
    public FormController(final UIService uiService, final PDFService pdfService) {
        this.uiService = uiService;
        this.pdfService = pdfService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable listApplicationSystems() {
        LOGGER.debug("listApplicationSystems");
        ModelResponse modelResponse = uiService.getAllApplicationSystems("id", "name", "applicationPeriods");
        return new Viewable(APPLICATION_SYSTEM_LIST_VIEW, modelResponse.getModel());
    }

    @GET
    @Path("/{applicationSystemId}")
    public Response getApplication(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) throws URISyntaxException {
        LOGGER.debug("getApplication {}", new Object[]{applicationSystemId});
        ModelResponse modelResponse = uiService.getApplication(applicationSystemId);
        return Response.seeOther(new URI(new RedirectToPhaseViewPath(applicationSystemId, modelResponse.getPhaseId()).getPath())).build();
    }

    @POST
    @Path("/{applicationSystemId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Response prefillForm(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                final MultivaluedMap<String, String> multiValues) throws URISyntaxException {
        LOGGER.debug("prefillForm {}, {}", applicationSystemId, multiValues);
        uiService.storePrefilledAnswers(applicationSystemId, toSingleValueMap(multiValues));
        return Response.seeOther(new URI(
                new RedirectToFormViewPath(applicationSystemId).getPath())).build();
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhase(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId) {

        LOGGER.debug("getPhase {}, {}", applicationSystemId, phaseId);
        ModelResponse modelResponse = uiService.getPhase(applicationSystemId, phaseId);
        return new Viewable(ROOT_VIEW, modelResponse.getModel());
    }

    @GET
    @Path("/{applicationSystemId}/esikatselu")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPreview(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) {
        ModelResponse modelResponse = uiService.getPreview(applicationSystemId);
        return new Viewable(ROOT_VIEW, modelResponse.getModel());
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}/{elementId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhaseElement(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                    @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                    @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId) {
        LOGGER.debug("getPhaseElement {}, {}", applicationSystemId, phaseId);
        ModelResponse modelResponse = uiService.getPhaseElement(applicationSystemId, phaseId, elementId);
        return new Viewable(ROOT_VIEW, modelResponse.getModel());
    }

    @POST
    @Path("/{applicationSystemId}/{phaseId}/{elementId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Viewable updateRules(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                final MultivaluedMap<String, String> multiValues) {
        LOGGER.debug("updateRules {}, {}, {}", applicationSystemId, phaseId, elementId);
        ModelResponse modelResponse = uiService.updateRules(applicationSystemId, phaseId, elementId, toSingleValueMap(multiValues));
        return new Viewable(ROOT_VIEW, modelResponse.getModel());
    }

    @POST
    @Path("/{applicationSystemId}/esikatselu")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Response submitApplication(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) throws URISyntaxException {
        LOGGER.debug("submitApplication {}", new Object[]{applicationSystemId});
        ModelResponse modelResponse = uiService.submitApplication(applicationSystemId);
        RedirectToPendingViewPath redirectToPendingViewPath = new RedirectToPendingViewPath(applicationSystemId, modelResponse.getApplication().getOid());
        return Response.seeOther(new URI(redirectToPendingViewPath.getPath())).build();
    }

    @POST
    @Path("/{applicationSystemId}/{phaseId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Response savePhase(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                              final MultivaluedMap<String, String> answers) throws URISyntaxException {
        LOGGER.debug("savePhase {}, {}", applicationSystemId, phaseId);
        ModelResponse modelResponse = uiService.savePhase(applicationSystemId, phaseId, toSingleValueMap(answers));
        if (modelResponse.hasErrors()) {
            return Response.status(Response.Status.OK).entity(new Viewable(ROOT_VIEW, modelResponse.getModel())).build();
        } else {
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationSystemId,
                            modelResponse.getPhaseId()).getPath())).build();
        }
    }

    @GET
    @Path("/{applicationSystemId}/valmis/{oid}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getComplete(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam("oid") final String oid) {

        LOGGER.debug("getComplete {}, {}", new Object[]{applicationSystemId});
        ModelResponse response = uiService.getCompleteApplication(applicationSystemId, oid);
        return new Viewable(VALMIS_VIEW, response.getModel());
    }

    @GET
    @Path("/{applicationSystemId}/tulostus/{oid}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPrint(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                             @PathParam("oid") final String oid) {
        LOGGER.debug("getPrint {}, {}", new Object[]{applicationSystemId, oid});
        ModelResponse modelResponse = uiService.getCompleteApplication(applicationSystemId, oid);
        return new Viewable(PRINT_VIEW, modelResponse.getModel());
    }

    @GET
    @Path("/{applicationSystemId}/pdf/{oid}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPDF(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
    	@PathParam("oid") final String oid) throws URISyntaxException {
    	String url = "/lomake/" + applicationSystemId + "/tulostus/" + oid;
    	HttpResponse httpResponse = pdfService.getUriToPDF(url);
    	URI location = UriUtil.pathSegmentsToUri(httpResponse.getFirstHeader("Content-Location").getValue());
    	return Response.seeOther(location).build();
    }
    
    @POST
    @Path("/{applicationSystemId}/{elementId}/help")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getFormHelp(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                final MultivaluedMap<String, String> answers) {
        return new Viewable(VERBOSE_HELP_VIEW, uiService.getElementHelp(applicationSystemId, elementId,toSingleValueMap(answers)));
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}/{gradeGridId}/additionalLanguageRow")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getAdditionalLanguageRow(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                             @PathParam("gradeGridId") final String gradeGridId) {
        LOGGER.debug("getAdditionalLanguageRow {}, {}, {}", applicationSystemId, gradeGridId);
        return new Viewable(ROOT_VIEW, uiService.getAdditionalLanguageRow(applicationSystemId, gradeGridId));
    }
}
