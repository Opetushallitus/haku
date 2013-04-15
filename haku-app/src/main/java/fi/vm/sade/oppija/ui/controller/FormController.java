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
import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.ui.common.MultivaluedMapUtil;
import fi.vm.sade.oppija.ui.common.RedirectToFormViewPath;
import fi.vm.sade.oppija.ui.common.RedirectToPendingViewPath;
import fi.vm.sade.oppija.ui.common.RedirectToPhaseViewPath;
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
    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final String PHASE_ID_PATH_PARAM = "phaseId";
    public static final String ELEMENT_ID_PATH_PARAM = "elementId";

    final FormService formService;
    private final ApplicationService applicationService;
    private final UserHolder userHolder;
    private final AdditionalQuestionService additionalQuestionService;
    private final String koulutusinformaatioBaseUrl;

    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService,
                          final ApplicationService applicationService, final UserHolder userHolder,
                          final AdditionalQuestionService additionalQuestionService,
                          @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUr) {
        this.formService = formService;
        this.applicationService = applicationService;
        this.userHolder = userHolder;
        this.additionalQuestionService = additionalQuestionService;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUr;
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
    @Path("/{applicationPeriodId}/{formId}/{phaseId}/{elementId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhaseElement(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                    @PathParam(FORM_ID_PATH_PARAM) final String formId,
                                    @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                    @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId) {
        return getPhase(applicationPeriodId, formId, elementId);
    }

    @GET
    @Path("/{applicationPeriodId}/{formId}/esikatselu")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPreview(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                               @PathParam(FORM_ID_PATH_PARAM) final String formId) {
        return getPhase(applicationPeriodId, formId, "esikatselu");
    }

    @GET
    @Path("/{applicationPeriodId}/{formId}/{phaseId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhase(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                             @PathParam(FORM_ID_PATH_PARAM) final String formId,
                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId) {

        LOGGER.debug("getElement {}, {}, {}", new Object[]{applicationPeriodId, formId, phaseId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Element element = activeForm.getChildById(phaseId);
        Map<String, Object> model = new HashMap<String, Object>();
        final FormId formIdentifier = new FormId(applicationPeriodId, activeForm.getId());
        Map<String, String> values = applicationService.getApplication(formIdentifier).getVastauksetMerged();
        values = userHolder.populateWithPrefillData(values);
        model.put("categoryData", values);
        model.put("element", element);
        model.put("template", element.getType());
        model.put("form", activeForm);
        model.put("hakemusId", formIdentifier);
        model.put("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);

        return new Viewable(ROOT_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}/{formId}/{phaseId}/{elementId}/relatedData/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Serializable getElementRelatedData(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                              @PathParam(FORM_ID_PATH_PARAM) final String formId,
                                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
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
        userHolder.addPrefillData(MultivaluedMapUtil.toSingleValueMap(multiValues));

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
                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
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
    @Path("/{applicationPeriodId}/{formId}/{phaseId}/{gradeGridId}/additionalLanguageRow")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getAdditionalLanguageRow(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                             @PathParam(FORM_ID_PATH_PARAM) final String formId,
                                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                             @PathParam("gradeGridId") final String gradeGridId) {

        LOGGER.debug("getAdditionalLanguageRow {}, {}, {}", new Object[]{applicationPeriodId, formId, gradeGridId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Element element = activeForm.getChildById(gradeGridId);
        GradeGrid gradeGrid = (GradeGrid) element;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("element", gradeGrid);
        model.put("template", "gradegrid/additionalLanguageRow");
        return new Viewable(ROOT_VIEW, model);
    }

    /**
     * Searches for additional questions related to an application option
     * and its education degree and sora requirement.
     *
     * @param applicationSystemId application system id
     * @param formIdStr form that is used
     * @param phaseId phase id
     * @param themeId theme id
     * @param aoId application option id
     * @param preview is for preview (optional)
     * @param ed education degree of the application option (optional)
     * @param sora is sora question required (optional)
     * @return list of questions
     */
    @GET
    @Path("/{applicationSystemId}/{formIdStr}/{phaseId}/{themeId}/additionalquestions/{aoId}")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable getAdditionalQuestions(@PathParam("applicationSystemId") final String applicationSystemId,
                                           @PathParam("formIdStr") final String formIdStr,
                                           @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                           @PathParam("themeId") final String themeId,
                                           @PathParam("aoId") final String aoId,
                                           @QueryParam("preview") final boolean preview,
                                           @QueryParam("ed") final Integer ed,
                                           @QueryParam("sora") final Boolean sora
                                           ) {
        LOGGER.debug("getAdditionalQuestions {}, {}, {}, {}, {}, {}", new Object[]{applicationSystemId,
                formIdStr, phaseId, themeId, aoId, preview});
        String viewName = preview ? "/additionalQuestionsPreview" : "/additionalQuestions";

        final FormId formId = new FormId(applicationSystemId, formIdStr);
        Set<Question> additionalQuestions = additionalQuestionService.
                findAdditionalQuestions(formId, phaseId, themeId, aoId, ed, sora);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("additionalQuestions", additionalQuestions);
        model.put("categoryData", applicationService.getApplication(formId).getVastauksetMerged());
        return new Viewable(viewName, model);
    }

}
