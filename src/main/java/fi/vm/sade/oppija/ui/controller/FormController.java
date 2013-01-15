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
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.*;
import fi.vm.sade.oppija.lomake.domain.elements.custom.GradeGrid;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DataRelatedQuestion;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserPrefillDataService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Path("/lomake")
public class FormController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    public static final String DEFAULT_VIEW = "/elements/Phase";
    public static final String ROOT_VIEW = "/elements/Root";
    public static final String VERBOSE_HELP_VIEW = "/help";
    public static final String LINK_LIST_VIEW = "/linkList";
    public static final String VALMIS_VIEW = "/valmis";

    final FormService formService;
    private final ApplicationService applicationService;
    private final UserPrefillDataService userPrefillDataService;

    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService,
                          final ApplicationService applicationService, final UserPrefillDataService userPrefillDataService) {
        this.formService = formService;
        this.applicationService = applicationService;
        this.userPrefillDataService = userPrefillDataService;
    }

    @GET
    public Viewable listApplicationPeriods() {
        LOGGER.debug("listApplicationPeriods");
        Map<String, ApplicationPeriod> applicationPerioidMap = formService.getApplicationPerioidMap();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("linkList", applicationPerioidMap.keySet());
        return new Viewable(LINK_LIST_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}")
    public Viewable listForms(@PathParam("applicationPeriodId") final String applicationPeriodId) {
        LOGGER.debug("listForms");
        ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(applicationPeriodId);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("path", applicationPeriod.getId() + "/");
        model.put("linkList", applicationPeriod.getFormIds());
        return new Viewable(LINK_LIST_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}/{formId}")
    public Response getApplication(@PathParam("applicationPeriodId") final String applicationPeriodId,
                                   @PathParam("formId") final String formId) throws URISyntaxException {
        LOGGER.debug("getApplication {}, {}", new Object[]{applicationPeriodId, formId});
        Application application = applicationService.getApplication(new FormId(applicationPeriodId, formId));
        if (application.isNew()) {

            Phase firstPhase = formService.getFirstPhase(applicationPeriodId, formId);
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationPeriodId, formId, firstPhase.getId()).getPath())).build();

        } else {
            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationPeriodId, formId,
                            application.getVaiheId()).getPath())).build();
        }
    }

    @GET
    @Path("/{applicationPeriodId}/{formIdStr}/{elementId}")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getElement(@PathParam("applicationPeriodId") final String applicationPeriodId,
                               @PathParam("formIdStr") final String formIdStr,
                               @PathParam("elementId") final String elementId) {

        LOGGER.debug("getElement {}, {}, {}", new Object[]{applicationPeriodId, formIdStr, elementId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        Element element = activeForm.getElementById(elementId);
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
    @Path("/{ applicationPeriodId}/{formId}/{elementId}/relatedData/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Serializable getElementRelatedData(@PathParam("applicationPeriodId") final String applicationPeriodId,
                                              @PathParam("formId") final String formId,
                                              @PathParam("elementId") final String elementId,
                                              @PathParam("key") final String key) {
        LOGGER.debug("getElementRelatedData {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, elementId, key});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        try {
            DataRelatedQuestion<Serializable> element = (DataRelatedQuestion<Serializable>) activeForm.getElementById(elementId);
            return element.getData(key);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return null;
        }
    }

    @POST
    @Path("/{applicationPeriodId}/{formId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response prefillForm(@PathParam("applicationPeriodId") final String applicationPeriodId,
                                @PathParam("formId") final String formId,
                                final MultivaluedMap<String, String> multiValues)
            throws URISyntaxException {
        userPrefillDataService.addUserPrefillData(toSingleValueMap(multiValues));

        return Response.seeOther(new URI(
                new RedirectToFormViewPath(applicationPeriodId, formId).getPath())).build();
    }

    @POST
    @Path("/{applicationPeriodId}/{formId}/{phaseId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response savePhase(@PathParam("applicationPeriodId") final String applicationPeriodId,
                              @PathParam("formId") final String formId,
                              @PathParam("phaseId") final String phaseId,
                              MultivaluedMap<String, String> multiValues) throws URISyntaxException {
        LOGGER.debug("savePhase {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, phaseId, multiValues});
        final FormId hakuLomakeId = new FormId(applicationPeriodId, formId);
        ApplicationState applicationState = applicationService.saveApplicationPhase(new ApplicationPhase(hakuLomakeId,
                phaseId, toSingleValueMap(multiValues)));

        Map<String, Object> model = new HashMap<String, Object>();

        model.put("hakemusId", hakuLomakeId);
        if (applicationState.isValid()) {

            return Response.seeOther(new URI(
                    new RedirectToPhaseViewPath(applicationPeriodId, formId,
                            applicationState.getHakemus().getVaiheId()).getPath())).build();

        } else {
            model.putAll(applicationState.getModelObjects());
            Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
            Phase phase = activeForm.getPhase(phaseId);
            model.put("element", phase);
            model.put("form", activeForm);
            model.put("template", phase.getType());
            return Response.status(Response.Status.OK).entity(new Viewable(ROOT_VIEW, model)).build();
        }

    }

    @POST
    @Path("/{applicationPeriodId}/{formId}/send")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response submitApplication(@PathParam("applicationPeriodId") final String applicationPeriodId,
                                      @PathParam("formId") final String formId) throws URISyntaxException {
        LOGGER.debug("submitApplication {}, {}", new Object[]{applicationPeriodId, formId});
        String oid = applicationService.submitApplication(new FormId(applicationPeriodId, formId));
        RedirectToPendingViewPath redirectToPendingViewPath = new RedirectToPendingViewPath(applicationPeriodId, formId, oid);
        return Response.seeOther(new URI(redirectToPendingViewPath.getPath())).build();
    }

    @GET
    @Path("/{applicationPeriodId}/{formId}/valmis/{oid}")
    public Viewable getComplete(@PathParam("applicationPeriodId") final String applicationPeriodId,
                                @PathParam("formId") final String formId,
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
            throw new ResourceNotFoundExceptionRuntime("Could not find pending application");
        }

        model.put("categoryData", application.getVastaukset());
        model.put("hakemusId", hakuLomakeId);
        model.put("applicationNumber", application.getOid());
        return new Viewable(VALMIS_VIEW, model);
    }

    @GET
    @Path("/{applicationPeriodId}/{formId}/{vaiheId}/{teemaId}/help")
    public Viewable getFormHelp(@PathParam("applicationPeriodId") final String applicationPeriodId,
                                @PathParam("formId") final String formId, @PathParam("vaiheId") final String vaiheId,
                                @PathParam("teemaId") final String teemaId) {

        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Phase phase = activeForm.getPhase(vaiheId);

        Map<String, Object> model = new HashMap<String, Object>();

        for (Element element : phase.getChildren()) {
            if (element.getId().equals(teemaId)) {
                Theme theme = (Theme) element;
                model.put("themeTitle", theme.getTitle());
                HashMap<String, String> helpMap = new HashMap<String, String>();
                for (Element tElement : theme.getChildren()) {
                    if (tElement instanceof Titled) {
                        helpMap.put(((Titled) tElement).getTitle(), ((Titled) tElement).getVerboseHelp());
                    }
                }
                model.put("themeHelpMap", helpMap);
                break;
            }
        }

        return new Viewable(VERBOSE_HELP_VIEW, model);
    }

    /**
     * @param applicationPeriodId
     * @param formIdStr
     * @param gradeGridId
     * @return
     */
    @GET
    @Path("/{applicationPeriodId}/{formIdStr}/{gradeGridId}/additionalLanguageRow")
    public Viewable getAdditionalLanguageRow(@PathParam("applicationPeriodId") final String applicationPeriodId,
                                             @PathParam("formIdStr") final String formIdStr,
                                             @PathParam("gradeGridId") final String gradeGridId) {

        LOGGER.debug("getAdditionalLanguageRow {}, {}, {}", new Object[]{applicationPeriodId, formIdStr, gradeGridId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        Element element = activeForm.getElementById(gradeGridId);
        GradeGrid gradeGrid = (GradeGrid) element;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("element", gradeGrid);
        model.put("template", "gradegrid/additionalLanguageRow");

        return new Viewable(ROOT_VIEW, model);
    }

    // TODO: implement param reader for Map
    private Map<String, String> toSingleValueMap(MultivaluedMap<String, String> multi) {
        HashMap<String, String> singleValueMap = new HashMap<String, String>(multi.size());
        for (Map.Entry<String, List<String>> entry : multi.entrySet()) {
            singleValueMap.put(entry.getKey(), entry.getValue().get(0));
        }
        return singleValueMap;
    }

}
