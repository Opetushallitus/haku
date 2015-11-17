package fi.vm.sade.haku.http;

import com.google.common.util.concurrent.*;

import java.io.IOException;

public interface RestClient {

    <T> ListenableFuture<T> get(final String url, final Class<T> responseClass) throws IOException;

}
