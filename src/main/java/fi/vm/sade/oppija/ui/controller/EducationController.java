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

import fi.vm.sade.oppija.lomake.domain.ApplicationOption;
import fi.vm.sade.oppija.lomake.domain.HakuLomakeId;
import fi.vm.sade.oppija.lomake.domain.Organization;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.service.AdditionalQuestionService;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.service.HakukohdeService;
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
@RequestMapping(value = EducationController.EDUCATION_CONTROLLER_PATH)
public class EducationController {

    public static final String TERM = "term";
    public static final String EDUCATION_CONTROLLER_PATH = "/education";

    @Qualifier("HakukohdeServiceSolrImpl")
    @Autowired
    HakukohdeService hakukohdeService;
    @Autowired
    @Qualifier("formServiceImpl")
    FormService formService;
    @Autowired
    ApplicationService applicationService;
    @Autowired
    AdditionalQuestionService additionalQuestionService;

    @RequestMapping(value = "/{hakuId}/organisaatio/search", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", params = TERM)
    @ResponseBody
    public List<Organization> searchOrganisaatio(@PathVariable final String hakuId, @RequestParam(TERM) String term) {
        return hakukohdeService.searchOrganisaatio(hakuId, term);
    }

    @RequestMapping(value = "/{hakuId}/hakukohde/search", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", params = "organisaatioId")
    @ResponseBody
    public List<ApplicationOption> searchHakukohde(@PathVariable final String hakuId, @RequestParam("organisaatioId") String organisaatioId) {
        return hakukohdeService.searchHakukohde(hakuId, organisaatioId);
    }

    @RequestMapping(value = "/additionalquestion/{hakuId}/{lomakeId}/{vaiheId}/{teemaId}/{hakukohdeId}", method = RequestMethod.GET)
    public ModelAndView getAdditionalQuestions(@PathVariable final String hakuId, @PathVariable final String lomakeId, @PathVariable final String teemaId,
                                               @PathVariable final String vaiheId, @PathVariable final String hakukohdeId,
                                               @RequestParam(value = "preview", required = false) Boolean preview) {
        String viewName = preview != null && preview ? "additionalQuestionsPreview" : "additionalQuestions";
        final ModelAndView modelAndView = new ModelAndView(viewName);
        Form activeForm = formService.getActiveForm(hakuId, lomakeId);
        final HakuLomakeId hakuLomakeId = new HakuLomakeId(hakuId, activeForm.getId());
        List<String> hakukohdeIds = new ArrayList<String>();
        hakukohdeIds.add(hakukohdeId);
        Set<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, hakukohdeIds, hakuLomakeId, vaiheId);
        modelAndView.addObject("additionalQuestions", additionalQuestions);
        modelAndView.addObject("categoryData", applicationService.getHakemus(hakuLomakeId).getVastauksetMerged());
        return modelAndView;
    }
}
