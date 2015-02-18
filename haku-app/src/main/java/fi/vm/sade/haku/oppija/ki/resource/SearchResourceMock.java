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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;

/**
 * @author Mikko Majapuro
 */
@Component
@Path("/education/")
@Profile(value = {"dev", "it"})
public class SearchResourceMock {

    @GET
    @Path("/lop/search/{term}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<Map<String, String>> organizationSearch(@PathParam("term") final String term, @DefaultValue(value = "1") @QueryParam("baseEducation") final String baseEducation) {
        if (term.equalsIgnoreCase("esp")) {
            return ImmutableList.<Map<String, String>>of(
                    ImmutableMap.of("id", "1.2.246.562.10.89537774706", "name", "FAKTIA, Espoo op", "key", "faktia, espoo op"),
                    ImmutableMap.of("id", "1.2.246.562.10.10108401950", "name", "Espoon kaupunki", "key", "espoon kaupunki")
            );
        } else if (term.equalsIgnoreCase("sturen") && baseEducation.equals("9")) {
            return ImmutableList.<Map<String, String>>of(
                    ImmutableMap.of("id", "1.2.246.562.10.51872958189", "name", "Stadin ammattiopisto, Sturenkadun toimipaikka")
            );
        } else if (term.equalsIgnoreCase("anna")) {
            return ImmutableList.<Map<String, String>>of(
                    ImmutableMap.of("id", "1.2.246.562.10.35241670047", "name", "Anna Tapion koulu")
            );
        } else if (term.equalsIgnoreCase("urh")) {
            return ImmutableList.<Map<String, String>>of(
                    ImmutableMap.of("id", "1.2.246.562.10.35241670048", "name", "Urheilijoiden koulu", "key", "urheilijoiden koulu")
            );
        } else {
            return ImmutableList.of();
        }
    }

    public static void main(String... args) {
        System.out.println(new SearchResourceMock().hakukohdeSearch(null, "1.2.246.562.10.89537774706", "9"));
        System.out.println(new SearchResourceMock().hakukohdeSearch(null, "1.2.246.562.10.51872958189", "9"));
        System.out.println(new SearchResourceMock().hakukohdeSearch(null, "1.2.246.562.10.35241670047", "9"));
        System.out.println(new SearchResourceMock().hakukohdeSearch(null, "1.2.246.562.10.35241670048", "9"));
    }

    @GET
    @Path("/ao/search/{asId}/{lopId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String hakukohdeSearch(@PathParam("asId") final String asId, @PathParam("lopId") final String lopId,
                                  @DefaultValue(value = "1") @QueryParam("baseEducation") final String baseEducation) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, List<ApplicationOptionSearchResultDTO>> optionMap = objectMapper.readValue(getClass().getResourceAsStream("/mockdata/koulutukset.json"), new TypeReference<Map<String, List<ApplicationOptionSearchResultDTO>>>() {
            });
            List<ApplicationOptionSearchResultDTO> applicationOptions = optionMap.get(lopId);
            if (applicationOptions == null) applicationOptions = optionMap.get(lopId + "/" + baseEducation);
            if (applicationOptions == null) applicationOptions = Collections.EMPTY_LIST;
            for (ApplicationOptionSearchResultDTO x : applicationOptions) {
                x.getOrganizationGroups().size(); // Just checking for nulls
            }
            return objectMapper.writeValueAsString(applicationOptions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
