package fi.vm.sade.haku.oppija.common.jackson;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import fi.vm.sade.haku.http.HttpRestClient;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

public class UnknownPropertiesAllowingJacksonJsonClientFactory {
    public static Client create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
        ClientConfig cc = new DefaultClientConfig();
        cc.getSingletons().add(jacksProv);
        Client client = Client.create(cc);
        client.addFilter(new JerseyOphClientRequestFilter());
        return client;
    }

    private static class JerseyOphClientRequestFilter extends ClientFilter {
        @Override
        public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
            // Modify the request
            ClientRequest mcr = modifyRequest(cr);
            // Call the next filter
            ClientResponse resp = getNext().handle(mcr);
            // Modify the response
            return resp;
        }

        private ClientRequest modifyRequest(ClientRequest cr) {
            MultivaluedMap<String, Object> headers = cr.getHeaders();
            headers.add("clientSubSystemCode", "haku.hakemus-api");
            if(!HttpRestClient.ImmutableHttpMethods.contains(cr.getMethod())) {
                headers.add("CSRF", "UnknownPropertiesAllowingJacksonJsonClientFactory");
                headers.add("Cookie", new Cookie("CSRF","UnknownPropertiesAllowingJacksonJsonClientFactory"));
            }
            return cr;
        }
    }
}

