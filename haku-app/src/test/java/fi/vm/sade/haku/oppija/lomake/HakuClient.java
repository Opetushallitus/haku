package fi.vm.sade.haku.oppija.lomake;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HakuClient {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    private static final Logger LOGGER = LoggerFactory.getLogger(HakuClient.class);
    public final String formUrl;

    private final Map<String, Map<String, String>> applicationData;
    private final Client client;
    private List<NewCookie> cookies = Collections.emptyList();

    public HakuClient(final String baseUrl, final String resource) throws IOException {
        URL url = Resources.getResource(resource);
        final ObjectMapper mapper = new ObjectMapper();
        Map data = mapper.readValue(url, Map.class);
        this.client = ClientBuilder.newClient(new ClientConfig());
        this.client.property(ClientProperties.FOLLOW_REDIRECTS, false);
        this.applicationData = (Map<String, Map<String, String>>) data.get("answers");
        this.applicationData.put("esikatselu", new ImmutableMap.Builder<String, String>()
                .put("nav-send", "true").build());
        this.formUrl = baseUrl + data.get("applicationSystemId");

    }

    private void getPhase(final ClientResponse previousPhaseResponse) throws IOException {
        WebTarget target = client.target(previousPhaseResponse.getLocation());
        Invocation.Builder request = target.request();
        for (NewCookie cookie : cookies) {
            request.cookie(cookie);
        }
        Response response = request.accept(MediaType.TEXT_HTML + CHARSET_UTF_8).get();
        if (response.getStatus() == HttpStatus.SC_OK) {
            response.getEntity();
        } else {
            LOGGER.debug(response.getEntity().toString());
            throw new RuntimeException("get uri failed (" + response.getStatus() + ") " + previousPhaseResponse.getLocation());
        }
    }

    public ClientResponse postPhase(final ClientResponse previousPhaseResponse) throws IOException {
        String phaseId = parsePhaseId(previousPhaseResponse.getLocation().toString());
        if (this.applicationData.containsKey(phaseId)) {
            Form form = mapToForm(this.applicationData.get(phaseId));
            WebTarget target = client.target(previousPhaseResponse.getLocation());
            Invocation.Builder request = target.request();
            for (NewCookie cookie : cookies) {
                request.cookie(cookie);
            }
            getPhase(previousPhaseResponse);
            ClientResponse response = request.accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE + CHARSET_UTF_8, MediaType.TEXT_HTML + CHARSET_UTF_8)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), ClientResponse.class);

            if (response.getStatus() == HttpStatus.SC_SEE_OTHER) {
                response.close();
                return response;
            } else {
                throw new RuntimeException("Phase " + phaseId + " not completed (status=" + response.getStatus() + "). Response:\n"+ response.getEntity().toString());
            }
        } else {
            return null;
        }
    }

    public void apply() throws IOException {
        ClientResponse response = getFirstPhaseLocation();
        fillForm(response);
    }

    private ClientResponse getFirstPhaseLocation() throws IOException {
        WebTarget target = client.target(formUrl);
        ClientResponse response = target.request().accept(MediaType.TEXT_HTML + CHARSET_UTF_8).get(ClientResponse.class);
        if (response.getStatus() == HttpStatus.SC_SEE_OTHER) {
            response.close();
            cookies = new ArrayList<>(response.getCookies().values());
            return response;
        } else {
            throw new RuntimeException(response.getEntity().toString());
        }
    }

    private void fillForm(final ClientResponse response) throws IOException {
        ClientResponse postResponse = postPhase(response);
        if (postResponse != null) {
            fillForm(postResponse);
        }
    }


    private Form mapToForm(final Map<String, String> values) {
        Form form = new Form();
        values.forEach((key, value) ->
                form.param(key, value)
        );
        return form;
    }

    private static String parsePhaseId(final String url) {
        String[] split = url.split("/");
        return split[split.length - 1];
    }

    public static void main(String[] args) throws IOException {

        HakuClient hakuClient = new HakuClient(
                "http://localhost:9090/haku-app/lomake/",
                "application.json");

        try {
            hakuClient.apply();
        } catch (RuntimeException re) {
            LOGGER.debug(re.getMessage());
            List<String> lines = IOUtils.readLines(new StringReader(re.getMessage()));
            for (String line : lines) {
                if (line.contains("warning")) {
                    LOGGER.debug(line);
                }
            }
        }


    }
}
