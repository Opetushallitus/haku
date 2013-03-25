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

import com.google.common.collect.Lists;
import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.koulutusinformaatio.domain.search.SearchFilters;
import fi.vm.sade.koulutusinformaatio.domain.search.SearchResult;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchControllerTest {

    public static final String KEY = "key";
    public static final String VALUE = "value";
    private SearchController searchController;
    private SearchService searchService;

    @Before
    public void setUp() throws Exception {
        searchService = mock(SearchService.class);

        SearchFilters searchFilters = new SearchFilters(searchService);
        searchController = new SearchController(searchService, searchFilters, null);
    }

    @Test
    public void testListTarjontatiedot() throws Exception {
        List<Map<String, Collection<Object>>> results = new ArrayList<Map<String, Collection<Object>>>(0);
        SearchResult searchResult = new SearchResult(results);
        when(searchService.search(anySet())).thenReturn(searchResult);
        Viewable viewable = searchController.listTarjontatiedot(VALUE);
        assertEquals(viewable.getTemplateName(), SearchController.VIEW_NAME_ITEMS);

    }

    @Test
    public void testGetTarjontatiedot() throws Exception {
        searchController.getTarjontatiedot("1");
    }

    @Test
    public void testToSingleValueMap() throws Exception {
        Map<String, List<String>> parameters = new HashMap<String, List<String>>(1);
        parameters.put(KEY, Lists.newArrayList(VALUE));
        Set<Map.Entry<String, List<String>>> setOfParameters = parameters.entrySet();
        Map<String, String> values = SearchController.toSingleValueMap(setOfParameters);
        assertEquals(VALUE, values.get(KEY));
    }
}
