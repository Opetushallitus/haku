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

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import fi.vm.sade.auditlog.User;
import fi.vm.sade.haku.HakuOperation;
import fi.vm.sade.haku.OppijaAuditLogger;
import fi.vm.sade.haku.VirkailijaAuditLogger;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.ui.common.*;
import fi.vm.sade.haku.oppija.ui.service.UIService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Objects.firstNonNull;
import static fi.vm.sade.haku.oppija.ui.common.BeanToMapConverter.*;
import static fi.vm.sade.haku.oppija.ui.common.MultivaluedMapUtil.filterOPHParameters;
import static fi.vm.sade.haku.oppija.ui.common.MultivaluedMapUtil.toSingleValueMap;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.APPLICATION_BLACKLISTED_FIELDS;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
    private final AuthenticationService authenticationService;

    private final String generatorUrl;

    private final ObjectMapper mapper;

    private final OppijaAuditLogger oppijaAuditLogger;

    @Autowired
    public FormController(final UIService uiService, final PDFService pdfService,
                          final AuthenticationService authenticationService,
                          @Value("${application.system.generatorUrl:}") final String generatorUrl,
                          OppijaAuditLogger oppijaAuditLogger) {
        this.uiService = uiService;
        this.pdfService = pdfService;
        this.authenticationService = authenticationService;
        this.generatorUrl = generatorUrl;
        this.oppijaAuditLogger = oppijaAuditLogger;

        mapper = new ObjectMapper()
                .disable(SerializationConfig.Feature.INDENT_OUTPUT)
                .disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS)
                .disable(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);

    }

    @GET
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable listApplicationSystems() {
        LOGGER.debug("listApplicationSystems");
        ModelResponse modelResponse = uiService.getAllApplicationSystems("id", "name", "applicationPeriods", "state", "lastGenerated");
        if (isNotBlank(generatorUrl)) {
            modelResponse.addObjectToModel("generatorUrl", generatorUrl);
        }
        return new Viewable(APPLICATION_SYSTEM_LIST_VIEW, modelResponse.getModel());
    }

    @GET
    @Path("/{applicationSystemId}")
    public Response getApplication(@Context HttpServletRequest request,
                                   @PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) throws URISyntaxException {
        LOGGER.debug("getApplication {}", new Object[]{applicationSystemId});
        uiService.ensureLanguage(request, applicationSystemId);
        ModelResponse modelResponse = uiService.getApplication(applicationSystemId);
        Response.ResponseBuilder builder = Response.seeOther(new URI(new RedirectToPhaseViewPath(applicationSystemId, modelResponse.getPhaseId()).getPath()));

        return builder.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Path("/{applicationSystemId}/form")
    public Map getApplicationSystemForm(@Context HttpServletRequest request,
                                   @PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) throws URISyntaxException {
        LOGGER.info("Getting form for as "+applicationSystemId);
        Form form = uiService.getApplicationSystemForm(applicationSystemId);
        Element element = form.getChildById("osaaminen");
        LOGGER.info("Got form for as "+applicationSystemId);
        Map retMap = mapper.convertValue(element, Map.class);
        LOGGER.info("Returning form as map for as "+applicationSystemId);
        return retMap;
    }

    @POST
    @Path("/{applicationSystemId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Response prefillForm(@Context HttpServletRequest request,
                                @PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                final MultivaluedMap<String, String> post) throws URISyntaxException {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        LOGGER.debug("prefillForm {}, {}", applicationSystemId, multiValues);
        String lang = uiService.ensureLanguage(request, applicationSystemId);
        uiService.storePrefilledAnswers(applicationSystemId, toSingleValueMap(multiValues), lang);
        Response.ResponseBuilder builder = Response.seeOther(new URI(
                new RedirectToFormViewPath(applicationSystemId).getPath()));

        return builder.build();
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Response getPhase(@Context HttpServletRequest request,
                             @PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId) throws ExecutionException {

        LOGGER.debug("getPhase {}, {}", applicationSystemId, phaseId);
        String lang = uiService.ensureLanguage(request, applicationSystemId);
        ModelResponse modelResponse = uiService.getPhase(applicationSystemId, phaseId, lang);
        Viewable viewable = new Viewable(ROOT_VIEW, modelResponse.getModel());

        Response.ResponseBuilder builder = Response.ok(viewable);
        return builder.build();

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
                                final MultivaluedMap<String, String> post) {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        LOGGER.debug("updateRules {}, {}, {}", applicationSystemId, phaseId, elementId);
        ModelResponse modelResponse = uiService.updateRules(applicationSystemId, phaseId, elementId, toSingleValueMap(multiValues));
        return new Viewable(ROOT_VIEW, modelResponse.getModel());
    }

    @POST
    @Path("/{applicationSystemId}/{phaseId}/rules")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Viewable updateRulesMulti(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                final MultivaluedMap<String, String> post) throws ExecutionException {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        LOGGER.debug("updateRulesMulti {}, {}, {}", applicationSystemId, phaseId);
        List<String> ruleIds = firstNonNull(multiValues.get("ruleIds[]"), ImmutableList.<String>of());
        ModelResponse modelResponse = uiService.updateRulesMulti(applicationSystemId, phaseId, ruleIds, toSingleValueMap(multiValues));
        return new Viewable("/elements/JsonElementList.jsp", modelResponse.getModel());
    }

    private Target.Builder targetWithIpAndSession(Target.Builder builder, HttpServletRequest request) {
        String ipAddressProxy = request.getHeader("X-FORWARDED-FOR");
        if (ipAddressProxy != null) {
            builder.setField("ip", ipAddressProxy);
        }
        String sessionId = request.getRequestedSessionId();
        if (sessionId != null) {
            builder.setField("sessionId", sessionId);
        }
        return builder;
    }

    private Changes.Builder changesEntryNewApplication(fi.vm.sade.haku.oppija.hakemus.domain.Application app) {
        Changes.Builder builder = new Changes.Builder();
        for (Map.Entry<String, String> entry : applicationToMap(app).entrySet()) {
            builder.added(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    @POST
    @Path("/{applicationSystemId}/esikatselu")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Response submitApplication(@Context HttpServletRequest request,
                                      @PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) throws URISyntaxException {
        try {
            LOGGER.debug("submitApplication {}", new Object[]{applicationSystemId});
            Locale userLocale = (Locale) Config.get(request.getSession(), Config.FMT_LOCALE);
            ModelResponse modelResponse = uiService.submitApplication(applicationSystemId, userLocale.getLanguage());
            final String oid = modelResponse.getApplication().getOid();
            RedirectToPendingViewPath redirectToPendingViewPath = new RedirectToPendingViewPath(applicationSystemId, oid);


            Target.Builder targetBuilder = new Target.Builder()
                .setField("applicationSystemId", applicationSystemId);
            Changes.Builder changesBuilder = changesEntryNewApplication(modelResponse.getApplication());

            auditLogRequest(request, HakuOperation.SUBMIT_NEW_APPLICATION, targetBuilder.build(), changesBuilder.build());

            return Response.seeOther(new URI(redirectToPendingViewPath.getPath())).build();
        } catch(Throwable t) {
            fi.vm.sade.haku.oppija.hakemus.domain.Application application = uiService.getApplication(applicationSystemId).getApplication();

            Target.Builder targetBuilder = new Target.Builder()
                    .setField("applicationSystemId", applicationSystemId)
                    .setField("message", "Failed: " + t.getMessage());
            auditLogRequest(request, HakuOperation.SUBMIT_NEW_APPLICATION, targetBuilder.build());

            throw t;
        }
    }

    private Map<String,String> applicationToMap(fi.vm.sade.haku.oppija.hakemus.domain.Application a) {
        try {
            return convert(a, APPLICATION_BLACKLISTED_FIELDS);
        } catch(Exception e) {
            LOGGER.error("Failed to map application", e);
            return emptyMap();
        }
    }

    @POST
    @Path("/{applicationSystemId}/{phaseId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Response savePhase(@Context HttpServletRequest request,
                              @PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                              final MultivaluedMap<String, String> post) throws URISyntaxException, ExecutionException {
        final MultivaluedMap<String, String> answers = filterOPHParameters(post);
        LOGGER.debug("savePhase {}, {}", applicationSystemId, phaseId);
        String lang = uiService.ensureLanguage(request, applicationSystemId);
        ModelResponse modelResponse = uiService.savePhase(applicationSystemId, phaseId, toSingleValueMap(answers), lang);
        fi.vm.sade.haku.oppija.hakemus.domain.Application application = modelResponse.getApplication();

        Changes.Builder changesBuilder = changesEntryNewApplication(application);
        Target.Builder targetBuilder = new Target.Builder()
                .setField("applicationSystemId", applicationSystemId)
                .setField("phaseId", phaseId);

        auditLogRequest(request, HakuOperation.SAVE_PHASE_TO_SESSION, targetBuilder.build(), changesBuilder.build());

        if (modelResponse.hasErrors()) {
            return Response.status(Response.Status.OK).entity(new Viewable(ROOT_VIEW, modelResponse.getModel())).build();
        } else {
            if(OppijaConstants.PHASE_PERSONAL.equals(phaseId)) {
                LOGGER.info(OppijaConstants.PHASE_PERSONAL + " phase of " +  applicationSystemId + " filled:"
                                + " session: " + request.getSession().getId()
                                + ", first names: " + answers.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES)
                                + ", nickname: " +  answers.get(OppijaConstants.ELEMENT_ID_NICKNAME)
                                + ", surname: " + answers.get(OppijaConstants.ELEMENT_ID_LAST_NAME)
                                + ", email: " + answers.get(OppijaConstants.ELEMENT_ID_EMAIL)
                                + ", mobilephone: " + answers.get(OppijaConstants.ELEMENT_ID_PREFIX_PHONENUMBER + 1)
                );
            }
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
    	HttpResponse httpResponse = uiService.getUriToPDF(applicationSystemId, oid);
    	URI location = UriUtil.pathSegmentsToUri(httpResponse.getFirstHeader("Content-Location").getValue());
    	return Response.seeOther(location).build();
    }
    
    @POST
    @Path("/{applicationSystemId}/{elementId}/help")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getFormHelp(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                final MultivaluedMap<String, String> post) {
        final MultivaluedMap<String, String> answers = filterOPHParameters(post);
        return new Viewable(VERBOSE_HELP_VIEW, uiService.getElementHelp(applicationSystemId, elementId,toSingleValueMap(answers)));
    }

    @POST
    @Path("/session/refresh")
    @Produces(MediaType.TEXT_PLAIN)
    public String refreshSession() {
        return "OK";
    }

    private InetAddress getInetAddress(HttpServletRequest request) {
        InetAddress inetaddress;
        try {
            inetaddress = InetAddress.getByName(request.getRemoteAddr());
        } catch (UnknownHostException e) {
            LOGGER.error("Could not create inetaddress of remote address {}", request.getRemoteAddr());
            inetaddress = null;
        }
        return inetaddress;
    }

    private void auditLogRequest(HttpServletRequest request, HakuOperation operation, Target target) {
        auditLogRequest(request, operation, target, null);
    }

    private void auditLogRequest(HttpServletRequest request, HakuOperation operation, Target target, Changes changes) {
        if(changes == null) {
            changes = new Changes.Builder().build();
        }
        //InetAddress inetaddress = getInetAddress(request);
        //User user = new User(oppijaAuditLogger.getCurrentPersonOid(), inetaddress, request.getSession().toString(), request.getHeader("user-agent"));
        oppijaAuditLogger.log(oppijaAuditLogger.getUser(), operation, target, changes);
    }
}
