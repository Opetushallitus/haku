package fi.vm.sade.haku.http;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.concurrent.*;

public class RestClient {
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static <T> Future<T> get(final String url, final Class<T> responseClass) throws IOException {
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) {
                request.setParser(new JsonObjectParser(JSON_FACTORY));
            }
        });
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        final HttpResponse res = request.execute();
        FutureTask<T> task = new FutureTask<>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    return res.parseAs(responseClass);
                } catch (IOException e) {
                    throw new ExecutionException(e);
                }
            }
        });
        executor.execute(task);
        return task;
    }
}
