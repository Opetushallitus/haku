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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.vm.sade.authentication.cas.CasClient;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import fi.vm.sade.oppija.common.authentication.PersonJsonAdapter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${cas.service.authentication-service}")
    private String targetService;
    private String hetuResource = targetService + "/resources/byHetu";
    private String henkiloResource = targetService + "/resources/henkilo";

    @Value("${web.url.cas}")
    private String casUrl;
    @Value("${haku.app.username.to.usermanagement}")
    private String clientAppUser;
    @Value("${haku.app.username.to.usermanagement}")
    private String clientAppPass;

    private Gson gson;

    public String addPerson(Person person) {

        String serviceTicket = CasClient.getTicket(casUrl + "/v1/tickets", clientAppUser, clientAppPass, targetService + "/j_spring_cas_security_check");

        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(hetuResource + "/" + person.getSocialSecurityNumber() + "?ticket=" + serviceTicket);
        try {
            client.executeMethod(get);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int status = get.getStatusCode();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Person.class, new PersonJsonAdapter());
        gson = gsonBuilder.create();
        String responseString = null;
        if (status == 404) {
            responseString = createHenkilo(client, person);
        } else if (status >= 500) {
            throw new RuntimeException("Server made a boo-boo: " + get.getStatusText());
        }

        System.out.println(responseString);
        JsonObject henkiloJson = new JsonParser().parse(responseString).getAsJsonObject();
        String oid = henkiloJson.get("oidHenkilo").getAsString();
        return oid;

    }

    private String createHenkilo(HttpClient client, Person person) {

        String responseString = null;
        PostMethod post = new PostMethod(hetuResource);
        try {
            RequestEntity entity = new StringRequestEntity(gson.toJson(person, Person.class), MediaType.APPLICATION_JSON, "UTF-8");
            post.setRequestEntity(entity);
            client.executeMethod(post);
            responseString = post.getResponseBodyAsString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return responseString;
    }

}
