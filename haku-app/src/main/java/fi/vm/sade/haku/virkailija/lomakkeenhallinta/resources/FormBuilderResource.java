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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGenerator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.List;


@Controller
@Path("/generatelomake")
public class FormBuilderResource {

    private static final Logger log = LoggerFactory.getLogger(FormBuilderResource.class);

    @Autowired
    private FormGenerator formGenerator;

    @Autowired
    private ApplicationSystemService applicationSystemService;

    @Autowired
    private HakuService hakuService;

    @POST
    @Path("one/{oid}")
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    @PreAuthorize("hasRole('" + FormBuilderPermissionChecker.ROLE_GENERATE_ALL_HAKUS + "') or @formBuilderPermissionChecker.isAllowedToGenerateHaku(#oid)")
    public Response generateOne(@Param("oid") @PathParam("oid") final String oid) throws URISyntaxException {
        log.info("Starting to generate application system " + oid);
        doGenerate(oid);
        log.info("Generated application system " +oid);
        return Response.ok().build();
    }

    @POST
    @Path("all")
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    @PreAuthorize("hasRole('" + FormBuilderPermissionChecker.ROLE_GENERATE_ALL_HAKUS + "')")
    public Response doGenerateAll() throws URISyntaxException {
        log.info("Loading application systems for generation");
        List<ApplicationSystem> applicationSystems = hakuService.getApplicationSystems();
        int asCount = applicationSystems.size();
        int index = 1;
        log.info("Starting to generate "+ asCount+ " application systems");
        for (ApplicationSystem applicationSystem : applicationSystems) {
            log.info("Application system generation cycle " + index++ +"/" + asCount);
            doGenerate(applicationSystem.getId());
            log.info("Generated application system " +applicationSystem.getId());
        }
        return Response.ok().build();
    }

    private void doGenerate(final String oid) {
        log.debug("Generating application system " + oid);
        try {
            ApplicationSystem as = formGenerator.generate(oid);
            applicationSystemService.save(as);
        } catch (RuntimeException exception) {
            log.error("Application system generation failed for " + oid, exception);
        }
        log.debug("Generated application system " + oid);
    }
}
