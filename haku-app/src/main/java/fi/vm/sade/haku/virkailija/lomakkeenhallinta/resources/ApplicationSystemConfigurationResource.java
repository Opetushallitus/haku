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

import com.sun.jersey.api.Responses;
import fi.vm.sade.haku.oppija.repository.ApplicationSystemConfigurationRepository;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ApplicationSystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;


@Controller
@Path("/application-system-configuration")
public class ApplicationSystemConfigurationResource {

    private static final Logger log = LoggerFactory.getLogger(ApplicationSystemConfigurationResource.class);
    public static final String JSON = MediaType.APPLICATION_JSON + ";charset=UTF-8";

    private final ApplicationSystemConfigurationRepository applicationSystemConfigurationRepository;

    @Autowired
    public ApplicationSystemConfigurationResource(ApplicationSystemConfigurationRepository applicationSystemConfigurationRepository) {
        this.applicationSystemConfigurationRepository = applicationSystemConfigurationRepository;
    }

    @GET
    @Produces(JSON)
    public List<ApplicationSystemConfiguration> listConfiguration() {
        return applicationSystemConfigurationRepository.list();
    }

    @GET
    @Path("{asid}")
    @Produces(JSON)
    public Response getConfiguration(@PathParam("asid") final String asid) {
        ApplicationSystemConfiguration byId = applicationSystemConfigurationRepository.findById(asid);
        System.out.println(byId);
        if (byId == null)
            return Responses.notFound().build();

        return Response.ok(byId).build();
    }

    @POST
    @Consumes(JSON)
    @Produces(JSON)
    public ApplicationSystemConfiguration addConfiguration(final Map<String, String> body) {
        return this.applicationSystemConfigurationRepository.save(new ApplicationSystemConfiguration(body.get("asid"), body.get("configuration")));
    }
}
