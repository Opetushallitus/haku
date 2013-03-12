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

import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminControllerTest {

    private AdminController adminController;
    private FormModelHolder formModelHolder;

    @Before
    public void setUp() throws Exception {
        formModelHolder = mock(FormModelHolder.class);
        when(formModelHolder.getModel()).thenReturn(null);
        adminController = new AdminController(formModelHolder);
    }

    @Test
    public void testUploadTemplate() throws Exception {
        Viewable viewable = adminController.upload();
        assertEquals("Unexpected template name", AdminController.ADMIN_UPLOAD_VIEW, viewable.getTemplateName());
    }

    @Test
    public void testUploadModel() throws Exception {
        Viewable upload = adminController.upload();
        assertTrue(upload.getModel() == AdminController.ATTACHMENT_MODEL);
    }

    @Test
    public void testGetIndex() throws Exception {
        Viewable viewable = adminController.getIndex();
        assertEquals("Unexpected template name", AdminController.ADMIN_INDEX_VIEW, viewable.getTemplateName());
        assertTrue("Unexpected template name", 8 == ((Map) viewable.getModel()).size());
    }

    @Test
    public void testEditModel() throws Exception {
        Viewable viewable = adminController.editModel();
        assertEquals("Unexpected template name", AdminController.ADMIN_EDIT_VIEW, viewable.getTemplateName());

    }
}
