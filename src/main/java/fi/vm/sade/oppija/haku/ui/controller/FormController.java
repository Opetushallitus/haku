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

package fi.vm.sade.oppija.haku.ui.controller;

import fi.vm.sade.oppija.ExceptionController;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakuLomakeId;
import fi.vm.sade.oppija.haku.domain.VaiheenVastaukset;
import fi.vm.sade.oppija.haku.domain.elements.*;
import fi.vm.sade.oppija.haku.domain.elements.questions.DataRelatedQuestion;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.service.UserPrefillDataService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
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
    public static final String DEFAULT_VIEW = "elements/Vaihe";
    public static final String VERBOSE_HELP_VIEW = "help";
    public static final String LINK_LIST_VIEW = "linkList";
    public static final String REDIRECT_LOMAKE = "redirect:/lomake/";
    public static final String VALMIS_VIEW = "valmis";

    final FormService formService;
    private final HakemusService hakemusService;
    private final UserPrefillDataService userPrefillDataService;

    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService,
                          HakemusService hakemusService, final UserPrefillDataService userPrefillDataService) {
        this.formService = formService;
        this.hakemusService = hakemusService;
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
    public String getHakemus(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        LOGGER.debug("getHakemus {}, {}", new Object[]{applicationPeriodId, formId});
        Hakemus hakemus = hakemusService.getHakemus(new HakuLomakeId(applicationPeriodId, formId));
        if (hakemus.isNew()) {
            Vaihe firstVaihe = formService.getFirstCategory(applicationPeriodId, formId);
            return "redirect:" + formId + "/" + firstVaihe.getId();
        } else {
            return REDIRECT_LOMAKE + applicationPeriodId + "/" + formId + "/" + hakemus.getVaiheId();
        }
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{elementId}", method = RequestMethod.GET)
    public ModelAndView getElement(@PathVariable final String applicationPeriodId,
                                   @PathVariable final String formId,
                                   @PathVariable final String elementId) {

        LOGGER.debug("getElement {}, {}, {}", new Object[]{applicationPeriodId, formId, elementId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Element element = activeForm.getElementById(elementId);
        final ModelAndView modelAndView = new ModelAndView("/elements/" + element.getType());
        modelAndView.addObject("element", element);
        final HakuLomakeId hakuLomakeId = new HakuLomakeId(applicationPeriodId, activeForm.getId());
        Map<String, String> values = hakemusService.getHakemus(hakuLomakeId).getVastauksetMerged();
        values = userPrefillDataService.populateWithPrefillData(values);
        modelAndView.addObject("categoryData", values);
        return modelAndView.addObject("hakemusId", hakuLomakeId);
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{elementId}/relatedData/{key}", method = RequestMethod.GET,
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
        return new ModelAndView(REDIRECT_LOMAKE + applicationPeriodId + "/" + formId);
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ModelAndView saveCategory(@PathVariable final String applicationPeriodId,
                                     @PathVariable final String formId,
                                     @PathVariable final String categoryId,
                                     @RequestBody final MultiValueMap<String, String> multiValues) {
        LOGGER.debug("getCategory {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId, multiValues});
        final HakuLomakeId hakuLomakeId = new HakuLomakeId(applicationPeriodId, formId);
        HakemusState hakemusState = hakemusService.tallennaVaihe(new VaiheenVastaukset(hakuLomakeId, categoryId, multiValues.toSingleValueMap()));

        ModelAndView modelAndView = new ModelAndView(DEFAULT_VIEW);
        if (hakemusState.isValid()) {
            modelAndView = new ModelAndView(REDIRECT_LOMAKE + applicationPeriodId + "/" + formId + "/" + hakemusState.getHakemus().getVaiheId());
        } else {
            for (Map.Entry<String, Object> stringObjectEntry : hakemusState.getModelObjects().entrySet()) {
                modelAndView.addObject(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            }
            Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
            modelAndView.addObject("element", activeForm.getCategory(categoryId));
            modelAndView.addObject("form", activeForm);
        }
        return modelAndView.addObject("hakemusId", hakuLomakeId);
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/send", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ModelAndView sendForm(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        LOGGER.debug("sendForm {}, {}", new Object[]{applicationPeriodId, formId});
        hakemusService.laitaVireille(new HakuLomakeId(applicationPeriodId, formId));
        return new ModelAndView(REDIRECT_LOMAKE + applicationPeriodId + "/" + formId + "/valmis");
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/valmis", method = RequestMethod.GET)
    public ModelAndView getComplete(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId) {

        LOGGER.debug("sendForm {}, {}", new Object[]{applicationPeriodId, formId});
        ModelAndView modelAndView = new ModelAndView(VALMIS_VIEW);
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        modelAndView.addObject("form", activeForm);
        final HakuLomakeId hakuLomakeId = new HakuLomakeId(applicationPeriodId, activeForm.getId());
        final Hakemus hakemus = hakemusService.getHakemus(hakuLomakeId);
        modelAndView.addObject("categoryData", hakemus.getVastaukset());
        modelAndView.addObject("hakemusId", hakuLomakeId);
        return modelAndView.addObject("applicationNumber", hakemus.getOid());
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
