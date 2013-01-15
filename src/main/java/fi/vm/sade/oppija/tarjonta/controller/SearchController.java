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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.tarjonta.domain.SearchFilters;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;


@Controller
@Path("tarjontatiedot")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    public static final String VIEW_NAME_ITEMS = "/tarjonta/tarjontatiedot";
    public static final String VIEW_NAME_KOULUTUSKUVAUS = "/tarjonta/koulutuskuvaus";
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

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable listTarjontatiedot(@QueryParam("text") String text) {
        //LOGGER.debug("parameters: " + parameters);
        Map<String, List<String>> parameters = new HashMap<String, List<String>>(1);
        parameters.put("text", Lists.newArrayList(text));
        Set<Map.Entry<String, List<String>>> setOfParameters = parameters.entrySet();
        SearchResult searchResult = service.search(setOfParameters);
        Map<String, String> parameterMaps = toSingleValueMap(setOfParameters);

        ImmutableMap<String, Object> model = ImmutableMap.of(
                MODEL_NAME, searchResult,
                PARAMETERS_MULTI_VALUE, parameters,
                PARAMETERS_SINGLE_VALUE, parameterMaps,
                SEARCH_FILTERS, searchFilters.getFilters());
        return new Viewable(VIEW_NAME_ITEMS, model);
    }

    @GET
    @Path("{tarjontatietoId}")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getTarjontatiedot(@PathParam("tarjontatietoId") final String tarjontatietoId) {
        LOGGER.info("tarjontatiedot/" + tarjontatietoId);
        ImmutableMap<String, Map<String, Object>> model = ImmutableMap.of(MODEL_NAME, service.searchById(tarjontatietoId));
        return new Viewable(VIEW_NAME_KOULUTUSKUVAUS, model);
    }

    public static Map<String, String> toSingleValueMap(final Set<Map.Entry<String, List<String>>> setOfparameters) {
        LinkedHashMap<String, String> singleValueMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, List<String>> stringStringEntry : setOfparameters) {
            singleValueMap.put(stringStringEntry.getKey(), stringStringEntry.getValue().get(0));
        }
        return singleValueMap;
    }

}

