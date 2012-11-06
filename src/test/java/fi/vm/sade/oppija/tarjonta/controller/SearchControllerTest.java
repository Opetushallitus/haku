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

import fi.vm.sade.oppija.tarjonta.domain.SearchFilters;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.domain.exception.SearchException;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SearchControllerTest {

    private SearchController searchController;

    @Before
    public void setUp() throws Exception {
        SearchService searchService = createMockService();
        SearchFilters searchFilters = new SearchFilters(searchService);
        searchController = new SearchController(searchService, searchFilters);
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
            List<Map<String, Collection<Object>>> items = new ArrayList<Map<String, Collection<Object>>>(0);

            @Override
            public SearchResult search(MultiValueMap<String, String> parameters) throws SearchException {
                return new SearchResult(items);
            }

            @Override
            public Map<String, Object> searchById(String field) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<String> getUniqValuesByField(String field) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }
}
