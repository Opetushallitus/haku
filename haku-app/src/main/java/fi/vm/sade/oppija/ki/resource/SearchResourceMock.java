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
package fi.vm.sade.oppija.ki.resource;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Mikko Majapuro
 */
@Component
@Path("/education/")
@Profile("dev")
public class SearchResourceMock {

    @GET
    @Path("/lop/search/{term}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String organizationSearch(@PathParam("term") final String term, @DefaultValue(value = "1") @QueryParam("baseEducation") final String baseEducation) {
        if (term.equalsIgnoreCase("esp")) {
            return "[{\"id\":\"1.2.246.562.10.89537774706\",\"name\":\"FAKTIA, Espoo op\",\"key\":\"faktia, espoo op\"}," +
                    "{\"id\":\"1.2.246.562.10.10108401950\",\"name\":\"Espoon kaupunki\",\"key\":\"espoon kaupunki\"}]";
        } else if (term.equalsIgnoreCase("sturen") && baseEducation.equals("9")) {
            return "[{\"id\":\"1.2.246.562.10.51872958189\",\"name\":\"Stadin ammattiopisto, Sturenkadun toimipaikka\"}]";
        } else {
            return "[]";
        }
    }

    @GET
    @Path("/ao/search/{asId}/{lopId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String hakukohdeSearch(@PathParam("asId") final String asId, @PathParam("lopId") final String lopId,
                                  @DefaultValue(value = "1") @QueryParam("baseEducation") final String baseEducation) {
        if (lopId.equalsIgnoreCase("1.2.246.562.10.89537774706")) {
            return "[{\"id\":\"1.2.246.562.14.673437691210\"," +
                    "\"name\":\"Talonrakennus ja ymäristösuunnittelu, yo\"," +
                    "\"educationDegree\":\"32\", " +
                    "\"sora\": true, " +
                    "\"teachingLanguages\":[\"FI\"], " +
                    "\"childLONames\":[" +
                    "\"Käsi- ja taideteollisuusalan perustutkinto, Tuotteen suunnittelun ja valmistuksen koulutusohjelma\"," +
                    "\"Käsi- ja taideteollisuusalan perustutkinto, Ympäristön suunnittelun ja rakentamisen koulutusohjelma" +
                    "\"]}," +
                    "{\"id\":\"1.2.246.562.14.79893512065\"," +
                    "\"name\":\"Kaivosalan perustutkinto, pk\"," +
                    "\"aoIdentifier\":\"333\"," +
                    "\"educationDegree\":\"32\", " +
                    "\"sora\": true , " +
                    "\"teachingLanguages\":[\"FI\"]," +
                    "\"athleteEducation\":true, " +
                    "\"childLONames\":[" +
                    "\"Kaivosalan perustutkinto, Kaivosalan koulutusohjelma\"]}," +
                    "{\"id\":\"1.2.246.562.14.39251489298\"," +
                    "\"name\":\"Musiikkiteknologian koulutusohjelma, pk (Musiikkialan perustutkinto)\"," +
                    "\"educationDegree\":\"32\", " +
                    "\"teachingLanguages\":[\"FI\"], " +
                    "\"childLONames\":[" +
                    "\"Musiikkialan perustutkinto, Musiikkiteknologian koulutusohjelma\"," +
                    "\"Musiikkialan perustutkinto, Pianonvirityksen koulutusohjelma\"]}," +
                    "{\"id\":\"1.2.246.562.14.71344129359\"," +
                    "\"name\":\"Tuotteen suunnittelun ja valmistuksen koulutusohjelma, pk (Käsi- ja taideteollisuusalan perustutkinto)\", " +
                    "\"educationDegree\":\"30\", " +
                    "\"athleteEducation\":true, " +
                    "\"sora\": false, " +
                    "\"teachingLanguages\":[\"SV\"], " +
                    "\"childLONames\":[" +
                    "\"Käsi- ja taideteollisuusalan perustutkinto, Tuotteen suunnittelun ja valmistuksen koulutusohjelma\"]}]";
        } else if (lopId.equalsIgnoreCase("1.2.246.562.10.51872958189") && baseEducation.equals("9")) {
            return "[{\"id\":\"1.2.246.562.5.20176855623\",\"name\":\"Tieto- ja tietoliikennetekniikan perustutkinto, yo\"," +
                    "\"aoIdentifier\":\"143\",\"educationDegree\":\"32\",\"childLONames\":[\"Tieto- ja tietoliikennetekniikka, " +
                    "elektroniikka-asentaja\"],\"sora\":false,\"teachingLanguages\":[\"FI\"],\"athleteEducation\":true}," +
                    "{\"id\":\"1.2.246.562.5.32094353409\",\"name\":\"Turvallisuusalan perustutkinto, yo\",\"aoIdentifier\":\"505\"," +
                    "\"educationDegree\":\"32\",\"childLONames\":[\"Turvallisuusala, turvallisuusvalvoja\"],\"sora\":false," +
                    "\"teachingLanguages\":[\"FI\"],\"athleteEducation\":true},{\"id\":\"1.2.246.562.5.37738069758\"," +
                    "\"name\":\"Maanmittausalan perustutkinto, yo\",\"aoIdentifier\":\"890\",\"educationDegree\":\"32\"," +
                    "\"childLONames\":[\"Maanmittaustekniikka, kartoittaja\"],\"sora\":false,\"teachingLanguages\":[\"FI\"]," +
                    "\"athleteEducation\":true},{\"id\":\"1.2.246.562.5.52308596866\",\"name\":\"Suunnitteluassistentin perustutkinto, " +
                    "yo\",\"aoIdentifier\":\"668\",\"educationDegree\":\"32\",\"childLONames\":[\"Tekninen suunnittelu, suunnitteluassistentti\"]," +
                    "\"sora\":false,\"teachingLanguages\":[\"FI\"],\"athleteEducation\":true},{\"id\":\"1.2.246.562.5.66688607689\"," +
                    "\"name\":\"Sähkö- ja automaatiotekniikan perustutkinto, yo\",\"aoIdentifier\":\"192\",\"educationDegree\":\"32\"," +
                    "\"childLONames\":[\"Sähkö- ja automaatiotekniikka, sähköasentaja\"],\"sora\":false,\"teachingLanguages\":[\"FI\"]," +
                    "\"athleteEducation\":true},{\"id\":\"1.2.246.562.5.79820899882\",\"name\":\"Puualan perustutkinto, yo\"," +
                    "\"aoIdentifier\":\"891\",\"educationDegree\":\"32\",\"childLONames\":[\"Teollisuuspuuseppä, puuseppä\"]," +
                    "\"sora\":false,\"teachingLanguages\":[\"FI\"],\"athleteEducation\":true},{\"id\":\"1.2.246.562.5.95890367071\"," +
                    "\"name\":\"Verhoilu- ja sisustusalan perustutkinto, yo\",\"aoIdentifier\":\"653\",\"educationDegree\":\"32\"," +
                    "\"childLONames\":[\"Sisustus, sisustaja\",\"Verhoilu, verhoilija\"],\"sora\":false,\"teachingLanguages\":[\"FI\"],\"athleteEducation\":true}]";
        } else {
            return "[]";
        }
    }
}
