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

package fi.vm.sade.oppija.tarjonta.controller;

import fi.vm.sade.oppija.ExceptionController;
import fi.vm.sade.oppija.tarjonta.domain.SearchFilters;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;


@Controller
public class SearchController extends ExceptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    public static final String VIEW_NAME_ITEMS = "tarjonta/tarjontatiedot";
    public static final String VIEW_NAME_KOULUTUSKUVAUS = "tarjonta/koulutuskuvaus";
    public static final String MODEL_NAME = "searchResult";
    public static final String PARAMETERS_SINGLE_VALUE = "parameters";
    public static final String PARAMETERS_MULTI_VALUE = "parameters_multi";
    public static final String SEARCH_FILTERS = "filters";

    private final SearchService service;
    private final SearchFilters searchFilters;

    @Autowired
    public SearchController(final SearchService searchService, final SearchFilters searchFilters) {
        this.service = searchService;
        this.searchFilters = searchFilters;
    }


    @RequestMapping(value = "/tarjontatiedot", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView listTarjontatiedot(@RequestParam MultiValueMap<String, String> parameters) {
        LOGGER.debug("parameters: " + parameters);
        SearchResult searchResult = service.search(parameters);
        ModelAndView modelAndView = new ModelAndView(VIEW_NAME_ITEMS);
        modelAndView.addObject(MODEL_NAME, searchResult);
        modelAndView.addObject(PARAMETERS_MULTI_VALUE, parameters);
        modelAndView.addObject(PARAMETERS_SINGLE_VALUE, parameters.toSingleValueMap());
        modelAndView.addObject(SEARCH_FILTERS, searchFilters.getFilters());
        return modelAndView;
    }


    @RequestMapping(value = "/tarjontatiedot/{tarjontatietoId}", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView getTarjontatiedot(@PathVariable final String tarjontatietoId) {
        LOGGER.info("tarjontatiedot/" + tarjontatietoId);
        Map<String, Object> searchResult = service.searchById(tarjontatietoId);
        ModelAndView modelAndView = new ModelAndView(VIEW_NAME_KOULUTUSKUVAUS);
        modelAndView.addObject(MODEL_NAME, searchResult);
        return modelAndView;
    }

}
