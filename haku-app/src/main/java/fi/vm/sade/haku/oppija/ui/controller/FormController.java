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
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserHolder;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.ui.common.MultivaluedMapUtil;
import fi.vm.sade.haku.oppija.ui.common.RedirectToFormViewPath;
import fi.vm.sade.haku.oppija.ui.common.RedirectToPendingViewPath;
import fi.vm.sade.haku.oppija.ui.common.RedirectToPhaseViewPath;
import fi.vm.sade.haku.oppija.ui.service.UIService;
import fi.vm.sade.haku.oppija.ui.service.UIServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Component
@Path("lomake")
public class FormController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    public static final String ROOT_VIEW = "/elements/Root";
    public static final String VERBOSE_HELP_VIEW = "/help";
    public static final String APPLICATION_SYSTEM_LIST_VIEW = "/applicationSystemList";
    public static final String VALMIS_VIEW = "/valmis";
    public static final String PRINT_VIEW = "/print/print";

    public static final String APPLICATION_SYSTEM_ID_PATH_PARAM = "applicationSystemId";
    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final String PHASE_ID_PATH_PARAM = "phaseId";
    public static final String ELEMENT_ID_PATH_PARAM = "elementId";
    public static final int MAX_PREFILL_PARAMETERS = 100;
    public static final String MODEL_KEY_ELEMENT = "element";
    public static final String MODEL_KEY_TEMPLATE = "template";
    public static final String MODEL_KEY_FORM = "form";
    public static final String MODEL_KEY_APPLICATION_SYSTEM_ID = "applicationSystemId";
    public static final String MODEL_KEY_CATEGORY_DATA = "categoryData";
    public static final String MODEL_KEY_KOULUTUSINFORMAATIO_BASE_URL = "koulutusinformaatioBaseUrl";

    private final FormService formService;
    private final ApplicationService applicationService;
    private final UserHolder userHolder;
    private final String koulutusinformaatioBaseUrl;
    private final UIService uiService;
    private final ApplicationSystemService applicationSystemService;

    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService,
                          final ApplicationService applicationService, final UserHolder userHolder,
                          @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl,
                          final UIService uiService, ApplicationSystemService applicationSystemService) {
        this.formService = formService;
        this.applicationService = applicationService;
        this.userHolder = userHolder;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
        this.uiService = uiService;
        this.applicationSystemService = applicationSystemService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable listApplicationSystems() {
        LOGGER.debug("listApplicationSystems");
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("applicationSystems", applicationSystemService.getAllApplicationSystems("id", "name", "applicationPeriods"));
        return new Viewable(APPLICATION_SYSTEM_LIST_VIEW, model);
    }

    @GET
    @Path("/{applicationSystemId}")
    public Response getApplication(
            @PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) throws URISyntaxException {
        LOGGER.debug("RedirectToLastPhase {}", new Object[]{applicationSystemId});
        Application application = userHolder.getApplication(applicationSystemId);
        if (application.isNew()) {
            Element firstPhase = formService.getFirstPhase(applicationSystemId);
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationSystemId, firstPhase.getId()).getPath())).build();

        } else {
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationSystemId,
                            application.getPhaseId()).getPath())).build();
        }
    }

    @POST
    @Path("/{applicationSystemId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Response prefillForm(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                final MultivaluedMap<String, String> multiValues)
            throws URISyntaxException {
        if (multiValues.size() > MAX_PREFILL_PARAMETERS) {
            throw new IllegalArgumentException("Too many prefill data values");
        }
        userHolder.addPrefillData(applicationSystemId, MultivaluedMapUtil.toSingleValueMap(multiValues));

        return Response.seeOther(new URI(
                new RedirectToFormViewPath(applicationSystemId).getPath())).build();
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhase(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId) {

        LOGGER.debug("getPhase {}, {}", applicationSystemId, phaseId);
        Form activeForm = formService.getActiveForm(applicationSystemId);
        ElementTree elementTree = new ElementTree(activeForm);
        Element element = elementTree.getChildById(phaseId);
        Map<String, Object> model = new HashMap<String, Object>();
        Application application = applicationService.getApplication(applicationSystemId);
        elementTree.checkPhaseTransfer(application.getPhaseId(), phaseId);
        Map<String, String> values = application.getVastauksetMerged();
        values = userHolder.populateWithPrefillData(values);
        model.put(MODEL_KEY_CATEGORY_DATA, values);
        model.put(MODEL_KEY_ELEMENT, element);
        model.put(MODEL_KEY_TEMPLATE, element.getType());
        model.put(MODEL_KEY_FORM, activeForm);
        model.put(MODEL_KEY_APPLICATION_SYSTEM_ID, applicationSystemId);
        model.put(MODEL_KEY_KOULUTUSINFORMAATIO_BASE_URL, koulutusinformaatioBaseUrl);

        return new Viewable(ROOT_VIEW, model);
    }

    @GET
    @Path("/{applicationSystemId}/esikatselu")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPreview(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) {
        Form activeForm = formService.getActiveForm(applicationSystemId);
        Map<String, Object> model = new HashMap<String, Object>();
        Application application = applicationService.getApplication(applicationSystemId);
        Map<String, String> values = application.getVastauksetMerged();
        model.put(MODEL_KEY_CATEGORY_DATA, values);
        model.put(MODEL_KEY_ELEMENT, activeForm);
        model.put(MODEL_KEY_TEMPLATE, activeForm.getType());
        model.put(MODEL_KEY_FORM, activeForm);
        model.put(MODEL_KEY_APPLICATION_SYSTEM_ID, applicationSystemId);
        return new Viewable(ROOT_VIEW, model);
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}/{elementId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhaseElement(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                    @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                    @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId) {
        LOGGER.debug("getPhaseElement {}, {}", applicationSystemId, phaseId);
        Form activeForm = formService.getActiveForm(applicationSystemId);
        ElementTree elementTree = new ElementTree(activeForm);
        Map<String, Object> model = new HashMap<String, Object>();
        Application application = applicationService.getApplication(applicationSystemId);
        elementTree.checkPhaseTransfer(application.getPhaseId(), phaseId);
        Element element = elementTree.getChildById(elementId);
        Map<String, String> values = application.getVastauksetMerged();
        values = userHolder.populateWithPrefillData(values);
        model.put(MODEL_KEY_CATEGORY_DATA, values);
        model.put(MODEL_KEY_ELEMENT, element);
        model.put(MODEL_KEY_TEMPLATE, element.getType());
        model.put(MODEL_KEY_FORM, activeForm);
        model.put(MODEL_KEY_APPLICATION_SYSTEM_ID, applicationSystemId);
        model.put(MODEL_KEY_KOULUTUSINFORMAATIO_BASE_URL, koulutusinformaatioBaseUrl);

        return new Viewable(ROOT_VIEW, model);
    }

    @POST
    @Path("/{applicationSystemId}/{phaseId}/{elementId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Viewable updateRules(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                final MultivaluedMap<String, String> multiValues) {
        LOGGER.debug("updateRules {}, {}, {}", applicationSystemId, phaseId);
        Form activeForm = formService.getActiveForm(applicationSystemId);
        Element element = new ElementTree(activeForm).getChildById(elementId);

        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, String> values = applicationService.getApplication(applicationSystemId).getVastauksetMerged();
        values.putAll(MultivaluedMapUtil.toSingleValueMap(multiValues));
        model.put(MODEL_KEY_CATEGORY_DATA, values);
        model.put(MODEL_KEY_ELEMENT, element);
        model.put(MODEL_KEY_TEMPLATE, element.getType());
        model.put(MODEL_KEY_FORM, activeForm);
        model.put(MODEL_KEY_APPLICATION_SYSTEM_ID, applicationSystemId);
        model.put(MODEL_KEY_KOULUTUSINFORMAATIO_BASE_URL, koulutusinformaatioBaseUrl);

        return new Viewable(ROOT_VIEW, model);
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}/{elementId}/relatedData/{key}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public Serializable getElementRelatedData(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                              @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                              @PathParam("key") final String key) {
        LOGGER.debug("getElementRelatedData {}, {}, {}, {}", applicationSystemId, elementId, key);
        Form activeForm = formService.getActiveForm(applicationSystemId);
        return new ElementTree(activeForm).getRelatedData(elementId, key);
    }

    @POST
    @Path("/{applicationSystemId}/esikatselu")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Response submitApplication(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) throws URISyntaxException {
        LOGGER.debug("submitApplication {}", new Object[]{applicationSystemId});
        String oid = applicationService.submitApplication(applicationSystemId);
        RedirectToPendingViewPath redirectToPendingViewPath = new RedirectToPendingViewPath(applicationSystemId, oid);
        return Response.seeOther(new URI(redirectToPendingViewPath.getPath())).build();
    }

    @POST
    @Path("/{applicationSystemId}/{phaseId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Response savePhase(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                              final MultivaluedMap<String, String> multiValues) throws URISyntaxException {
        LOGGER.debug("savePhase {}, {}, {}, {}", applicationSystemId, phaseId, multiValues);
        Form activeForm = formService.getActiveForm(applicationSystemId);
        ApplicationState applicationState = applicationService.saveApplicationPhase(
                new ApplicationPhase(applicationSystemId, phaseId, MultivaluedMapUtil.toSingleValueMap(multiValues)));
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(MODEL_KEY_APPLICATION_SYSTEM_ID, applicationSystemId);
        if (applicationState.isValid()) {
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationSystemId,
                            applicationState.getApplication().getPhaseId()).getPath())).build();

        } else {
            LOGGER.debug("Invalid fields: {}", applicationState.getErrors().keySet());
            model.putAll(applicationState.getModelObjects());
            Element phase = new ElementTree(activeForm).getChildById(phaseId);
            model.put(MODEL_KEY_ELEMENT, phase);
            model.put(MODEL_KEY_FORM, activeForm);
            model.put(MODEL_KEY_TEMPLATE, phase.getType());
            model.put(MODEL_KEY_KOULUTUSINFORMAATIO_BASE_URL, koulutusinformaatioBaseUrl);
            return Response.status(Response.Status.OK).entity(new Viewable(ROOT_VIEW, model)).build();
        }

    }

    @GET
    @Path("/{applicationSystemId}/valmis/{oid}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getComplete(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam("oid") final String oid) throws ResourceNotFoundException {

        LOGGER.debug("getComplete {}, {}", new Object[]{applicationSystemId});
        UIServiceResponse response = uiService.getApplicationComplete(applicationSystemId, oid);
        return new Viewable(VALMIS_VIEW, response.getModel());
    }

    @GET
    @Path("/{applicationSystemId}/tulostus/{oid}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPrint(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                             @PathParam("oid") final String oid) throws ResourceNotFoundException {
        LOGGER.debug("getPrint {}, {}", new Object[]{applicationSystemId, oid});
        UIServiceResponse uiServiceResponse = uiService.getApplicationPrint(applicationSystemId, oid);
        return new Viewable(PRINT_VIEW, uiServiceResponse.getModel());
    }

    @GET
    @Path("/{applicationSystemId}/{elementId}/help")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getFormHelp(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId) throws ResourceNotFoundException {
        return new Viewable(VERBOSE_HELP_VIEW, uiService.getElementHelp(applicationSystemId, elementId));
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}/{gradeGridId}/additionalLanguageRow")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getAdditionalLanguageRow(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                             @PathParam("gradeGridId") final String gradeGridId) {

        LOGGER.debug("getAdditionalLanguageRow {}, {}, {}", applicationSystemId, gradeGridId);
        Form activeForm = formService.getActiveForm(applicationSystemId);
        Element element = new ElementTree(activeForm).getChildById(gradeGridId);
        GradeGrid gradeGrid = (GradeGrid) element;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(MODEL_KEY_ELEMENT, gradeGrid);
        model.put(MODEL_KEY_TEMPLATE, "gradegrid/additionalLanguageRow");
        return new Viewable(ROOT_VIEW, model);
    }
}
