package fi.vm.sade.oppija.ui.controller;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

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
@Controller
@RequestMapping(value = "/virkailija", method = RequestMethod.GET)
@Secured("ROLE_OFFICER")
public class OfficerController {

    public static final String REDIRECT_VIRKAILIJA_HAKEMUS = "redirect:/virkailija/hakemus/";
    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerController.class);
    @Autowired
    ApplicationService applicationService;
    @Autowired
    @Qualifier("formServiceImpl")
    FormService formService;

    @RequestMapping(value = "/hakemus/{oid}", method = RequestMethod.GET)
    public String getApplication(@PathVariable final String oid) {
        LOGGER.debug("officer getApplication by oid {}", new Object[]{ oid });
        Application app = applicationService.getHakemus(oid);
        FormId formId = app.getFormId();
        return REDIRECT_VIRKAILIJA_HAKEMUS + formId.getApplicationPeriodId() + "/" + formId.getFormId() + "/" + app.getVaiheId() + "/" + oid + "/";
    }

    @RequestMapping(value = "/hakemus/{applicationPeriodId}/{formIdStr}/{elementId}/{oid}", method = RequestMethod.GET)
    public ModelAndView getPhase(@PathVariable final String applicationPeriodId,
                                   @PathVariable final String formIdStr,
                                   @PathVariable final String phaseId,
                                   @PathVariable final String oid) {

        LOGGER.debug("getPhase {}, {}, {}, {}", new Object[]{applicationPeriodId, formIdStr, phaseId, oid});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        Element element = activeForm.getPhase(phaseId);
        final ModelAndView modelAndView = new ModelAndView("/virkailija/" + element.getType());
        final FormId formId = new FormId(applicationPeriodId, activeForm.getId());
        Map<String, String> values = applicationService.getHakemus(oid).getVastauksetMerged();
        modelAndView.addObject("categoryData", values);
        modelAndView.addObject("element", element);
        modelAndView.addObject("form", activeForm);
        return modelAndView.addObject("hakemusId", formId);
    }
}
