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
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.*;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DataRelatedQuestion;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.service.UserPrefillDataService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.ui.common.RedirectToFormViewPath;
import fi.vm.sade.oppija.ui.common.RedirectToPendingViewPath;
import fi.vm.sade.oppija.ui.common.RedirectToPhaseViewPath;
import fi.vm.sade.oppija.ui.common.ViewPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(value = "/lomake", method = RequestMethod.GET)
public class FormController extends ExceptionController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    public static final String DEFAULT_VIEW = "elements/Phase";
    public static final String VERBOSE_HELP_VIEW = "help";
    public static final String LINK_LIST_VIEW = "linkList";
    public static final String VALMIS_VIEW = "valmis";

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

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listApplicationPeriods() {
        LOGGER.debug("listApplicationPeriods");
        Map<String, ApplicationPeriod> applicationPerioidMap = formService.getApplicationPerioidMap();
        final ModelAndView modelAndView = new ModelAndView(LINK_LIST_VIEW);
        return modelAndView.addObject(LINK_LIST_VIEW, applicationPerioidMap.keySet());
    }


    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET)
    public ModelAndView listForms(@PathVariable final String applicationPeriodId) {
        LOGGER.debug("listForms");
        ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(applicationPeriodId);
        final ModelAndView modelAndView = new ModelAndView(LINK_LIST_VIEW);
        modelAndView.addObject("path", applicationPeriod.getId() + "/");
        return modelAndView.addObject(LINK_LIST_VIEW, applicationPeriod.getFormIds());
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET)
    public String getApplication(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        LOGGER.debug("getApplication {}, {}", new Object[]{applicationPeriodId, formId});
        Application application = applicationService.getApplication(new FormId(applicationPeriodId, formId));
        if (application.isNew()) {
            Phase firstPhase = formService.getFirstPhase(applicationPeriodId, formId);
            return "redirect:" + formId + "/" + firstPhase.getId();
        } else {
            return new RedirectToPhaseViewPath(applicationPeriodId, formId, application.getVaiheId()).getPath();
        }
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formIdStr}/{elementId}", method = RequestMethod.GET)
    public ModelAndView getElement(@PathVariable final String applicationPeriodId,
                                   @PathVariable final String formIdStr,
                                   @PathVariable final String elementId) {

        LOGGER.debug("getElement {}, {}, {}", new Object[]{applicationPeriodId, formIdStr, elementId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formIdStr);
        Element element = activeForm.getElementById(elementId);
        final ModelAndView modelAndView = new ModelAndView("/elements/" + element.getType());
        final FormId formId = new FormId(applicationPeriodId, activeForm.getId());
        Map<String, String> values = applicationService.getApplication(formId).getVastauksetMerged();
        values = userPrefillDataService.populateWithPrefillData(values);
        modelAndView.addObject("categoryData", values);
        modelAndView.addObject("element", element);
        modelAndView.addObject("form", activeForm);
        return modelAndView.addObject("hakemusId", formId);
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{elementId}/relatedData/{key}",
            method = RequestMethod.GET,
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Serializable getElementRelatedData(@PathVariable final String applicationPeriodId,
                                              @PathVariable final String formId,
                                              @PathVariable final String elementId,
                                              @PathVariable final String key) {
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

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ModelAndView prefillForm(@PathVariable final String applicationPeriodId, @PathVariable final String formId,
                                    @RequestBody final MultiValueMap<String, String> multiValues) {
        userPrefillDataService.addUserPrefillData(multiValues.toSingleValueMap());
        return new ModelAndView(new RedirectToFormViewPath(applicationPeriodId, formId).getPath());
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{phaseId}",
            method = RequestMethod.POST,
            consumes = "application/x-www-form-urlencoded")
    public ModelAndView savePhase(@PathVariable final String applicationPeriodId,
                                  @PathVariable final String formId,
                                  @PathVariable final String phaseId,
                                  @RequestBody final MultiValueMap<String, String> multiValues) {
        LOGGER.debug("savePhase {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, phaseId, multiValues});
        final FormId hakuLomakeId = new FormId(applicationPeriodId, formId);
        ApplicationState applicationState = applicationService.saveApplicationPhase(new ApplicationPhase(hakuLomakeId,
                phaseId, multiValues.toSingleValueMap()));

        ModelAndView modelAndView = new ModelAndView(DEFAULT_VIEW);
        if (applicationState.isValid()) {
            RedirectToPhaseViewPath redirectToPhaseViewPath = new RedirectToPhaseViewPath(applicationPeriodId, formId, applicationState.getHakemus().getVaiheId());
            modelAndView = createModelAndView(redirectToPhaseViewPath);
        } else {
            modelAndView.addAllObjects(applicationState.getModelObjects());
            Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
            modelAndView.addObject("element", activeForm.getPhase(phaseId));
            modelAndView.addObject("form", activeForm);
        }
        return modelAndView.addObject("hakemusId", hakuLomakeId);
    }

    private ModelAndView createModelAndView(ViewPath viewPath) {
        return new ModelAndView(viewPath.getPath());
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/send", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ModelAndView submitApplication(@PathVariable final String applicationPeriodId,
                                          @PathVariable final String formId) {
        LOGGER.debug("submitApplication {}, {}", new Object[]{applicationPeriodId, formId});
        String oid = applicationService.submitApplication(new FormId(applicationPeriodId, formId));
        RedirectToPendingViewPath redirectToPendingViewPath = new RedirectToPendingViewPath(applicationPeriodId, formId, oid);
        return createModelAndView(redirectToPendingViewPath);
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/valmis/{oid}/", method = RequestMethod.GET)
    public ModelAndView getComplete(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId,
                                    @PathVariable final String oid) {

        LOGGER.debug("getComplete {}, {}", new Object[]{applicationPeriodId, formId});
        ModelAndView modelAndView = new ModelAndView(VALMIS_VIEW);
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        modelAndView.addObject("form", activeForm);
        final FormId hakuLomakeId = new FormId(applicationPeriodId, activeForm.getId());
        final Application application = applicationService.getPendingApplication(hakuLomakeId, oid);
        modelAndView.addObject("categoryData", application.getVastaukset());
        modelAndView.addObject("hakemusId", hakuLomakeId);
        return modelAndView.addObject("applicationNumber", application.getOid());
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{vaiheId}/{teemaId}/help", method = RequestMethod.GET)
    public ModelAndView getFormHelp(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId, @PathVariable final String vaiheId,
                                    @PathVariable final String teemaId) {

        ModelAndView modelAndView = new ModelAndView(VERBOSE_HELP_VIEW);
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Phase phase = activeForm.getPhase(vaiheId);

        for (Element element : phase.getChildren()) {
            if (element.getId().equals(teemaId)) {
                Theme theme = (Theme) element;
                modelAndView.getModel().put("themeTitle", theme.getTitle());
                HashMap<String, String> helpMap = new HashMap<String, String>();
                for (Element tElement : theme.getChildren()) {
                    if (tElement instanceof Titled) {
                        helpMap.put(((Titled) tElement).getTitle(), ((Titled) tElement).getVerboseHelp());
                    }
                }
                modelAndView.getModel().put("themeHelpMap", helpMap);
                break;
            }
        }

        return modelAndView;
    }


}
