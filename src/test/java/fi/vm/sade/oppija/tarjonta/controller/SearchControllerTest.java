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

import fi.vm.sade.oppija.tarjonta.domain.*;
import fi.vm.sade.oppija.tarjonta.domain.exception.SearchException;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchControllerTest {

    public static final String SORT_ORDER = "decs";
    public static final String SORT_FIELD = "name";
    private SearchController searchController;

    @Before
    public void setUp() throws Exception {
        SearchService searchService = createMockService();
        SearchFilters searchFilters = new SearchFilters(searchService);
        searchController = new SearchController(searchService, searchFilters);
    }

    @Test
    public void testGetSortParametersOrder() throws Exception {
        SortParameters sortParameters = searchController.getSortParameters(SORT_ORDER, SORT_FIELD);
        assertEquals(SORT_ORDER, sortParameters.getSortOrder());
    }

    @Test
    public void testGetSortParametersField() throws Exception {
        SortParameters sortParameters = searchController.getSortParameters(SORT_ORDER, SORT_FIELD);
        assertEquals(SORT_FIELD, sortParameters.getSortField());
    }

    @Test
    public void testGetPagingParametersStart() throws Exception {
        PagingParameters pagingParameters = searchController.getPagingParameters(1, 10);
        assertEquals(new Integer(1), pagingParameters.getStart());
    }

    @Test
    public void testGetPagingParametersCount() throws Exception {
        PagingParameters pagingParameters = searchController.getPagingParameters(1, 10);
        assertEquals(new Integer(10), pagingParameters.getRows());
    }

    @Test
    public void testGetFilters() throws Exception {
        Map<String, Map<String, String>> filters = searchController.getFilters(null, null, null, null);
        assertEquals(4, filters.size());
    }

    @Test
    public void testGetSearchParameters() throws Exception {
        SearchParameters searchParameters = searchController.getSearchParameters("text", null, null, searchController.getFilters(null, null, null, null));
        assertTrue(searchParameters.getFields().contains("id"));
        assertTrue(searchParameters.getFields().contains("name"));
    }

    @Test
    public void testExceptions() throws Exception {
        String expected = "msg";
        Throwable t = new Throwable(expected);
        ModelAndView mav = searchController.exceptions(t);
        Object actual = mav.getModel().get("message");
        assertEquals(expected, actual);
    }


    private SearchService createMockService() {
        return new SearchService() {
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>(0);

            @Override
            public SearchResult search(SearchParameters searchParameters) throws SearchException {
                return new SearchResult(items);
            }

            @Override
            public Map<String, Object> searchById(SearchParameters searchParameters) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<String> getUniqValuesByField(String field) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }
}
