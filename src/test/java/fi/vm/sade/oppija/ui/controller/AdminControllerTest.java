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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdminControllerTest {

    AdminController adminController = new AdminController();

    @Test
    public void testUploadTemplate() throws Exception {
        Viewable upload = adminController.upload();
        assertEquals(AdminController.ADMIN_UPLOAD_VIEW, upload.getTemplateName());
    }

    @Test
    public void testUploadModel() throws Exception {
        Viewable upload = adminController.upload();
        assertTrue(upload.getModel() == AdminController.ATTACHMENT_MODEL);
    }
}
