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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OfficerClient extends BaseClient {
    public static final Logger LOGGER = LoggerFactory.getLogger(OfficerClient.class);

    public OfficerClient(final String baseUrl) {
        this.baseUrl = baseUrl;
        this.officer = new User("officer");
        httpclient = new DefaultHttpClient();
    }

    public void addPersonAndAuthenticate(String oid) {
        try {
            login(officer, httpclient);
            HttpPost httpPost = new HttpPost(baseUrl + "/hakemus/" + oid + "/addPersonAndAuthenticate");
            ExecuteAndConsume(httpclient, httpPost);
        } catch (IOException e) {
            LOGGER.error("Error posting form ", e);
        }
    }

}
