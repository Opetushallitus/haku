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

package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Teema;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.elements.questions.Question;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.service.AdditionalQuestionService;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.codehaus.plexus.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/lomake", method = RequestMethod.GET)
public class FormController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    public static final String DEFAULT_VIEW = "default";
    public static final String VERBOSE_HELP_VIEW = "help";
    public static final String LINK_LIST_VIEW = "linkList";
    public static final String ERROR_NOTFOUND = "error/notfound";
    public static final String ERROR_SERVERERROR = "error/servererror";
    public static final String USER_ID = "j_username";

    final FormService formService;
    private final HakemusService hakemusService;
    private final AdditionalQuestionService additionalQuestionService;

    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService,
                          @Qualifier("additionalQuestionService") final AdditionalQuestionService additionalQuestionService,
                          HakemusService hakemusService) {
        this.formService = formService;
        this.additionalQuestionService = additionalQuestionService;
        this.hakemusService = hakemusService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listApplicationPeriods() {
        LOGGER.debug("listApplicationPeriods");
        Map<String, ApplicationPeriod> applicationPerioidMap = formService.getApplicationPerioidMap();
        final ModelAndView modelAndView = new ModelAndView(LINK_LIST_VIEW);
        modelAndView.addObject(LINK_LIST_VIEW, applicationPerioidMap.keySet());
        return modelAndView;
    }


    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET)
    public ModelAndView listForms(@PathVariable final String applicationPeriodId) {
        LOGGER.debug("listForms");
        ApplicationPeriod applicationPeriod = formService.getApplicationPeriodById(applicationPeriodId);
        final ModelAndView modelAndView = new ModelAndView(LINK_LIST_VIEW);
        modelAndView.addObject("path", applicationPeriod.getId() + "/");
        modelAndView.addObject(LINK_LIST_VIEW, applicationPeriod.getFormIds());
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET)
    public String getFormAndRedirectToFirstCategory(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        LOGGER.debug("getFormAndRedirectToFirstCategory {}, {}", new Object[]{applicationPeriodId, formId});
        Vaihe firstVaihe = formService.getFirstCategory(applicationPeriodId, formId);
        return "redirect:" + formId + "/" + firstVaihe.getId();
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.GET)
    public ModelAndView getCategory(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId,
                                    @PathVariable final String categoryId) {

        LOGGER.debug("getCategory {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        final ModelAndView modelAndView = new ModelAndView(DEFAULT_VIEW);
        modelAndView.addObject("category", activeForm.getCategory(categoryId));
        modelAndView.addObject("form", activeForm);
        final HakemusId hakemusId = new HakemusId(applicationPeriodId, activeForm.getId(), categoryId);
        modelAndView.addObject("categoryData", hakemusService.getHakemus(hakemusId).getValues());
        modelAndView.addObject("hakemusId", hakemusId);
        modelAndView.addObject("additionalQuestions", additionalQuestionService.findAdditionalQuestionsInCategory(hakemusId));
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ModelAndView prefillForm(@PathVariable final String applicationPeriodId, @PathVariable final String formId, @RequestBody final MultiValueMap<String, String> multiValues) {
        final HakemusId hakemusId = new HakemusId(applicationPeriodId, formId, "henkilotiedot");
        HakemusState hakemusState = hakemusService.save(hakemusId, multiValues.toSingleValueMap());
        return new ModelAndView("redirect:/lomake/" + applicationPeriodId + "/" + formId);
        //return saveCategory(applicationPeriodId, formId, formService.getFirstCategory(applicationPeriodId, formId).getId(), multiValues);
    }


    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ModelAndView saveCategory(@PathVariable final String applicationPeriodId,
                                     @PathVariable final String formId,
                                     @PathVariable final String categoryId,
                                     @RequestBody final MultiValueMap<String, String> multiValues) {
        LOGGER.debug("getCategory {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId, multiValues});
        Map<String, String> values = multiValues.toSingleValueMap();

        final HakemusId hakemusId = new HakemusId(applicationPeriodId, formId, categoryId);

        HakemusState hakemusState = hakemusService.save(hakemusId, values);

        ModelAndView modelAndView = new ModelAndView(DEFAULT_VIEW);
        if (hakemusState.isValid()) {
            final Vaihe vaihe = (Vaihe) hakemusState.getModelObjects().get("vaihe");
            modelAndView = new ModelAndView("redirect:/lomake/" + applicationPeriodId + "/" + formId + "/" + vaihe.getId());
        } else {
            for (Map.Entry<String, Object> stringObjectEntry : hakemusState.getModelObjects().entrySet()) {
                modelAndView.addObject(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
                modelAndView.addObject("category", activeForm.getCategory(categoryId));
                modelAndView.addObject("form", activeForm);
            }
        }
        modelAndView.addObject("hakemusId", hakemusId);
        LOGGER.debug(modelAndView.getModel().toString());
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/send", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ModelAndView sendForm(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        LOGGER.debug("sendForm {}, {}", new Object[]{applicationPeriodId, formId});
        //TODO: send application
        ModelAndView modelAndView = new ModelAndView("redirect:/lomake/" + applicationPeriodId + "/" + formId + "/valmis");
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/valmis", method = RequestMethod.GET)
    public ModelAndView getComplete(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId) {

        LOGGER.debug("sendForm {}, {}", new Object[]{applicationPeriodId, formId});
        ModelAndView modelAndView = new ModelAndView("valmis");
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        modelAndView.addObject("form", activeForm);
        final HakemusId hakemusId = new HakemusId(applicationPeriodId, activeForm.getId(), null);
        modelAndView.addObject("categoryData", hakemusService.getHakemus(hakemusId).getValues());
        modelAndView.addObject("hakemusId", hakemusId);
        //TODO: implement application number
        modelAndView.addObject("applicationNumber", new Date().getTime());
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{vaiheId}/{teemaId}/help", method = RequestMethod.GET)
    public ModelAndView getFormHelp(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId, @PathVariable final String vaiheId,
                                    @PathVariable final String teemaId) {

        ModelAndView modelAndView = new ModelAndView(VERBOSE_HELP_VIEW);
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Vaihe phase = activeForm.getCategory(vaiheId);

        for (Element element : phase.getChildren()) {
            if (element.getId().equals(teemaId)) {
                Teema theme = (Teema) element;
                modelAndView.getModel().put("themeTitle", theme.getTitle());
                HashMap<String, String> helpMap = new HashMap<String, String>();
                for (Element qElement : theme.getChildren()) {
                    if (qElement instanceof Question) {
                        helpMap.put(((Question) qElement).getTitle(), ((Question) qElement).getVerboseHelp());
                    }
                }
                modelAndView.getModel().put("themeHelpMap", helpMap);
                break;
            }
        }

        return modelAndView;
    }


    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView resourceNotFoundExceptions(ResourceNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView(ERROR_NOTFOUND);
        modelAndView.addObject("stackTrace", ExceptionUtils.getFullStackTrace(e));
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView exceptions(Throwable t) {
        ModelAndView modelAndView = new ModelAndView(ERROR_SERVERERROR);
        modelAndView.addObject("stackTrace", ExceptionUtils.getFullStackTrace(t));
        modelAndView.addObject("message", t.getMessage());
        return modelAndView;
    }


}
