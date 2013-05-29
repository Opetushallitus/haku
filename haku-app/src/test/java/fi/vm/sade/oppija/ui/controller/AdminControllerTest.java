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

import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminControllerTest {

    private AdminController adminController;
    private FormModelHolder formModelHolder;
    private FormModel formModel;

    @Before
    public void setUp() throws Exception {
        formModelHolder = mock(FormModelHolder.class);
        formModel = new FormModel();
        when(formModelHolder.getModel()).thenReturn(formModel);
        adminController = new AdminController();
        adminController.formModelHolder = formModelHolder;
    }

    @Test
    public void testAsJson() throws Exception {
        assertEquals(this.formModel, adminController.asJson());
    }
}
