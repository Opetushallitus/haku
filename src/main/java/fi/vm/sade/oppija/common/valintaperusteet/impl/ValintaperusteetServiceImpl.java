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
package fi.vm.sade.oppija.common.valintaperusteet.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.ValintaperusteetService;

public class ValintaperusteetServiceImpl implements ValintaperusteetService {

    private final WebResource service;

    private final MapToAdditionalQuestionsFunction converter = new MapToAdditionalQuestionsFunction();

    public ValintaperusteetServiceImpl(@Value("valintaperusteet.resource.url") final String baseUrl) {
        final ClientConfig config = new DefaultClientConfig();
        // json serialization:
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        // TODO configure threadpool size etc
        final Client client = Client.create(config);
        service = client.resource(baseUrl);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdditionalQuestions retrieveAdditionalQuestions(List<String> oids) throws IOException {

        if (oids != null && !oids.isEmpty()) {
            ClientResponse response = null;

            try {
                response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                        .post(ClientResponse.class, oids);

                Map<String, Map<String, Map<String, String>>> entity = null;
                if (response.getStatus() == 200) {
                    entity = response.getEntity(Map.class);
                    return converter.apply(entity);
                }
            } catch (Throwable t) {
                throw new IOException(String.format("Failed to retrieve data, exception: %s", t.getClass().getName()));
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (Throwable t) {
                        // silently ignore
                    }
                }
            }
            throw new IOException(String.format("Failed to retrieve data with params '%s', http status:%i", oids,
                    response.getStatus()));
        } else {
            return new AdditionalQuestions();
        }
    }
}