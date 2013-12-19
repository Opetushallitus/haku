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
import fi.vm.sade.haku.oppija.common.HttpClientHelper;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonJsonAdapter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
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

    public String addPerson(Person person) {

        log.debug("start addPerson, {}", System.currentTimeMillis() / 1000);

        String realHetuUrl = getClientHelper().getRealUrl("/resources/henkilo/byHetu/" +
                person.getSocialSecurityNumber());

        HttpClient client = new HttpClient();
        log.info("Getting person from " + realHetuUrl);
        GetMethod get = new GetMethod(realHetuUrl);
        try {
            log.debug("execute getByHetu addPerson, {}", System.currentTimeMillis() / 1000);
            client.executeMethod(get);
        } catch (IOException e) {
            log.error("Checking hetu failed due to: " + e.toString());
            return null;
        }

        int status = get.getStatusCode();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Person.class, new PersonJsonAdapter());
        gson = gsonBuilder.create();
        String responseString = null;
        if (status == 404) {
            responseString = createHenkilo(client, person);
        } else if (status == 200) {
            try {
                responseString = get.getResponseBodyAsString();
            } catch (IOException e) {
                // It's because I'm lazy
                throw new RuntimeException(e);
            }
        } else {
            log.error("Checking hetu failed due to: " + get.getStatusCode() + get.getStatusText());
            return null;
        }

        JsonObject henkiloJson = new JsonParser().parse(responseString).getAsJsonObject();

        log.debug("endAddPerson, {}", System.currentTimeMillis() / 1000);
        return henkiloJson.get("oidHenkilo").getAsString();
    }

    @Override
    public List<String> getOrganisaatioHenkilo() {
        CachingRestClient cachingRestClient = getCachingRestClient();
        String personOid = SecurityContextHolder.getContext().getAuthentication().getName();
        List<String> orgs = new ArrayList<String>();
        String response = null;
        try {
            InputStream is = cachingRestClient.get(targetService + "/resources/henkilo/" + personOid + "/organisaatiohenkilo");
            response = IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Getting organisaatiohenkilos failed due to: " + e.toString());
            return orgs;
        }

        try {
            JsonArray orgJson = new JsonParser().parse(response).getAsJsonArray();
            Iterator<JsonElement> elems = orgJson.iterator();
            while (elems.hasNext()) {
                JsonObject orgObj = elems.next().getAsJsonObject();
                orgs.add(orgObj.get("organisaatioOid").getAsString());
            }
        } catch (JsonSyntaxException jse) {
            log.error("JsonSyntaxException for response: {}", response);
        }
        return orgs;
    }

    private synchronized CachingRestClient getCachingRestClient() {
        if (cachingRestClient == null) {
            cachingRestClient = new CachingRestClient();
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



    private String createHenkilo(HttpClient client, Person person) {

        log.debug("start createHenkilo, {}", System.currentTimeMillis() / 1000);
        String henkiloResource = targetService + "/resources/henkilo";

        String responseString = null;
        PostMethod post = new PostMethod(henkiloResource);
        try {
            RequestEntity entity = new StringRequestEntity(gson.toJson(person, Person.class), MediaType.APPLICATION_JSON + ";charset=UTF-8", "UTF-8");
            post.setRequestEntity(entity);
            client.executeMethod(post);
            responseString = post.getResponseBodyAsString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Creating person failed due to: " + e.toString());
        }
        log.debug("createHenkilo responseString: {}", responseString);

        log.debug("end createHenkilo, {}", System.currentTimeMillis() / 1000);
        return responseString;
    }

    private HttpClientHelper getClientHelper() {
        if (this.clientHelper == null) {
            this.clientHelper = new HttpClientHelper(casUrl, targetService, "/resources/henkilo/", clientAppUser, clientAppPass);
        }
        return this.clientHelper;
    }
}
