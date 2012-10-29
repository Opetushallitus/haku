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

import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.Hakukohde;
import fi.vm.sade.oppija.haku.domain.Organisaatio;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.questions.Question;
import fi.vm.sade.oppija.haku.service.AdditionalQuestionService;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.service.HakukohdeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Controller for education institute search
 *
 * @author Mikko Majapuro
 */
@Controller
@RequestMapping(value = "/education")
public class EducationController {

    public static final String TERM = "term";

    @Qualifier("hakukohdeServiceDummyImpl")
    @Autowired
    HakukohdeService hakukohdeService;
    @Autowired
    @Qualifier("formServiceImpl")
    FormService formService;
    @Autowired
    HakemusService hakemusService;
    @Autowired
    AdditionalQuestionService additionalQuestionService;

    @RequestMapping(value = "/organisaatio/search", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", params = TERM)
    @ResponseBody
    public List<Organisaatio> searchOrganisaatio(@RequestParam(TERM) String term) {
        return hakukohdeService.searchOrganisaatio(null, term);
    }

    @RequestMapping(value = "/hakukohde/search", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", params = "organisaatioId")
    @ResponseBody
    public List<Hakukohde> searchHakukohde(@RequestParam("organisaatioId") String organisaatioId) {
        return hakukohdeService.searchHakukohde(null, organisaatioId);
    }

    @RequestMapping(value = "/additionalquestion/{applicationPeriodId}/{formId}/{vaiheId}/{teemaId}/{hakukohdeId}", method = RequestMethod.GET)
    public ModelAndView getAdditionalQuestions(@PathVariable final String applicationPeriodId, @PathVariable final String formId, @PathVariable final String teemaId,
                                               @PathVariable final String vaiheId, @PathVariable final String hakukohdeId,
                                               @RequestParam(value = "preview", required = false) Boolean preview) {
        String viewName = preview != null && preview ? "additionalQuestionsPreview" : "additionalQuestions";
        final ModelAndView modelAndView = new ModelAndView(viewName);
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        final HakemusId hakemusId = new HakemusId(applicationPeriodId, activeForm.getId(), vaiheId);
        List<String> hakukohdeIds = new ArrayList<String>();
        hakukohdeIds.add(hakukohdeId);
        Set<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, hakukohdeIds, hakemusId);
        modelAndView.addObject("additionalQuestions", additionalQuestions);
        modelAndView.addObject("categoryData", hakemusService.getHakemus(hakemusId).getValues());
        return modelAndView;
    }
}
