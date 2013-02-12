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
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.application.process.service.ApplicationProcessStateService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.seeOther;


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

    @Autowired
    ApplicationService applicationService;
    @Autowired
    @Qualifier("formServiceImpl")
    FormService formService;
    @Autowired
    ApplicationProcessStateService applicationProcessStateService;

    @GET
    @Path("/hakemus/{oid}")
    public Response getApplication(@PathParam(OID_PATH_PARAM) final String oid)
            throws ResourceNotFoundException, URISyntaxException {
        LOGGER.debug("officer getApplication by oid {}", new Object[]{oid});
        Application app = applicationService.getApplication(oid);
        FormId formId = app.getFormId();
        Phase phase = formService.getLastPhase(formId.getApplicationPeriodId(), formId.getFormId());
        return seeOther(new URI(VIRKAILIJA_HAKEMUS_VIEW +
                formId.getApplicationPeriodId() + "/" + formId.getFormId() + "/" + phase.getId() + "/" + oid)).build();
    }

    @GET
    @Path("/hakemus/{applicationPeriodId}/{formIdStr}/{phaseId}/{oid}")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable getPhase(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                             @PathParam("formIdStr") final String formIdStr,
                             @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                             @PathParam(OID_PATH_PARAM) final String oid) throws ResourceNotFoundException {

        LOGGER.debug("getPhase {}, {}, {}, {}", new Object[]{applicationPeriodId, formIdStr, phaseId, oid});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        Phase phase = activeForm.getPhase(phaseId);
        final FormId formId = new FormId(applicationPeriodId, activeForm.getId());
        Application app = applicationService.getApplication(oid);
        Map<String, String> values = app.getVastauksetMerged();
        ApplicationProcessState processState = applicationProcessStateService.get(oid);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("categoryData", values);
        model.put("element", phase);
        model.put("form", activeForm);
        model.put("oid", oid);
        model.put("applicationPhaseId", app.getPhaseId());
        model.put("applicationProcessState", processState);
        model.put("hakemusId", formId);
        return new Viewable("/virkailija/" + phase.getType(), model);
    }

    @POST
    @Path("/hakemus/{applicationPeriodId}/{formId}/{phaseId}/{oid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Response savePhase(@PathParam(APPLICATION_PERIOD_ID_PATH_PARAM) final String applicationPeriodId,
                              @PathParam("formId") final String formId,
                              @PathParam(PHASE_ID_PATH_PARAM) final String phaseId,
                              @PathParam(OID_PATH_PARAM) final String oid,
                              final MultivaluedMap<String, String> multiValues) throws URISyntaxException {
        LOGGER.debug("savePhase {}, {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, phaseId, oid, multiValues});
        final FormId hakuLomakeId = new FormId(applicationPeriodId, formId);
        ApplicationState applicationState = applicationService.saveApplicationPhase(
                new ApplicationPhase(hakuLomakeId, phaseId, MultivaluedMapUtil.toSingleValueMap(multiValues)), oid);

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
    @Path("/hakemus/{oid}/applicationProcessState/{status}/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Response changeApplicationProcessState(@PathParam(OID_PATH_PARAM) final String oid,
                                                  @PathParam("status") final String status) throws URISyntaxException {
        LOGGER.debug("changeApplicationProcessState {}, {}", new Object[]{oid, status});

        // TODO: change when setApplicationProcessStateStatus returns correct exception and the updated application
        applicationProcessStateService.setApplicationProcessStateStatus(oid, ApplicationProcessStateStatus.valueOf(status));
        try {
            return getApplication(oid);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundExceptionRuntime("Updated application not found.");
        }
    }

    @GET
    @Path("/hakemus/")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable search(@QueryParam("term") final String term) {
        Map<String, Object> model = new HashMap<String, Object>();
        List<Application> applications = Lists.newArrayList();//applicationService.findApplications(term);
        model.put("applications", applications);
        return new Viewable("/virkailija/searchIndex", model);
    }
}
