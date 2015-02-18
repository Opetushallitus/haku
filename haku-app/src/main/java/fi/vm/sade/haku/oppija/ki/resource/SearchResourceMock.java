/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.haku.oppija.ki.resource;

import java.io.IOException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl.KoulutusinformaatioServiceMockImpl;

/**
 * @author Mikko Majapuro
 */
@Component
@Path("/education/")
@Profile(value = {"dev", "it"})
public class SearchResourceMock {
    @Autowired
    private KoulutusinformaatioServiceMockImpl koulutusInformaatioMock;

    @GET
    @Path("/lop/search/{term}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String organizationSearch(@PathParam("term") final String term, @DefaultValue(value = "1") @QueryParam("baseEducation") final String baseEducation) throws IOException {
        return new ObjectMapper().writeValueAsString(koulutusInformaatioMock.organizationSearch(term, baseEducation));
    }

    @GET
    @Path("/ao/search/{asId}/{lopId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String hakukohdeSearch(@PathParam("asId") final String asId, @PathParam("lopId") final String lopId,
                                  @DefaultValue(value = "1") @QueryParam("baseEducation") final String baseEducation) throws IOException {
        return new ObjectMapper().writeValueAsString(koulutusInformaatioMock.hakukohdeSearch(lopId, baseEducation));
    }
}
