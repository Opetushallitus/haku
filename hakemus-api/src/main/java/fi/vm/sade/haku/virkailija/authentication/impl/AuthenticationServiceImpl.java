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
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonJsonAdapter;
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
public class AuthenticationServiceImpl implements AuthenticationService {

    final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final String targetService;
    private final CachingRestClient cachingRestClient;
    private final Gson gson;
    private final String userOidPrefix;
    private final String langCookieName;

    @Autowired
    public AuthenticationServiceImpl(
    @Value("${web.url.cas}") String casUrl,
    @Value("${cas.service.authentication-service}") String targetService,
    @Value("${haku.app.username.to.usermanagement}") String clientAppUser,
    @Value("${haku.app.password.to.usermanagement}") String clientAppPass,
    @Value("${user.oid.prefix}") String userOidPrefix,
    @Value("${haku.langCookie}") String langCookieName) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Person.class, new PersonJsonAdapter());
        gson = gsonBuilder.create();
        cachingRestClient = new CachingRestClient();
        cachingRestClient.setWebCasUrl(casUrl);
        cachingRestClient.setCasService(targetService);
        cachingRestClient.setUsername(clientAppUser);
        cachingRestClient.setPassword(clientAppPass);
        cachingRestClient.setCallerId("callerid.haku.haku-app.backend");
        this.targetService = targetService;
        this.userOidPrefix = userOidPrefix;
        this.langCookieName = langCookieName;
    }

    /**
     * Returns a Person object which might be a new one corresponding to user inputs or existing user
     * with patched values from user input. Existings Person objects are queried first by personOid,
     * hetu, and email. If no existing users are found, a new Person object is created.
     *
     * @param person Person object built from the user input
     * @return New or existing and patched Person object
     */
    public Person addPerson(Person person) {
        String hetu = person.getSocialSecurityNumber();
        String personOid = person.getPersonOid();
        String email = person.getEmail();
        Optional<Person> newPerson = Optional.absent();

        if (isNotBlank(personOid)) {
            newPerson = Optional.fromNullable(getHenkilo(personOid));
        }

        if (!newPerson.isPresent() && isNotBlank(hetu)) {
            newPerson = Optional.fromNullable(fetchPerson(hetu));
        }

        if (!newPerson.isPresent() && isNotBlank(email)) {
            newPerson = Optional.fromNullable(fetchPersonByStudentToken(email));
        }

        if (!newPerson.isPresent()) {
            newPerson = Optional.fromNullable(createPerson(person));
        }

        try {
            return person.mergeWith(newPerson.get());
        } catch (IllegalStateException e) {
            throw new RemoteServiceException("Could not create new person from " + person, e);
        }
    }

    @Override
    public List<String> getOrganisaatioHenkilo() {
        String personOid = SecurityContextHolder.getContext().getAuthentication().getName();
        String url = "/resources/henkilo/" + personOid + "/organisaatiohenkilo";
        try {
            List<String> orgs = new ArrayList<>();
            if (log.isDebugEnabled()) {
                log.debug("Getting organisaatiohenkilos for {}", personOid);
                log.debug("Using cachingRestClient webCasUrl: {}, casService: {} ", cachingRestClient.getWebCasUrl(), cachingRestClient.getCasService());
            }
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
            throw new RemoteServiceException(targetService + url, e);
        }
    }

    @Override
    public Person getCurrentHenkilo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        String personOid = auth.getName();

        if (personOid == null || !personOid.startsWith(userOidPrefix)) {
            return null;
        }
        return getHenkilo(personOid);
    }

    @Override
    public Person getHenkilo(String personOid) {
        String url = "/resources/s2s/" + personOid;
        try {
            String personJson = cachingRestClient.getAsString(url);
            log.debug("Got person: {}", personJson);
            Person person = gson.fromJson(personJson, Person.class);
            log.debug("Deserialized person: {}", person);
            return person;
        } catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
        }
    }
    
    @Override
    public List<Person> getHenkiloList(List<String> personOids) {
        String oidsJson = gson.toJson(personOids);
        String url = "/resources/henkilo/henkilotByHenkiloOidList";
        try {
            HttpResponse response = cachingRestClient.post(url, MediaType.APPLICATION_JSON, oidsJson);
            Type listType = new TypeToken<List<Person>>(){}.getType();
            BasicResponseHandler handler = new BasicResponseHandler();
            String responseJson = handler.handleResponse(response);
            return gson.fromJson(responseJson, listType);
        } catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
        }
    }

    @Override
    public Person getStudentOid(String personOid) {
        String url = "/resources/henkilo/" + personOid + "/yksiloi";

        String responseString = null;
        try {
            HttpResponse response = cachingRestClient.put(url, MediaType.APPLICATION_JSON, null);
            BasicResponseHandler handler = new BasicResponseHandler();
            responseString = handler.handleResponse(response);
        } catch (CachingRestClient.HttpException hte) {
            // Nothing to do
        } catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
        }
        log.debug("Person found: {}", responseString);
        return gson.fromJson(responseString, Person.class);
    }

    @Override
    public Person checkStudentOid(String personOid) {

        String url = "/resources/s2s/" + personOid;
        try {
            String responseString = cachingRestClient.getAsString(url);
            log.debug("Person found: {}", responseString);
            return gson.fromJson(responseString, Person.class);
        } catch (CachingRestClient.HttpException hte) {
            // Nothing to do
        } catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
        }
        return null;
    }

    private Person createPerson(Person person) {
        String personJson = gson.toJson(person, Person.class);
        String url = "/resources/henkilo";
        try {
            log.debug("Creating person: {}", personJson);
            HttpResponse response = cachingRestClient.post(url, MediaType.APPLICATION_JSON, personJson);
            BasicResponseHandler handler = new BasicResponseHandler();
            String oid = handler.handleResponse(response);
            return getHenkilo(oid);
        } catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
        }
    }

    private String fetchResourceByUrl(String url) {
        String response;
        try {
            response = cachingRestClient.getAsString(url);
        } catch (CachingRestClient.HttpException hte) {
            if (hte.getStatusCode() == 404) {
                return null;
            } else {
                throw new RemoteServiceException(targetService + url, hte);
            }
        } catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
        }
        return response;
    }

    private Person fetchPerson(String hetu) {
        String url = "/resources/s2s/byHetu/" + hetu;
        String resource = fetchResourceByUrl(url);
        if (resource == null) {
            return null;
        }
        return gson.fromJson(resource, Person.class);
    }

    private Person fetchPersonByStudentToken(String token) {
        String url = "/resources/henkilo/identification?idp=oppijaToken&id=" + token;
        String resource = fetchResourceByUrl(url);
        if (resource == null) {
            return null;
        }
        return gson.fromJson(resource, Person.class);
    }

    public String getLangCookieName() {
        return langCookieName;
    }
}
