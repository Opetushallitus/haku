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

import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.seeOther;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import com.sun.jersey.api.view.Viewable;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.ValintaperusteetService;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;


@Path("virkailija")
@Controller
@Secured("ROLE_OFFICER")
public class OfficerController {

    public static final String VIRKAILIJA_HAKEMUS_VIEW = "/virkailija/hakemus/";
    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerController.class);
    public static final String DEFAULT_VIEW = "virkailija/Phase";
    public static final String OID_PATH_PARAM = "oid";
    public static final String PHASE_ID_PATH_PARAM = "phaseId";
    public static final String APPLICATION_PERIOD_ID_PATH_PARAM = "applicationPeriodId";
    public static final String ADDITIONAL_INFO_VIEW = "/virkailija/additionalInfo";
    public static final String MEDIA_TYPE_TEXT_HTML_UTF8 = MediaType.TEXT_HTML + ";charset=UTF-8";

    @Autowired
    private KoodistoService koodistoService;
    @Autowired
    ApplicationService applicationService;
    @Autowired
    @Qualifier("formServiceImpl")
    FormService formService;
    @Autowired
    ValintaperusteetService valintaperusteetService;

    @GET
    @Path("/hakemus/{oid}")
    public Response redirectToLastPhase(@PathParam(OID_PATH_PARAM) final String oid)
            throws ResourceNotFoundException, URISyntaxException {
        LOGGER.debug("RedirectToLastPhase by oid {}", new Object[]{oid});
        Application app = applicationService.getApplication(oid);
        FormId formId = app.getFormId();
        Phase phase = formService.getLastPhase(formId.getApplicationPeriodId(), formId.getFormId());
        return seeOther(new URI(VIRKAILIJA_HAKEMUS_VIEW +
                formId.getApplicationPeriodId() + "/" + formId.getFormId() + "/" + phase.getId() + "/" + oid)).build();
    }

    @GET
    @Path("/hakemus/{applicationPeriodId}/{formIdStr}/{phaseId}/{oid}")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getPhase(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                             @PathParam("formIdStr") final String formIdStr,
                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                             @PathParam(OID_PATH_PARAM) final String oid) throws ResourceNotFoundException, IOException {

        LOGGER.debug("getPhase {}, {}, {}, {}", new Object[]{applicationPeriodId, formIdStr, phaseId, oid});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        Phase phase = activeForm.getPhase(phaseId);
        final FormId formId = new FormId(applicationPeriodId, activeForm.getId());
        Application app = applicationService.getApplication(oid);
        Map<String, String> values = app.getVastauksetMerged();
        Map<String, Object> model = new HashMap<String, Object>();
        List<String> applicationPreferenceOids = applicationService.getApplicationPreferenceOids(app);
        AdditionalQuestions additionalQuestions = valintaperusteetService.retrieveAdditionalQuestions(applicationPreferenceOids);
        model.put("application", app);
        model.put("additionalQuestions", additionalQuestions);
        model.put("categoryData", values);
        model.put("element", phase);
        model.put("form", activeForm);
        model.put("oid", oid);
        model.put("applicationPhaseId", app.getPhaseId());
        model.put("hakemusId", formId);
        return new Viewable("/virkailija/" + phase.getType(), model);
    }

    @POST
    @Path("/hakemus/{applicationPeriodId}/{formId}/{phaseId}/{oid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Response savePhase(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                              @PathParam("formId") final String formId,
                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                              @PathParam(OID_PATH_PARAM) final String oid,
                              final MultivaluedMap<String, String> multiValues) throws URISyntaxException {

        LOGGER.debug("savePhase {}, {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, phaseId, oid, multiValues});
        final FormId hakuLomakeId = new FormId(applicationPeriodId, formId);
        ApplicationPhase phase = new ApplicationPhase(hakuLomakeId, phaseId, MultivaluedMapUtil.toSingleValueMap(multiValues));
        ApplicationState applicationState = applicationService.saveApplicationPhase(phase, oid, false);

        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Map<String, Object> model = new HashMap<String, Object>();
        String templateName = DEFAULT_VIEW;
        if (applicationState.isValid()) {
            templateName = VIRKAILIJA_HAKEMUS_VIEW + applicationPeriodId + "/" +
                    formId + "/" + activeForm.getLastPhase().getId() + "/" + oid + "/";
            return seeOther(new URI(templateName)).build();

        } else {
            model.putAll(applicationState.getModelObjects());
            model.put("element", activeForm.getPhase(phaseId));
            model.put("form", activeForm);
            model.put("oid", oid);
        }
        model.put("hakemusId", hakuLomakeId);
        return ok(new Viewable(templateName, model)).build();
    }

    @POST
    @Path("/hakemus/{oid}/additionalInfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Response saveAdditionalInfo(@PathParam(OID_PATH_PARAM) final String oid, final MultivaluedMap<String, String> multiValues)
            throws URISyntaxException, ResourceNotFoundException {
        LOGGER.debug("saveAdditionalInfo {}, {}", new Object[]{oid, multiValues});
        applicationService.saveApplicationAdditionalInfo(oid, MultivaluedMapUtil.toSingleValueMap(multiValues));
        String templateName = VIRKAILIJA_HAKEMUS_VIEW + oid + "/";
        return seeOther(new URI(templateName)).build();
    }

    @GET
    @Path("/hakemus/{oid}/additionalInfo")
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Viewable getAdditionalInfo(@PathParam(OID_PATH_PARAM) final String oid) throws ResourceNotFoundException, IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        Application app = applicationService.getApplication(oid);
        List<String> applicationPreferenceOids = applicationService.getApplicationPreferenceOids(app);
        AdditionalQuestions additionalQuestions = valintaperusteetService.retrieveAdditionalQuestions(applicationPreferenceOids);
        model.put("application", app);
        model.put("additionalQuestions", additionalQuestions);
        String templateName = ADDITIONAL_INFO_VIEW;
        return new Viewable(templateName, model);
    }

    @POST
    @Path("/hakemus/{oid}/applicationProcessState/{status}/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MEDIA_TYPE_TEXT_HTML_UTF8)
    public Response changeApplicationProcessState(@PathParam(OID_PATH_PARAM) final String oid,
                                                  @PathParam("status") final String status) throws URISyntaxException {
        LOGGER.debug("changeApplicationProcessState {}, {}", new Object[]{oid, status});

        try {
            applicationService.setApplicationState(oid, status);
            return redirectToLastPhase(oid);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundExceptionRuntime("Updated application not found.", e);
        }
    }

    @GET
    @Path("/hakemus/")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable search() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organizationTypes", koodistoService.getOrganizationtypes());
        model.put("learningInstitutionTypes", koodistoService.getLearningInstitutionTypes());
        return new Viewable("/virkailija/searchIndex", model);
    }
}
