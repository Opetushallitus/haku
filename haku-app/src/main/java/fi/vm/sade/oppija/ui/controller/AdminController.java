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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static javax.ws.rs.core.Response.created;


@Controller
@Path("/admin")
@Secured("ROLE_APP_HAKEMUS_CRUD")
public class AdminController {

    private static final String CHARSET_UTF_8 = ";charset=UTF-8";

    @Autowired
    public FormModelHolder formModelHolder;

    @GET
    @Path("/model")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public FormModel asJson() {
        return formModelHolder.getModel();
    }

    @POST
    @Path("/model")
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public Response replaceModel(final FormModel formModel) throws URISyntaxException {
        formModelHolder.updateModel(formModel);
        return created(new URI("/lomake/")).build();
    }
}
