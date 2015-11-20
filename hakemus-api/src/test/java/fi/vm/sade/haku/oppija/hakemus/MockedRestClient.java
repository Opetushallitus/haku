package fi.vm.sade.haku.oppija.hakemus;

import com.google.api.client.http.HttpResponse;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.HttpRestClient;
import fi.vm.sade.haku.http.HttpRestClient.Response;
import fi.vm.sade.haku.http.RestClient;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockedRestClient implements RestClient {

    private final Map<String, Object> mappings;

    public MockedRestClient(final Map<String, Object> mappings) {
        this.mappings = mappings;
    }

    @Override
    public <T> ListenableFuture<Response<T>> get(String url, Class<T> responseClass) throws IOException {
        return (ListenableFuture<Response<T>>) mappings.get(url);
    }

    @Override
    public <T, B> ListenableFuture<Response<T>> post(String url, B body, Class<T> responseClass) throws IOException {
        HttpResponse r = mock(HttpResponse.class);
        when(r.getStatusCode()).thenReturn(200);
        return Futures.immediateFuture(new HttpRestClient.Response<T>(r, null));
    }

}
