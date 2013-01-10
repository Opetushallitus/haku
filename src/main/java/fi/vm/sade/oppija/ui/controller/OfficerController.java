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

import fi.vm.sade.oppija.ExceptionController;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping(value = "/virkailija", method = RequestMethod.GET)
@Secured("ROLE_OFFICER")
public class OfficerController extends ExceptionController {

    public static final String REDIRECT_VIRKAILIJA_HAKEMUS = "redirect:/virkailija/hakemus/";
    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerController.class);
    public static final String DEFAULT_VIEW = "virkailija/Phase";
    @Autowired
    ApplicationService applicationService;
    @Autowired
    @Qualifier("formServiceImpl")
    FormService formService;
    @Autowired
    ApplicationProcessStateService applicationProcessStateService;

    @RequestMapping(value = "/hakemus/{oid}", method = RequestMethod.GET)
    public String getApplication(@PathVariable final String oid) throws ResourceNotFoundException {
        LOGGER.debug("officer getApplication by oid {}", new Object[]{oid});
        Application app = applicationService.getApplication(oid);
        FormId formId = app.getFormId();
        Phase phase = formService.getLastPhase(formId.getApplicationPeriodId(), formId.getFormId());
        return REDIRECT_VIRKAILIJA_HAKEMUS + formId.getApplicationPeriodId() + "/" + formId.getFormId() + "/" + phase.getId() + "/" + oid + "/";
    }

    @RequestMapping(value = "/hakemus/{applicationPeriodId}/{formIdStr}/{phaseId}/{oid}", method = RequestMethod.GET)
    public ModelAndView getPhase(@PathVariable final String applicationPeriodId,
                                 @PathVariable final String formIdStr,
                                 @PathVariable final String phaseId,
                                 @PathVariable final String oid) throws ResourceNotFoundException {

        LOGGER.debug("getPhase {}, {}, {}, {}", new Object[]{applicationPeriodId, formIdStr, phaseId, oid});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        Phase phase = activeForm.getPhase(phaseId);
        final ModelAndView modelAndView = new ModelAndView("/virkailija/" + phase.getType());
        final FormId formId = new FormId(applicationPeriodId, activeForm.getId());
        Application app = applicationService.getApplication(oid);
        Map<String, String> values = app.getVastauksetMerged();
        ApplicationProcessState processState = applicationProcessStateService.get(oid);
        modelAndView.addObject("categoryData", values);
        modelAndView.addObject("element", phase);
        modelAndView.addObject("form", activeForm);
        modelAndView.addObject("oid", oid);
        modelAndView.addObject("applicationPhaseId", app.getVaiheId());
        modelAndView.addObject("applicationProcessState", processState);
        return modelAndView.addObject("hakemusId", formId);
    }

    @RequestMapping(value = "/hakemus/{applicationPeriodId}/{formId}/{phaseId}/{oid}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ModelAndView savePhase(@PathVariable final String applicationPeriodId,
                                  @PathVariable final String formId,
                                  @PathVariable final String phaseId,
                                  @PathVariable final String oid,
                                  @RequestBody final MultiValueMap<String, String> multiValues) {
        LOGGER.debug("savePhase {}, {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, phaseId, oid, multiValues});
        final FormId hakuLomakeId = new FormId(applicationPeriodId, formId);
        ApplicationState applicationState = applicationService.saveApplicationPhase(new ApplicationPhase(hakuLomakeId, phaseId, multiValues.toSingleValueMap()), oid);

        ModelAndView modelAndView = new ModelAndView(DEFAULT_VIEW);
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        if (applicationState.isValid()) {
            modelAndView = new ModelAndView(REDIRECT_VIRKAILIJA_HAKEMUS + applicationPeriodId + "/" + formId + "/" + activeForm.getLastPhase().getId() + "/" + oid + "/");
        } else {
            modelAndView.addAllObjects(applicationState.getModelObjects());
            modelAndView.addObject("element", activeForm.getPhase(phaseId));
            modelAndView.addObject("form", activeForm);
            modelAndView.addObject("oid", oid);
        }
        return modelAndView.addObject("hakemusId", hakuLomakeId);
    }

    @RequestMapping(value = "/hakemus/{oid}/applicationProcessState/{status}/", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public String changeApplicationProcessState(@PathVariable final String oid, @PathVariable final String status) {
        LOGGER.debug("changeApplicationProcessState {}, {}", new Object[]{oid, status});

        // TODO: change when setApplicationProcessStateStatus returns correct exception and the updated application
        applicationProcessStateService.setApplicationProcessStateStatus(oid, ApplicationProcessStateStatus.valueOf(status));
        try {
            return getApplication(oid);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundExceptionRuntime("Updated application not found.");
        }
    }
}
