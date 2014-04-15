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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.resources;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.FormGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@Controller
@Path("/lomakkeenhallinta")
// @Secured("ROLE_APP_HAKEMUS_CRUD")
public class FormBuilderResource {

    private final FormGenerator formGenerator;
    private final ApplicationSystemService applicationSystemService;

    @Autowired
    public FormBuilderResource(final FormGenerator formGenerator, final ApplicationSystemService applicationSystemService) {
        this.formGenerator = formGenerator;
        this.applicationSystemService = applicationSystemService;
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    public Response generateOne(@PathParam("oid") final String oid) throws URISyntaxException {
        ApplicationSystem as = formGenerator.generateOne(oid);
        applicationSystemService.save(as);
        return Response.seeOther(new URI("/lomake/"+oid)).build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    public Response generate() throws URISyntaxException {
        List<ApplicationSystem> applicationSystems = formGenerator.generate();
        for (ApplicationSystem applicationSystem : applicationSystems) {
            applicationSystemService.save(applicationSystem);
        }
        return Response.seeOther(new URI("/lomake/")).build();
    }
}
