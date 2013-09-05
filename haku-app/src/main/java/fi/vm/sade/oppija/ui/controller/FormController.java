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
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DataRelatedQuestion;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.ui.common.MultivaluedMapUtil;
import fi.vm.sade.oppija.ui.common.RedirectToFormViewPath;
import fi.vm.sade.oppija.ui.common.RedirectToPendingViewPath;
import fi.vm.sade.oppija.ui.common.RedirectToPhaseViewPath;
import fi.vm.sade.oppija.ui.service.UIService;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
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
import java.util.List;
import java.util.Map;

@Component
@Path("/lomake")
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

    final FormService formService;
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
        model.put("applicationSystems", applicationSystemService.getAllApplicationSystems());
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
        userHolder.addPrefillData(MultivaluedMapUtil.toSingleValueMap(multiValues));

        return Response.seeOther(new URI(
                new RedirectToFormViewPath(applicationSystemId).getPath())).build();
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhase(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId) {

        LOGGER.debug("getElement {}, {}", applicationSystemId, phaseId);
        Form activeForm = formService.getActiveForm(applicationSystemId);
        Element element = activeForm.getChildById(phaseId);
        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, String> values = applicationService.getApplication(applicationSystemId).getVastauksetMerged();
        values = userHolder.populateWithPrefillData(values);
        model.put("categoryData", values);
        model.put("element", element);
        model.put("template", element.getType());
        model.put("form", activeForm);
        model.put("applicationSystemId", applicationSystemId);
        model.put("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);

        return new Viewable(ROOT_VIEW, model);
    }

    @GET
    @Path("/{applicationSystemId}/esikatselu")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPreview(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId) {
        return getPhase(applicationSystemId, "esikatselu");
    }

    @GET
    @Path("/{applicationSystemId}/{phaseId}/{elementId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhaseElement(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                    @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                    @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId) {
        return getPhase(applicationSystemId, elementId);
    }

    @POST
    @Path("/{applicationSystemId}/{phaseId}/{elementId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + CHARSET_UTF_8)
    public Viewable updateRules(@PathParam(APPLICATION_SYSTEM_ID_PATH_PARAM) final String applicationSystemId,
                                @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                final MultivaluedMap<String, String> multiValues) {
        LOGGER.debug("getElement {}, {}, {}", applicationSystemId, phaseId);
        Form activeForm = formService.getActiveForm(applicationSystemId);
        Element element = activeForm.getChildById(elementId);

        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, String> values = applicationService.getApplication(applicationSystemId).getVastauksetMerged();
        values.putAll(MultivaluedMapUtil.toSingleValueMap(multiValues));
        model.put("categoryData", values);
        model.put("element", element);
        model.put("template", element.getType());
        model.put("form", activeForm);
        model.put("applicationSystemId", applicationSystemId);
        model.put("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);

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
        try {
            @SuppressWarnings("unchecked")
            DataRelatedQuestion<Serializable> element =
                    (DataRelatedQuestion<Serializable>) activeForm.getChildById(elementId);
            return element.getData(key);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return null;
        }
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
        boolean skipValidators = skipValidators(multiValues, activeForm, phaseId);
        ApplicationState applicationState = applicationService.saveApplicationPhase(
                new ApplicationPhase(applicationSystemId, phaseId, MultivaluedMapUtil.toSingleValueMap(multiValues)), skipValidators);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("applicationSystemId", applicationSystemId);
        if (applicationState.isValid()) {
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationSystemId,
                            applicationState.getApplication().getPhaseId()).getPath())).build();

        } else {
            model.putAll(applicationState.getModelObjects());
            Element phase = activeForm.getChildById(phaseId);
            model.put("element", phase);
            model.put("form", activeForm);
            model.put("template", phase.getType());
            model.put("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);
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
        Element element = activeForm.getChildById(gradeGridId);
        GradeGrid gradeGrid = (GradeGrid) element;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("element", gradeGrid);
        model.put("template", "gradegrid/additionalLanguageRow");
        return new Viewable(ROOT_VIEW, model);
    }

    private boolean skipValidators(MultivaluedMap<String, String> multiValues, Form form, String phaseId) {
        List<String> phaseIdList = multiValues.get(PHASE_ID_PATH_PARAM);
        if (phaseIdList == null || phaseIdList.size() == 0) {
            return false;
        }

        String targetPhaseId = phaseIdList.get(0);
        boolean skipValidators = targetPhaseId.endsWith("-skip-validators");
        if (skipValidators) {
            targetPhaseId = targetPhaseId.substring(0, targetPhaseId.lastIndexOf("-skip-validators"));
            multiValues.get(PHASE_ID_PATH_PARAM).set(0, targetPhaseId);
        }

        for (Element phase : form.getChildren()) {
            if (phase.getId().equals(targetPhaseId)) {
                return skipValidators;
            } else if (phase.getId().equals(phaseId)) {
                return false; // Never skip validators when moving forwards
            }
        }
        return false; // Do not skip, if neither the target phase nor the current phase was found in form's phases.
    }

}
