package fi.vm.sade.haku.oppija.common.jackson;


import fi.vm.sade.haku.http.HttpRestClient;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

public class UnknownPropertiesAllowingJacksonJsonClientFactory {
    public static Client create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
        ClientConfig cc = new ClientConfig(jacksProv);
        cc.register(JerseyOphClientRequestFilter.class);
        return JerseyClientBuilder.createClient(cc);
    }

    private static class JerseyOphClientRequestFilter implements ClientRequestFilter {
        private void modifyRequest(ClientRequestContext clientRequestContext) {
            MultivaluedMap<String, Object> headers = clientRequestContext.getHeaders();
            headers.add("clientSubSystemCode", "haku.hakemus-api");
            if (!HttpRestClient.ImmutableHttpMethods.contains(clientRequestContext.getMethod())) {
                headers.add("CSRF", "UnknownPropertiesAllowingJacksonJsonClientFactory");
                headers.add("Cookie", new Cookie("CSRF", "UnknownPropertiesAllowingJacksonJsonClientFactory"));
            }
        }

        @Override
        public void filter(ClientRequestContext clientRequestContext) {
            modifyRequest(clientRequestContext);
        }
    }
}

