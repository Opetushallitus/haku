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
package fi.vm.sade.oppija.koulutusinformaatio.resource;

import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Mikko Majapuro
 */
@Component
@Path("/education/")
public class SearchResourceMock {

    @GET
    @Path("/lop/search/{term}")
    @Produces(MediaType.APPLICATION_JSON)
    public String organizationSearch(@PathParam("term") final String term) {
        if (term.equalsIgnoreCase("esp")) {
            return "[{\"id\":\"1.2.246.562.10.89537774706\",\"name\":\"FAKTIA, Espoo op\",\"key\":\"faktia, espoo op\"}," +
                    "{\"id\":\"1.2.246.562.10.10108401950\",\"name\":\"Espoon kaupunki\",\"key\":\"espoon kaupunki\"}]";
        } else {
            return "[]";
        }
    }

    @GET
    @Path("/ao/search/{asId}/{lopId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String hakukohdeSearch(@PathParam("asId") final String asId, @PathParam("lopId") final String lopId) {
        if (lopId.equalsIgnoreCase("1.2.246.562.10.89537774706")) {
            return "[{\"id\":\"1.2.246.562.14.673437691210\",\"name\":\"Talonrakennus ja ymäristösuunnittelu, yo\"," +
                    "\"educationDegree\":\"32\"},{\"id\":\"1.2.246.562.14.79893512065\",\"name\":\"Kaivosalan perustutkinto, " +
                    "pk\",\"educationDegree\":\"32\"},{\"id\":\"1.2.246.562.14.39251489298\",\"name\":\"Musiikkiteknologian " +
                    "koulutusohjelma, pk (Musiikkialan perustutkinto)\",\"educationDegree\":\"32\"},{\"id\":\"1.2.246.562.14.71344129359\"," +
                    "\"name\":\"Tuotteen suunnittelun ja valmistuksen koulutusohjelma, pk (Käsi- ja taideteollisuusalan perustutkinto)\"" +
                    ",\"educationDegree\":\"32\"}]";
        } else {
            return "[]";
        }
    }
}
