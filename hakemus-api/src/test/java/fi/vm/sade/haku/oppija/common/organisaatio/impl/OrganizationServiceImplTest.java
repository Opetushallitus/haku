package fi.vm.sade.haku.oppija.common.organisaatio.impl;

import com.google.gson.Gson;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrganizationServiceImplTest {


    public static final String OID = "1";

    @Test
    public void testSearch() throws IOException {
        Gson gson = new Gson();
        OrganisaatioHakutulos hakutulos = new OrganisaatioHakutulos();
        OrganisaatioPerustieto pt = new OrganisaatioPerustieto();
        pt.setOid("1");
        hakutulos.setNumHits(1);
        hakutulos.setOrganisaatiot(Collections.singletonList(pt));
        HttpClient client = mockClient(new ByteArrayInputStream(gson.toJson(hakutulos)
                        .getBytes(StandardCharsets.UTF_8)));
        OrganizationServiceImpl service = new OrganizationServiceImpl();
        service.setHttpClient(client);

        List<Organization> search = service.search(new OrganisaatioSearchCriteria());
        assertEquals("Wrong oid ", OID, search.get(0).getOid());
    }

    @Test
    public void testGetParentOids() throws IOException {
        HttpClient httpClient = mockClient(new ByteArrayInputStream(
                "1.2.246.562.10.00000000001/1.2.246.562.10.00000000001/1.2.246.562.10.00000000001"
                        .getBytes(StandardCharsets.UTF_8)));
        OrganizationServiceImpl service = new OrganizationServiceImpl();
        service.setHttpClient(httpClient);
        List<String> parentOids = service.findParentOids("1.2.246.562.10.00000000001");
        assertEquals(3, parentOids.size());
        for (String oid : parentOids) {
            assertEquals("1.2.246.562.10.00000000001", oid);
        }
    }

    @Test
    public void testGetParentOidsFail() throws IOException {
        HttpClient httpClient = mockClient(new ByteArrayInputStream(
                "This is absolute / and total garbage"
                        .getBytes(StandardCharsets.UTF_8)));
        OrganizationServiceImpl service = new OrganizationServiceImpl();
        service.setHttpClient(httpClient);
        boolean thrown = false;
        try {
            List<String> parentOids = service.findParentOids("1.2.246.562.10.00000000001");
        } catch (ResourceNotFoundException e) {
            assertEquals("Getting organization parentoids for 1.2.246.562.10.00000000001 failed. 'This is absolute ' doesn't look like oid",
                    e.getMessage());
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testGetParentOidsFail2() throws IOException {
        HttpClient httpClient = mockClient(new ByteArrayInputStream(
                "Absolute/Garbage".getBytes(StandardCharsets.UTF_8)));
        OrganizationServiceImpl service = new OrganizationServiceImpl();
        service.setHttpClient(httpClient);
        boolean thrown = false;
        try {
            List<String> parentOids = service.findParentOids("1.2.246.562.10.00000000001");
        } catch (StringIndexOutOfBoundsException e) {
            assertEquals("String index out of range: 14",
                    e.getMessage());
            thrown = true;
        }
        assertTrue(thrown);
    }

    private HttpClient mockClient(InputStream content) throws IOException {
        HttpClient httpClient =mock(HttpClient.class);
        HttpResponse response = mock(HttpResponse.class);
        StatusLine status = mock(StatusLine.class);
        HttpEntity entity = mock(HttpEntity.class);
        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(status);
        when(response.getEntity()).thenReturn(entity);
        when(status.getStatusCode()).thenReturn(200);
        when(entity.getContent()).thenReturn(content);
        return httpClient;
    }

}
