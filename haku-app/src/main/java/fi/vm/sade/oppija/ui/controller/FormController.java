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
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DataRelatedQuestion;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.domain.rules.LanguageTestRule;
import fi.vm.sade.oppija.lomake.service.AdditionalQuestionService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Path("/lomake")
public class FormController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    public static final String ROOT_VIEW = "/elements/Root";
    public static final String VERBOSE_HELP_VIEW = "/help";
    public static final String APPLICATION_PERIOD_LIST_VIEW = "/applicationPeriodList";
    public static final String VALMIS_VIEW = "/valmis";
    public static final String PRINT_VIEW = "/print/print";

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
    private final UIService uiService;

    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService,
                          final ApplicationService applicationService, final UserHolder userHolder,
                          final AdditionalQuestionService additionalQuestionService,
                          @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl,
                          final UIService uiService) {
        this.formService = formService;
        this.applicationService = applicationService;
        this.userHolder = userHolder;
        this.additionalQuestionService = additionalQuestionService;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
        this.uiService = uiService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable listApplicationPeriods() {
        LOGGER.debug("listApplicationPeriods");
        Map<String, ApplicationPeriod> applicationPerioidMap = formService.getApplicationPerioidMap();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("applicationPeriods", applicationPerioidMap.values());
        return new Viewable(APPLICATION_PERIOD_LIST_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}")
    public Response getApplication(
            @PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId) throws URISyntaxException {
        LOGGER.debug("RedirectToLastPhase {}", new Object[]{applicationPeriodId});
        Application application = userHolder.getApplication(applicationPeriodId);
        if (application.isNew()) {
            Element firstPhase = formService.getFirstPhase(applicationPeriodId);
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationPeriodId, firstPhase.getId()).getPath())).build();

        } else {
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationPeriodId,
                            application.getPhaseId()).getPath())).build();
        }
    }

    @GET
    @Path("/{applicationPeriodId}/esikatselu")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPreview(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId) {
        return getPhase(applicationPeriodId, "esikatselu");
    }

    @GET
    @Path("/{applicationPeriodId}/{phaseId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhase(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId) {

        LOGGER.debug("getElement {}, {}, {}", applicationPeriodId, phaseId);
        Form activeForm = formService.getActiveForm(applicationPeriodId);
        Element element = activeForm.getChildById(phaseId);
        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, String> values = applicationService.getApplication(applicationPeriodId).getVastauksetMerged();
        values = userHolder.populateWithPrefillData(values);
        model.put("categoryData", values);
        model.put("element", element);
        model.put("template", element.getType());
        model.put("form", activeForm);
        model.put("applicationPeriodId", applicationPeriodId);
        model.put("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);

        return new Viewable(ROOT_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}/{phaseId}/{elementId}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPhaseElement(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                    @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                    @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId) {
        return getPhase(applicationPeriodId, elementId);
    }

    @GET
    @Path("/{applicationPeriodId}/{phaseId}/{elementId}/languageTest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public Serializable getLanguageTestChildren(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                                @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                                @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                                @QueryParam("ai") final String aidinkieli,
                                                @QueryParam("a1") final String a1Kieli,
                                                @QueryParam("a2") final String a2Kieli,
                                                @QueryParam("a1Grade") final String a1Grade,
                                                @QueryParam("a2Grade") final String a2Grade) {
        LOGGER.debug("getLanguageTest {}, {}, {}, {}", applicationPeriodId, phaseId, elementId);
        Form activeForm = formService.getActiveForm(applicationPeriodId);
        LanguageTestRule rule = (LanguageTestRule) activeForm.getChildById(elementId);
        return rule.getTests(aidinkieli, a1Kieli, a2Kieli, a1Grade, a2Grade);
    }

    @GET
    @Path("/{applicationPeriodId}/{phaseId}/{elementId}/relatedData/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Serializable getElementRelatedData(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                              @PathParam(ELEMENT_ID_PATH_PARAM) final String elementId,
                                              @PathParam("key") final String key) {
        LOGGER.debug("getElementRelatedData {}, {}, {}, {}", applicationPeriodId, elementId, key);
        Form activeForm = formService.getActiveForm(applicationPeriodId);
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
    @Path("/{applicationPeriodId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response prefillForm(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                final MultivaluedMap<String, String> multiValues)
            throws URISyntaxException {
        userHolder.addPrefillData(MultivaluedMapUtil.toSingleValueMap(multiValues));

        return Response.seeOther(new URI(
                new RedirectToFormViewPath(applicationPeriodId).getPath())).build();
    }

    @POST
    @Path("/{applicationPeriodId}/esikatselu")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response submitApplication(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId) throws URISyntaxException {
        LOGGER.debug("submitApplication {}", new Object[]{applicationPeriodId});
        String oid = applicationService.submitApplication(applicationPeriodId);
        RedirectToPendingViewPath redirectToPendingViewPath = new RedirectToPendingViewPath(applicationPeriodId, oid);
        return Response.seeOther(new URI(redirectToPendingViewPath.getPath())).build();
    }

    @POST
    @Path("/{applicationPeriodId}/{phaseId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response savePhase(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                              final MultivaluedMap<String, String> multiValues) throws URISyntaxException {
        LOGGER.debug("savePhase {}, {}, {}, {}", applicationPeriodId, phaseId, multiValues);
        Form activeForm = formService.getActiveForm(applicationPeriodId);
        boolean skipValidators = skipValidators(multiValues, activeForm, phaseId);
        ApplicationState applicationState = applicationService.saveApplicationPhase(
                new ApplicationPhase(applicationPeriodId, phaseId, MultivaluedMapUtil.toSingleValueMap(multiValues)), skipValidators);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("applicationPeriodId", applicationPeriodId);
        if (applicationState.isValid()) {
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationPeriodId,
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
    @Path("/{applicationPeriodId}/valmis/{oid}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getComplete(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                @PathParam("oid") final String oid) throws ResourceNotFoundException {

        LOGGER.debug("getComplete {}, {}", new Object[]{applicationPeriodId});
        UIServiceResponse response = uiService.getApplicationComplete(applicationPeriodId, oid);
        return new Viewable(VALMIS_VIEW, response.getModel());
    }

    @GET
    @Path("/{applicationPeriodId}/tulostus/{oid}")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getPrint(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                             @PathParam("oid") final String oid) throws ResourceNotFoundException {
        LOGGER.debug("getPrint {}, {}", new Object[]{applicationPeriodId, oid});
        UIServiceResponse uiServiceResponse = uiService.getApplicationPrint(applicationPeriodId, oid);
        return new Viewable(PRINT_VIEW, uiServiceResponse.getModel());
    }

    @GET
    @Path("/{applicationPeriodId}/{phaseId}/{themeId}/help")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getFormHelp(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                @PathParam(THEME_ID_PATH_PARAM) final String themeId) {

        Form activeForm = formService.getActiveForm(applicationPeriodId);
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
    @Path("/{applicationPeriodId}/{phaseId}/{gradeGridId}/additionalLanguageRow")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getAdditionalLanguageRow(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                             @PathParam("gradeGridId") final String gradeGridId) {

        LOGGER.debug("getAdditionalLanguageRow {}, {}, {}", applicationPeriodId, gradeGridId);
        Form activeForm = formService.getActiveForm(applicationPeriodId);
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
     * @param phaseId             phase id
     * @param themeId             theme id
     * @param aoId                application option id
     * @return list of questions
     */
    @GET
    @Path("/{applicationSystemId}/{phaseId}/{themeId}/additionalquestions/{aoId}")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable getAdditionalQuestions(@PathParam("applicationSystemId") final String applicationSystemId,
                                           @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                                           @PathParam("themeId") final String themeId,
                                           @PathParam("aoId") final String aoId
    ) {
        LOGGER.debug("getAdditionalQuestions {}, {}, {}, {}, {}", applicationSystemId, phaseId, themeId, aoId);
        List<Question> additionalQuestions = additionalQuestionService.
                findAdditionalQuestions(applicationSystemId, themeId, aoId);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("additionalQuestions", additionalQuestions);
        model.put("categoryData", applicationService.getApplication(applicationSystemId).getVastauksetMerged());
        return new Viewable("/additionalQuestions", model);
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
