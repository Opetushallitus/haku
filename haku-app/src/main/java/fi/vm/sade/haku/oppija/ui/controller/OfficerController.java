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
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import fi.vm.sade.auditlog.User;
import fi.vm.sade.haku.HakuOperation;
import fi.vm.sade.haku.OppijaAuditLogger;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.oppija.lomake.exception.IllegalStateException;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.Session;
import fi.vm.sade.haku.oppija.ui.common.UriUtil;
import fi.vm.sade.haku.oppija.ui.controller.dto.EligibilitiesDTO;
import fi.vm.sade.haku.oppija.ui.service.OfficerUIService;
import fi.vm.sade.haku.oppija.ui.service.UIService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationByEmailDTO;
import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationReplacementDTO;
import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationTemplateDTO;
import fi.vm.sade.properties.OphProperties;
import org.apache.http.HttpResponse;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.ui.common.MultivaluedMapUtil.filterOPHParameters;
import static fi.vm.sade.haku.oppija.ui.common.MultivaluedMapUtil.toSingleValueMap;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.seeOther;

@Path("virkailija")
@Controller
@PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
public class OfficerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfficerController.class);
    private static final String VIRKAILIJA_HAKEMUS_VIEW = "/virkailija/hakemus";
    private static final String DEFAULT_VIEW = "/virkailija/Form";
    private static final String VALINTA_TAB_VIEW = "/virkailija/valintaTab";
    private static final String KELPOISUUS_JA_LIITTEET_TAB_VIEW = "/virkailija/kelpoisuusLiitteetTab";
    private static final String OID_PATH_PARAM = "oid";
    private static final String ORGANIZATION_OID_PATH_PARAM = "orgOid";
    private static final String VERBOSE_HELP_VIEW = "/help";
    private static final String PHASE_ID_PATH_PARAM = "phaseId";
    private static final String ELEMENT_ID_PATH_PARAM = "elementId";
    private static final String APPLICATION_SYSTEM_ID_PATH_PARAM = "applicationSystemId";
    static final String ADDITIONAL_INFO_VIEW = "/virkailija/additionalInfo";
    private static final String SEARCH_INDEX_VIEW = "/virkailija/searchIndex";
    private static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final String MEDIA_TYPE_TEXT_HTML_UTF8 = MediaType.TEXT_HTML + CHARSET_UTF_8;
    private static final String APPLICATION_PRINT_VIEW = "/print/print";
    private static final String PHASE_ID_PREVIEW = "esikatselu";

    @Autowired
    private OfficerUIService officerUIService;
    @Autowired
    private UIService uiService;
    @Autowired
    private FormService formService;
    @Autowired
    private Session userSession;
    @Autowired
    private PDFService pdfService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private OphProperties urlConfiguration;

    @Autowired
    private OppijaAuditLogger oppijaAuditLogger;

    @Autowired
    private AuthenticationService authenticationService;

    public OfficerController() {}

    @Autowired
    public OfficerController(OfficerUIService officerUIService, UIService uiService, FormService formService, Session userSession, PDFService pdfService, EmailService emailService, OphProperties urlConfiguration, OppijaAuditLogger oppijaAuditLogger, AuthenticationService authenticationService) {
        this.officerUIService = officerUIService;
        this.uiService = uiService;
        this.formService = formService;
        this.userSession = userSession;
        this.pdfService = pdfService;
        this.emailService = emailService;
        this.urlConfiguration = urlConfiguration;
        this.oppijaAuditLogger = oppijaAuditLogger;
        this.authenticationService = authenticationService;
    }

    @GET
    @Path("/hakemus/")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable search() {
        ModelResponse modelResponse = officerUIService.getOrganizationAndLearningInstitutions();
        return new Viewable(SEARCH_INDEX_VIEW, modelResponse.getModel());
    }

    @POST
    @Path("/hakemus/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Response newApplication(final MultivaluedMap<String, String> post) throws URISyntaxException {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        LOGGER.debug("create new application");
        Application application = officerUIService.createApplication(multiValues.getFirst("asId"));

        Changes changes = new Changes.Builder().build();
        Target target = new Target.Builder()
                .setField("hakuOid", multiValues.getFirst("asId"))
                .setField("hakemusOid", application.getOid()).build();

        auditLogRequest(HakuOperation.CREATE_NEW_APPLICATION, target, changes);
        return redirectToOidResponse(application.getOid());
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{elementId}/help")
    public Viewable getFormHelp(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId, final MultivaluedMap<String, String> post) {
        final MultivaluedMap<String, String> answers = filterOPHParameters(post);
        return new Viewable(VERBOSE_HELP_VIEW, uiService.getElementHelp(applicationSystemId, elementId, toSingleValueMap(answers)));
    }

    @GET
    @Path("/hakemus/{oid}/")
    public Viewable redirectToLastPhase(@PathParam(OID_PATH_PARAM) final String oid) throws URISyntaxException, IOException {
        LOGGER.debug("get application  {}", oid);
        ModelResponse modelResponse = officerUIService.getValidatedApplication(oid, "esikatselu", false);

        Changes changes = new Changes.Builder().build();
        Target target = new Target.Builder().setField("oid", oid).build();

        auditLogRequest(HakuOperation.VIEW_APPLICATION, target, changes);

        return new Viewable(DEFAULT_VIEW, modelResponse.getModel());
    }
    @GET
    @Path("/hakemus/{oid}/valinta")
    public Viewable valintaTab(@PathParam(OID_PATH_PARAM) final String oid) throws URISyntaxException, IOException {
        LOGGER.debug("get application  {}", oid);
        ModelResponse modelResponse = officerUIService.getValintaTab(oid);

        Changes changes = new Changes.Builder().build();
        Target target = new Target.Builder().setField("oid", oid).build();

        auditLogRequest(HakuOperation.VIEW_APPLICATION, target, changes);
        return new Viewable(VALINTA_TAB_VIEW, modelResponse.getModel());
    }
    @GET
    @Path("/hakemus/{oid}/kelpoisuus_ja_liitteet")
    public Viewable kelpoisuusJaLiitteetTab(@PathParam(OID_PATH_PARAM) final String oid) throws URISyntaxException, IOException {
        LOGGER.debug("get application  {}", oid);
        ModelResponse modelResponse = officerUIService.getValidatedApplication(oid, "esikatselu", true);

        Changes changes = new Changes.Builder().build();
        Target target = new Target.Builder().setField("oid", oid).build();

        auditLogRequest(HakuOperation.VIEW_APPLICATION, target, changes);

        return new Viewable(KELPOISUUS_JA_LIITTEET_TAB_VIEW, modelResponse.getModel());
    }
    @GET
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getPreview(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                               @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                               @PathParam(OID_PATH_PARAM) final String oid) throws IOException {
        LOGGER.debug("getPreview {}, {}, {}", applicationSystemId, phaseId, oid);
        ModelResponse modelResponse = officerUIService.getValidatedApplication(oid, phaseId, false);

        modelResponse.setNoteMessages(this.userSession.getNotes());
        this.userSession.clearNotes();

        Changes changes = new Changes.Builder().build();
        Target target = new Target.Builder()
                .setField("oid", oid)
                .setField("hakuOid", applicationSystemId)
                .build();

        auditLogRequest(HakuOperation.PREVIEW_APPLICATION, target, changes);

        return new Viewable(DEFAULT_VIEW, modelResponse.getModel()); // TODO remove hardcoded Phase
    }

    @POST
    @Path("/hakemus/multiple")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable openApplications(final MultivaluedMap<String, String> post) throws IOException {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        LOGGER.debug("Opening multiple applications");
        Map<String, String> values = toSingleValueMap(multiValues);
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
                                      @PathParam("elementId") final String elementId) {
        LOGGER.debug("getPreviewElement {}, {}, {}", applicationSystemId, phaseId, oid);
        ModelResponse modelResponse = officerUIService.getApplicationElement(oid, phaseId, elementId, true);

        Changes changes = new Changes.Builder().build();
        Target target = new Target.Builder()
                .setField("oid", oid)
                .setField("hakuOid", applicationSystemId)
                .setField("phaseId", phaseId)
                .build();

        auditLogRequest(HakuOperation.PREVIEW_APPLICATION, target, changes);

        return new Viewable("/elements/Root", modelResponse.getModel()); // TODO remove hardcoded Phase
    }

    @POST
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public Response updatePhase(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                @PathParam(OID_PATH_PARAM) final String oid,
                                final MultivaluedMap<String, String> post)
            throws URISyntaxException, IOException {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        LOGGER.debug("updatePhase {}, {}, {}", applicationSystemId, phaseId, oid);

        Map<String, String> values = toSingleValueMap(multiValues);
        ModelResponse modelResponse = officerUIService.updateApplication(oid,
                new ApplicationPhase(applicationSystemId, phaseId, values),
                userSession.getUser());

        Changes.Builder changesBuilder = new Changes.Builder();
        for(Map.Entry<String, String> changesStr : values.entrySet()) {
            changesBuilder.added(changesStr.getKey(), changesStr.getValue());
        }
        Changes changes = changesBuilder.build();

        Target target = new Target.Builder()
                .setField("oid", oid)
                .setField("hakuOid", applicationSystemId)
                .setField("phaseId", phaseId)
                .build();

        auditLogRequest(HakuOperation.UPDATE_APPLICATION_PHASE, target, changes);

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
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public Viewable updateView(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                               @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                               @PathParam(OID_PATH_PARAM) final String oid,
                               @PathParam("elementId") final String elementId,
                               final MultivaluedMap<String, String> post) {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        LOGGER.debug("updateView {}, {}", new Object[]{oid, multiValues});
        ModelResponse modelResponse = officerUIService.getApplicationElement(oid, phaseId, elementId, false);
        modelResponse.addAnswers(toSingleValueMap(multiValues));

        Target target = new Target.Builder()
                .setField("oid", oid)
                .setField("hakuOid", applicationSystemId)
                .setField("phaseId", phaseId)
                .build();

        auditLogRequest(HakuOperation.REFRESH_APPLICATION_VIEW, target);

        return new Viewable("/elements/Root", modelResponse.getModel());
    }

    @POST
    @Path("/hakemus/{applicationSystemId}/{phaseId}/{oid}/rules")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public Viewable updateMultiRuleView(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                        @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                        @PathParam(OID_PATH_PARAM) final String oid,
                                        final MultivaluedMap<String, String> post) {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        LOGGER.debug("updateMultiRuleView {}, {}", new Object[]{oid, multiValues});
        List<String> ruleIds = multiValues.get("ruleIds[]");
        ModelResponse modelResponse = officerUIService.getApplicationMultiElement(oid, phaseId, ruleIds, false, toSingleValueMap(multiValues));

        Target target = new Target.Builder()
                .setField("oid", oid)
                .setField("hakuOid", applicationSystemId)
                .setField("phaseId", phaseId)
                .build();

        auditLogRequest(HakuOperation.REFRESH_APPLICATION_VIEW, target);

        return new Viewable("/elements/JsonElementList.jsp", modelResponse.getModel());
    }

    @POST
    @Path("/hakemus/{oid}/additionalInfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Response saveAdditionalInfo(@PathParam(OID_PATH_PARAM) final String oid,
                                       final MultivaluedMap<String, String> post) throws URISyntaxException {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        LOGGER.debug("saveAdditionalInfo {}, {}", new Object[]{oid, multiValues});
        Map<String,String> vals = toSingleValueMap(multiValues);
        officerUIService.saveApplicationAdditionalInfo(oid, vals);

        Changes.Builder changesBuilder = new Changes.Builder();
        for (Map.Entry<String, String> entry : vals.entrySet()) {
            changesBuilder.added(entry.getKey(), entry.getValue());
        }

        Target target = new Target.Builder()
                .setField("oid", oid)
                .build();

        auditLogRequest(HakuOperation.SAVE_ADDITIONAL_INFO, target, changesBuilder.build());

        return redirectToOidResponse(oid);
    }

    @GET
    @Path("/hakemus/{oid}/additionalInfo")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getAdditionalInfo(@PathParam(OID_PATH_PARAM) final String oid) {
        LOGGER.debug("getAdditionalInfo  {}, {}", new Object[]{oid});
        ModelResponse modelResponse = officerUIService.getAdditionalInfo(oid);

        Target target = new Target.Builder().setField("oid", oid).build();
        auditLogRequest(HakuOperation.VIEW_ADDITIONAL_INFO, target);

        return new Viewable(ADDITIONAL_INFO_VIEW, modelResponse.getModel());
    }

    @POST
    @Path("/hakemus/{oid}/state")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_CRUD')")
    public Response state(@PathParam(OID_PATH_PARAM) final String oid, final MultivaluedMap<String, String> post) throws URISyntaxException {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        String reason = concatMultivaluedQueryParam("note", multiValues);
        Application.State state = Application.State.valueOf(multiValues.getFirst("state"));
        officerUIService.changeState(oid, state, reason);

        Target target = new Target.Builder()
                .setField("oid", oid)
                .setField("state", state.name())
                .setField("reason", reason).build();

        auditLogRequest(HakuOperation.CHANGE_APPLICATION_STATE, target);
        return redirectToOidResponse(oid);
    }

    @POST
    @Path("/hakemus/{oid}/note")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Response addNote(@PathParam(OID_PATH_PARAM) final String oid, final MultivaluedMap<String, String> post) throws URISyntaxException {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        String note = concatMultivaluedQueryParam("note-text", multiValues);
        officerUIService.addNote(oid, note);

        Target target = new Target.Builder().setField("oid", oid).build();
        Changes changes = new Changes.Builder().added("note", note).build();
        auditLogRequest(HakuOperation.ADD_NOTE, target, changes);

        return redirectToOidResponse(oid);
    }

    @POST
    @Path("/hakemus/{oid}/postProcess")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public Response postProcess(@PathParam(OID_PATH_PARAM) final String oid,
                                final MultivaluedMap<String, String> post) throws URISyntaxException {
        final MultivaluedMap<String, String> multiValues = filterOPHParameters(post);
        boolean email = Boolean.valueOf(multiValues.getFirst("email"));
        officerUIService.postProcess(oid, email);
        return redirectToOidResponse(oid);
    }

    @POST
    @Path("/hakemus/{oid}/processAttachmentsAndEligibility")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')")
    public void processAttachmentAndEligibility(@PathParam(OID_PATH_PARAM) final String oid,
                                                EligibilitiesDTO eligibilities) throws URISyntaxException {
        try {
            officerUIService.processAttachmentsAndEligibilities(oid, eligibilities);
        } catch (IllegalStateException ise) {
            throw new JSONException(Response.Status.CONFLICT, "FOOBAR", ise);
        }

    }

    @GET
    @Path("/hakemus/{oid}/print")
    @Produces(MediaType.TEXT_PLAIN)
    public Response applicationPrint(@PathParam(OID_PATH_PARAM) final String oid) throws URISyntaxException {
    	HttpResponse httpResponse = pdfService.getUriToPDF(oid);
    	URI location = UriUtil.pathSegmentsToUri(httpResponse.getFirstHeader("Content-Location").getValue());

        Target target = new Target.Builder().setField("oid", oid).build();
        auditLogRequest(HakuOperation.PRINT_APPLICATION, target);

    	return Response.seeOther(location).build();
    }

    @GET
    @Path("/hakemus/{oid}/print/view")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getApplicationPrintView(@PathParam(OID_PATH_PARAM) final String oid) {
        ModelResponse modelResponse = officerUIService.getApplicationPrint(oid);

        Target target = new Target.Builder().setField("oid", oid).build();
        auditLogRequest(HakuOperation.PRINT_PREVIEW_APPLICATION, target);

        return new Viewable(APPLICATION_PRINT_VIEW, modelResponse.getModel());
    }

    @POST
    @Path("/hakemus/email")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Deprecated // TODO NOT IN USE?
    public Response applicationEmail(ApplicationByEmailDTO applicationByEmail) throws URISyntaxException, IOException {
    	String id = emailService.sendApplicationByEmail(applicationByEmail);

        Target target = new Target.Builder().setField("oid", applicationByEmail.getApplicationOID()).build();
        auditLogRequest(HakuOperation.SEND_BY_EMAIL, target);

        return Response.ok(id).build();
    }

    @GET
    @Path("/hakemus/getApplicationByEmailDTO")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApplicationByEmailDTO() {
    	ApplicationTemplateDTO template = new ApplicationTemplateDTO();
    	
    	List<ApplicationReplacementDTO> replacements = new ArrayList<ApplicationReplacementDTO>();
    	ApplicationReplacementDTO replacement = new ApplicationReplacementDTO();
    	replacement.setName("name");
    	replacement.setValue("value");
    	replacements.add(replacement);
    	replacements.add(replacement);
    	
    	template.setTemplateReplacements(replacements);
    	
    	ApplicationByEmailDTO applicationByEmail = new ApplicationByEmailDTO();
    	applicationByEmail.setApplicationTemplate(template);
    	
    	return Response.ok(applicationByEmail).build();
    }
    
    @GET
    @Path("/hakemus/applicationSystems")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> getApplicationSystems() {
        List<ApplicationSystem> applicationSystemList = officerUIService.getApplicationSystems();
        List<Map<String, String>> applicationSystems = new ArrayList<Map<String, String>>(applicationSystemList.size());
        for (ApplicationSystem as : applicationSystemList) {
            Map<String, String> applicationSystem = new HashMap<String, String>();
            applicationSystem.put("id", as.getId());
            applicationSystem.put("hakukausiUri", as.getHakukausiUri());
            applicationSystem.put("hakukausiVuosi", as.getHakukausiVuosi().toString());
            applicationSystem.put("kohdejoukko", as.getKohdejoukkoUri());
            I18nText name = as.getName();
            Map<String, String> translations = name.getTranslations();
            for (Map.Entry<String, String> translation : translations.entrySet()) {
                String key = translation.getKey();
                String val = translation.getValue();
                applicationSystem.put("name_" + key, val);
            }
            applicationSystems.add(applicationSystem);
        }
        return applicationSystems;
    }

    @GET
    @Path("/hakemus/baseEducations/{kohdejoukko}")
    public List<Map<String, String>> getBaseEducations(@PathParam("kohdejoukko") final String kohdejoukko) {
        if (OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(kohdejoukko)) {
            return officerUIService.getHigherEdBaseEdOptions();
        } else {
            return null;
        }

    }

    @GET
    @Path("/autocomplete/{list}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> getAutocomplete(@PathParam("list") String list,
                                                     @QueryParam("term") String term) throws IOException {
        if ("school".equals(list)) {
            return officerUIService.getSchools(term);
        } else if ("preference".equals(list)) {
            return officerUIService.getPreferences(term);
        } else if ("group".equals(list)) {
            return officerUIService.getGroups(term);
        }
        return new ArrayList<Map<String, Object>>(0);
    }
    
    @POST
    @Path("/hakemus/note/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, String> getNamesForNoteUsers(List<String> oids) {
        return officerUIService.getNamesForNoteUsers(oids);
    }
    
    private String concatMultivaluedQueryParam(final String paramName, final MultivaluedMap<String, String> multiValues) {
        for (Map.Entry<String, List<String>> entry : multiValues.entrySet()) {
            LOGGER.debug(paramName + " " + entry.getKey() + " -> " + entry.getValue());
        }
        StringBuilder reasonBuilder = new StringBuilder();
        for (String reasonPart : multiValues.get(paramName)) {
            reasonBuilder.append(reasonPart);
        }
        return reasonBuilder.toString();
    }

    private Response redirectToOidResponse(String oid) throws URISyntaxException {
        return seeOther(UriUtil.pathSegmentsToUri(VIRKAILIJA_HAKEMUS_VIEW, oid, "")).build();
    }

    private void auditLogRequest(HakuOperation operation, Target target) {
        auditLogRequest(operation, target, null);
    }

    private void auditLogRequest(HakuOperation operation, Target target, Changes changes) {
        if(changes == null) {
            changes = new Changes.Builder().build();
        }
        User user = oppijaAuditLogger.getUser();
        oppijaAuditLogger.log(user, operation, target, changes);
    }
}
