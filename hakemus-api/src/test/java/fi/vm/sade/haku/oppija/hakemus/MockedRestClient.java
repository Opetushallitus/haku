package fi.vm.sade.haku.oppija.hakemus;

import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.RestClient;

import java.io.IOException;
import java.util.Map;

public class MockedRestClient implements RestClient {

    private final Map<String, Object> mappings;

    public MockedRestClient(final Map<String, Object> mappings) {
        this.mappings = mappings;
    }

    @Override
    public <T> ListenableFuture<T> get(String url, Class<T> responseClass) throws IOException {
        return (ListenableFuture<T>) mappings.get(url);
    }

}
