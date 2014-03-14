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

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.resource.DateParam;
import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.dto.ThemeQuestionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;


@Controller
@Path("/themequestion")
public class ThemeQuestionResource {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionResource.class);

    public ThemeQuestionResource() {
    }

    //TODO: Fix with real implemenetation
    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public Application getThemedQuestionByOid(@PathParam("oid") String oid) {
        LOGGER.debug("Getting application by oid : {}", oid);
        throw new JSONException(Response.Status.NOT_FOUND, "Not implemented", null);
    }

    //TODO: Fix with real implemenetation
    @POST
    @Path("{oid}/{theme}/{type}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public void postThemeQuestion(@PathParam("oid") String oid,
                                    @PathParam("theme") String theme,
                                    @PathParam("type") String type,
                                    List<ThemeQuestionDTO> questionParameters) {
        throw new JSONException(Response.Status.NOT_FOUND, "Not implemented", null);
    }
}
