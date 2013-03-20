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

import com.google.common.collect.Lists;
import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.oppija.lomake.domain.elements.custom.GradeGrid;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DataRelatedQuestion;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.AdditionalQuestionService;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserPrefillDataService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.ui.common.MultivaluedMapUtil;
import fi.vm.sade.oppija.ui.common.RedirectToFormViewPath;
import fi.vm.sade.oppija.ui.common.RedirectToPendingViewPath;
import fi.vm.sade.oppija.ui.common.RedirectToPhaseViewPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Component
@Path("/lomake")
public class FormController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    public static final String ROOT_VIEW = "/elements/Root";
    public static final String VERBOSE_HELP_VIEW = "/help";
    public static final String LINK_LIST_VIEW = "/linkList";
    public static final String VALMIS_VIEW = "/valmis";

    public static final String FORM_ID_PATH_PARAM = "formId";
    public static final String APPLICATION_PERIOD_ID_PATH_PARAM = "applicationPeriodId";
    public static final String THEME_ID_PATH_PARAM = "themeId";
    public static final String ELEMENT_ID_PATH_PARAM = "elementId";
    public static final String FORM_ID_STR_PATH_PARAM = "formIdStr";
    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final String PHASE_ID = "phaseId";

    final FormService formService;
    private final ApplicationService applicationService;
    private final UserPrefillDataService userPrefillDataService;
    private final AdditionalQuestionService additionalQuestionService;


    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService,
                          final ApplicationService applicationService, final UserPrefillDataService userPrefillDataService,
                          final AdditionalQuestionService additionalQuestionService) {
        this.formService = formService;
        this.applicationService = applicationService;
        this.userPrefillDataService = userPrefillDataService;
        this.additionalQuestionService = additionalQuestionService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable listApplicationPeriods() {
        LOGGER.debug("listApplicationPeriods");
        Map<String, ApplicationPeriod> applicationPerioidMap = formService.getApplicationPerioidMap();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("linkList", applicationPerioidMap.keySet());
        return new Viewable(LINK_LIST_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}") //NOSONAR Avoid Duplicate Literals. Sotkuseksi menee, jos
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable listForms(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId) {
        LOGGER.debug("listForms");
        ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(applicationPeriodId);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("path", applicationPeriod.getId() + "/");
        model.put("linkList", applicationPeriod.getFormIds());
        return new Viewable(LINK_LIST_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}/{formId}")
    public Response getApplication(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                   @PathParam(FORM_ID_PATH_PARAM) final String formId) throws URISyntaxException {
        LOGGER.debug("RedirectToLastPhase {}, {}", new Object[]{applicationPeriodId, formId});
        Application application = applicationService.getApplication(new FormId(applicationPeriodId, formId));
        if (application.isNew()) {

            Element firstPhase = formService.getFirstPhase(applicationPeriodId, formId);
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationPeriodId, formId, firstPhase.getId()).getPath())).build();

        } else {
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationPeriodId, formId,
                            application.getPhaseId()).getPath())).build();
        }
    }

    @GET
    @Path("/{applicationPeriodId}/{formIdStr}/esikatselu")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPreview(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                               @PathParam(FORM_ID_STR_PATH_PARAM) final String formIdStr) {
        return getElement(applicationPeriodId, formIdStr, "esikatselu");
    }

    @GET
    @Path("/{applicationPeriodId}/{formIdStr}/{elementId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getElement(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                               @PathParam(FORM_ID_STR_PATH_PARAM) final String formIdStr,
                               @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId) {

        LOGGER.debug("getElement {}, {}, {}", new Object[]{applicationPeriodId, formIdStr, elementId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        Element element = activeForm.getChildById(elementId);
        Map<String, Object> model = new HashMap<String, Object>();
        final FormId formId = new FormId(applicationPeriodId, activeForm.getId());
        Map<String, String> values = applicationService.getApplication(formId).getVastauksetMerged();
        values = userPrefillDataService.populateWithPrefillData(values);
        model.put("categoryData", values);
        model.put("element", element);
        model.put("template", element.getType());
        model.put("form", activeForm);
        model.put("hakemusId", formId);

        return new Viewable(ROOT_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}/{formId}/{elementId}/relatedData/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Serializable getElementRelatedData(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                              @PathParam(FORM_ID_PATH_PARAM) final String formId,
                                              @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                              @PathParam("key") final String key) {
        LOGGER.debug("getElementRelatedData {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, elementId, key});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        try {
            DataRelatedQuestion<Serializable> element = (DataRelatedQuestion<Serializable>) activeForm.getChildById(elementId);
            return element.getData(key);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return null;
        }
    }

    @POST
    @Path("/{applicationPeriodId}/{formId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response prefillForm(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                @PathParam(FORM_ID_PATH_PARAM) final String formId,
                                final MultivaluedMap<String, String> multiValues)
            throws URISyntaxException {
        userPrefillDataService.addUserPrefillData(MultivaluedMapUtil.toSingleValueMap(multiValues));

        return Response.seeOther(new URI(
                new RedirectToFormViewPath(applicationPeriodId, formId).getPath())).build();
    }

    @POST
    @Path("/{applicationPeriodId}/{formId}/esikatselu")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response submitApplication(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                      @PathParam(FORM_ID_PATH_PARAM) final String formId) throws URISyntaxException {
        LOGGER.debug("submitApplication {}, {}", new Object[]{applicationPeriodId, formId});
        String oid = applicationService.submitApplication(new FormId(applicationPeriodId, formId));
        RedirectToPendingViewPath redirectToPendingViewPath = new RedirectToPendingViewPath(applicationPeriodId, formId, oid);
        return Response.seeOther(new URI(redirectToPendingViewPath.getPath())).build();
    }

    @POST
    @Path("/{applicationPeriodId}/{formId}/{phaseId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response savePhase(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                              @PathParam(FORM_ID_PATH_PARAM) final String formId,
                              @PathParam(PHASE_ID) final String phaseId,
                              final MultivaluedMap<String, String> multiValues) throws URISyntaxException {
        LOGGER.debug("savePhase {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, phaseId, multiValues});
        final FormId hakuLomakeId = new FormId(applicationPeriodId, formId);
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        boolean skipValidators = skipValidators(multiValues, activeForm, phaseId);

        ApplicationState applicationState = applicationService.saveApplicationPhase(
                new ApplicationPhase(hakuLomakeId, phaseId, MultivaluedMapUtil.toSingleValueMap(multiValues)), skipValidators);

        Map<String, Object> model = new HashMap<String, Object>();

        model.put("hakemusId", hakuLomakeId);
        if (applicationState.isValid()) {
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationPeriodId, formId,
                            applicationState.getHakemus().getPhaseId()).getPath())).build();

        } else {
            model.putAll(applicationState.getModelObjects());
            Element phase = activeForm.getPhase(phaseId);
            model.put("element", phase);
            model.put("form", activeForm);
            model.put("template", phase.getType());
            return Response.status(Response.Status.OK).entity(new Viewable(ROOT_VIEW, model)).build();
        }

    }

    private boolean skipValidators(MultivaluedMap<String, String> multiValues, Form form, String phaseId) {
        List<String> phaseIdList = multiValues.get(PHASE_ID);
        if (phaseIdList == null || phaseIdList.size() == 0) {
            return false;
        }

        String targetPhaseId = phaseIdList.get(0);
        boolean skipValidators = targetPhaseId.endsWith("-skip-validators");
        if (skipValidators) {
            targetPhaseId = targetPhaseId.substring(0, targetPhaseId.lastIndexOf("-skip-validators"));
            multiValues.get(PHASE_ID).set(0, targetPhaseId);
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

    @GET
    @Path("/{applicationPeriodId}/{formId}/valmis/{oid}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getComplete(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                @PathParam(FORM_ID_PATH_PARAM) final String formId,
                                @PathParam("oid") final String oid) {

        LOGGER.debug("getComplete {}, {}", new Object[]{applicationPeriodId, formId});
        Map<String, Object> model = new HashMap<String, Object>();
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        model.put("form", activeForm);
        final FormId hakuLomakeId = new FormId(applicationPeriodId, activeForm.getId());

        final Application application;
        try {
            application = applicationService.getPendingApplication(hakuLomakeId, oid);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundExceptionRuntime("Could not find pending application", e);
        }

        model.put("categoryData", application.getAnswers());
        model.put("hakemusId", hakuLomakeId);
        model.put("applicationNumber", application.getOid());
        return new Viewable(VALMIS_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}/{formId}/{vaiheId}/{themeId}/help")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getFormHelp(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                @PathParam(FORM_ID_PATH_PARAM) final String formId, @PathParam("vaiheId") final String vaiheId,
                                @PathParam(THEME_ID_PATH_PARAM) final String themeId) {

        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Map<String, Object> model = new HashMap<String, Object>();
        Element theme = activeForm.getChildById(themeId);
        model.put("theme", theme);
        List<Element> listsOfTitledElements = new ArrayList<Element>();
        for (Element tElement : theme.getChildren()) {
            if (tElement instanceof Titled) {
                listsOfTitledElements.add(tElement);
            }
        }
        model.put("listsOfTitledElements", listsOfTitledElements);

        return new Viewable(VERBOSE_HELP_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}/{formIdStr}/{gradeGridId}/additionalLanguageRow")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getAdditionalLanguageRow(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                             @PathParam(FORM_ID_STR_PATH_PARAM) final String formIdStr,
                                             @PathParam("gradeGridId") final String gradeGridId) {

        LOGGER.debug("getAdditionalLanguageRow {}, {}, {}", new Object[]{applicationPeriodId, formIdStr, gradeGridId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        Element element = activeForm.getChildById(gradeGridId);
        GradeGrid gradeGrid = (GradeGrid) element;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("element", gradeGrid);
        model.put("template", "gradegrid/additionalLanguageRow");

        return new Viewable(ROOT_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}/{formIdStr}/{phaseId}/{themeId}/additionalquestions/{aoId}")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable getAdditionalQuestions(@PathParam("applicationPeriodId") final String applicationPeriodId,
                                           @PathParam("formIdStr") final String formIdStr,
                                           @PathParam(PHASE_ID) final String phaseId,
                                           @PathParam("themeId") final String themeId,
                                           @PathParam("aoId") final String aoId,
                                           @QueryParam("preview") final boolean preview) {
        String viewName = preview ? "/additionalQuestionsPreview" : "/additionalQuestions";

        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        final FormId formId = new FormId(applicationPeriodId, activeForm.getId());
        Set<Question> additionalQuestions = additionalQuestionService.
                findAdditionalQuestions(themeId, Lists.newArrayList(aoId), formId, phaseId);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("additionalQuestions", additionalQuestions);
        model.put("categoryData", applicationService.getApplication(formId).getVastauksetMerged());
        return new Viewable(viewName, model);
    }
}
