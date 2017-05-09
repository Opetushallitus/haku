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

import com.google.common.base.Optional;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.virkailija.authentication.KayttooikeusService;
import fi.vm.sade.properties.OphProperties;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile(value = {"default", "vagrant"})
public class KayttooikeusServiceImpl implements KayttooikeusService {

    final Logger log = LoggerFactory.getLogger(KayttooikeusServiceImpl.class);

    private final String targetService;
    private final CachingRestClient cachingRestClient;
    private final String userOidPrefix;
    private final String langCookieName;
    private OphProperties urlConfiguration;

    @Autowired
    public KayttooikeusServiceImpl(
            OphProperties urlConfiguration,
    @Value("${cas.service.kayttooikeus-service}") String targetService,
    @Value("${haku.app.username.to.usermanagement}") String clientAppUser,
    @Value("${haku.app.password.to.usermanagement}") String clientAppPass,
    @Value("${user.oid.prefix}") String userOidPrefix,
    @Value("${haku.langCookie}") String langCookieName) {
        this.urlConfiguration = urlConfiguration;
        cachingRestClient = new CachingRestClient().setClientSubSystemCode("haku.hakemus-api");
        cachingRestClient.setWebCasUrl(urlConfiguration.url("cas.url"));
        cachingRestClient.setCasService(targetService);
        cachingRestClient.setUsername(clientAppUser);
        cachingRestClient.setPassword(clientAppPass);
        this.targetService = targetService;
        this.userOidPrefix = userOidPrefix;
        this.langCookieName = langCookieName;
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
                String organization = orgObj.get("organisaatio.oid").getAsString();
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
