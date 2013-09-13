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

package fi.vm.sade.oppija.common.authentication.impl;

import com.google.gson.*;
import fi.vm.sade.authentication.cas.CasClient;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import fi.vm.sade.oppija.common.authentication.PersonJsonAdapter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
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

    @Value("${cas.service.authentication-service}")
    private String targetService;

    @Value("${web.url.cas}")
    private String casUrl;
    @Value("${haku.app.username.to.usermanagement}")
    private String clientAppUser;
    @Value("${haku.app.password.to.usermanagement}")
    private String clientAppPass;

    private Gson gson;

    public String addPerson(Person person) {

        String hetuResource = targetService + "/resources/henkilo/byHetu";
        String serviceTicket = getServiceticket();

        HttpClient client = new HttpClient();
        String realHetuUrl = hetuResource + "/" + person.getSocialSecurityNumber() + "?ticket=" + serviceTicket;
        log.info("Getting person from " + realHetuUrl);
        GetMethod get = new GetMethod(realHetuUrl);
        try {
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
        } else if (status >= 500) {
            log.error("Checking hetu failed due to: " + get.getStatusCode() + get.getStatusText());
            return null;
        } else if (status == 200) {
            try {
                responseString = get.getResponseBodyAsString();
            } catch (IOException e) {
                // It's because I'm lazy
                throw new RuntimeException(e);
            }
        }

        JsonObject henkiloJson = new JsonParser().parse(responseString).getAsJsonObject();
        String oid = henkiloJson.get("oidHenkilo").getAsString();
        return oid;

    }

    @Override
    public List<String> getOrganisaatioHenkilo() {

        List<String> orgs = new ArrayList<String>();
        String personOid = SecurityContextHolder.getContext().getAuthentication().getName();
        String resource = targetService + "/resources/henkilo/" + personOid + "/organisaatiohenkilo";
        String serviceTicket = getServiceticket();
        String url = resource + "?ticket=" + serviceTicket;
        HttpClient client = new HttpClient();
        log.info("Getting organisaatiohenkilos for " + personOid);
        GetMethod get = new GetMethod(url);
        try {
            client.executeMethod(get);
        } catch (IOException e) {
            log.error("Getting organisaatiohenkilos failed due to: " + e.toString());
            return orgs;
        }

        int status = get.getStatusCode();
        if (status == 200) {
            String responseString = null;
            try {
                responseString = get.getResponseBodyAsString();
                JsonArray orgJson = new JsonParser().parse(responseString).getAsJsonArray();
                Iterator<JsonElement> elems = orgJson.iterator();
                while (elems.hasNext()) {
                    JsonObject orgObj = elems.next().getAsJsonObject();
                    orgs.add(orgObj.get("organisaatioOid").getAsString());
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return orgs;
    }

    @Override
    public String getStudentOid(String personOid) {
        String resource = targetService + "/resources/henkilo/" + personOid + "/yksiloi";
        String serviceTicket = getServiceticket();
        String url = resource + "?ticket=" + serviceTicket;
        HttpClient client = new HttpClient();
        PutMethod put = new PutMethod(url);
        try {
            client.executeMethod(put);
        } catch(IOException e) {
            log.error("Getting studentOid for {} failed due to: {}", personOid, e.toString());
            return null;
        }

        return null;
    }

    private String getServiceticket() {
        String realCasUrl = casUrl + "/v1/tickets";
        log.info("Getting CAS ticket from " + realCasUrl + " for " + targetService);
        return CasClient.getTicket(realCasUrl, clientAppUser, clientAppPass, targetService + "/j_spring_cas_security_check");
    }

    private String createHenkilo(HttpClient client, Person person) {

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
        return responseString;
    }


}
