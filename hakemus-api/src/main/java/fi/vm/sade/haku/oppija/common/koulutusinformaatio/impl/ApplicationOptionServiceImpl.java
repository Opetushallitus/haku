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

package fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl;

import com.google.common.base.Strings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionDTOToApplicationOptionFunction;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * @author Mikko Majapuro
 */
@Service
@Profile(value = {"default", "devluokka"})
public class ApplicationOptionServiceImpl implements ApplicationOptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationOptionServiceImpl.class);
    private final WebResource webResource;
    private final Client clientWithJacksonSerializer;
    private final ApplicationOptionDTOToApplicationOptionFunction converterFunction;

    @Autowired
    public ApplicationOptionServiceImpl(@Value("${koulutusinformaatio.ao.resource.url}") final String koulutusinformaatioAOResourceUrl) {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        clientWithJacksonSerializer = Client.create(cc);
        webResource = clientWithJacksonSerializer.resource(koulutusinformaatioAOResourceUrl);
        converterFunction = new ApplicationOptionDTOToApplicationOptionFunction();
    }

    @Override
    public ApplicationOption get(String oid) {
        LOGGER.debug("get application option : {}", oid);
        if (Strings.isNullOrEmpty(oid)) {
            return null;
        } else {
            WebResource asWebResource = webResource.path(oid);
            ApplicationOptionDTO ao = asWebResource.accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").get(new GenericType<ApplicationOptionDTO>() {
            });
            return converterFunction.apply(ao);
        }
    }

    @Override
    public ApplicationOption get(String oid, String lang) {
        LOGGER.debug("get application option : {}", oid);
        if (Strings.isNullOrEmpty(oid)) {
            return null;
        } else {
            UriBuilder builder = webResource.path(oid).getUriBuilder();
            builder.queryParam("uiLang", lang).build();
            LOGGER.debug(builder.build().toString());
            WebResource asWebResource = clientWithJacksonSerializer.resource(builder.build());
            ApplicationOptionDTO ao = asWebResource.accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").get(new GenericType<ApplicationOptionDTO>() {
            });
            return converterFunction.apply(ao);
        }
    }
}
