package fi.vm.sade.haku.http;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.util.concurrent.*;

import java.io.IOException;
import java.util.concurrent.*;

public class RestClient {
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    static final ExecutorService executorThreadPool = Executors.newFixedThreadPool(2);
    static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(executorThreadPool);

    public static <T> Future<T> get(final String url, final Class<T> responseClass) throws IOException {
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) {
                request.setParser(new JsonObjectParser(JSON_FACTORY));
            }
        });

        ListenableFuture<HttpResponse> request = executor.submit(new RestRequest(url, requestFactory));
        AsyncFunction<HttpResponse, T> response = new AsyncFunction<HttpResponse, T>() {
            @Override
            public ListenableFuture<T> apply(HttpResponse response) throws Exception {
                Parse<T> task = new Parse<>(response, responseClass);
                return executor.submit(task);
            }
        };

        return Futures.transform(request, response);
    }

    private static class RestRequest implements Callable<HttpResponse> {
        private final HttpRequest request;

        public RestRequest(String url, HttpRequestFactory requestFactory) throws IOException {
            this.request = requestFactory.buildGetRequest(new GenericUrl(url));
        }

        @Override
        public HttpResponse call() throws Exception {
            try {
                return request.execute();
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
        }
    }

    private static class Parse<T> implements Callable<T> {
        private final HttpResponse response;
        private final Class<T> responseClass;

        public Parse(HttpResponse response, Class<T> responseClass) {
            this.response = response;
            this.responseClass = responseClass;
        }

        @Override
        public T call() throws Exception {
            return response.parseAs(responseClass);
        }
    }
}
