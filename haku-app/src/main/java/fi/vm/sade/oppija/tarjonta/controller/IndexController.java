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

import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Path("admin/index")
@Controller
@Secured("ROLE_ADMIN")
public class IndexController {

    @Value("${tarjonta.data.url}")
    String tarjontaUrl;

    @Autowired
    @Qualifier("indexerServiceImpl")
    IndexerService indexerService;

    @GET
    @Path("update")
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    public String updateIndex() throws URISyntaxException {
        URI uri = new URI(tarjontaUrl);
        return indexerService.update(uri);
    }

    @GET
    @Path("drop")
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    public String dropIndex() throws IOException {
        return Boolean.toString(indexerService.drop());
    }

}
