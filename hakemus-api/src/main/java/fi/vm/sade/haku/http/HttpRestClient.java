package fi.vm.sade.haku.http;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.util.concurrent.*;
import fi.vm.sade.haku.oppija.configuration.HakemusApiCallerId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Component("restClient")
@Profile({"default", "devluokka"})
public class HttpRestClient implements RestClient {

    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();
    static final ExecutorService executorThreadPool = new ThreadPoolExecutor(1, 100, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(executorThreadPool);
    static final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest request) {
            request.setParser(new JsonObjectParser(JSON_FACTORY));
        }
    });

    private static String callerId = HakemusApiCallerId.callerId;

    @Override
    public <T> ListenableFuture<Response<T>> get(final String url, final Class<T> responseClass) throws IOException {
        return submitRequest(new GetRequest(url, requestFactory), responseClass);
    }

    @Override
    public <T, B> ListenableFuture<Response<T>> post(final String url, final B body, final Class<T> responseClass) throws IOException {
        return submitRequest(new PostRequest(url, new JsonHttpContent(JSON_FACTORY, body), requestFactory), responseClass);
    }

    private static <T> ListenableFuture<Response<T>> submitRequest(Callable<HttpResponse> request, final Class<T> responseClass) {
        return Futures.transform(executor.submit(request), new AsyncFunction<HttpResponse, Response<T>>() {
            @Override
            public ListenableFuture<Response<T>> apply(HttpResponse response) throws Exception {
                return executor.submit(new Parse<>(response, responseClass));
            }
        });
    }

    public static List ImmutableHttpMethods = Arrays.asList(HttpMethods.GET, HttpMethods.HEAD, HttpMethods.OPTIONS);
    private static class RestRequest implements Callable<HttpResponse> {
        private final HttpRequest request;

        protected RestRequest(HttpRequest request) {
            this.request = request;
        }

        @Override
        public HttpResponse call() throws Exception {
            request.getHeaders().set("Caller-Id", callerId);
            if(!ImmutableHttpMethods.contains(request.getRequestMethod())) {
                request.getHeaders().set("CSRF", "HttpRestClient");
                String cookie = request.getHeaders().getCookie();
                String csrf = "CSRF=HttpRestClient";
                if(cookie == null) {
                    cookie = csrf;
                } else {
                    cookie = cookie + "; " + csrf;
                }
                request.getHeaders().setCookie(cookie);
            }
            try {
                return request.execute();
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
        }
    }

    private final static class GetRequest extends RestRequest {
        public GetRequest(String url, HttpRequestFactory requestFactory) throws IOException {
            super(requestFactory.buildGetRequest(new GenericUrl(url)));
        }
    }

    private final static class PostRequest extends RestRequest {
        public PostRequest(String url, HttpContent content, HttpRequestFactory requestFactory) throws IOException {
            super(requestFactory.buildPostRequest(new GenericUrl(url), content));
        }
    }

    public static class Response<T> {
        // Do not expose HttpResponse directly in order to allow mocking
        // Response objects in tests. Duplicate getters to Response class if
        // access to HttpResponse class is needed.
        private final HttpResponse response;

        private final T result;

        public T getResult() {
            return result;
        }

        public boolean isSuccessStatusCode() {
            return response.isSuccessStatusCode();
        }

        public Response(HttpResponse response, T result) {
            this.response = response;
            this.result = result;
        }
    }

    private static class Parse<T> implements Callable<Response<T>> {
        private final HttpResponse response;
        private final Class<T> responseClass;

        public Parse(HttpResponse response, Class<T> responseClass) {
            this.response = response;
            this.responseClass = responseClass;
        }

        @Override
        public Response<T> call() throws Exception {
            return new Response<>(response, response.parseAs(responseClass));
        }
    }
}
