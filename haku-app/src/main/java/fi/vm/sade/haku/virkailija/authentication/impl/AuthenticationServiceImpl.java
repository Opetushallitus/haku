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
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.oppija.common.HttpClientHelper;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonJsonAdapter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
//@Profile("dev")
public class AuthenticationServiceImpl implements AuthenticationService {

    final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);


    @Value("${web.url.cas}")
    private String casUrl;

    @Value("${cas.service.authentication-service}")
    private String targetService;
    @Value("${haku.app.username.to.usermanagement}")
    private String clientAppUser;
    @Value("${haku.app.password.to.usermanagement}")
    private String clientAppPass;

    private HttpClientHelper clientHelper;

    private CachingRestClient cachingRestClient;

    private Gson gson;

    public Person addPerson(Person person) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Person.class, new PersonJsonAdapter());
        gson = gsonBuilder.create();

        String responseString = null;
        try {
            responseString = getCachingRestClient().getAsString("/resources/henkilo/byHetu/"+person.getSocialSecurityNumber());
            log.debug("Person found: "+responseString);
        } catch (CachingRestClient.HttpException hte) {
            log.debug("HttpException: "+hte.getStatusCode());
            try {
                if (hte.getStatusCode() == 404) {
                    log.debug("Person not found, creating");
                    String personJson = gson.toJson(person, Person.class);
                    CachingRestClient client = getCachingRestClient();
                    HttpResponse response = client.post("/resources/henkilo", MediaType.APPLICATION_JSON, personJson);
                    BasicResponseHandler handler = new BasicResponseHandler();
                    String oid = handler.handleResponse(response);
                    responseString = getCachingRestClient().getAsString("/resources/henkilo/"+oid);
                    log.debug("Created person: "+responseString);
                } else {
                    log.warn("Something unexpected happened while fetching person: " + hte.getErrorContent());
                }
            } catch (IOException e) {
                log.error("Fetching or creating person failed: ", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            log.error("Fetching or creating person failed: ", e);
            return null;
        }

        Person newPerson = gson.fromJson(responseString, Person.class);
        return newPerson;
    }

    @Override
    public List<String> getOrganisaatioHenkilo() {
        String personOid = SecurityContextHolder.getContext().getAuthentication().getName();
        String url = "/resources/henkilo/" + personOid + "/organisaatiohenkilo";
        try {
            List<String> orgs = new ArrayList<String>();
            CachingRestClient cachingRestClient = getCachingRestClient();
            if (log.isDebugEnabled()) {
                log.debug("Getting organisaatiohenkilos for {}", personOid);
                log.debug("Using cachingRestClient webCasUrl: {}, casService: {} ", cachingRestClient.getWebCasUrl(), cachingRestClient.getCasService());
            }
            InputStream is = null;

            is = cachingRestClient.get(url);

            JsonArray orgJson = new JsonParser().parse(IOUtils.toString(is)).getAsJsonArray();
            Iterator<JsonElement> elems = orgJson.iterator();
            while (elems.hasNext()) {
                JsonObject orgObj = elems.next().getAsJsonObject();
                orgs.add(orgObj.get("organisaatioOid").getAsString());
            }
            return orgs;
        } catch (IOException e) {
            throw new RemoteServiceException(url, e);
        }
    }

    private synchronized CachingRestClient getCachingRestClient() {
        if (cachingRestClient == null) {
            cachingRestClient = new CachingRestClient();
            cachingRestClient.setWebCasUrl(casUrl);
            cachingRestClient.setCasService(targetService);
            cachingRestClient.setUsername(clientAppUser);
            cachingRestClient.setPassword(clientAppPass);
        }
        return cachingRestClient;
    }

    @Override
    public String getStudentOid(String personOid) {
        String url = getClientHelper().getRealUrl(personOid + "/yksiloi");
        HttpClient client = new HttpClient();
        PutMethod put = new PutMethod(url);
        try {
            client.executeMethod(put);
            return personOid;
        } catch (IOException e) {
            log.error("Getting studentOid for {} failed due to: {}", personOid, e.toString());
            return null;
        }
    }

    @Override
    public String checkStudentOid(String personOid) {
        String url = getClientHelper().getRealUrl(personOid);
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(url);
        try {
            client.executeMethod(get);
        } catch (IOException e) {
            log.error("Getting studentOid for {} failed due to: {}", personOid, e.toString());
            return null;
        }

        int status = get.getStatusCode();
        if (status == 200) {
            String responseString = null;
            try {
                responseString = get.getResponseBodyAsString();
            } catch (IOException e) {
                // It's because I'm lazy
                throw new RuntimeException(e);
            }
            JsonObject henkiloJson = new JsonParser().parse(responseString).getAsJsonObject();
            String oid = null;
            if (!henkiloJson.get("oppijanumero").isJsonNull()) {
                oid = henkiloJson.get("oppijanumero").getAsString();
            }
            return oid;
        }

        return null;

    }

    private HttpClientHelper getClientHelper() {
        if (this.clientHelper == null) {
            this.clientHelper = new HttpClientHelper(casUrl, targetService, "/resources/henkilo/", clientAppUser, clientAppPass);
        }
        return this.clientHelper;
    }
}
