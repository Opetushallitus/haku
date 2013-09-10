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

package fi.vm.sade.oppija.common.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.esapi.ESAPI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.ws.http.HTTPException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public class XSSFilter implements ContainerRequestFilter {


    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        clean(containerRequest.getQueryParameters());
        clean(containerRequest.getRequestHeaders());
        clean(containerRequest.getCookieNameValueMap());
        clean(containerRequest.getFormParameters());
        return containerRequest;
    }

    private void clean(MultivaluedMap<String, String> parameters) {
        for(Map.Entry<String, List<String>> params : parameters.entrySet()) {
            String key = params.getKey();
            List<String> values = params.getValue();
            List<String> cleanValues = new ArrayList<String>();
            for(String value : values)
            {
                cleanValues.add(stripXSS(value));
            }
            parameters.put(key, cleanValues);
        }
    }

    public static String stripXSS(String value) throws HTTPException {
        if(value != null) {
            // Use the ESAPI library to avoid encoded attacks.
            value = ESAPI.encoder().canonicalize(value);

            // Avoid null characters
            value = value.replaceAll("\0", "");

            if (!Jsoup.isValid(value, Whitelist.none())) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Illegal request (XSS)").type(MediaType.APPLICATION_JSON).build());
            }
        }
        return value;
    }
}
