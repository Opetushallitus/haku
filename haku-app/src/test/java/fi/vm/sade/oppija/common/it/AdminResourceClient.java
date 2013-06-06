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

package fi.vm.sade.oppija.common.it;

import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminResourceClient {
    public static final Logger LOGGER = LoggerFactory.getLogger(AdminResourceClient.class);

    private String baseUrl;
    private User admin;
    private DefaultHttpClient httpclient;

    public AdminResourceClient(final String baseUrl) {
        this.baseUrl = baseUrl;
        this.admin = new User("admin");
        this.httpclient = new DefaultHttpClient();
    }

    public void updateModel(FormModel formModel) {
        try {
            login(admin, httpclient);
            String jsonString = new FormModelToJsonString().apply(formModel);
            postModel(jsonString, httpclient);
        } catch (IOException e) {
            LOGGER.error("Error posting form ", e);
        }
    }

    private void login(final User user, final DefaultHttpClient httpclient) throws IOException {
        HttpPost loginPost = new HttpPost(baseUrl + "j_spring_security_check");
        loginPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("j_username", user.getUsername()));
        params.add(new BasicNameValuePair("j_password", user.getPassword()));
        loginPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        ExecuteAndConsume(httpclient, loginPost);
    }

    private void postModel(final String model, final DefaultHttpClient httpclient) throws IOException {
        HttpPost httpPost = new HttpPost(baseUrl + "admin/model");
        httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
        httpPost.setEntity(new StringEntity(model, "UTF-8"));
        ExecuteAndConsume(httpclient, httpPost);
    }

    private void ExecuteAndConsume(DefaultHttpClient httpclient, HttpUriRequest httpUriRequest) throws IOException {
        HttpResponse httpResponse = httpclient.execute(httpUriRequest);
        EntityUtils.consume(httpResponse.getEntity());
    }

    public static class User {
        private final String username;
        private final String password;

        public User(final String username, String password) {
            this.username = username;
            this.password = password;
        }

        public User(final String username) {
            this(username, username);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
