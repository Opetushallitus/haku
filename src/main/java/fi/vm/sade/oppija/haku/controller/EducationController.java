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

import fi.vm.sade.oppija.haku.domain.Hakukohde;
import fi.vm.sade.oppija.haku.domain.Organisaatio;
import fi.vm.sade.oppija.haku.domain.questions.Question;
import fi.vm.sade.oppija.haku.service.HakukohdeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controller for education institute search
 *
 * @author Mikko Majapuro
 */
@Controller
@RequestMapping(value = "/education")
public class EducationController {

    public static final String TERM = "term";

    @Autowired
    HakukohdeService hakukohdeService;

    @RequestMapping(value = "/organisaatio/search", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", params = TERM)
    @ResponseBody
    public List<Organisaatio> searchOrganisaatio(@RequestParam(TERM) String term) {
        return hakukohdeService.searchOrganisaatio(term);
    }

    @RequestMapping(value = "/hakukohde/search", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", params = "organisaatioId")
    @ResponseBody
    public List<Hakukohde> searchHakukohde(@RequestParam("organisaatioId") String organisaatioId) {
        return hakukohdeService.searchHakukohde(organisaatioId);
    }

    @RequestMapping(value = "/additionalquestion/{teemaId}/{hakukohdeId}", method = RequestMethod.GET)
    public ModelAndView getCategory(@PathVariable final String teemaId, @PathVariable final String hakukohdeId) {
        final ModelAndView modelAndView = new ModelAndView("additionalQuestions");
        List<Question> additionalQuestions = hakukohdeService.getHakukohdeSpecificQuestions(hakukohdeId, teemaId);
        modelAndView.addObject("additionalQuestions", additionalQuestions);
        return modelAndView;
    }
}
