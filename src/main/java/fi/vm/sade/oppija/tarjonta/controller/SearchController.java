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

import fi.vm.sade.oppija.tarjonta.converter.ArrayParametersToMap;
import fi.vm.sade.oppija.tarjonta.domain.*;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@Controller
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    public static final String VIEW_NAME_ITEMS = "tarjonta/tarjontatiedot";
    public static final String VIEW_NAME_KOULUTUSKUVAUS = "tarjonta/koulutuskuvaus";
    public static final String MODEL_NAME = "searchResult";
    public static final String MODEL_NAME_SEARCH_PARAMETERS = "searchParameters";
    public static final String PARAMETERS = "parameters";
    public static final String SEARCH_FILTERS = "filters";
    public static final String SORT_PARAMETERS = "sort_parameters";
    public static final String PAGING_PARAMETERS = "paging_parameters";
    public static final String FILTERS = "filters";
    public static final String TUNNISTE = "AOId";
    public static final String KOULUTUSTYYPPI = "koulutustyyppi";
    public static final String KOULUTUSKIELI = "koulutuksenkieli";
    public static final String OPETUSMUOTO = "opetusmuoto";
    public static final String OPPILAITOSTYYPPI = "oppilaitostyyppi";
    public static final String SEARCH_PARAMETER = "text";
    private final SearchService service;
    private final SearchFilters searchFilters;
    private ArrayParametersToMap arrayParametersToMap;

    @Autowired
    public SearchController(final SearchService searchService, final SearchFilters searchFilters) {
        this.service = searchService;
        this.searchFilters = searchFilters;
        arrayParametersToMap = new ArrayParametersToMap();
    }

    @ModelAttribute(SORT_PARAMETERS)
    public SortParameters getSortParameters(@RequestParam(value = "sortOrder", required = false) String sortOrder,
                                            @RequestParam(value = "sortField", required = false) String sortField) {
        return new SortParameters(sortOrder, sortField);
    }

    @ModelAttribute(PAGING_PARAMETERS)
    public PagingParameters getPagingParameters(@RequestParam(value = "start", required = false) Integer start,
                                                @RequestParam(value = "rows", required = false) Integer rows) {
        return new PagingParameters(start, rows);
    }

    @ModelAttribute(FILTERS)
    public Map<String, Map<String, String>> getFilters(@RequestParam(value = KOULUTUSTYYPPI, required = false) String[] koulutustyyppi,
                                                       @RequestParam(value = KOULUTUSKIELI, required = false) String[] koulutuskieli,
                                                       @RequestParam(value = OPETUSMUOTO, required = false) String[] opetusmuoto,
                                                       @RequestParam(value = OPPILAITOSTYYPPI, required = false) String[] oppilaitostyyppi) {
        Map<String, Map<String, String>> filters = new HashMap<String, Map<String, String>>();
        arrayParametersToMap = new ArrayParametersToMap();
        filters.put(KOULUTUSTYYPPI, arrayParametersToMap.convert(koulutustyyppi));
        filters.put(KOULUTUSKIELI, arrayParametersToMap.convert(koulutuskieli));
        filters.put(OPETUSMUOTO, arrayParametersToMap.convert(opetusmuoto));
        filters.put(OPPILAITOSTYYPPI, arrayParametersToMap.convert(oppilaitostyyppi));
        return filters;
    }

    @ModelAttribute(MODEL_NAME_SEARCH_PARAMETERS)
    public SearchParameters getSearchParameters(@RequestParam(value = SEARCH_PARAMETER, required = false) String text,
                                                @ModelAttribute(SORT_PARAMETERS) SortParameters sortParameters,
                                                @ModelAttribute(PAGING_PARAMETERS) PagingParameters pagingParameters,
                                                @ModelAttribute(FILTERS) Map<String, Map<String, String>> filters) {

        HashSet<String> fields = new HashSet<String>();
        fields.add("id");
        fields.add("name");
        filters.put(SEARCH_PARAMETER, arrayParametersToMap.convert(new String[]{text}));
        return new SearchParameters(text, fields, sortParameters, pagingParameters, filters);
    }

    @RequestMapping(value = "/tarjontatiedot", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView listTarjontatiedot(@ModelAttribute(MODEL_NAME_SEARCH_PARAMETERS) SearchParameters searchParameters, @RequestParam(value = "update", defaultValue = "false") boolean update) {
        System.out.println(searchParameters);
        SearchResult searchResult = service.search(searchParameters);
        ModelAndView modelAndView = new ModelAndView(VIEW_NAME_ITEMS);
        modelAndView.addObject(MODEL_NAME, searchResult);
        modelAndView.addObject(PARAMETERS, searchParameters);
        modelAndView.addObject(SEARCH_FILTERS, searchFilters.getFilters());
        return modelAndView;
    }


    @RequestMapping(value = "/tarjontatiedot/{tarjontatietoId}", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView getTarjontatiedot(@PathVariable final String tarjontatietoId) {
        Map<String, Map<String, String>> filters = new HashMap<String, Map<String, String>>();
        filters.put(TUNNISTE, arrayParametersToMap.convert(new String[]{tarjontatietoId}));
        SearchParameters searchParameters = new SearchParameters(filters);
        Map<String, Object> searchResult = service.searchById(searchParameters);
        ModelAndView modelAndView = new ModelAndView(VIEW_NAME_KOULUTUSKUVAUS);
        modelAndView.addObject(MODEL_NAME, searchResult);
        return modelAndView;
    }

}
