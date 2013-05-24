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

package fi.vm.sade.oppija.lomakkeenhallinta.resources;

import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;


@Controller
@Path("/lomakkeenhallinta")
@Secured("ROLE_APP_HAKEMUS_CRUD")
public class FormBuilderResource {

    @Autowired
    private FormModelHolder formModelHolder;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response generate() throws URISyntaxException {
        boolean b = formModelHolder.generateAndReplace();
        if (b) {
            return Response.seeOther(new URI("/lomake/")).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Lomakkeen luonti epäonnistui(koodisto)").build();
        }
    }

}
