package fi.vm.sade.oppija.lomake;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.client.apache.ApacheHttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HakuClient {

    public final String formUrl;

    private final Map<String, Map<String, String>> applicationData;
    private final Client client;
    private List<NewCookie> cookies = Collections.emptyList();

    public HakuClient(final String baseUrl, final String resource) throws IOException {
        URL url = Resources.getResource(resource);
        final ObjectMapper mapper = new ObjectMapper();
        Map data = mapper.readValue(url, Map.class);
        this.client = ApacheHttpClient.create();
        Map<String, String> formId = (Map<String, String>) data.get("formId");
        this.client.setFollowRedirects(false);
        this.applicationData = (Map<String, Map<String, String>>) data.get("answers");
        this.applicationData.put("esikatselu", new ImmutableMap.Builder<String, String>()
                .put("nav-send", "true").build());
        this.formUrl = baseUrl + formId.get("applicationPeriodId") + '/' + formId.get("formId");

    }

    private void getPhase(final ClientResponse previousPhaseResponse) throws IOException {
        WebResource webResource = client.resource(previousPhaseResponse.getLocation());
        WebResource.Builder builder = webResource.getRequestBuilder();
        for (NewCookie cookie : cookies) {
            builder.cookie(cookie);
        }
        ClientResponse response = builder.accept(MediaType.TEXT_HTML).get(ClientResponse.class);
        if (response.getStatus() == HttpStatus.SC_OK) {
            response.close();
            return;
        } else {
            System.out.println(IOUtils.toString(response.getEntityInputStream(), "UTF-8"));
            throw new RuntimeException("get uri failed (" + response.getStatus() + ") " + previousPhaseResponse.getLocation());
        }
    }

    public ClientResponse postPhase(final ClientResponse previousPhaseResponse) throws IOException {
        String phaseId = parsePhaseId(previousPhaseResponse.getLocation().toString());
        if (this.applicationData.containsKey(phaseId)) {
            Form form = mapToForm(this.applicationData.get(phaseId));
            WebResource webResource = client.resource(previousPhaseResponse.getLocation());
            WebResource.Builder builder = webResource.getRequestBuilder();
            for (NewCookie cookie : cookies) {
                builder.cookie(cookie);
            }
            getPhase(previousPhaseResponse);
            ClientResponse response = builder.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.TEXT_HTML).post(ClientResponse.class, form);

            if (response.getStatus() == HttpStatus.SC_SEE_OTHER) {
                response.close();
                return response;
            } else {
                throw new RuntimeException(IOUtils.toString(response.getEntityInputStream(), "UTF-8"));
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
        WebResource r = client.resource(formUrl);
        ClientResponse response = r.accept(MediaType.TEXT_HTML).get(ClientResponse.class);
        if (response.getStatus() == HttpStatus.SC_SEE_OTHER) {
            response.close();
            cookies = response.getCookies();
            return response;
        } else {
            throw new RuntimeException(IOUtils.toString(response.getEntityInputStream(), "UTF-8"));
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
        for (Map.Entry<String, String> value : values.entrySet()) {
            form.add(value.getKey(), value.getValue());
        }
        return form;
    }

    private static String parsePhaseId(final String url) {
        String[] split = url.split("/");
        return split[split.length - 1];
    }

    public static void main(String[] args) throws IOException {

        HakuClient hakuClient = new HakuClient(
                "http://localhost:8080/haku-app/lomake/",
                "application.json");

        try {
            hakuClient.apply();
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
            List<String> lines = IOUtils.readLines(new StringReader(re.getMessage()));
            for (String line : lines) {
                if (line.contains("warning")) {
                    System.out.println(line);
                }
            }
        }


    }
}
