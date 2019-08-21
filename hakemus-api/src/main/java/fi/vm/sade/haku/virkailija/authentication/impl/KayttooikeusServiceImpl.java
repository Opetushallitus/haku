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

package fi.vm.sade.haku.virkailija.authentication.impl;

import com.google.gson.*;
import fi.vm.sade.haku.oppija.configuration.HakemusApiCallerId;
import fi.vm.sade.javautils.legacy_caching_rest_client.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.virkailija.authentication.KayttooikeusService;
import fi.vm.sade.properties.OphProperties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile(value = {"default", "vagrant", "devluokka"})
public class KayttooikeusServiceImpl implements KayttooikeusService {

    final Logger log = LoggerFactory.getLogger(KayttooikeusServiceImpl.class);

    private final String targetService;
    private final CachingRestClient cachingRestClient;
    private OphProperties urlConfiguration;
    private static String callerId = HakemusApiCallerId.callerId;

    @Autowired
    public KayttooikeusServiceImpl(
            OphProperties urlConfiguration,
    @Value("${cas.service.kayttooikeus-service}") String targetService,
    @Value("${haku.app.username.to.usermanagement}") String clientAppUser,
    @Value("${haku.app.password.to.usermanagement}") String clientAppPass) {
        this.urlConfiguration = urlConfiguration;
        cachingRestClient = new CachingRestClient(callerId);
        cachingRestClient.setWebCasUrl(urlConfiguration.url("cas.url"));
        cachingRestClient.setCasService(targetService);
        cachingRestClient.setUsername(clientAppUser);
        cachingRestClient.setPassword(clientAppPass);
        this.targetService = targetService;
    }

    @Override
    public List<String> getOrganisaatioHenkilo() {
        String personOid = SecurityContextHolder.getContext().getAuthentication().getName();
        String url = urlConfiguration.url("kayttooikeus-service.organisaatiohenkilo", personOid);
        try {
            List<String> orgs = new ArrayList<>();
            InputStream is = cachingRestClient.get(url);

            JsonArray orgJson = new JsonParser().parse(IOUtils.toString(is)).getAsJsonArray();
            for (JsonElement elem: orgJson) {
                JsonObject orgObj = elem.getAsJsonObject();
                String organization = orgObj.get("organisaatioOid").getAsString();
                if (!orgObj.get("passivoitu").getAsBoolean()){
                    orgs.add(organization);
                } else {
                    log.debug("Ignoring inactive organization: " + organization);
                }
            }

            return orgs;
        } catch (IOException e) {
            throw new RemoteServiceException(url, e);
        }
    }
}
