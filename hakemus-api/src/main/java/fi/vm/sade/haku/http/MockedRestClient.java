package fi.vm.sade.haku.http;

import com.google.api.client.util.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.HttpRestClient.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockedRestClient implements RestClient {

    private final Map<String, Object> mappings;

    public static class Captured {
        public final String method;
        public final String url;
        public final Object body;

        public Captured(String method, String url, Object body) {
            this.method = method;
            this.url = url;
            this.body = body;
        }
    }

    private List<Captured> capturedEvents = Lists.newArrayList();

    public void clearCaptured() {
        capturedEvents.clear();
    }

    public List<Captured> getCaptured() {
        return capturedEvents;
    }

    public MockedRestClient() {
        this.mappings = new HashMap<>();
    }

    public MockedRestClient(final Map<String, Object> mappings) {
        this.mappings = mappings;
    }

    @Override
    public <T> ListenableFuture<Response<T>> get(String url, Class<T> responseClass) throws IOException {
        Object o = mappings.get(url);
        if(o == null) {
            throw new RuntimeException("Url not mocked: " + url);
        }
        capturedEvents.add(new Captured("GET", url, null));
        return (ListenableFuture<Response<T>>) o;
    }

    @Override
    public <T, B> ListenableFuture<Response<T>> post(String url, B body, Class<T> responseClass) throws IOException {
        capturedEvents.add(new Captured("POST", url, body));
        Response<T> r = mock(Response.class);
        when(r.isSuccessStatusCode()).thenReturn(true);
        return Futures.immediateFuture(r);
    }

}
