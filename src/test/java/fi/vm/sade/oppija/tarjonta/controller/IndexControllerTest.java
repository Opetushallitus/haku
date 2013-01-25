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

import fi.vm.sade.oppija.tarjonta.service.IndexService;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class IndexControllerTest {

    public static final String EXPECTED_STRING = "done";
    private IndexController indexController;
    private IndexService indexService;

    @Before
    public void setUp() throws Exception {
        indexService = new IndexService() {

            @Override
            public String update(URI uri) {
                return EXPECTED_STRING;
            }

            @Override
            public boolean drop() {
                return true;
            }
        };
        indexController = new IndexController();
        indexController.tarjontaUrl = "";
        indexController.indexService = indexService;
    }

    @Test
    public void testUpdateIndex() throws Exception {
        assertEquals(EXPECTED_STRING, indexController.updateIndex());
    }

    @Test
    public void testDropIndex() throws Exception {
        assertEquals(String.valueOf(true), indexController.dropIndex());
    }
}
