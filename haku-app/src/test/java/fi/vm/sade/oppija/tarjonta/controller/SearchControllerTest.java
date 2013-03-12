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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import fi.vm.sade.koulutusinformaatio.domain.SearchFilters;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;

public class SearchControllerTest {

    private SearchController searchController;

    @Before
    public void setUp() throws Exception {
        SearchService searchService = mock(SearchService.class);

        SearchFilters searchFilters = new SearchFilters(searchService);
        searchController = new SearchController(searchService, searchFilters, null);
    }

    @Test
    public void testGetTarjontatiedot() throws Exception {
        searchController.getTarjontatiedot("1");
    }

}
