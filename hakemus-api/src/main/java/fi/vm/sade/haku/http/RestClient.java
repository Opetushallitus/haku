package fi.vm.sade.haku.http;

import com.google.common.util.concurrent.*;
import fi.vm.sade.haku.http.HttpRestClient.Response;

import java.io.IOException;

public interface RestClient {

    <T> ListenableFuture<Response<T>> get(final String url, final Class<T> responseClass) throws IOException;
    <T, B> ListenableFuture<Response<T>> post(final String url, final B body, final Class<T> responseClass) throws IOException;
}
